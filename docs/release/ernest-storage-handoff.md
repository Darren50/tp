# Ernest's storage release handoff

Owner: `ernestgoh-god`. Baseline: `8911912` (upstream/master fetched on
2026-10-31). This is a member handoff, not release certification.

## Completed independent work

`FileStorage.FileOperations` supplies package-private creation/write/atomic-move/
cleanup fault injection. Normal operations still use `Files`. Real temporary-file
tests preserve the previous target's bytes, snapshot and frozen plan after
partial writes, unsupported atomic moves and other replacement errors. Creation
failure skips cleanup; cleanup failure retains the original cause and adds a
suppressed error. No dependency, global provider, wire-format change, public API
change or non-atomic fallback is introduced.

These are controlled failures on Windows. Actual provider rejection of atomic
moves, permission denial, other operating systems and power-loss durability need
their own evidence. Existing directory-target tests provide a separate real
filesystem replacement failure; dispatcher fake-storage tests cover publication.

## Completed storage integration

<!-- @@author ernestgoh-god -->
The final release fixes connect Gabriel's tested Planner.validate(Plan) boundary
to PlanCodec before encoding and after decoding, preserving StorageException
causes. StateCodec plan decoding follows the same path. Tests reject excessive,
missing and duplicate work, overlap, deadline and cutoff violations; valid
alternative allocations and supported formats remain frozen on read.
FileStorage regressions preserve invalid input bytes in both read paths and
reject invalid saves before creating directories or replacing a previous plan.
Storage diagram sources and SVG/PNG renders now show this dependency. The API,
file versions, single-writer model and atomic replacement requirement remain.
<!-- @@author -->

## Packaged corrupt-plan example for Ka How

Put this exact UTF-8 text in an isolated launch folder's
`data/scheduleflow.txt` (retain its final newline):

```text
SCHEDULEFLOW|6
NEXT|2|1|1
TASK2|1|2026-10-06T18:00|30|NORMAL|Draft
PLAN|2026-10-05T07:00|2027-10-05T00:00|1|0|0
SESSION|1|2026-10-05T08:00|2026-10-05T09:00|60
```

The task ID, row counts, horizon and interval are structurally valid, but the
session credits 60 minutes against a 30-minute source task. After integration,
UI-035 and the packaged release regression require exit 1, no prompt, quiet
stderr and this exact error:

```text
Error: Invalid saved plan: Scheduled work plus remainder must equal source remaining work.
```

MainTest and the packaged release test verify byte preservation; UI-031/UI-032
retain valid frozen restarts. Final evidence is in the task/parser release review.
This is local Windows evidence, not hosted CI or power-loss certification.

## Historical other-owner documentation handoff

These were the original independent-seam requests. The completed integration
and final report above supersede their pending statuses.

The following owner updates remain pending; this note supplies wording/evidence
without editing their assigned blocks or sending messages on their behalf.

- **Ka How, UI plan / packaged acceptance:** replace the obsolete statement that
  injected temporary-write/unsupported-atomic-move checks are unexecuted with:
  “FileStorageTest injects creation, partial-write, unsupported-atomic-move,
  replacement and cleanup failures through a package-private collaborator while
  using real temporary files. Original target bytes and frozen plans are
  preserved; these checks do not certify actual provider failures or power-loss
  durability.” The plan was reviewed; its fixtures/output remain unchanged.
- **Ka How, final guide PDFs:** regenerate the guide PDFs after all owners finish
  their Markdown/diagram changes. This member change does not update release PDFs.
- **Nickhil, Appendix B:** no user-story status becomes complete from these
  independent failure tests; impossible-plan validation remains pending.
- **Gabriel, glossary/planning docs:** the save seam changes no domain term or
  allocation rule. Supply the tested validator API/exception contract for the
  pending integration.

## Historical independent-seam review

The following review and 2026-10-31 evidence describe the original independent
increment before validator integration. Superseding integration results and
current artifact evidence are in [the final release review](nickpatt-release-review.md).

- Updated Ernest's DG persistence design, atomic-save implementation, storage
  verification, NFR-3 evidence and manual-recovery limits; updated the UG saving
  chapter and own PPP. Existing acknowledgements still cover all reused tools;
  no new runtime dependency or adapted third-party implementation was added.
- Reviewed the assigned UG Quick start, Recording duration, Listing tasks,
  Planning partway through the day, Why did my schedule disappear?, and
  Cannot load my data FAQ. Their commands, restoration and recovery behavior are
  unchanged by this save-only seam, so their text remains unchanged.
- Updated the storage class and atomic-save sequence sources and matching
  SVG/PNG assets to show the operations seam and cleanup order. No nonexistent
  validator dependency is drawn; that update is pending the API itself.
- Only FileStorage.java and FileStorageTest.java change Java. Both retain exactly
  one `ernestgoh-god` start before the package and one bare end after the class.
  No other contributor's code or historical authorship is reassigned.

## Local verification (2026-10-31)

- Java shell and Gradle launcher/daemon: Java 25.0.3; Gradle 9.6.1; Node 24.15.0.
- `gradlew.bat test --tests scheduleflow.storage.FileStorageTest`: 26 passed.
- `gradlew.bat build`: 449 JUnit tests, zero failures/errors/skips; Checkstyle
  and packaging passed. No application dependency/configuration was changed.
- `node --test test/ui-runner.test.mjs`: all 12 runner tests passed.
- Local UI skill: all 28 active cases passed, zero failed/blocked. Exact evidence:
  `C:\Users\ernes\Documents\GitHub\tp\build\ui-transcripts\ui-2026-10-31T06-44-24.341Z-72444.txt`.
- `node --test test/release.test.mjs`: both checks passed: all 28 maintained
  scenarios with assertions enabled, and exact-JAR normal ZIP/native launcher
  startup/exit. Exact evidence:
  `C:\Users\ernes\Documents\GitHub\tp\build\ui-transcripts\release-2026-10-31T06-52-06.995Z-68284.txt`.
  Earlier attempts exposed environment setup failures: no `jar` on PATH, then
  launcher command echo from a nonempty `DEBUG`. The final fresh run set
  `JAVA_HOME=C:\Program Files\Java\jdk-25.0.3` and removed `DEBUG` only in its
  process environment. No checks, launcher code or expected output were changed.
- Both diagram sources rendered with the repository's note-arrow exporter;
  SVG/PNG display assets were visually inspected. Guide links resolve locally.
- The impossible-plan diagnostic reached a prompt and exited normally after
  `exit`, with unchanged bytes; this confirms the missing rejection boundary,
  not a passing acceptance result.
- Six unavailable-range cases (UI-022 through UI-027) remain separately planned,
  unexecuted and owned by Darren/Ka How. They are not included in the 28 passes.
- Hosted CI, macOS/Linux runs, actual unsupported providers/permission denial
  and power-loss recovery were not tested. No whole-release sign-off is claimed.
