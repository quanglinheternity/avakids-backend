package com.example.avakids_backend.util.file.sevrice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.avakids_backend.exception.AppException;
import com.example.avakids_backend.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileStorageService {

    @Value("${app.file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${app.file.max-size:5242880}") // 5MB default
    private long maxFileSize;

    @Value("${app.file.max-size-img:2097152}")
    private long MAX_AVATAR_SIZE;

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
    private static final List<String> IMAGE_EXTENSIONS = List.of("jpg", "jpeg", "png", "webp");

    private static final List<String> IMAGE_CONTENT_TYPES = List.of("image/jpeg", "image/png", "image/webp");

    /**
     * Upload một file và trả về đường dẫn file
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
            log.info("File uploaded successfully: {}", relativePath);

            return relativePath.toString().replace("\\", "/"); // Chuẩn hóa path

        } catch (IOException e) {
            log.error("Failed to upload file", e);
            throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
        }
    }

    /**
     * Upload nhiều file cùng lúc
     * @param files Danh sách file cần upload
     * @param subFolder Thư mục con để lưu file
     * @return Danh sách đường dẫn của các file đã upload
     */
    public List<String> uploadMultipleFiles(MultipartFile[] files, String subFolder) {
        if (files == null || files.length == 0) {
            throw new AppException(ErrorCode.FILE_EMPTY);
        }

        List<String> uploadedFilePaths = new ArrayList<>();
        List<String> failedFiles = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            try {
                if (file != null && !file.isEmpty()) {
                    String filePath = uploadFile(file, subFolder);
                    uploadedFilePaths.add(filePath);
                    log.info("File {} uploaded: {}", i + 1, filePath);
                }
            } catch (Exception e) {
                String fileName = file != null ? file.getOriginalFilename() : "unknown";
                failedFiles.add(fileName);
                log.error("Failed to upload file: {}", fileName, e);

                // Rollback: xóa các file đã upload nếu có lỗi
                if (!uploadedFilePaths.isEmpty()) {
                    log.warn("Rolling back uploaded files due to error");
                    uploadedFilePaths.forEach(this::deleteFile);
                    uploadedFilePaths.clear();
                }

                throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        }

        if (!failedFiles.isEmpty()) {
            log.error("Failed to upload {} files: {}", failedFiles.size(), failedFiles);
        }

        log.info("Successfully uploaded {} files", uploadedFilePaths.size());
        return uploadedFilePaths;
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

        List<String> uploadedFilePaths = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                if (file != null && !file.isEmpty()) {
                    validateImage(file);
                    String filePath = uploadFileInternal(file, subFolder);
                    uploadedFilePaths.add(filePath);
                }
            } catch (Exception e) {
                log.error("Failed to upload image: {}", file.getOriginalFilename(), e);

                // Rollback
                uploadedFilePaths.forEach(this::deleteFile);
                throw new AppException(ErrorCode.FILE_UPLOAD_FAILED);
            }
        }

        return uploadedFilePaths;
    }

    /**
     * Upload file internal (không validate lại)
     */
    private String uploadFileInternal(MultipartFile file, String subFolder) throws IOException {
        String fileName = generateUniqueFileName(file.getOriginalFilename());
        Path uploadPath = createUploadPath(subFolder);
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Path rootPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path relativePath = rootPath.relativize(filePath.toAbsolutePath().normalize());

        return relativePath.toString().replace("\\", "/");
    }

    /**
     * Xóa một file
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
        }
    }

    /**
     * Xóa nhiều file
     */
    public void deleteMultipleFiles(List<String> filePaths) {
        if (filePaths == null || filePaths.isEmpty()) {
            return;
        }

        for (String filePath : filePaths) {
            deleteFile(filePath);
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
        if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
            throw new AppException(ErrorCode.INVALID_FILE_TYPE);
        }

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
