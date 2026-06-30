package com.bandhub.service;

import java.util.List;

import com.bandhub.dto.UserCreateDTO;
import com.bandhub.dto.UserResponseDTO;


public interface UserService {

    List<UserResponseDTO> findAllUsers();

    UserResponseDTO createUser(UserCreateDTO user);
}
