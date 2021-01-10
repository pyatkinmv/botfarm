package ru.pyatkinmv;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.function.Supplier;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {
    private static long DEFAULT_WAIT_MILLIS = 300;

    @SneakyThrows
    public static void runAndWait(Runnable runnable, long millis) {
        runnable.run();
        Thread.sleep(millis);
    }

    public static void runAndWait(Runnable runnable) {
        runAndWait(runnable, DEFAULT_WAIT_MILLIS);
    }

    @SneakyThrows
    public static <T> T supplyAndWait(Supplier<T> supplier, long millis) {
        final T result = supplier.get();
        Thread.sleep(millis);
        return result;
    }

    public static <T> T supplyAndWait(Supplier<T> supplier) {
        return supplyAndWait(supplier, DEFAULT_WAIT_MILLIS);
    }

//    public static <T, R> void runAndWait(Function<T, R> function, T item, long millis) {
//
//    }
}
