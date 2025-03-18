package httpserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import exceptions.BadRequestException;
import httpserver.Endpoint;
import manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {

    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = router.getEndpoint(exchange);
        try {
            switch (endpoint) {
                case GET_PRIORITIZED -> {
                    sendContent(exchange, gson.toJson(manager.getPrioritizedTasks()));
                }
                case UNKNOWN -> throw new BadRequestException();
            }
        } catch (BadRequestException e) {
            sendBadRequest(exchange);
        }
    }
}
