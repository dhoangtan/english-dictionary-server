package englishdictionary.server.gateways;

import englishdictionary.server.dtos.AddWordDto;
import englishdictionary.server.dtos.CreateWordListDto;
import englishdictionary.server.dtos.RenameWordListDto;
import englishdictionary.server.errors.UserNotFoundException;
import englishdictionary.server.errors.WordNotFoundException;
import englishdictionary.server.errors.WordlistNotFoundException;
import englishdictionary.server.models.User;
import englishdictionary.server.models.Word;
import englishdictionary.server.models.Wordlist;
import englishdictionary.server.services.WordlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<Wordlist>> getAllUserWordLists(@PathVariable("user-id") String userId) {
        try {
            List<Wordlist> wordlists = wordlistService.getAllUserWordLists(userId);
            return new ResponseEntity<>(wordlists, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{user-id}/{wordlist-id}")
    public ResponseEntity<Wordlist> getUserWordlist(@PathVariable("user-id") String userId, @PathVariable("wordlist-id") String wordlistId) {
        try {
            Wordlist wordlist = wordlistService.getWordlistById(wordlistId);
            return new ResponseEntity<>(wordlist, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public List<Wordlist> searchForWordList(@RequestParam(name = "name") String name, @RequestParam(name = "word") String word) {
        try {
            return wordlistService.searchForWordlist(name, word);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/{wordlist-id}/word/{word-id}")
    public Word getWordInWordlist(
            @PathVariable("wordlist-id") String wordlistId,
            @PathVariable("word-id") Integer wordId
    ) {
        try {
            Word word = wordlistService.getWordlistWord(wordlistId, wordId);
            return word;
        }
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "")
    public ResponseEntity<Wordlist> createWordlist(@RequestBody CreateWordListDto wordListDto) {
        try {
            Wordlist wordlist = wordlistService.createWordlist(wordListDto.getName(),wordListDto.getUserId());
            return new ResponseEntity<>(wordlist, HttpStatus.CREATED);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public HttpStatus deleteWordlist(@PathVariable("id") String id) {
        if (wordlistService.deleteWordlist(id))
            return HttpStatus.ACCEPTED;
        return HttpStatus.BAD_REQUEST;
    }

    @DeleteMapping("/{wordlist-id}/word/{word-id}")
    public HttpStatus deleteWordlistWord(
            @PathVariable("wordlist-id") String wordlistId,
            @PathVariable("word-id") Integer wordId
    ) {
        try {
            if (wordlistService.removeWordlistWord(wordlistId, wordId))
                return HttpStatus.OK;
            return HttpStatus.BAD_REQUEST;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("")
    public HttpStatus renameWordList(@RequestBody RenameWordListDto dto) {
        try {
            if (wordlistService.renameWordList(dto.getId(), dto.getName()))
                return HttpStatus.OK;
            return HttpStatus.BAD_REQUEST;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/word")
    public HttpStatus addWordToWordList(@RequestBody AddWordDto dto) {
        try {
            if (wordlistService.addWordToWordlist(dto.getWordlistId(), dto.getWord()))
                return HttpStatus.CREATED;
            return HttpStatus.BAD_REQUEST;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
