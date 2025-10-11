package ru.yandex.javacourse.schedule.tasks;

import static ru.yandex.javacourse.schedule.tasks.TaskStatus.NEW;

import java.util.*;

public class Epic extends Task {
	protected Set<Integer> subtasks = new HashSet<>();

	public Epic(String name, String description) {
		super(name, description, NEW);
	}

	public void addSubtaskId(Integer subtask) {
		if (getId() == subtask)
			throw new IllegalArgumentException("Epic cannot be added to epic like subtask for itself");
		subtasks.add(subtask);
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
		return "Epic{" +
				"id=" + id +
				", name='" + name + '\'' +
				", status=" + status +
				", description='" + description + '\'' +
				", subtaskIds=" + getSubtaskIds() +
				'}';
	}
}
