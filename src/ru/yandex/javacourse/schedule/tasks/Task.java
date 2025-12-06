package ru.yandex.javacourse.schedule.tasks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
	protected int id;

	protected String name;
	protected TaskType type;
	protected TaskStatus status;
	protected String description;
	protected Duration duration = Duration.ZERO;

	protected LocalDateTime startTime;


	public Task(String name, String description, TaskStatus status) {
		this.name = name;
		this.description = description;
		this.status = status;
	}

	public Task(int id, String name, TaskStatus status, String description) {
		this(name, description, status);
		this.id = id;
	}

	public Task(int id, String name, TaskStatus status, String description, Duration duration, LocalDateTime startTime) {
		this.id = id;
		this.name = name;
		this.status = status;
		this.description = description;
		this.duration = duration;
		this.startTime = startTime;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}

	public Duration getDuration() {
		return duration;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	public LocalDateTime getEndTime() {
		return startTime.plus(duration);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TaskType getType() {
		return TaskType.TASK;
	}

	public void setType(TaskType type) {
		this.type = type;
	}

	public TaskStatus getStatus() {
		return status;
	}

	public void setStatus(TaskStatus status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Task task = (Task) o;
		return id == task.id;
	}

	@Override
	public String toString() {
		return String.format("%s,%s,%s,%s,%s,,%s,%s",this.getId(),this.getType(),this.getName(),
				this.getStatus().toString(),this.getDescription(),this.getStartTime(),this.getDuration());
	}
}
