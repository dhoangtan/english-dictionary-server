package englishdictionary.server.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import englishdictionary.server.models.Word;
import englishdictionary.server.models.Wordlist;
import org.springframework.stereotype.Service;

import javax.print.Doc;
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
        for (DocumentSnapshot document : documents) {
            Wordlist wordlist = document.toObject(Wordlist.class);
            wordlist.setWordlistId(document.getId());
            // NOTE: somehow userId cannot be parsed
            //      manually set userId is required.
            wordlist.setUserId(document.get("user_id").toString());
            wordlists.add(wordlist);
        }
        return wordlists;
    }

    public Wordlist getWordlistById(String wordListId) throws ExecutionException, InterruptedException {
        firestore = FirestoreClient.getFirestore();
        DocumentSnapshot documentSnapshot = firestore.collection("word_lists").document(wordListId).get().get();
        return documentSnapshot.toObject(Wordlist.class);
    }

    public Wordlist createWordlist(String wordlistName, String userId) throws ExecutionException, InterruptedException {
        firestore = FirestoreClient.getFirestore();
        DocumentSnapshot documentSnapshot = firestore.collection("users").document(userId).get().get();

        Wordlist wordlist = new Wordlist();
        wordlist.setName(wordlistName);
        wordlist.setUserId(userId);
        wordlist.setWords(new ArrayList<>());

        Map<String, Object> docData = new HashMap<>();
        docData.put("name", wordlist.getName());
        docData.put("user_id", wordlist.getUserId());
        docData.put("words", wordlist.getWords());

        firestore.collection("word_lists").document().set(docData);
        return wordlist;
    }

    public Word getWordlistWord(String wordlistId, Integer wordId) throws ExecutionException, InterruptedException{
        firestore = FirestoreClient.getFirestore();
        DocumentSnapshot documentSnapshot = firestore.collection("word_lists").document(wordlistId).get().get();

        Wordlist wordlist = documentSnapshot.toObject(Wordlist.class);
        if (wordlist == null)
            return null;
        return wordlist.getWords().stream().filter(w -> Objects.equals(w.getId(), wordId)).findFirst().orElse(null);
    }

    public List<Wordlist> searchForWordlist(String name, String word) throws ExecutionException, InterruptedException{
        firestore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> query = firestore.collection("word_lists").get();
        List<QueryDocumentSnapshot> documentSnapshots = query.get().getDocuments();
        List<Wordlist> wordlists = new ArrayList<>();

        for (DocumentSnapshot document : documentSnapshots) {
            Wordlist wordlist = document.toObject(Wordlist.class);
            wordlist.setWordlistId(document.getId());
            // NOTE: somehow userId cannot be parsed
            //      manually set userId is required.
            wordlist.setUserId(document.get("user_id").toString());

            if (
                    wordlist.getName().toLowerCase().contains(name.toLowerCase()) &&
                    wordlist.getWords().stream().filter(w -> w.getWord().toLowerCase().contains(word.toLowerCase())).findFirst().orElse(null) != null
            ) {
                wordlists.add(wordlist);
            }
        }
        return wordlists;
    }

    public boolean deleteWordlist(String id) {
        firestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = firestore.collection("word_lists").document(id).delete();
        return true;
    }

    public boolean removeWordlistWord(String wordlistId, Integer wordId) throws ExecutionException, InterruptedException {
        firestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = firestore.collection("word_lists").document(wordlistId);
        DocumentSnapshot document = documentReference.get().get();

        Wordlist wordlist = document.toObject(Wordlist.class);
        wordlist.setWordlistId(document.getId());
        // NOTE: somehow userId cannot be parsed
        //      manually set userId is required.
        wordlist.setUserId(document.get("user_id").toString());

        wordlist.getWords().removeIf(word -> word.getId().equals(wordId));

        Map<String, Object> map = wordlist.toHashMap();
        documentReference.update(map);

        return true;
    }

    public boolean renameWordList(String id, String name) throws ExecutionException, InterruptedException {
        firestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = firestore.collection("word_lists").document(id);
        documentReference.update("name", name);
        return true;
    }

    public boolean addWordToWordlist(String wordlistId, Word word) throws ExecutionException, InterruptedException{
        firestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = firestore.collection("word_lists").document(wordlistId);
        DocumentSnapshot document = documentReference.get().get();

        Wordlist wordlist = document.toObject(Wordlist.class);
        wordlist.setWordlistId(document.getId());
        // NOTE: somehow userId cannot be parsed
        //      manually set userId is required.
        wordlist.setUserId(document.get("user_id").toString());

        // prevent adding the same word multiple times
        if (wordlist.getWords()
                .stream()
                .anyMatch(w ->
                        (w.getWord().equals(word.getWord()) && w.getDefinition().equals(word.getDefinition()))
                )
        )
            return false;

        if (wordlist.getWords().size() == 0)
            word.setId(1);
        else
            word.setId(wordlist.getWords().get(wordlist.getWords().size() - 1).getId()+1);
        wordlist.getWords().add(word);

        documentReference.update(wordlist.toHashMap());

        return true;
    }

}
