# Skill Registry — ENTREGA_CLIENTE_SERVIDOR

Generated: 2026-05-11
Project: ENTREGA_CLIENTE_SERVIDOR

## User Skills

| Skill | Trigger Context |
|-------|----------------|
| branch-pr | Creating pull requests, opening PRs, preparing changes for review |
| issue-creation | Creating GitHub issues, reporting bugs, requesting features |
| judgment-day | "judgment day", "review adversarial", "dual review", "juzgar" |
| skill-creator | Creating new AI skills, adding agent instructions |
| skill-registry | "update skills", "skill registry", "actualizar skills" |
| go-testing | Go tests, Bubbletea TUI testing, teatest |
| sdd-explore | Investigating ideas, exploring codebase |
| sdd-propose | Creating change proposals |
| sdd-spec | Writing specifications |
| sdd-design | Creating technical designs |
| sdd-tasks | Breaking down tasks |
| sdd-apply | Implementing tasks |
| sdd-verify | Validating implementation |
| sdd-archive | Archiving completed changes |
| sdd-onboard | SDD guided walkthrough |
| sdd-init | Initializing SDD in a project |

## Project Conventions

No project-level AGENTS.md, CLAUDE.md, or .cursorrules found.

## Compact Rules

### Java / Maven Project
- Java 21, Maven build system
- Use Hexagonal/Clean Architecture layers: `domain`, `application`, `infrastructure`, `presentation`
- Follow existing package naming conventions (e.g. `com.cliente.*`, `com.arquitectura.*`)
- No test framework configured in server module — client has JUnit Jupiter 5
- Jackson for JSON serialization; Gson also present in client
- Hibernate + JPA + HikariCP for server persistence (MySQL)
- H2 in-memory DB for client-side persistence
- TCP + UDP socket communication via custom messaging library (`JavaMensajeriaComunicacion`)
- JavaFX 21 for client UI with FXML controllers
- Handler pattern (Chain of Responsibility) for message routing on server
