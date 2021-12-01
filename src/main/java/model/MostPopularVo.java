package model;

public class MostPopularVo {
    private String rowkey;
    private String family;
    private String column;
    private String value;

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "MostPopularVo{" +
                "rowkey='" + rowkey + '\'' +
                ", family='" + family + '\'' +
                ", column='" + column + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
