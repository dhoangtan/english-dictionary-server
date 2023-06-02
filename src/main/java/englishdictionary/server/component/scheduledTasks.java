package englishdictionary.server.component;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;
import englishdictionary.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
public class scheduledTasks {
    @Autowired
    private UserService userService;

    @Autowired
    private JavaMailSender emailSender;


    @Scheduled(fixedRate = 60000) // Runs every minute
    public void runTask() throws ExecutionException, InterruptedException, FirebaseAuthException {
        List<String> userIds = userService.getAllUserId();
        for (String userId : userIds) {
            notifier(userId);
        }
        System.out.println("Task executed");
    }
    public void notifier(String id) throws ExecutionException, InterruptedException, FirebaseAuthException {
        try{
            Timestamp timestamp = userService.getDate(id);
            long thresholdSeconds = 60;
            long currentTimeSeconds = Instant.now().getEpochSecond();
            long documentTimeSeconds = timestamp.toDate().toInstant().getEpochSecond();
            long timeDifference = currentTimeSeconds - documentTimeSeconds;
            if (timeDifference > thresholdSeconds){
                sendNotification(id);
            }
        }catch (NullPointerException ignored){

        }

    }

    public void sendNotification(String id) throws ExecutionException, InterruptedException {
        String userEmail = userService.getUserEmail(id);
        String subject = "Notification Subject";
        String message = "It is time for us to get back on track!!!!!";
        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(userEmail);
        emailMessage.setSubject(subject);
        emailMessage.setText(message);

        emailSender.send(emailMessage);
    }
}
