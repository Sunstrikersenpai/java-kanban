package httpserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.BadRequestException;
import httpserver.Endpoint;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {

    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = router.getEndpoint(exchange);
        try {
            switch (endpoint) {
                case GET_HISTORY -> {
                    List<Task> history = manager.getHistory();
                    sendContent(exchange, gson.toJson(history));
                }
                case UNKNOWN -> {
                    throw new BadRequestException();
                }
            }
        } catch (BadRequestException e) {
            sendBadRequest(exchange);
        }
    }
}
