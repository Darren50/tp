<!-- @@author nickpatt -->
# Task/parser release review

Reviewed on 2026-11-01, starting from `a99c040` on `bugfix`, with a clean index
and working tree. This review implements nickpatt's P0/P1 assignment in the
supplied release-improvements plan and records the combined release checks.
Maintenance authorship follows the whole-file and DG/UG ownership allocations.

## Contract-to-test mapping

| Contract | Evidence under `src/test/java/scheduleflow` |
| --- | --- |
| Help, exit, task/commitment add/list/delete, plan, both schedule forms | `cli/CommandParserTest.parse_supportedCommands_returnsEveryCommandType` |
| Task edit, reopen, completion/completed list, remaining, both progress forms | `parse_taskEdit_preservesAllThreeSubsets`, `parse_reopen_acceptsExactMinutesSpacingAndIntegerBoundaries`, `parse_completionAndCompletedView_acceptsSpacingAndLargestIdentifier`, `parse_taskRemaining_acceptsPositiveWholeMinuteTokens`, `parse_taskProgress_acceptsBothReportedWorkForms` |
| Weekly recurrence add/list/delete, hours query/set/reset, policy set/show/reset, commitment edit/skip/bounded/one-time | `parse_recurrence_acceptsBoundedUnboundedAndIndependentIds`, `parse_hoursCommands_supportsWeekdaysDatesAndResets`, `parse_policy_preservesDurationsAndRejectsMalformedOrOverflowingFields`, `parse_commitmentEdit_allFieldSubsetsPreserveOmission`, `parse_commitmentSkip_acceptsStrictDateAndRejectsMalformedFields`, `parse_commitmentDateRanges_preservesInclusiveDatesAndRejectsIncompleteSyntax`, `parse_oneTimeCommitment_mapsDateToItsWeekdayAndSingleDateRange` |
| Both unavailable transports, mode-specific hints, reversed ranges left to domain | `parse_unavailableContinuous_preservesNamesDatesAndTimesWithoutDomainValidation`, `parse_unavailableDaily_preservesLeapAndBoundaryDatesWithoutDomainValidation`, `parse_unavailable_rejectsMissingReorderedDuplicateMixedAndExtraFields`, `parse_unavailable_rejectsInvalidCalendarDatesYearsAndTimesWithModeUsage`, `parse_unavailable_embeddedControlsRetainSelectedModeUsage` |
| Full input, strict dates/times, ASCII IDs/overflow, duplicate/reordered/unknown fields, controls versus outer trimming | `parse_invalidDates_rejectsRolloverAndWrongShape`, `parse_invalidTimes_rejectsRolloverAndWrongShape`, `parse_invalidIntegers_rejectsSignsZeroPaddingOverflowAndNonAsciiDigits`, malformed-field/invalid-ID/trailing-input tests, `parse_embeddedControlsAndNonSpaceSeparators_rejectsInput`, `parse_extraSpaces_preservesInternalNameSpacing` |
| Parser/domain separation, unresolved today, excluded console APIs | `parse_domainValues_defersBusinessValidation`, `parse_scheduleToday_keepsDateUnresolvedAcrossCalls`, new `parse_domainOnlyTaskApis_rejectsUnreleasedConsoleForms` |
| Exact 1/45/MAX_VALUE work, overflow-safe percentage flooring, lifecycle restrictions/no-op identity | `task/TaskServiceTest.add_wholeMinuteBoundariesAndAbsentDeadline_preservesHoursAndCounter`, `progress_percentagesFloorAndLargeValues_creditExactReportedWork`, `lifecycle_invalidInputs_rejectWithoutChangingSource`, `lifecycle_noOps_returnIdenticalSource`; `model/TaskTest.constructor_wholeMinutesOptionalDeadlineAndCompletion_preservesValues` |
| Position, priority, provenance/release, settings and counters; deleted templates | `lifecycle_mixedSnapshot_preservesMetadataPositionHoursAndExhaustedCounters`, `task/TaskRecurrenceTest.lifecycle_generatedTask_preservesPriorityProvenanceAndIdentityNoOps`, new `lifecycle_deletedTemplate_retainsIndependentTaskAndAllSettings` |
| Edit transport permits contradictory values; service rejects them | `task/TaskEditTest.constructor_values_retainsSuppliedImmutableDetails`, `TaskServiceTest.edit_invalidFullEdits_rejectsAtomicallyAndAllowsRetry` |
| Shared name/aligned commitment-duration validation | `common/InputRulesTest`, `common/ValidationExceptionTest`, `model/TaskTest` |

