package io.repsy.repo.storage;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface PackageStorageService {
    void saveFiles(String packageName, String version, MultipartFile packageRep, MultipartFile metaJson) throws IOException;
    Resource loadFile(String packageName, String version, String filename) throws IOException;
}