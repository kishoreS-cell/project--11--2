FlowNet — Urban Flood Nowcasting System
========================================

Look & feel: neon "ops-room" theme — dark background, glowing cyan
accents, a 3D glass water droplet (WebGL shader) on the sign-in/sign-up
pages, a drifting perspective grid + falling-rain canvas behind
everything, and glowing cards throughout. Purely visual — all the
login gating, validation, offline-demo fallback and ops functionality
below is unchanged.

Files:
- flownet-app.html   The whole app, in one HTML file. This is the merge of
                      what used to be two separate files:
                        - flownet-site.html          (sign-in / sign-up +
                                                       public site + a basic
                                                       ops view)
                        - flownet-ops-dashboard.html (earlier standalone
                                                       ops-only dashboard,
                                                       with the AI
                                                       Nowcasting gauge)
                      Opening flownet-app.html starts on a SIGN-IN page;
                      nothing else is reachable until you sign in. After
                      signing in: home, report an issue (ticket wizard),
                      track a ticket, city status, and — for staff/admin
                      accounts — the "Operations view" with all five tabs:
                      Monitoring, AI Nowcasting, GIS Map, AI Inspection,
                      Maintenance queue, plus "Run demo scenario".

                      This is the one to open / present.


LOGIN / SIGN-UP
---------------
The app is gated. On load you get the sign-in card; "Create one" opens the
sign-up card. Every navigation goes through one guard function, so there is no
way into the site without an account:

  1. signed out            -> only the sign-in and sign-up pages open
  2. signed in              -> the auth pages redirect you back to Home
  3. operations view       -> staff / admin accounts only

Your session is kept in sessionStorage, so a page refresh does not sign you out,
but closing the tab does.

Sign-up rules (enforced in the browser AND again on the server):
  username   3-20 characters, letters / numbers / underscore only, must be unique
  password   6-64 characters, at least one letter and at least one number
  confirm    must match the password
  role       Resident (citizen) | Municipal staff | Municipal admin
  code       staff and admin also need the ward access code: FLOWNET-STAFF-2026

Demo accounts (from ../flownet-database/sql/seed.sql):
  admin   / flownet123   (admin   — full ops view)
  staff   / ward2026     (staff   — full ops view)
  citizen / chennai01    (citizen — public pages only, no ops view)


OPERATIONS VIEW TABS
---------------------
  Monitoring       live-looking rainfall / water level / risk cards + trend chart
  AI Nowcasting    risk gauge, model confidence, predicted zone + ETA to flooding
  GIS Map          city drainage map — click a manhole/sensor/zone for readings
  AI Inspection    pick a location, "run" an AI defect scan, create a ticket from it
  Maintenance      the ticket queue (citizen reports + AI-inspection tickets)

"Run demo scenario" walks through all of the above automatically: rainfall
spikes, risk goes critical, the map highlights the affected zone, inspection
finds a blockage, and a maintenance ticket gets created.


TWO WAYS TO RUN
----------------
A) With the real database (this is what makes sign-in/sign-up "reflect in the DB")

   1. Set up MySQL and run ../flownet-database/sql/schema.sql then seed.sql.
   2. From ../flownet-database, compile and run ApiServer:
        javac -d out $(find src -name "*.java")
        java -cp "out:lib/mysql-connector-j-9.1.0.jar" com.flownet.web.ApiServer
   3. Open http://localhost:8080/ in your browser (NOT by double-clicking the
      file this time) — ApiServer serves this same flownet-app.html itself.
   4. Sign in, or create an account and then sign in with it.

   The forms call POST /api/login and POST /api/signup, which check / insert
   rows in the MySQL `users` table. Check it worked with:
     SELECT id, username, role, created_at, last_login FROM users;

B) Straight off the disk (no server, for a quick demo)

   Double-click flownet-app.html. ApiServer is not reachable, so the page
   falls back to OFFLINE DEMO MODE: the exact same validation rules run, but
   accounts are kept in this browser (localStorage) instead of MySQL. An amber
   banner on the form says so. The three demo accounts above still work.
   Nothing here touches the database.


Note: ticket submissions and sensor state still reset on page reload — only
accounts are persisted.

SIGN-IN / SIGN-UP BACKGROUND VIDEO
----------------------------------
The sign-in and sign-up pages (the first screens) play a looping, muted video
behind the glass login card:

  assets/login-bg.mp4          the looping background video (H.264, no audio)
  assets/login-bg-poster.jpg   first frame - shown instantly while the video
                               loads, and kept as a still if autoplay is blocked
                               or the visitor has "reduce motion" turned on

  - Keep the assets/ folder next to flownet-app.html (works both when you
    double-click the file and when ApiServer serves it).
  - The video plays ONLY on the auth pages and pauses once you sign in, so the
    rest of the app keeps its original neon grid + rain backdrop.
  - If the video file is missing or can't play, the page automatically falls
    back to the old grid + rain backdrop.
  - To swap the video: replace assets/login-bg.mp4 (and the poster).
  - Tune the darkness over the video: `.fx-video-wrap::after` in the CSS.
  - ApiServer now serves .mp4/.jpg/.webm with correct content-types and HTTP
    Range support (needed for video playback in Safari).

  - The big hero shape beside the login card is a real-time 3D water droplet
    (WebGL fragment shader, inline in flownet-app.html - search "dropFX"). It
    wobbles gently, floats, and leans towards the mouse. Browsers without WebGL
    get a flat SVG droplet instead. The small spinning cube logo in the card /
    header is unchanged.
