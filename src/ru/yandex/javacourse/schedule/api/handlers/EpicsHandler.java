package ru.yandex.javacourse.schedule.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.schedule.exception.TaskNotFoundException;
import ru.yandex.javacourse.schedule.manager.TaskManager;
import ru.yandex.javacourse.schedule.tasks.Epic;
import ru.yandex.javacourse.schedule.tasks.Subtask;
import ru.yandex.javacourse.schedule.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        EpicsHandler.Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_EPICS: {
                handleGetEpics(exchange);
                break;
            }
            case GET_EPIC: {
                handleGetEpic(exchange);
                break;
            }
            case GET_SUBTASKS: {
                handleGetSubtask(exchange);
                break;
            }
            case CREATE_EPIC: {
                handleCreateEpic(exchange);
                break;
            }
            case DELETE_EPIC: {
                handleDeleteEpic(exchange);
                break;
            }
            default:
                sendNotFound(exchange);
        }
    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOptional = getEpicId(exchange);
        if (taskIdOptional.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        int taskId = taskIdOptional.get();
        try {
            List<Subtask> epicSubtasks = taskManager.getEpicSubtasks(taskId);
            sendText(exchange, gson.toJson(epicSubtasks));
        } catch (TaskNotFoundException ex) {
            sendNotFound(exchange);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOptional = getEpicId(exchange);
        if (taskIdOptional.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        int taskId = taskIdOptional.get();
        try {
            taskManager.deleteEpic(taskId);
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (TaskNotFoundException ex) {
            sendNotFound(exchange);
        }
    }

    private Optional<Epic> parseEpic(InputStream requestBody) throws IOException {
        byte[] bytes = requestBody.readAllBytes();
        String body = new String(bytes, StandardCharsets.UTF_8);

        if (body.isEmpty())
            return Optional.empty();

        return Optional.of(gson.fromJson(body, Epic.class));
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        Optional<Epic> optionalEpic = parseEpic(exchange.getRequestBody());
        try {
            taskManager.addNewEpic(optionalEpic.get());
            exchange.sendResponseHeaders(201, 0);
            exchange.close();
        } catch (Exception exception) {
            sendHasInteractions(exchange);
        }
    }

    private Optional<Integer> getEpicId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException | IndexOutOfBoundsException exception) {
            return Optional.empty();
        }
    }

    private void handleGetEpic(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOptional = getEpicId(exchange);
        if (taskIdOptional.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        int taskId = taskIdOptional.get();

        try {
            Task task = taskManager.getEpic(taskId);
            sendText(exchange, gson.toJson(task));
        } catch (TaskNotFoundException ex) {
            sendNotFound(exchange);
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        String json = gson.toJson(taskManager.getEpics());
        sendText(exchange, json);
    }

    private EpicsHandler.Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (requestMethod.equals("GET")) {
            if (pathParts.length == 3)
                return EpicsHandler.Endpoint.GET_EPIC;
            if (pathParts.length == 4)
                return EpicsHandler.Endpoint.GET_SUBTASKS;
            return EpicsHandler.Endpoint.GET_EPICS;
        }
        if (requestMethod.equals("POST")) {
            return EpicsHandler.Endpoint.CREATE_EPIC;
        }
        if (requestMethod.equals("DELETE")) {
            return EpicsHandler.Endpoint.DELETE_EPIC;
        }
        return EpicsHandler.Endpoint.UNKNOWN;
    }

    enum Endpoint {
        GET_EPICS,
        GET_EPIC,
        GET_SUBTASKS,
        CREATE_EPIC,
        DELETE_EPIC,
        UNKNOWN
    }
}
