package ru.yandex.javacourse.schedule.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.tasks.Epic;
import ru.yandex.javacourse.schedule.tasks.Subtask;
import ru.yandex.javacourse.schedule.tasks.Task;
import ru.yandex.javacourse.schedule.tasks.TaskStatus;
import ru.yandex.javacourse.schedule.exception.IntersectTimeException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class TaskManagerTest<T extends TaskManager> {

    TaskManager manager;

    @Test
    public void testEpicStatusWithSubtaskWithAllNewTaskStatus() {
        Epic epic1 = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic1);
        Subtask s0 = new Subtask("Test 1", "Testing task 1", TaskStatus.NEW, epic1);
        Subtask s1 = new Subtask("Test 2", "Testing task 2", TaskStatus.NEW, epic1);
        manager.addNewSubtask(s0);
        manager.addNewSubtask(s1);
        assertEquals(TaskStatus.NEW, epic1.getStatus(), "should equals");
    }

    @Test
    public void testEpicStatusWithSubtaskWithAllDoneTaskStatus() {
        Epic epic1 = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic1);
        Subtask s0 = new Subtask("Test 1", "Testing task 1", TaskStatus.DONE, epic1);
        Subtask s1 = new Subtask("Test 2", "Testing task 2", TaskStatus.DONE, epic1);
        manager.addNewSubtask(s0);
        manager.addNewSubtask(s1);
        assertEquals(TaskStatus.DONE, epic1.getStatus(), "should equals");
    }

    @Test
    public void testEpicStatusWithSubtaskWithNewAndDoneTaskStatus() {
        Epic epic1 = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic1);
        Subtask s0 = new Subtask("Test 1", "Testing task 1", TaskStatus.NEW, epic1);
        Subtask s1 = new Subtask("Test 2", "Testing task 2", TaskStatus.DONE, epic1);
        manager.addNewSubtask(s0);
        manager.addNewSubtask(s1);
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus(), "should equals");
    }

    @Test
    public void testEpicStatusWithSubtaskWithInProcessTaskStatus() {
        Epic epic1 = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic1);
        Subtask s0 = new Subtask("Test 1", "Testing task 1", TaskStatus.IN_PROGRESS, epic1);
        Subtask s1 = new Subtask("Test 2", "Testing task 2", TaskStatus.IN_PROGRESS, epic1);
        manager.addNewSubtask(s0);
        manager.addNewSubtask(s1);
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus(), "should equals");
    }

    @Test
    public void testEpicStatusWithSubtaskWithAllTypeTaskStatus() {
        Epic epic1 = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic1);
        Subtask s0 = new Subtask("Test 1", "Testing task 1", TaskStatus.NEW, epic1);
        Subtask s1 = new Subtask("Test 2", "Testing task 2", TaskStatus.IN_PROGRESS, epic1);
        Subtask s2 = new Subtask("Test 3", "Testing task 3", TaskStatus.DONE, epic1);
        manager.addNewSubtask(s0);
        manager.addNewSubtask(s1);
        manager.addNewSubtask(s2);
        assertEquals(TaskStatus.IN_PROGRESS, epic1.getStatus(), "should equals");
    }

    @Test
    public void testPrioritizedTasks() {
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task(1, "Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofHours(3), now);
        Task task1 = new Task(2,"Test 2",  TaskStatus.NEW,"Testing task 2", Duration.ofHours(4), now.minusDays(1));
        Task task2 = new Task(3,"Test 3",  TaskStatus.NEW,"Testing task 3", Duration.ofHours(5), now.minusDays(2));
        manager.addNewTask(task);
        manager.addNewTask(task1);
        manager.addNewTask(task2);

        Set<Task> tasks = Set.of(task2,task1,task);
        assertEquals(tasks, manager.getPrioritizedTasks(), "should equals");
    }


    @Test
    public void testEpicEndTimeWithOneSubtasks() {
        LocalDateTime now = LocalDateTime.now();
        Epic epic = new Epic(1, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        manager.addNewEpic(epic);
        Subtask s0 = new Subtask(2,"Test 1",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(3), now, epic.getId());
        epic.addSubtaskId(s0.getId());
        manager.addNewSubtask(s0);
        LocalDateTime endTime = now.plus(Duration.ofHours(3));
        assertEquals(epic.getEndTime(), endTime, "endTime should be equals");
    }

    @Test
    public void testEpicEndTimeWithTwoSubtasks() {
        LocalDateTime now = LocalDateTime.now();
        Epic epic = new Epic(1, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        manager.addNewEpic(epic);
        Subtask s0 = new Subtask(2,"Test 1",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(3), now, epic.getId());
        Subtask s1 = new Subtask(3,"Test 2",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(4), now.minusDays(1), epic.getId());
        epic.addSubtaskId(s0.getId());
        manager.addNewSubtask(s0);
        epic.addSubtaskId(s1.getId());
        manager.addNewSubtask(s1);
        LocalDateTime endTime = now.minusDays(1).plus(Duration.ofHours(3)).plus(Duration.ofHours(4));
        assertEquals(epic.getEndTime(), endTime, "endTime should be equals");
    }

    @Test
    public void testEpicEndTimeWithTwoSubtasksWithStartDate() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(5);
        LocalDateTime now = LocalDateTime.now();
        Epic epic = new Epic(1, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        manager.addNewEpic(epic);
        Subtask s0 = new Subtask(2,"Test 1",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(3), startDate, epic.getId());
        Subtask s1 = new Subtask(3,"Test 2",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(4), now, epic.getId());
        epic.addSubtaskId(s0.getId());
        manager.addNewSubtask(s0);
        epic.addSubtaskId(s1.getId());
        manager.addNewSubtask(s1);
        LocalDateTime endTime = startDate.plus(Duration.ofHours(3)).plus(Duration.ofHours(4));
        assertEquals(epic.getEndTime(), endTime, "endTime should be equals");
    }

    @Test
    public void testEpicEndTimeWithNullSubtasksWithStartDate() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(5);
        LocalDateTime now = LocalDateTime.now();
        Epic epic = new Epic(1, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        manager.addNewEpic(epic);
        Subtask s0 = new Subtask(2,"Test 1",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(3), startDate, epic.getId());
        Subtask s1 = new Subtask(3,"Test 2",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(4), now, epic.getId());
        Subtask s2 = new Subtask(4,"Test 2",  TaskStatus.NEW,"Testing task 1", Duration.ZERO, null, epic.getId());
        epic.addSubtaskId(s0.getId());
        manager.addNewSubtask(s0);
        epic.addSubtaskId(s1.getId());
        manager.addNewSubtask(s1);
        epic.addSubtaskId(s2.getId());
        manager.addNewSubtask(s2);
        LocalDateTime endTime = startDate.plus(Duration.ofHours(3)).plus(Duration.ofHours(4));
        assertEquals(epic.getEndTime(), endTime, "endTime should be equals");
    }
    @Test
    public void testEpicEndTimeWithSameStartDate() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(5);
        Epic epic = new Epic(1, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        manager.addNewEpic(epic);
        Subtask s0 = new Subtask(2,"Test 1",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(3), startDate, epic.getId());
        Subtask s1 = new Subtask(3,"Test 2",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(4), startDate, epic.getId());
        epic.addSubtaskId(s0.getId());
        manager.addNewSubtask(s0);
        epic.addSubtaskId(s1.getId());
        assertThrows(IntersectTimeException.class, () -> manager.addNewSubtask(s1));
    }
    @Test
    public void testEpicEndTimeWithIntersectTime() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(5);
        Epic epic = new Epic(1, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        manager.addNewEpic(epic);
        Subtask s0 = new Subtask(2,"Test 1",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(3), startDate, epic.getId());
        Subtask s1 = new Subtask(3,"Test 2",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(4), startDate.plusHours(2), epic.getId());
        epic.addSubtaskId(s0.getId());
        manager.addNewSubtask(s0);
        epic.addSubtaskId(s1.getId());
        assertThrows(IntersectTimeException.class, () -> manager.addNewSubtask(s1));
    }

    @Test
    public void testGetPrioritizedTasks() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(5);
        Task task = new Task(1, "Test 1", TaskStatus.NEW, "Testing task 1", Duration.ZERO, startDate);
        Task task1 = new Task(2, "Test 1", TaskStatus.NEW, "Testing task 1", Duration.ZERO, startDate.plusDays(1));
        Task task2 = new Task(3, "Test 1", TaskStatus.NEW, "Testing task 1", Duration.ZERO, startDate.plusDays(2));
        Task task3 = new Task(4, "Test 1", TaskStatus.NEW, "Testing task 1", Duration.ZERO, startDate.plusDays(3));
        Task task4 = new Task(5, "Test 1", TaskStatus.NEW, "Testing task 1", Duration.ZERO, startDate.plusDays(4));
        manager.addNewTask(task);
        manager.addNewTask(task1);
        manager.addNewTask(task2);
        manager.addNewTask(task3);
        manager.addNewTask(task4);
        System.out.println(manager.getPrioritizedTasks());
//        assertEquals(manager.getPrioritizedTasks());
    }

    @Test
    public void testAddTask() {
        Task task = new Task("Test 1", "Testing task 1", TaskStatus.NEW);
        manager.addNewTask(task);
        assertEquals(1, manager.getTasks().size(), "task should be added");
        Task addedTask = manager.getTasks().get(0);
        assertEquals(task, addedTask, "added task id should be set");
        Task byIdTask = manager.getTask(task.getId());
        assertEquals(task, byIdTask, "added task id should be found");
    }

    @Test
    public void checkTaskNotChangedAfterAddTask() {
        String name = "Test 1";
        String description = "Testing task 1";
        TaskStatus status = TaskStatus.NEW;
        Task task1before = new Task(name, description, status);
        manager.addNewTask(task1before);
        Task task1after = manager.getTask(task1before.getId());
        assertEquals(task1after.getDescription(), description);
        assertEquals(task1after.getStatus(), status);
        assertEquals(task1after.getName(), name);
    }

    @Test
    public void testAddEpic() {
        Epic epic1 = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic1);
        assertEquals(1, manager.getEpics().size(), "epic should be added");
        assertEquals(0, manager.getTasks().size(), "not task should be added");
        assertEquals(0, manager.getSubtasks().size(), "not subtask should be added");
    }

    @Test
    public void testAddChangedEpic() {
        Epic epic1 = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic1);
        assertEquals(1, manager.getEpics().size(), "epic should be added");
        epic1.setStatus(TaskStatus.NEW);
        assertEquals(1, manager.getEpics().size(), "epic should be added");
    }
    @Test
    public void testAddEpicWithSubclass() {
        Epic epic1 = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic1);
        Subtask s0 = new Subtask("Test 1", "Testing task 1", TaskStatus.NEW, epic1);
        Subtask s1 = new Subtask("Test 1", "Testing task 1", TaskStatus.NEW, epic1);
        manager.addNewSubtask(s0);
        manager.addNewSubtask(s1);
        assertEquals(1, manager.getEpics().size(), "epic should be added");
        assertEquals(2, manager.getSubtasks().size(), "subtasks should be added");
    }
    @Test
    public void testRemoveEpicWithSubclass() {
        Epic epic1 = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic1);
        Subtask s0 = new Subtask("Test 1", "Testing task 1", TaskStatus.NEW, epic1);
        Subtask s1 = new Subtask("Test 1", "Testing task 1", TaskStatus.NEW, epic1);
        manager.addNewSubtask(s0);
        manager.addNewSubtask(s1);

        manager.deleteEpic(epic1.getId());
        assertEquals(0, manager.getEpics().size(), "epic should be removed");
        assertEquals(0, manager.getSubtasks().size(), "subtasks should be removed");
    }

    @Test
    public void testRemoveSubclass() {
        Epic epic1 = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic1);
        Subtask s0 = new Subtask("Test 1", "Testing task 1", TaskStatus.NEW, epic1);
        Subtask s1 = new Subtask("Test 1", "Testing task 1", TaskStatus.NEW, epic1);
        manager.addNewSubtask(s0);
        manager.addNewSubtask(s1);

        manager.deleteSubtask(s1.getId());
        assertEquals(1, manager.getEpics().size(), "epic should be added");
        assertEquals(1, manager.getSubtasks().size(), "subtask should be removed");
        assertEquals(1, epic1.getSubtaskIds().size(), "subtask should be removed");
    }

    @Test
    public void testRemoveHistorySubtask() {
        Epic epic1 = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic1);
        Subtask s0 = new Subtask("Test 1", "Testing task 1", TaskStatus.NEW, epic1);
        manager.addNewSubtask(s0);
        manager.getEpic(epic1.getId());
        manager.getSubtask(s0.getId());

        assertEquals(2, manager.getHistory().size(), "history should be added");
        manager.deleteSubtask(s0.getId());
        assertEquals(1, manager.getHistory().size(), "history should be removed");
    }
    @Test
    public void testRemoveHistoryTask() {
        Task t0 = new Task("Test 1", "Testing task 1", TaskStatus.NEW);
        manager.addNewTask(t0);
        manager.getTask(t0.getId());

        assertEquals(1, manager.getHistory().size(), "history should be added");
        manager.deleteTask(t0.getId());
        assertEquals(0, manager.getHistory().size(), "history should be removed");
    }

    @Test
    public void testRemoveHistoryEpicWithSubclass() {
        Epic epic1 = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic1);
        Subtask s0 = new Subtask("Test 1", "Testing task 1", TaskStatus.NEW, epic1);
        manager.addNewSubtask(s0);
        manager.getEpic(epic1.getId());
        manager.getSubtask(s0.getId());

        assertEquals(2, manager.getHistory().size(), "history should be added");
        manager.deleteEpic(epic1.getId());
        assertEquals(0, manager.getHistory().size(), "history should be removed");
    }

    private void fillManager() {
        Epic epic1 = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic1);
        manager.getEpic(epic1.getId());

        Subtask s0 = new Subtask("Test 1", "Testing task 1", TaskStatus.NEW, epic1);
        Subtask s1 = new Subtask("Test 1", "Testing task 1", TaskStatus.NEW, epic1);
        manager.addNewSubtask(s0);
        manager.getSubtask(s0.getId());
        manager.addNewSubtask(s1);
        manager.getSubtask(s1.getId());

        Epic epic2 = new Epic("Epic #2", "Epic2 description");
        manager.addNewEpic(epic2);
        manager.getEpic(epic2.getId());

        Task task = new Task("Test 1", "Testing task 1", TaskStatus.NEW);
        manager.addNewTask(task);
        manager.getTask(task.getId());

        Task task1 = new Task("Test 2", "Testing task 2", TaskStatus.NEW);
        manager.addNewTask(task1);
        manager.getTask(task1.getId());
    }
    @Test
    public void testRemoveHistoryAllEpicWithSubclass() {
        fillManager();

        assertEquals(6, manager.getHistory().size(), "history should be added");
        manager.deleteEpics();
        assertEquals(2, manager.getHistory().size(), "history should be removed");
    }
    @Test
    public void testRemoveHistoryAllTaskWithSubclass() {
        fillManager();

        assertEquals(6, manager.getHistory().size(), "history should be added");
        manager.deleteTasks();
        assertEquals(4, manager.getHistory().size(), "history should be removed");
    }

    @Test
    public void testRemoveHistoryAllSubtaskWithSubclass() {
        fillManager();

        assertEquals(6, manager.getHistory().size(), "history should be added");
        manager.deleteSubtasks();
        assertEquals(4, manager.getHistory().size(), "history should be removed");
    }
    @Test
    public void testRemoveHistoryOneEpicWithSubclass() {
        fillManager();

        assertEquals(6, manager.getHistory().size(), "history should be added");
        manager.deleteEpic(manager.getEpics().get(0).getId());
        assertEquals(3, manager.getHistory().size(), "history should be removed");
    }
}
