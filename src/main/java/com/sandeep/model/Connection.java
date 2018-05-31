package com.sandeep.model;

import com.datastax.driver.core.KeyspaceMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Connection {
    private String name;
    private String host;
    private String port;

    private List<KeyspaceMetadata> keyspaces;

    public String getName() {
        return this.name;
    }

    public List<KeyspaceMetadata> getKeyspaces() {
        return this.keyspaces;
    }

    public void setKeyspaces(List<KeyspaceMetadata> keyspaces) {
        this.keyspaces = keyspaces;
    }

    public Connection(String name) {
        this.name = name;
    }

    public Connection(String name, String host) {
        this(name);
        this.host = host;
    }
}
