# Git Branching Strategy & Workflow Policy

This document establishes the version control workflow, branching conventions, pull request rules, and release tagging practices for the **Business Licence Renewal System**.

---

## 1. Branch Topology

We adopt a structured GitFlow-adapted workflow designed for collaborative feature development and continuous integration.

```
       [hotfix/*]
           │
           ▼
[main] ────●────────────────────────● (v1.0.0 Release Tag)
           ▲                        ▲
           │                        │ (Production Release)
[development] ──●──────●──────●─────●
                ▲      ▲      ▲
                │      │      │ (Feature PR merges)
    [feature/auth]  [feature/crud]  [feature/ci-pipeline]
```

### Permanent Branches
- **`main`**: Represents production-ready, releasable software. Only merged from `development` upon completion of sprint milestones or release candidate validation. Every commit on `main` is tagged with a semantic version (e.g., `v1.0.0`).
- **`development`**: Integration branch where completed features are merged and tested. Serves as the primary target for pull requests.

### Ephemeral / Working Branches
- **`feature/<feature-name>`**: Branched off `development` for implementing specific user stories (e.g., `feature/auth-and-models`, `feature/licence-crud-workflow`). Merged back into `development` via Pull Request after passing tests.
- **`bugfix/<issue-id>`**: Branched off `development` to resolve defects found during testing or staging.
- **`hotfix/<issue-id>`**: Branched directly off `main` for critical production fixes. Merged into both `main` and `development`.

---

## 2. Commit Message Guidelines

We follow Conventional Commits specification:

Format: `<type>(<scope>): <short imperative summary>`

Types:
- `feat`: A new feature (e.g., `feat(auth): implement server-enforced role authentication`)
- `fix`: A bug fix (e.g., `fix(validation): correct turnover range validation logic`)
- `docs`: Documentation updates (e.g., `docs(srs): add entity relationship diagrams`)
- `style`: Formatting, missing semi-colons, whitespace (no code change)
- `refactor`: Refactoring code without changing external behavior
- `test`: Adding or updating automated tests (e.g., `test(selenium): add owner login journey`)
- `ci`: CI/CD pipeline or build configuration changes (e.g., `ci(jenkins): add tomcat deploy stage`)
- `chore`: Maintenance tasks, dependency updates, or wrapper changes

---

## 3. Pull Request (PR) Policy & Merge Rules

1. **Branch Naming**: Must start with `feature/`, `bugfix/`, or `hotfix/`.
2. **Target Branch**: All feature PRs must target `development`.
3. **Automated Verification**: Before merging, the project must build cleanly with `./mvnw.cmd clean test` without compilation warnings or test failures.
4. **Peer Review**: At least one code review approval required.
5. **No Direct Pushes to `main`**: All code enters `main` via PR from `development` or a tagged release branch.
6. **Merge Method**: Merge commit (`--no-ff`) preferred for major features to preserve feature history, or Squash & Merge for single atomic fixes.

---

## 4. Release Tagging Policy

Release tags follow Semantic Versioning (`vMAJOR.MINOR.PATCH`):
- `v0.1.0`: Minimal skeleton and architecture milestone.
- `v0.5.0`: Core MVP feature milestone (Auth, CRUD, Review Queue).
- `v1.0.0`: Full assignment release (Jenkins CI/CD, Selenium tests, Docker, Ansible).
