package ru.yandex.javacourse.schedule.tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
	protected int epicId;

	public Subtask(String name, String description, TaskStatus status, Epic epic) {
		super(name, description, status);
		this.epicId = epic.getId();
	}

	public Subtask(int id, String name, TaskStatus status, String description, int epicId) {
		super(id, name, status, description);
		this.epicId = epicId;
	}

	public Subtask(int id, String name, TaskStatus status, String description, Duration duration, LocalDateTime startTime, int epicId) {
		super(id, name, status, description, duration, startTime);
		this.epicId = epicId;
	}

	public TaskType getType() {
		return TaskType.SUBTASK;
	}

	public int getEpicId() {
		return epicId;
	}

	@Override
	public String toString() {
		return String.format("%s,%s,%s,%s,%s,%s,%s,%s",this.getId(),this.getType(),this.getName(),
				this.getStatus().toString(),this.getDescription(),this.getEpicId(),this.getStartTime(),this.getDuration());
	}
}
