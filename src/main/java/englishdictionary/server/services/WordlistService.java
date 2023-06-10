package englishdictionary.server.services;

import java.security.InvalidParameterException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import com.google.firebase.auth.FirebaseAuthException;
import englishdictionary.server.errors.DuplicateWordlistException;
import englishdictionary.server.errors.WordNotFoundException;
import englishdictionary.server.errors.WordlistNotFoundException;
import englishdictionary.server.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

import englishdictionary.server.models.Word;
import englishdictionary.server.models.Wordlist;

@Service
public class WordlistService {
    private Firestore firestore;

    @Autowired
    private UserService userService;

    // Wordlist query migration

    private User getUserByDocumentReference(DocumentReference documentReference) throws ExecutionException, InterruptedException {
        return documentReference.get().get().toObject(User.class);
    }

    private List<Word> getWords(List<HashMap<String, Object>> listObjectAsHashMap) {
        List<Word> words = new ArrayList<>();

        for (HashMap<String, Object> h : listObjectAsHashMap) {
            Word w = new Word();
            w.setId(Integer.parseInt(h.get("id").toString()));
            w.setWord(h.get("word").toString());
            w.setDefinition(h.get("definition").toString());
            words.add(w);
        }

        return words;
    }

    public Wordlist getWordlistByIdRef(String wordListId) throws ExecutionException, InterruptedException {
        firestore = FirestoreClient.getFirestore();
        DocumentSnapshot documentSnapshot = firestore.collection("word_lists").document(wordListId).get().get();
        Wordlist wordlist = new Wordlist();

        wordlist.setName(documentSnapshot.getString("name"));
        wordlist.setWordlistId(documentSnapshot.getId());
        wordlist.setWords((List<Word>) documentSnapshot.get("words"));
        DocumentReference userRef = (DocumentReference) documentSnapshot.get("user");
        System.out.println(userRef.getPath());
        User user = userRef.get().get().toObject(User.class);

        wordlist.setUser(user);

        if (wordlist == null)
            throw new WordlistNotFoundException(wordListId);

        wordlist.setWordlistId(wordListId);
        return wordlist;
    }

    public List<Wordlist> getAllUserWordListsRef(String userId) throws ExecutionException, InterruptedException, FirebaseAuthException {
        firestore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> future = firestore.collection("word_lists_ref").get(); // TODO: Remove ref after complete feature
        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
        List<Wordlist> wordlists = new ArrayList<>();
        for (DocumentSnapshot document : documents) {

            DocumentReference userRef = (DocumentReference) document.get("user");
            System.out.println(userRef.getPath());
            User user = userRef.get().get().toObject(User.class);

            if (!userService.getUserId(user.getEmail()).equals(userId))
                continue;

            Wordlist wordlist = new Wordlist();
            wordlist.setName(document.getString("name"));
            wordlist.setWordlistId(document.getId());
            wordlist.setWords((List<Word>) document.get("words"));
            wordlists.add(wordlist);

            wordlist.setUser(user);
        }

        return wordlists;
    }

    public Wordlist createWordlistRef(String wordlistName, String userId) throws ExecutionException, InterruptedException, FirebaseAuthException {
        if (wordlistName == null)
            throw new InvalidParameterException("Wordlist name cannot be null");

        firestore = FirestoreClient.getFirestore();

        List<Wordlist> wordlists = getAllUserWordListsRef(userId);
        for(Wordlist wordlist : wordlists)
            if (Objects.equals(wordlist.getName(), wordlistName))
                throw new DuplicateWordlistException(wordlistName);


        DocumentReference userRef = userService.getUserDocumentReferenceById(userId);
        User user = userRef.get().get().toObject(User.class);

        Wordlist wordlist = new Wordlist();

        wordlist.setName(wordlistName);
        wordlist.setUser(user);
        wordlist.setWords(new ArrayList<>());

        Map<String, Object> docData = new HashMap<>();
        docData.put("name", wordlist.getName());
        docData.put("user", userRef);
        docData.put("words", wordlist.getWords());

        firestore.collection("word_lists").document().set(docData).get();
        return wordlist;
    }

