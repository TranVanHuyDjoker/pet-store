package com.hivetech.service;

import com.hivetech.model.dto.PageDTO;
import com.hivetech.model.dto.PetDTO;
import com.hivetech.model.dto.PetDetailDTO;
import com.hivetech.model.request.PetDetailRequest;
import com.hivetech.model.request.PetRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface PetService {
    PageDTO<PetDTO> findPetByCategory(String categorySlug, int currentPage, int limit);

    PetDTO findPetById(long id);

    PetDTO insertPet(PetRequest request);

    PetDTO updatePet(long id, PetRequest request);

    PetDTO deletePetById(long id);

    PageDTO<PetDTO> getPets(String keyword, int page, int limit, String sortDirection, String sortBy);

    PetDetailDTO getPetDetails(long petId);

    PetDetailDTO insertOrUpdatePetDetails(long petId, PetDetailRequest request);

    List<PetDTO> findAll();
}
