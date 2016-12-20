package StreamAPI;

import static java.util.stream.Collectors.counting;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StreamAPI {
    private String fileName;
    private Map<String, Long> resultMap;

    public StreamAPI(String fileName) {
        this.fileName = fileName;
    }

    public void countWords() {
        resultMap = null;

        try (BufferedReader br = Files.newBufferedReader(Paths.get(fileName))) {
            resultMap = br.lines().map(w -> w.split("\\n")).flatMap(Arrays::stream)
                    .map(String::toLowerCase)
                    //some problems with "антон" the first word in file
                    .map(w -> w
                            .split("[[ ]*|[)]*|[(]*|[\\[]*|[\\]]*|[»]*|[«]*|[…]*|[,]*|[—]*|[;]*|[.]*|[:]*|[/]*|[!]*|[?]*|[+]*]+"))
                    .flatMap(Arrays::stream)
                    .filter(w -> !"".equals(w) && !"-".equals(w))
                    .collect(Collectors.groupingBy(Function.identity(), counting()))
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.
                            toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public Map<String, Long> getWordCountMap() {
        return resultMap;
    }

}




