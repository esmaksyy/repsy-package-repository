package io.repsy.repo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.repsy.repo.entity.PackageEntity;
import io.repsy.repo.repository.PackageRepository;
import io.repsy.repo.storage.PackageStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;

@RestController
public class PackageController {

    private final PackageStorageService storageService;
    private final PackageRepository packageRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public PackageController(PackageStorageService storageService, PackageRepository packageRepository, ObjectMapper objectMapper) {
        this.storageService = storageService;
        this.packageRepository = packageRepository;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/{packageName}/{version}")
    @Transactional
    public ResponseEntity<?> uploadPackage(
            @PathVariable("packageName") String packageName,
            @PathVariable("version") String version,
            @RequestParam("package.rep") MultipartFile packageRep,
            @RequestParam("meta.json") MultipartFile metaJson) {
        
        System.out.println("packageRep: " + packageRep);
        System.out.println("metaJson: " + metaJson);

        if (packageRep == null || metaJson == null ||
                packageRep.isEmpty() || metaJson.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body("Both package.rep and meta.json files are required.");
        }

        if (!packageRep.getOriginalFilename().endsWith(".rep")) {
            return ResponseEntity
                    .badRequest()
                    .body("package.rep file must have .rep extension");
        }

        MetaDto meta;
        try {
            meta = objectMapper.readValue(metaJson.getInputStream(), MetaDto.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("meta.json is not valid JSON");
        }
        if (!packageName.equals(meta.getName()) || !version.equals(meta.getVersion())) {
            return ResponseEntity.badRequest().body("packageName/version does not match meta.json");
        }

        try {
            storageService.saveFiles(packageName, version, packageRep, metaJson);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File save failed: " + e.getMessage());
        }

        PackageEntity pkg = new PackageEntity();
        pkg.setName(packageName);
        pkg.setVersion(version);
        pkg.setAuthor(meta.getAuthor());
        pkg.setUploadDate(LocalDateTime.now());
        pkg.setMetaFilePath("meta.json");
        pkg.setPackageFilePath("package.rep");
        pkg.setStorageType(System.getProperty("storage.strategy", "object-storage"));

        packageRepository.save(pkg);

        return ResponseEntity.status(HttpStatus.CREATED).body("Package uploaded successfully");
    }

    @GetMapping("/{packageName}/{version}/{filename}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable("packageName") String packageName,
            @PathVariable("version") String version,
            @PathVariable("filename") String filename
    ) {
        PackageEntity pkg = packageRepository.findByNameAndVersion(packageName, version)
                .orElse(null);
        if (pkg == null) {
            return ResponseEntity.notFound().build();
        }
        Resource fileResource;
        try {
            fileResource = storageService.loadFile(packageName, version, filename);
            if (fileResource == null || !fileResource.exists()) {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
        String contentType = "application/octet-stream";
        if (filename.endsWith(".json")) {
            contentType = "application/json";
        } else if (filename.endsWith(".rep")) {
            contentType = "application/zip";
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(fileResource);
    }
}