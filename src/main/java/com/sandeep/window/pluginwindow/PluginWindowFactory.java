package com.sandeep.window.pluginwindow;

import com.datastax.driver.core.KeyspaceMetadata;
import com.datastax.driver.core.TableMetadata;

import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.ColoredTreeCellRenderer;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import com.intellij.ui.treeStructure.Tree;
import com.sandeep.database.DBUtil;

import com.sandeep.model.Connection;
import com.sandeep.model.ListTableModel;
import com.sandeep.model.Table;
import com.sandeep.service.AsyncQueryExecutor;
import com.sandeep.service.LoadingService;
import com.sandeep.service.PluginDataStoreService;
import com.sandeep.window.pluginwindow.panels.newpanel.AddNewConnectionPanel;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Slf4j
public class PluginWindowFactory implements ToolWindowFactory {
    private JPanel myToolWindowContent;
    private JLabel messageLabel;
    private Tree connectionTree;
    private JTable resultsTable;
    private JTextField filterText;
    private JButton button1;
    private String openingMessage;
    private ArrayList<Connection> connectionList;
    //public static final int DEPTH = 4;
    private Icon STRUCTURE_TOOL_WINDOW = IconLoader.getIcon("/icons/persistenceMappedSuperclass.png");
    private Icon LEAF_TOOL_WINDOW = IconLoader.getIcon("/icons/persistenceUnit.png");

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        // myToolWindow = toolWindow;
        connectionList = new ArrayList<>();
        Container tableParent = resultsTable.getParent();
        PluginDataStoreService service = PluginDataStoreService.getInstance(project);
        log.info("Loaded connections: " + service.getConnectionDetails());
        if (service.getConnectionDetails() == null) {
            AddNewConnectionPanel cpnl = new AddNewConnectionPanel(myToolWindowContent);
            cpnl.setProject(project);
            cpnl.show();
        }

        List<String> connectionDetails = service.getConnectionDetails();

        for (String connectionDetail : connectionDetails) {
            String[] tokens = connectionDetail.split(";");
            Connection connection = new Connection(tokens[0], tokens[1]);
            // TODO: make this parallel
            connection.setKeyspaces(DBUtil.getKeyspaces(connection.getHost()));
            connectionList.add(connection);
        }

        this.openingMessage = "Lets see where it  goes";

        messageLabel.setText(this.openingMessage);
        connectionTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                //LoadingService.startLoading();

                TreePath tp = connectionTree.getPathForLocation(me.getX(), me.getY());
                if (tp != null && tp.getPathCount() == 4) {
                    LoadingService loadingService = new LoadingService(tableParent, resultsTable);
                    new Thread(loadingService).start();

                    String host = String.valueOf("127.0.0.1");
                    String schema = String.valueOf(tp.getPath()[2]);
                    String table = String.valueOf(tp.getPath()[3]);
                    ExecutorService service = Executors.newCachedThreadPool();
                    Future<Table> tableFuture = service.submit(new AsyncQueryExecutor(host, "select * from " + schema + "." + table));

                    try {
                        Table tableData = tableFuture.get();
                        log.info("Table:" + tableData);
                        TableModel dtm = new ListTableModel(tableData.getTableData(), tableData.getColumnDefinitions());
                        final TableRowSorter<TableModel> sorter = new TableRowSorter<>(dtm);
                        resultsTable.setModel(dtm);
                        resultsTable.createDefaultColumnsFromModel();
                        resultsTable.setEnabled(true);
                        resultsTable.setRowSorter(sorter);

                        filterText.addKeyListener(new KeyListener() {
                            @Override
                            public void keyTyped(KeyEvent e) {
                            }

                            @Override
                            public void keyPressed(KeyEvent e) {

                            }

                            @Override
                            public void keyReleased(KeyEvent e) {
                                String text = filterText.getText();
                                if (text.length() == 0) {
                                    sorter.setRowFilter(null);
                                } else {
                                    sorter.setRowFilter(RowFilter.regexFilter(text));
                                }
                            }
                        });
                        loadingService.setStopLoading(true);
                        //  LoadingService.stopLoading();
                        //Messages.showMessageDialog(project, "You clicked : " + schema + "  " + table, "Click Event", Messages.getInformationIcon());

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        buildTree(connectionList);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(myToolWindowContent, "", false);
        toolWindow.getContentManager().addContent(content);
    }

    public void buildTree(List<Connection> connectionList) {
        connectionTree.setBackground(Color.WHITE);

        connectionTree.setCellRenderer(new ColoredTreeCellRenderer() {
            @Override
            public void customizeCellRenderer(@NotNull JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

                SimpleTextAttributes textattributes = new SimpleTextAttributes(Color.WHITE, Color.BLACK, Color.PINK, SimpleTextAttributes.STYLE_PLAIN);
                if (value instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                    if (node.isLeaf()) {
                        setIcon(LEAF_TOOL_WINDOW);
                    } else {
                        setIcon(STRUCTURE_TOOL_WINDOW);
                    }
                    String text = (String) node.getUserObject();
                    append(text, textattributes);
                }

            }
        });
        DefaultTreeModel model = (DefaultTreeModel) connectionTree.getModel();
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Connections");
        model.setRoot(root);

        for (Connection connection : connectionList) {
            DefaultMutableTreeNode conn = new DefaultMutableTreeNode(connection.getName());
            List<KeyspaceMetadata> keyspaces = connection.getKeyspaces();

            for (KeyspaceMetadata keyspaceMetadata : keyspaces) {
                DefaultMutableTreeNode keyspaceNode = new DefaultMutableTreeNode(keyspaceMetadata.getName());
                List<TableMetadata> tablesMetadata = keyspaceMetadata.getTables().stream().collect(Collectors.toList());

                for (TableMetadata tableMetadata : tablesMetadata) {
                    DefaultMutableTreeNode tableNode = new DefaultMutableTreeNode(tableMetadata.getName());
                    keyspaceNode.add(tableNode);
                }
                conn.add(keyspaceNode);
            }
            model.insertNodeInto(conn, root, root.getChildCount());
        }
    }

}
