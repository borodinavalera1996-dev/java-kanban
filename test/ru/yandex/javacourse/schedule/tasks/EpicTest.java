package ru.yandex.javacourse.schedule.tasks;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EpicTest {

    @Test
    public void testSubtaskUniqueIds() {
        Epic epic = new Epic(1, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        Subtask s0 = new Subtask(2,"Test 1",  TaskStatus.NEW, "Testing task 1", epic.getId());
        Subtask s1 = new Subtask(3, "Test 2", TaskStatus.NEW, "Testing task 2", epic.getId());
        epic.addSubtaskId(s0.getId());
        epic.addSubtaskId(s1.getId());
        assertEquals(2, epic.getSubtaskIds().size(), "should add distinct subtask ids");
        epic.addSubtaskId(s0.getId());
        assertEquals(2, epic.getSubtaskIds().size(), "should not add same subtask id twice");
    }

    @Test
    public void testSubtaskWithAllNewTaskStatus() {
        Epic epic = new Epic(1, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        Subtask s0 = new Subtask(2,"Test 1",  TaskStatus.NEW, "Testing task 1", epic.getId());
        Subtask s1 = new Subtask(3, "Test 2", TaskStatus.NEW, "Testing task 2", epic.getId());
        epic.addSubtaskId(s0.getId());
        epic.addSubtaskId(s1.getId());
        assertEquals(2, epic.getSubtaskIds().size(), "should add distinct subtask ids");
        epic.addSubtaskId(s0.getId());
        assertEquals(2, epic.getSubtaskIds().size(), "should not add same subtask id twice");
    }

    @Test
    public void testSubtaskRemoving() {
        Epic epic = new Epic(1, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        Subtask s0 = new Subtask(2,"Test 1",  TaskStatus.NEW, "Testing task 1", epic.getId());
        epic.addSubtaskId(s0.getId());
        assertEquals(1, epic.getSubtaskIds().size(), "epic should add subtask");
        epic.removeSubtask(s0.getId());
        assertEquals(0, epic.getSubtaskIds().size(), "epic should remove subtask");
    }
}
