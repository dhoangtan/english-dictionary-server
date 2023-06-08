package englishdictionary.server.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import lombok.SneakyThrows;

import java.io.IOException;

public class DocumentReferencesSerializer extends JsonSerializer<DocumentReference> {
    @SneakyThrows
    @Override
    public void serialize(DocumentReference documentReference, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (documentReference != null) {
            Firestore firestore = FirestoreClient.getFirestore();
            DocumentSnapshot documentSnapshot = documentReference.get().get();
            if (documentSnapshot.exists()) {
                Object data = documentSnapshot.getString("name");
                jsonGenerator.writeObject(data);
            }
        }
    }
}
