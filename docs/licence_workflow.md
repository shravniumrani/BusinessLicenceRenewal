# Licence workflow implementation and local verification

## What is included

- Owner application creation with business name, registration number, trade
  category, annual turnover, licence expiry date, contact email and phone.
- Owner-scoped lists, search, details, edit and deletion of submitted records.
- Officer queue, status filter, review details and optional UNDER_REVIEW step.
- Mandatory approval/rejection remarks, reviewer identity and decision timestamp.
- Dashboard counters scoped to the signed-in role.
- File-backed H2 storage, unique registration numbers and owner/reviewer foreign
  keys. Decisions and audit rows are committed atomically. Conditional status
  updates prevent stale owner edits or competing decisions from overwriting one
  another.

## Validation and response behavior

Registration numbers contain 3–40 letters/digits/hyphens and are normalized to
uppercase. Turnover is non-negative with at most two decimal places, up to
9999999999999.99. Expiry dates from 2000 through twenty years from today are
accepted, including overdue licences. Email and phone are validated on the server.
Business names, categories and contact values have bounded lengths. Approval and
rejection require nonblank remarks up to 1000 characters.

Unauthorized role or owner access returns 403; missing records return 404;
malformed input returns 400; duplicates and stale status changes return 409.
Search text is a literal substring, not SQL or a wildcard expression. JSP output
escapes applicant data and remarks. Successful POSTs redirect to prevent accidental
form resubmission on refresh. CSRF and session protections remain enabled.

## Persistence

Default directory on Shravni's Windows account:
`C:\Users\SHRAVNI\BusinessLicenceRenewal-data`.

H2 creates `licences.mv.db` there, separate from the WAR and the Git repository.
Restarting Tomcat or replacing the WAR retains the data. Do not delete this data
folder during deployment. For a custom path, set `LICENCE_DATA_DIR` before
starting Tomcat. The JVM property `licence.data.dir` takes precedence over that
environment variable. Back up only while Tomcat is stopped. Use a single Tomcat
instance for this embedded file store. Automated repository tests use separate
in-memory or temporary databases and do not touch the local application database.

The fixed demo accounts remain in use. Database identities are seeded from their
IDs, names and roles. Registration and database-backed credentials are future work.

## Automated verification

Run from the project folder with JDK 21:

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
.\mvnw.cmd clean package
```

The suite has the original 7 tests plus 10 workflow tests (17 total). New tests
exercise ownership isolation, create/edit/delete, status locking, required
remarks, role boundaries, duplicates, bad input, concurrent decisions and file
persistence after reopening the database.

Preparation checks in the assistant workspace: core Java compilation succeeded
using its available Java 17 compiler, and 27 validation/access checks passed.
The full Maven/JUnit build and JSP/Tomcat verification were **not run** there:
Maven Central could not be resolved and the environment lacks JDK 21 and Tomcat.
Run the command above on the student's laptop and retain the actual output as
evidence. Do not describe the new tests as passed until that run succeeds.

## Manual Tomcat checks after the build passes

1. Owner1: submit `REG-2026-1001` with business Apex Retailers, category Retail,
   turnover 4500000, expiry 2026-12-31, email contact@apex.example and phone
   9876543210. Note the APP tracking number.
2. Search for part of the registration number. Edit the submitted record and
   verify the updated value. Create a second submitted record and delete it.
3. Owner2: verify owner1's records are absent. Visiting owner1's detail/edit URL
   with its tracking ID must return 403.
4. Officer: find the application, mark it under review, then approve with remarks.
   Owner1 must see the final status and remarks, with no edit/delete action.
5. Submit another record and reject it with remarks. A blank remarks submission
   must be rejected (HTML validation and server validation).
6. Verify status filtering and counters. Restart Tomcat and verify records remain.
7. Recheck officer access to `/owner/dashboard` is 403 and protected pages after
   logout redirect to login.

Keep screenshots and actual Maven output for the course evidence. Jenkins,
Selenium, Docker, Ansible, the second feature PR, real conflict resolution and
release tagging remain separate work.
