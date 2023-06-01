package englishdictionary.server.services;

import com.google.api.client.util.DateTime;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import englishdictionary.server.models.User;
import englishdictionary.server.models.UserAuth;
import org.springframework.stereotype.Service;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
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
    public String getUserId(UserAuth userAuth) throws FirebaseAuthException, ExecutionException, InterruptedException {
        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(userAuth.getEmail());
        String uid = userRecord.getUid();
        Firestore dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("users").document(uid);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        String storedPass = document.getString("password");
        String hashedPass = hashPassword(userAuth.getPassword());
        if (MessageDigest.isEqual(storedPass.getBytes(StandardCharsets.UTF_8), hashedPass.getBytes(StandardCharsets.UTF_8))) {
            return uid;
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
    public String createUser(User user) throws FirebaseAuthException, ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        String hashedPassword = hashPassword(user.getPassword());
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(user.getEmail())
                .setPassword(user.getPassword())
                .setDisabled(false);
        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
        String uid = userRecord.getUid();
        user.setPassword(hashedPassword);
        DocumentReference userDocRef = dbFirestore.collection("users").document(uid);
        ApiFuture<WriteResult> writeResult = userDocRef.set(user);
        writeResult.get();

        return uid;
    }
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
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
    public Timestamp getDate(String userId, String wordListId) throws ExecutionException, InterruptedException {
        Firestore dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("word_lists").document(userId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        if(document.exists()){
           return document.getDate("date");
        }
        return null;
    }
    public Boolean dateThreshold (User user){
        LocalDate currentDate = LocalDate.now();
        return true;
    }
}
