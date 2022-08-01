package com.hivetech.service;

import com.hivetech.model.dto.PhotoDTO;
import io.minio.errors.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface PhotoService {
    List<PhotoDTO> findByPet(long petId);

    List<PhotoDTO> insert(MultipartFile[] request, long petId) throws IOException, NoSuchAlgorithmException, ServerException, InsufficientDataException, ErrorResponseException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    PhotoDTO deleteById(int id);

    PhotoDTO activePrimaryPhoto(long petId, int photoId);
}
