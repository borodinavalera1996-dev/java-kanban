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

public class HttpTaskManagerPrioririzedTest {

    // создаём экземпляр InMemoryTaskManager
    TaskManager manager = new InMemoryTaskManager();
    // передаём его в качестве аргумента в конструктор HttpTaskServer
    HttpTaskServer taskServer = new HttpTaskServer(manager, null);
    Gson gson = HttpTaskServer.getGson();

    public HttpTaskManagerPrioririzedTest() throws IOException {
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
    public void testGetOnePrioritizedTasks() throws IOException, InterruptedException {
        // создаём задачу
        Task task = new Task(1, "Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
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
    public void testGetZeroPrioritizedTasks() throws IOException, InterruptedException {
        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertNotNull( "Задачи не возвращаются", tasks);
        assertEquals("Некорректное количество задач", 0, tasks.size());
    }

    @Test
    public void testGetManyPrioritizedTasks() throws IOException, InterruptedException {
        // создаём задачу
        Task task1 = new Task(1, "Test 1", TaskStatus.NEW, "Testing task 1", Duration.ofMinutes(5), LocalDateTime.now());
        manager.addNewTask(task1);
        Task task2 = new Task(2, "Test 2", TaskStatus.NEW, "Testing task 2", Duration.ofMinutes(5), LocalDateTime.now().minusDays(1));
        manager.addNewTask(task2);

        // создаём HTTP-клиент и запрос
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // проверяем код ответа
        assertEquals(200, response.statusCode());

        List<Task> tasks = gson.fromJson(response.body(), new TaskListTypeToken().getType());

        assertNotNull( "Задачи не возвращаются", tasks);
        assertEquals("Некорректное количество задач", 2, tasks.size());
        assertEquals("Некорректный id задачи", 2, tasks.get(0).getId());
        assertEquals("Некорректный id задачи", 1, tasks.get(1).getId());
    }

    class TaskListTypeToken extends TypeToken<List<Task>> {

    }
}
