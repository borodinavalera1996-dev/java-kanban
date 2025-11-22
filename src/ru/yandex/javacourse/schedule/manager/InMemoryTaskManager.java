package ru.yandex.javacourse.schedule.manager;

import static ru.yandex.javacourse.schedule.tasks.TaskStatus.IN_PROGRESS;
import static ru.yandex.javacourse.schedule.tasks.TaskStatus.NEW;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import ru.yandex.javacourse.schedule.exception.IntersectTimeException;
import ru.yandex.javacourse.schedule.tasks.Epic;
import ru.yandex.javacourse.schedule.tasks.Subtask;
import ru.yandex.javacourse.schedule.tasks.Task;
import ru.yandex.javacourse.schedule.tasks.TaskStatus;

public class InMemoryTaskManager implements TaskManager {

	private final Map<Integer, Task> tasks = new HashMap<>();
	private final Map<Integer, Epic> epics = new HashMap<>();
	private final Map<Integer, Subtask> subtasks = new HashMap<>();

	protected int generatorId = 0;

	private final HistoryManager historyManager = Managers.getDefaultHistory();

	private final Set<Task> sorterTask = new TreeSet<>(Comparator.comparing(Task::getStartTime));
	@Override
	public ArrayList<Task> getTasks() {
		return new ArrayList<>(this.tasks.values());
	}

	@Override
	public ArrayList<Subtask> getSubtasks() {
		return new ArrayList<>(subtasks.values());
	}

	@Override
	public ArrayList<Epic> getEpics() {
		return new ArrayList<>(epics.values());
	}

	@Override
	public ArrayList<Subtask> getEpicSubtasks(int epicId) {
		ArrayList<Subtask> tasks = new ArrayList<>();
		Epic epic = epics.get(epicId);
		if (epic == null) {
			return null;
		}
		epic.getSubtaskIds().stream().map(subtasks::get).peek(tasks::add);
		return tasks;
	}

	@Override
	public Task getTask(int id) {
		final Task task = tasks.get(id);
		historyManager.addTask(task);
		return task;
	}

	@Override
	public Set<Task> getPrioritizedTasks() {
		return sorterTask;
	}

	@Override
	public Subtask getSubtask(int id) {
		final Subtask subtask = subtasks.get(id);
		historyManager.addTask(subtask);
		return subtask;
	}

	@Override
	public Epic getEpic(int id) {
		final Epic epic = epics.get(id);
		historyManager.addTask(epic);
		return epic;
	}

	@Override
	public int addNewTask(Task task) {
		if (task.getStartTime() != null && intersectTask(task))
			throw new IntersectTimeException();
		setId(task);
		tasks.put(task.getId(), task);
		if (task.getStartTime() != null)
			sorterTask.add(task);
		return task.getId();
	}

	private void setId(Task task) {
		if (task.getId() == 0) {
			final int id = ++generatorId;
			task.setId(id);
		}
	}

	@Override
	public int addNewEpic(Epic epic) {
		setId(epic);
		epics.put(epic.getId(), epic);
		return epic.getId();

	}

	@Override
	public Integer addNewSubtask(Subtask subtask) {
		final int epicId = subtask.getEpicId();
		Epic epic = epics.get(epicId);
		if (epic == null) {
			return null;
		}
		if (epic.getSubtaskIds().size() > 1 && subtask.getStartTime() != null && intersectTask(subtask))
			throw new IntersectTimeException();
		setId(subtask);
		subtasks.put(subtask.getId(), subtask);
		epic.addSubtaskId(subtask.getId());
		updateEpicStatus(epicId);
		updateEpicTime(epicId);
		if (subtask.getStartTime() != null)
			sorterTask.add(subtask);
		return subtask.getId();
	}

	@Override
	public void updateTask(Task task) {
		final int id = task.getId();
		final Task savedTask = tasks.get(id);
		if (savedTask == null) {
			return;
		}
		tasks.put(id, task);
	}

	@Override
	public void updateEpic(Epic epic) {
		final Epic savedEpic = epics.get(epic.getId());
		savedEpic.setName(epic.getName());
		savedEpic.setDescription(epic.getDescription());
	}