    public Word getWordlistWord(String wordlistId, Integer wordId) throws ExecutionException, InterruptedException{
        firestore = FirestoreClient.getFirestore();
        DocumentSnapshot documentSnapshot = firestore.collection("word_lists").document(wordlistId).get().get();

        Wordlist wordlist = documentSnapshot.toObject(Wordlist.class);

        if (wordlist == null)
            throw new WordlistNotFoundException(wordlistId);

        Word word = wordlist.getWords().stream().filter(w -> Objects.equals(w.getId(), wordId)).findFirst().orElse(null);
        if (word == null)
            throw new WordNotFoundException(wordId);

        return word;
    }

    public List<Wordlist> searchForWordlist(String name, String word) throws ExecutionException, InterruptedException{
        firestore = FirestoreClient.getFirestore();
        ApiFuture<QuerySnapshot> query = firestore.collection("word_lists").get();
        List<QueryDocumentSnapshot> documentSnapshots = query.get().getDocuments();
        List<Wordlist> wordlists = new ArrayList<>();

        for (DocumentSnapshot document : documentSnapshots) {
            Wordlist wordlist = document.toObject(Wordlist.class);
            wordlist.setWordlistId(document.getId());
            wordlist.setUser(getUserByDocumentReference((DocumentReference) document.get("user")));

            if (
                    wordlist.getName().toLowerCase().contains(name.toLowerCase()) &&
                    wordlist.getWords().stream().filter(w -> w.getWord().toLowerCase().contains(word.toLowerCase())).findFirst().orElse(null) != null
            ) {
                wordlists.add(wordlist);
            }
        }
        return wordlists;
    }

    public boolean deleteWordlist(String wordlistId) throws ExecutionException, InterruptedException{
        firestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> writeResult = firestore.collection("word_lists").document(wordlistId).delete();
        writeResult.get();

        return writeResult.isDone();
    }

    // TODO: need to optimize
    public boolean removeWordlistWord(String wordlistId, Integer wordId) throws ExecutionException, InterruptedException {
        firestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = firestore.collection("word_lists_ref").document(wordlistId);
        DocumentSnapshot document = documentReference.get().get();

        Wordlist wordlist = new Wordlist();

        wordlist.setWordlistId(document.getId());
        wordlist.setName(document.getString("name"));
        wordlist.setUser(getUserByDocumentReference((DocumentReference) document.get("user")));

        List<HashMap<String, Object>> listHashMappu = (List<HashMap<String, Object>>) document.get("words");
        List<Word> words = new ArrayList<>();

        for (HashMap<String, Object> h : listHashMappu) {
            Word word = new Word();
            word.setId(Integer.parseInt(h.get("id").toString()));
            word.setWord(h.get("word").toString());
            word.setDefinition(h.get("definition").toString());
            words.add(word);
        }

        wordlist.setWords(words);

        if (wordlist == null)
            throw new WordlistNotFoundException(wordlistId);

        wordlist.getWords().removeIf(word -> word.getId() == wordId);

        Map<String, Object> map = wordlist.toHashMap();
        ApiFuture<WriteResult> updateResult = documentReference.update(map);
        updateResult.get();

        return updateResult.isDone();
    }

    public boolean renameWordList(String wordlistId, String name) throws ExecutionException, InterruptedException {
        firestore = FirestoreClient.getFirestore();
        DocumentSnapshot documentSnapshot = firestore.collection("word_lists").document(wordlistId).get().get();
        if (!documentSnapshot.exists())
            throw new WordlistNotFoundException(wordlistId);
        DocumentReference documentReference = documentSnapshot.getReference();
        ApiFuture<WriteResult> updateResult = documentReference.update("name", name);
        updateResult.get();

        return updateResult.isDone();
    }

    public boolean addWordToWordlist(String wordlistId, Word word) throws ExecutionException, InterruptedException{
        firestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = firestore.collection("word_lists").document(wordlistId);
        DocumentSnapshot document = documentReference.get().get();

        Wordlist wordlist = new Wordlist();

        wordlist.setWordlistId(document.getId());
        wordlist.setName(document.getString("name"));
        wordlist.setUser(getUserByDocumentReference((DocumentReference) document.get("user")));

        List<HashMap<String, Object>> listHashMappu = (List<HashMap<String, Object>>) document.get("words");
        List<Word> words = new ArrayList<>();

        for (HashMap<String, Object> h : listHashMappu) {
            Word w = new Word();
            w.setId(Integer.parseInt(h.get("id").toString()));
            w.setWord(h.get("word").toString());
            w.setDefinition(h.get("definition").toString());
            words.add(w);
        }

        wordlist.setWords(words);

        if (wordlist == null)
            throw new WordlistNotFoundException(wordlistId);

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
