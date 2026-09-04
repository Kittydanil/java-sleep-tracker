package ru.yandex.practicum.sleeptracker;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SleepTrackerApp {
    private static final List<Function<List<SleepingSession>, SleepAnalysisResult>> ANALYSIS_FUNCTIONS =
            new ArrayList<>(List.of(
                    new TotalSessionsAnalyzer(),
                    new MinDurationAnalyzer(),
                    new MaxDurationAnalyzer(),
                    new AverageDurationAnalyzer(),
                    new BadQualitySessionsAnalyzer(),
                    new NoSleepNightsAnalyzer(),
                    new ChronotypeAnalyzer()
            ));
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Ошибка: не указан путь к файлу лога сна");
            System.out.println("Использование: java SleepTrackerApp" +
                    " C:\\Users\\KittyDanil\\java-sleep-tracker\\src\\main\\resources\\sleep_log.txt");
            return;
        }

        String filePath = args[0];

        List<SleepingSession> sessions = readFile(filePath);

        try {
            System.out.println("Анализ сна");
            System.out.println("--------------------------------");

            ANALYSIS_FUNCTIONS.stream()
                    .map(function -> function.apply(sessions))
                    .forEach(result -> {
                        System.out.println(result.getDescription() + ": " + result.getResult());
                    });

            System.out.println("--------------------------------");
            System.out.println("Анализ завершен.");
        } catch (Exception e) {
            System.out.println("Произошла ошибка при обработке файла: " + e.getMessage());
        }
    }

    private static List<SleepingSession> readFile(String filePath) {
        try (var lines =  Files.lines(Path.of(filePath))) {
            return lines
                    .map(String::trim)
                    .filter(line -> !line.isBlank())
                    .map(SleepTrackerApp::parseLine)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Ошибка чтения файла: " + filePath, e);
        }  catch (DateTimeParseException e) {
            throw new RuntimeException("Ошибка парсинга даты в файле: " + e.getMessage(), e);
        }
    }

    private static SleepingSession parseLine(String line) {
        String[] parts = line.split(";");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Некорректная строка: " + line);
        }

        LocalDateTime start = LocalDateTime.parse(parts[0].trim(), FORMATTER);
        LocalDateTime end = LocalDateTime.parse(parts[1].trim(), FORMATTER);
        SleepQuality quality = SleepQuality.valueOf(parts[2].trim());

        return new SleepingSession(start, end, quality);
    }
}