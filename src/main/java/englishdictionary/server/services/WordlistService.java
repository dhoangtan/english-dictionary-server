package englishdictionary.server.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import englishdictionary.server.models.WordList;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class WordlistService {
    public List<WordList> getAllUserWordLists(String userId) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = firestore.collection("word_lists").whereEqualTo("user_id", userId).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<WordList> wordLists = new ArrayList<>();
        for (DocumentSnapshot document : documents)
            wordLists.add(document.toObject(WordList.class));
        return wordLists;
    }

    public List<WordList> getAllSystemWordLists() throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = firestore.collection("word_lists").whereEqualTo("user_id", "system").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<WordList> wordLists = new ArrayList<>();
        for (DocumentSnapshot document : documents)
            wordLists.add(document.toObject(WordList.class));
        return wordLists;
    }

    public WordList getWordListById(String wordListId) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        DocumentSnapshot documentSnapshot = firestore.collection("word_lists").document(wordListId).get().get();
        return documentSnapshot.toObject(WordList.class);
    }
}
