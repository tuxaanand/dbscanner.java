package dbscanner.graph;

import dbscanner.ForeignKey;
import dbscanner.ReferredColumn;
import dbscanner.TablePrimaryKey;

import java.util.Map;
import java.util.Set;

public class DbTable {
    private String name;
    private Map<String, TableColumn> columns;
    private TablePrimaryKey primaryKey;
    private Set<ForeignKey> foreignKeys;
    private Set<ReferredColumn> referredColumns;

    public DbTable(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<String, TableColumn> getColumns() {
        return columns;
    }

    public void setColumns(Map<String, TableColumn> columns) {
        this.columns = columns;
    }

    public TablePrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(TablePrimaryKey primaryKey) {
        this.primaryKey = primaryKey;
    }

    public Set<ForeignKey> getForeignKeys() {
        return foreignKeys;
    }

    public void setForeignKeys(Set<ForeignKey> foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public Set<ReferredColumn> getReferredColumns() {
        return referredColumns;
    }

    public void setReferredColumns(Set<ReferredColumn> referredColumns) {
        this.referredColumns = referredColumns;
    }

    @Override
    public String toString() {
        return "DbTable{\n" +
                "name='" + name + '\'' +
                ",\n columns=" + columns +
                ",\n primaryKey=" + primaryKey +
                ",\n foreignKeys=" + foreignKeys +
                ",\n referredColumn=" + referredColumns +
                '}' + "\n";
    }
}
