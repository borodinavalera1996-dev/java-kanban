package ru.yandex.javacourse.schedule.manager;

import java.util.*;

import ru.yandex.javacourse.schedule.tasks.Task;

/**
 * In memory history manager.
 *
 * @author Vladimir Ivanov (ivanov.vladimir.l@gmail.com)
 */
public class InMemoryHistoryManager implements HistoryManager {

	private Node head;
	private Node tail;

	private Map<Integer, InMemoryHistoryManager.Node> histories = new HashMap<>();

	@Override
	public List<Task> getHistory() {
		List<Task> tasks = new ArrayList<>();
		Node node = head;
		while (node != null) {
			tasks.add(node.value);
			node = node.after;
		}
		return tasks;
	}

	@Override
	public void addTask(Task task) {
		if (task == null) {
			return;
		}
		if (histories.get(task.getId()) != null) {
			remove(task.getId());
		}
		Node node = new Node(task.getId(), task);
		linkLast(node);
		histories.put(task.getId(), node);
	}

	@Override
	public void remove(int id) {
		removeNode(histories.get(id));
		histories.remove(id);
	}

	@Override
	public void removeAll(Collection<Integer> tasks) {
		for (Integer task : tasks) {
			remove(task);
		}
	}

	private void removeNode(Node node) {
		if (node == null) return;
		Node before = node.before;
		Node after = node.after;
		if (before == null) {
			head = node.after;
		} else {
			before.after = after;
		}
		if (after == null) {
			tail = node.before;
		} else {
			after.before = before;
		}
	}

	private void linkLast(Node node) {
		Node last = tail;
		tail = node;
		if (last == null)
			head = node;
		else {
			node.before = last;
			last.after = node;
		}
	}

	static class Node {
		Node before;

		Node after;
		final Object key;
		Task value;

		Node(Integer key, Task value) {
			this.key = key;
			this.value = value;
		}
	}
}
