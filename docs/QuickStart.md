---
title: ScheduleFlow Quick Start
description: Set up Java 25, launch ScheduleFlow, and create your first study plan.
---

<!-- @@author ernestgoh-god -->
# Quick start

[Home](README.md) / Quick start

ScheduleFlow runs locally in your terminal. You need **Java 25**; no account
or network connection is needed to manage your coursework after installation.

## 1. Get the application

Check your Java version:

```shell
java -version
```

Download `scheduleflow.jar` from the
[ScheduleFlow releases page](https://github.com/AY2627S1-CS2113-T10-1/tp/releases)
and put it in a dedicated folder. Use the **v2.1 JAR** with the v2.1 User Guide.
If v2.1 has not been published yet, use the source-build steps below to try
the current checkout. Older release JARs may not support every documented command.

## 2. Launch ScheduleFlow

Open a terminal in the folder containing the JAR and run:

```shell
java -ea -jar scheduleflow.jar
```

`-ea` enables internal assertions. Enter commands after `scheduleflow>`;
do not type the prompt itself.

## 3. Make your first plan

Enter each command on its own line. Replace `2026-11-20` with a future date
within the [planning range](UserGuide.md#planning-hours-and-date-range).

```text
task add n/CS2113 draft due/2026-11-20 1800 d/45
commitment add n/CS2113 lecture day/MON start/1000 d/120
plan
schedule today
```

The plan depends on the current time, study hours, and commitments. Work is
reserved in 30-minute slots; work that cannot fit is reported explicitly.
Use `help` for command syntax or the
[User Guide](UserGuide.md) for examples and troubleshooting.

## 4. Return to your work

Enter `exit` to close the application. Successful changes and generated plans
save automatically in `data/scheduleflow.txt`, relative to the folder you
launched from. Relaunch from **the same folder** to restore your saved work.

Use `task list`, `commitment list`, and `schedule today` to view it. To view a
previous date, use `schedule YYYY-MM-DD`. After changing source data, run
`plan` again. See [saving and restoring data](UserGuide.md#saving-and-restoring-data)
for backups and recovery.

## Building from source

For development setup, see the [Developer Guide](DeveloperGuide.md#setting-up-getting-started).
The following commands are run from the repository root.
<!-- @@author -->

<!-- @@author nickpatt -->
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
<!-- @@author -->

<!-- @@author ernestgoh-god -->
Copy that JAR into a dedicated folder, then follow the launch steps above.
Developer test commands and project structure are documented in the
[Developer Guide](DeveloperGuide.md#build-packaging-and-console-verification)
and [repository README](https://github.com/AY2627S1-CS2113-T10-1/tp#readme).

[Back to home](README.md) &middot; [Full User Guide](UserGuide.md)
<!-- @@author -->
