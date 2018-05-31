package com.sandeep.database.plugin.model;

import com.sandeep.model.Connection;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ConnectionList {
    private Map<String, Connection> connectionDetails = new HashMap<>();
}