The audit found missing combined regression coverage, not a task/parser production
defect. Public APIs and console grammar/output remain unchanged.

## Documentation and ownership

- Updated the documentation entry, Nickhil's portfolio and AboutUs link.
- DG: reviewed D2/I2/W2/E2 and Appendix B; corrected the stale task revision and
  range story status, documented deleted-template lifecycle coverage/manual steps.
- UG: reviewed all ten assigned blocks (Tasks intro, progress, add, edit,
  complete, reopen, delete and three task FAQs). Updated the candidate note and
  retained-template behavior; other assigned examples/rules remain accurate.
- Both Nickhil diagram source/SVG/PNG sets were reviewed against the current
  methods and positive-progress call order. No API or depicted call changed;
  they remain accurate and need no rerender.
- Both changed Java tests retain exactly one `nickpatt` start before `package`
  and one bare end after the final brace. No production ownership is transferred.

## Release review boundaries

The final check must distinguish passing task/parser checks from release
certification. At the starting revision, Gabriel's pure validator and Ernest's
filesystem-failure seam/tests exist, but PlanCodec does not invoke the validator.
The final integration fixes below resolve that restoration defect. Several
console-guide paragraphs also described range dispatch as deferred even though
the six range scenarios were active; those are now corrected. Hosted
three-OS CI and actual provider/power-loss behavior cannot be certified locally.

## Nickhil increment verification

- Java and Gradle both reported Microsoft Java 25.0.4.1; Node was v24.19.0.
- `gradlew.bat -I .gradle/console-verification.init.gradle build`: passed,
  including the two added regressions and both Checkstyle tasks. The ignored
  init script sets `java.io.tmpdir` to `build/verification-tmp`; `TEMP`/`TMP`
  use the same location, and `GRADLE_USER_HOME` uses `.gradle/stage1-user-home`.
- `node --test test/ui-runner.test.mjs`: 12 passed.
- `node --test test/release.test.mjs`: both checks passed, including all 34
  assertion-enabled scenarios and the native Windows distribution launcher:
  `D:\NUS\Y2S1\CS2113\tp\build\ui-transcripts\release-2026-11-01T01-42-21.539Z-22136.txt`.
- Default UI runner: 34 passed, zero failed/blocked/planned in the fresh run:
  `D:\NUS\Y2S1\CS2113\tp\build\ui-transcripts\ui-2026-11-01T01-41-32.420Z-28532.txt`.
- Earlier baseline build: 81 temporary-directory access failures in the sandbox;
  resolved by the local temporary-directory settings, without a project change.
- Earlier UI run: UI-034 timed out at first save in the sandbox;
  `D:\NUS\Y2S1\CS2113\tp\build\ui-transcripts\ui-2026-11-01T01-40-53.565Z-25532.txt`.
  The fresh successful run used automatic escalation, unchanged expectations
  and the same five-second timeout. Neither failed run is counted as a pass.

Generated verification logs/transcripts remain under ignored `build/`.
<!-- @@author -->

<!-- @@author ka-how -->
## Final integration review

The user additionally requested fixes found by the full release-improvements
check, retaining each component's existing ownership. The resulting fixes are:

