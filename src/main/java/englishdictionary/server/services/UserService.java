package englishdictionary.server.services;

import com.google.api.client.util.Value;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import englishdictionary.server.EnglishDictionaryServerApplication;
import englishdictionary.server.dtos.UserUploadAvatarDto;
import englishdictionary.server.models.User;
import englishdictionary.server.models.UserAuth;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private Optional<String> getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

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

    public User getUser(String id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        User user;
        if (document.exists()) {
            user = document.toObject(User.class);
            return user;
        }
        return null;
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

    public String getUserEmail(String id) throws ExecutionException, InterruptedException {
        Firestore dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.getString("email");
        }
        return null;
    }

    public String getUserFullname(String id) throws ExecutionException, InterruptedException {
        Firestore dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.getString("full_name");
        }
        return null;
    }

    public Long getUserGender(String id) throws ExecutionException, InterruptedException {
        Firestore dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.getLong("gender");
        }
        return null;
    }

    public Long getUserLevel(String id) throws ExecutionException, InterruptedException {
        Firestore dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
            return document.getLong("level");
        }
        return null;
    }

    public Long getUserOccupation(String id) throws ExecutionException, InterruptedException {
        Firestore dbfirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbfirestore.collection("users").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();
        if (document.exists()) {
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
        String url = "https://firebasestorage.googleapis.com/v0/b/englishdictionary-8237a.appspot.com/o/"+id+"?alt=media&token="+token;
        return url;
    }
}
