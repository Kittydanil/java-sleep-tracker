package ru.yandex.practicum.sleeptracker;

import java.util.List;
import java.util.function.Function;

public class AverageDurationAnalyzer implements Function<List<SleepingSession>, SleepAnalysisResult> {
    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        return new SleepAnalysisResult(
                "Средняя продолжительность сна",
                String.format("%.2f", sessions.stream()
                .mapToDouble(SleepingSession::getDurationMinutes)
                .average()
                .orElse(0.0))
        );
    }
}
