package com.hivetech.controller;

import com.hivetech.config.exception.BadRequestException;
import com.hivetech.model.dto.PageDTO;
import com.hivetech.model.dto.PetDTO;
import com.hivetech.model.dto.PetDetailDTO;
import com.hivetech.model.dto.PhotoDTO;
import com.hivetech.model.request.PetDetailRequest;
import com.hivetech.model.request.PetRequest;
import com.hivetech.service.PetService;
import com.hivetech.service.PhotoService;
import com.hivetech.utils.StringUtils;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PetController {
    private final PetService petService;
    private final PhotoService photoService;
    //API
    //PETS
    @GetMapping("/api/v1/pets")
    public ResponseEntity<PageDTO<PetDTO>> getPets(@RequestParam(defaultValue = "1") int currentPage,
                                                   @RequestParam(defaultValue = "10") int limit,
                                                   @RequestParam(defaultValue = "ASC") String sortDirection,
                                                   @RequestParam(defaultValue = "id") String sortBy,
                                                   @RequestParam(defaultValue = "") String keyword) {
        return ResponseEntity.ok(petService.getPets(keyword, currentPage, limit, sortDirection, sortBy));
    }


    @GetMapping("/api/v1/pets/{id}")
    public ResponseEntity<PetDTO> getPetById(@PathVariable long id) {
        return ResponseEntity.ok(petService.findPetById(id));
    }

    @PostMapping("/api/v1/pets")
    public ResponseEntity<PetDTO> insertPet(@Valid @RequestBody PetRequest request, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        return ResponseEntity.ok(petService.insertPet(request));
    }

    @PutMapping("/api/v1/pets/{id}")
    public ResponseEntity<PetDTO> updatePet(@PathVariable long id, @Valid @RequestBody PetRequest request, BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
        return ResponseEntity.ok(petService.updatePet(id, request));
    }

    @DeleteMapping("/api/v1/pets/{id}")
    public ResponseEntity<PetDTO> deletePetById(@PathVariable long id) {
        return ResponseEntity.ok(petService.deletePetById(id));
    }

    //PHOTOS
    @GetMapping("/api/v1/pets/{petId}/photos")
    public ResponseEntity<List<PhotoDTO>> getPhotos(@PathVariable long petId) {
        return ResponseEntity.ok(photoService.findByPet(petId));
    }

    @PostMapping("/api/v1/pets/{petId}/photos")
    public ResponseEntity<List<PhotoDTO>> insertPetPhoto(@PathVariable long petId, @ModelAttribute MultipartFile[] files) throws IOException, NoSuchAlgorithmException, ServerException, InsufficientDataException, ErrorResponseException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return ResponseEntity.ok(photoService.insert(files, petId));
    }
    @PutMapping("/api/v1/pets/{petId}/photos/{photoId}")
    public ResponseEntity<PhotoDTO> updatePrimaryPhoto(@PathVariable long petId, @PathVariable int photoId) {
        return ResponseEntity.ok(photoService.activePrimaryPhoto(petId, photoId));
    }

    @DeleteMapping("/api/v1/pets/{petId}/photos/{id}")
    public ResponseEntity<PhotoDTO> deletePhotoById(@PathVariable int id) {
        return ResponseEntity.ok(photoService.deleteById(id));
    }
    //DETAILS
    @GetMapping("/api/v1/pets/{petId}/details")
    public ResponseEntity<PetDetailDTO> getPetDetails(@PathVariable long petId) {
        return ResponseEntity.ok(petService.getPetDetails(petId));
    }

    @PostMapping("/api/v1/pets/{petId}/details")
    public ResponseEntity<PetDetailDTO> insertPetDetails(@PathVariable long petId,@Valid @RequestBody PetDetailRequest request, BindingResult bindingResult) throws BindException {
        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }
        return ResponseEntity.ok(petService.insertOrUpdatePetDetails(petId,request));
    }

    //VIEW
    @GetMapping({"/admin/pets/", "/admin/pets"})
    public String petPage(Model model) {
        model.addAttribute("title", "Quản trị: Thú cưng");
        return "pet/admin/index";
    }

    @GetMapping("/admin/pets/{id}/details")
    public String petDetails(@PathVariable int id, Model model) {
        model.addAttribute("title", "Thú cưng " + id);
        return "pet/admin/details";
    }

    @GetMapping("/admin/pets/add")
    public String add(Model model) {
        model.addAttribute("title", "Thêm thú cưng");
        return "pet/admin/form";
    }

    @GetMapping("/admin/pets/update")
    public String update(@RequestParam int id, Model model) {
        model.addAttribute("title", "Cập nhật thú cưng " + id);
        return "pet/admin/form";
    }
    @GetMapping("/admin/pet/export/pdf")
    public void exportToPDF(HttpServletResponse response) throws IOException {
//        response.setContentType("application/pdf");
//        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//        String currentDateTime = dateFormatter.format(new Date());
//
//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=pets_" + currentDateTime + ".pdf";
//        response.setHeader(headerKey, headerValue);
//p
//        List<PetDTO> pets = petService.findAll();
//        ExportFileUtils fileUtils = new ExportFileUtils(pets);
//        fileUtils.exportPFDFile(response);
    }

    @GetMapping("/admin/pet/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
//        response.setContentType("application/octet-stream");
//        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//        String currentDateTime = dateFormatter.format(new Date());
//
//        String headerKey = "Content-Disposition";
//        String headerValue = "attachment; filename=pets_" + currentDateTime + ".xlsx";
//        response.setHeader(headerKey, headerValue);
//
//        List<PetDTO> pets = petService.findAll();
//        ExportFileUtils fileUtils = new ExportFileUtils(pets);
//        fileUtils.exportExcelFile(response);
    }

    @GetMapping("/{categorySlug}")
    public String getPetsByCategory(@PathVariable String categorySlug, Model model){
        model.addAttribute("title","Danh mục thú cưng" );
        return "pet/user/index";
    }
    @GetMapping("/{categorySlug}/{petId}")
    public String petDetail(Model model, @PathVariable long petId) {
        model.addAttribute("title", "Thú cưng " +  petId);
        return "pet/user/detail";
    }
}
