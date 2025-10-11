package ru.yandex.javacourse.schedule.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EpicTest {

    @Test
    public void testSubtaskUniqueIds() {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        Subtask s0 = new Subtask("Test 1", "Testing task 1", TaskStatus.NEW, epic);
        Subtask s1 = new Subtask("Test 2", "Testing task 2", TaskStatus.NEW, epic);
        epic.addSubtaskId(s0.getId());
        epic.addSubtaskId(s1.getId());
        assertEquals(2, epic.getSubtaskIds().size(), "should add distinct subtask ids");
        epic.addSubtaskId(s0.getId());
        assertEquals(2, epic.getSubtaskIds().size(), "should not add same subtask id twice");
    }

    @Test
    public void testSubtaskRemoving() {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        Subtask s0 = new Subtask("Test 1", "Testing task 1", TaskStatus.NEW, epic);
        epic.addSubtaskId(s0.getId());
        assertEquals(1, epic.getSubtaskIds().size(), "epic should add subtask");
        epic.removeSubtask(s0.getId());
        assertEquals(0, epic.getSubtaskIds().size(), "epic should remove subtask");
    }
}
