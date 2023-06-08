package englishdictionary.server.models.testing;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.cloud.firestore.DocumentReference;
import englishdictionary.server.models.testing.User;
import englishdictionary.server.models.Word;
import englishdictionary.server.serializer.DocumentReferencesDeserializer;
import englishdictionary.server.serializer.DocumentReferencesSerializer;
import englishdictionary.server.serializer.UserDeserializer;
import englishdictionary.server.serializer.UserSerializer;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Wordlist {
    private String wordlistId;
    private String name;

    @JsonSerialize(using = UserSerializer.class)
    @JsonDeserialize(using = UserDeserializer.class)
    private User user;
    private List<Word> words;
}
