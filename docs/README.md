<!-- @@author nickpatt -->
# ScheduleFlow

ScheduleFlow is a Java 25 command-line application for university students who
want to manage coursework tasks, fixed commitments, and study plans.

It allocates available study time in 30-minute slots according to task deadlines,
reports work that cannot fit before its deadline, and saves successful changes
automatically. Task work remains exact even when its reserved capacity is rounded
up to a whole slot.

## Current implementation

The following features are implemented:

- Add, edit, list, and delete coursework tasks, preserving stable IDs.
- Record minute/session progress, revise estimates, retain completed tasks and
  reopen them with an explicit estimate.
- Add, list, and stop weekly task templates with saved occurrence-generation history.
- Add, edit, skip, list, and delete weekly/bounded commitments, and manage one-time
  commitments and continuous/daily unavailable periods.
- Configure weekday study hours, date overrides and optional work/break settings.
- Generate an earliest-deadline-first study plan.
- View today's schedule or a schedule for a specific date.
- Report unallocated work and the reason it could not be scheduled.
- Persist tasks, commitments, templates, settings, ID counters and frozen plans
  in `data/scheduleflow.txt`.
- Restore saved data when the application starts.
- Preserve state when validation or saving fails.
- Run with UTF-8 console input and output.
- Package the application as an executable Shadow JAR.

Plans and their source data survive restarts without moving sessions. Successful
source changes clear the saved plan; rejected commands and recognized no-ops
preserve it. Run `plan` again after a change to schedule the revised work.

## User documentation

- [User Guide](UserGuide.md) — installation, commands, examples, scheduling
  rules, persistence, and troubleshooting.
- [Developer Guide](DeveloperGuide.md) — architecture, implementation details,
  design decisions, testing, and release verification.
- [About Us](AboutUs.md) — project team information.

## Quick start

The project requires Java 25.

From the repository root, run:

```shell
java -version
./gradlew --version
./gradlew build
```

On Windows, use:

```powershell
java -version
.\gradlew.bat --version
.\gradlew.bat build
```

The executable JAR is produced at:

```text
build/libs/scheduleflow.jar
```

Launch it from a dedicated folder:

```shell
java -ea -jar scheduleflow.jar
```

The application stores data relative to the launch folder at:

```text
data/scheduleflow.txt
```

## Supported commands

Use `help` for syntax, `task` commands for coursework and weekly templates,
`commitment` commands for fixed events, and `unavailable continuous` or
`unavailable daily` for multiple dates. Use `hours` and `policy` to configure
availability and rest, `plan` to generate a saved plan, `schedule` to view it,
and `exit` to close the application. See the
[complete command summary](UserGuide.md#command-summary) for exact field order,
examples and restrictions.

Ordinary task estimates, progress and reopening use exact whole minutes;
creation/reopening require positive values. Commitments and weekly template
estimates require positive multiples of 30. Study hours default to 08:00–22:00
every day; weekday and date-specific overrides can change them. Work/break
limits apply when a policy is configured. Allocation slots remain 30 minutes.

## Testing

Run the Java build, tests, and Checkstyle with:

```shell
./gradlew build
```

Run the maintained process-level UI scenarios with Node.js 22 or later:

```shell
node --test test/ui-runner.test.mjs
node .agents/skills/test-ui/scripts/run-ui-tests.mjs
node --test test/release.test.mjs
```

The UI scenarios run the packaged application in isolated temporary folders and
cover startup, persistence, restart behavior, validation errors, Unicode data,
ID handling, partial plans, and release packaging.

See the [UI test plan](../test/ui-test-plan.md) for the complete scenario list
and verification protocol.

## Project structure

```text
src/main/java/scheduleflow/
├── cli/          Command parsing, dispatch, application lifecycle, and rendering
├── common/       Shared input validation and validation exceptions
├── commitment/   Recurring commitment operations
├── model/       Immutable tasks, commitments, and snapshots
├── planning/    Study-plan generation and daily schedule projection
├── storage/     UTF-8 persistence and atomic file replacement
└── task/        Coursework task operations

src/test/java/   JUnit 5 tests mirroring the production packages
test/             Node.js process/UI tests and scenario fixtures
docs/             User, developer, and team documentation
```

The application starts at `scheduleflow.Main`. The command-line layer parses and
dispatches commands to the task, commitment, planning, and storage components.
Mutating operations create candidate snapshots, save them first, and publish
them only after persistence succeeds.

For repository conventions and contribution instructions, see
[AGENTS.md](../AGENTS.md).
<!-- @@author -->
