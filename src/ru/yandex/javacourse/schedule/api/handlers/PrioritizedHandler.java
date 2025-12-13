package ru.yandex.javacourse.schedule.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.schedule.manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            handleGetPrioritizedTasks(exchange);
            return;
        }
        sendNotFound(exchange);
    }

    private void handleGetPrioritizedTasks(HttpExchange exchange) throws IOException {
        String json = gson.toJson(taskManager.getPrioritizedTasks());
        sendText(exchange, json);
    }
}
