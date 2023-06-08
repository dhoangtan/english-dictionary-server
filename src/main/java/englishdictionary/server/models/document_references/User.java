package englishdictionary.server.models.document_references;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.cloud.firestore.DocumentReference;
import englishdictionary.server.serializer.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String email;
    private String fullName;
    @JsonSerialize(using = DocumentReferencesSerializer.class)
    @JsonDeserialize(using = DocumentReferencesDeserializer.class)
    private DocumentReference gender;
    @JsonSerialize(using = DocumentReferencesSerializer.class)
    @JsonDeserialize(using = DocumentReferencesDeserializer.class)
    private DocumentReference level;
    @JsonSerialize(using = DocumentReferencesSerializer.class)
    @JsonDeserialize(using = DocumentReferencesDeserializer.class)
    private DocumentReference occupation;
    private String password;
}
