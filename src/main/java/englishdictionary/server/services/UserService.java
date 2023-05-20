package englishdictionary.server.services;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.remoteconfig.internal.TemplateResponse;
import englishdictionary.server.models.User;
import englishdictionary.server.models.UserAuth;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class UserService {

    public User getUser(String id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        User user;
        if(document.exists()){
            user = document.toObject(User.class);
            return user;
        }
        return null;
    }

    public String getUserEmail(String id) throws ExecutionException, InterruptedException {
        Firestore dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        if(document.exists()){
            return document.getString("email");
        }
        return null;
    }
    public String getUserFullname(String id) throws ExecutionException, InterruptedException {
        Firestore dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        if(document.exists()){
            return document.getString("full_name");
        }
        return null;
    }
    public Long getUserGender(String id) throws ExecutionException, InterruptedException {
        Firestore dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        if(document.exists()){
            return document.getLong("gender");
        }
        return null;
    }
    public Long getUserLevel(String id) throws ExecutionException, InterruptedException {
        Firestore dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        if(document.exists()){
            return document.getLong("level");
        }
        return null;
    }
    public Long getUserOccupation(String id) throws ExecutionException, InterruptedException {
        Firestore dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        if(document.exists()){
            return document.getLong("occupation");
        }
        return null;
    }
    public String createUser(User user, String password) throws FirebaseAuthException, ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(user.getEmail())
                .setPassword(password)
                .setDisabled(false);
        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        ApiFuture<WriteResult> result = dbFirestore.collection("users").document(userRecord.getUid()).set(user);
        result.get();
        return userRecord.getUid();
    }
    public String updateUserInfo (UserAuth userAuth, String id) throws FirebaseAuthException, ExecutionException, InterruptedException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(id)
                .setEmail(userAuth.getEmail())
                .setPassword(userAuth.getPassword());
        UserRecord userRecord = FirebaseAuth.getInstance().updateUser(request);
        return userRecord.getUid();
    }

    public Boolean updateUserProfile (User user, String id ) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collection = dbFirestore.collection("users").document(id).set(user);
        collection.get();
        return true;
    }
}
