package httpserver.handlers;

import com.google.gson.stream.JsonReader;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.BadRequestException;
import exceptions.IntersectionException;
import exceptions.NotFoundException;
import httpserver.Endpoint;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = router.getEndpoint(exchange);
        System.out.println(endpoint);
        System.out.println("Получен запрос " + exchange.getRequestMethod() + " " + exchange.getRequestURI());
        try {
            switch (endpoint) {
                case GET_ALL_TASKS -> {
                    sendContent(exchange, gson.toJson(manager.getAllTasks()));
                }
                case GET_TASK -> {
                    int id = getIdFromUri(exchange);
                    Optional<Task> task = manager.getTaskById(id);
                    if (task.isEmpty()) {
                        throw new NotFoundException("Task id: " + id + " not found");
                    }
                    sendContent(exchange, gson.toJson(task.get()));
                }
                case ADD_OR_UPD_TASK -> {
                    JsonReader jr = new JsonReader(new InputStreamReader(exchange.getRequestBody()));
                    Task task = gson.fromJson(jr, Task.class);
                    if (task.getId() == null) {
                        manager.addTask(task);
                    } else {
                        manager.updateTask(task);
                    }
                    sendFine(exchange);
                    jr.close();
                }
                case DELETE_TASK -> {
                    manager.deleteTask(getIdFromUri(exchange));
                    sendFine(exchange);
                }
                case UNKNOWN -> {
                    throw new BadRequestException();
                }
            }
        } catch (
                NotFoundException e) {
            sendNotFound(exchange);
        } catch (
                IntersectionException e) {
            sendIntersectionException(exchange, e.getMessage());
        } catch (
                BadRequestException e) {
            sendBadRequest(exchange);
        }
    }
}
