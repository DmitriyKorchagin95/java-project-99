package hexlet.code.service.impl;

import hexlet.code.service.AuthenticationService;
import hexlet.code.service.JsonWebTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JsonWebTokenService jsonWebTokenService;
    private final AuthenticationManager authenticationManager;

    @Override
    public String authenticate(String username, String password) {

        var authentication = new UsernamePasswordAuthenticationToken(
                username,
                password
        );

        authenticationManager.authenticate(authentication);
        return jsonWebTokenService.generateToken(username);
    }
}
