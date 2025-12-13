package ru.yandex.javacourse.schedule.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.schedule.exception.TaskNotFoundException;
import ru.yandex.javacourse.schedule.manager.TaskManager;
import ru.yandex.javacourse.schedule.tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {

    public SubtasksHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        SubtasksHandler.Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod());

        switch (endpoint) {
            case GET_SUBTASKS: {
                handleGetSubtasks(exchange);
                break;
            }
            case GET_SUBTASK: {
                handleGetSubtask(exchange);
                break;
            }
            case CREATE_SUBTASK: {
                handleCreateSubtask(exchange);
                break;
            }
            case UPDATE_SUBTASK: {
                handleUpdateSubtask(exchange);
                break;
            }
            case DELETE_SUBTASK: {
                handleDeleteSubtask(exchange);
                break;
            }
            default:
                sendNotFound(exchange);
        }
    }

    private void handleDeleteSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOptional = getSubtaskId(exchange);
        if (taskIdOptional.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        int taskId = taskIdOptional.get();
        try {
            taskManager.deleteSubtask(taskId);
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (TaskNotFoundException ex) {
            sendNotFound(exchange);
        }
    }

    private void handleUpdateSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOptional = getSubtaskId(exchange);
        if (taskIdOptional.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        int taskId = taskIdOptional.get();
        try {
            Optional<Subtask> optionalSubtask = parseSubtask(exchange.getRequestBody());
            taskManager.updateSubtask(optionalSubtask.get());
            exchange.sendResponseHeaders(201, 0);
            exchange.close();
        } catch (TaskNotFoundException ex) {
            sendNotFound(exchange);
        }
    }

    private Optional<Subtask> parseSubtask(InputStream requestBody) throws IOException {
        byte[] bytes = requestBody.readAllBytes();
        String body = new String(bytes, StandardCharsets.UTF_8);

        if (body.isEmpty())
            return Optional.empty();

        return Optional.of(gson.fromJson(body, Subtask.class));
    }

    private void handleCreateSubtask(HttpExchange exchange) throws IOException {
        Optional<Subtask> optionalSubtask = parseSubtask(exchange.getRequestBody());
        try {
            taskManager.addNewSubtask(optionalSubtask.get());
            exchange.sendResponseHeaders(201, 0);
            exchange.close();
        } catch (Exception exception) {
            sendHasInteractions(exchange);
        }
    }

    private Optional<Integer> getSubtaskId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException | IndexOutOfBoundsException exception) {
            return Optional.empty();
        }
    }

    private void handleGetSubtask(HttpExchange exchange) throws IOException {
        Optional<Integer> taskIdOptional = getSubtaskId(exchange);
        if (taskIdOptional.isEmpty()) {
            sendNotFound(exchange);
            return;
        }
        int taskId = taskIdOptional.get();

        try {
            Subtask task = taskManager.getSubtask(taskId);
            sendText(exchange, gson.toJson(task));
        } catch (TaskNotFoundException ex) {
            sendNotFound(exchange);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        String json = gson.toJson(taskManager.getSubtasks());
        sendText(exchange, json);
    }

    private SubtasksHandler.Endpoint getEndpoint(String requestPath, String requestMethod) {
        String[] pathParts = requestPath.split("/");

        if (requestMethod.equals("GET")) {
            if (pathParts.length == 3)
                return SubtasksHandler.Endpoint.GET_SUBTASK;
            return SubtasksHandler.Endpoint.GET_SUBTASKS;
        }
        if (requestMethod.equals("POST")) {
            if (pathParts.length == 3)
                return SubtasksHandler.Endpoint.UPDATE_SUBTASK;
            return SubtasksHandler.Endpoint.CREATE_SUBTASK;
        }
        if (requestMethod.equals("DELETE")) {
            return SubtasksHandler.Endpoint.DELETE_SUBTASK;
        }
        return SubtasksHandler.Endpoint.UNKNOWN;
    }

    enum Endpoint {
        GET_SUBTASKS,
        GET_SUBTASK,
        CREATE_SUBTASK,
        UPDATE_SUBTASK,
        DELETE_SUBTASK,
        UNKNOWN
    }
}
