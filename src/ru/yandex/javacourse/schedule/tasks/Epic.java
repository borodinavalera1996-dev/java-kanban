package ru.yandex.javacourse.schedule.tasks;

import static ru.yandex.javacourse.schedule.tasks.TaskStatus.NEW;

import java.util.*;

public class Epic extends Task {
	protected Set<Integer> subtasks = new HashSet<>();

	public Epic(String name, String description) {
		super(name, description, NEW);
		this.type = TaskType.EPIC;
	}

	public Epic(int id, String name, TaskStatus status, String description) {
		super(id, name, status, description);
		this.type = TaskType.EPIC;
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
		return String.format("%s,%s,%s,%s,%s,",this.getId(),this.getType(),this.getName(),
				this.getStatus().toString(),this.getDescription());
	}
}
