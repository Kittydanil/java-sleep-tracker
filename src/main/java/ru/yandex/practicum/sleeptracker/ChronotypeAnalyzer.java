package ru.yandex.practicum.sleeptracker;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChronotypeAnalyzer implements Function<List<SleepingSession>, SleepAnalysisResult> {
    private static final LocalTime JAY_START = LocalTime.of(22, 0);
    private static final LocalTime JAY_END = LocalTime.of(7, 0);

    private static final LocalTime OWL_START = LocalTime.of(23, 0);
    private static final LocalTime OWL_END = LocalTime.of(9, 0);

    private static final LocalTime NOON = LocalTime.of(12, 0);
    private static final LocalTime SIX_AM = LocalTime.of(6, 0);

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        if (sessions.isEmpty()) {
            return new SleepAnalysisResult("Определенный хронотип", Chronotype.PIGEON.getDescription());
        }

        Map<Chronotype, Long> chronotypeCount = sessions.stream()
                .filter(this::isNightSession)
                .map(this::determineChronotype)
                .collect(Collectors.groupingBy(
                        chronotype -> chronotype,
                        Collectors.counting()
                ));

        return new SleepAnalysisResult(
                "Определенный хронотип",
                getDominantChronotype(chronotypeCount).getDescription()
        );
    }

    private boolean isNightSession(SleepingSession session) {
        return session.getStart().toLocalTime().isBefore(NOON) ||
                !session.getEnd().toLocalTime().isBefore(SIX_AM);
    }

    private Chronotype determineChronotype(SleepingSession session) {
        LocalTime start = session.getStart().toLocalTime();
        LocalTime end = session.getEnd().toLocalTime();

        boolean isOwl = start.isAfter(OWL_START) && end.isAfter(OWL_END);
        boolean isJay = start.isBefore(JAY_START) && end.isBefore(JAY_END);

        if (isOwl && !isJay) {
            return Chronotype.OWL;
        }
        if (isJay && !isOwl) {
            return Chronotype.JAY;
        }
        return Chronotype.PIGEON;
    }

    private Chronotype getDominantChronotype(Map<Chronotype, Long> chronotypeCount) {
        if (chronotypeCount.isEmpty()) {
            return Chronotype.PIGEON;
        }

        long maxCount = chronotypeCount.values().stream()
                .max(Long::compare)
                .orElse(0L);

        List<Chronotype> maxChronotypes = chronotypeCount.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Если несколько типов делят первое место — возвращаем «Голубь»
        if (maxChronotypes.size() > 1) {
            return Chronotype.PIGEON;
        }

        return maxChronotypes.get(0);
    }
}
