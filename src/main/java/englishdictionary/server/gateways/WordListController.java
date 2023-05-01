package englishdictionary.server.gateways;

import englishdictionary.server.models.WordList;
import englishdictionary.server.services.WordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/word_list/")
public class WordListController {
    @Autowired
    private WordListService wordListService;

    @GetMapping("/{user_id}")
    public List<WordList> getAllUserWordLists(@PathVariable("user_id") String userId) {
        try {
            return wordListService.getAllUserWordLists(userId);
        } catch (ExecutionException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND
            );
        } catch (InterruptedException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @GetMapping("/default")
    public List<WordList> getAllSystemWordLists() {
        try {
            return wordListService.getSystemWordLists();
        } catch (ExecutionException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND
            );
        } catch (InterruptedException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND
            );
        }
    }



}
