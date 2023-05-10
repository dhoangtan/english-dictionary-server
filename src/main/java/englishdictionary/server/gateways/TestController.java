package englishdictionary.server.gateways;

import englishdictionary.server.models.User;
import englishdictionary.server.services.UserServices;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ExecutionException;

@RestController
public class TestController {
    @GetMapping("/test")
    public String index() {
        return "Xin chao con me may";
    }
}
