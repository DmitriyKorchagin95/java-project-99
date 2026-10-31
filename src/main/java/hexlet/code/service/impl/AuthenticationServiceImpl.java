package hexlet.code.service.impl;

import hexlet.code.service.AuthenticationService;
import hexlet.code.util.JsonWebTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final JsonWebTokenUtils jsonWebTokenUtils;
    private final AuthenticationManager authenticationManager;

    @Override
    public String authenticate(String username, String password) {

        var authentication = new UsernamePasswordAuthenticationToken(
                username,
                password
        );

        authenticationManager.authenticate(authentication);
        return jsonWebTokenUtils.generateToken(username);
    }
}
