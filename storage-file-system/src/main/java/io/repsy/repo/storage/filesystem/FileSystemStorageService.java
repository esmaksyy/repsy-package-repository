package io.repsy.repo.storage.filesystem;

import io.repsy.repo.storage.PackageStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

public class FileSystemStorageService implements PackageStorageService {

    private final Path rootDirectory;

    public FileSystemStorageService(String rootDirectory) {
        this.rootDirectory = Paths.get(rootDirectory).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Kök dizin oluşturulamadı: " + this.rootDirectory, e);
        }
    }

    @Override
    public void saveFiles(String packageName, String version, MultipartFile packageRep, MultipartFile metaJson) throws IOException {
        Path packageDir = rootDirectory.resolve(Paths.get(packageName, version));
        Files.createDirectories(packageDir);

        Path packageRepPath = packageDir.resolve("package.rep");
        Files.copy(packageRep.getInputStream(), packageRepPath, StandardCopyOption.REPLACE_EXISTING);

        Path metaJsonPath = packageDir.resolve("meta.json");
        Files.copy(metaJson.getInputStream(), metaJsonPath, StandardCopyOption.REPLACE_EXISTING);
    }

    @Override
    public Resource loadFile(String packageName, String version, String filename) throws IOException {
        Path filePath = rootDirectory.resolve(Paths.get(packageName, version, filename));
        if (!Files.exists(filePath)) {
            throw new IOException("Dosya bulunamadı: " + filePath.toString());
        }
        return new UrlResource(filePath.toUri());
    }
}