package StreamAPI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class Main {
    public static void streamVersion(String readFileName, Path writingFile) {
        StreamAPI wordCounter = new StreamAPI(readFileName);
        wordCounter.countWords();

        Map<String, Long> resultMap = wordCounter.getWordCountMap();

        try (BufferedWriter writer = Files.newBufferedWriter(writingFile)) {
            resultMap.forEach((k, v) -> {
                try {
                    writer.write(k + " : " + v + "\n");
                } catch (IOException e) {
                    throw new RuntimeException();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static void handVersion(String readFileName, Path writingFile) {
        Map<String, Integer> resultMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(readFileName), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (String token : line
                        .split("[[ ]*|[)]*|[(]*|[\\[]*|[\\]]*|[»]*|[«]*|[…]*|[,]*|[—]*|[;]*|[.]*|[:]*|[/]*|[!]*|[?]*|[+]*]+")) {
                    token = token.toLowerCase();
                    if (token.equals("") || token.equals("-")) {
                        continue;
                    }
                    if (resultMap.containsKey(token)) {
                        Integer value = resultMap.get(token);
                        value++;
                        resultMap.put(token, value);
                    } else {
                        resultMap.put(token, 1);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }

        //sort and write to file
        Object[] keys = resultMap.keySet().toArray();
        Arrays.sort(keys);
        try (BufferedWriter writer = Files.newBufferedWriter(writingFile)) {
            for (Object key : keys) {
                writer.write(key + " : " + resultMap.get(key) + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public static boolean compareTwoFiles(String firstFile, String secondFile) {
        try (BufferedReader firstReader = new BufferedReader(
                new InputStreamReader(new FileInputStream(firstFile), StandardCharsets.UTF_8));
                BufferedReader secondReader = new BufferedReader(
                        new InputStreamReader(new FileInputStream(secondFile),
                                StandardCharsets.UTF_8))) {

            String firstFileLine, secondFileLine;

            while ((firstFileLine = firstReader.readLine()) != null) {
                if ((secondFileLine = secondReader.readLine()) == null) {
                    return false;
                }
                if (!firstFileLine.equals(secondFileLine)) {
                    System.out.println((firstFileLine + " != " + secondFileLine));
                    return false;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException();
        }
        return true;
    }

    public static void main(String[] args) {

        String inputFileName = "CherryGarden.txt";
        String streamFileName = "streamResult.txt";
        String handFileName = "handResult.txt";

        Path handWritingPath = Paths.get(handFileName);
        Path streamWritingPath = Paths.get(streamFileName);

        //Считают слова и пишут результат в файлы
        streamVersion(inputFileName, handWritingPath);
        handVersion(inputFileName, streamWritingPath);

        System.out.println(compareTwoFiles(streamFileName, handFileName));
    }
}