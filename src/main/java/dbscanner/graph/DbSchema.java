package dbscanner.graph;

import java.util.Map;
import java.util.Set;

public class DbSchema {
    private String name;

    private Map<String, DbTable> tables;

    public DbSchema(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<String, DbTable> getTables() {
        return tables;
    }

    public void setTables(Map<String, DbTable> tables) {
        this.tables = tables;
    }

    @Override
    public String toString() {
        return "DbSchema{\n" +
                "name='" + name + '\'' +
                ",\n tables=" + tables + "\n" +
                '}';
    }
}
