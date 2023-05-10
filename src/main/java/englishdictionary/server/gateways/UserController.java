package englishdictionary.server.gateways;

import englishdictionary.server.models.User;
import englishdictionary.server.services.UserServices;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/user/")
public class UserController {
    public UserServices userServices;
    public UserController(UserServices userServices) {this.userServices = userServices;}
    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") String id) throws InterruptedException{
        try{
            return userServices.getUser(id);

        } catch (ExecutionException e){
            throw new ResponseStatusException( HttpStatusCode.valueOf(404));
        }
    }
    @GetMapping("/hello")
    public String test(){
        return ("you are in");
    }
}
