package sample.basic.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**Helps learn foreign words/phrases.*/
class VocabTrainer {
    private static final String CSV_DELIMITER = "\\|";
    private static final String GET_ANSWER_COMMAND = "?";

    /**{@code args[0]} can be either a file path or a classpath resource, can try out with "vocabtrainer/vocab.csv" resource.*/
    public static void main(String[] args) {
        if (args.length == 0) {
            printText("Path should be specified as an argument.");
            return;
        }

        List<Translation> translations;
        String csvPath = args[0];
        try {
            translations = readTranslationsFromCsv(csvPath);
        } catch (IOException e) {
            printText("Couldn't get translations from '%s'.".formatted(csvPath));
            return;
        }
        Collections.shuffle(translations);

        printSeparationLine();
        int size = translations.size();
        printText("Number of translations to train: [%d]. Type '%s' to get an answer.".formatted(size, GET_ANSWER_COMMAND));
        Scanner scanner = new Scanner(System.in);
        for (int i = 0; i < size; i++) {
            printSeparationLine();
            Translation translation = translations.get(i);
            System.out.println(translation.from);
            String answer = translation.to;
            boolean answered = process(scanner, answer);
            printProgress(answer, answered, size, i + 1);
        }
        printSeparationLine();
        printText("Completed!");
        printSeparationLine();
    }

    private static List<Translation> readTranslationsFromCsv(String path) throws IOException {
        List<Translation> translations = new ArrayList<>();
        for (String line : getLines(path)) {
            String[] parts = line.split(CSV_DELIMITER);
            translations.add(new Translation(parts[1], parts[0]));
        }
        return translations;
    }

    private static List<String> getLines(String path) {
        try {
            Path p = Paths.get(path);
            if (Files.exists(p))
                return Files.readAllLines(p);

            try (InputStream s = VocabTrainer.class.getClassLoader().getResourceAsStream(path)) {
                if (s != null) {
                    try (BufferedReader r = new BufferedReader(new InputStreamReader(s))) {
                        return r.lines().toList();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Couldn't load [\"%s\"].".formatted(path));
        }
        throw new RuntimeException("Couldn't find [\"%s\"].".formatted(path));
    }

    private static boolean process(Scanner scanner, String answer) {
        while (true) {
            String input = scanner.nextLine().trim();
            if (answer.equals(input)) {
                return true;
            }
            if (GET_ANSWER_COMMAND.equals(input)) {
                return false;
            }
            StringBuilder hint = new StringBuilder(answer.length());
            for (int j = 0; j < answer.length(); j++) {
                char ch = answer.charAt(j);
                if (ch == ' ' || ch == '.'  || ch == ',' || sameChar(input, j, ch)) {
                    hint.append(ch);
                } else {
                    hint.append("*");
                }
            }
            printText(hint.toString());
        }
    }

    private static boolean sameChar(String input, int index, char ch) {
        return index < input.length() && input.charAt(index) == ch;
    }

    private static void printText(String text) {
        System.out.println(text);
    }

    private static void printSeparationLine() {
        System.out.println("-".repeat(60));
    }

    private static void printProgress(String answer, boolean answered, int size, int completed) {
        if (!answered) {
            System.out.println(answer);
        }
        //prints in this format: "✅ [===  ] 60%" or "❗ [===  ] 60%"
        System.out.printf("%s [%s%s] %d%%%n", answered ? '✅' : '❗', "=".repeat(completed),
                " ".repeat(size - completed), completed * 100 / size);
    }

    private record Translation(String from, String to) {}
}
