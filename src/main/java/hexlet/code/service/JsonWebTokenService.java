package hexlet.code.service;

public interface JsonWebTokenService {
    String generateToken(String username);
}
