package com.hivetech.service.impl;

import com.hivetech.config.exception.BadRequestException;
import com.hivetech.config.exception.NotFoundException;
import com.hivetech.model.dto.PageDTO;
import com.hivetech.model.dto.PetDTO;
import com.hivetech.model.dto.PetDetailDTO;
import com.hivetech.model.entity.Category;
import com.hivetech.model.entity.Pet;
import com.hivetech.model.entity.PetDetail;
import com.hivetech.model.entity.Photo;
import com.hivetech.model.request.PetDetailRequest;
import com.hivetech.model.request.PetRequest;
import com.hivetech.repository.CategoryRepo;
import com.hivetech.repository.PetDetailRepo;
import com.hivetech.repository.PetRepo;
import com.hivetech.repository.PhotoRepo;
import com.hivetech.service.PetService;
import com.hivetech.utils.constants.CategoryConstants;
import com.hivetech.utils.constants.PetConstants;
import com.hivetech.utils.enumerates.PetStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PetServiceImpl implements PetService {
    private final PetRepo petRepo;
    private final CategoryRepo categoryRepo;
    private final ModelMapper modelMapper;
    private final PhotoRepo photoRepo;
    private final PetDetailRepo petDetailRepo;


    @Override
    public PetDTO findPetById(long id) {
        Pet pet = petRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(PetConstants.NOT_FOUND));
        Photo photo = photoRepo.findPrimaryPhotoByPet(pet);
        PetDTO petDTO = modelMapper.map(pet, PetDTO.class);
        if (Objects.isNull(photo)) return petDTO;
        petDTO.setPrimaryPhoto(photo.getPhotoPath());
        return petDTO;
    }

    @Override
    public PetDTO insertPet(PetRequest request) {
        if (petRepo.existsByName(request.getName())) {
            throw new BadRequestException(PetConstants.EXISTS_BY_NAME);
        }
        Category category = categoryRepo.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException(CategoryConstants.NOT_FOUND));
        Pet pet = modelMapper.map(request, Pet.class);
        pet.setCategory(category);
        pet.setStatus(PetStatus.AVAILABLE);
        pet.setCreateAt(LocalDate.now());
        pet.setUpdateAt(LocalDate.now());
        pet = petRepo.save(pet);
        log.info("Insert pet success {}", pet);

        return modelMapper.map(pet, PetDTO.class);
    }

    @Override
    public PetDTO updatePet(long id, PetRequest request) {
        Pet pet = petRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(PetConstants.NOT_FOUND));
        Category category = categoryRepo.findById(request.getCategoryId())
                .orElseThrow(() -> new NotFoundException(CategoryConstants.NOT_FOUND));
        pet.setName(request.getName());
        pet.setCategory(category);
        pet.setUpdateAt(LocalDate.now());
        pet.setColorType(request.getColor());
        pet.setDescription(request.getDescription());
        pet.setColorType(request.getColor());
        pet.setGender(request.getGender());
        pet.setPrice(request.getPrice());
        pet.setSalePrice(request.getSalePrice());

        pet = petRepo.save(pet);
        log.info("Update Pet with success: " + pet);
        return modelMapper.map(pet, PetDTO.class);
    }

    @Override
    public PetDTO deletePetById(long id) {
        Pet pet = petRepo.findById(id)
                .orElseThrow(() -> new NotFoundException(PetConstants.NOT_FOUND));
        petRepo.deleteById(id);
        log.info("Delete Pet with id: " + id);
        return modelMapper.map(pet, PetDTO.class);
    }

    @Override
    public PageDTO<PetDTO> getPets(String keyword, int currentPage, int limit, String sortDirection, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.valueOf(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(currentPage - 1, limit, sort);
        Page<Pet> petPage = petRepo.paging(keyword, pageable);
        return getPetDTOPageDTO(currentPage, limit, petPage);
    }

    @Override
    public PetDetailDTO getPetDetails(long petId) {
        Pet pet = petRepo.findById(petId)
                .orElseThrow(() -> new NotFoundException(PetConstants.NOT_FOUND));
        PetDetail petDetail = petDetailRepo.findByPet(pet)
                .orElseThrow(() -> new NotFoundException("Thú cưng này chưa có thông tin chi tiết"));
        return modelMapper.map(petDetail, PetDetailDTO.class);
    }

    @Override
    public PetDetailDTO insertOrUpdatePetDetails(long petId, PetDetailRequest request) {
        Pet pet = petRepo.findById(petId)
                .orElseThrow(() -> new NotFoundException(PetConstants.NOT_FOUND));
        PetDetail petDetail = petDetailRepo.findByPet(pet).orElse(null);
        if (Objects.isNull(petDetail)) {
            petDetail = modelMapper.map(request, PetDetail.class);
            petDetail.setPet(pet);
            petDetail = petDetailRepo.save(petDetail);
            log.info("Insert pet details success {}", petDetail);
            return modelMapper.map(petDetail, PetDetailDTO.class);
        }
        petDetail.setDadType(request.getDadType());
        petDetail.setMomType(request.getMomType());
        petDetail.setOrigin(request.getOrigin());
        petDetail.setVaccination(request.getVaccination());
        petDetail.setPurebred(request.getPurebred());
        petDetail.setAge(request.getAge());
        petDetailRepo.save(petDetail);
        log.info("Update pet details success {}", petDetail);
        return modelMapper.map(petDetail, PetDetailDTO.class);
    }

    @Override
    public List<PetDTO> findAll() {
        return petRepo.findAll().stream().map(pet -> modelMapper.map(pet, PetDTO.class)).collect(Collectors.toList());
    }

    private PageDTO<PetDTO> getPetDTOPageDTO(int currentPage, int limit, Page<Pet> petPage) {
        List<PetDTO> petDTOList = petPage.getContent()
                .stream()
                .map(pet -> {
                    Photo photo = photoRepo.findPrimaryPhotoByPet(pet);
                    PetDTO dto = modelMapper.map(pet, PetDTO.class);
                    dto.setPrimaryPhoto(Objects.isNull(photo) ? null : photo.getPhotoPath());
                    return dto;
                }).collect(Collectors.toList());
        return new PageDTO(petDTOList,
                limit,
                currentPage,
                petPage.getTotalPages());
    }

    @Override
    public PageDTO findPetByCategory(String categorySlug, int currentPage, int limit) {
        Pageable pageable = PageRequest.of(currentPage - 1, limit);
        Category category = categoryRepo.findBySlug(categorySlug)
                .orElseThrow(() -> new NotFoundException(CategoryConstants.NOT_FOUND));
        Page<Pet> petPage = petRepo.findByCategory(category, pageable);
        return getPetDTOPageDTO(currentPage, limit, petPage);
    }
}
