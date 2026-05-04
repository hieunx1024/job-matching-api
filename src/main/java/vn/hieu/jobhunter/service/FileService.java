package vn.hieu.jobhunter.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;

import java.util.HashMap;
import java.util.Map;

@Service
public class FileService {

    @Value("${jobhunter.upload-file.base-uri}")
    private String baseURI;

    @Value("${jobhunter.storage.type:CLOUDINARY}")
    private String storageType;

    private final Cloudinary cloudinary;

    public FileService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public void createDirectory(String folder) throws URISyntaxException {
        if ("LOCAL".equalsIgnoreCase(storageType)) {
            try {
                Path p = getBasePath(folder);
                if (!Files.exists(p)) {
                    Files.createDirectories(p);
                    System.out.println(">>> CREATE NEW DIRECTORY SUCCESSFUL, PATH = " + p);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(">>> Cloudinary handles directory mapping for: " + folder);
        }
    }

    public String store(MultipartFile file, String folder) throws IOException {
        System.out.println(">>> FILE SERVICE: Current storage type = " + storageType);

        if ("CLOUDINARY".equalsIgnoreCase(storageType)) {
            String originalName = file.getOriginalFilename();
            boolean isPdf = originalName != null && originalName.toLowerCase().endsWith(".pdf");

            // FIX: Dùng "raw" cho PDF thay vì "image" để tránh lỗi 401
            String resourceType = isPdf ? "raw" : "auto";

            // FIX: Tạo public_id KHÔNG có đuôi .pdf (Cloudinary tự append)
            String publicId = null;
            if (originalName != null) {
                String nameWithoutExt = isPdf
                        ? originalName.substring(0, originalName.length() - 4)
                        : originalName;
                publicId = nameWithoutExt + "_" + System.currentTimeMillis();
                // Không thêm ".pdf" ở đây — Cloudinary sẽ tự thêm
            }

            Map<String, Object> params = new HashMap<>();
            params.put("folder", "jobhunter/" + folder);
            params.put("resource_type", resourceType);
            params.put("type", "upload");
            params.put("access_mode", "public");
            params.put("use_filename", true);
            params.put("unique_filename", true);

            if (publicId != null) {
                params.put("public_id", publicId);
            }

            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
                System.out.println(">>> Cloudinary Upload Result (" + resourceType + "): " + uploadResult);
                return (String) uploadResult.get("secure_url");
            } catch (IOException e) {
                System.err.println(">>> FAILED TO UPLOAD TO CLOUDINARY: " + e.getMessage());
                throw e;
            }

        } else {
            // LOCAL STORAGE
            String finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
            Path rootDir = getBasePath(folder);
            if (!Files.exists(rootDir)) {
                Files.createDirectories(rootDir);
            }
            Path destination = rootDir.resolve(finalName);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            return finalName;
        }
    }

    public long getFileLength(String fileName, String folder) {
        if (fileName != null && fileName.startsWith("http"))
            return 1;

        try {
            Path path = getBasePath(folder).resolve(fileName);
            File file = path.toFile();
            if (!file.exists() || file.isDirectory())
                return 0;
            return file.length();
        } catch (Exception e) {
            return 0;
        }
    }

    public InputStreamResource getResource(String fileName, String folder)
            throws URISyntaxException, FileNotFoundException {
        Path path = getBasePath(folder).resolve(fileName);
        File file = path.toFile();
        return new InputStreamResource(new FileInputStream(file));
    }

    private Path getBasePath(String folder) {
        String safeBaseURI = baseURI;
        if (safeBaseURI.startsWith("file://")) {
            safeBaseURI = safeBaseURI.substring(7);
        }
        return Paths.get(safeBaseURI, folder);
    }
}