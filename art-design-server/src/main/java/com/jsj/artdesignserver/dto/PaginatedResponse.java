package com.jsj.artdesignserver.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginatedResponse<T> {
    private List<T> records;
    private int current;
    private int size;
    private long total;
}

