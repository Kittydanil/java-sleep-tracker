package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleepTrackerAppTest {
    private static final LocalDateTime BASE = LocalDateTime.of(2026, 10, 1, 23, 0);

    private SleepingSession session(int startHour, int startMin, int endHour, int endMin, SleepQuality quality) {
        return new SleepingSession(
                BASE.plusHours(startHour).plusMinutes(startMin),
                BASE.plusHours(endHour).plusMinutes(endMin),
                quality
        );
    }

    private SleepingSession session(int day,
                                            int startHour, int startMin,
                                            int endDay, int endHour, int endMin,
                                            SleepQuality quality) {
        return new SleepingSession(
                LocalDateTime.of(2026, 10, day, startHour, startMin),
                LocalDateTime.of(2026, 10, endDay, endHour, endMin),
                quality
        );
    }


    // ── TotalSessionsAnalyzer ──

    @Test
    @DisplayName("TotalSessionsAnalyzer: пустой список — 0 сессий")
    void totalSessionsEmpty() {
        var result = new TotalSessionsAnalyzer().apply(Collections.emptyList());
        assertEquals(String.valueOf(0), result.getResult());
    }

    @Test
    @DisplayName("TotalSessionsAnalyzer: три сессии — результат 3")
    void totalSessionsThree() {
        var sessions = List.of(
                session(0, 0, 6, 0, SleepQuality.GOOD),
                session(24, 0, 30, 0, SleepQuality.NORMAL),
                session(48, 0, 54, 0, SleepQuality.BAD)
        );
        var result = new TotalSessionsAnalyzer().apply(sessions);
        assertEquals(String.valueOf(3), result.getResult());
    }

    // ── MinDurationAnalyzer ──

    @Test
    @DisplayName("MinDurationAnalyzer: выбирает самую короткую сессию")
    void minDurationCorrect() {
        var sessions = List.of(
                session(0, 0, 6, 0, SleepQuality.GOOD),    // 360 мин
                session(24, 0, 25, 30, SleepQuality.NORMAL), // 90 мин
                session(48, 0, 56, 0, SleepQuality.BAD)      // 480 мин
        );
        var result = new MinDurationAnalyzer().apply(sessions);
        assertEquals(String.valueOf(90L), result.getResult());
    }

    @Test
    @DisplayName("MinDurationAnalyzer: пустой список — 0")
    void minDurationEmpty() {
        var result = new MinDurationAnalyzer().apply(Collections.emptyList());
        assertEquals(String.valueOf(0L), result.getResult());
    }

    // ── MaxDurationAnalyzer ──

    @Test
    @DisplayName("MaxDurationAnalyzer: выбирает самую длинную сессию")
    void maxDurationCorrect() {
        var sessions = List.of(
                session(0, 0, 2, 0, SleepQuality.GOOD),     // 120 мин
                session(24, 0, 32, 0, SleepQuality.NORMAL),  // 480 мин
                session(48, 0, 49, 0, SleepQuality.BAD)      // 60 мин
        );
        var result = new MaxDurationAnalyzer().apply(sessions);
        assertEquals(String.valueOf(480L), result.getResult());
    }

    @Test
    @DisplayName("MaxDurationAnalyzer: пустой список — 0")
    void maxDurationEmpty() {
        var result = new MaxDurationAnalyzer().apply(Collections.emptyList());
        assertEquals(String.valueOf(0L), result.getResult());
    }

    // ── AverageDurationAnalyzer ──

    @Test
    @DisplayName("AverageDurationAnalyzer: корректное среднее")
    void averageDurationCorrect() {
        var sessions = List.of(
                session(0, 0, 2, 0, SleepQuality.GOOD),    // 120 мин
                session(24, 0, 26, 0, SleepQuality.NORMAL), // 120 мин
                session(48, 0, 52, 0, SleepQuality.BAD)      // 240 мин
        );
        var result = new AverageDurationAnalyzer().apply(sessions);
        // Среднее: (120 + 120 + 240) / 3 = 160.00
        assertEquals("160,00", result.getResult());
    }

    @Test
    @DisplayName("AverageDurationAnalyzer: пустой список — 0.00")
    void averageDurationEmpty() {
        var result = new AverageDurationAnalyzer().apply(Collections.emptyList());
        assertEquals("0,00", result.getResult());
    }

    // ── BadQualitySessionsAnalyzer ──

    @Test
    @DisplayName("BadQualitySessionsAnalyzer: считает только BAD")
    void badQualityCount() {
        var sessions = List.of(
                session(0, 0, 6, 0, SleepQuality.GOOD),
                session(24, 0, 30, 0, SleepQuality.BAD),
                session(48, 0, 54, 0, SleepQuality.BAD),
                session(72, 0, 78, 0, SleepQuality.NORMAL)
        );
        var result = new BadQualitySessionsAnalyzer().apply(sessions);
        assertEquals(String.valueOf(2L), result.getResult());
    }

    @Test
    @DisplayName("BadQualitySessionsAnalyzer: нет BAD — результат 0")
    void badQualityNone() {
        var sessions = List.of(
                session(0, 0, 6, 0, SleepQuality.GOOD),
                session(24, 0, 30, 0, SleepQuality.NORMAL)
        );
        var result = new BadQualitySessionsAnalyzer().apply(sessions);
        assertEquals(String.valueOf(0L), result.getResult());
    }

    // ── NoSleepNightsAnalyzer ──

    @Test
    @DisplayName("NoSleepNightsAnalyzer: нет ночей — результат 0")
    void noSleepNightsNone() {
        var result = new NoSleepNightsAnalyzer().apply(Collections.emptyList());
        assertEquals(String.valueOf(0), result.getResult());
    }

    @Test
    @DisplayName("NoSleepNightsAnalyzer: три ночи подряд не бессонные — результат 0")
    void noSleepNightsCovered() {
        var sessions = List.of(
                session(1, 23, 0, 2, 7, 0, SleepQuality.GOOD),
                session(2, 23, 0, 3, 7, 0, SleepQuality.GOOD),
                session(3, 23, 0, 4, 7, 0, SleepQuality.GOOD)
        );
        var result = new NoSleepNightsAnalyzer().apply(sessions);
        assertEquals(String.valueOf(0), result.getResult());
    }

    @Test
    @DisplayName("NoNoSleepNightsAnalyzer: разрыв в 2 ночи между сессиями — результат 2")
    void noSleepNightsGapBetweenSessions() {
        var sessions = List.of(
                session(1, 23, 0, 2, 7, 0, SleepQuality.GOOD),
                session(4, 23, 0, 5, 7, 0, SleepQuality.GOOD)
        );
        var result = new NoSleepNightsAnalyzer().apply(sessions);
        assertEquals(String.valueOf(2), result.getResult());
    }

    @Test
    @DisplayName("NoSleepNightsAnalyzer: Сессия ровно с 0:00 до 6:00 — результат 0")
    void noSleepNightsMidnightToSix() {
        var sessions = List.of(
                session(1, 0, 0, 1, 6, 0, SleepQuality.GOOD)
        );
        var result = new NoSleepNightsAnalyzer().apply(sessions);
        assertEquals(String.valueOf(0), result.getResult());
    }

    @Test
    @DisplayName("ChronotypeAnalyzer: чистая «Сова» — результат «Сова»")
    void chronotypePureOwl() {
        var sessions = List.of(
                session(1, 23, 30, 2, 9, 30, SleepQuality.GOOD),
                session(2, 0, 15, 3, 10, 0, SleepQuality.NORMAL),
                session(3, 23, 45, 4, 9, 15, SleepQuality.GOOD)
        );
        var result = new ChronotypeAnalyzer().apply(sessions);
        assertEquals("Сова", result.getResult());
    }

    @Test
    @DisplayName("ChronotypeAnalyzer: чистый «Жаворонок» — результат «Жаворонок»")
    void chronotypePureJay() {
        var sessions = List.of(
                session(1, 21, 30, 2, 6, 30, SleepQuality.GOOD),
                session(2, 20, 45, 3, 5, 45, SleepQuality.NORMAL),
                session(3, 21, 0, 4, 6, 0, SleepQuality.GOOD)
        );
        var result = new ChronotypeAnalyzer().apply(sessions);
        assertEquals("Жаворонок", result.getResult());
    }

    @Test
    @DisplayName("ChronotypeAnalyzer: большинство «Сова» при одном «Жаворонок» — результат «Сова»")
    void chronotypeOwlMajority() {
        var sessions = List.of(
                session(1, 23, 30, 2, 9, 30, SleepQuality.GOOD),
                session(2, 23, 45, 3, 10, 0, SleepQuality.NORMAL),
                session(3, 0, 15, 4, 9, 45, SleepQuality.GOOD),
                session(4, 21, 30, 5, 6, 30, SleepQuality.BAD)
        );
        var result = new ChronotypeAnalyzer().apply(sessions);
        assertEquals("Сова", result.getResult());
    }

    @Test
    @DisplayName("ChronotypeAnalyzer: ничья между «Сова» и «Жаворонок» — «Голубь»")
    void chronotypeTiePigeon() {
        var sessions = List.of(
                session(1, 23, 30, 2, 9, 30, SleepQuality.GOOD),
                session(2, 23, 45, 3, 9, 15, SleepQuality.NORMAL),
                session(3, 21, 30, 4, 6, 30, SleepQuality.GOOD),
                session(4, 21, 0, 5, 6, 0, SleepQuality.BAD)
        );
        var result = new ChronotypeAnalyzer().apply(sessions);
        assertEquals("Голубь", result.getResult());
    }

    @Test
    @DisplayName("ChronotypeAnalyzer: все сессии «Голубь» — результат «Голубь»")
    void chronotypePurePigeon() {
        var sessions = List.of(
                session(1, 22, 30, 2, 7, 30, SleepQuality.GOOD),
                session(2, 22, 15, 3, 8, 0, SleepQuality.NORMAL),
                session(3, 22, 45, 4, 7, 0, SleepQuality.GOOD)
        );
        var result = new ChronotypeAnalyzer().apply(sessions);
        assertEquals("Голубь", result.getResult());
    }

    @Test
    @DisplayName("ChronotypeAnalyzer: пустой список — «Голубь»")
    void chronotypeEmpty() {
        var result = new ChronotypeAnalyzer().apply(Collections.emptyList());
        assertEquals("Голубь", result.getResult());
    }
}