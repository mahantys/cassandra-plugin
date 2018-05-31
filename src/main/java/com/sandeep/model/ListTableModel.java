package com.sandeep.model;

import lombok.Data;

import javax.swing.event.TableModelListener;

import javax.swing.table.TableModel;
import java.util.List;

@Data
public class ListTableModel implements TableModel {

    private List<String> columnDefinitions;
    private List<List<String>> rowData;
    private int rowCount;
    private int columnCount;

    @Override
    public String getColumnName(int columnIndex) {
        return columnDefinitions.get(columnIndex);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        Class returnValue;
        if ((columnIndex >= 0) && (columnIndex < getColumnCount()) && getRowCount() > 0) {
            returnValue = getValueAt(0, columnIndex).getClass();
        } else {
            returnValue = Object.class;
        }
        return returnValue;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < rowCount && columnIndex < columnCount) {
            return rowData.get(rowIndex).get(columnIndex);
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    @Override
    public void addTableModelListener(TableModelListener l) {

    }

    @Override
    public void removeTableModelListener(TableModelListener l) {

    }

    public ListTableModel(List<List<String>> rowData, List<String> columnDefinitions) {
        this.rowData = rowData;
        this.columnDefinitions = columnDefinitions;
        this.rowCount = rowData.size();
        this.columnCount = columnDefinitions.size();
    }
}
