package httpserver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import httpserver.adapters.DurationAdapter;
import httpserver.adapters.LocalDateTimeAdapter;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTaskManagerTests {

    private TaskManager manager;
    private Server server;
    private final HttpClient client = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    private static final String URL_BASE = "http://localhost:8080";

    public HttpTaskManagerTests() {
    }

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new Server(manager);
        server.start();
    }

    @AfterEach
    public void shutDown() {
        server.stop();
    }

    @Test
    public void POST_GET_ShouldAddAndGetTask() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description", Status.NEW);
        taskSetDefaultTime(task);
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/tasks"))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());
        assertTrue(getResponse.body().contains("Task 1"));

        HttpRequest getRequestById = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/tasks/" + manager.getAllTasks().getFirst().getId()))
                .GET()
                .build();

        HttpResponse<String> getResponseId = client.send(getRequestById, HttpResponse.BodyHandlers.ofString());

        StringReader stringReader = new StringReader(getResponseId.body());
        Task task1 = gson.fromJson(stringReader, Task.class);

        assertEquals(200, getResponseId.statusCode());
        assertEquals(manager.getAllTasks().getFirst(), task1);
    }

    @Test
    public void shouldReturn404WhenTaskNotFound() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/tasks/999"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void deleteRequest_DeleteTaskFromManager() throws IOException, InterruptedException {
        Task task = new Task("Task 1", "Description", Status.NEW);
        taskSetDefaultTime(task);
        String taskJson = gson.toJson(task);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode());

        int id = manager.getAllTasks().getFirst().getId();

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/tasks/" + id))
                .DELETE()
                .build();
        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, deleteResponse.statusCode());
        assertTrue(manager.getAllTasks().isEmpty());
    }

    @Test
    public void shouldReturn406WhenIntersection() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Desc", Status.NEW);
        task1.setDuration(Duration.ofMinutes(30));
        task1.setStartTime(LocalDateTime.of(2025, 3, 20, 12, 15));
        String json1 = gson.toJson(task1);

        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json1))
                .build();
        HttpResponse<String> postResponse = client.send(postRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode());

        Task task2 = new Task("Task 2", "Desc", Status.NEW);
        task2.setDuration(Duration.ofMinutes(30));
        task2.setStartTime(LocalDateTime.of(2025, 3, 20, 12, 15));
        String json2 = gson.toJson(task2);

        HttpRequest postRequest2 = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json2))
                .build();

        HttpResponse<String> postResponse2 = client.send(postRequest2, HttpResponse.BodyHandlers.ofString());
        assertEquals(406, postResponse2.statusCode());
    }

    @Test
    public void getShouldReturnHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Desc", Status.NEW);
        taskSetDefaultTime(task1);
        manager.addTask(task1);

        Task task2 = new Task("Task 2", "Desc", Status.NEW);
        taskSetDefaultTime(task2);
        manager.addTask(task2);

        manager.getTaskById(3);
        manager.getTaskById(2);

        List<Task> historyFromManager = manager.getHistory();

        HttpRequest getHistory = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(getHistory, HttpResponse.BodyHandlers.ofString());
        List<Task> historyFromResponse = gson.fromJson(new StringReader(response.body()), new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertEquals(historyFromManager, historyFromResponse);
    }

    @Test
    public void getShouldReturnPrioritizedTask() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Desc", Status.NEW);
        taskSetDefaultTime(task1);
        manager.addTask(task1);

        Task task2 = new Task("Task 2", "Desc", Status.NEW);
        taskSetDefaultTime(task2);
        manager.addTask(task2);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(URL_BASE + "/prioritized")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Set<Task> priSet = gson.fromJson(new StringReader(response.body()), new TypeToken<Set<Task>>() {
        }.getType());

        assertEquals(200, response.statusCode());
        assertEquals(priSet, manager.getPrioritizedTasks());
    }

    @Test
    public void shouldAddAndGetAndDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Desc");
        String epicJson = gson.toJson(epic);

        HttpRequest postEpic = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();
        HttpResponse<String> postResponse = client.send(postEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode());

        HttpRequest getEpic = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/epics"))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getEpic, HttpResponse.BodyHandlers.ofString());
        List<Epic> epicFrResp = gson.fromJson(new StringReader(getResponse.body()), new TypeToken<List<Epic>>() {
        }.getType());

        assertEquals(200, getResponse.statusCode());
        assertEquals(manager.getEpicById(1).get(), epicFrResp.getFirst());

        HttpRequest delEpic = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/epics/1"))
                .DELETE()
                .build();

        HttpResponse<String> delResponse = client.send(delEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, delResponse.statusCode());
    }

    @Test
    public void shouldAddAndGetAndDeleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic 1", "Desc");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Desc", Status.NEW, 1);
        taskSetDefaultTime(subtask);
        String subtaskJson = gson.toJson(subtask);

        HttpRequest postSub = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .build();
        HttpResponse<String> postResponse = client.send(postSub, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, postResponse.statusCode());

        System.out.println(manager.getAllSubtasks());

        HttpRequest getSub = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/subtasks"))
                .GET()
                .build();
        HttpResponse<String> getResponse = client.send(getSub, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, getResponse.statusCode());
        assertTrue(getResponse.body().contains("Subtask 1"));

        HttpRequest delSub = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/subtasks/2"))
                .DELETE()
                .build();
        HttpResponse<String> delResponse = client.send(delSub, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, delResponse.statusCode());
        assertTrue(manager.getAllSubtasks().isEmpty());
    }

    protected static LocalDateTime startTime = LocalDateTime.of(2025, 2, 18, 10, 0);

    protected void taskSetDefaultTime(Task task) {
        task.setStartTime(startTime);
        task.setDuration(Duration.ofMinutes(30));
        startTime = startTime.plusMinutes(35);
    }
}
