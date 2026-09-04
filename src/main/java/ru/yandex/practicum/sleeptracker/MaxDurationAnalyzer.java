package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public class MaxDurationAnalyzer implements Function<List<SleepingSession>, SleepAnalysisResult> {
    private static final String DESCRIPTION = "Максимальная продолжительность сна в минутах";

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        return new SleepAnalysisResult(
                DESCRIPTION,
                sessions.stream()
                .mapToLong(SleepingSession::getDurationMinutes)
                .max()
                .orElse(0)
        );
    }
}