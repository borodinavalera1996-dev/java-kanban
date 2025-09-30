package ru.yandex.javacourse.schedule.tasks;

import ru.yandex.javacourse.schedule.tasks.util.GeneratorId;

import java.util.Objects;

public class Task {

	static GeneratorId generatorId = new GeneratorId();

	protected int id;
	protected String name;
	protected TaskStatus status;
	protected String description;


	public Task(String name, String description, TaskStatus status) {
		this.id = generatorId.getNewId();
		this.name = name;
		this.description = description;
		this.status = status;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		return "Task{" +
				"id=" + id +
				", name='" + name + '\'' +
				", status='" + status + '\'' +
				", description='" + description + '\'' +
				'}';
	}
}
