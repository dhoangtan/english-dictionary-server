package englishdictionary.server.serializer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

import java.io.IOException;

public class DocumentReferencesDeserializer extends JsonDeserializer<DocumentReference> {
    private final Firestore firestore;
    public DocumentReferencesDeserializer() {
        this.firestore = FirestoreClient.getFirestore();
    }
    @Override
    public DocumentReference deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        String documentPath = jsonParser.getValueAsString();
        return firestore.document(documentPath);
    }
}
