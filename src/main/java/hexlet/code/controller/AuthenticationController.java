package hexlet.code.controller;

import hexlet.code.dto.AuthRequest;
import hexlet.code.service.AuthenticationService;
import io.sentry.Sentry;
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
    public ResponseEntity<String> create(@RequestBody AuthRequest authRequest) {

        try {
            throw new Exception("This is a test.");
        } catch (Exception e) {
            Sentry.captureException(e);
        }

        return new ResponseEntity<>(
                authenticationService.authenticate(
                        authRequest.getUsername(),
                        authRequest.getPassword()
                ),
                HttpStatus.OK);
    }
}
