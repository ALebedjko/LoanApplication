package io.fourfinanceit.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

public class DateUtils {
    private static Optional<LocalDateTime> now = Optional.empty();

    public static LocalDateTime now() {
        return now.orElseGet(() -> LocalDateTime.now());
    }

    public static LocalDate today() {
        return now().toLocalDate();
    }

    public static void setNow(LocalDateTime now) {
        DateUtils.now = Optional.of(now);
    }

    public static void reset() {
        DateUtils.now = Optional.empty();
    }
}
