package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private static final String NOT_FOUND_MSG_TEMPLATE = "User with id %d not found";
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final TaskRepository taskRepository;

    @Transactional
    public UserDTO create(UserCreateDTO dto) {
        log.info("Creating user with email={}", dto.getEmail());

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResourceAlreadyExistsException(
                    String.format("User with email %s already exists", dto.getEmail())
            );
        }

        var user = userMapper.map(dto);
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        var savedUser = userRepository.save(user);
        return userMapper.map(savedUser);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        log.debug("Finding user by id={}", id);

        return userRepository.findById(id)
                .map(userMapper::map)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(NOT_FOUND_MSG_TEMPLATE, id)
                ));
    }

    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        log.debug("Finding all users");

        return userRepository.findAll().stream()
                .map(userMapper::map)
                .toList();
    }

    @Transactional
    public UserDTO update(Long id, UserUpdateDTO dto) {
        log.info("Updating user id={}", id);

        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(NOT_FOUND_MSG_TEMPLATE, id)
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

    @Transactional
    public void delete(Long id) {
        log.info("Deleting user id={}", id);

        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        String.format(NOT_FOUND_MSG_TEMPLATE, id)
                ));

        if (taskRepository.existsByAssignee(user)) {
            throw new ResourceAlreadyExistsException("Cannot delete user: user has assigned tasks");
        }

        userRepository.delete(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Loading user by email={}", email);

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User with email %s not found", email)
                ));
    }
}
