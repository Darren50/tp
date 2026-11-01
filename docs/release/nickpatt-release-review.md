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
Impossible frozen-plan restoration therefore remains an integration defect until
the final fix is tested. Several console-guide paragraphs still describe range
dispatch as deferred even though the six range scenarios are active. Hosted
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
  `build/ui-transcripts/release-2026-11-01T01-42-21.539Z-22136.txt`.
- Default UI runner: 34 passed, zero failed/blocked/planned in the fresh run:
  `build/ui-transcripts/ui-2026-11-01T01-41-32.420Z-28532.txt`.
- Earlier baseline build: 81 temporary-directory access failures in the sandbox;
  resolved by the local temporary-directory settings, without a project change.
- Earlier UI run: UI-034 timed out at first save in the sandbox;
  `build/ui-transcripts/ui-2026-11-01T01-40-53.565Z-25532.txt`.
  The fresh successful run used automatic escalation, unchanged expectations
  and the same five-second timeout. Neither failed run is counted as a pass.

Generated verification logs/transcripts remain under ignored `build/`.
<!-- @@author -->
