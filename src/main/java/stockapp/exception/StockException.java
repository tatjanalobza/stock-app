package stockapp.exception;

import java.util.function.Supplier;

public class StockException extends RuntimeException {
    public static final String MSG_COULD_NOT_FIND_STOCK = "Stock could not be found";

    public StockException(String message, Object...o) { super(String.format(message, o));}

    public static Supplier<StockException> supplier(String message, Object...args) {
        return () -> new StockException(String.format(message, args));
    }
}
