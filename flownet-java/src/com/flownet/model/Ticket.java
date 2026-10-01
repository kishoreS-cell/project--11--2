package com.flownet.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Ticket {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    private String id;               // e.g. FN-2026-0001
    private String issueTypeId;      // e.g. waterlogging
    private String issueLabel;       // e.g. Waterlogged road
    private String zoneId;           // e.g. wardV
    private String zoneName;         // e.g. Velachery Basin
    private String landmark;
    private String description;
    private String severity;         // notice | worse | urgent
    private String priority;         // low | med | high
    private String team;
    private String status;           // Submitted | Acknowledged | Assigned to team | Resolved
    private int stage;               // 0..3
    private String source;           // Citizen report | AI Inspection
    private String reporterName;
    private String reporterContact;
    private LocalDateTime submittedAt;

    public Ticket() {
    }

    public Ticket(String id, String issueTypeId, String issueLabel, String zoneId, String zoneName,
                  String landmark, String description, String severity, String priority, String team,
                  String status, int stage, String source, String reporterName, String reporterContact,
                  LocalDateTime submittedAt) {
        this.id = id;
        this.issueTypeId = issueTypeId;
        this.issueLabel = issueLabel;
        this.zoneId = zoneId;
        this.zoneName = zoneName;
        this.landmark = landmark;
        this.description = description;
        this.severity = severity;
        this.priority = priority;
        this.team = team;
        this.status = status;
        this.stage = stage;
        this.source = source;
        this.reporterName = reporterName;
        this.reporterContact = reporterContact;
        this.submittedAt = submittedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getIssueTypeId() { return issueTypeId; }
    public void setIssueTypeId(String issueTypeId) { this.issueTypeId = issueTypeId; }

    public String getIssueLabel() { return issueLabel; }
    public void setIssueLabel(String issueLabel) { this.issueLabel = issueLabel; }

    public String getZoneId() { return zoneId; }
    public void setZoneId(String zoneId) { this.zoneId = zoneId; }

    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }

    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public String getTeam() { return team; }
    public void setTeam(String team) { this.team = team; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getStage() { return stage; }
    public void setStage(int stage) { this.stage = stage; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }

    public String getReporterContact() { return reporterContact; }
    public void setReporterContact(String reporterContact) { this.reporterContact = reporterContact; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    @Override
    public String toString() {
        return String.format(
            "%-14s | %-24s | %-22s | %-6s | %-20s | %-10s | %s",
            id, issueLabel, zoneName, priority, status, team,
            submittedAt == null ? "-" : submittedAt.format(FMT)
        );
    }
}
