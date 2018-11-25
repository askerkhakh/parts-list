package com.example;

import java.sql.ResultSet;
import java.sql.SQLException;

class TableMetaDataFactory {
    static TableMetaData ofColumnsResultSet(ResultSet tableColumns) throws SQLException {
        TableMetaDataImpl tableMetaData = new TableMetaDataImpl();
        while (tableColumns.next()) {
            tableMetaData.add(
                    new FieldMetaDataImpl(tableColumns.getString("COLUMN_NAME"), tableColumns.getInt("DATA_TYPE"))
            );
        }
        return tableMetaData;
    }
}
