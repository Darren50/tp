# Gabriel Yu's Project Portfolio Page

### Project: ScheduleFlow

<!-- @@author GabrielYuSP -->

## Overview

ScheduleFlow is a Java 25 command-line application that helps university students
manage coursework, fixed commitments and study time. It generates study plans in
30-minute slots, explains work that cannot fit before its deadline, and saves
records and frozen plans for later sessions.

## Summary of Contributions

Given below are my contributions to the project.

- **New Feature: Added deadline-aware task planning**
  ([planner implementation][planner-commit]).
  - **What it does:** Assigns unfinished tasks to eligible half-hour slots in
    deadline order, respecting study windows, fixed commitments and deadlines.
    Reports work that cannot fit.
  - **Justification:** Students need to see realistic capacity before deadlines
    rather than a schedule that silently places work too late.
  - **Highlights:** Preserves saved estimates and distinguishes exact work from
    reserved capacity. Handles overdue, out-of-horizon and partially allocated
    tasks. Deterministic tests check allocation order, cutoff rounding, deadline
    boundaries, commitment blocking and conservation of remaining work.

- **New Feature: Added configurable study hours daily schedules and breaks**
  ([study hours][hours-commit], [daily projection][schedule-commit],
  [PR #81][pr81]).
  - **What it does:** Supports default study hours, weekday settings and date
    overrides. Projects saved plans into busy, task, break, unplanned and free
    entries, reserving breaks after continuous work under a configured policy.
  - **Justification:** A useful plan reflects when a student is available and
    makes required rest time visible.
  - **Highlights:** Date overrides take precedence over weekday settings and
    defaults. Schedule projection uses the frozen plan rather than regenerating
    it. Fixed-time tests cover hours precedence, labels, break placement and
    continuous-work limits.

- **Code contributed:** [RepoSense code dashboard][reposense]. My area covers
  deadline-aware planning, daily schedule projection, study hours and work/break
  policies through `EarliestDeadlinePlanner`, `ScheduleService`, `StudyHours`,
  `StudyWindow` and `StudyPolicy`, plus their tests.

- **Enhancements to existing features:**
  - **Frozen-plan validation:** Added [`Planner.validate(Plan)`][planner]
    to reject impossible allocations without changing estimates or recreating
    the schedule ([PR #127][pr127]). The generator validates every result before
    returning it. Validation uses frozen time/source, long work totals, original
    recurrence releases, effective hours and half-open intervals. It accepts
    feasible alternative placements and legacy breaks without requiring the
    greedy planner's exact result.
  - **Unavailable study periods:** Added fixed-time planner and projection tests
    for blocking time with one-time commitments ([PR #82][pr82],
    [issue #48][issue48]). Covered replanning, reduced deadline capacity and
    deleting/restoring a block; documented the [acceptance cases][ui-plan].
  - **Regression coverage:** [EarliestDeadlinePlannerTest][planner-tests] checks
    real generated allocations and their validation.
    [PlannerTest][validation-tests] covers partial work, overlap, accounting and
    time boundaries. Fixed-time tests keep these cases independent of the clock.

- **Documentation:**
  - **User Guide:** Explained saved plans, partial allocations, exact work versus
    reserved capacity, recurrence release timing and insufficient study time.
    See [study plans][ug-plans], [unallocated work][ug-unallocated],
    [weekly tasks][ug-recurring] and [troubleshooting][ug-troubleshooting].
  - **Developer Guide:** Documented [planning/projection boundaries][dg-design],
    the deadline-constrained example, [fixed-time verification][dg-tests], manual
    testing and [planning terminology][dg-glossary] ([PR #102][pr102]). Added
    the [planning class diagram][class-diagram] and
    [planning sequence][sequence-diagram]; updated both views for validation.
    Documented the [multi-date unavailable contract][dg-unavailable].

- **Team-based tasks:**
  - Integrated deadline allocation with study hours and continuous-work policy,
    and verified interactions with fixed commitments ([PR #81][pr81],
    [PR #82][pr82]).

<!-- @@author -->

[planner-commit]: https://github.com/AY2627S1-CS2113-T10-1/tp/commit/1644a96
[hours-commit]: https://github.com/AY2627S1-CS2113-T10-1/tp/commit/9216291
[schedule-commit]: https://github.com/AY2627S1-CS2113-T10-1/tp/commit/dd5910c
[pr81]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/81
[reposense]: https://nus-cs2113-ay2627-s1.github.io/tp-dashboard/?search=GabrielYuSP&breakdown=true
[planner]: https://github.com/AY2627S1-CS2113-T10-1/tp/blob/master/src/main/java/scheduleflow/planning/Planner.java
[pr127]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/127
[pr82]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/82
[issue48]: https://github.com/AY2627S1-CS2113-T10-1/tp/issues/48
[ui-plan]: https://github.com/AY2627S1-CS2113-T10-1/tp/blob/master/test/ui-test-plan.md
[planner-tests]: https://github.com/AY2627S1-CS2113-T10-1/tp/blob/master/src/test/java/scheduleflow/planning/EarliestDeadlinePlannerTest.java
[validation-tests]: https://github.com/AY2627S1-CS2113-T10-1/tp/blob/master/src/test/java/scheduleflow/planning/PlannerTest.java
[ug-plans]: ../UserGuide.md#study-plans
[ug-unallocated]: ../UserGuide.md#unallocated-work
[ug-recurring]: ../UserGuide.md#weekly-recurring-tasks
[ug-troubleshooting]: ../UserGuide.md#what-if-my-work-does-not-fit-before-its-deadline
[dg-design]: ../DeveloperGuide.md#planning-schedule-projection-and-recurrence
[dg-tests]: ../DeveloperGuide.md#fixed-time-planning-and-recurrence-verification
[dg-glossary]: ../DeveloperGuide.md#appendix-d-glossary
[pr102]: https://github.com/AY2627S1-CS2113-T10-1/tp/pull/102
[class-diagram]: ../images/diagrams/gabriel-planning-classes.svg
[sequence-diagram]: ../images/diagrams/gabriel-planning-sequence.svg
[dg-unavailable]: ../DeveloperGuide.md#multi-date-unavailable-periods
