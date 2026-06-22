package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserDTO create(UserCreateDTO dto) {
        log.info("Creating user with email={}", dto.getEmail());

        var user = userMapper.map(dto);

        user.setPasswordHash(
                passwordEncoder.encode(dto.getPassword())
        );

        var savedUser = userRepository.save(user);

        return userMapper.map(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        log.debug("Finding user by id={}", id);

        return userRepository.findById(id)
                .map(userMapper::map)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("User with id %d not found", id)
                        ));
    }

    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        log.debug("Finding all users");

        return userRepository.findAll()
                .stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO update(Long id, UserUpdateDTO dto) {
        log.info("Updating user id={}", id);

        var user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("User with id %d not found", id)
                        ));

        userMapper.update(dto, user);

        if (dto.getPassword().isPresent()) {
            user.setPasswordHash(
                    passwordEncoder.encode(dto.getPassword().get())
            );
        }

        var updatedUser = userRepository.save(user);

        return userMapper.map(updatedUser);
    }

    public void delete(Long id) {
        log.info("Deleting user id={}", id);

        var user = userRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                String.format("User with id %d not found", id)
                        ));

        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }
}
