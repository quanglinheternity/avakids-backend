package com.example.avakids_backend.service.PaymentVnPay;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.avakids_backend.DTO.Payment.CreateVnPayPaymentResponse;
import com.example.avakids_backend.DTO.Payment.PaymentResponse;
import com.example.avakids_backend.config.PaymentVnPayConfig;
import com.example.avakids_backend.entity.*;
import com.example.avakids_backend.enums.OrderStatus;
import com.example.avakids_backend.enums.PaymentStatus;
import com.example.avakids_backend.repository.Order.OrderRepository;
import com.example.avakids_backend.repository.Payment.PaymentRepository;
import com.example.avakids_backend.service.Inventory.InventoryService;
import com.example.avakids_backend.util.codeGenerator.CodeGenerator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentVnPayServiceImpl implements PaymentVnPayService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;

    private static final String VNP_TXN_REF_NAME = "TRX";

    @Override
    public CreateVnPayPaymentResponse createVnPayPayment(Payment payment, Order order) {
        long amount = payment.getAmount().multiply(new BigDecimal("100")).longValue();

        String currentVnpTxnRef = CodeGenerator.generateCode(VNP_TXN_REF_NAME);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", PaymentVnPayConfig.vnp_Version);
        vnp_Params.put("vnp_Command", PaymentVnPayConfig.vnp_Command);
        vnp_Params.put("vnp_TmnCode", PaymentVnPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", currentVnpTxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + order.getOrderNumber());
        vnp_Params.put("vnp_OrderType", "other");

        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", PaymentVnPayConfig.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        String queryUrl = buildQueryUrl(vnp_Params);
        String paymentUrl = PaymentVnPayConfig.vnp_PayUrl + "?" + queryUrl;
        return new CreateVnPayPaymentResponse(paymentUrl, currentVnpTxnRef);
    }

    private String buildQueryUrl(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = params.get(fieldName);

            if (fieldValue != null && !fieldValue.isEmpty()) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (i < fieldNames.size() - 1) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String vnp_SecureHash = PaymentVnPayConfig.hmacSHA512(PaymentVnPayConfig.secretKey, hashData.toString());
        query.append("&vnp_SecureHash=").append(vnp_SecureHash);

        return query.toString();
    }

    @Override
    public Map<String, String> getVnPayParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();

        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);

            if (paramValue != null && !paramValue.isEmpty()) {
                try {
                    params.put(paramName, URLDecoder.decode(paramValue, StandardCharsets.UTF_8.toString()));
                } catch (UnsupportedEncodingException e) {
                    log.error("Error decoding parameter: {}", paramName, e);
                    params.put(paramName, paramValue);
                }
            }
        }

        return params;
    }

    @Transactional
    @Override
    public PaymentResponse processVnPayReturn(Map<String, String> vnpParams) {

        // 1. Lấy các thông tin từ VNPay
        String vnpTxnRef = vnpParams.get("vnp_TxnRef");
        String vnpResponseCode = vnpParams.get("vnp_ResponseCode");
        String vnpTransactionNo = vnpParams.get("vnp_TransactionNo");
        String vnpSecureHash = vnpParams.get("vnp_SecureHash");
        String vnpAmount = vnpParams.get("vnp_Amount");
        String vnpBankCode = vnpParams.get("vnp_BankCode");
        String vnpPayDate = vnpParams.get("vnp_PayDate");

        // 2. Verify secure hash
        String calculatedHash = verifyVnPaySecureHash(vnpParams);

        if (!calculatedHash.equals(vnpSecureHash)) {
            log.error("Invalid secure hash. Expected: {}, Got: {}", calculatedHash, vnpSecureHash);
            return PaymentResponse.builder()
                    .success(false)
                    .message("Invalid signature")
                    .build();
        }

        // 3. Tìm payment theo vnpTxnRef
        Payment payment = paymentRepository
                .findByTransactionId(vnpTxnRef)
                .orElseThrow(() -> new RuntimeException("Payment not found with txnRef: " + vnpTxnRef));

        Order order = payment.getOrder();

        // 4. Xử lý theo response code
        PaymentResponse.PaymentResponseBuilder responseBuilder = PaymentResponse.builder();

        if (order.getStatus() == OrderStatus.CONFIRMED || order.getStatus() == OrderStatus.CANCELLED) {

            return PaymentResponse.builder()
                    .success(false)
                    .message("Đơn hàng đã được xử lý trước đó.")
                    .build();
        }

        if ("00".equals(vnpResponseCode)) {

            payment.setStatus(PaymentStatus.PAID);
            payment.setTransactionId(vnpTransactionNo);

            order.setStatus(OrderStatus.CONFIRMED);
            order.setUpdatedAt(LocalDateTime.now());

            paymentRepository.save(payment);
            orderRepository.save(order);

            log.info("Payment completed successfully for order: {}", order.getOrderNumber());

            responseBuilder
                    .success(true)
                    .message("Giao dịch thành công.")
                    .transactionId(vnpTransactionNo)
                    .amount(new BigDecimal(vnpAmount).divide(BigDecimal.valueOf(100)))
                    .bankCode(vnpBankCode)
                    .paymentDate(vnpPayDate);

        } else {

            payment.setStatus(PaymentStatus.FAILED);

            updateProductQuantities(order);

            order.setStatus(OrderStatus.CANCELLED);
            order.setUpdatedAt(LocalDateTime.now());

            paymentRepository.save(payment);
            orderRepository.save(order);

            log.warn("Payment failed for order: {}. Response code: {}", order.getOrderNumber(), vnpResponseCode);

            responseBuilder
                    .success(false)
                    .message(getVnPayResponseMessage(vnpResponseCode))
                    .transactionId(vnpTxnRef);
        }

        return responseBuilder.build();
    }

    private void updateProductQuantities(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            ProductVariant variant = item.getVariant();
            if (variant != null) {
                inventoryService.increaseStock(
                        variant, item.getQuantity(), "Restore stock from cancelled order #" + order.getId(), order);
            }
        }
    }

    private String getVnPayResponseMessage(String responseCode) {
        Map<String, String> messages = new HashMap<>();
        messages.put("00", "Giao dịch thành công");
        messages.put("07", "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).");
        messages.put(
                "09",
                "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.");
        messages.put(
                "10",
                "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần");
        messages.put(
                "11",
                "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.");
        messages.put("12", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.");
        messages.put("13", "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP).");
        messages.put("24", "Giao dịch không thành công do: Khách hàng hủy giao dịch");
        messages.put(
                "51", "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.");
        messages.put(
                "65",
                "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.");
        messages.put("75", "Ngân hàng thanh toán đang bảo trì.");
        messages.put("79", "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định.");
        messages.put("99", "Các lỗi khác");

        return messages.getOrDefault(responseCode, "Lỗi không xác định");
    }

    private String verifyVnPaySecureHash(Map<String, String> vnpParams) {
        // Loại bỏ vnp_SecureHash và vnp_SecureHashType
        Map<String, String> paramsToHash = new TreeMap<>();

        for (Map.Entry<String, String> entry : vnpParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (value != null
                    && !value.isEmpty()
                    && !key.equals("vnp_SecureHash")
                    && !key.equals("vnp_SecureHashType")) {
                paramsToHash.put(key, value);
            }
        }

        // Tạo hash data string với params đã được sort
        StringBuilder hashData = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, String> entry : paramsToHash.entrySet()) {
            if (!first) {
                hashData.append('&');
            }
            hashData.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            hashData.append('=');
            hashData.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
            first = false;
        }

        String hashDataStr = hashData.toString();
        log.info("Hash data string: {}", hashDataStr);

        return PaymentVnPayConfig.hmacSHA512(PaymentVnPayConfig.secretKey, hashDataStr);
    }
}
