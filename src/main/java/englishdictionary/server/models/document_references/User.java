package englishdictionary.server.models.document_references;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.cloud.firestore.DocumentReference;
import com.google.firebase.auth.FirebaseAuthException;
import englishdictionary.server.serializer.*;
import englishdictionary.server.services.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


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

    public Map<String, Object> toHashMap() {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("email", email);
        hashMap.put("fullName", fullName);
        hashMap.put("gender", gender);
        hashMap.put("level", level);
        hashMap.put("occupation", occupation);
        hashMap.put("password", password);
        return hashMap;
    }

    public DocumentReference toDocumentReference() {
        UserService userService = new UserService();
        try {
            return userService.getUserDocumentReferenceById(userService.getUserId(email));
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException(e);
        }
    }
}
