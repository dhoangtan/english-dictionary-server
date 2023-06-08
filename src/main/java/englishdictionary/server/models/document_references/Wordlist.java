package englishdictionary.server.models.document_references;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import englishdictionary.server.models.Word;
import englishdictionary.server.serializer.UserDeserializer;
import englishdictionary.server.serializer.UserSerializer;
import lombok.Data;

import java.util.List;

@Data
public class Wordlist {
    private String wordlistId;
    private String name;

    @JsonSerialize(using = UserSerializer.class)
    @JsonDeserialize(using = UserDeserializer.class)
    private User user;
    private List<Word> words;
}
