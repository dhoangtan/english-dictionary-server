package englishdictionary.server.gateways;

import englishdictionary.server.dtos.CreateWordListDto;
import englishdictionary.server.dtos.RenameWordListDto;
import englishdictionary.server.models.Word;
import englishdictionary.server.models.Wordlist;
import englishdictionary.server.services.WordlistService;
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
    private WordlistService wordlistService;

    @GetMapping("/{user-id}")
    public List<Wordlist> getAllUserWordLists(@PathVariable("user-id") String userId) {
        try {
            return wordlistService.getAllUserWordLists(userId);
        } catch (ExecutionException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/{user-id}/{wordlist-id}")
    public Wordlist getUserWordlist(@PathVariable("user-id") String userId, @PathVariable("wordlist-id") String wordlistId) {
        try {
            return wordlistService.getWordlistById(wordlistId);
        } catch (ExecutionException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/search")
    public List<Wordlist> searchForWordList(@RequestParam(name = "name") String name, @RequestParam(name = "word") String word) {
        try {
            return wordlistService.searchForWordlist(name, word);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(value = "/{wordlist-id}/word/{word-id}")
    public Word getWordInWordlist(
            @PathVariable("wordlist-id") String wordlistId,
            @PathVariable("word-id") Integer wordId
    ) {
        try {
            Word word = wordlistService.getWordlistWord(wordlistId, wordId);
            if (word == null)
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            return word;
        } catch (ExecutionException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } catch (InterruptedException e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping(value = "")
    public HttpStatus createWordlist(@RequestBody CreateWordListDto wordListDto) {
        try {
            wordlistService.createWordlist(wordListDto.getName(),wordListDto.getUserId());
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return HttpStatus.CREATED;
    }

    @DeleteMapping("/{id}")
    public HttpStatus deleteWordlist(@PathVariable("id") String id) {
        if (wordlistService.deleteWordlist(id))
            return HttpStatus.ACCEPTED;
        return HttpStatus.NOT_FOUND;
    }

    @DeleteMapping("/{wordlist-id}/word/{word-id}")
    public HttpStatus deleteWordlistWord(
            @PathVariable("wordlist-id") String wordlistId,
            @PathVariable("word-id") Integer wordId
    ) {
        try {
            if (wordlistService.removeWordlistWord(wordlistId, wordId))
                return HttpStatus.ACCEPTED;
            return HttpStatus.NOT_ACCEPTABLE;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("")
    public HttpStatus renameWordList(@RequestBody RenameWordListDto dto) {
        try {
            if (wordlistService.renameWordList(dto.getId(), dto.getName()))
                return HttpStatus.ACCEPTED;
            return HttpStatus.NOT_ACCEPTABLE;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
