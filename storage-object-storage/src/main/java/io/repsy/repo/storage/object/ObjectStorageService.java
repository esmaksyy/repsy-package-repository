package io.repsy.repo.storage.object;

import io.minio.MinioClient;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import io.repsy.repo.storage.PackageStorageService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class ObjectStorageService implements PackageStorageService {

    private final MinioClient minioClient;
    private final String bucketName;

    public ObjectStorageService(String endpoint, String accessKey, String secretKey, String bucketName) {
        this.minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        this.bucketName = bucketName;

        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Minio bucket kontrol/oluşturma başarısız!", e);
        }
    }

    @Override
    public void saveFiles(String packageName, String version, MultipartFile packageRep, MultipartFile metaJson) throws IOException {
        try {
            String basePath = packageName + "/" + version + "/";

            try (InputStream repStream = packageRep.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(basePath + "package.rep")
                                .stream(repStream, packageRep.getSize(), -1)
                                .contentType("application/octet-stream")
                                .build()
                );
            }

            try (InputStream metaStream = metaJson.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(basePath + "meta.json")
                                .stream(metaStream, metaJson.getSize(), -1)
                                .contentType("application/json")
                                .build()
                );
            }
        } catch (Exception e) {
            throw new IOException("MinIO'ya dosya yüklenirken hata oluştu!", e);
        }
    }

    @Override
    public Resource loadFile(String packageName, String version, String filename) throws IOException {
        String objectPath = packageName + "/" + version + "/" + filename;
        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectPath)
                            .build()
            );
            return new InputStreamResource(stream);
        } catch (Exception e) {
            throw new IOException("MinIO'dan dosya okunamadı!", e);
        }
    }
}