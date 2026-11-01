// @@author nickpatt
package scheduleflow.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import scheduleflow.common.ValidationException;
import scheduleflow.model.RecurringTask;
import scheduleflow.model.Snapshot;
import scheduleflow.model.StudyHours;
import scheduleflow.model.StudyWindow;
import scheduleflow.model.Task;
import scheduleflow.model.TaskOccurrence;
import scheduleflow.model.TaskPriority;
import scheduleflow.planning.StudyPolicy;

/** Verifies task mutations preserve recurrence data and reject inconsistent snapshot fixtures. */
class TaskRecurrenceTest {
    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 1, 7, 0);

    @Test
    void mutations_generatedTask_preserveOriginalReleaseAndAllRecurrence() {
        Snapshot source = new RecurringTaskService().add(Snapshot.empty(), "Quiz", NOW.plusDays(1), 30,
                Optional.empty(), NOW);
        TaskService service = new TaskService();
        TaskOccurrence occurrence = source.tasks().getFirst().occurrence().orElseThrow();
        Snapshot edited = service.edit(source, 1, new TaskEdit(Optional.of("Renamed"),
                Optional.of(NOW.plusDays(3)), false), NOW);
        Snapshot revised = service.reviseRemaining(edited, 1, 60);
        Snapshot added = service.add(revised, "Ordinary", NOW.plusDays(2), 30, NOW);
        Snapshot deleted = service.delete(added, added.tasks().getLast().id());
        for (Snapshot candidate : List.of(edited, revised, added, deleted,
                source.withStudyHours(source.studyHours()), source.withCommitments(List.of(), 1))) {
            assertEquals(source.recurringTasks(), candidate.recurringTasks());
            assertEquals(source.nextRecurringTaskId(), candidate.nextRecurringTaskId());
            assertEquals(occurrence, candidate.tasks().getFirst().occurrence().orElseThrow());
            assertEquals(NOW.plusDays(1).minusWeeks(1), occurrence.release());
        }
        assertSame(revised, service.reviseRemaining(revised, 1, 60));
        assertSame(edited, service.edit(edited, 1,
                new TaskEdit(Optional.of("Renamed"), Optional.empty(), false), NOW));
    }

    @Test
    void lifecycle_generatedTask_preservesPriorityProvenanceAndIdentityNoOps() {
        Snapshot source = new RecurringTaskService().add(Snapshot.empty(), "Quiz", NOW.plusDays(1), 30,
                Optional.empty(), NOW);
        TaskService service = new TaskService();
        Snapshot prioritized = service.setPriority(source, 1, TaskPriority.HIGH);
        Snapshot worked = service.recordWork(prioritized, 1, 7);
        Snapshot credited = service.recordSessionProgress(worked, 1, 3, 50);
        Snapshot revised = service.reviseRemaining(credited, 1, 19);
        Snapshot completed = service.complete(revised, 1);
        Snapshot cleared = service.edit(completed, 1,
                new TaskEdit(Optional.empty(), Optional.empty(), true), NOW);
        Snapshot reopened = service.reopen(cleared, 1, 17);
        Snapshot undatedAdded = service.addWithoutDeadline(reopened, "Undated", 11);
        for (Snapshot candidate : List.of(prioritized, worked, credited, revised, completed, cleared,
                reopened, undatedAdded)) {
            assertEquals(source.recurringTasks(), candidate.recurringTasks());
            assertEquals(source.nextRecurringTaskId(), candidate.nextRecurringTaskId());
            assertEquals(source.tasks().getFirst().occurrence(), candidate.tasks().getFirst().occurrence());
            assertEquals(TaskPriority.HIGH, candidate.tasks().getFirst().priority());
        }
        assertEquals(22, credited.tasks().getFirst().remainingMinutes());
        assertEquals(0, completed.tasks().getFirst().remainingMinutes());
        assertEquals(Optional.empty(), reopened.tasks().getFirst().deadline());
        assertEquals(17, reopened.tasks().getFirst().remainingMinutes());
        assertSame(prioritized, service.setPriority(prioritized, 1, TaskPriority.HIGH));
        assertSame(prioritized, service.recordWork(prioritized, 1, 0));
        assertSame(prioritized, service.recordSessionProgress(prioritized, 1, 1, 99));
        assertSame(completed, service.complete(completed, 1));
        assertSame(cleared, service.edit(cleared, 1,
                new TaskEdit(Optional.empty(), Optional.empty(), true), NOW));
        assertSame(cleared, new RecurringTaskService().materialize(cleared, NOW));
        assertThrows(ValidationException.class, () -> service.recordWork(completed, 1, 0));
        assertThrows(ValidationException.class, () -> service.reviseRemaining(completed, 1, 19));
        assertThrows(ValidationException.class, () -> service.reopen(reopened, 1, 19));
        assertThrows(NullPointerException.class, () -> service.setPriority(source, 1, null));
        assertThrows(ValidationException.class, () -> service.setPriority(source, 0, TaskPriority.HIGH));
    }

    @Test
    void lifecycle_deletedTemplate_retainsIndependentTaskAndAllSettings() {
        TaskService service = new TaskService();
        RecurringTaskService recurrence = new RecurringTaskService();
        LocalDateTime due = NOW.plusDays(1);
        Snapshot configured = Snapshot.empty().withStudyHours(new StudyHours(Map.of(
                "monday", new StudyWindow(LocalTime.of(9, 0), LocalTime.of(18, 0)),
                "2026-10-02", new StudyWindow(LocalTime.of(10, 0), LocalTime.of(17, 0)))))
                .withStudyPolicy(Optional.of(new StudyPolicy(60, 30)));
        Snapshot ordinary = service.add(configured, "First", due, 45, NOW);
        Snapshot generated = recurrence.add(ordinary, "Quiz", due, 30, Optional.of(due.toLocalDate()), NOW);
        Snapshot populated = service.add(service.setPriority(generated, 2, TaskPriority.HIGH),
                "Last", due, 1, NOW);
        Snapshot source = recurrence.delete(populated, 1);
        Task retained = source.tasks().get(1);

        Snapshot worked = service.recordWork(source, 2, 7);
        Snapshot credited = service.recordSessionProgress(worked, 2, 3, 50);
        Snapshot completed = service.complete(credited, 2);
        Snapshot reopened = service.reopen(completed, 2, 45);
        Snapshot edited = service.edit(reopened, 2,
                new TaskEdit(Optional.of("Independent quiz"), Optional.of(due.plusDays(1)), false), NOW);
        List<Snapshot> candidates = List.of(worked, credited, completed, reopened, edited);
        List<Integer> remaining = List.of(23, 22, 0, 45, 45);
        for (int index = 0; index < candidates.size(); index++) {
            boolean isEdited = index == candidates.size() - 1;
            Task expected = new Task(2, isEdited ? "Independent quiz" : retained.name(),
                    isEdited ? Optional.of(due.plusDays(1)) : retained.deadline(), remaining.get(index),
                    retained.priority(), retained.occurrence());
            assertEquals(source.withTasks(List.of(source.tasks().getFirst(), expected,
                    source.tasks().getLast()), source.nextTaskId()), candidates.get(index));
        }
        assertEquals(List.of(), source.recurringTasks());
        assertEquals(30, source.tasks().get(1).remainingMinutes());
        assertEquals(due.minusWeeks(1), retained.occurrence().orElseThrow().release());
        assertSame(edited, recurrence.materialize(edited, NOW.plusWeeks(2)));
        assertSame(source, service.recordWork(source, 2, 0));
        assertSame(completed, service.complete(completed, 2));
        assertThrows(ValidationException.class, () -> service.recordWork(completed, 2, 0));
        assertThrows(ValidationException.class, () -> service.reopen(edited, 2, 1));
    }

    @Test
    void snapshot_invalidRecurrence_rejectsButAllowsDeletedTemplateProvenance() {
        LocalDateTime due = NOW.plusDays(1);
        Task task = new Task(1, "Quiz", due, 30, Optional.of(new TaskOccurrence(1, due)));
        RecurringTask processed = new RecurringTask(1, "Quiz", due, Optional.empty(), 30, Optional.of(due));
        Snapshot valid = state(List.of(task), List.of(processed), 2);
        assertEquals(List.of(task), state(List.of(task), List.of(), 2).tasks());
        assertThrows(UnsupportedOperationException.class, () -> valid.recurringTasks().clear());
        assertThrows(ValidationException.class, () -> state(List.of(task), List.of(processed), 1));
        assertThrows(ValidationException.class, () -> state(List.of(), List.of(processed, processed), 2));
        assertThrows(ValidationException.class, () -> state(List.of(), null, 2));
        assertThrows(ValidationException.class, () -> state(List.of(), Arrays.asList((RecurringTask) null), 2));
        assertThrows(ValidationException.class, () -> state(List.of(task,
                new Task(2, "Duplicate", due.plusDays(1), 30, task.occurrence())), List.of(processed), 2));
        assertThrows(ValidationException.class, () -> state(List.of(task), List.of(new RecurringTask(1,
                "Quiz", due, Optional.empty(), 30, Optional.empty())), 2));
        assertThrows(ValidationException.class, () -> state(List.of(new Task(1, "Wrong week", due, 30,
                Optional.of(new TaskOccurrence(1, due.plusDays(1))))), List.of(processed), 2));
        assertThrows(ValidationException.class, () -> state(List.of(new Task(1, "Ahead", due, 30,
                Optional.of(new TaskOccurrence(1, due.plusWeeks(1))))), List.of(processed), 2));
        assertThrows(ValidationException.class, () -> state(List.of(), List.of(), 0));
    }

    /** Builds structural fixtures with room for two task IDs. */
    private Snapshot state(List<Task> tasks, List<RecurringTask> templates, int nextRecurring) {
        return new Snapshot(tasks, List.of(), 3, 1, Snapshot.empty().studyHours(), templates, nextRecurring);
    }
}
// @@author
