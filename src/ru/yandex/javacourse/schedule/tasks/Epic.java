package ru.yandex.javacourse.schedule.tasks;

import static ru.yandex.javacourse.schedule.tasks.TaskStatus.NEW;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class Epic extends Task {
	protected LinkedHashSet<Subtask> subtasks = new LinkedHashSet<>();

	public Epic(String name, String description) {
		super(name, description, NEW);
	}

	public void addSubtaskId(Subtask subtask) {
		subtasks.add(subtask);
	}

	public List<Subtask> getSubtasks() {
		return new ArrayList<>(subtasks);
	}

	public List<Integer> getSubtaskIds() {
		List<Integer> subtaskIds = new ArrayList<>();
		for (Subtask subtask : subtasks) {
			subtaskIds.add(subtask.id);
		}
		return subtaskIds;
	}

	public void cleanSubtaskIds() {
		subtasks.clear();
	}

	public void removeSubtask(Subtask subtask) {
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
