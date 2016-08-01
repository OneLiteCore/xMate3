package core.base.common;

public final class KeyValue {

    private String key;
    private Object value;

    public KeyValue() {
    }

    public KeyValue(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public KeyValue setKey(String key) {
        this.key = key;
        return this;
    }

    public Object getValue() {
        return value;
    }

    public <T> T getCastValue(){
        return (T) value;
    }

    public KeyValue setValue(Object value) {
        this.value = value;
        return this;
    }
}
