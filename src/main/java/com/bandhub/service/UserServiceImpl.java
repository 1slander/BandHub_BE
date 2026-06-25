package com.bandhub.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.bandhub.dto.UserResponseDTO;
import com.bandhub.model.UserEntity;
import com.bandhub.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository){
        this.userRepository=userRepository;
    }

 
    @Override
    public List<UserResponseDTO> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private UserResponseDTO toResponse(UserEntity user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getGlobalRole(),
                user.getMainInstrument(),
                user.getLocation(),
                user.getBio(),
                user.getLookingForBand(),
                user.getCreatedAt(),
                user.getActive());
    }
}
