package com.hivetech.controller;

import com.hivetech.config.exception.BadRequestException;
import com.hivetech.model.dto.PromotionCodeDTO;
import com.hivetech.model.request.ApplyPromotionCodeRequest;
import com.hivetech.model.request.PromotionCodeRequest;
import com.hivetech.service.PromotionCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PromotionCodeController {
    private final PromotionCodeService promotionCodeService;

    //API
    @GetMapping("/api/v1/promotion-codes")
    public ResponseEntity<List<PromotionCodeDTO>> getPromotionCodes() {
        return ResponseEntity.ok(promotionCodeService.getPromotionCode());
    }

    @PostMapping("/api/v1/promotion-codes")
    public ResponseEntity<PromotionCodeDTO> addPromotionCode(@Valid @RequestBody PromotionCodeRequest request, BindingResult bindingResult) throws BindException {
        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }
        return ResponseEntity.ok(promotionCodeService.addPromotionCode(request));
    }

    @PostMapping("/api/v1/promotion-codes/apply")
    public ResponseEntity<PromotionCodeDTO> applyPromotionCode(@Valid @RequestBody ApplyPromotionCodeRequest request,
                                                               BindingResult bindingResult) throws BindException {
        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(ObjectUtils.isEmpty(authentication) && authentication instanceof AnonymousAuthenticationToken){
            throw new BadRequestException("Người dùng phải đăng nhập để sử dụng chức năng này!");
        }
        return ResponseEntity.ok(promotionCodeService.applyPromotionCode(authentication,request));
    }

    @PutMapping("/api/v1/promotion-codes/{id}")
    public ResponseEntity<PromotionCodeDTO> updatePromotionCode(@PathVariable long id,
                                                                @Valid @RequestBody PromotionCodeRequest request,
                                                                BindingResult bindingResult) throws BindException {
        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }
        return ResponseEntity.ok(promotionCodeService.updatePromotionCode(id, request));
    }

    @DeleteMapping("/api/v1/promotion-codes/{id}")
    public ResponseEntity<PromotionCodeDTO> updatePromotionCode(@PathVariable long id) {
        return ResponseEntity.ok(promotionCodeService.deletePromotionCodeById(id));
    }

    @GetMapping("/api/v1/promotion-codes/")
    public ResponseEntity<PromotionCodeDTO> getPromotionCodeByCode(@PathVariable String code) {
        return ResponseEntity.ok(promotionCodeService.getPromotionCodeByCode(code));
    }

    @GetMapping("/api/v1/promotion-codes/{id}")
    public ResponseEntity<PromotionCodeDTO> getPromotionCodeById(@PathVariable long id) {
        return ResponseEntity.ok(promotionCodeService.getPromotionCodeById(id));
    }

    //VIEW
    @GetMapping("/admin/promotion-codes")
    public String getPromotionCodesPage(Model model) {
        model.addAttribute("title", "Quản trị: Mã giảm giá");
        return "/code/index";
    }

    @GetMapping("/admin/promotion-codes/add")
    public String getPromotionCodesFormPage(Model model) {
        model.addAttribute("title", "Quản trị: Thêm mã giảm giá");
        return "/code/form";
    }

    @GetMapping("/admin/promotion-codes/update")
    public String getPromotionCodesFormPage(@RequestParam long id, Model model) {
        model.addAttribute("title", "Quản trị: Cập nhật mã giảm giá");
        return "/code/form";
    }
}
