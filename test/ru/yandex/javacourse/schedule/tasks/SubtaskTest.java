package ru.yandex.javacourse.schedule.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SubtaskTest {

    @Test
    public void testCreatingCorrectTask() {
        Epic epic = new Epic("Epic 1", "Testing epic 1");
        Subtask s0 = new Subtask("Test 1", "Testing task 1", TaskStatus.NEW, epic);
        assertEquals(s0.epicId, epic.id, "subtask should be added to epic");
    }

    @Test
    public void testNotNullAttaching() {
        Epic epic = null;
        assertThrows(IllegalArgumentException.class,
                () -> new Subtask("Subtask 1", "Testing subtask 1", TaskStatus.NEW, epic),
                "subtask cannot be attached to null");
    }
}
