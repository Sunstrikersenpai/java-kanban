package httpserver.handlers;

import com.google.gson.stream.JsonReader;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.BadRequestException;
import exceptions.IntersectionException;
import exceptions.NotFoundException;
import httpserver.Endpoint;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = router.getEndpoint(exchange);
        System.out.println(endpoint);
        System.out.println("Получен запрос " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
        try {
            switch (endpoint) {
                case GET_ALL_EPICS -> {
                    sendContent(exchange, gson.toJson(manager.getAllEpics()));
                }
                case GET_EPIC -> {
                    int id = getIdFromUri(exchange);
                    Optional<Epic> task = manager.getEpicById(id);
                    if (task.isEmpty()) {
                        throw new NotFoundException("Task id: " + id + " not found");
                    }
                    sendContent(exchange, gson.toJson(task.get()));
                }
                case ADD_OR_UPD_EPIC -> {
                    try (JsonReader jr = new JsonReader(new InputStreamReader(exchange.getRequestBody()))) {
                        Epic task = gson.fromJson(jr, Epic.class);
                        if (task.getId() == null) {
                            manager.addEpic(task);
                        } else {
                            manager.updateEpic(task);
                        }
                        sendFine(exchange);
                    }
                }
                case GET_EPIC_SUBTASK -> {
                    int id = getIdFromUri(exchange);
                    List<Subtask> subs = manager.getSubtasksOfEpic(id);
                    sendContent(exchange, gson.toJson(subs));
                }
                case DELETE_EPIC -> {
                    manager.deleteEpic(getIdFromUri(exchange));
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
