package com.lowes.dto.response.admin;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AdminPaginatedResponseDTO<T> {
    private List<T> content;

    @NonNull
    private int totalPages;

    @NonNull
    private long totalElements;

    @NonNull
    private int pageNumber;

    @NonNull
    private int pageSize;
}