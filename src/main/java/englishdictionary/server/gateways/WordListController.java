package englishdictionary.server.gateways;

import englishdictionary.server.models.Word;
import englishdictionary.server.models.WordList;
import englishdictionary.server.services.WordListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/wordlists")
public class WordListController {
    @Autowired
    private WordListService wordListService;

    @GetMapping("/{user-id}/{wordlist-id}")
    public List<WordList> getAllUserWordLists(
            @PathVariable("user-id") String userId,
            @PathVariable("wordlist-id") String wordlistId) {
        //TODO
        return null;
    }


    @GetMapping("/default/{wordlist-id}")
    public List<WordList> getAllSystemWordLists(
            @PathVariable("wordlist-id") String wordlistId
    ) {
        //TODO
        return null;
    }

    @GetMapping("/{wordlist-id}/word/{word-id}")
    public Word getWordInWordlist(
            @PathVariable("wordlist-id") String wordlistId,
            @PathVariable("word-id") String wordId
    ) {
        //TODO
        return null;
    }
}
