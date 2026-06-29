package com.example.sheikahregister.services.impl;

import com.example.sheikahregister.common.mappers.SpecimenMapper;
import com.example.sheikahregister.domain.dto.request.CreateSpecimenRequest;
import com.example.sheikahregister.domain.dto.response.specimen.SpecimenResponse;
import com.example.sheikahregister.domain.entities.Specimen;
import com.example.sheikahregister.exceptions.ResourceNotFoundException;
import com.example.sheikahregister.repositories.SpecimenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpecimenServiceImplTest {

    @Mock
    private SpecimenRepository specimenRepository;

    @Mock
    private SpecimenMapper specimenMapper;

    @InjectMocks
    private SpecimenServiceImpl specimenService;

    private UUID specimenId;
    private CreateSpecimenRequest createRequest;
    private Specimen specimenEntity;
    private SpecimenResponse specimenResponse;

    @BeforeEach
    void setUp() {
        specimenId = UUID.randomUUID();

        createRequest = CreateSpecimenRequest.builder()
                .name("Lynel")
                .region("Hebra Mountains")
                .dangerLevel(9)
                .isFriendly(false)
                .build();

        specimenEntity = Specimen.builder()
                .id(specimenId)
                .name("Lynel")
                .region("Hebra Mountains")
                .dangerLevel(9)
                .isFriendly(false)
                .build();

        specimenResponse = SpecimenResponse.builder()
                .id(specimenId)
                .name("Lynel")
                .region("Hebra Mountains")
                .dangerLevel(9)
                .isFriendly(false)
                .build();
    }

    @Test
    void createSpecimen_shouldMapSaveAndReturnDto() {
        when(specimenMapper.toEntityCreate(createRequest)).thenReturn(specimenEntity);
        when(specimenRepository.save(specimenEntity)).thenReturn(specimenEntity);
        when(specimenMapper.toDto(specimenEntity)).thenReturn(specimenResponse);

        SpecimenResponse result = specimenService.createSpecimen(createRequest);

        assertThat(result).isEqualTo(specimenResponse);
        verify(specimenRepository).save(specimenEntity);
    }

    @Test
    void getSpecimenById_shouldReturnDto_whenExists() {
        when(specimenRepository.findById(specimenId)).thenReturn(Optional.of(specimenEntity));
        when(specimenMapper.toDto(specimenEntity)).thenReturn(specimenResponse);

        SpecimenResponse result = specimenService.getSpecimenById(specimenId);

        assertThat(result).isEqualTo(specimenResponse);
        verify(specimenRepository).findById(specimenId);
    }

    @Test
    void getSpecimenById_shouldThrow_whenNotFound() {
        when(specimenRepository.findById(specimenId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specimenService.getSpecimenById(specimenId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("not found");

        verify(specimenMapper, never()).toDto(any());
    }
}
