package englishdictionary.server.models;

import lombok.Data;

import java.util.List;

@Data
public class Wordlist {
    private String wordlistId;
    private String name;
    private String userId;
    private List<Word> words;
}
