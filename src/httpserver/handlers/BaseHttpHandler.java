package httpserver.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import httpserver.adapters.DurationAdapter;
import httpserver.adapters.LocalDateTimeAdapter;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

public class BaseHttpHandler {
    protected final Charset charset = StandardCharsets.UTF_8;
    protected final TaskManager manager;
    protected final APIRouter router;
    protected final Gson gson;

    protected BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
        this.router = new APIRouter();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }

    public void sendContent(HttpExchange exchange, String message) throws IOException {
        byte[] response = message.getBytes(charset);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, response.length);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    public void sendFine(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(201, 0);
        exchange.close();
    }

    public void sendNotFound(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(404, 0);
        exchange.close();
    }

    public void sendIntersectionException(HttpExchange exchange, String message) throws IOException {
        byte[] response = message.getBytes(charset);
        exchange.getResponseHeaders().add("Content-Type", "plain/text");
        exchange.sendResponseHeaders(406, 0);
        exchange.getResponseBody().write(response);
        exchange.close();
    }

    public void sendBadRequest(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(400, 0);
        exchange.close();
    }

    protected int getIdFromUri(HttpExchange exchange) {
        String[] path = exchange.getRequestURI().getPath().split("/");
        return Integer.parseInt(path[2]);
    }

}
