package com.example.sheikahregister.services.impl;

import com.example.sheikahregister.common.mappers.SpecimenMapper;
import com.example.sheikahregister.domain.dto.request.CreateSpecimenRequest;
import com.example.sheikahregister.domain.dto.request.UpdateSpecimenRequest;
import com.example.sheikahregister.domain.dto.response.PageableResponse;
import com.example.sheikahregister.domain.dto.response.specimen.SpecimenResponse;
import com.example.sheikahregister.domain.entities.Specimen;
import com.example.sheikahregister.exceptions.ResourceNotFoundException;
import com.example.sheikahregister.repositories.SpecimenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    @Test
    void getAllSpecimens_shouldReturnPageableResponse_whenDataExists() {
        Page<Specimen> entityPage = new PageImpl<>(List.of(specimenEntity));
        Page<SpecimenResponse> dtoPage = new PageImpl<>(List.of(specimenResponse));

        when(specimenRepository.findAll(any(Pageable.class))).thenReturn(entityPage);
        when(specimenMapper.toDtoList(entityPage)).thenReturn(dtoPage);

        PageableResponse<SpecimenResponse> result =
                specimenService.getAllSpecimens(0, 10, "name", "asc");

        assertThat(result.getContent()).containsExactly(specimenResponse);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void getAllSpecimens_shouldThrow_whenEmpty() {
        Page<Specimen> emptyEntityPage = new PageImpl<>(List.of());
        Page<SpecimenResponse> emptyDtoPage = new PageImpl<>(List.of());

        when(specimenRepository.findAll(any(Pageable.class))).thenReturn(emptyEntityPage);
        when(specimenMapper.toDtoList(emptyEntityPage)).thenReturn(emptyDtoPage);

        assertThatThrownBy(() -> specimenService.getAllSpecimens(0, 10, "id", "asc"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAllSpecimens_shouldBuildDescendingSort_whenSortOrderIsDesc() {
        Page<Specimen> entityPage = new PageImpl<>(List.of(specimenEntity));
        Page<SpecimenResponse> dtoPage = new PageImpl<>(List.of(specimenResponse));

        when(specimenRepository.findAll(any(Pageable.class))).thenReturn(entityPage);
        when(specimenMapper.toDtoList(entityPage)).thenReturn(dtoPage);

        specimenService.getAllSpecimens(2, 5, "dangerLevel", "desc");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(specimenRepository).findAll(pageableCaptor.capture());
        Pageable usedPageable = pageableCaptor.getValue();

        assertThat(usedPageable.getPageNumber()).isEqualTo(2);
        assertThat(usedPageable.getPageSize()).isEqualTo(5);

        Sort.Order order = usedPageable.getSort().getOrderFor("dangerLevel");
        assertThat(order).isNotNull();
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void updateSpecimen_shouldUpdateAndReturnDto_whenExists() {
        UpdateSpecimenRequest updateRequest = UpdateSpecimenRequest.builder()
                .name("Hinox")
                .region("Faron")
                .dangerLevel(6)
                .isFriendly(false)
                .build();

        Specimen updatedEntity = Specimen.builder()
                .id(specimenId)
                .name("Hinox")
                .region("Faron")
                .dangerLevel(6)
                .isFriendly(false)
                .build();

        SpecimenResponse updatedResponse = SpecimenResponse.builder()
                .id(specimenId)
                .name("Hinox")
                .region("Faron")
                .dangerLevel(6)
                .isFriendly(false)
                .build();

        when(specimenRepository.findById(specimenId)).thenReturn(Optional.of(specimenEntity));
        when(specimenMapper.toDto(specimenEntity)).thenReturn(specimenResponse);

        when(specimenMapper.toEntityUpdate(updateRequest, specimenId)).thenReturn(updatedEntity);
        when(specimenRepository.save(updatedEntity)).thenReturn(updatedEntity);
        when(specimenMapper.toDto(updatedEntity)).thenReturn(updatedResponse);

        SpecimenResponse result = specimenService.updateSpecimen(specimenId, updateRequest);

        assertThat(result).isEqualTo(updatedResponse);
        verify(specimenRepository).save(updatedEntity);
    }

    @Test
    void updateSpecimen_shouldThrowAndNotSave_whenNotFound() {
        UpdateSpecimenRequest updateRequest = UpdateSpecimenRequest.builder()
                .name("Hinox").region("Faron").dangerLevel(6).isFriendly(false)
                .build();

        when(specimenRepository.findById(specimenId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> specimenService.updateSpecimen(specimenId, updateRequest))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(specimenRepository, never()).save(any());
        verify(specimenMapper, never()).toEntityUpdate(any(), any());
    }
}
