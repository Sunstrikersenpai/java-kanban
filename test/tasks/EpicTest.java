package tasks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    Epic epic;

    @BeforeEach
    void setUp() {
        epic = new Epic("Epic 1", "Description 1");
        epic.setId(1);
    }

    @Test
    void cannotAddEpicAsSubtaskToItself() {
        epic.addSubtask(1);
        assertFalse(epic.getSubtaskIds().contains(1));
    }

    @Test
    void equalsIfSameId() {
        Epic sameIdEpic = new Epic("Epic 2", "Description 2");
        sameIdEpic.setId(1);
        assertEquals(epic, sameIdEpic);
    }
}