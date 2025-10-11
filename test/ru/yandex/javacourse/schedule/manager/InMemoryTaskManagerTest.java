package ru.yandex.javacourse.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.tasks.Epic;
import ru.yandex.javacourse.schedule.tasks.Subtask;
import ru.yandex.javacourse.schedule.tasks.Task;
import ru.yandex.javacourse.schedule.tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class InMemoryTaskManagerTest {

    TaskManager manager;

    @BeforeEach
    public void initManager(){
        manager = Managers.getDefault();
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
