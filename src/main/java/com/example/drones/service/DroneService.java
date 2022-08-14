package com.example.drones.service;

import com.example.drones.dto.DroneDTO;
import com.example.drones.dto.DroneRegisterDTO;
import com.example.drones.dto.MedicationDTO;
import com.example.drones.enums.DroneState;
import com.example.drones.exception.CheckException;
import com.example.drones.exception.RestException;
import com.example.drones.mapping.DroneMapper;
import com.example.drones.model.Drone;
import com.example.drones.repository.DroneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DroneService {
    private static final int MIN_BATTERY_LEVEL_TO_LOAD = 25;
    private static final int MIN_BATTERY_CAPACITY = 1;
    private static final int MAX_BATTERY_CAPACITY = 100;
    private static final int WEIGHT_LIMIT = 500;

    private final DroneMapper droneMapper;
    private final DroneRepository droneRepository;
    private final MedicationService medicationService;

    /**
     * Register new Drone method.
     *
     * @param droneRegisterDTO register DTO
     * @return new registered Drone
     */
    public DroneDTO register(DroneRegisterDTO droneRegisterDTO) {
        log.info("Start creating new Drone");
        checkDroneRegisterDTO(droneRegisterDTO);

        Drone newDrone = droneMapper.toEntity(droneRegisterDTO);
        newDrone.setDroneState(DroneState.IDLE);
        newDrone = droneRepository.save(newDrone);
        log.info("Added Drone with id: " + newDrone.getId());

        return droneMapper.toDTO(newDrone);
    }

    /**
     * Non-blocking at all add medications to drone method. Stages:
     * 1) check and parse files
     * 2) set Drone state LOADING
     * 3) check and add medications
     * 4) set Drone state LOADED
     *
     * @param medicationDTOs medication DTOs
     * @param files          MultipartFiles to DTOs
     */
    public void addMedication(List<MedicationDTO> medicationDTOs, List<MultipartFile> files) {
        log.info("Start creating new Medication");
        checkMedicationRequestFiles(medicationDTOs, files);

        Long droneId = medicationDTOs.get(0).getDroneId();
        Drone drone = getDroneSafety(droneId);
        IntStream.range(0, medicationDTOs.size())
                .forEach(i -> {
                    try {
                        medicationDTOs.get(i).setImage(files.get(i).getBytes());
                    } catch (IOException e) {
                        throw new RestException("Exception during reading file");
                    }
                });

        loadingDrone(drone);
        loadMedications(medicationDTOs, drone);
    }

    /**
     * Get all medications of current Drone
     *
     * @param droneId drone id
     * @return list of medications
     */
    public List<MedicationDTO> getAllDroneMedications(Long droneId) {
        log.info("Start getting all Medications for Drone with id: " + droneId);
        Drone drone = getDroneSafety(droneId);
        return medicationService.getAllDroneMedications(drone);
    }

    /**
     * Get all available drones
     *
     * @return available drones
     */
    public List<DroneDTO> getAllAvailableDrones() {
        return droneMapper.toDTOs(droneRepository.getAllAvailableDrones(MIN_BATTERY_LEVEL_TO_LOAD));
    }

    /**
     * Check drone battery level
     *
     * @param droneId drone id
     * @return battery level
     */
    public Integer getDroneBatteryLevel(Long droneId) {
        log.info("Check battery level for Drone with id: " + droneId);
        return getDroneSafety(droneId).getBatteryCapacity();
    }

    /**
     * Set drone state to IDLE in transaction
     *
     * @param drone drone
     */
    @Transactional
    void idleDrone(Drone drone) {
        drone.setDroneState(DroneState.IDLE);
        droneRepository.save(drone);
    }

    /**
     * Check and set drone state to LOADIN in transaction
     *
     * @param drone drone
     */
    @Transactional
    void loadingDrone(Drone drone) {
        checkDroneForLoading(drone);
        drone.setDroneState(DroneState.LOADING);
        droneRepository.save(drone);
    }

    /**
     * Set drone state to LOADED in transaction
     *
     * @param drone drone
     */
    @Transactional
    void loadedDrone(Drone drone) {
        drone.setDroneState(DroneState.LOADED);
        droneRepository.save(drone);
    }

    /**
     * Loading meds transactional method
     *
     * @param medicationDTOs medication DTOs
     * @param drone          drone
     */
    @Transactional
    void loadMedications(List<MedicationDTO> medicationDTOs, Drone drone) {
        try {
            medicationService.addMedications(medicationDTOs, drone);
            loadedDrone(drone);
        } catch (CheckException e) {
            idleDrone(drone);
            throw new RestException(e.getMessage());
        }
    }

    /**
     * Get drone by id or throw Exception
     *
     * @param droneId drone id
     * @return drone
     */
    private Drone getDroneSafety(Long droneId) {
        return droneRepository.findById(droneId)
                .orElseThrow(() -> new RestException("No drone found with id: " + droneId));
    }

    /**
     * Check Drone register DTO
     *
     * @param droneRegisterDTO register DTO
     */
    private void checkDroneRegisterDTO(DroneRegisterDTO droneRegisterDTO) {
        if (droneRegisterDTO.getBatteryCapacity() > MAX_BATTERY_CAPACITY
                || droneRegisterDTO.getBatteryCapacity() < MIN_BATTERY_CAPACITY) {
            throw new CheckException("Battery capacity can't be more than " + MAX_BATTERY_CAPACITY
                    + " or less than " + MIN_BATTERY_CAPACITY);
        }
        if (droneRegisterDTO.getWeightLimit() > WEIGHT_LIMIT) {
            throw new CheckException("Weight limit can't be more than " + WEIGHT_LIMIT);
        }
    }

    /**
     * Check Meds register DTO and files (not empty + equals sizes)
     *
     * @param medicationDTOs medication DTOs
     * @param files          MultipartFiles
     */
    private void checkMedicationRequestFiles(List<MedicationDTO> medicationDTOs, List<MultipartFile> files) {
        if (CollectionUtils.isEmpty(medicationDTOs) || CollectionUtils.isEmpty(files)) {
            throw new CheckException("Medications haven't been provided");
        }
        if (medicationDTOs.size() != files.size()) {
            throw new CheckException("Medications files size aren't equal to request DTOs size");
        }
    }

    /**
     * Check if drone is available for Loading
     *
     * @param drone drone
     */
    private void checkDroneForLoading(Drone drone) {
        if (drone.getDroneState() != DroneState.IDLE) {
            throw new RestException("Drone with id: " + drone.getId() + " isn't available for loading");
        }
        if (drone.getBatteryCapacity() < MIN_BATTERY_LEVEL_TO_LOAD) {
            throw new RestException("Drone can't be loaded with medications with less than " + MIN_BATTERY_LEVEL_TO_LOAD + "% battery");
        }
    }
}
