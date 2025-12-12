package ru.yandex.javacourse.schedule.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.schedule.manager.TaskManager;
import ru.yandex.javacourse.schedule.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager taskManager;

    private final Gson gson;

    public TasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        TasksHandler.Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_TASKS: {
                handleGetTasks(exchange);
                break;
            }
            case GET_TASK: {
                handleGetTask(exchange);
                break;
            }
            case CREATE_TASK: {
                handleCreateTask(exchange);
                break;
            }
            case UPDATE_TASK: {
                handleUpdateTask(exchange);
                break;
            }
            case DELETE_TASK: {
                handleDeleteTask(exchange);
                break;
            }
            default:
                sendNotFound(exchange);
        }
    }

    private void handleDeleteTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOptional = getTaskId(exchange);
        if (taskIdOptional.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        int taskId = taskIdOptional.get();
        if (taskManager.getTask(taskId) == null) {
            sendNotFound(exchange);
            return;
        }
        taskManager.deleteTask(taskId);
        exchange.sendResponseHeaders(200, 0);
        exchange.close();
    }

    private void handleUpdateTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOptional = getTaskId(exchange);
        if (taskIdOptional.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        int taskId = taskIdOptional.get();
        if (taskManager.getTask(taskId) == null) {
            sendNotFound(exchange);
            return;
        }
        Optional<Task> optionalTask = parseTask(exchange.getRequestBody());
        taskManager.updateTask(optionalTask.get());
        exchange.sendResponseHeaders(201, 0);
        exchange.close();
    }

    private Optional<Task> parseTask(InputStream requestBody) throws IOException {
        byte[] bytes = requestBody.readAllBytes();
        String body = new String(bytes, StandardCharsets.UTF_8);

        if (body.isEmpty())
            return Optional.empty();

        return Optional.of(gson.fromJson(body, Task.class));

    }

    private void handleCreateTask(HttpExchange exchange) throws IOException {
        Optional<Task> optionalTask = parseTask(exchange.getRequestBody());
        try {
            taskManager.addNewTask(optionalTask.get());
            exchange.sendResponseHeaders(201, 0);
            exchange.close();
        } catch (Exception exception) {
            sendHasInteractions(exchange);
        }
    }

    private Optional<Integer> getTaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException | IndexOutOfBoundsException exception) {
            return Optional.empty();
        }
    }

    private void handleGetTask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOptional = getTaskId(exchange);
        if (taskIdOptional.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        int taskId = taskIdOptional.get();

        Task task = taskManager.getTask(taskId);
        if (task == null) {
            sendNotFound(exchange);
            return;
        }

        String json = gson.toJson(task);
        sendText(exchange, json);
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        String json = gson.toJson(taskManager.getTasks());
        sendText(exchange, json);
    }

    private TasksHandler.Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (requestMethod.equals("GET")) {
            if (pathParts.length == 3)
                return TasksHandler.Endpoint.GET_TASK;
            return TasksHandler.Endpoint.GET_TASKS;
        }
        if (requestMethod.equals("POST")) {
            if (pathParts.length == 3)
                return TasksHandler.Endpoint.UPDATE_TASK;
            return TasksHandler.Endpoint.CREATE_TASK;
        }
        if (requestMethod.equals("DELETE")) {
            return TasksHandler.Endpoint.DELETE_TASK;
        }
        return TasksHandler.Endpoint.UNKNOWN;
    }

    enum Endpoint {GET_TASKS, GET_TASK, CREATE_TASK, UPDATE_TASK, DELETE_TASK, UNKNOWN}
}
