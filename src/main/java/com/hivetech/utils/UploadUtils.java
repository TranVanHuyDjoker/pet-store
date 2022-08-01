package com.hivetech.utils;

import com.hivetech.config.exception.BadRequestException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class UploadUtils {

    private UploadUtils() {
    }

    // sua path photo
    private static final List<String> FILE_EXTENSIONS = List.of("jpg", "jepg", "jfif");

    //LƯU RA NGOÀI PROJECT
    public static String createFile(String path, MultipartFile multipartFile, int filename) throws IOException {
        String fileExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        if (!FILE_EXTENSIONS.contains(fileExtension)) {
            throw new BadRequestException("Không được đăng ảnh có định dạng đuôi khác JPG,JEPG");
        }
        Path uploadPath = Paths.get(path);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new BadRequestException("Lỗi " + e.getLocalizedMessage());
            }
        }
        try {
            Path cleanPath = Paths.get(path + filename + "." + fileExtension);
            Files.copy(multipartFile.getInputStream(), cleanPath,
                    StandardCopyOption.REPLACE_EXISTING);
            return cleanPath.toString().replace("\\", "/");
        } catch (IOException e) {
            throw new BadRequestException("Lỗi", e.getCause());
        }
    }

    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) file.delete();
    }

    //MD5?
    public static String hashByteToMD5(InputStream inputStream) throws IOException, NoSuchAlgorithmException {
        byte[] bytes = inputStream.readAllBytes();
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] digest = md5.digest(bytes);
        BigInteger no = new BigInteger(1, digest);
        String hashCode = no.toString(16);
        while (hashCode.length() < 32) {
            hashCode = "0" + hashCode;
        }
        return hashCode;
    }

}
