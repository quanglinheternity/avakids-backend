package com.example.avakids_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // General Errors (1000-1099)
    UNCATEGORIZED_ERROR(1000, "Lỗi hệ thống, vui lòng thử lại sau", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(1001, "Yêu cầu không hợp lệ", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1002, "Bạn không có quyền truy cập", HttpStatus.UNAUTHORIZED),
    ACCESS_DENIED(1003, "Quyền truy cập bị từ chối", HttpStatus.FORBIDDEN),
    INVALID_ENUM_VALUE(1004, "Giá trị không hợp lệ", HttpStatus.BAD_REQUEST),
    INTERNAL_SERVER_ERROR(1005, "Lỗi hệ thống", HttpStatus.INTERNAL_SERVER_ERROR),

    // Authentication Errors (2000-2099)
    AUTHENTICATION_REQUIRED(2001, "Vui lòng đăng nhập để tiếp tục", HttpStatus.UNAUTHORIZED),
    AUTHENTICATION_FAILED(2002, "Sai tài khoản hoặc mật khẩu", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(2003, "Phiên đăng nhập đã hết hạn", HttpStatus.UNAUTHORIZED),
    INVALID_USERNAME(2004, "Tài khoản không hợp lệ", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(2005, "Mật khẩu không hợp lệ", HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(2006, "Mật khẩu không được để trống", HttpStatus.BAD_REQUEST),

    // User-related Errors (3000-3099)
    // ===== USER – VALIDATION =====
    EMAIL_REQUIRED(3000, "Email không được để trống", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID(3001, "Email không đúng định dạng", HttpStatus.BAD_REQUEST),

    PHONE_REQUIRED(3002, "Số điện thoại không được để trống", HttpStatus.BAD_REQUEST),
    PHONE_INVALID(3003, "Số điện thoại không hợp lệ", HttpStatus.BAD_REQUEST),

    PASSWORD_TOO_SHORT(3004, "Mật khẩu tối thiểu 6 ký tự", HttpStatus.BAD_REQUEST),

    FULLNAME_REQUIRED(3005, "Họ tên không được để trống", HttpStatus.BAD_REQUEST),
    // ===== USER – BUSINESS =====
    EMAIL_ALREADY_EXISTS(3100, "Email đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
    PHONE_ALREADY_EXISTS(3101, "Số điện thoại đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(3102, "Không tìm thấy người dùng.", HttpStatus.BAD_REQUEST),
    // File-related Errors (9200-9299)
    FILE_EMPTY(9201, "File không được để trống", HttpStatus.BAD_REQUEST),
    FILE_TOO_LARGE(9202, "File quá lớn. Kích thước tối đa: 5MB", HttpStatus.BAD_REQUEST),
    INVALID_FILE_TYPE(
            9203,
            "Loại file không được hỗ trợ. Chỉ chấp nhận: jpg, jpeg, png, pdf, doc, docx, xls, xlsx",
            HttpStatus.BAD_REQUEST),
    INVALID_FILE_NAME(9204, "Tên file không hợp lệ", HttpStatus.BAD_REQUEST),
    FILE_UPLOAD_FAILED(9205, "Upload file thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_NOT_FOUND(9206, "Không tìm thấy file", HttpStatus.NOT_FOUND),
    FILE_DELETE_FAILED(9207, "Xóa file thất bại", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_NOT_READABLE(9208, "File không thể đọc được", HttpStatus.BAD_REQUEST),
    FILE_NOT_IMAGE(9209, "Ảnh không hợp lệ. Chỉ chấp nhận jpg, jpeg, png.", HttpStatus.BAD_REQUEST),
    ADDRESS_NOT_FOUND(9210, "Không tìm thấy địa chỉ.", HttpStatus.BAD_REQUEST),
    // ===== USER ADDRESS =====
    RECIPIENT_NAME_REQUIRED(9308, "Tên người nhận không được để trống", HttpStatus.BAD_REQUEST),
    RECIPIENT_NAME_TOO_LONG(9309, "Tên người nhận tối đa 100 ký tự", HttpStatus.BAD_REQUEST),

    ADDRESS_REQUIRED(9302, "Địa chỉ không được để trống", HttpStatus.BAD_REQUEST),
    ADDRESS_TOO_LONG(9303, "Địa chỉ tối đa 255 ký tự", HttpStatus.BAD_REQUEST),

    DISTRICT_REQUIRED(9304, "Quận / Huyện không được để trống", HttpStatus.BAD_REQUEST),
    CITY_REQUIRED(9305, "Thành phố không được để trống", HttpStatus.BAD_REQUEST),
    PROVINCE_REQUIRED(9306, "Tỉnh / Thành không được để trống", HttpStatus.BAD_REQUEST),

    CATEGORY_NAME_REQUIRED(9300, "Tên danh mục không được để trống", HttpStatus.BAD_REQUEST),
    CATEGORY_TOO_LONG(9300, "Tên danh mục quá dài, tối đa 255 ký tự", HttpStatus.BAD_REQUEST),
    CATEGORY_SLUG_REQUIRED(9301, "Slug danh mục không được để trống", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_FOUND(9302, "Danh mục không tồn tại", HttpStatus.NOT_FOUND),
    CATEGORY_NAME_ALREADY_EXISTS(3100, "Tên danh mục đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
    CATEGORY_SLUG_ALREADY_EXISTS(3100, "Đường dẫn đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),

    // PRODUCT

    // ===== PRODUCT =====
    PRODUCT_NOT_FOUND(3200, "Không tìm thấy sản phẩm", HttpStatus.NOT_FOUND),
    PRODUCT_NAME_REQUIRED(3200, "Tên sản phẩm không được để trống", HttpStatus.NOT_FOUND),
    CATEGORY_ID_REQUIRED(3200, "Phải chọn danh mục cho sản phẩm", HttpStatus.NOT_FOUND),
    PRODUCT_PRICE_REQUIRED(3200, "Giá mặc định của sản phẩm không được để trống.", HttpStatus.NOT_FOUND),
    PRODUCT_SLUG_REQUIRED(3200, "Tên slug sản phẩm  không được để trống", HttpStatus.NOT_FOUND),
    PRODUCT_SLUG_ALREADY_EXISTS(3200, "Slug sản phẩm đã tồn tại trong hệ thống", HttpStatus.NOT_FOUND),

    PRODUCT_SKU_ALREADY_EXISTS(3201, "SKU sản phẩm đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),
    PRODUCT_SKU_REQUIRED(3201, "SKU sản phẩm không được để trống.", HttpStatus.BAD_REQUEST),

    PRODUCT_NAME_ALREADY_EXISTS(3202, "Tên sản phẩm đã tồn tại trong hệ thống", HttpStatus.BAD_REQUEST),

    PRODUCT_PRICE_INVALID(3203, "Giá sản phẩm không hợp lệ", HttpStatus.BAD_REQUEST),
    PRODUCT_SALE_PRICE_INVALID(3203, "Giá sản phẩm không hợp lệ", HttpStatus.BAD_REQUEST),

    PRODUCT_STOCK_INVALID(3205, "Số lượng tồn kho không hợp lệ", HttpStatus.BAD_REQUEST),
    PRODUCT_QUANTITY_REQUIRED(3205, "Số lượng không được để trống", HttpStatus.BAD_REQUEST),
    PRODUCT_QUANTITY_MIN(3205, "Số lượng phải lớn hơn hoặc bằng 1", HttpStatus.BAD_REQUEST),
    PRODUCT_SALE_PRICE_GREATER_THAN_PRICE(3206, "Giá khuyến mãi không được lớn hơn giá gốc", HttpStatus.BAD_REQUEST),
    IMAGE_NOT_FOUND(404, "Ảnh không hợp lệ.", HttpStatus.FOUND),
    TOO_MANY_IMAGES(400, "Tổng MB của các file vượt quá MB quy định.", HttpStatus.BAD_REQUEST),

    PRODUCT_OUT_OF_STOCK(3203, "Sản phẩm không đủ tồn kho", HttpStatus.BAD_REQUEST),

    // CART
    CART_QUANTITY_INVALID(4201, "Số lượng sản phẩm phải lớn hơn 0", HttpStatus.BAD_REQUEST),
    CART_ALREADY_EXISTS(4201, "Giỏ hàng không tồn tại trong hệ thống.", HttpStatus.BAD_REQUEST),
    VOUCHER_CODE_ALREADY_EXISTS(4201, "Mã voucher đã tồn tại trong hệ thống.", HttpStatus.BAD_REQUEST),
    ORDER_ALREADY_EXISTS(4201, "Đơn không tồn tại trong hệ thống.", HttpStatus.BAD_REQUEST),

    CART_EXCEED_AVAILABLE_STOCK(4202, "Số lượng vượt quá tồn kho cho phép", HttpStatus.BAD_REQUEST),
    ORDER_ITEMS_NULL(4201, "Giỏ hàng không tồn tại trong hệ thống.", HttpStatus.BAD_REQUEST),
    ORDER_ITEMS_EMPTY(4202, "Đơn hàng phải có ít nhất một sản phẩm.", HttpStatus.BAD_REQUEST),

    SHIPPING_ADDRESS_NULL(4203, "Địa chỉ giao hàng là bắt buộc.", HttpStatus.BAD_REQUEST),

    PRODUCT_ID_NULL(4204, "Sản phẩm chọn là bắt buộc.", HttpStatus.BAD_REQUEST),
    ORDER_ID_NULL(4204, "Đặt hàng là bắt buộc.", HttpStatus.BAD_REQUEST),
    PRODUCT_IS_ACTIVE(4204, "Sản phẩm trạng thái không hoạt động.", HttpStatus.BAD_REQUEST),
    QUANTITY_NULL(4205, "Số lượng là bắt buộc.", HttpStatus.BAD_REQUEST),
    MSG_PAYMENT_METHOD_NULL(4205, "Chọn cách thanh toán là bắt buộc.", HttpStatus.BAD_REQUEST),
    QUANTITY_INVALID(4206, "Số lượng phải lớn hơn hoặc bằng 1.", HttpStatus.BAD_REQUEST),
    END_TIME_BEFORE_START_TIME(4206, "Thời gian kết thúc phải sau thời gian bắt đầu", HttpStatus.BAD_REQUEST),
    VOUCHER_DISCOUNT_PERCENTAGE_INVALID(
            4207, "Giá trị giảm giá theo phần trăm không được vượt quá 100%", HttpStatus.BAD_REQUEST),
    CUSTOMER_NOTE_TOO_LONG(4207, "Ghi chú khách hàng không được vượt quá 1000 ký tự.", HttpStatus.BAD_REQUEST),
    ORDER_STATUS_FINAL(4207, "Không thể thay đổi trạng thái đơn hàng đã kết thúc", HttpStatus.BAD_REQUEST),
    INVALID_ORDER_STATUS_TRANSITION(4207, "Chuyển trạng thái đơn hàng không hợp lệ", HttpStatus.BAD_REQUEST),
    // ===== Voucher Code =====
    VOUCHER_CODE_NULL(4301, "Mã voucher không được để trống", HttpStatus.BAD_REQUEST),
    VOUCHER_ALREADY_EXISTS(4301, "Voucher không tồn tại", HttpStatus.BAD_REQUEST),
    VOUCHER_INVALID(4301, "Không thể xóa voucher đã được sử dụng", HttpStatus.BAD_REQUEST),

    VOUCHER_CODE_LENGTH_INVALID(4302, "Mã voucher phải từ 3-50 ký tự", HttpStatus.BAD_REQUEST),
    VOUCHER_QUANTITY_INVALID(4302, "Số lượng mới không được nhỏ hơn số lượng đã sử dụng", HttpStatus.BAD_REQUEST),

    VOUCHER_CODE_FORMAT_INVALID(
            4303, "Mã voucher chỉ chứa chữ in hoa, số, gạch ngang và gạch dưới", HttpStatus.BAD_REQUEST),

    // ===== Voucher Name =====
    VOUCHER_NAME_NULL(4304, "Tên voucher không được để trống", HttpStatus.BAD_REQUEST),

    VOUCHER_NAME_LENGTH_EXCEEDED(4305, "Tên voucher không quá 200 ký tự", HttpStatus.BAD_REQUEST),

    // ===== Discount =====
    DISCOUNT_TYPE_NULL(4306, "Loại giảm giá không được để trống", HttpStatus.BAD_REQUEST),

    DISCOUNT_VALUE_NULL(4307, "Giá trị giảm giá không được để trống", HttpStatus.BAD_REQUEST),

    DISCOUNT_VALUE_INVALID(4308, "Giá trị giảm giá phải lớn hơn 0", HttpStatus.BAD_REQUEST),

    DISCOUNT_AMOUNT_INVALID(4309, "Giảm giá tối đa phải >= 0", HttpStatus.BAD_REQUEST),

    MIN_ORDER_AMOUNT_INVALID(4310, "Giá trị đơn hàng tối thiểu phải >= 0", HttpStatus.BAD_REQUEST),
    MIN_ORDER_AMOUNT_NULL(4310, "Giá trị đơn hàng không được để trống", HttpStatus.BAD_REQUEST),

    // ===== Quantity =====
    VOUCHER_TOTAL_QUANTITY_NULL(4311, "Số lượng voucher không được để trống", HttpStatus.BAD_REQUEST),
    INVALID_VOUCHER(3001, "Voucher không hợp lệ hoặc đã hết hạn", HttpStatus.BAD_REQUEST),
    ORDER_ALREADY_USED_VOUCHER(3002, "Đơn hàng đã sử dụng voucher", HttpStatus.BAD_REQUEST),
    ORDER_AMOUNT_TOO_LOW(3003, "Đơn hàng chưa đạt giá trị tối thiểu", HttpStatus.BAD_REQUEST),
    VOUCHER_USAGE_LIMIT_EXCEEDED(3004, "Bạn đã sử dụng hết số lần cho phép với voucher này", HttpStatus.BAD_REQUEST),

    VOUCHER_TOTAL_QUANTITY_INVALID(4312, "Số lượng voucher phải >= 1", HttpStatus.BAD_REQUEST),

    MSG_VOUCHER_USAGE_LIMIT_INVALID(4313, "Giới hạn sử dụng phải >= 1", HttpStatus.BAD_REQUEST),

    // ===== Time =====
    MSG_VOUCHER_START_TIME_NULL(4314, "Thời gian bắt đầu không được để trống", HttpStatus.BAD_REQUEST),

    MSG_VOUCHER_END_TIME_NULL(4315, "Thời gian kết thúc không được để trống", HttpStatus.BAD_REQUEST),
    BANNER_NOT_NULL(4301, "Banner không tồn tại.", HttpStatus.BAD_REQUEST),
    BANNER_TITLE_BLANK(4301, "Tiêu đề banner không được để trống", HttpStatus.BAD_REQUEST),
    BANNER_IMAGE_URL_BLANK(4302, "Ảnh banner không được để trống", HttpStatus.BAD_REQUEST),
    BANNER_POSITION_NULL(4303, "Vị trí banner không được để trống", HttpStatus.BAD_REQUEST),
    BANNER_DISPLAY_ORDER_INVALID(4304, "Thứ tự hiển thị phải >= 0", HttpStatus.BAD_REQUEST),
    BANNER_TIME_INVALID(4305, "Thời gian kết thúc phải sau thời gian bắt đầu", HttpStatus.BAD_REQUEST),
    BANNER_NOT_FOUND(4306, "Không tìm thấy banner", HttpStatus.NOT_FOUND),
    USER_VIP_NOT_FOUND(4306, "Không tìm thấy tài khoản vip", HttpStatus.NOT_FOUND),
    USER_VIP_POINTS_NOT_FOUND(4306, "Điểm bạn không đủ.", HttpStatus.NOT_FOUND),
    WISH_NOT_FOUND(4306, "Không tìm thấy sản phẩm trong wish", HttpStatus.NOT_FOUND),
    WISH_ALREADY_EXISTS(4306, "Đã tồn tại trong hệ thống.", HttpStatus.NOT_FOUND),
    RATING_NOT_NULL(1001, "Đánh giá sao không được để trống", HttpStatus.BAD_REQUEST),
    PRODUCT_CATEGORY_NOT_NULL(1001, "Sản phẩm không có trong đơn hàng", HttpStatus.BAD_REQUEST),
    PRODUCT_DELIVERED_NOT_NULL(1001, "Chỉ có thể đánh giá sau khi đơn hàng đã hoàn thành", HttpStatus.BAD_REQUEST),
    REVIEW_NOT_NULL(1001, "Review không tồn tại", HttpStatus.BAD_REQUEST),
    ORDER_USER_NOT_NULL(1001, "Đơn hàng không thuộc về bạn", HttpStatus.BAD_REQUEST),
    USER_NOT_ACCESS(1001, "Bạn không có quyền chỉnh sửa review này", HttpStatus.BAD_REQUEST),
    RATING_MIN(1002, "Đánh giá sao phải từ 1 đến 5", HttpStatus.BAD_REQUEST),
    RATING_MAX(1003, "Đánh giá sao phải từ 1 đến 5", HttpStatus.BAD_REQUEST),
    CONTENT_SIZE(1004, "Nội dung đánh giá không được vượt quá 2000 ký tự", HttpStatus.BAD_REQUEST),
    IMAGE_URL_SIZE(1005, "URL hình ảnh không được vượt quá 500 ký tự", HttpStatus.BAD_REQUEST),
    OPTION_EXISTS(1005, "option name đã tồn tại trong hệ thống.", HttpStatus.BAD_REQUEST),
    OPTION_NULL_NOT(1005, "option không tồn tại trong hệ thống.", HttpStatus.BAD_REQUEST),
    PRODUCT_REVIEW_ALREADY_EXISTS(4301, "Bạn đã đánh giá sản phẩm này từ đơn hàng này rồi.", HttpStatus.BAD_REQUEST),
    ORDER_PAYMENT_REQUIRED(3001, "Đơn hàng phải được thanh toán trước khi hoàn thành", HttpStatus.BAD_REQUEST),
    POINT_REDEEM_LOG_NOT_FOUND(3001, "Không tìm thấy lịch sử sử dụng điểm cho đơn hàng này", HttpStatus.NOT_FOUND),

    PRICE_NOT_NULL(2005, "Giá không được null", HttpStatus.BAD_REQUEST),
    PRICE_INVALID(2006, "Giá phải lớn hơn 0", HttpStatus.BAD_REQUEST),

    SALE_PRICE_INVALID(2007, "Giá sale phải lớn hơn 0", HttpStatus.BAD_REQUEST),

    STOCK_NOT_NULL(2008, "Số lượng tồn kho không được null", HttpStatus.BAD_REQUEST),
    STOCK_NEGATIVE(2009, "Số lượng tồn kho không được âm", HttpStatus.BAD_REQUEST),

    WEIGHT_INVALID(2010, "Cân nặng phải lớn hơn 0", HttpStatus.BAD_REQUEST),

    DIMENSION_TOO_LONG(2011, "Kích thước tối đa 100 ký tự", HttpStatus.BAD_REQUEST),
    BARCODE_TOO_LONG(2012, "Barcode tối đa 50 ký tự", HttpStatus.BAD_REQUEST),

    IS_DEFAULT_NOT_NULL(2013, "Phải xác định variant mặc định hay không", HttpStatus.BAD_REQUEST),

    OPTION_VALUE_EMPTY(2014, "Variant phải có ít nhất 1 option value", HttpStatus.BAD_REQUEST),
    OPTION_VALUE_ID_NULL(2015, "Option value id không được null", HttpStatus.BAD_REQUEST),
    OPTION_VALUE_NOT_BELONG_TO_PRODUCT(3001, "Option value không thuộc product", HttpStatus.BAD_REQUEST),

    OPTION_VALUE_REQUIRED(3002, "Product có option nhưng variant chưa chọn option value", HttpStatus.BAD_REQUEST),

    DUPLICATE_OPTION_TYPE(3003, "Variant không được chứa nhiều option value cùng loại", HttpStatus.BAD_REQUEST),
    VARIANT_ALREADY_EXISTS(3200, "Biến thể của sản phẩm đã tồn tại.", HttpStatus.BAD_REQUEST),
    VARIANT_NOT_FOUND(4001, "Variant không tồn tại", HttpStatus.NOT_FOUND),
    NOTIFICATION_NOT_FOUND(4001, "Thông báo không tồn tại", HttpStatus.NOT_FOUND),

    VARIANT_NOT_BELONG_TO_PRODUCT(4002, "Variant không thuộc product", HttpStatus.BAD_REQUEST),
    POINT_ALREADY_REFUNDED(3002, "Điểm của đơn hàng này đã được hoàn trước đó", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatusCode httpStatusCode;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
