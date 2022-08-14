package com.example.drones.service;

import com.example.drones.dto.MedicationDTO;
import com.example.drones.exception.CheckException;
import com.example.drones.mapping.MedicationMapper;
import com.example.drones.mapping.MedicationMapperImpl;
import com.example.drones.model.Drone;
import com.example.drones.model.Medication;
import com.example.drones.repository.MedicationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Check business-logic here better and faster with black-box method on mocks
 */
@ExtendWith(MockitoExtension.class)
class MedicationServiceTest {
    @Spy
    private final MedicationMapper medicationMapper = new MedicationMapperImpl();
    @Mock
    private MedicationRepository medicationRepository;
    @InjectMocks
    private MedicationService medicationService;

    @Test
    void addMedicationsFailNameTest() {
        // GIVEN
        List<MedicationDTO> medicationDTOs = List.of(MedicationDTO.builder()
                .name("name!")
                .build());

        // WHEN
        CheckException thrown = assertThrows(CheckException.class,
                () -> medicationService.addMedications(medicationDTOs, new Drone()));

        // THEN
        assertEquals("Only letters, numbers, underscore, dash available for name", thrown.getMessage());
    }

    @Test
    void addMedicationsFailCodeTest() {
        // GIVEN
        List<MedicationDTO> medicationDTOs = List.of(MedicationDTO.builder()
                .name("name-1")
                .code("CODE-1")
                .build());

        // WHEN
        CheckException thrown = assertThrows(CheckException.class,
                () -> medicationService.addMedications(medicationDTOs, new Drone()));

        // THEN
        assertEquals("Only upper case letters, underscore, numbers available for code", thrown.getMessage());
    }

    @Test
    void addMedicationsFailWeightTest() {
        // GIVEN
        List<MedicationDTO> medicationDTOs = List.of(
                MedicationDTO.builder()
                        .name("name-1")
                        .code("CODE_1")
                        .weight(200)
                        .build(),
                MedicationDTO.builder()
                        .name("name-2")
                        .code("CODE_2")
                        .weight(200)
                        .build());
        Drone drone = Drone.builder()
                .weightLimit(300)
                .build();

        // WHEN
        CheckException thrown = assertThrows(CheckException.class,
                () -> medicationService.addMedications(medicationDTOs, drone));

        // THEN
        assertEquals("Maximum weight capacity for drone has been reached", thrown.getMessage());
    }

    @Test
    void addMedicationsTest() {
        // GIVEN
        List<MedicationDTO> medicationDTOs = List.of(
                MedicationDTO.builder()
                        .name("name-1")
                        .code("CODE_1")
                        .weight(200)
                        .build(),
                MedicationDTO.builder()
                        .name("name-2")
                        .code("CODE_2")
                        .weight(200)
                        .build());
        Drone drone = Drone.builder()
                .weightLimit(400)
                .build();

        // WHEN
        List<MedicationDTO> savedMeds = medicationService.addMedications(medicationDTOs, drone);

        // THEN
        verify(medicationRepository, times(1)).saveAll(anyCollection());
        savedMeds.forEach(med -> assertEquals(drone.getId(), med.getDroneId()));
    }

    @Test
    void getAllDroneMedicationsTest() {
        // GIVEN
        Drone drone = Drone.builder()
                .id(1L)
                .build();
        Medication medication = Medication.builder()
                .id(1L)
                .name("name")
                .weight(30)
                .code("CODE")
                .image(new byte[0])
                .drone(drone)
                .build();
        drone.setMedications(List.of(medication));

        // WHEN
        List<MedicationDTO> allDroneMedications = medicationService.getAllDroneMedications(drone);

        // THEN
        assertEquals(drone.getMedications().size(), allDroneMedications.size());
        assertEquals(drone.getMedications().get(0).getId(), allDroneMedications.get(0).getId());
        assertEquals(drone.getMedications().get(0).getName(), allDroneMedications.get(0).getName());
        assertEquals(drone.getMedications().get(0).getWeight(), allDroneMedications.get(0).getWeight());
        assertEquals(drone.getMedications().get(0).getCode(), allDroneMedications.get(0).getCode());
        assertEquals(drone.getMedications().get(0).getImage().length, allDroneMedications.get(0).getImage().length);
        assertEquals(drone.getMedications().get(0).getDrone().getId(), allDroneMedications.get(0).getDroneId());
    }
}