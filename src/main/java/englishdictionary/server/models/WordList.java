package englishdictionary.server.models;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class WordList {
    private String name;
    private String userId;
    private LocalDate date;
    private List<Word> words;
}
