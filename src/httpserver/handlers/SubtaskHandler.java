package httpserver.handlers;

import com.google.gson.stream.JsonReader;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.BadRequestException;
import exceptions.IntersectionException;
import exceptions.NotFoundException;
import httpserver.Endpoint;
import manager.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler implements HttpHandler {

    public SubtaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = router.getEndpoint(exchange);
        System.out.println(endpoint);
        System.out.println("Получен запрос " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
        try {
            switch (endpoint) {
                case GET_ALL_SUBTASKS -> {
                    sendContent(exchange, gson.toJson(manager.getAllSubtasks()));
                }
                case GET_SUBTASK -> {
                    int id = getIdFromUri(exchange);
                    Optional<Subtask> task = manager.getSubtaskById(id);
                    if (task.isEmpty()) {
                        throw new NotFoundException("Task id: " + id + " not found");
                    }
                    sendContent(exchange, gson.toJson(task.get()));
                }
                case ADD_OR_UPD_SUBTASK -> {
                    try (JsonReader jr = new JsonReader(new InputStreamReader(exchange.getRequestBody()))) {
                        Subtask task = gson.fromJson(jr, Subtask.class);
                        if (task.getId() == null) {
                            manager.addSubtask(task);
                        } else {
                            manager.updateSubtask(task);
                        }
                        sendFine(exchange);
                    }
                }
                case DELETE_SUBTASK -> {
                    manager.deleteSubtask(getIdFromUri(exchange));
                    sendFine(exchange);
                }
                case UNKNOWN -> {
                    throw new BadRequestException();
                }
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (IntersectionException e) {
            sendIntersectionException(exchange, e.getMessage());
        } catch (BadRequestException e) {
            sendBadRequest(exchange);
        }
    }
}