| Release requirement | Final result |
| --- | --- |
| Nickhil's task/parser P0/P1 | Completed in local commit `d6d1ea7`, authored by nickpatt; contract mapping above. |
| Frozen plan validation | Ernest's PlanCodec now calls Gabriel's validator before encoding and after decoding, preserving causes; StateCodec and both FileStorage read paths reject the whole impossible file. No format/API migration or replanning. |
| Actual storage failure paths | Existing Ernest-owned operation-seam tests cover creation, partial writes, unsupported moves, other replacement errors and suppressed cleanup failures. New regressions cover invalid allocation before filesystem changes and preserved old bytes/plan. |
| Batch commands | Existing Darren-owned transactions and active UI-022..027 pass; both modes are now included in Ka How's help and UG summary/navigation. |
| Packaged corrupt startup | UI-035, MainTest and the additional release test reject excessive work with status 1, no prompt and quiet stderr. MainTest/release also preserve bytes. Valid frozen restarts still pass. |
| CI and failed console output | Existing Java 25/Node three-OS workflow includes build, runner, UI and release checks with artifact retention. Existing failing-writer/App/Main tests pass. Hosted runs were not triggered. |
| DG/UG/PPPs/diagrams | Corrected affected allocated blocks and stale integration claims; reviewed all five PPP links. Updated Ernest's class/sequence sources and SVG/PNG pairs and inspected them. Nickhil's diagrams need no change. Other owners' diagrams retain unchanged APIs/calls. |
| StudyWindow | Gabriel-owned source is unchanged: no demonstrated defect; both existing boundary/invalid-endpoint tests pass. |
| PDF exports | Updated DG (44 pages, 11 figures) and UG (23 pages), rendered every page, inspected page contacts and changed pages/diagrams. All 717 DG and 470 UG text blocks are retained; no broken internal anchors; 13 portable cross-guide links verified. |
| Attribution | All eight changed Java files across both increments have exactly one canonical owner start before package and one bare end after the final brace: two nickpatt tests, three Ernest storage files, three Ka How presentation/process files. Guide edits follow the supplied maps; no public API is reassigned. |

Final-state checks on Windows with Java 25.0.4.1 / Gradle 9.6.1 / Node 24.19.0:

- Full Java build, JUnit and Checkstyle: **480 passed, 0 failures/errors/skips**.
  Command: `gradlew.bat -I .gradle/console-verification.init.gradle build`.
  Same ignored cache/tmp settings as above; no dependency/configuration workaround.
- Runner self-tests: **12 passed**.
- Default UI runner: **35 passed, 0 failed/blocked/planned**, unique active IDs.
  Exact transcript:
  `D:\NUS\Y2S1\CS2113\tp\build\ui-transcripts\ui-2026-11-01T01-53-03.677Z-7084.txt`.
- Packaged release checks: **3 passed**, including every scenario with `-ea`,
  exact-JAR normal distribution and native Windows launcher, impossible-startup
  rejection and preserved bytes. Exact transcript:
  `D:\NUS\Y2S1\CS2113\tp\build\ui-transcripts\release-2026-11-01T01-53-34.138Z-21116.txt`.
- Changed-file ownership, 131 relative documentation/asset links and whitespace:
  passed. The final report adds no source changes after the verified build.

Candidate artifacts from `d6d1ea7` plus the final integration change containing
this report (all commits remain local):

| Artifact | SHA-256 |
| --- | --- |
| `build/libs/scheduleflow.jar` | `400d2dac6eb0f7e83a7ef4d33485f9ee3ce50b52ff4558f48ec80b2d055c7478` |
| `build/distributions/tp.zip` (normal distribution) | `b47dee63d36a828858e6effd23616ebc7896734090abc20ced5263203bb2cfac` |

No active local acceptance case remains blocked. Actual Ubuntu/macOS hosted CI,
real provider denial/unsupported-move behavior and power-loss recovery remain
unexecuted; controlled faults do not certify them. One writer per file and
atomic replacement remain required. No push, publication, tag or issue creation
was performed. Review historical evidence as historical; final counts above
come from the current JUnit reports/fixtures rather than the original handoff.
<!-- @@author -->
