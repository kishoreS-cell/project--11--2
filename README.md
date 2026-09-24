# FlowNet - Complete Package

Everything for the FlowNet urban flood-nowcasting demo in one place.

## Contents

```
frontend/               static HTML/JS site, gated behind sign-in
  flownet-app.html           the whole app in one file: sign-in + sign-up,
                             then the public site + full operations view
                             (monitoring, AI nowcasting, drainage map, AI
                             inspection, maintenance queue). Previously this
                             was two separate files (flownet-site.html and
                             flownet-ops-dashboard.html) - they're now merged
                             into this single linked page.

flownet-java/            plain Java console app, in-memory (no database)
  see flownet-java/README.md

flownet-database/        same console app backed by MySQL over JDBC, plus
                          ApiServer - a small web server that gives the
                          front end's staff login a real database to check
  see flownet-database/README.md
```

## Sign in first

The app opens on a **sign-in page** and nothing else is reachable until an
account signs in. New users register on the **sign-up page**, which creates a
row in the `users` table. Demo accounts:

| Username | Password   | Role    | Gets the ops view? |
|----------|------------|---------|--------------------|
| admin    | flownet123 | admin   | yes |
| staff    | ward2026   | staff   | yes |
| citizen  | chennai01  | citizen | no  |

Staff/admin sign-up also needs the ward access code `FLOWNET-STAFF-2026`.

## Which one to use

- Quick demo, no setup: open `frontend/flownet-app.html` directly. Sign-in
  and sign-up work, but in an offline mode that keeps accounts in the browser
  instead of MySQL (the form says so).
- Want **sign-in and sign-up to actually hit the database**: set up MySQL,
  run `flownet-database`'s `ApiServer` and open `http://localhost:8080/` —
  see `flownet-database/README.md` for the full steps.
- Want to run the ticket logic in Java without setting up a database:
  use `flownet-java/`.

`flownet-java` and `flownet-database` implement the same features
(report an issue, track a ticket, city-wide status, maintenance queue)
that the wizard/track/status/ops-maintenance views show in the front end.
Accounts are implemented in `flownet-database` only (they need a real
`users` table): the console app starts on the same sign-in / sign-up gate,
and `ApiServer` exposes `POST /api/login` and `POST /api/signup` for the
web front end. `flownet-java` is in-memory and has no accounts.
