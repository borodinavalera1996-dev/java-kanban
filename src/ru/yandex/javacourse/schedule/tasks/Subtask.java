package ru.yandex.javacourse.schedule.tasks;

public class Subtask extends Task {
	protected int epicId;

	public Subtask(String name, String description, TaskStatus status, Epic epic) {
		super(name, description, status);
		this.epicId = epic.getId();
	}

	public int getEpicId() {
		return epicId;
	}

	@Override
	public String toString() {
		return "Subtask{" +
				"id=" + id +
				", epicId=" + epicId +
				", name='" + name + '\'' +
				", status=" + status +
				", description='" + description + '\'' +
				'}';
	}
}
