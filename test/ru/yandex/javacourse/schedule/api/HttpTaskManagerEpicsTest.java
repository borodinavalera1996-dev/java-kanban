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

public class HttpTaskManagerEpicsTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager, null);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerEpicsTest() throws IOException {
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
    public void testAddEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic(1, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        // конвертируем её в JSON
        String taskJson = gson.toJson(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        // вызываем рест, отвечающий за создание задач
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(201, response.statusCode());

        // проверяем, что создалась одна задача с корректным именем
        List<Epic> tasksFromManager = manager.getEpics();

        assertNotNull( "Задачи не возвращаются", tasksFromManager);
        assertEquals("Некорректное количество задач", 1, tasksFromManager.size());
        assertEquals("Некорректное имя задачи", tasksFromManager.get(0).getName(), "Epic 1");
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic(1, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        manager.addNewEpic(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        Epic task1 = gson.fromJson(response.body(), Epic.class);

        assertEquals("Некорректное имя задачи", task1.getName(), "Epic 1");
    }

    @Test
    public void testGetNotFoundSubtaskByEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic(1, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        manager.addNewEpic(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }
    @Test
    public void testGetSubtaskByEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic(1, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        manager.addNewEpic(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Subtask> tasks = gson.fromJson(response.body(), new SubtaskListTypeToken().getType());

        assertNotNull( "Задачи не возвращаются", tasks);
        assertEquals("Некорректное количество задач", 0, tasks.size());
    }

    @Test
    public void testGetNotFoundEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic(1, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        manager.addNewEpic(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());
    }
    @Test
    public void testGetOneEpics() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic = new Epic(1, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        manager.addNewEpic(epic);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Epic> tasks = gson.fromJson(response.body(), new EpicListTypeToken().getType());

        assertNotNull( "Задачи не возвращаются", tasks);
        assertEquals("Некорректное количество задач", 1, tasks.size());
        assertEquals("Некорректное имя задачи", tasks.get(0).getName(), "Epic 1");
    }
    @Test
    public void testGetZeroEpics() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Epic> tasks = gson.fromJson(response.body(), new EpicListTypeToken().getType());

        assertNotNull( "Задачи не возвращаются", tasks);
        assertEquals("Некорректное количество задач", 0, tasks.size());
    }

    @Test
    public void testGetManyEpics() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic1 = new Epic(1, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        manager.addNewEpic(epic1);
        Epic epic2 = new Epic(2, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        manager.addNewEpic(epic2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Epic> tasks = gson.fromJson(response.body(), new EpicListTypeToken().getType());

        assertNotNull( "Задачи не возвращаются", tasks);
        assertEquals("Некорректное количество задач", 2, tasks.size());
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic1 = new Epic(1, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        manager.addNewEpic(epic1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        assertEquals("Некорректное количество задач", 0, manager.getEpics().size());
    }

    @Test
    public void testNotFoundDeleteEpic() throws IOException, InterruptedException {
        // создаём задачу
        Epic epic1 = new Epic(1, "Epic 1",  TaskStatus.NEW, "Testing epic 1");
        manager.addNewEpic(epic1);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(404, response.statusCode());

        assertEquals("Некорректное количество задач", 1, manager.getEpics().size());
    }

    class EpicListTypeToken extends TypeToken<List<Epic>> {

    }

    class SubtaskListTypeToken extends TypeToken<List<Subtask>> {

    }
}