	@Override
	public void updateSubtask(Subtask subtask) {
		final int id = subtask.getId();
		final int epicId = subtask.getEpicId();
		final Subtask savedSubtask = subtasks.get(id);
		if (savedSubtask == null) {
			return;
		}
		final Epic epic = epics.get(epicId);
		if (epic == null) {
			return;
		}
		subtasks.put(id, subtask);
		updateEpicStatus(epicId);
		updateEpicTime(epicId);
	}

	@Override
	public void deleteTask(int id) {
		tasks.remove(id);
		historyManager.remove(id);
	}

	@Override
	public void deleteEpic(int id) {
		final Epic epic = epics.remove(id);
		historyManager.remove(id);
		for (Integer subtaskId : epic.getSubtaskIds()) {
			subtasks.remove(subtaskId);
			historyManager.remove(subtaskId);
		}
	}

	@Override
	public void deleteSubtask(int id) {
		Subtask subtask = subtasks.remove(id);
		if (subtask == null) {
			return;
		}
		Epic epic = epics.get(subtask.getEpicId());
		epic.removeSubtask(subtask.getId());
		historyManager.remove(subtask.getId());
		updateEpicStatus(epic.getId());
		updateEpicTime(epic.getId());
	}

	@Override
	public void deleteTasks() {
		historyManager.removeAll(tasks.keySet());
		tasks.clear();
	}

	@Override
	public void deleteSubtasks() {
		epics.values().stream().peek(Epic::cleanSubtaskIds)
				.peek(epic -> updateEpicStatus(epic.getId()))
				.peek(epic -> updateEpicTime(epic.getId()));
		historyManager.removeAll(subtasks.keySet());
		subtasks.clear();
	}

	@Override
	public void deleteEpics() {
		historyManager.removeAll(epics.keySet());
		epics.clear();
		historyManager.removeAll(subtasks.keySet());
		subtasks.clear();
	}

	@Override
	public List<Task> getHistory() {
		return historyManager.getHistory();
	}

	//	Добавьте метод проверяющий пересекается ли задача с любой другой в списке менеджера
	private boolean intersectTask(Task task1, Task task2) {
		if (task1.getStartTime().isBefore(task2.getStartTime()))
			return !task1.getStartTime().plus(task1.getDuration()).isBefore(task2.getStartTime());
		return !task2.getStartTime().plus(task2.getDuration()).isBefore(task1.getStartTime());
	}

	//	При добавлении или изменении задач и подзадач сначала выполните проверку на пересечение.
	//	Для этого используйте Stream API и метод, который вы реализовали в предыдущем пункте.
	private boolean intersectTask(Task task) {
		if (sorterTask.isEmpty()) return false;
		Optional<Task> first = sorterTask.stream().filter(task1 -> intersectTask(task1, task)).findFirst();
		return first.isPresent();
	}


	private void updateEpicTime(int epicId) {
		Epic epic = epics.get(epicId);
		List<Integer> subs = epic.getSubtaskIds();

		Optional<LocalDateTime> minStartTime = subs.stream().map(subtasks::get).map(Task::getStartTime).filter(Objects::nonNull).min(LocalDateTime::compareTo);
		epic.setStartTime(minStartTime.orElse(null));
		if (epic.getStartTime() == null)
			return;

		long sum = subs.stream().map(subtasks::get).map(Task::getDuration).filter(Objects::nonNull).mapToLong(Duration::getSeconds).sum();
		epic.setDuration(Duration.ofSeconds(sum));

		epic.setEndTime(epic.getStartTime().plus(epic.getDuration()));
	}

	private void updateEpicStatus(int epicId) {
		Epic epic = epics.get(epicId);
		List<Integer> subs = epic.getSubtaskIds();
		if (subs.isEmpty()) {
			epic.setStatus(NEW);
			return;
		}
		TaskStatus status = null;
		for (int id : subs) {
			final Subtask subtask = subtasks.get(id);
			if (status == null) {
				status = subtask.getStatus();
				continue;
			}

			if (status == subtask.getStatus()
					&& status != IN_PROGRESS) {
				continue;
			}
			epic.setStatus(IN_PROGRESS);
			return;
		}
		epic.setStatus(status);
	}
}
