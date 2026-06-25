package com.bandhub.dto;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        String name,
        String surname,
        String email,
        String globalRole,
        String mainInstrument,
        String location,
        String bio,
        Boolean lookingForBand,
        LocalDateTime createdAt,
        Boolean active) {
}
