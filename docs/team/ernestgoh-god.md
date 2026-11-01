# Ernest Goh's Project Portfolio Page

### Project: ScheduleFlow

<!-- @@author ernestgoh-god -->

## Overview

ScheduleFlow is a Java 25 command-line application that helps university students
manage coursework, fixed commitments and study time. It generates study plans in
30-minute slots, explains work that cannot fit before its deadline, and saves
records and frozen plans for later sessions.

## Summary of Contributions

Given below are my contributions to the project.

- **New Feature: Added automatic saving and loading**
  ([PR #24][pr24], [PR #31][pr31]).
  - **What it does:** Saves and restores tasks, commitments and independent
    next-ID counters as versioned UTF-8 text. Extensions preserve study hours,
    policies, recurring templates and occurrence metadata.
  - **Justification:** Students can continue after closing the application
    without re-entering work or receiving reused IDs after deletion.
  - **Highlights:** Writes a sibling temporary file and atomically replaces the
    store. Validates the complete file and returns an empty first-launch state
    only when the file is definitely absent. Invalid or unreadable files never
    produce partial state. Added preconditions, assertions, Javadoc and opt-in
    diagnostics that omit saved values.

- **New Feature: Added frozen study-plan restoration** ([PR #89][pr89]).
  - **What it does:** Stores a plan and its complete source snapshot together,
    restoring the original generation time, sessions, exact credited work,
    unallocated work and breaks without regenerating the schedule.
  - **Justification:** Regenerating after a restart can move sessions as time
    changes. Restoring the saved result lets students resume the same plan.
  - **Highlights:** Extended storage and startup/dispatch integration to save
    source and result in one atomic replacement. Reconciled recurrence-format
    changes and retained legacy readers. Successful source changes remove stale
    plans; failed saves preserve the previous live state and plan.

- **Code contributed:** [RepoSense code dashboard][reposense]. My area comprises
  persistence through `FileStorage`, `StateCodec`, `PlanCodec` and the
  `Snapshot` aggregate, plus their tests.

- **Enhancements to existing features:**
  - **Task controls:** Added progress parsing and save/publication integration,
    completed remaining-time commands, and connected completion and completed
    views. Covered no-ops, rejected inputs, failed saves and saved restarts
    ([PR #77][pr77], [PR #83][pr83], [PR #84][pr84], [PR #90][pr90]).
  - **Study policies:** Added set/show/reset commands and persisted policies in
    snapshots and plans. Equal settings preserve a plan; changed settings save
    before publication and feed the continuous-work allocator ([PR #90][pr90]).
  - **Unavailable-period parsing:** Added continuous/daily range parsing and
    malformed-input tests ([PR #88][pr88]).
  - **Save-failure hardening:** Added a filesystem-operation seam and real-file
    tests for temporary creation, partial writes, unsupported atomic moves,
    replacement errors and cleanup failures ([PR #124][pr124]). Both snapshot
    and plan saves preserve old bytes on rejection. Cleanup retains the original
    cause and suppressed errors; production saves still require atomic moves.
    Controlled Windows failures do not establish other-provider or power-loss
    durability.
  - **Saved-plan validation:** Connected `Planner.validate(Plan)` to encoding and
    decoding. Rejects infeasible allocations and incorrect accounting, preserves
    causes and old bytes, and accepts alternative valid frozen schedules. See
    [storage verification][dg-verification].
  - **Regression coverage:** [FileStorageTest][storage-tests] uses temporary
    folders and fresh readers; [StateCodecTest][state-tests] and
    [PlanCodecTest][plan-tests] isolate conversion from filesystem access.
    Coverage includes Unicode/pipe-containing names, legacy formats, deleted-ID
    counters, exact work, corrupt data, frozen-plan round trips and cleanup.
    Parser, dispatcher, application and packaged tests cover policy settings,
    rollback and restarts using isolated data and fixed clocks.

- **Documentation:**
  - **User Guide:** Updated [quick start][ug-start],
    [duration guidance][ug-duration], [task listings][ug-list], the
    [frozen-plan example][ug-frozen], [saving/restoration][ug-save] and recovery
    FAQs. Clarified estimates versus reserved capacity, data location,
    compatibility, backups and plan invalidation.
  - **Developer Guide:** Wrote [persistence design][dg-design],
    [atomic saving/restoration][dg-implementation],
    [storage verification][dg-verification], [non-functional requirements][dg-nfr]
    and [manual recovery tests][dg-manual], and updated acknowledgements.
    Added the [storage class diagram][class-diagram] and
    [atomic-save sequence][sequence-diagram], with PlantUML sources and SVGs
    ([PR #107][pr107]). Updated the diagrams to show validation before file changes.

- **Team-based tasks:**
  - Integrated persistence with exact-work, recurrence, study-hours and policy
    changes. Added regressions for retaining metadata and publishing source/plan
    changes together ([PR #84][pr84], [PR #89][pr89], [PR #90][pr90]).

<!-- @@author -->

[pr24]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/24
[pr31]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/31
[pr89]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/89
[reposense]: https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=ernestgoh-god&breakdown=true
[pr77]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/77
[pr83]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/83
[pr84]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/84
[pr90]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/90
[pr88]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/88
[pr124]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/124
[dg-verification]: ../DeveloperGuide.md#storage-diagnostics-and-compatibility-verification
[storage-tests]: https://github.com/AY2627S1-CS2113-T10-1/tp/blob/master/src/test/java/scheduleflow/storage/FileStorageTest.java
[state-tests]: https://github.com/AY2627S1-CS2113-T10-1/tp/blob/master/src/test/java/scheduleflow/storage/StateCodecTest.java
[plan-tests]: https://github.com/AY2627S1-CS2113-T10-1/tp/blob/master/src/test/java/scheduleflow/storage/PlanCodecTest.java
[ug-start]: ../UserGuide.md#quick-start
[ug-duration]: ../UserGuide.md#recording-duration-tasks-and-commitments
[ug-list]: ../UserGuide.md#listing-tasks
[ug-frozen]: ../UserGuide.md#planning-partway-through-the-day
[ug-save]: ../UserGuide.md#saving-and-restoring-data
[dg-design]: ../DeveloperGuide.md#persistence-and-the-snapshot-aggregate
[dg-implementation]: ../DeveloperGuide.md#atomic-persistence-and-restoring-frozen-plans
[dg-nfr]: ../DeveloperGuide.md#appendix-c-non-functional-requirements
[dg-manual]: ../DeveloperGuide.md#saving-restoration-failure-and-recovery
[class-diagram]: ../images/diagrams/ernest-storage-classes.svg
[sequence-diagram]: ../images/diagrams/ernest-atomic-plan-save.svg
[pr107]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/107
