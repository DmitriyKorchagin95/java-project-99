package hexlet.code.conroller;

import hexlet.code.dto.AuthRequest;
import hexlet.code.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody AuthRequest authRequest) {
        return new ResponseEntity<>(
                authenticationService.authenticate(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                ),
                HttpStatus.OK);
    }
}
