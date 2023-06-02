package englishdictionary.server.gateways;

import englishdictionary.server.models.User;
import englishdictionary.server.models.UserAuth;
import englishdictionary.server.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ExecutionException;
import com.google.firebase.auth.FirebaseAuthException;
@Component
@RestController
@RequestMapping("api/user/")
public class UserController {

    private final UserService userServices;
    private UserAuth userAuth;
    public UserController(UserService userServices) {
        this.userServices = userServices;
    }

    @GetMapping("/{id}/profile")
    public User getUser(@PathVariable("id") String id) throws InterruptedException{
        try{
            return userServices.getUser(id);
        } catch (ExecutionException e){
            throw new ResponseStatusException( HttpStatusCode.valueOf(404));
        }
    }
    @GetMapping("/test")
    public String test(){
        return ("test user endpoint");
    }

    @GetMapping("/{id}/profile/email")
    public String getUserEmail(@PathVariable("id") String id) throws ExecutionException, InterruptedException {
        return userServices.getUserEmail(id);
    }

    @GetMapping("/{id}/profile/fullname")
    public String getUserFullname(@PathVariable("id") String id) throws ExecutionException, InterruptedException {
        return userServices.getUserFullname(id);
    }

    @GetMapping("/{id}/profile/gender")
    public Long getUserGender(@PathVariable("id") String id) throws ExecutionException, InterruptedException {
        return userServices.getUserGender(id);
    }

    @GetMapping("/{id}/profile/level")
    public Long getUserLevel(@PathVariable("id") String id) throws ExecutionException, InterruptedException {
        return userServices.getUserLevel(id);
    }

    @GetMapping("/{id}/profile/occupation")
    public Long getUserOccupation(@PathVariable("id") String id) throws ExecutionException, InterruptedException {
        return userServices.getUserOccupation(id);
    }
    @PostMapping("/")
    public ResponseEntity<String> login(@RequestBody UserAuth userAuth) {
        try {
            String uid = userServices.getUserId(userAuth);
            if (uid != null) {
                // User authenticated successfully
                return ResponseEntity.ok("{\"uid\": \"" + uid + "\"}");
            } else {
                // Authentication failed
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed");
            }
        } catch (Exception e) {
            // Handle any exceptions
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred");
        }
    }

    @PostMapping("/new")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        try {
            String uid = userServices.createUser(user);
            return ResponseEntity.ok().body("User created successfully: " + uid);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating user: " + e.getMessage());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateUserInfo(@RequestBody UserAuth userAuth, @PathVariable("id") String id) throws FirebaseAuthException, ExecutionException, InterruptedException {
        if(userServices.updateUserInfo(userAuth, id) != null){
            return ResponseEntity.ok().build();
        }
        else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping ("/profile/{id}")
    public ResponseEntity<Integer> updateUserProfile(@RequestBody User user, @PathVariable("id") String id) throws ExecutionException, InterruptedException {
        if(userServices.updateUserProfile(user, id)){
            return ResponseEntity.ok().build();
        }
        else{
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


}
