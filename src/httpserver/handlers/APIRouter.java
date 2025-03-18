package httpserver.handlers;

import com.sun.net.httpserver.HttpExchange;
import httpserver.Endpoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class APIRouter {
    private final Map<String, Map<String, Endpoint>> router;
    private final Set<String> resources = Set.of("tasks", "epics", "subtasks");

    public APIRouter() {
        router = new HashMap<>();
        router.put("tasks", Map.of("GET", Endpoint.GET_ALL_TASKS, "POST", Endpoint.ADD_OR_UPD_TASK));
        router.put("epics", Map.of("GET", Endpoint.GET_ALL_EPICS, "POST", Endpoint.ADD_OR_UPD_EPIC));
        router.put("subtasks", Map.of("GET", Endpoint.GET_ALL_SUBTASKS, "POST", Endpoint.ADD_OR_UPD_SUBTASK));
        router.put("tasks/{id}", Map.of("GET", Endpoint.GET_TASK, "DELETE", Endpoint.DELETE_TASK));
        router.put("epics/{id}", Map.of("GET", Endpoint.GET_EPIC, "DELETE", Endpoint.DELETE_EPIC));
        router.put("subtasks/{id}", Map.of("GET", Endpoint.GET_SUBTASK, "DELETE", Endpoint.DELETE_SUBTASK));
        router.put("history", Map.of("GET", Endpoint.GET_HISTORY));
        router.put("prioritized", Map.of("GET", Endpoint.GET_PRIORITIZED));
        router.put("epics/{id}/subtasks", Map.of("GET", Endpoint.GET_EPIC_SUBTASK));
    }

    public Endpoint getEndpoint(HttpExchange exchange) {
        String[] path = exchange.getRequestURI().getPath().split("/");
        String method = exchange.getRequestMethod();

        if (path.length == 2) {
            return getFromRouter(path[1], method);
        }

        if (path.length == 3 && path[2].matches("\\d+") && resources.contains(path[1])) {
            String key = path[1] + "/{id}";
            return getFromRouter(key, method);
        }

        if (path.length == 4 && path[1].equals("epics") && path[2].matches("\\d+") && path[3].equals("subtasks")) {
            return getFromRouter("epics/{id}/subtasks", method);
        }

        return Endpoint.UNKNOWN;
    }

    private Endpoint getFromRouter(String key, String method) {
        return Optional.ofNullable(router.get(key))
                .map(m -> m.get(method))
                .orElse(Endpoint.UNKNOWN);
    }
}