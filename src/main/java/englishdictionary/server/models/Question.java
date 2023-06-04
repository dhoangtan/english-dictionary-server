package englishdictionary.server.models;

import lombok.Data;

import java.util.List;

@Data
public class Question {
    private String question;
    private List<Answer> answers;
}
