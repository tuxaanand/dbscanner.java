package dbscanner;

import dbscanner.graph.TableColumn;

import java.util.Set;

public class TablePrimaryKey {
    private String name;
    private Set<TableColumn> columns;

    public TablePrimaryKey(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<TableColumn> getColumns() {
        return columns;
    }

    public void setColumns(Set<TableColumn> columns) {
        this.columns = columns;
    }

    @Override
    public String toString() {
        return "TablePrimaryKey{" +
                "name='" + name + '\'' +
                ", columns=" + columns +
                '}';
    }
}
