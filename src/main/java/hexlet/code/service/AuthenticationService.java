package hexlet.code.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public String authenticate(String username, String password) {

        var authentication = new UsernamePasswordAuthenticationToken(
                username,
                password
        );

        authenticationManager.authenticate(authentication);
        return jwtService.generateToken(username);
    }
}
