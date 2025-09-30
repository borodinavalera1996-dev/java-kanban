package ru.yandex.javacourse.schedule.tasks;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TaskTest {

    @Test
    public void testGenerateId() {
        Task t0 = new Task("Test 1", "Testing task 1", TaskStatus.NEW);
        Task t1 = new Task("Test 2", "Testing task 2", TaskStatus.IN_PROGRESS);
        assertNotEquals(t0, t1, "task entities should be compared by id");
    }

}
