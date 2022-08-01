package com.hivetech.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PetDetailDTO implements Serializable {
    private long id;
    private String origin;
    private int age;
    private String dadType;
    private String momType;
    private long purebred;
    private String vaccination;
}
