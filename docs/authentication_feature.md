# First feature: authentication and user roles

Implemented: stored salted PBKDF2-HMAC-SHA256 credentials (600,000 iterations), login,
logout via POST, session ID rotation at login, 20-minute sessions, HttpOnly session
cookies, cookie-only tracking, CSRF checks, session-based attempt cooldown, escaped
user display and server-side role checks. The cooldown is a local demo control,
not a distributed brute-force defence. Demo accounts are fixed; registration is
outside the current stage. No real credentials or personal data should be entered.

| Username | Local demo password | Role |
|---|---|---|
| owner1 | OwnerDemo!2026 | BUSINESS_OWNER |
| owner2 | OwnerTwo!2026 | BUSINESS_OWNER |
| officer | OfficerDemo!2026 | LICENSING_OFFICER |

The credentials above are intentionally public demo credentials. The application
stores their independently salted hashes in demo-users.properties, never the raw
passwords. User objects held in sessions contain no password hashes. Use HTTPS and
secure cookies if this demonstration is later deployed outside localhost.

GET /login renders a login form. POST /login verifies credentials. GET /dashboard
redirects to the matching workspace. Business owners cannot access /officer/*,
and officers cannot access /owner/*. POST /logout invalidates the session; GET
/logout is rejected with 405. /health remains public. JSP views live under WEB-INF
and cannot be requested directly. User role is taken from stored account data,
not from browser fields.

Licence records, persistent licence storage, search, decisions and metric counts
are still pending for the next feature. This stage does not claim those features.

## Windows build

Run from BusinessLicenceRenewal in PowerShell:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
.\mvnw.cmd clean package
```

## Manual acceptance checks once Tomcat is configured

1. Visit /business-licence-renewal/login; wrong password gives a clear error.
2. Sign in as owner1; owner workspace opens. Request /officer/dashboard: 403.
3. Sign out, sign in as officer; officer workspace opens. Request /owner/dashboard: 403.
4. Sign out; requesting either workspace redirects to login.
5. Submit a POST without the current form token: 403. GET /health still works.

Record actual results after running these checks. No screenshots, PR review,
runtime success or Jenkins results are claimed by this update.

Reference: Jakarta Servlet 6.0 HttpServletRequest.changeSessionId():
https://jakarta.ee/specifications/servlet/6.0/apidocs/jakarta.servlet/jakarta/servlet/http/httpservletrequest

## Verification in preparation environment

12 direct Java checks passed for all three demo accounts, rejection of incorrect
and unknown credentials, null inputs, malformed hashes, distinct owner IDs and
role access policy. These core classes compiled with the available Java 17
compiler. The project's target remains Java 21. A complete Maven/Servlet/JSP
build and browser checks were not run here: Maven downloads are blocked and
Java 21 is unavailable. Run the Windows build before committing this feature.
