package com.sandeep.service;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.sandeep.database.plugin.model.ConnectionList;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

@State(name = "cassandraPlugin", storages = @Storage("plugin_config.xml"))
@Slf4j
public class PluginDataStoreService implements PersistentStateComponent<PluginDataStoreService> {
    private List<String> connectionDetails;

    public List<String> getConnectionDetails() {
        return connectionDetails;
    }

    public void setConnectionDetails(List<String> connectionDetails) {
        this.connectionDetails = connectionDetails;
    }

    @Nullable
    @Override
    public PluginDataStoreService getState() {
        log.info("Get State Called");
        return this;
    }

    @Override
    public void loadState(PluginDataStoreService state) {
        log.info("Load State Called");
        XmlSerializerUtil.copyBean(state, this);
    }

    @Nullable
    public static PluginDataStoreService getInstance(Project project) {
        return ServiceManager.getService(project, PluginDataStoreService.class);
    }

}
