package ru.yandex.practicum.sleeptracker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.function.Function;
import java.util.stream.LongStream;

public class NoSleepNightsAnalyzer implements Function<List<SleepingSession>, SleepAnalysisResult> {
    private static final LocalTime NIGHT_START = LocalTime.of(0, 0);
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);
    private static final LocalTime NOON = LocalTime.of(12, 0);

    private static final String DESCRIPTION = "Количество бессонных ночей";

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult(DESCRIPTION, 0);
        }

        LocalDate firstDate = sessions.getFirst().getStart().toLocalDate();
        LocalDate lastDate = sessions.getLast().getEnd().toLocalDate();

        LocalDate firstNight = sessions.getFirst().getStart().toLocalTime().isBefore(NOON)
                ? firstDate
                : firstDate.plusDays(1);

        long totalNights = ChronoUnit.DAYS.between(firstNight, lastDate) + 1;

        long noSleepNights = LongStream.range(0, totalNights)
                .mapToObj(firstNight::plusDays)
                .filter(night -> isNightWithoutSleep(night, sessions))
                .count();

        return new SleepAnalysisResult(DESCRIPTION, noSleepNights);
    }

    private boolean isNightWithoutSleep(LocalDate night, List<SleepingSession> sessions) {
        LocalDateTime nightStart = LocalDateTime.of(night, NIGHT_START);
        LocalDateTime nightEnd = LocalDateTime.of(night, NIGHT_END);

        return sessions.stream()
                .noneMatch(session ->
                        session.getStart().isBefore(nightEnd) &&
                                session.getEnd().isAfter(nightStart)
                );
    }
}
