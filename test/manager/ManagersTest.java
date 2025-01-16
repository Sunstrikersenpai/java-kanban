package manager;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {

    @Test
    void testGetDefault() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager);
    }
}