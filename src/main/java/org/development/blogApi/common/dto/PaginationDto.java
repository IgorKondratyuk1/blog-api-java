package org.development.blogApi.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PaginationDto<T> {
    private long pagesCount;
    private long page;
    private long pageSize;
    private long totalCount;
    private List<T> items;
}
