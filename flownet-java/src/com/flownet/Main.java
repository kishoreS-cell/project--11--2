package com.flownet;

import com.flownet.model.IssueType;
import com.flownet.model.Ticket;
import com.flownet.model.Zone;
import com.flownet.service.TicketService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Console version of the FlowNet public site + ops maintenance queue.
 * No database: all data lives in memory and resets on restart.
 * See the flownet-database project for the SQL-backed version.
 */
public class Main {

    private static final Scanner SC = new Scanner(System.in);
    private static final TicketService SERVICE = new TicketService();

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" FlowNet - Urban Flood Nowcasting System (Java)");
        System.out.println("=================================================");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = SC.nextLine().trim();
            switch (choice) {
                case "1": reportIssue(); break;
                case "2": trackTicket(); break;
                case "3": cityStatus(); break;
                case "4": maintenanceQueue(); break;
                case "5": advanceTicket(); break;
                case "0": running = false; break;
                default: System.out.println("Invalid option, try again.");
            }
        }
        System.out.println("Goodbye.");
    }

    private static void printMenu() {
        System.out.println();
        System.out.println("1. Report an issue");
        System.out.println("2. Track a ticket");
        System.out.println("3. View city-wide status");
        System.out.println("4. View maintenance queue (ops)");
        System.out.println("5. Advance a ticket's stage (ops)");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private static void reportIssue() {
        List<IssueType> types = SERVICE.listIssueTypes();
        System.out.println("\nWhat's the issue?");
        for (int i = 0; i < types.size(); i++) {
            System.out.println((i + 1) + ". " + types.get(i).getLabel());
        }
        int typeIdx = readInt("Pick 1-" + types.size() + ": ", 1, types.size()) - 1;

        List<Zone> zones = SERVICE.listZones();
        System.out.println("\nWhich area?");
        for (int i = 0; i < zones.size(); i++) {
            System.out.println((i + 1) + ". " + zones.get(i).getName());
        }
        int zoneIdx = readInt("Pick 1-" + zones.size() + ": ", 1, zones.size()) - 1;

        System.out.print("Landmark (e.g. opposite bus depot): ");
        String landmark = SC.nextLine().trim();

        System.out.print("Describe what you're seeing: ");
        String description = SC.nextLine().trim();

        System.out.println("\nHow urgent is it?");
        System.out.println("1. Just noticed it");
        System.out.println("2. Getting worse");
        System.out.println("3. Urgent - water entering homes/shops");
        int sevIdx = readInt("Pick 1-3: ", 1, 3);
        String severity = sevIdx == 3 ? "urgent" : sevIdx == 2 ? "worse" : "notice";

        System.out.print("Your name (optional): ");
        String name = SC.nextLine().trim();
        System.out.print("Contact number (optional): ");
        String contact = SC.nextLine().trim();

        Ticket ticket = SERVICE.submitTicket(
            types.get(typeIdx).getId(), zones.get(zoneIdx).getId(), landmark, description,
            severity, name.isEmpty() ? null : name, contact.isEmpty() ? null : contact
        );

        System.out.println("\nTicket submitted. Your ticket ID is: " + ticket.getId());
        System.out.println("Keep this ID to track progress.");
    }

    private static void trackTicket() {
        System.out.print("\nEnter ticket ID (e.g. FN-2026-0001): ");
        String id = SC.nextLine().trim();
        Optional<Ticket> found = SERVICE.trackTicket(id);
        if (!found.isPresent()) {
            System.out.println("No ticket found with ID " + id);
            return;
        }
        Ticket t = found.get();
        System.out.println("\nTicket " + t.getId());
        System.out.println("Issue:       " + t.getIssueLabel());
        System.out.println("Location:    " + t.getLandmark() + ", " + t.getZoneName());
        System.out.println("Priority:    " + t.getPriority());
        System.out.println("Assigned to: " + t.getTeam());
        System.out.println("Status:      " + t.getStatus());
        System.out.println("Submitted:   " + t.getSubmittedAt());
        System.out.print("Progress:    ");
        String[] stages = SERVICE.stages();
        for (int i = 0; i < stages.length; i++) {
            System.out.print((i <= t.getStage() ? "[x] " : "[ ] ") + stages[i] + "  ");
        }
        System.out.println();
    }

    private static void cityStatus() {
        System.out.println("\nCity-wide flood risk: " + labelFor(SERVICE.cityWideRisk()));
        System.out.println(String.format("%-8s | %-22s | %-8s | %-7s | %-10s | %s",
            "ZONE", "NAME", "RISK", "LEVEL", "FLOW", "STATUS"));
        for (Zone z : SERVICE.listZones()) {
            System.out.println(z);
        }
    }

    private static void maintenanceQueue() {
        List<Ticket> tickets = SERVICE.maintenanceQueue();
        if (tickets.isEmpty()) {
            System.out.println("\nNo tickets in the queue.");
            return;
        }
        System.out.println();
        System.out.println(String.format("%-14s | %-24s | %-22s | %-6s | %-20s | %-10s | %s",
            "ID", "ISSUE", "ZONE", "PRIOR", "STATUS", "TEAM", "SUBMITTED"));
        for (Ticket t : tickets) {
            System.out.println(t);
        }
    }

    private static void advanceTicket() {
        System.out.print("\nEnter ticket ID to advance: ");
        String id = SC.nextLine().trim();
        Optional<Ticket> updated = SERVICE.advanceTicket(id);
        if (!updated.isPresent()) {
            System.out.println("No ticket found with ID " + id);
            return;
        }
        System.out.println("Ticket " + updated.get().getId() + " is now: " + updated.get().getStatus());
    }

    private static String labelFor(String risk) {
        switch (risk) {
            case "warn": return "Moderate";
            case "crit": return "High";
            default: return "Low";
        }
    }

    private static int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = SC.nextLine().trim();
            try {
                int val = Integer.parseInt(line);
                if (val >= min && val <= max) return val;
            } catch (NumberFormatException ignored) {
                // fall through to error message
            }
            System.out.println("Enter a number between " + min + " and " + max + ".");
        }
    }
}
