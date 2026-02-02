package com.example.avakids_backend.util.file.sevrice;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;

import static com.example.avakids_backend.util.file.sevrice.ImageUtil.compressJpeg;
import static com.example.avakids_backend.util.file.sevrice.ImageUtil.resize;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudService {

    private final Cloudinary cloudinary;

    @Value("${app.file.max-size:5242880}") // 5MB default
    private long maxFileSize;

    @Value("${app.file.max-size-img:2097152}")
    private long MAX_AVATAR_SIZE;

    private static final List<String> ALLOWED_EXTENSIONS_CLOUDINARY =
            Arrays.asList("jpg", "jpeg", "png", "pdf", "doc", "docx", "xls", "xlsx");

    private static final List<String> ALLOWED_CONTENT_TYPES_CLOUDINARY = Arrays.asList(
            "image/jpeg",
            "image/png",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private static final List<String> IMAGE_EXTENSIONS_CLOUDINARY = List.of("jpg", "jpeg", "png", "webp", "gif");
    private static final List<String> IMAGE_CONTENT_TYPES_CLOUDINARY =
            List.of("image/jpeg", "image/png", "image/webp", "image/gif");

    /**
     * Upload một file lên Cloudinary
     */
    public String uploadFile(MultipartFile file, String subFolder) {
        validateFile(file);

        try {
            String fileName = generateUniqueFileName(file.getOriginalFilename());
            String publicId = generatePublicId(fileName, subFolder);
            byte[] uploadBytes = file.getBytes();
            if (isImageFile(file.getOriginalFilename())) {

                BufferedImage originalImage =
                        ImageIO.read(new ByteArrayInputStream(uploadBytes));

                if (originalImage == null) {
                    throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
                }

                // Resize (ví dụ max width = 1200px)
                BufferedImage resizedImage = resize(originalImage, 1200);

                // Compress JPEG (quality 0.75f ~ khá đẹp & nhẹ)
                uploadBytes = compressJpeg(resizedImage, 0.75f);
            }
            Map<String, Object> options = new HashMap<>();
            options.put("public_id", publicId);
            options.put("folder", subFolder);

            // Set resource type based on file type
            if (isImageFile(file.getOriginalFilename())) {
                options.put("resource_type", "image");
            } else {
                options.put("resource_type", "auto"); // Cloudinary tự động detect
            }

            Map uploadResult = cloudinary.uploader().upload(uploadBytes, options);
            String secureUrl = uploadResult.get("secure_url").toString();

            log.info("File uploaded successfully to Cloudinary: {}", secureUrl);
            return secureUrl;

        } catch (IOException e) {
            log.error("Failed to upload file to Cloudinary", e);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Upload nhiều file cùng lúc
     */
    public List<String> uploadMultipleFiles(MultipartFile[] files, String subFolder) {
        if (files == null || files.length == 0) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        List<String> uploadedUrls = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            try {
                if (file != null && !file.isEmpty()) {
                    String fileUrl = uploadFile(file, subFolder);
                    uploadedUrls.add(fileUrl);
                    log.info("File {} uploaded: {}", i + 1, fileUrl);
                }
            } catch (Exception e) {
                String fileName = file != null ? file.getOriginalFilename() : "unknown";
                failedFiles.add(fileName);
                log.error("Failed to upload file: {}", fileName, e);

                // Rollback: xóa các file đã upload nếu có lỗi
                if (!uploadedUrls.isEmpty()) {
                    log.warn("Rolling back uploaded files due to error");
                    deleteMultipleFiles(uploadedUrls);
                    uploadedUrls.clear();
                }

                throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        }

        if (!failedFiles.isEmpty()) {
            log.error("Failed to upload {} files: {}", failedFiles.size(), failedFiles);
        }

        log.info("Successfully uploaded {} files to Cloudinary", uploadedUrls.size());
        return uploadedUrls;
    }

    /**
     * Upload nhiều file với List<MultipartFile>
     */
    public List<String> uploadMultipleFiles(List<MultipartFile> files, String subFolder) {
        if (files == null || files.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }
        return uploadMultipleFiles(files.toArray(new MultipartFile[0]), subFolder);
    }

    /**
     * Upload nhiều ảnh cùng lúc
     */
    public List<String> uploadMultipleImages(MultipartFile[] files, String subFolder) {
        if (files == null || files.length == 0) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        List<String> uploadedUrls = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                if (file != null && !file.isEmpty()) {
                    validateImage(file);
                    String fileUrl = uploadFileInternal(file, subFolder);
                    uploadedUrls.add(fileUrl);
                }
            } catch (Exception e) {
                String fileName = file.getOriginalFilename();
                failedFiles.add(fileName);
                log.error("Failed to upload image: {}", fileName, e);

                // Rollback
                if (!uploadedUrls.isEmpty()) {
                    deleteMultipleFiles(uploadedUrls);
                }
                throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        }

        return uploadedUrls;
    }

    /**
     * Upload file internal (dành cho ảnh, không validate lại)
     */
    private String uploadFileInternal(MultipartFile file, String subFolder) throws IOException {
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        String publicId = generatePublicId(fileName, subFolder);
        byte[] fileBytes = file.getBytes();

        Map<String, Object> options = new HashMap<>();
        options.put("public_id", publicId);
        options.put("folder", subFolder);
        options.put("resource_type", "image");

        Map uploadResult = cloudinary.uploader().upload(fileBytes, options);
        return uploadResult.get("secure_url").toString();
    }

    /**
     * Xóa một file từ Cloudinary
     */
    public void deleteFile(String fileUrl) {
        try {
            if (fileUrl != null && !fileUrl.isEmpty()) {
                String publicId = extractPublicId(fileUrl);
                Map result = cloudinary
                        .uploader()
                        .destroy(publicId, ObjectUtils.asMap("resource_type", getResourceTypeFromUrl(fileUrl)));

                if ("ok".equals(result.get("result"))) {
                    log.info("File deleted successfully from Cloudinary: {}", fileUrl);
                } else {
                    log.warn("File deletion may have failed: {}", result);
                }
            }
        } catch (Exception e) {
            log.error("Failed to delete file from Cloudinary: {}", fileUrl, e);
            // Không ném exception vì đây có thể là cleanup operation
        }
    }

    /**
     * Xóa nhiều file từ Cloudinary
     */
    public void deleteMultipleFiles(List<String> fileUrls) {
        if (fileUrls == null || fileUrls.isEmpty()) {
            return;
        }

        List<String> publicIds = fileUrls.stream()
                .filter(url -> url != null && !url.isEmpty())
                .map(this::extractPublicId)
                .collect(Collectors.toList());

        if (!publicIds.isEmpty()) {
            try {
                Map result = cloudinary.api().deleteResources(publicIds, ObjectUtils.asMap("resource_type", "image"));
                log.info("Deleted {} files from Cloudinary: {}", publicIds.size(), result);
            } catch (Exception e) {
                log.error("Failed to delete multiple files from Cloudinary", e);
                // Fallback: xóa từng file nếu batch delete thất bại
                for (String url : fileUrls) {
                    try {
                        deleteFile(url);
                    } catch (Exception ex) {
                        log.error("Failed to delete individual file: {}", url, ex);
                    }
                }
            }
        }
    }

    /**
     * Validate file
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        if (file.getSize() > maxFileSize) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_FILE_NAME);
        }

        String extension = getFileExtension(originalFileName);
        if (!ALLOWED_EXTENSIONS_CLOUDINARY.contains(extension.toLowerCase())) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES_CLOUDINARY.contains(contentType)) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }
    }

    /**
     * Validate image file
     */
    public void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        if (file.getSize() > MAX_AVATAR_SIZE) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new AppException(ErrorCode.INVALID_FILE_NAME);
        }

        String extension = getFileExtension(fileName).toLowerCase();
        if (!IMAGE_EXTENSIONS_CLOUDINARY.contains(extension)) {
            throw new AppException(ErrorCode.FILE_NOT_IMAGE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !IMAGE_CONTENT_TYPES_CLOUDINARY.contains(contentType)) {
            throw new AppException(ErrorCode.FILE_NOT_IMAGE);
        }
    }

    /**
     * Tạo public_id cho Cloudinary
     */
    private String generatePublicId(String fileName, String subFolder) {
        String nameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
        // Tạo đường dẫn theo ngày: folder/year/month/day/filename
        Calendar calendar = Calendar.getInstance();
        String datePath = String.format(
                "%04d/%02d/%02d",
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

        return String.format("%s/%s/%s", subFolder, datePath, nameWithoutExt);
    }

    /**
     * Tạo tên file unique
     */
    private String generateUniqueFileName(String originalFileName) {
        String name = StringUtils.cleanPath(originalFileName);
        String extension = getFileExtension(name);
        String nameWithoutExt = name.substring(0, name.lastIndexOf('.'));

        return UUID.randomUUID().toString() + "_" + nameWithoutExt + "." + extension;
    }

    /**
     * Lấy extension của file
     */
    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }

    /**
     * Kiểm tra xem file có phải là ảnh không
     */
    private boolean isImageFile(String fileName) {
        if (fileName == null) return false;
        String extension = getFileExtension(fileName).toLowerCase();
        return IMAGE_EXTENSIONS_CLOUDINARY.contains(extension);
    }

    /**
     * Lấy resource_type từ URL
     */
    private String getResourceTypeFromUrl(String url) {
        return isImageFile(url) ? "image" : "raw";
    }

    /**
     * Extract public_id từ URL Cloudinary
     */
    public String extractPublicId(String imageUrl) {
        try {
            String path = URI.create(imageUrl).getPath();

            String marker = "/upload/";
            int idx = path.indexOf(marker);
            if (idx < 0) throw new IllegalArgumentException("Invalid Cloudinary URL");

            String afterUpload = path.substring(idx + marker.length());

            // Bỏ version nếu có (v1234567/)
            if (afterUpload.startsWith("v")) {
                int slash = afterUpload.indexOf('/');
                if (slash > 0) afterUpload = afterUpload.substring(slash + 1);
            }

            // Bỏ transformation nếu có
            int nextSlash = afterUpload.indexOf('/');
            if (nextSlash > 0) {
                String possibleTransformation = afterUpload.substring(0, nextSlash);
                // Kiểm tra nếu đây là transformation (chứa các tham số như w_, h_, c_)
                if (possibleTransformation.contains("_") || possibleTransformation.matches(".*\\d+.*")) {
                    afterUpload = afterUpload.substring(nextSlash + 1);
                }
            }

            // Loại bỏ extension
            int dot = afterUpload.lastIndexOf('.');
            if (dot > 0) afterUpload = afterUpload.substring(0, dot);

            return afterUpload;
        } catch (Exception e) {
            throw new RuntimeException("Cannot extract public_id from url: " + imageUrl, e);
        }
    }

    /**
     * Upload file trực tiếp từ byte array (giữ nguyên method cũ cho compatibility)
     */
    public String uploadImage(byte[] imageBytes, String publicId) {
        try {
            Map uploadResult = cloudinary
                    .uploader()
                    .upload(
                            imageBytes,
                            ObjectUtils.asMap(
                                    "public_id", publicId,
                                    "folder", "photos",
                                    "resource_type", "image"));
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Upload to Cloudinary failed", e);
        }
    }
}
