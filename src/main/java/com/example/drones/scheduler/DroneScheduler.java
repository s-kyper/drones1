package com.example.drones.scheduler;

import com.example.drones.repository.DroneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DroneScheduler {
    private final DroneRepository droneRepository;

    /**
     * Scheduler to log drones states every 1 minute
     */
    @Scheduled(fixedRate = 60000)
    public void checkDronesBatteriesLevels() {
        StringBuilder sb = new StringBuilder();
        droneRepository.findAll()
                .forEach(drone -> sb.append("\n")
                        .append("Drone id: ").append(drone.getId())
                        .append(", Battery level: ").append(drone.getBatteryCapacity()));
        log.info(sb.toString());
    }
}
