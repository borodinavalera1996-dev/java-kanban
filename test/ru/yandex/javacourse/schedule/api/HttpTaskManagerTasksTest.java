package ru.yandex.javacourse.schedule.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import ru.yandex.javacourse.schedule.manager.InMemoryTaskManager;
import ru.yandex.javacourse.schedule.manager.TaskManager;
import ru.yandex.javacourse.schedule.tasks.Task;
import ru.yandex.javacourse.schedule.tasks.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class HttpTaskManagerTasksTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager, null);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerTasksTest() throws IOException {
    }

    @BeforeEach
    public void setUp() {
        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();
        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task(1, "Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull( "Задачи не возвращаются", tasksFromManager);
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());
        assertEquals("Некорректное имя задачи", tasksFromManager.get(0).getName(), "Test 1");
    }

    @Test
    public void testIntersectTimeAddTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task(1, "Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task);

        Task task1 = new Task(2, "Test 2", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        String taskJson = gson.toJson(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull( "Задачи не возвращаются", tasksFromManager);
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task(1, "Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task);

        Task task1 = new Task(1, "Test 2", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        // конвертируем её в JSON
        String taskJson = gson.toJson(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull( "Задачи не возвращаются", tasksFromManager);
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());
        assertEquals("Некорректное имя задачи", tasksFromManager.get(0).getName(), "Test 2");
    }

    @Test
    public void testNotFoundUpdateTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task(1, "Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task);

        Task task1 = new Task(2, "Test 2", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        // конвертируем её в JSON
        String taskJson = gson.toJson(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());

        List<Task> tasksFromManager = manager.getTasks();

        assertNotNull( "Задачи не возвращаются", tasksFromManager);
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task(1, "Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        Task task1 = gson.fromJson(response.body(), Task.class);

        assertEquals("Некорректное имя задачи", task1.getName(), "Test 1");
    }

    @Test
    public void testGetNotFoundTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task(1, "Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }
    @Test
    public void testGetOneTasks() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task(1, "Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertNotNull( "Задачи не возвращаются", tasks);
        assertEquals("Некорректное количество задач", 1, tasks.size());
        assertEquals("Некорректное имя задачи", tasks.get(0).getName(), "Test 1");
    }
    @Test
    public void testGetZeroTasks() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertNotNull( "Задачи не возвращаются", tasks);
        assertEquals("Некорректное количество задач", 0, tasks.size());
    }

    @Test
    public void testGetManyTasks() throws IOException, InterruptedException {
        // создаём задачу
        Task task1 = new Task(1, "Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task1);
        Task task2 = new Task(2, "Test 2", TaskStatus.NEW, "Testing task 2", Duration.ofMinutes(5), LocalDateTime.now().minusDays(1));
        manager.addNewTask(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertNotNull( "Задачи не возвращаются", tasks);
        assertEquals("Некорректное количество задач", 2, tasks.size());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task1 = new Task(1, "Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        assertEquals("Некорректное количество задач", 0, manager.getTasks().size());
    }

    @Test
    public void testNotFoundDeleteTask() throws IOException, InterruptedException {
        // создаём задачу
        Task task1 = new Task(1, "Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());

        assertEquals("Некорректное количество задач", 1, manager.getTasks().size());
    }

    class TaskListTypeToken extends TypeToken<List<Task>> {

    }
}
