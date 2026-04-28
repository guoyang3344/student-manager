package com.example.studentmanager.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long total;
    private List<T> records;
    private Long current;
    private Long size;
    private Long pages;

    public PageResult() {
    }

    public PageResult(Long total, List<T> records, Long current, Long size, Long pages) {
        this.total = total;
        this.records = records;
        this.current = current;
        this.size = size;
        this.pages = pages;
    }
}
