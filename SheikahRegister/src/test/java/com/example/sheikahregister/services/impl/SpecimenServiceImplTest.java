package com.example.sheikahregister.services.impl;

import com.example.sheikahregister.common.mappers.SpecimenMapper;
import com.example.sheikahregister.domain.dto.request.CreateSpecimenRequest;
import com.example.sheikahregister.domain.dto.response.specimen.SpecimenResponse;
import com.example.sheikahregister.domain.entities.Specimen;
import com.example.sheikahregister.repositories.SpecimenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class SpecimenServiceImplTest {

    @Mock
    private SpecimenRepository specimenRepository;

    @Mock
    private SpecimenMapper specimenMapper;

    @InjectMocks
    private SpecimenServiceImpl specimenService;

    @BeforeEach
    void setUp() {
        UUID specimenId = UUID.randomUUID();

        CreateSpecimenRequest createRequest = CreateSpecimenRequest.builder()
                .name("Lynel")
                .region("Hebra Mountains")
                .dangerLevel(9)
                .isFriendly(false)
                .build();

        Specimen specimenEntity = Specimen.builder()
                .id(specimenId)
                .name("Lynel")
                .region("Hebra Mountains")
                .dangerLevel(9)
                .isFriendly(false)
                .build();

        SpecimenResponse specimenResponse = SpecimenResponse.builder()
                .id(specimenId)
                .name("Lynel")
                .region("Hebra Mountains")
                .dangerLevel(9)
                .isFriendly(false)
                .build();
    }
}
