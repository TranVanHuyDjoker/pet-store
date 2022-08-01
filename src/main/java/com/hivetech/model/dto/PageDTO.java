package com.hivetech.model.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Data
public class PageDTO<T> {

    private List<T> listDTO;
    private int limit;
    private int currentPage;
    private long totalPages;

    public PageDTO(List<T> listDTO, int limit, int currentPage, int totalPages) {
        this.listDTO = listDTO;
        this.limit = limit;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }
}
