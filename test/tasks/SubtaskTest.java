package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {
    Subtask subtask;

    @BeforeEach
    void setUp() {
        subtask = new Subtask("Subtask 1", "Description 1", Status.NEW, 1);
        subtask.setId(1);
    }

    @Test
    void equalsIfSameId() {
        Subtask sameIdSubtask = new Subtask("Subtask 2", "Description 2", Status.NEW, 1);
        sameIdSubtask.setId(subtask.getId());
        assertEquals(subtask, sameIdSubtask);
    }

}