package ru.yandex.javacourse.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.tasks.Epic;
import ru.yandex.javacourse.schedule.tasks.Subtask;
import ru.yandex.javacourse.schedule.tasks.Task;
import ru.yandex.javacourse.schedule.tasks.TaskStatus;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    @Test
    public void testAddTask() throws IOException {
        Task task = new Task("Test 1", "Testing task 1", TaskStatus.NEW);

        File tempFile = File.createTempFile("FileBackedTaskManager", ".csv");
        TaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);

        manager.addNewTask(task);
        assertEquals(1, manager.getTasks().size(), "task should be added");

        TaskManager manager2 = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(1, manager2.getTasks().size(), "task should be added");
    }

    @Test
    public void testAddManyTask() throws IOException {
        File tempFile = File.createTempFile("FileBackedTaskManager", ".csv");
        TaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);

        Task task = new Task(1, "Test 1", TaskStatus.NEW, "Testing task 1");
        manager.addNewTask(task);

        Epic epic = new Epic(2, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        manager.addNewEpic(epic);
        Subtask s0 = new Subtask(3,"Test 1",  TaskStatus.NEW, "Testing task 1", epic.getId());
        manager.addNewSubtask(s0);
        Subtask s1 = new Subtask(4, "Test 2", TaskStatus.NEW, "Testing task 2", epic.getId());
        manager.addNewSubtask(s1);
        epic.addSubtaskId(s0.getId());
        epic.addSubtaskId(s1.getId());

        assertEquals(1, manager.getTasks().size(), "task should be added");
        assertEquals(2, manager.getSubtasks().size(), "subtasks should be added");
        assertEquals(1, manager.getEpics().size(), "epic should be added");

        TaskManager manager2 = FileBackedTaskManager.loadFromFile(tempFile);
        assertEquals(1, manager2.getTasks().size(), "task should be added");
        assertEquals(2, manager.getSubtasks().size(), "subtasks should be added");
        assertEquals(1, manager.getEpics().size(), "epic should be added");
    }

    @Test
    public void testIdGenerator() throws IOException {
        File tempFile = File.createTempFile("FileBackedTaskManager", ".csv");
        TaskManager manager = FileBackedTaskManager.loadFromFile(tempFile);

        Task task = new Task(1, "Test 1", TaskStatus.NEW, "Testing task 1");
        manager.addNewTask(task);

        Epic epic = new Epic(2, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        manager.addNewEpic(epic);
        Subtask s0 = new Subtask(3,"Test 1",  TaskStatus.NEW, "Testing task 1", epic.getId());
        manager.addNewSubtask(s0);
        epic.addSubtaskId(s0.getId());

        assertEquals(1, manager.getTasks().size(), "task should be added");
        assertEquals(1, manager.getSubtasks().size(), "subtask should be added");
        assertEquals(1, manager.getEpics().size(), "epic should be added");

        TaskManager manager2 = FileBackedTaskManager.loadFromFile(tempFile);
        Subtask s1 = new Subtask("Test 2", "Testing task 2", TaskStatus.NEW, epic);
        manager2.addNewSubtask(s1);
        epic.addSubtaskId(s1.getId());

        assertEquals(4, manager2.getSubtasks().get(1).getId(), "ids should be equals");
    }

}
