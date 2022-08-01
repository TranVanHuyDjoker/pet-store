package com.hivetech.service.impl;

import com.hivetech.model.dto.DeliveryDTO;
import com.hivetech.repository.DeliveryRepo;
import com.hivetech.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {
    private final DeliveryRepo deliveryRepo;
    private final ModelMapper modelMapper;

    @Override
    public List<DeliveryDTO> getDeliveries() {
        return deliveryRepo.findAll().stream()
                .map(delivery -> modelMapper.map(delivery, DeliveryDTO.class))
                .collect(Collectors.toList());
    }
}
