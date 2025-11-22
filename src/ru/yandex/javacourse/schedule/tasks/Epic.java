package ru.yandex.javacourse.schedule.tasks;

import static ru.yandex.javacourse.schedule.tasks.TaskStatus.NEW;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Epic extends Task {
	protected Set<Integer> subtasks = new HashSet<>();

	protected LocalDateTime endTime;

	public Epic(String name, String description) {
		super(name, description, NEW);
	}

	public Epic(int id, String name, TaskStatus status, String description) {
		super(id, name, status, description);
	}

	public Epic(int id, String name, TaskStatus status, String description, Duration duration, LocalDateTime startTime) {
		super(id, name, status, description, duration, startTime);
	}

	public TaskType getType() {
		return TaskType.EPIC;
	}

	public void addSubtaskId(Integer subtask) {
		if (getId() == subtask)
			throw new IllegalArgumentException("Epic cannot be added to epic like subtask for itself");
		subtasks.add(subtask);
	}

	public LocalDateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDateTime endTime) {
		this.endTime = endTime;
	}

	public List<Integer> getSubtaskIds() {
		return new ArrayList<>(subtasks);
	}

	public void cleanSubtaskIds() {
		subtasks.clear();
	}

	public void removeSubtask(Integer subtask) {
		subtasks.remove(subtask);
	}

	@Override
	public String toString() {
		return String.format("%s,%s,%s,%s,%s,,%s,%s,",this.getId(),this.getType(),this.getName(),
				this.getStatus().toString(),this.getDescription(),this.getStartTime(),this.getDuration());
	}
}
