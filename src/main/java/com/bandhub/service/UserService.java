package com.bandhub.service;

import java.net.URI;
import java.util.List;

import com.bandhub.dto.UserCreateDTO;
import com.bandhub.dto.UserResponseDTO;
import com.bandhub.model.UserEntity;

public interface UserService {

    List<UserResponseDTO> findAllUsers();

    UserResponseDTO createUser(UserCreateDTO user);
}
