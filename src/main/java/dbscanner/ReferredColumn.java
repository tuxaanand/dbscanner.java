package dbscanner;

import dbscanner.graph.TableColumn;

public class ReferredColumn {
    private TableColumn column;
    private String referredByTable;
    private String referredByColumn;

    public ReferredColumn(TableColumn column, String referredByTable, String referredByColumn) {
        this.column = column;
        this.referredByTable = referredByTable;
        this.referredByColumn = referredByColumn;
    }

    public TableColumn getColumn() {
        return column;
    }

    public String getReferredByTable() {
        return referredByTable;
    }

    public String getReferredByColumn() {
        return referredByColumn;
    }

    @Override
    public String toString() {
        return "ReferredColumn{" +
                "column=" + column +
                ", referredByTable='" + referredByTable + '\'' +
                ", referredByColumn='" + referredByColumn + '\'' +
                '}' + "\n";
    }
}
