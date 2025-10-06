package ru.yandex.javacourse.schedule.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.tasks.Task;
import ru.yandex.javacourse.schedule.tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {

    HistoryManager historyManager;

    @BeforeEach
    public void initHistoryManager(){
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    public void testRemovingHistoryByOneTask() {
        Task task = new Task("Test 1", "Testiong task 1", TaskStatus.NEW);
        historyManager.addTask(task);
        assertEquals(1, historyManager.getHistory().size(), "historic task should be added");
        historyManager.remove(task.getId());
        assertEquals(0, historyManager.getHistory().size(), "historic task should be removed");
    }

    @Test
    public void testRemovingHistoryByTasks() {
        Task task1 = new Task( "Test 1", "Testiong task 1", TaskStatus.NEW);
        historyManager.addTask(task1);
        Task task2 = new Task( "Test 2", "Testiong task 2", TaskStatus.NEW);
        historyManager.addTask(task2);
        assertEquals(2, historyManager.getHistory().size(), "historic tasks should be added");
        historyManager.remove(task2.getId());
        assertEquals(1, historyManager.getHistory().size(), "historic task should be removed");
    }

    @Test
    public void testHistoricVersions(){
        Task task = new Task("Test 1", "Testiong task 1", TaskStatus.NEW);
        historyManager.addTask(task);
        assertEquals(1, historyManager.getHistory().size(), "historic task should be added");
        task.setStatus(TaskStatus.IN_PROGRESS);
        historyManager.addTask(task);
        assertEquals(1, historyManager.getHistory().size(), "historic task should be added");
    }

    @Test
    public void testHistoricVersionsByPointer(){
        Task task = new Task("Test 1", "Testiong task 1", TaskStatus.NEW);
        historyManager.addTask(task);
        assertEquals(task.getStatus(), historyManager.getHistory().get(0).getStatus(), "historic task should be stored");
        task.setStatus(TaskStatus.IN_PROGRESS);
        historyManager.addTask(task);
        assertEquals(TaskStatus.IN_PROGRESS, historyManager.getHistory().get(0).getStatus(), "historic task should be changed");
        assertEquals(1, historyManager.getHistory().size(), "historic task should be changed");
    }

    @Test
    public void testOrderHistoryByTasks() {
        Task task1 = new Task( "Test 1", "Testiong task 1", TaskStatus.NEW);
        historyManager.addTask(task1);
        Task task2 = new Task( "Test 2", "Testiong task 2", TaskStatus.NEW);
        historyManager.addTask(task2);
        Task task3 = new Task( "Test 3", "Testiong task 3", TaskStatus.NEW);
        historyManager.addTask(task3);
        assertEquals(3, historyManager.getHistory().size(), "historic tasks should be added");
        assertEquals(task1.getId(), historyManager.getHistory().get(0).getId(), "historic task should be on order adding");
        assertEquals(task2.getId(), historyManager.getHistory().get(1).getId(), "historic task should be on order adding");
        assertEquals(task3.getId(), historyManager.getHistory().get(2).getId(), "historic task should be on order adding");
    }

    @Test
    public void testOrderRemovingHistoryByTasks() {
        Task task1 = new Task( "Test 1", "Testiong task 1", TaskStatus.NEW);
        historyManager.addTask(task1);
        Task task2 = new Task( "Test 2", "Testiong task 2", TaskStatus.NEW);
        historyManager.addTask(task2);
        Task task3 = new Task( "Test 3", "Testiong task 3", TaskStatus.NEW);
        historyManager.addTask(task3);
        assertEquals(3, historyManager.getHistory().size(), "historic tasks should be added");
        historyManager.remove(task2.getId());
        assertEquals(2, historyManager.getHistory().size(), "historic tasks should be removed");
        assertEquals(task1.getId(), historyManager.getHistory().get(0).getId(), "historic task should be on order adding");
        assertEquals(task3.getId(), historyManager.getHistory().get(1).getId(), "historic task should be on order adding");
    }

    @Test
    public void testOrderUpdatingHistoryByTasks() {
        Task task1 = new Task( "Test 1", "Testiong task 1", TaskStatus.NEW);
        historyManager.addTask(task1);
        Task task2 = new Task( "Test 2", "Testiong task 2", TaskStatus.NEW);
        historyManager.addTask(task2);
        Task task3 = new Task( "Test 3", "Testiong task 3", TaskStatus.NEW);
        historyManager.addTask(task3);
        assertEquals(3, historyManager.getHistory().size(), "historic tasks should be added");
        task2.setStatus(TaskStatus.IN_PROGRESS);
        historyManager.addTask(task2);
        assertEquals(task1.getId(), historyManager.getHistory().get(0).getId(), "historic task should be on order adding");
        assertEquals(task3.getId(), historyManager.getHistory().get(1).getId(), "historic task should be on order adding");
        assertEquals(task2.getId(), historyManager.getHistory().get(2).getId(), "historic task should be on order adding");
    }

    @Test
    public void testOrderRemovingFirstHistoryByTasks() {
        Task task1 = new Task( "Test 1", "Testiong task 1", TaskStatus.NEW);
        historyManager.addTask(task1);
        Task task2 = new Task( "Test 2", "Testiong task 2", TaskStatus.NEW);
        historyManager.addTask(task2);
        Task task3 = new Task( "Test 3", "Testiong task 3", TaskStatus.NEW);
        historyManager.addTask(task3);
        assertEquals(3, historyManager.getHistory().size(), "historic tasks should be added");
        historyManager.remove(task1.getId());
        assertEquals(2, historyManager.getHistory().size(), "historic tasks should be removed");
        assertEquals(task2.getId(), historyManager.getHistory().get(0).getId(), "historic task should be on order adding");
        assertEquals(task3.getId(), historyManager.getHistory().get(1).getId(), "historic task should be on order adding");
    }
    @Test
    public void testOrderRemovingLastHistoryByTasks() {
        Task task1 = new Task( "Test 1", "Testiong task 1", TaskStatus.NEW);
        historyManager.addTask(task1);
        Task task2 = new Task( "Test 2", "Testiong task 2", TaskStatus.NEW);
        historyManager.addTask(task2);
        Task task3 = new Task( "Test 3", "Testiong task 3", TaskStatus.NEW);
        historyManager.addTask(task3);
        assertEquals(3, historyManager.getHistory().size(), "historic tasks should be added");
        historyManager.remove(task3.getId());
        assertEquals(2, historyManager.getHistory().size(), "historic tasks should be removed");
        assertEquals(task1.getId(), historyManager.getHistory().get(0).getId(), "historic task should be on order adding");
        assertEquals(task2.getId(), historyManager.getHistory().get(1).getId(), "historic task should be on order adding");
    }
    @Test
    public void testDoubleHistoryByTasks() {
        Task task1 = new Task( "Test 1", "Testiong task 1", TaskStatus.NEW);
        historyManager.addTask(task1);
        historyManager.addTask(task1);
        Task task2 = new Task( "Test 2", "Testiong task 2", TaskStatus.NEW);
        historyManager.addTask(task2);
        historyManager.addTask(task2);
        Task task3 = new Task( "Test 3", "Testiong task 3", TaskStatus.NEW);
        historyManager.addTask(task3);
        historyManager.addTask(task3);
        assertEquals(3, historyManager.getHistory().size(), "historic tasks should be added");
        assertEquals(task1.getId(), historyManager.getHistory().get(0).getId(), "historic task should be on order adding");
        assertEquals(task2.getId(), historyManager.getHistory().get(1).getId(), "historic task should be on order adding");
        assertEquals(task3.getId(), historyManager.getHistory().get(2).getId(), "historic task should be on order adding");
    }
}
