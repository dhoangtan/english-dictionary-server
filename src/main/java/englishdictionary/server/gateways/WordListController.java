package englishdictionary.server.gateways;

import englishdictionary.server.models.Word;
import englishdictionary.server.models.WordList;
import englishdictionary.server.services.WordlistService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/wordlists")
public class WordListController {
    private final WordlistService wordlistService;

    public WordListController(WordlistService wordlistService) {
        this.wordlistService = wordlistService;
    }

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
