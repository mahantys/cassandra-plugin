package com.sandeep.database;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.sandeep.model.Keyspace;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DBUtil {
    public static boolean testConnection(String hostPort, String schema) {
        String query = "select release_version from system.local";

        Cluster cluster = null;
        try {
            cluster = Cluster.builder()
                    .addContactPoint(hostPort)
                    .build();
            Session session = cluster.connect();
            ResultSet rs = session.execute(query);
            Row row = rs.one();
            log.info(row.getString("release_version"));
            return true;
        } catch (Exception ex) {
            log.error("Failed to connect to " + hostPort + " due to " + ex.getMessage());
            return false;
        } finally {
            if (cluster != null) {
                cluster.close();
            }
        }
    }

    public static List<KeyspaceMetadata> getKeyspaces(String hostPort) throws Exception {
        Cluster cluster = null;
        try {
            cluster = Cluster.builder()
                    .addContactPoint(hostPort)
                    .build();
            cluster.connect();
            log.info("COnfig: " + cluster.getMetadata());
            List<KeyspaceMetadata> keyspaceNames = cluster.getMetadata().getKeyspaces();
            log.info("Discovered keyspaces " + keyspaceNames);
            return keyspaceNames;
        } finally {
            if (cluster != null) {
                cluster.close();
            }
        }
    }

    public static ResultSet executeQuery(String hostPort, String query) {
        Cluster cluster = null;
        try {
            cluster = Cluster.builder()
                    .addContactPoint(hostPort)
                    .build();
            Session session = cluster.connect();
            log.info("COnfig: " + cluster.getConfiguration());
            ResultSet rs = session.execute(query);
            return rs;
        } catch (Exception ex) {
            log.error("Failed to connect to " + hostPort + " due to " + ex.getMessage());
        } finally {
            if (cluster != null) {
                cluster.close();
            }
        }
        return null;
    }
}
