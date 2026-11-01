# Darren's Project Portfolio Page

### Project: ScheduleFlow

<!-- @@author Darren50 -->

## Overview

ScheduleFlow is a Java 25 command-line application that helps university students
manage coursework, fixed commitments and study time. It generates study plans in
30-minute slots, explains work that cannot fit before its deadline, and saves
records and frozen plans for later sessions.

## Summary of Contributions

Given below are my contributions to the project.

- **New Feature: Added recurring commitments**
  ([PR #23][pr23], [PR #26][pr26], [PR #34][pr34], [PR #70][pr70]).
  - **What it does:** Adds, deletes and lists weekly commitments such as lectures
    and tutorials. Records receive stable `C` IDs that are never reused, and
    listing groups them by weekday and start time.
  - **Justification:** A realistic study plan must reserve time when the student
    is already busy.
  - **Highlights:** Operations return immutable candidate snapshots so changes
    can be saved before publication. Overlap errors report every conflicting
    commitment in start-time order, while adjacent intervals remain valid.
    Added preconditions, assertions, Javadoc and opt-in diagnostics that omit
    commitment names and schedules.

- **New Feature: Added commitment editing date bounds and skipped occurrences**
  ([PR #71][pr71], [PR #78][pr78], [PR #79][pr79]).
  - **What it does:** Edits selected fields without changing the ID, bounds a
    series with `from/` and `until/`, and cancels one occurrence with
    `commitment skip`.
  - **Justification:** Students can correct class times, limit classes to a
    semester and reclaim cancelled sessions without deleting a whole series.
  - **Highlights:** Validates the complete replacement and excludes only the
    edited record from conflict checks. Compares actual calendar occurrences,
    allowing different semesters to share a weekly slot and skipped dates to
    free capacity. Date bounds and skips retain compatibility with older files.

- **New Feature: Added one-time commitments and multi-date unavailable periods**
  ([PR #80][pr80], [PR #86][pr86], [PR #126][pr126]).
  - **What it does:** Blocks a single date, a continuous interval across dates,
    or the same daily window over a date range.
  - **Justification:** Meetings, trips and appointments should reserve only
    their actual dates rather than repeating every week.
  - **Highlights:** Represents one-time events through equal start/end dates,
    reusing calendar overlap and planning logic. Continuous ranges split at
    midnight; daily ranges expand into dated commitments. The dispatcher
    prepares confirmation, saves the complete batch once, then publishes it.
    Conflicts or failed saves preserve the previous plan and retry IDs.

- **Code contributed:** [RepoSense code dashboard][reposense]. My area comprises
  `Commitment`, `CommitmentService` and `CommandDispatcher`, plus their tests.

- **Enhancements to existing features:**
  - **Calendar corrections:** Fixed edits after skipped occurrences. Weekday and
    date-range changes discard obsolete cancellations, retain applicable skips
    and continue to reject real overlaps. Regression coverage checks changed
    weekdays, shortened bounds, rollback and saved reloads.
  - **Regression coverage:** Added [service tests][service-tests] and
    [model tests][model-tests] for validation, adjacency, conflict ordering,
    date bounds, skips, one-time events, counter exhaustion, immutability and
    range expansion. [Dispatcher tests][dispatcher-tests] check batch
    prepare/save/publish order, failed-save retries, plan/ID preservation,
    unrelated data, later-segment rollback and insufficient ID capacity.
  - **Integration testing:** Added fixed-date tests showing that task and
    commitment durations survive saving and partial allocation ([PR #30][pr30]).
    Packaged scenarios cover edits and unavailable ranges across restarts,
    independent deletion, invalid input, midnight boundaries and leap day.

- **Documentation:**
  - **User Guide:** Maintained the [commitment chapter][ug-commitments], including
    adding, editing, skipping, deleting and one-time events, plus
    [unavailable study periods][ug-unavailable] and
    [blocking multiple dates][ug-ranges].
  - **Developer Guide:** Wrote [commitment/dispatch design][dg-design],
    the [implementation walkthrough][dg-implementation],
    [transaction verification][dg-verification] and
    [manual tests][dg-manual], including the command index. Added the
    [class diagram][class-diagram] and [edit sequence][sequence-diagram]
    with PlantUML sources and rendered SVGs. Explained the one-time-event
    representation in the [design considerations][dg-design-considerations].

- **Team-based tasks:**
  - Reviewed and approved text rendering ([PR #22][pr22]), task operations
    ([PR #25][pr25]) and console/dispatch integration ([PR #29][pr29]).

<!-- @@author -->

[pr23]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/23
[pr26]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/26
[pr34]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/34
[pr70]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/70
[pr71]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/71
[pr78]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/78
[pr79]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/79
[pr80]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/80
[pr86]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/86
[pr126]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/126
[reposense]: https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=Darren50&breakdown=true
[service-tests]: ../../src/test/java/scheduleflow/commitment/CommitmentServiceTest.java
[model-tests]: ../../src/test/java/scheduleflow/model/CommitmentTest.java
[dispatcher-tests]: ../../src/test/java/scheduleflow/cli/CommandDispatcherTest.java
[pr30]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/30
[ug-commitments]: ../UserGuide.md#commitments
[ug-unavailable]: ../UserGuide.md#marking-a-study-period-unavailable
[ug-ranges]: ../UserGuide.md#blocking-multiple-dates
[dg-design]: ../DeveloperGuide.md#commitments-and-command-execution
[dg-implementation]: ../DeveloperGuide.md#dated-commitments-and-save-before-publication-dispatch
[dg-verification]: ../DeveloperGuide.md#commitmentdispatch-transaction-verification
[dg-manual]: ../DeveloperGuide.md#commitments-and-unavailable-periods
[class-diagram]: ../images/diagrams/darren-commitment-dispatch-classes.svg
[sequence-diagram]: ../images/diagrams/darren-commitment-edit.svg
[dg-design-considerations]: ../DeveloperGuide.md#commitment-design-considerations
[pr22]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/22
[pr25]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/25
[pr29]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/29
