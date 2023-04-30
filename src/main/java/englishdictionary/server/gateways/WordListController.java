package englishdictionary.server.gateways;

import englishdictionary.server.models.WordList;
import englishdictionary.server.services.WordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/word_list")
public class WordListController {
    @Autowired
    private WordListService wordListService;

    @GetMapping("/{user_id}")
    public List<WordList> getAllUserWordList(@PathVariable("user_id") String userId) {
        try {
            return wordListService.getUserWordLists(userId);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
