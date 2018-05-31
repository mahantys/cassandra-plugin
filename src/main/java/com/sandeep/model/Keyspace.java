package com.sandeep.model;

import lombok.Data;

import java.util.List;

@Data
public class Keyspace {
    private String name;
    private List<Table> tables;
}
