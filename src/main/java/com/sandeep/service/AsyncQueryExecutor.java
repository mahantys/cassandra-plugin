package com.sandeep.service;

import com.datastax.driver.core.ColumnDefinitions;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.sandeep.database.DBUtil;
import com.sandeep.model.Table;

import java.awt.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

public class AsyncQueryExecutor implements Callable {
    private String query;
    private String host;

    @Override
    public Table call() throws Exception {
        Table table = new Table();
        ResultSet rs = DBUtil.executeQuery(host, query);
        // print(rs);
        List<String> columnDefinitions = rs.getColumnDefinitions()
                .asList()
                .stream()
                .map(definition -> definition.getName())
                .collect(Collectors.toList());

        table.setColumnDefinitions(columnDefinitions);

        List<List<String>> tableData = new ArrayList<>();
        rs.iterator().forEachRemaining(row -> {
            List<String> rowList = new ArrayList<>();
            for (int colIndex = 0; colIndex < columnDefinitions.size(); colIndex++) {
                DataType dt = row.getColumnDefinitions().getType(colIndex);
                rowList.add(stringValueOf(dt, row, colIndex));
            }
            tableData.add(rowList);
        });
        table.setTableData(tableData);
        return table;
    }

    private String stringValueOf(DataType dataType, Row row, int colIndex) {

        if (dataType.equals(DataType.text()) || dataType.equals(DataType.varchar())) {
            return String.valueOf(row.getString(colIndex));
        } else if (dataType.equals(DataType.timestamp())) {
            return String.valueOf(row.getTimestamp(colIndex));
        } else if (dataType.equals(DataType.cint()) || dataType.equals(DataType.smallint()) || dataType.equals(DataType.tinyint()) || dataType.equals(DataType.varint())) {
            return String.valueOf(row.getLong(colIndex));
        } else if (dataType.equals(DataType.cfloat()) || dataType.equals(DataType.cdouble()) || dataType.equals(DataType.decimal())) {
            return String.valueOf(row.getFloat(colIndex));
        } else if (dataType.equals(DataType.time())) {
            return String.valueOf(row.getTime(colIndex));
        } else if (dataType.equals(DataType.date())) {
            return String.valueOf(row.getDate(colIndex));
        } else if (dataType.equals(DataType.cboolean())) {
            return String.valueOf(row.getBool(colIndex));
        } else if (dataType.equals(DataType.timeuuid()) || dataType.equals(DataType.uuid())) {
            return String.valueOf(row.getUUID(colIndex));
        } else if (dataType.equals(DataType.counter()) || dataType.equals(DataType.duration())) {
            return String.valueOf(row.getLong(colIndex));
        }
        //TODO: Collections and dont return empty string
        return "";
    }


    public AsyncQueryExecutor(String host, String query) {
        this.host = host;
        this.query = query;
    }
}
