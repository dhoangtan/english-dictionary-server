package englishdictionary.server.component;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.cloud.FirestoreClient;
import englishdictionary.server.services.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
    final int oneDay = 24 * 60 * 60 * 1000;

    @Scheduled(fixedRate = oneDay) // Runs every minute
    public void runTask() throws ExecutionException, InterruptedException, FirebaseAuthException, MessagingException {
        List<String> userIds = userService.getAllUserId();
        for (String userId : userIds) {
            notifier(userId);
        }
        System.out.println("Task executed");
    }
    public void notifier(String id) throws ExecutionException, InterruptedException, FirebaseAuthException, MessagingException {
        try{
            Timestamp timestamp = userService.getDate(id);
            long thresholdSeconds = 60;
            long currentTimeSeconds = Instant.now().getEpochSecond();
            long documentTimeSeconds = timestamp.toDate().toInstant().getEpochSecond();
            long timeDifference = currentTimeSeconds - documentTimeSeconds;
            System.out.println(currentTimeSeconds);
            System.out.println(documentTimeSeconds);
            System.out.println(timeDifference);
            if (true){
                sendNotification(id);
            }
        }catch (NullPointerException ignored){

        }

    }

    public void sendNotification(String id) throws ExecutionException, InterruptedException, MessagingException {
        String userEmail = userService.getUserEmail(id);
        String subject = "Notification Subject";
        String message = "<html><body><h1>It is time for us to get back on track!!!!!</h1></body></html>";

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        messageHelper.setTo(userEmail);
        messageHelper.setSubject(subject);
        messageHelper.setText(message, true );

        emailSender.send(mimeMessage);
    }
}
