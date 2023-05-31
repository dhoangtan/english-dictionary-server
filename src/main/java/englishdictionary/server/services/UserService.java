package englishdictionary.server.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import englishdictionary.server.errors.UserNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;

import englishdictionary.server.EnglishDictionaryServerApplication;
import englishdictionary.server.models.User;
import englishdictionary.server.models.UserAuth;

@Service
public class UserService {

    public Boolean uploadFile(MultipartFile file, String id) {
        try {
            String bucketName = "englishdictionary-8237a.appspot.com";
            InputStream serviceAccount = EnglishDictionaryServerApplication.class.getResourceAsStream("/service_account_key.json");
//            String filename = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();
            String filename = id;

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
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User getUser(String userId) throws ExecutionException, InterruptedException {
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

    public String getUserId(UserAuth userAuth) throws FirebaseAuthException, ExecutionException, InterruptedException {
        UserRecord userRecord = FirebaseAuth.getInstance().getUserByEmail(userAuth.getEmail());
        if (userRecord != null) {
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
        }
        return null;
    }

    public String getUserEmail(String userId) throws ExecutionException, InterruptedException {
        User user = getUser(userId);

        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user.getEmail();
    }

    public String getUserFullname(String userId) throws ExecutionException, InterruptedException {
        User user = getUser(userId);

        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user.getFullName();
    }

    public Integer getUserGender(String userId) throws ExecutionException, InterruptedException {
        User user = getUser(userId);

        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user.getGender();
    }

    public Integer getUserLevel(String userId) throws ExecutionException, InterruptedException {
        User user = getUser(userId);

        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user.getLevel();
    }

    public Integer getUserOccupation(String userId) throws ExecutionException, InterruptedException {
        User user = getUser(userId);

        if (user == null) {
            throw new UserNotFoundException(userId);
        }
        return user.getOccupation();
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

        if (!writeResult.isDone())
            throw new RuntimeException("An unexpected error occurred");

        return uid;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("An unexpected error occurred");
        }
    }

    public String updateUserInfo(UserAuth userAuth, String id) throws FirebaseAuthException, ExecutionException, InterruptedException {
        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(id)
                .setEmail(userAuth.getEmail())
                .setPassword(userAuth.getPassword());
        UserRecord userRecord = FirebaseAuth.getInstance().updateUser(request);
        return userRecord.getUid();
    }

    public Boolean updateUserProfile(User user, String id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collection = dbFirestore.collection("users").document(id).set(user);
        collection.get();
        return true;
    }
    public String getFileAccessToken(String id) throws FirebaseAuthException {
        String bucketName = "englishdictionary-8237a.appspot.com";
        String fullFilePath = "gs://" + bucketName + "/" + id;
        String token = FirebaseAuth.getInstance().createCustomToken(fullFilePath);
        String url = "https://firebasestorage.googleapis.com/v0/b/englishdictionary-8237a.appspot.com/o/"+id+".jpg"+"?alt=media&token="+token;
        return url;
    }
}
