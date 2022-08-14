package com.example.drones.dto;

import com.example.drones.enums.DroneModel;
import com.example.drones.enums.DroneState;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
public class DroneDTO extends AbstractBaseDTO {
    private Long id;
    private String serialNumber;
    private DroneModel droneModel;
    private int weightLimit;
    private int batteryCapacity;
    private DroneState droneState;
    private List<MedicationDTO> medications = new ArrayList<>();
}
