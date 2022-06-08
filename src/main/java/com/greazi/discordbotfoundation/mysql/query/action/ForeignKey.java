package com.greazi.discordbotfoundation.mysql.query.action;

public class ForeignKey {

    private final String name, columnName, referenceTable, referenceColumn;

    public ForeignKey(String name, String columnName, String referenceTable, String referenceColumn){
        this.name = name;
        this.columnName = columnName;
        this.referenceTable = referenceTable;
        this.referenceColumn = referenceColumn;
    }

    public String getName() {
        return name;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getReferenceColumn() {
        return referenceColumn;
    }

    public String getReferenceTable() {
        return referenceTable;
    }

    public String toQuery(){
        return "CONSTRAINT "+name+" FOREIGN KEY ("+columnName+") REFERENCES "+referenceTable+"(" + referenceColumn + ")";
    }

}
