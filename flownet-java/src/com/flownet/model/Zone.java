package com.flownet.model;

public class Zone {

    private String id;          // e.g. wardV
    private String name;        // e.g. Velachery Basin
    private String risk;        // safe | warn | crit
    private String waterLevel;  // e.g. 1.4 m
    private String flow;        // e.g. Normal | Elevated | Rising
    private String status;      // e.g. Normal drainage flow

    public Zone() {
    }

    public Zone(String id, String name, String risk, String waterLevel, String flow, String status) {
        this.id = id;
        this.name = name;
        this.risk = risk;
        this.waterLevel = waterLevel;
        this.flow = flow;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRisk() { return risk; }
    public void setRisk(String risk) { this.risk = risk; }

    public String getWaterLevel() { return waterLevel; }
    public void setWaterLevel(String waterLevel) { this.waterLevel = waterLevel; }

    public String getFlow() { return flow; }
    public void setFlow(String flow) { this.flow = flow; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String riskLabel() {
        switch (risk) {
            case "warn": return "Moderate";
            case "crit": return "High";
            default: return "Low";
        }
    }

    @Override
    public String toString() {
        return String.format("%-8s | %-22s | %-8s | %-7s | %-10s | %s",
            id, name, riskLabel(), waterLevel, flow, status);
    }
}
