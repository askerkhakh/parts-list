package com.example.meta;

public class TableMetaDataFactory {

    public static TableMetaData partsTable() {
        // метаданные конструируем в коде, в будущем можно будет читать из какого-нибудь внешнего ресурса
        TableMetaDataImpl tableMetaData = new TableMetaDataImpl("parts");
        tableMetaData.add(new FieldMetaDataImpl("Part Name", FieldType.STRING));
        tableMetaData.add(new FieldMetaDataImpl("Part Number", FieldType.STRING));
        tableMetaData.add(new FieldMetaDataImpl("Vendor", FieldType.STRING));
        tableMetaData.add(new FieldMetaDataImpl("Qty", FieldType.INTEGER));
        tableMetaData.add(new FieldMetaDataImpl("Shipped", FieldType.DATE));
        tableMetaData.add(new FieldMetaDataImpl("Receive", FieldType.DATE));
        return tableMetaData;
    }
}
