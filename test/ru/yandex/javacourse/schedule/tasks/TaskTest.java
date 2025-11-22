package ru.yandex.javacourse.schedule.tasks;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class TaskTest {

    @Test
    public void testEndTime() {
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task(1, "Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofHours(3), now);
        LocalDateTime endTime = now.plus(Duration.ofHours(3));
        assertEquals(task.getEndTime(), endTime, "endTime should be equals");
    }

    @Test
    public void testGenerateId() {
        Task t0 = new Task(1, "Test 1", TaskStatus.NEW, "Testing task 1");
        Task t1 = new Task(2, "Test 2", TaskStatus.IN_PROGRESS, "Testing task 2");
        assertNotEquals(t0, t1, "task entities should be compared by id");
    }

}
