package dbscanner;

import dbscanner.graph.DbSchema;
import dbscanner.graph.DbTable;
import dbscanner.graph.TableColumn;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Scanner {
    private Connection connection;

    public Scanner(Connection connection) {
        this.connection = connection;
    }

    public Set<DbSchema> getAllDbSchema() throws SQLException {
        final DatabaseMetaData metaData = this.connection.getMetaData();
        final ResultSet resultSet = metaData.getSchemas();
        Set<DbSchema> schemas = new HashSet<>();
        while (resultSet.next()) {
            final String schemaName = resultSet.getString("TABLE_SCHEM");
            final DbSchema dbSchema = new DbSchema(schemaName);
            schemas.add(dbSchema);
            final Map<String, DbTable> allTables = getAllTables(schemaName);
            dbSchema.setTables(allTables);
        }
        resultSet.close();
        return schemas;

    }

    public DbSchema getDbSchema(String schemaName) throws SQLException {
        final DatabaseMetaData metaData = this.connection.getMetaData();
        final ResultSet resultSet = metaData.getSchemas(null, schemaName);
        if (resultSet.next()) {
            System.out.printf("Processing schema - %s\n", schemaName);
            final DbSchema dbSchema = new DbSchema(schemaName);
            final Map<String, DbTable> allTables = getAllTables(schemaName);
            dbSchema.setTables(allTables);
            return dbSchema;
        }
        resultSet.close();
        return null;

    }

    private Map<String, DbTable> getAllTables(String schemaName) throws SQLException {
        final DatabaseMetaData metaData = this.connection.getMetaData();
        final ResultSet tablesRs
                = metaData.getTables(null, schemaName, null, new String[]{"TABLE"});
        Map<String, DbTable> dbTables = new HashMap<>();
        while (tablesRs.next()) {
            final String tableName = tablesRs.getString("TABLE_NAME");
            System.out.printf("\tProcessing table - %s\n", tableName);
            final DbTable dbTable = new DbTable(tableName);
            dbTables.put(tableName, dbTable);
            Map<String, TableColumn> tableColumns = getAllColumns(schemaName, tableName);
            TablePrimaryKey primaryKey = getPrimaryKey(schemaName, tableName, tableColumns);
            Set<ForeignKey> foreignKeys = getForeignKeys(schemaName, tableName, tableColumns);
            Set<ReferredColumn> referredColumns = getReferredColumns(schemaName, tableName, tableColumns);
            dbTable.setColumns(tableColumns);
            dbTable.setPrimaryKey(primaryKey);
            dbTable.setForeignKeys(foreignKeys);
            dbTable.setReferredColumns(referredColumns);
        }
        tablesRs.close();
        return dbTables;
    }

    private Set<ReferredColumn> getReferredColumns(String schemaName, String tableName, Map<String, TableColumn> tableColumns) throws SQLException {
        final DatabaseMetaData metaData = this.connection.getMetaData();
        final ResultSet exportedKeysRs = metaData.getExportedKeys(null, schemaName, tableName);
        Set<ReferredColumn> referredColumns = new HashSet<>();
        while (exportedKeysRs.next()) {
            final String pkColumnName = exportedKeysRs.getString("PKCOLUMN_NAME");
            final String fkTableName = exportedKeysRs.getString("FKTABLE_NAME");
            final String fkColumnName = exportedKeysRs.getString("FKCOLUMN_NAME");
            ReferredColumn referredColumn = new ReferredColumn(tableColumns.get(pkColumnName), fkTableName, fkColumnName);
            referredColumns.add(referredColumn);
        }
        exportedKeysRs.close();
        return referredColumns;
    }

    private Set<ForeignKey> getForeignKeys(String schemaName, String tableName, Map<String, TableColumn> tableColumns) throws SQLException {
        final DatabaseMetaData metaData = this.connection.getMetaData();
        final ResultSet foreignKeysRs = metaData.getImportedKeys(null, schemaName, tableName);
        Set<ForeignKey> foreignKeys = new HashSet<>();
        while (foreignKeysRs.next()) {
            final String fkName = foreignKeysRs.getString("FK_NAME");
            final String fkColumnName = foreignKeysRs.getString("FKCOLUMN_NAME");
            final String pkTableName = foreignKeysRs.getString("PKTABLE_NAME");
            final String pkColumnName = foreignKeysRs.getString("PKCOLUMN_NAME");
            ForeignKey foreignKey = new ForeignKey(fkName, tableColumns.get(fkColumnName), pkTableName, pkColumnName);
            foreignKeys.add(foreignKey);
        }
        foreignKeysRs.close();
        return foreignKeys;
    }

    private TablePrimaryKey getPrimaryKey(String schemaName, String tableName, Map<String, TableColumn> tableColumns) throws SQLException {
        final DatabaseMetaData metaData = this.connection.getMetaData();
        final ResultSet primaryKeysRs = metaData.getPrimaryKeys(null, schemaName, tableName);
        String pkName = null;
        Set<TableColumn> keyColumns = new HashSet<>();
        while(primaryKeysRs.next()) {
            if(pkName == null) {
                pkName = primaryKeysRs.getString("PK_NAME");
            }
            keyColumns.add(tableColumns.get(primaryKeysRs.getString("COLUMN_NAME")));
        }
        primaryKeysRs.close();

        if(pkName != null) {
            TablePrimaryKey key = new TablePrimaryKey(pkName);
            key.setColumns(keyColumns);
            return key;
        }
        return null;
    }

    private Map<String, TableColumn> getAllColumns(String schemaName, String tableName) throws SQLException {
        final DatabaseMetaData metaData = this.connection.getMetaData();
        final ResultSet columnsRs = metaData.getColumns(null, schemaName, tableName, null);
        Map<String, TableColumn> tableColumns = new HashMap<>();
        while (columnsRs.next()) {
            //COLUMN_NAME, TYPE_NAME, COLUMN_SIZE, DECIMAL_DIGITS, NULLABLE,
            final String columnName = columnsRs.getString("COLUMN_NAME");
            TableColumn tableColumn = new TableColumn(columnName,
                    columnsRs.getString("TYPE_NAME"),
                    columnsRs.getInt("COLUMN_SIZE"),
                    columnsRs.getInt("DECIMAL_DIGITS"),
                    columnsRs.getBoolean("NULLABLE"));
            tableColumns.put(columnName, tableColumn);
        }
        columnsRs.close();
        return tableColumns;
    }


}
