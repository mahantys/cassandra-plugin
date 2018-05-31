package com.sandeep.window.pluginwindow.panels.newpanel;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.sandeep.database.DBUtil;
import com.sandeep.service.PluginDataStoreService;
import com.sandeep.util.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import java.awt.Component;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class AddNewConnectionPanel extends DialogWrapper {

    private JPanel rootPanel;
    private JTextField nameTextField;
    private JTextField hostTextField;
    private JButton testButton;
    private boolean connectionSuccessful = false;
    private Project project;

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        testButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                connectionSuccessful = isConnectionSuccessful();
                if (connectionSuccessful) {
                    Messages.showMessageDialog("Connection successful!", "Test connection", Messages.getInformationIcon());
                } else {
                    Messages.showMessageDialog("Failed to connect", "Test connection", Messages.getInformationIcon());
                }
            }
        });

        return rootPanel;
    }

    // TODO: Add validation
    @Override
    protected void doOKAction() {
        String connectionHost = hostTextField.getText();
        String connectionName = StringUtils.isEmpty(nameTextField.getText()) ? connectionHost : nameTextField.getText();

        if (connectionSuccessful) {
            PluginDataStoreService service = PluginDataStoreService.getInstance(project);

            List<String> existingConnections = service.getConnectionDetails();
            if (existingConnections == null) {
                existingConnections = new ArrayList<>();
            }
            String connection = StringUtils.concat(connectionName, connectionHost);

            if (existingConnections.contains(connectionName)) {
                Messages.showMessageDialog("Connection already exists! Please use different connection name", "Test connection", Messages.getInformationIcon());
            }

            existingConnections.add(connection);
            service.setConnectionDetails(existingConnections);
            Messages.showMessageDialog("Connection added successfully!", "Test connection", Messages.getInformationIcon());
        }

        close(DialogWrapper.OK_EXIT_CODE);
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public AddNewConnectionPanel(Component parent) {
        super(parent, false);
        init();
        setTitle("Add Connection");
    }

    //TODO: progress bar
    private boolean isConnectionSuccessful() {
        return DBUtil.testConnection(hostTextField.getText(), nameTextField.getText());
    }

}
