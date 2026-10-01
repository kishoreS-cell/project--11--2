package com.flownet.service;

import com.flownet.model.IssueType;
import com.flownet.model.Ticket;
import com.flownet.model.Zone;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * In-memory version of the FlowNet ticket/zone logic (no database).
 * Data resets every time the program restarts, same as the static
 * HTML demo it is based on.
 */
public class TicketService {

    private static final String[] STAGES = {
        "Submitted", "Acknowledged", "Assigned to team", "Resolved"
    };

    private final List<Zone> zones = new ArrayList<>();
    private final List<IssueType> issueTypes = new ArrayList<>();
    private final List<Ticket> tickets = new ArrayList<>();
    private int ticketSeq = 2; // two tickets already seeded below

    public TicketService() {
        seedZones();
        seedIssueTypes();
        seedTickets();
    }

    private void seedZones() {
        zones.add(new Zone("wardT", "T Nagar Outfall", "safe", "0.6 m", "Normal", "Normal drainage flow"));
        zones.add(new Zone("wardN", "Nungambakkam Channel", "safe", "0.8 m", "Normal", "Normal drainage flow"));
        zones.add(new Zone("wardA", "Adyar Confluence", "warn", "1.1 m", "Elevated", "Water levels rising - monitor"));
        zones.add(new Zone("wardV", "Velachery Basin", "safe", "1.4 m", "Rising", "Normal drainage flow"));
        zones.add(new Zone("wardP", "Perungudi Lowland", "safe", "0.7 m", "Normal", "Normal drainage flow"));
    }

    private void seedIssueTypes() {
        issueTypes.add(new IssueType("waterlogging", "Waterlogged road"));
        issueTypes.add(new IssueType("blocked", "Blocked / overflowing drain"));
        issueTypes.add(new IssueType("manhole", "Damaged manhole cover"));
        issueTypes.add(new IssueType("sewage", "Sewage overflow"));
        issueTypes.add(new IssueType("other", "Something else"));
    }

    private void seedTickets() {
        tickets.add(new Ticket(
            "FN-2026-0001", "waterlogging", "Waterlogged road", "wardV", "Velachery Basin",
            "Near Velachery bus depot", "Ankle-deep water near the bus depot.", "worse", "med",
            "Ward 12 Field Crew", "Assigned to team", 2, "Citizen report", null, null,
            LocalDateTime.now().minusHours(6)
        ));
        tickets.add(new Ticket(
            "FN-2026-0002", "blocked", "Blocked drain", "wardT", "T Nagar Outfall",
            "2nd Street outfall", "Drain was overflowing, now cleared.", "notice", "low",
            "Ward 7 Sanitation", "Resolved", 3, "Citizen report", null, null,
            LocalDateTime.now().minusDays(1)
        ));
    }

    // ---------- Zones / city status ----------

    public List<Zone> listZones() {
        return Collections.unmodifiableList(zones);
    }

    public Optional<Zone> findZone(String zoneId) {
        return zones.stream().filter(z -> z.getId().equals(zoneId)).findFirst();
    }

    public String cityWideRisk() {
        boolean anyCrit = zones.stream().anyMatch(z -> "crit".equals(z.getRisk()));
        boolean anyWarn = zones.stream().anyMatch(z -> "warn".equals(z.getRisk()));
        if (anyCrit) return "crit";
        if (anyWarn) return "warn";
        return "safe";
    }

    // ---------- Issue types ----------

    public List<IssueType> listIssueTypes() {
        return Collections.unmodifiableList(issueTypes);
    }

    // ---------- Ticket wizard (report an issue) ----------

    /**
     * Mirrors submitTicket() in flownet-app.html: builds a new ticket ID,
     * derives priority from severity, and assigns a team from the zone name.
     */
    public Ticket submitTicket(String issueTypeId, String zoneId, String landmark,
                                String description, String severity,
                                String reporterName, String reporterContact) {

        IssueType issue = issueTypes.stream()
            .filter(t -> t.getId().equals(issueTypeId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown issue type: " + issueTypeId));

        Zone zone = zones.stream()
            .filter(z -> z.getId().equals(zoneId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Unknown zone: " + zoneId));

        ticketSeq++;
        String id = String.format("FN-2026-%04d", ticketSeq);

        String priority;
        if ("urgent".equals(severity)) priority = "high";
        else if ("worse".equals(severity)) priority = "med";
        else priority = "low";

        String team = zone.getName().split(" ")[0] + " Field Crew";

        Ticket ticket = new Ticket(
            id, issue.getId(), issue.getLabel(), zone.getId(), zone.getName(),
            landmark, description, severity, priority, team,
            STAGES[0], 0, "Citizen report", reporterName, reporterContact,
            LocalDateTime.now()
        );

        tickets.add(ticket);
        return ticket;
    }

    // ---------- Track a ticket ----------

    public Optional<Ticket> trackTicket(String ticketId) {
        String normalized = ticketId == null ? "" : ticketId.trim().toUpperCase();
        return tickets.stream().filter(t -> t.getId().equalsIgnoreCase(normalized)).findFirst();
    }

    public String[] stages() {
        return STAGES.clone();
    }

    // ---------- Maintenance queue (ops view) ----------

    public List<Ticket> maintenanceQueue() {
        List<Ticket> copy = new ArrayList<>(tickets);
        copy.sort((a, b) -> b.getSubmittedAt().compareTo(a.getSubmittedAt()));
        return copy;
    }

    /** Advances a ticket to the next stage (Acknowledged -> Assigned -> Resolved). */
    public Optional<Ticket> advanceTicket(String ticketId) {
        Optional<Ticket> found = trackTicket(ticketId);
        found.ifPresent(t -> {
            if (t.getStage() < STAGES.length - 1) {
                t.setStage(t.getStage() + 1);
                t.setStatus(STAGES[t.getStage()]);
            }
        });
        return found;
    }
}
