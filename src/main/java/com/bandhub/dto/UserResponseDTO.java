package com.bandhub.dto;

import java.time.LocalDateTime;

import com.bandhub.model.UserRole;

public record UserResponseDTO(
        Long id,
        String name,
        String surname,
        String email,
        UserRole role,
        String mainInstrument,
        String location,
        String bio,
        Boolean lookingForBand,
        LocalDateTime createdAt,
        Boolean active) {
}
