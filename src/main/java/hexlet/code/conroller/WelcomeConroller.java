package hexlet.code.conroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeConroller {

    @GetMapping("/welcome")
    public ResponseEntity<?> welcome() {
        return new ResponseEntity<>("Welcome to Spring Boot!", HttpStatus.OK);
    }
}
