package com.sandeep.model;

import lombok.Data;

import java.util.List;

@Data
public class Table {
    private List<String> columnDefinitions;
    private List<List<String>> tableData;
}
