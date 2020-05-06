package io.specialrooter.context.model;

public class SimpleDictItemModel {
    private String label;
    private Object value;

    public SimpleDictItemModel() {
    }

    public SimpleDictItemModel(String label, Object value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
