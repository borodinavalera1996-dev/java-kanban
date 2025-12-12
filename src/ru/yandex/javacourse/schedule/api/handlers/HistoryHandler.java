package ru.yandex.javacourse.schedule.api.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.javacourse.schedule.manager.HistoryManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    private final HistoryManager historyManager;

    private final Gson gson;

    public HistoryHandler(HistoryManager historyManager, Gson gson) {
        this.historyManager = historyManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            handleGetHistory(exchange);
            return;
        }
        sendNotFound(exchange);
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        String json = gson.toJson(historyManager.getHistory());
        sendText(exchange, json);
    }
}
