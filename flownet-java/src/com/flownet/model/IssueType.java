package com.flownet.model;

public class IssueType {

    private String id;     // e.g. waterlogging
    private String label;  // e.g. Waterlogged road

    public IssueType() {
    }

    public IssueType(String id, String label) {
        this.id = id;
        this.label = label;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    @Override
    public String toString() {
        return id + " - " + label;
    }
}
