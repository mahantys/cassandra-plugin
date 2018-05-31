package com.sandeep.service;

import com.intellij.openapi.util.IconLoader;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.net.URL;
@Slf4j
public class LoadingService implements Runnable{

    private Container parent;
    private JComponent child;

    private boolean stopLoading = false;

    public boolean isStopLoading() {
        return stopLoading;
    }

    public void setStopLoading(boolean stopLoading) {
        this.stopLoading = stopLoading;
    }


    @Override
    public void run() {
        showLoading();
        while(!isStopLoading()) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        hideLoading();
    }

    public LoadingService(Container parent, JComponent child){
        this.parent = parent;
        this.child = child;
    }

    private Container getLoading() {
        URL myImgIconUrl = getClass().getClassLoader().getResource("icons/loading.gif");
        Icon myImgIcon = new ImageIcon(myImgIconUrl);
        JLabel imageLbl = new JLabel(myImgIcon);

        JPanel panel = new JPanel();
        panel.setBorder(new EtchedBorder());

        JLabel label = new JLabel("Hello World!");
        label.setFont(new Font("Verdana", Font.BOLD, 14));
        panel.add(imageLbl, BorderLayout.CENTER);

        return panel;
    }

    private void showLoading() {
        log.info("Showing loading");
        parent.removeAll();
        parent.add(getLoading());
    }

    private void hideLoading() {
        log.info("Stopped loading");
        parent.removeAll();
        parent.add(child);
    }
}

