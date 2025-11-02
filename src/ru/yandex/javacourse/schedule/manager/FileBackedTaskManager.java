package ru.yandex.javacourse.schedule.manager;

import ru.yandex.javacourse.schedule.tasks.*;

import java.io.*;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public static final String COLUMNS = "id,type,name,status,description,epic\n";
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
        readFromFile();
    }

    private void readFromFile() {
        try (FileReader fileReader = new FileReader(file); BufferedReader bufferedReader = new BufferedReader(fileReader)) {
            bufferedReader.readLine(); // skip first line
            while (bufferedReader.ready()) {
                Task task = fromString(bufferedReader.readLine());
                if (TaskType.EPIC.equals(task.getType())) {
                    super.addNewEpic((Epic) task);
                } else if (TaskType.SUBTASK.equals(task.getType())) {
                    super.addNewSubtask((Subtask)task);
                } else {
                    super.addNewTask(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Не удалось прочитать файл " + file.getName(), e);
        }
    }

    private Task fromString(String value) {
        String[] split = value.split(",");
        int id = Integer.parseInt(split[0]);
        String type = split[1];
        String name = split[2];
        TaskStatus status = TaskStatus.valueOf(split[3]);
        String description = split[4];

        findMaxId(id);
        if (TaskType.EPIC.toString().equals(type)) {
            return new Epic(id, name, status, description);
        } else if (TaskType.SUBTASK.toString().equals(type)) {
            int epicId = Integer.parseInt(split[5]);
            return new Subtask(id, name, status, description, epicId);
        } else {
            return new Task(id, name, status, description);
        }
    }

    private void findMaxId(int id) {
        if (id > generatorId) {
            generatorId = id;
        }
    }

    private void save() {
        String tasks = getTasksByString();
        try (FileWriter fileWriter = new FileWriter(file); BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(tasks);
        } catch (IOException e) {
           throw new ManagerSaveException("Не удалось сохранить задачи в файл " + file.getName(), e);
        }
    }

    private String getTasksByString() {
        StringBuilder tasks = new StringBuilder();
        tasks.append(COLUMNS);
        prepareTasks(tasks, getTasks());
        prepareTasks(tasks, getEpics());
        prepareTasks(tasks, getSubtasks());
        return tasks.toString();
    }

    private void prepareTasks(StringBuilder stringBuilder, List tasks) {
        for (Object task : tasks) {
            stringBuilder.append(task.toString()).append("\n");
        }
    }

    static FileBackedTaskManager loadFromFile(File file) {
        return new FileBackedTaskManager(file);
    }

    @Override
    public int addNewTask(Task task) {
        int i = super.addNewTask(task);
        save();
        return i;
    }

    @Override
    public int addNewEpic(Epic epic) {
        int i = super.addNewEpic(epic);
        save();
        return i;
    }

    @Override
    public Integer addNewSubtask(Subtask subtask) {
        Integer i = super.addNewSubtask(subtask);
        save();
        return i;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    public static void main(String[] args) throws IOException {
        File file = File.createTempFile("FileBackedTaskManager", ".csv");
        FileBackedTaskManager taskManager = loadFromFile(file);

        Task task = new Task( "Test 1", "Testing task 1", TaskStatus.NEW);
        taskManager.addNewTask(task);

        Epic epic = new Epic("Epic 1", "Testing epic 1");
        taskManager.addNewEpic(epic);

        Subtask s0 = new Subtask("Test 1", "Testing task 1",  TaskStatus.NEW, epic);
        taskManager.addNewSubtask(s0);
        epic.addSubtaskId(s0.getId());

        Subtask s1 = new Subtask( "Test 2", "Testing task 2",  TaskStatus.NEW, epic);
        taskManager.addNewSubtask(s1);
        epic.addSubtaskId(s1.getId());

        System.out.println("FIRST TASK MANAGER:");
        System.out.println("TASKS: " + taskManager.getTasks());
        System.out.println("SUBTASKS: " + taskManager.getSubtasks());
        System.out.println("EPICS: " + taskManager.getEpics());
        System.out.println("--------------------");


        FileBackedTaskManager taskManager2 = loadFromFile(file);

        System.out.println("FIRST SECOND MANAGER2:");
        System.out.println("TASKS: " + taskManager2.getTasks());
        System.out.println("SUBTASKS: " + taskManager2.getSubtasks());
        System.out.println("EPICS: " + taskManager2.getEpics());
    }
}
