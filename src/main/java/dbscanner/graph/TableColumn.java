package dbscanner.graph;

public class TableColumn {
    private String name;
    private String type;
    private int size;
    private int decimalSize;
    private boolean nullable;

    public TableColumn(String name, String type, int size, int decimalSize, boolean nullable) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.decimalSize = decimalSize;
        this.nullable = nullable;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    public int getDecimalSize() {
        return decimalSize;
    }

    public boolean isNullable() {
        return nullable;
    }


    @Override
    public String toString() {
        return "TableColumn{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", size=" + size +
                ", decimalSize=" + decimalSize +
                ", nullable=" + nullable +
                '}' + "\n";
    }
}
