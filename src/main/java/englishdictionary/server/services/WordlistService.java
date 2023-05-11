package englishdictionary.server.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import englishdictionary.server.models.User;
import englishdictionary.server.models.Word;
import englishdictionary.server.models.Wordlist;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Service
public class WordlistService {
    private Firestore firestore;

    public List<Wordlist> getAllUserWordLists(String userId) throws ExecutionException, InterruptedException {
        firestore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = firestore.collection("word_lists").whereEqualTo("user_id", userId).get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Wordlist> wordlists = new ArrayList<>();
        for (DocumentSnapshot document : documents)
            wordlists.add(document.toObject(Wordlist.class));
        return wordlists;
    }

    public List<Wordlist> getAllSystemWordLists() throws ExecutionException, InterruptedException {
        firestore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = firestore.collection("word_lists").whereEqualTo("user_id", "system").get();
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Wordlist> wordlists = new ArrayList<>();
        for (DocumentSnapshot document : documents)
            wordlists.add(document.toObject(Wordlist.class));
        return wordlists;
    }

    public Wordlist getWordListById(String wordListId) throws ExecutionException, InterruptedException {
        firestore = FirestoreClient.getFirestore();
        DocumentSnapshot documentSnapshot = firestore.collection("word_lists").document(wordListId).get().get();
        return documentSnapshot.toObject(Wordlist.class);
    }

    public Wordlist createWordlist(String wordlistName, String userId) throws ExecutionException, InterruptedException {
        firestore = FirestoreClient.getFirestore();
        DocumentSnapshot documentSnapshot = firestore.collection("users").document(userId).get().get();

        Wordlist wordlist = new Wordlist();
        wordlist.setName(wordlistName);
        wordlist.setUser(documentSnapshot.toObject(User.class));
        wordlist.setWords(new ArrayList<>());

        Map<String, Object> docData = new HashMap<>();
        docData.put("name", wordlist.getName());
        docData.put("user", wordlist.getUser());
        docData.put("words", wordlist.getWords());

        firestore.collection("word_lists").document().set(docData);
        return wordlist;
    }
}
