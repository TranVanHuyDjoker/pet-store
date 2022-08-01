package com.hivetech.service.impl;


import com.hivetech.config.exception.BadRequestException;
import com.hivetech.config.exception.NotFoundException;
import com.hivetech.model.dto.PhotoDTO;
import com.hivetech.model.entity.Pet;
import com.hivetech.model.entity.Photo;
import com.hivetech.repository.PetRepo;
import com.hivetech.repository.PhotoRepo;
import com.hivetech.service.PhotoService;
import com.hivetech.utils.UploadUtils;
import com.hivetech.utils.constants.PetConstants;
import com.hivetech.utils.constants.PhotoConstants;
import com.tinify.Options;
import com.tinify.Source;
import com.tinify.Tinify;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PhotoServiceImpl implements PhotoService {
    private final PetRepo petRepo;
    private final PhotoRepo photoRepo;
    private final ModelMapper modelMapper;
    private final MinioClient minioClient;
    private final Executor executor;
    @Value("${minio.bucket.name}")
    private String bucketName;


    private Pet findPetByPetId(long petId) {
        return petRepo.findById(petId)
                .orElseThrow(() -> new NotFoundException(PetConstants.NOT_FOUND));
    }

    @Override
    public List<PhotoDTO> findByPet(long petId) {
        Pet pet = this.findPetByPetId(petId);
        return photoRepo.findByPet(pet).stream().map(p -> modelMapper.map(p, PhotoDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<PhotoDTO> insert(MultipartFile[] photos, long petId) throws IOException, NoSuchAlgorithmException, ServerException, InsufficientDataException, ErrorResponseException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        if (photos.length == 0) {
            throw new BadRequestException("Hãy chọn ảnh của thú cưng để đăng tải");
        }
        Pet pet = this.findPetByPetId(petId);
        List<Photo> photoList = photoRepo.findByPet(pet);
        if (photos.length + photoList.size() > 4) {
            throw new BadRequestException("Mỗi thú cưng chỉ có thể đăng tối đa 4 tấm ảnh");
        }
        List<Photo> savedPhotos = new ArrayList<>();
        for (MultipartFile file : photos) {
            String hashingCode = UploadUtils.hashByteToMD5(file.getInputStream());
            if (photoRepo.existsByHashingCodeMD5(hashingCode)) {
                throw new BadRequestException("Thú cưng đã có tấm ảnh này rồi");
            }
            executor.execute(() -> {
                Source source = null;
                try {
                    source = Tinify.fromBuffer(file.getBytes());
                    Options options = new Options()
                            .with("method", "fit")
                            .with("width", 150)
                            .with("height", 100);
                    Source resized = source.resize(options);
                    byte[] bytes = resized.toBuffer();
                    InputStream stream = new ByteArrayInputStream(bytes);
                    log.info("Count updload files in this month {}", Tinify.compressionCount());
                    minioClient.putObject(PutObjectArgs
                            .builder()
                            .bucket(bucketName)
                            .object(file.getOriginalFilename())
                            .stream(stream, bytes.length, -1)
                            .build());
                } catch (IOException | ServerException | InsufficientDataException | ErrorResponseException |
                        NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException |
                        XmlParserException | InternalException e) {
                    log.error("Error handler file {}", e);
                }
            });
            Photo photo = new Photo();
            photo.setPet(pet);
            photo.setHashingCodeMD5(hashingCode);
            photo.setPhotoPath(String.format("%s%s", "photo-pet", "/".concat(file.getOriginalFilename())));
            boolean isPrimary = CollectionUtils.isEmpty(photoList) && savedPhotos.isEmpty() ? true : false;
            photo.setPrimaryPhoto(isPrimary);
            photo = photoRepo.save(photo);
            log.info("Add photo success {}", photo);
            savedPhotos.add(photo);
        }
        return savedPhotos.stream().map(p -> modelMapper.map(p, PhotoDTO.class)).collect(Collectors.toList());
    }


    @Override
    public PhotoDTO deleteById(int id) {
        Photo photo = photoRepo.findById(id)
                .orElseThrow(() -> {
                    return new NotFoundException(PhotoConstants.NOT_FOUND);
                });
        photoRepo.deleteById(id);
        log.info("Delete photo success {}", photo);
        return modelMapper.map(photo, PhotoDTO.class);
    }

    @Override
    public PhotoDTO activePrimaryPhoto(long petId, int photoId) {
        Pet pet = petRepo.findById(petId).orElseThrow(() -> new BadRequestException(PetConstants.NOT_FOUND));
        Photo primaryPhoto = photoRepo.findPrimaryPhotoByPet(pet);
        if (!Objects.isNull(primaryPhoto)) {
            primaryPhoto.setPrimaryPhoto(false);
            photoRepo.save(primaryPhoto);
        }
        Photo photo = photoRepo.findById(photoId)
                .orElseThrow(() -> new NotFoundException(PhotoConstants.NOT_FOUND));
        photo.setPrimaryPhoto(true);
        photo = photoRepo.save(photo);
        log.info("Update photo success {}", photo);
        return modelMapper.map(photo, PhotoDTO.class);
    }

}
