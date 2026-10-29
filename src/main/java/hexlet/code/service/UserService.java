package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;

import java.util.List;

public interface UserService {
    UserDTO create(UserCreateDTO dto);

    UserDTO findById(Long id);

    List<UserDTO> findAll();

    UserDTO update(Long id, UserUpdateDTO dto);

    void delete(Long id);
}
