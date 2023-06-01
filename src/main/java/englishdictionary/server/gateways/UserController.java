package englishdictionary.server.gateways;

import java.util.concurrent.ExecutionException;

import englishdictionary.server.errors.AuthorizationException;
import englishdictionary.server.errors.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.google.firebase.auth.FirebaseAuthException;

import englishdictionary.server.models.User;
import englishdictionary.server.models.UserAuth;
import englishdictionary.server.services.UserService;

@RestController
@RequestMapping("api/user/")
public class UserController {

    private final UserService userServices;

    public UserController(UserService userServices) {
        this.userServices = userServices;
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<User> getUser(@PathVariable("id") String id) {
        try {
            User user = userServices.getUser(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            // TODO: LOGGER CALLED
            throw userNotFoundException;
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/test")
    public String test() {
        return ("test user endpoint");
    }

    @GetMapping("/{id}/profile/email")
    public ResponseEntity<String> getUserEmail(@PathVariable("id") String id) {
        try {
            String email = userServices.getUserEmail(id);
            return new ResponseEntity<>(email, HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            throw userNotFoundException;
        } catch (ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/profile/fullname")
    public ResponseEntity<String> getUserFullname(@PathVariable("id") String id) {
        try {
            String fullName = userServices.getUserFullname(id);
            return new ResponseEntity<>(fullName, HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            throw userNotFoundException;
        } catch (ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/profile/gender")
    public ResponseEntity<Integer> getUserGender(@PathVariable("id") String id) {
        try {
            Integer gender = userServices.getUserGender(id);
            return new ResponseEntity<>(gender, HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            throw userNotFoundException;
        } catch (ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/profile/level")
    public ResponseEntity<Integer> getUserLevel(@PathVariable("id") String id) {
        try {
            Integer level = userServices.getUserLevel(id);
            return new ResponseEntity<>(level, HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            throw userNotFoundException;
        } catch (ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/profile/occupation")
    public ResponseEntity<Integer> getUserOccupation(@PathVariable("id") String id) {
        try {
            Integer occupation = userServices.getUserOccupation(id);
            return new ResponseEntity<>(occupation, HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            throw userNotFoundException;
        } catch (ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/")
    public ResponseEntity<String> login(@RequestBody UserAuth userAuth) {
        try {
            String uid = userServices.getUserId(userAuth);
            return ResponseEntity.status(HttpStatus.OK).body(uid);
        } catch (FirebaseAuthException | AuthorizationException authorizationException) {
            throw new AuthorizationException();
        } catch (ExecutionException | InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/new")
    public ResponseEntity<String> createUser(@RequestBody User user) {
        try {
            String uid = userServices.createUser(user);
            return ResponseEntity.status(HttpStatus.OK).body(uid);
        } catch (FirebaseAuthException | AuthorizationException authorizationException) {
            throw new AuthorizationException();
        } catch (ExecutionException | InterruptedException | RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public HttpStatus updateUserInfo(@RequestBody UserAuth userAuth, @PathVariable("id") String id) throws FirebaseAuthException, ExecutionException, InterruptedException {
        if (userServices.updateUserInfo(userAuth, id) != null)
            return HttpStatus.OK;
        return HttpStatus.UNAUTHORIZED;
    }

    @PutMapping("/profile/{id}")
    public HttpStatus updateUserProfile(@RequestBody User user, @PathVariable("id") String id) throws ExecutionException, InterruptedException {
        if (userServices.updateUserProfile(user, id))
            return HttpStatus.OK;
        throw new AuthorizationException();
    }

    @PostMapping(value = "/profile/files/{id}", consumes = {"*/*"})
    public ResponseEntity<String> uploadFile(@RequestBody MultipartFile file, @PathVariable("id") String id) {
        if (userServices.uploadFile(file, id))
            return ResponseEntity.status(HttpStatus.OK).build();
        throw new AuthorizationException();
    }

    @GetMapping("/profile/avatar/{id}")
    public ResponseEntity<String> showFile(@PathVariable("id") String id) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(userServices.getFileAccessToken(id));
        } catch (AuthorizationException authorizationException) {
            throw authorizationException;
        } catch (FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
