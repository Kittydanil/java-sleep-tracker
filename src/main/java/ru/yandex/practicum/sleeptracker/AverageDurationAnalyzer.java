package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public class AverageDurationAnalyzer implements Function<List<SleepingSession>, SleepAnalysisResult> {
    private static final String DESCRIPTION = "Средняя продолжительность сна";

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        return new SleepAnalysisResult(
                DESCRIPTION,
                String.format("%.2f", sessions.stream()
                .mapToDouble(SleepingSession::getDurationMinutes)
                .average()
                .orElse(0.0))
        );
    }
}
