package englishdictionary.server.services;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.ListUsersPage;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class AdminService {
    @Autowired
    private UserService userService;
    //=======================================Admin Action==========================================================
    public void editGender(String content, String docId){
        try {
            Firestore dbFirestore = FirestoreClient.getFirestore();
            DocumentReference documentRef = dbFirestore.collection("genders").document(docId);
            DocumentSnapshot documentSnapshot = documentRef.get().get();

            if (!documentSnapshot.exists()) {
                documentRef.set(Collections.singletonMap("name", content));
            } else {
                documentRef.update("name", content);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public List<String> getAllUserId() throws FirebaseAuthException {
        List<String> userIds = new ArrayList<>();
        ListUsersPage page = FirebaseAuth.getInstance().listUsers(null);
        for (UserRecord userRecord : page.iterateAll()) {
            userIds.add(userRecord.getUid());
        }

        return userIds;
    }
    public void deleteGender(String docId){
        try {
            Firestore dbFirestore = FirestoreClient.getFirestore();
            DocumentReference documentRef = dbFirestore.collection("genders").document(docId);
            documentRef.delete();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void deleteUser(String id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("users").document(id);
        Boolean active = userService.isUserActive(id);
        WriteResult write = documentReference.update("active", !active).get();
        documentReference.update("notify", false);
    }
}
