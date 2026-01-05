package com.example.avakids_backend.util.file.sevrice;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;



import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${app.file.max-size:5242880}") // 5MB default
    private long maxFileSize;
        @Value("${app.file.max-size-img:2097152}")
    private long MAX_AVATAR_SIZE ;

    private static final List<String> ALLOWED_EXTENSIONS =
            Arrays.asList("jpg", "jpeg", "png", "pdf", "doc", "docx", "xls", "xlsx");

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    private static final List<String> IMAGE_EXTENSIONS =
            List.of("jpg", "jpeg", "png", "webp");

    private static final List<String> IMAGE_CONTENT_TYPES =
            List.of("image/jpeg", "image/png", "image/webp");
    /**
     * Upload file và trả về đường dẫn file
     */
    public String uploadFile(MultipartFile file, String subFolder) {
        validateFile(file);

        try {
            // Tạo tên file unique
            String fileName = generateUniqueFileName(file.getOriginalFilename());

            // Tạo đường dẫn theo ngày: uploads/expenses/2024/11/03/
            Path uploadPath = createUploadPath(subFolder);

            // Copy file vào thư mục
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("File uploaded successfully: {}", filePath);
            // Trả về relative path để lưu vào DB
            Path rootPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path relativePath = rootPath.relativize(filePath.toAbsolutePath().normalize());
            // String relativePath = uploadPath.relativize(Paths.get(uploadDir)).resolve(fileName).toString();
            log.info("File uploaded successfully: {}", relativePath);

            return relativePath.toString().replace("\\", "/"); // Chuẩn hóa path

        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Xóa file
     */
    public void deleteFile(String filePath) {
        try {
            if (filePath != null && !filePath.isEmpty()) {
                Path path = Paths.get(uploadDir).resolve(filePath).normalize();
                boolean deleted = Files.deleteIfExists(path);
                if (deleted) {
                    log.info("File deleted successfully: {}", path.toAbsolutePath());
                } else {
                    log.warn("File not found, cannot delete: {}", path.toAbsolutePath());
                }
            }
        } catch (IOException e) {
            log.error("Failed to delete file: {}", filePath, e);
            // Không throw exception, chỉ log
        }
    }

    /**
     * Validate file
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        // Check file size
        if (file.getSize() > maxFileSize) {
            throw new AppException(ErrorCode.FILE_TOO_LARGE);
        }

        // Check file extension
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_FILE_NAME);
        }

        String extension = getFileExtension(originalFileName);
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }
    }
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
        if (!IMAGE_EXTENSIONS.contains(extension)) {
            throw new AppException(ErrorCode.FILE_NOT_IMAGE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !IMAGE_CONTENT_TYPES.contains(contentType)) {
            throw new AppException(ErrorCode.FILE_NOT_IMAGE);
        }
    }


    /**
     * Tạo đường dẫn upload theo cấu trúc: uploads/expenses/2024/11/03/
     */
    private Path createUploadPath(String subFolder) throws IOException {
        LocalDate now = LocalDate.now();
        Path uploadPath = Paths.get(uploadDir)
                .resolve(subFolder)
                .resolve(String.valueOf(now.getYear()))
                .resolve(String.format("%02d", now.getMonthValue()))
                .resolve(String.format("%02d", now.getDayOfMonth()));

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        return uploadPath;
    }

    /**
     * Tạo tên file unique: UUID_originalName
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
     * Get full file path
     */
    public Path getFilePath(String relativePath) {
        return Paths.get(uploadDir).resolve(relativePath).normalize();
    }
}
