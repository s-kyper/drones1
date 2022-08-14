package com.example.drones.service;

import com.example.drones.dto.MedicationDTO;
import com.example.drones.exception.CheckException;
import com.example.drones.mapping.MedicationMapper;
import com.example.drones.model.Drone;
import com.example.drones.model.Medication;
import com.example.drones.repository.MedicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MedicationService {
    private final MedicationMapper medicationMapper;
    private final MedicationRepository medicationRepository;

    /**
     * Transactional check and add meds method.
     * (Added Thread.sleep() in comments because it must be heavy operation, used for testing)
     *
     * @param medicationDTOs meds dto
     * @param drone          drone
     */
    @Transactional
    public List<MedicationDTO> addMedications(List<MedicationDTO> medicationDTOs, Drone drone) {
        //todo: delete, must be heavy operation
//        try {
//            Thread.sleep(medicationDTOs.size() * 10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        checkMedication(medicationDTOs, drone);

        List<Medication> medications = medicationMapper.toEntities(medicationDTOs);
        medications.forEach(medication -> medication.setDrone(drone));

        medications = medicationRepository.saveAll(medications);
        log.info("Added " + medicationDTOs.size() + " Medications to Drone with id: " + drone.getId());

        return medicationMapper.toDTOs(medications);
    }

    /**
     * Get all medications of current Drone
     *
     * @param drone drone
     * @return list of medications
     */
    public List<MedicationDTO> getAllDroneMedications(Drone drone) {
        return medicationMapper.toDTOs(drone.getMedications());
    }

    /**
     * Check meds DTOs (regex on name, regex on code, total weight)
     *
     * @param medicationDTOs meds DTOs
     * @param drone          drone
     */
    private void checkMedication(List<MedicationDTO> medicationDTOs, Drone drone) {
        medicationDTOs.forEach(medicationDTO -> {
            if (!medicationDTO.getName().matches("^[a-zA-Z0-9_-]*$")) {
                throw new CheckException("Only letters, numbers, underscore, dash available for name");
            }
            if (!medicationDTO.getCode().matches("^[A-Z0-9_]*$")) {
                throw new CheckException("Only upper case letters, underscore, numbers available for code");
            }
        });
        Integer medWeight = medicationDTOs.stream()
                .map(MedicationDTO::getWeight)
                .reduce(0, Integer::sum);
        if (medWeight > drone.getWeightLimit()) {
            throw new CheckException("Maximum weight capacity for drone has been reached");
        }
    }
}
