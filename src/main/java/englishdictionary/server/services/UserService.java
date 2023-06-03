package englishdictionary.server.services;

import java.io.IOException;
import java.io.InputStream;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.ListUsersPage;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import englishdictionary.server.errors.WordlistNotFoundException;
import englishdictionary.server.models.User;
import englishdictionary.server.models.UserAuth;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import englishdictionary.server.errors.AuthorizationException;
import englishdictionary.server.errors.UserNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import englishdictionary.server.EnglishDictionaryServerApplication;

@Service
public class UserService {
    // ==================UpLoadFile=====================
    public Boolean uploadFile(MultipartFile file, String id) throws IOException {
        String bucketName = "englishdictionary-8237a.appspot.com";
        InputStream serviceAccount = EnglishDictionaryServerApplication.class.getResourceAsStream("/service_account_key.json");
        String filename = id + ".jpg";

        Path tempFile = Files.createTempFile(id, filename);
        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

        StorageOptions options = StorageOptions.newBuilder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
        Storage storage = options.getService();
        BlobId blobId = BlobId.of(bucketName, filename);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
        storage.create(blobInfo, Files.readAllBytes(tempFile));
        Files.delete(tempFile);

        return true;
    }

    public String getFileAccessToken(String id) throws FirebaseAuthException {
        String bucketName = "englishdictionary-8237a.appspot.com";
        String fullFilePath = "gs://" + bucketName + "/" + id;
        String token = FirebaseAuth.getInstance().createCustomToken(fullFilePath);
        String url = "https://firebasestorage.googleapis.com/v0/b/englishdictionary-8237a.appspot.com/o/" + id + ".jpg" + "?alt=media&token=" + token;
        return url;
    }

    //====================================================
//======================UserProfileInformation==================
    public User getUser(String userId) throws ExecutionException, InterruptedException, UserNotFoundException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("users").document(userId);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        User user;
        if (!document.exists()) {
            throw new UserNotFoundException(userId);
        }
        user = document.toObject(User.class);
        return user;
    }

    public String getUserId(UserAuth userAuth) throws FirebaseAuthException, ExecutionException, InterruptedException, AuthorizationException {
        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(userAuth.getEmail());
        if (userRecord == null)
            throw new AuthorizationException();
        String uid = userRecord.getUid();
        Firestore dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("users").document(uid);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        String storedPass = document.getString("password");
        String hashedPass = hashPassword(userAuth.getPassword());
        if (MessageDigest.isEqual(storedPass.getBytes(StandardCharsets.UTF_8), hashedPass.getBytes(StandardCharsets.UTF_8))) {
            Map<String, Object> data = new HashMap<>();
            data.put("lastLog", com.google.cloud.Timestamp.now());
            WriteResult writeResult = documentReference.update(data).get();
            return uid;
        }
        throw new AuthorizationException();
    }

    public String getUserEmail(String userId) throws ExecutionException, InterruptedException, UserNotFoundException {
        User user = getUser(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user.getEmail();
    }

    public String getUserFullname(String userId) throws ExecutionException, InterruptedException, UserNotFoundException {
        User user = getUser(userId);
        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user.getFullName();
    }

    public Integer getUserGender(String userId) throws ExecutionException, InterruptedException, UserNotFoundException {
        User user = getUser(userId);

        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user.getGender();
    }

    public Integer getUserLevel(String userId) throws ExecutionException, InterruptedException, UserNotFoundException {
        User user = getUser(userId);

        if (user == null) {
            throw new UserNotFoundException(userId);
        }

        return user.getGender();
    }

    public Integer getUserOccupation(String userId) throws ExecutionException, InterruptedException, UserNotFoundException {
        User user = getUser(userId);

        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user.getOccupation();
    }

    private String hashPassword(String password) throws RuntimeException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("An unexpected error occurred");
        }
    }

    public Timestamp getDate(String id) throws ExecutionException, InterruptedException {
        Firestore dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            Timestamp timestamp = document.getTimestamp("lastLog");
            return timestamp;
        }
        return null;
    }

    public List<String> getAllUserId() throws FirebaseAuthException {
        List<String> userIds = new ArrayList<>();
        ListUsersPage page = FirebaseAuth.getInstance().listUsers(null);
        for (UserRecord userRecord : page.iterateAll()) {
            userIds.add(userRecord.getUid());
        }

        return userIds;
    }

    //===============================================================
//========================Data===================================
    public QuerySnapshot getAllOccupation() throws ExecutionException, InterruptedException {
        Firestore dbfirestore = FirestoreClient.getFirestore();
        CollectionReference documentReference = dbfirestore.collection("occupations");
        ApiFuture<QuerySnapshot> future = documentReference.get();
        QuerySnapshot document = future.get();
        return document;
    }

    public QuerySnapshot getAllLevel() throws ExecutionException, InterruptedException {
        Firestore dbfirestore = FirestoreClient.getFirestore();
        CollectionReference documentReference = dbfirestore.collection("levels");
        ApiFuture<QuerySnapshot> future = documentReference.get();
        QuerySnapshot document = future.get();
        return document;
    }

    public QuerySnapshot getAllGender() throws ExecutionException, InterruptedException {
        Firestore dbfirestore = FirestoreClient.getFirestore();
        CollectionReference documentReference = dbfirestore.collection("genders");
        ApiFuture<QuerySnapshot> future = documentReference.get();
        QuerySnapshot document = future.get();
        return document;
    }

    //=========================UserAction===============================
    public String createUser(User user) throws FirebaseAuthException, ExecutionException, InterruptedException, RuntimeException {
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

        if (!writeResult.isDone())
            throw new RuntimeException("An unexpected error occurred");

        Map<String, Object> data = new HashMap<>();
        data.put("lastLog", com.google.cloud.Timestamp.now());
        WriteResult write = userDocRef.update(data).get();
        return uid;
    }

    public String updateUserInfo(UserAuth userAuth, String id) throws FirebaseAuthException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(id)
                .setEmail(userAuth.getEmail())
                .setPassword(userAuth.getPassword());
        UserRecord userRecord = FirebaseAuth.getInstance().updateUser(request);
        return userRecord.getUid();
    }

    public Boolean updateUserProfile(User user, String id) throws ExecutionException, InterruptedException {

        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentSnapshot documentSnapshot = dbFirestore.collection("users").document("id").get().get();
        if (!documentSnapshot.exists())
            throw new UserNotFoundException(id);
        DocumentReference documentReference = documentSnapshot.getReference();
        ApiFuture<WriteResult> updateResult = documentReference.update(
                "fullName", user.getFullName(),
                "gender", user.getGender(),
                "level", user.getLevel(),
                "occupation", user.getOccupation()
                );
        updateResult.get();
        return true;
    }
//=================================================================================================


}
