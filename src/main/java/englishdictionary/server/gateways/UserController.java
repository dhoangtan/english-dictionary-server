package englishdictionary.server.gateways;

import englishdictionary.server.models.User;
import englishdictionary.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ExecutionException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
@RestController
@RequestMapping("api/user/")
public class UserController {

    private final UserService userServices;

    public UserController(UserService userServices) {
        this.userServices = userServices;
    }

    @GetMapping("/{id}")
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
    public ResponseEntity<String> createUser(@RequestBody User user) {
        try {
            String uid = userServices.createUser(user);
            return ResponseEntity.ok().body("User created successfully: " + uid);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error creating user: " + e.getMessage());
        }
    }

}
