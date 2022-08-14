package com.example.drones.service;

import com.example.drones.dto.DroneDTO;
import com.example.drones.dto.DroneRegisterDTO;
import com.example.drones.dto.MedicationDTO;
import com.example.drones.enums.DroneModel;
import com.example.drones.enums.DroneState;
import com.example.drones.exception.CheckException;
import com.example.drones.exception.RestException;
import com.example.drones.mapping.DroneMapper;
import com.example.drones.mapping.DroneMapperImpl;
import com.example.drones.mapping.MedicationMapper;
import com.example.drones.mapping.MedicationMapperImpl;
import com.example.drones.model.Drone;
import com.example.drones.repository.DroneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Check business-logic here better and faster with black-box method on mocks
 */
@ExtendWith(MockitoExtension.class)
class DroneServiceTest {
    @Spy
    private final MedicationMapper medicationMapper = new MedicationMapperImpl();
    @Spy
    private final DroneMapper droneMapper = new DroneMapperImpl();
    @Mock
    private DroneRepository droneRepository;
    @Mock
    private MedicationService medicationService;
    @InjectMocks
    private DroneService droneService;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(droneMapper, "medicationMapper", medicationMapper);
    }

    @Test
    void registerFailBattery0Test() {
        // GIVEN
        DroneRegisterDTO registerDTO = DroneRegisterDTO.builder()
                .batteryCapacity(0)
                .build();

        // WHEN
        CheckException thrown = assertThrows(CheckException.class,
                () -> droneService.register(registerDTO));

        // THEN
        assertEquals("Battery capacity can't be more than 100 or less than 1", thrown.getMessage());
    }

    @Test
    void registerFailBattery101Test() {
        // GIVEN
        DroneRegisterDTO registerDTO = DroneRegisterDTO.builder()
                .batteryCapacity(101)
                .build();

        // WHEN
        CheckException thrown = assertThrows(CheckException.class,
                () -> droneService.register(registerDTO));

        // THEN
        assertEquals("Battery capacity can't be more than 100 or less than 1", thrown.getMessage());
    }

    @Test
    void registerFailWeightTest() {
        // GIVEN
        DroneRegisterDTO registerDTO = DroneRegisterDTO.builder()
                .batteryCapacity(80)
                .weightLimit(501)
                .build();

        // WHEN
        CheckException thrown = assertThrows(CheckException.class,
                () -> droneService.register(registerDTO));

        // THEN
        assertEquals("Weight limit can't be more than 500", thrown.getMessage());
    }

    @Test
    void registerTest() {
        // GIVEN
        DroneRegisterDTO registerDTO = DroneRegisterDTO.builder()
                .serialNumber("1")
                .droneModel(DroneModel.HEAVYWEIGHT)
                .batteryCapacity(80)
                .weightLimit(500)
                .build();
        Drone drone = Drone.builder()
                .id(1L)
                .serialNumber("1")
                .droneModel(DroneModel.HEAVYWEIGHT)
                .batteryCapacity(80)
                .weightLimit(500)
                .droneState(DroneState.IDLE)
                .build();

        when(droneRepository.save(any())).thenReturn(drone);

        // WHEN
        DroneDTO savedDrone = droneService.register(registerDTO);

        // THEN
        verify(droneRepository, times(1)).save(any());
        assertEquals(registerDTO.getSerialNumber(), savedDrone.getSerialNumber());
        assertEquals(registerDTO.getDroneModel(), savedDrone.getDroneModel());
        assertEquals(registerDTO.getBatteryCapacity(), savedDrone.getBatteryCapacity());
        assertEquals(registerDTO.getWeightLimit(), savedDrone.getWeightLimit());
        assertEquals(DroneState.IDLE, savedDrone.getDroneState());
    }

    @Test
    void addMedicationFailEmptyDTOsTest() {
        // WHEN
        CheckException thrown = assertThrows(CheckException.class,
                () -> droneService.addMedication(Collections.emptyList(), List.of(createMultipartFile())));

        // THEN
        assertEquals("Medications haven't been provided", thrown.getMessage());
    }

    @Test
    void addMedicationFailEmptyFilesTest() {
        // WHEN
        CheckException thrown = assertThrows(CheckException.class,
                () -> droneService.addMedication(List.of(new MedicationDTO()), Collections.emptyList()));

        // THEN
        assertEquals("Medications haven't been provided", thrown.getMessage());
    }

    @Test
    void addMedicationFailDiffSizeTest() {
        // WHEN
        CheckException thrown = assertThrows(CheckException.class,
                () -> droneService.addMedication(List.of(new MedicationDTO(), new MedicationDTO()), List.of(createMultipartFile())));

        // THEN
        assertEquals("Medications files size aren't equal to request DTOs size", thrown.getMessage());
    }

    @Test
    void addMedicationTest() {
        // GIVEN
        Drone drone = Drone.builder()
                .id(1L)
                .droneState(DroneState.IDLE)
                .batteryCapacity(50)
                .build();
        MedicationDTO medicationDTO = MedicationDTO.builder()
                .droneId(1L)
                .build();

        when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));
        when(medicationService.addMedications(anyList(), any())).thenReturn(List.of(medicationDTO));

        // WHEN
        droneService.addMedication(List.of(medicationDTO), List.of(createMultipartFile()));

        // THEN
        assertEquals(DroneState.LOADED, drone.getDroneState());
        verify(droneRepository, times(2)).save(drone);
    }

    @Test
    void getAllDroneMedicationsTest() {
        // GIVEN
        Drone drone = Drone.builder()
                .id(1L)
                .build();

        when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));
        when(medicationService.getAllDroneMedications(drone)).thenReturn(List.of(new MedicationDTO()));

        // WHEN
        List<MedicationDTO> allDroneMedications = droneService.getAllDroneMedications(1L);

        // THEN
        assertEquals(1, allDroneMedications.size());
    }

    @Test
    void getAllAvailableDronesTest() {
        // GIVEN
        Drone drone = Drone.builder()
                .id(1L)
                .build();

        when(droneRepository.getAllAvailableDrones(25)).thenReturn(List.of(drone));

        // WHEN
        List<DroneDTO> allAvailableDrones = droneService.getAllAvailableDrones();

        // THEN
        assertEquals(1, allAvailableDrones.size());
    }

    @Test
    void getDroneBatteryLevelTest() {
        // GIVEN
        Drone drone = Drone.builder()
                .id(1L)
                .batteryCapacity(80)
                .build();

        when(droneRepository.findById(1L)).thenReturn(Optional.of(drone));

        // THEN
        assertEquals(80, droneService.getDroneBatteryLevel(1L));
    }

    @Test
    void idleDroneTest() {
        // GIVEN
        Drone drone = Drone.builder()
                .id(1L)
                .droneState(DroneState.RETURNING)
                .build();

        // WHEN
        droneService.idleDrone(drone);

        // THEN
        assertEquals(DroneState.IDLE, drone.getDroneState());
        verify(droneRepository, times(1)).save(drone);
    }

    @Test
    void loadingDroneStateFailTest() {
        // GIVEN
        Drone drone = Drone.builder()
                .id(1L)
                .droneState(DroneState.RETURNING)
                .build();

        // WHEN
        RestException thrown = assertThrows(RestException.class,
                () -> droneService.loadingDrone(drone));

        // THEN
        assertEquals("Drone with id: 1 isn't available for loading", thrown.getMessage());
    }

    @Test
    void loadingDroneBatteryFailTest() {
        // GIVEN
        Drone drone = Drone.builder()
                .id(1L)
                .batteryCapacity(15)
                .droneState(DroneState.IDLE)
                .build();

        // WHEN
        RestException thrown = assertThrows(RestException.class,
                () -> droneService.loadingDrone(drone));

        // THEN
        assertEquals("Drone can't be loaded with medications with less than 25% battery", thrown.getMessage());
    }

    @Test
    void loadingDroneTest() {
        // GIVEN
        Drone drone = Drone.builder()
                .id(1L)
                .batteryCapacity(80)
                .droneState(DroneState.IDLE)
                .build();

        // WHEN
        droneService.loadingDrone(drone);

        // THEN
        assertEquals(DroneState.LOADING, drone.getDroneState());
        verify(droneRepository, times(1)).save(drone);
    }

    @Test
    void loadedDroneTest() {
        // GIVEN
        Drone drone = Drone.builder()
                .id(1L)
                .batteryCapacity(80)
                .droneState(DroneState.LOADING)
                .build();

        // WHEN
        droneService.loadedDrone(drone);

        // THEN
        assertEquals(DroneState.LOADED, drone.getDroneState());
        verify(droneRepository, times(1)).save(drone);
    }

    @Test
    void loadMedicationsFailInMedicationServiceTest() {
        // GIVEN
        Drone drone = Drone.builder()
                .id(1L)
                .droneState(DroneState.IDLE)
                .batteryCapacity(50)
                .build();
        MedicationDTO medicationDTO = MedicationDTO.builder()
                .droneId(1L)
                .build();

        when(medicationService.addMedications(anyList(), any())).thenThrow(new CheckException("Some exception in med service"));

        // WHEN
        RestException thrown = assertThrows(RestException.class,
                () -> droneService.loadMedications(List.of(medicationDTO), drone));

        // THEN
        assertEquals("Some exception in med service", thrown.getMessage());
        assertEquals(DroneState.IDLE, drone.getDroneState());
    }

    @Test
    void loadMedicationsTest() {
        // GIVEN
        Drone drone = Drone.builder()
                .id(1L)
                .droneState(DroneState.IDLE)
                .batteryCapacity(50)
                .build();
        MedicationDTO medicationDTO = MedicationDTO.builder()
                .droneId(1L)
                .build();

        when(medicationService.addMedications(anyList(), any())).thenReturn(List.of(medicationDTO));

        // WHEN
        droneService.loadMedications(List.of(medicationDTO), drone);

        // THEN
        assertEquals(DroneState.LOADED, drone.getDroneState());
        verify(droneRepository, times(1)).save(drone);
    }

    private MultipartFile createMultipartFile() {
        return new MultipartFile() {
            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getOriginalFilename() {
                return null;
            }

            @Override
            public String getContentType() {
                return null;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public long getSize() {
                return 0;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return new byte[0];
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return null;
            }

            @Override
            public void transferTo(File file) throws IOException, IllegalStateException {

            }
        };
    }
}