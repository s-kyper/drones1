package com.example.drones.controller;

import com.example.drones.dto.DroneDTO;
import com.example.drones.dto.DroneRegisterDTO;
import com.example.drones.dto.MedicationDTO;
import com.example.drones.service.DroneService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/drones")
@RequiredArgsConstructor
public class DroneController {
    private final DroneService droneService;

    @PostMapping("/register")
    public DroneDTO registerDrone(@RequestBody DroneRegisterDTO droneRegisterDTO) {
        return droneService.register(droneRegisterDTO);
    }

    @PostMapping("/medication")
    public void addMedications(@RequestParam List<MultipartFile> files,
                               @RequestBody List<MedicationDTO> medicationDTOs) {
        droneService.addMedication(medicationDTOs, files);
    }

    @GetMapping("/{droneId}/medications")
    public List<MedicationDTO> getAllDroneMedications(@PathVariable("droneId") Long droneId) {
        return droneService.getAllDroneMedications(droneId);
    }

    @GetMapping("/available")
    public List<DroneDTO> getAllAvailableDrones() {
        return droneService.getAllAvailableDrones();
    }

    @GetMapping("/{droneId}/battery-level")
    public Integer getDroneBatteryLevel(@PathVariable("droneId") Long droneId) {
        return droneService.getDroneBatteryLevel(droneId);
    }
}
