package exceptions;

public class NotFoundException extends RuntimeException {

    public NotFoundException(int id) {
        super("Задача " + id + " не найдена");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
