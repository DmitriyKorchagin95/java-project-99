package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO create(UserCreateDTO dto);

    UserDTO findById(Long id);

    List<UserDTO> findAll();

    UserDTO update(Long id, UserUpdateDTO dto);

    void delete(Long id);

    Optional<User> getById(Long id);
}
