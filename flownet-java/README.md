# FlowNet - Java (in-memory)

Plain Java console version of the FlowNet flood-reporting demo (report an
issue, track a ticket, view city status, view/advance the maintenance
queue). No database - all data is held in memory and resets when the
program restarts.

For the version that persists data in MySQL, see the separate
`flownet-database` project.

## Structure

```
src/com/flownet/Main.java              console menu / entry point
src/com/flownet/service/TicketService.java   business logic (mirrors flownet-app.html)
src/com/flownet/model/Ticket.java
src/com/flownet/model/Zone.java
src/com/flownet/model/IssueType.java
```

## Compile and run

From the project root:

```
javac -d out $(find src -name "*.java")
java -cp out com.flownet.Main
```

On Windows (cmd), compile each file or use a wildcard per package, e.g.:

```
javac -d out src\com\flownet\model\*.java src\com\flownet\service\*.java src\com\flownet\Main.java
java -cp out com.flownet.Main
```
