package model;

/**
 * @author 郑欣然
 */
public class MostPopularVo {
    private String key;
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
