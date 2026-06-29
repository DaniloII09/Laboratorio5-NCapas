package com.example.sheikahregister.domain.dto.response.specimen;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecimenResponse {
    private UUID id;
    private String name;
    private String region;
    private Integer dangerLevel;
    private Boolean isFriendly;
}
