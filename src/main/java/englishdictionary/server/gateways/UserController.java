package englishdictionary.server.gateways;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import com.google.cloud.firestore.QuerySnapshot;
import englishdictionary.server.errors.AuthorizationException;
import englishdictionary.server.errors.UserNotFoundException;
import englishdictionary.server.utils.ControllerUtilities;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.stereotype.Component;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;

@Component
@RestController
@RequestMapping("api/user/")
public class UserController {

    private final UserService userServices;
    private final ControllerUtilities utilFuncs;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    private String getFunctionCall(String functionName, String resource) {
        return "[UserController] call [" + functionName + "] to resource " + resource;
    }

    public UserController(UserService userServices, ControllerUtilities controllerUtilities) {
        utilFuncs = controllerUtilities;
        this.userServices = userServices;
    }

    @GetMapping("/{id}/profile")
    public ResponseEntity<User> getUser(@PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUser", resource);
        try {
            logger.info(prompt);
            User user = userServices.getUser(id);
            logger.info(prompt + " - Completed");
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            logger.error("An error occurred when getting resource " + resource + " - Not Found - \n Error message:\n" + userNotFoundException.getMessage());
            throw userNotFoundException;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/{id}/profile/email")
    public ResponseEntity<String> getUserEmail(@PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            String email = userServices.getUserEmail(id);
            logger.info(prompt + " - Completed");
            return new ResponseEntity<>(email, HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            logger.error("An error occurred when getting resource " + resource + " - Not Found - \n Error message:\n" + userNotFoundException.getMessage());
            throw userNotFoundException;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/profile/fullname")
    public ResponseEntity<String> getUserFullname(@PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            String fullName = userServices.getUserFullname(id);
            logger.info(prompt + " - Completed");
            return new ResponseEntity<>(fullName, HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            logger.error("An error occurred when getting resource " + resource + " - Not Found - \n Error message:\n" + userNotFoundException.getMessage());
            throw userNotFoundException;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/profile/gender")
    public ResponseEntity<Integer> getUserGender(@PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            Integer gender = userServices.getUserGender(id);
            logger.info(prompt + " - Completed");
            return new ResponseEntity<>(gender, HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            logger.error("An error occurred when getting resource " + resource + " - Not Found - \n Error message:\n" + userNotFoundException.getMessage());
            throw userNotFoundException;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/profile/level")
    public ResponseEntity<Integer> getUserLevel(@PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            Integer level = userServices.getUserLevel(id);
            logger.info(prompt + " - Completed");
            return new ResponseEntity<>(level, HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            logger.error("An error occurred when getting resource " + resource + " - Not Found - \n Error message:\n" + userNotFoundException.getMessage());
            throw userNotFoundException;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}/profile/occupation")
    public ResponseEntity<Integer> getUserOccupation(@PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            Integer occupation = userServices.getUserOccupation(id);
            logger.info(prompt + " - Completed");
            return new ResponseEntity<>(occupation, HttpStatus.OK);
        } catch (UserNotFoundException userNotFoundException) {
            logger.error("An error occurred when getting resource " + resource + " - Not Found - \n Error message:\n" + userNotFoundException.getMessage());
            throw userNotFoundException;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/")
    public ResponseEntity<String> login(@RequestBody UserAuth userAuth, HttpServletRequest request) throws FirebaseAuthException {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            String uid = userServices.getUserId(userAuth);
            logger.info(prompt + " - Completed");
            return ResponseEntity.status(HttpStatus.OK).body(uid);
        } catch (FirebaseAuthException | AuthorizationException authorizationException) {
            logger.error("An error occurred when getting resource " + resource + " - Unauthorized - \n Error message: \n" + authorizationException.getMessage());
            throw authorizationException;
        } catch (ExecutionException | InterruptedException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/new")
    public ResponseEntity<String> createUser(@RequestBody User user, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            String uid = userServices.createUser(user);
            logger.info(prompt + " - Completed");
            return ResponseEntity.status(HttpStatus.OK).body(uid);
        } catch (FirebaseAuthException | AuthorizationException authorizationException) {
            logger.error("An error occurred when getting resource " + resource + " - Unauthorized - \n Error message: \n" + authorizationException.getMessage());
            throw new AuthorizationException();
        } catch (ExecutionException | InterruptedException | RuntimeException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateUserInfo(@RequestBody UserAuth userAuth, @PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            if (userServices.updateUserInfo(userAuth, id) != null) {
                logger.info(prompt + " - Completed");
                return ResponseEntity.status(HttpStatus.OK).build();
            }
            logger.error("An error occurred when getting resource " + resource + " - Unauthorized" );
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        catch (FirebaseAuthException e) {
            logger.error("An error occurred when getting resource " + resource + " - Unauthorized - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/profile/{id}")
    public ResponseEntity<String> updateUserProfile(@RequestBody User user, @PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            userServices.updateUserProfile(user, id);
            logger.info(prompt + " - Completed");
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        catch (InterruptedException | ExecutionException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/profile/files/{id}", consumes = {"*/*"})
    public ResponseEntity<String> uploadFile(@RequestBody MultipartFile file, @PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            userServices.uploadFile(file, id);
            logger.info(prompt + " - Completed");
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (IOException e) {
            logger.error("An error occurred when getting resource " + resource + " - Execution interrupted - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/profile/avatar/{id}")
    public ResponseEntity<String> showFile(@PathVariable("id") String id, HttpServletRequest request) {
        String resource = utilFuncs.getCurrentResourcePath(request);
        String prompt = getFunctionCall("getUserEmail", resource);
        try {
            logger.info(prompt);
            String response = userServices.getFileAccessToken(id);
            logger.info(prompt + " - Completed");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (AuthorizationException authorizationException) {
            logger.error("An error occurred when getting resource " + resource + " - Unauthorized - \n Error message: \n" + authorizationException.getMessage());
            throw authorizationException;
        } catch (FirebaseAuthException e) {
            logger.error("An error occurred when getting resource " + resource + " - Unauthorized - \n Error message: \n" + e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/gender")
    public QuerySnapshot getGender(){
        try{
            return userServices.getAllGender();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
