package com.bandhub.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.bandhub.dto.UserCreateDTO;
import com.bandhub.dto.UserResponseDTO;
import com.bandhub.model.UserEntity;
import com.bandhub.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository=userRepository;
        this.passwordEncoder=passwordEncoder;
    }

 
    @Override
    public List<UserResponseDTO> findAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    
    
    @Override
    public UserResponseDTO createUser(UserCreateDTO user) {

        

        if(userRepository.existsByEmail(user.email())){
           System.out.println("Ya existe un usuario registrado con email: " + user.email());
           throw new IllegalArgumentException("Email ya existe.");
         
        }

        // Posibles validaciones avanzadas

        // Si el usuario no existe, entonces creamos el user a partir del CreateDTO

        UserEntity newUser = new UserEntity();

        newUser.setEmail(user.email());
        // Tenemos que hash el password
        newUser.setPasswordHash(passwordEncoder.encode(user.password()));
        newUser.setName(user.name());
        newUser.setSurname(user.surname());
        newUser.setMainInstrument(user.mainInstrument());
        newUser.setBio(user.bio());
        newUser.setLocation(user.location());
        newUser.setLookingForBand(user.lookingForBand()== null ? false: user.lookingForBand());
        
        
       

        UserEntity savedUser=userRepository.save(newUser);

        return toResponse(savedUser);
        
    }


    private UserResponseDTO toResponse(UserEntity user) {
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getRole(),
                user.getMainInstrument(),
                user.getLocation(),
                user.getBio(),
                user.getLookingForBand(),
                user.getCreatedAt(),
                user.getActive());
    }
}
