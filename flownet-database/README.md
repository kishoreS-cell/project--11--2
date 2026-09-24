# FlowNet - Java + MySQL

Database-backed version of the FlowNet console app: report an issue, track
a ticket, view city status, view/advance the maintenance queue - all read
from and written to a MySQL database over JDBC.

**Sign-in and sign-up are the front door.** Nothing in the app - console or
web - is reachable until an account signs in. Accounts live in the `users`
table: `POST /api/login` / `POST /api/signup` serve the web front end, and
the console has the same two options at startup. Every successful sign-in
stamps `users.last_login`, and every sign-up inserts a new row.

## Structure

```
sql/schema.sql                         CREATE DATABASE + tables (incl. users)
sql/seed.sql                           sample zones, issue types, tickets, demo accounts
config.properties                      DB URL / user / password
src/com/flownet/Main.java              console entry point: login/signup gate, then the menu
src/com/flownet/web/ApiServer.java     HTTP server: frontend + POST /api/login + POST /api/signup
src/com/flownet/db/DBConnection.java   opens the JDBC connection
src/com/flownet/dao/ZoneDAO.java       CRUD for zones
src/com/flownet/dao/IssueTypeDAO.java  CRUD for issue types
src/com/flownet/dao/TicketDAO.java     CRUD for tickets
src/com/flownet/dao/UserDAO.java       accounts: authenticate() / recordLogin() /
                                       usernameExists() / createAndReturn()
src/com/flownet/model/Ticket.java
src/com/flownet/model/Zone.java
src/com/flownet/model/IssueType.java
src/com/flownet/model/User.java
```

## 1. Set up the database

In MySQL:

```
mysql -u root -p < sql/schema.sql
mysql -u root -p < sql/seed.sql
```

`seed.sql` creates two demo staff accounts in the new `users` table:

| Username | Password    | Role  |
|----------|-------------|-------|
| admin    | flownet123  | admin |
| staff    | ward2026    | staff |
| citizen  | chennai01   | citizen |

Passwords are stored as `SHA2(password, 256)` hashes, never plaintext.

Roles: `citizen` can report and track issues; `staff` and `admin` also get
the operations & monitoring view. A `citizen` account can be created by
anyone from the sign-up form; `staff` and `admin` additionally require the
ward access code, which is `FLOWNET-STAFF-2026` in this demo (defined as
`STAFF_ACCESS_CODE` in `ApiServer.java` and `Main.java` - change it there).

## 2. Configure the connection

Edit `config.properties` in the project root:

```
db.url=jdbc:mysql://localhost:3306/flownet?useSSL=false&serverTimezone=UTC
db.user=root
db.password=yourpassword
```

## 3. Get the MySQL JDBC driver

Download `mysql-connector-j-<version>.jar` from
https://dev.mysql.com/downloads/connector/j/ and place it in this project
(e.g. in a `lib/` folder).

## 4. Run the web login (recommended - this is what the front end needs)

`ApiServer` is a small built-in web server (uses only `com.sun.net.httpserver`,
which ships with the JDK - nothing extra to install). It does two things:
serves the static site from `../frontend`, and exposes the two account
endpoints:

| Endpoint | Body | Does |
|----------|------|------|
| `POST /api/login`  | `{username, password}` | `SELECT ... WHERE username=? AND password_hash=SHA2(?,256)`, then stamps `users.last_login = NOW()`. 200 / 401. |
| `POST /api/signup` | `{username, password, role, accessCode}` | Validates, rejects duplicates, inserts with `SHA2(password,256)`. 201 / 400 / 409. |

The server re-checks every rule the browser form checks (username format,
password length and content, role, access code, duplicate username), because
anyone can post straight to these endpoints without using the form.

```
javac -d out $(find src -name "*.java")
java -cp "out:lib/mysql-connector-j-9.1.0.jar" com.flownet.web.ApiServer
```

Then open **http://localhost:8080/** in a browser (not by double-clicking
the HTML file - it needs to be loaded from this server so the form's
`fetch('/api/login')` call has something to reach).

The first thing you see is the sign-in page; the rest of the site is not
reachable until you sign in. Use a demo account above, or click "Create one"
to register a new account and then sign in with it.

To confirm it reached the database:

```sql
SELECT id, username, role, created_at, last_login FROM users;
```

`last_login` shows the timestamp of your most recent sign-in, and any
account you created from the sign-up form is a new row here.

> If you open `flownet-app.html` straight off the disk instead, the page
> falls back to an offline demo mode and keeps accounts in the browser, so
> login and sign-up still work for a presentation - but nothing touches
> MySQL, and the form says so in an amber banner.

## 5. Or run the console app (no browser needed)

```
java -cp "out:lib/mysql-connector-j-9.1.0.jar" com.flownet.Main
```

It starts on the same gate as the web front end - **1. Sign in / 2. Create
an account / 0. Exit** - and the main menu only appears once an account is
signed in. Options 4 (maintenance queue) and 5 (advance a ticket) are
limited to `staff` / `admin` accounts; option 6 signs out and returns to the
gate. A successful sign-in here also updates `users.last_login`, and
"Create an account" inserts into the same `users` table as the web form.

On Windows (cmd), use `;` instead of `:` as the classpath separator, e.g.:

```
java -cp "out;lib\mysql-connector-j-9.1.0.jar" com.flownet.web.ApiServer
```

`config.properties` must stay in the directory you run `java` from (i.e.
`flownet-database/`), since `DBConnection` reads it as a relative file path -
this applies to both `Main` and `ApiServer`.
