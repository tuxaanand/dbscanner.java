package dbscanner;

import dbscanner.graph.TableColumn;

public class ForeignKey {
    private String name;
    private TableColumn column;
    private String referredTable;
    private String referredColumn;

    public ForeignKey(String name, TableColumn column, String referredTable, String referredColumn) {
        this.name = name;
        this.column = column;
        this.referredTable = referredTable;
        this.referredColumn = referredColumn;
    }

    public TableColumn getColumn() {
        return column;
    }

    public String getReferredTable() {
        return referredTable;
    }

    public String getReferredColumn() {
        return referredColumn;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ForeignKey{" +
                "name='" + name + '\'' +
                ", column=" + column +
                ", referredTable='" + referredTable + '\'' +
                ", referredColumn='" + referredColumn + '\'' +
                '}';
    }
}
