package com.hivetech.service.impl;

import com.hivetech.model.dto.RoleDTO;
import com.hivetech.repository.RoleRepo;
import com.hivetech.service.RoleService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepo roleRepo;
    private final ModelMapper modelMapper;

    public RoleServiceImpl(RoleRepo roleRepo, ModelMapper modelMapper) {
        this.roleRepo = roleRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<RoleDTO> findAll() {
        return roleRepo.findAll().stream().map(r -> modelMapper.map(r, RoleDTO.class)).collect(Collectors.toList());
    }
}
