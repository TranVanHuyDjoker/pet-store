package com.hivetech.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PhotoDTO {
    private int id;
    private String photoUrl;
    private String photoPath;
    private boolean primaryPhoto;
    private long petId;
}
