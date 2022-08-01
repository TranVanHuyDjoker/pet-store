package com.hivetech.model.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PhotoRequest {
    private MultipartFile[] files;
}
