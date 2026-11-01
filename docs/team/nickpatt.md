# Nickhil Pattikonda's Project Portfolio Page

### Project: ScheduleFlow

<!-- @@author nickpatt -->

## Overview

ScheduleFlow is a Java 25 command-line application that helps university students
manage coursework, fixed commitments and study time. It generates study plans in
30-minute slots, explains work that cannot fit before its deadline, and saves
records and frozen plans for later sessions.

## Summary of Contributions

Given below are my contributions to the project.

- **New Feature: Added task lifecycle operations with exact work estimates**
  ([progress/completion implementation][progress-commit],
  [task queries and priority APIs][queries-commit]).
  - **What it does:** Supports exact whole-minute estimates, direct progress,
    session-percentage credit, retained completion and explicit reopening.
    Students can revise estimates and resume completed work while preserving
    task IDs, priority and recurring-task provenance.
  - **Justification:** Students need to record the work actually completed and
    correct estimates without losing task identity or recurrence information.
  - **Highlights:** Uses immutable candidate snapshots so command execution can
    save before publication. Session credit is rounded down and uses long
    arithmetic before multiplication to avoid overflow. No-op updates retain
    snapshot identity. Rejected inputs preserve records, counters and plans.

- **Code contributed:** [RepoSense code dashboard][reposense]. My area comprises
  `Task`, `TaskPriority`, `TaskEdit`, `TaskService`, `CommandParser`,
  `ParseException`, shared input validators and their tests.

- **Enhancements to existing features:**
  - **Parsing and validation:** Maintained [CommandParser][parser] and
    [InputRules][rules]. Whole-command matching rejects malformed fields, invalid
    dates, embedded controls and integer overflow before returning command
    values. Domain services resolve IDs and state/time rules. The release audit
    verified the grammar and validation boundary without requiring a production
    task/parser behaviour change ([release audit][audit-commit]).
  - **Task regression coverage:** [TaskServiceTest][service-tests] covers minute
    boundaries, overflow-safe progress, lifecycle restrictions, immutable
    candidates, no-op identity, counters and deadline rules. Tests use fixed
    times and independent immutable inputs.
  - **Generated-task regressions:** Extended [TaskRecurrenceTest][recurrence-tests]
    for progress, completion, reopening and editing after template deletion.
    Checks retained position, provenance, priority, custom hours, policy and
    counters, so deleting a template does not prevent further work on its tasks.
  - **Console API boundaries:** [CommandParserTest][parser-tests] covers supported
    command families and both unavailable modes. Added rejection tests for
    domain-only task APIs. Optional-deadline and priority changes remain API
    capabilities; console task addition still requires a deadline.

- **Documentation:**
  - **User Guide:** Documented [task lifecycle][ug-tasks],
    [progress recording][ug-progress], [completion][ug-completion] and
    [reopening][ug-reopening], explaining exact remaining work, retained records,
    stable IDs, no-ops and plan invalidation ([User Guide change][ug-commit]).
  - **Developer Guide:** Wrote [task/parser design][dg-design],
    [lifecycle implementation][dg-implementation],
    [verification conventions][dg-verification] and
    [manual task tests][dg-manual] ([Developer Guide change][dg-commit]). Reviewed
    the [class diagram][class-diagram] and [progress sequence][sequence-diagram]
    against the implemented APIs and call order.

- **Team-based tasks:**
  - Corrected the [documentation entry][docs-entry] and reconciled task release
    notes and the user-story table with the source during the
    [release audit][audit-commit]. Documented coverage and commands for repeatable
    [task/parser verification][dg-verification].

<!-- @@author -->

[progress-commit]: https://github.com/AY2627S1-CS2113-T10-1/tp/commit/bfba4b1d6918b30d2fb608d964c357894d48341b
[queries-commit]: https://github.com/AY2627S1-CS2113-T10-1/tp/commit/207f2df373d1ceaea19dd75ab82ca9839b9c82aa
[reposense]: https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=nickpatt&breakdown=true
[parser]: https://github.com/AY2627S1-CS2113-T10-1/tp/blob/master/src/main/java/scheduleflow/cli/CommandParser.java
[rules]: https://github.com/AY2627S1-CS2113-T10-1/tp/blob/master/src/main/java/scheduleflow/common/InputRules.java
[audit-commit]: https://github.com/AY2627S1-CS2113-T10-1/tp/commit/d6d1ea74eb8bedd6d7a0b6076419331bca739f27
[service-tests]: https://github.com/AY2627S1-CS2113-T10-1/tp/blob/master/src/test/java/scheduleflow/task/TaskServiceTest.java
[recurrence-tests]: https://github.com/AY2627S1-CS2113-T10-1/tp/blob/master/src/test/java/scheduleflow/task/TaskRecurrenceTest.java
[parser-tests]: https://github.com/AY2627S1-CS2113-T10-1/tp/blob/master/src/test/java/scheduleflow/cli/CommandParserTest.java
[ug-tasks]: ../UserGuide.md#tasks
[ug-progress]: ../UserGuide.md#recording-progress
[ug-completion]: ../UserGuide.md#completing-and-viewing-completed-tasks
[ug-reopening]: ../UserGuide.md#reopening-a-completed-task
[ug-commit]: https://github.com/AY2627S1-CS2113-T10-1/tp/commit/3c4a20c400485a8a8b0fd6e181c5dcf43c9007fb
[dg-design]: ../DeveloperGuide.md#task-model-and-command-parsing
[dg-implementation]: ../DeveloperGuide.md#exact-task-work-and-retained-task-lifecycle
[dg-verification]: ../DeveloperGuide.md#taskparser-validation-and-junitdocumentation-conventions
[dg-manual]: ../DeveloperGuide.md#tasks-and-input-validation
[dg-commit]: https://github.com/AY2627S1-CS2113-T10-1/tp/commit/547c79ba81892c0b759013c5fd9480d957b6a76b
[class-diagram]: ../diagrams/nickhil-task-parser-classes.puml
[sequence-diagram]: ../diagrams/nickhil-task-progress.puml
[docs-entry]: ../README.md
