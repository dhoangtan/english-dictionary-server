package englishdictionary.server.gateways;

import englishdictionary.server.dtos.CreateWordListDto;
import englishdictionary.server.models.Word;
import englishdictionary.server.models.Wordlist;
import englishdictionary.server.services.WordlistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("api/wordlists")
public class WordListController {
    @Autowired
    private WordlistService wordlistService;

    @GetMapping("/{user-id}/{wordlist-id}")
    public List<Wordlist> getAllUserWordLists(
            @PathVariable("user-id") String userId,
            @PathVariable("wordlist-id") String wordlistId) {
        //TODO
        return null;
    }


    @GetMapping("/default/{wordlist-id}")
    public List<Wordlist> getAllSystemWordLists(
            @PathVariable("wordlist-id") String wordlistId
    ) {
        //TODO
        return null;
    }

    @GetMapping(value = "/{wordlist-id}/word/{word-id}")
    public Word getWordInWordlist(
            @PathVariable("wordlist-id") String wordlistId,
            @PathVariable("word-id") String wordId
    ) {
        //TODO
        return null;
    }

    @PostMapping(value = "/user", consumes = {
            "application/*"
    })
    public HttpStatus createWordlist(@RequestBody CreateWordListDto wordListDto) {
        Logger logger = LoggerFactory.getLogger(WordListController.class);
        try {
            logger.info("Hello");
            wordlistService.createWordlist(wordListDto.getName(),wordListDto.getUserId());
            return HttpStatus.CREATED;
        } catch (ExecutionException e) {
            logger.warn(e.getMessage());
            return HttpStatus.METHOD_NOT_ALLOWED;
        } catch (InterruptedException e) {
            logger.warn(e.getMessage());
            return HttpStatus.NOT_ACCEPTABLE;
        }
    }
}
