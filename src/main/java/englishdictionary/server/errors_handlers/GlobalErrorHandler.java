package englishdictionary.server.errors_handlers;

import englishdictionary.server.errors.DuplicateWordlistException;
import englishdictionary.server.errors.UserNotFoundException;
import englishdictionary.server.errors.WordNotFoundException;
import englishdictionary.server.errors.WordlistNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            WordlistNotFoundException.class,
            UserNotFoundException.class,
            WordNotFoundException.class,
            DuplicateWordlistException.class
    })
    public ResponseEntity<String> handleNotFoundException(Exception e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleSystemException(Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body("Internal server errors");
    }

}
