package ru.yandex.javacourse.schedule.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.javacourse.schedule.manager.InMemoryTaskManager;
import ru.yandex.javacourse.schedule.manager.TaskManager;
import ru.yandex.javacourse.schedule.tasks.Epic;
import ru.yandex.javacourse.schedule.tasks.Subtask;
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

public class HttpTaskManagerSubtasksTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager, null);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerSubtasksTest() throws IOException {
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
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic(1, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        manager.addNewEpic(epic);
        // создаём задачу
        Subtask task = new Subtask(2,"Test 1",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(3), LocalDateTime.now(), epic.getId());
        // конвертируем её в JSON
        String taskJson = gson.toJson(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> tasksFromManager = manager.getSubtasks();

        assertNotNull( "Задачи не возвращаются", tasksFromManager);
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());
        assertEquals("Некорректное имя задачи", tasksFromManager.get(0).getName(), "Test 1");
    }

    @Test
    public void testIntersectTimeAddSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic);
        // создаём задачу
        Subtask task = new Subtask(2,"Test 1",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(3), LocalDateTime.now(), epic.getId());
        manager.addNewSubtask(task);

        Subtask task1 = new Subtask(3,"Test 1",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(3), LocalDateTime.now(), epic.getId());
        String taskJson = gson.toJson(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(406, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Subtask> tasksFromManager = manager.getSubtasks();

        assertNotNull( "Задачи не возвращаются", tasksFromManager);
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic);
        // создаём задачу
        Subtask task = new Subtask(2,"Test 1",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(3), LocalDateTime.now(), epic.getId());
        manager.addNewSubtask(task);

        Subtask task1 = new Subtask(2,"Test 2",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(3), LocalDateTime.now(), epic.getId());
        String taskJson = gson.toJson(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        List<Subtask> tasksFromManager = manager.getSubtasks();

        assertNotNull( "Задачи не возвращаются", tasksFromManager);
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());
        assertEquals("Некорректное имя задачи", tasksFromManager.get(0).getName(), "Test 2");
    }

    @Test
    public void testNotFoundUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic);
        // создаём задачу
        Subtask task = new Subtask(2,"Test 1",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(3), LocalDateTime.now(), epic.getId());
        manager.addNewSubtask(task);

        Subtask task1 = new Subtask(3,"Test 2",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(3), LocalDateTime.now(), epic.getId());
        String taskJson = gson.toJson(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());

        List<Subtask> tasksFromManager = manager.getSubtasks();

        assertNotNull( "Задачи не возвращаются", tasksFromManager);
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic);
        // создаём задачу
        Subtask task = new Subtask(2,"Test 1",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(3), LocalDateTime.now(), epic.getId());
        manager.addNewSubtask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        Subtask task1 = gson.fromJson(response.body(), Subtask.class);

        assertEquals("Некорректное имя задачи", task1.getName(), "Test 1");
    }

    @Test
    public void testGetNotFoundSubtask() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }
    @Test
    public void testGetOneSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic);
        // создаём задачу
        Subtask task = new Subtask(2,"Test 1",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(3), LocalDateTime.now(), epic.getId());
        manager.addNewSubtask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Subtask> tasks = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());

        assertNotNull( "Задачи не возвращаются", tasks);
        assertEquals("Некорректное количество задач", 1, tasks.size());
        assertEquals("Некорректное имя задачи", tasks.get(0).getName(), "Test 1");
    }
    @Test
    public void testGetZeroSubtasks() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Subtask> tasks = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());

        assertNotNull( "Задачи не возвращаются", tasks);
        assertEquals("Некорректное количество задач", 0, tasks.size());
    }

    @Test
    public void testGetManySubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic);
        // создаём задачу
        Subtask task = new Subtask(2,"Test 1",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(3), LocalDateTime.now(), epic.getId());
        manager.addNewSubtask(task);

        Subtask task1 = new Subtask(3,"Test 2",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(3), LocalDateTime.now().minusDays(1), epic.getId());
        manager.addNewSubtask(task1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Subtask> tasks = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());

        assertNotNull( "Задачи не возвращаются", tasks);
        assertEquals("Некорректное количество задач", 2, tasks.size());
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic);
        // создаём задачу
        Subtask task = new Subtask(2,"Test 1",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(3), LocalDateTime.now(), epic.getId());
        manager.addNewSubtask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        assertEquals("Некорректное количество задач", 0, manager.getSubtasks().size());
    }

    @Test
    public void testNotFoundDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic #1", "Epic1 description");
        manager.addNewEpic(epic);
        // создаём задачу
        Subtask task = new Subtask(2,"Test 1",  TaskStatus.NEW,"Testing task 1", Duration.ofHours(3), LocalDateTime.now(), epic.getId());
        manager.addNewSubtask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());

        assertEquals("Некорректное количество задач", 1, manager.getSubtasks().size());
    }

    class SubtaskListTypeToken extends TypeToken<List<Subtask>> {

    }
}
