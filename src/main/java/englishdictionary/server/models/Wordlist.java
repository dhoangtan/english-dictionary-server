package englishdictionary.server.models;

import lombok.Data;

import java.util.List;

@Data
public class Wordlist {
    private String name;
    private User user;
    private List<Word> words;
}
