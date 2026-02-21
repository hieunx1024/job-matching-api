package vn.hieu.jobhunter.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    @Value("${jobhunter.upload-file.base-uri}")
    private String baseURI;

    private Path getBasePath(String folder) {
        String safeBaseURI = baseURI;
        if (safeBaseURI.startsWith("file://")) {
            safeBaseURI = safeBaseURI.substring(7);
        }
        return Paths.get(safeBaseURI, folder);
    }

    public void createDirectory(String folder) throws URISyntaxException {
        // Log logic kept for compatibility
        try {
            // Check if folder is full URI or relative
            Path p;
            if (folder.startsWith("file:")) {
                URI uri = new URI(folder);
                p = Paths.get(uri);
            } else {
                p = Paths.get(folder);
            }

            if (!Files.exists(p)) {
                Files.createDirectories(p);
                System.out.println(">>> CREATE NEW DIRECTORY SUCCESSFUL, PATH = " + p);
            } else {
                System.out.println(">>> SKIP MAKING DIRECTORY, ALREADY EXISTS: " + p);
            }
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public String store(MultipartFile file, String folder) throws URISyntaxException, IOException {
        String finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();

        // Resolve path safely using Path API, avoiding URI string concatenation issues
        Path rootDir = getBasePath(folder);
        if (!Files.exists(rootDir)) {
            try {
                Files.createDirectories(rootDir);
                System.out.println(">>> Created missing directory: " + rootDir);
            } catch (IOException e) {
                System.err.println(">>> FAILED TO CREATE DIRECTORY: " + rootDir);
                throw e;
            }
        }

        Path destination = rootDir.resolve(finalName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.err.println(">>> FAILED TO STORE FILE: " + destination);
            System.err.println(">>> EXCEPTION: " + e.getClass().getName());
            System.err.println(">>> MESSAGE: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
        return finalName;
    }

    public long getFileLength(String fileName, String folder) throws URISyntaxException {
        Path path = getBasePath(folder).resolve(fileName);
        File file = path.toFile();
        if (!file.exists() || file.isDirectory())
            return 0;
        return file.length();
    }

    public InputStreamResource getResource(String fileName, String folder)
            throws URISyntaxException, FileNotFoundException {
        Path path = getBasePath(folder).resolve(fileName);
        File file = path.toFile();
        return new InputStreamResource(new FileInputStream(file));
    }
}
