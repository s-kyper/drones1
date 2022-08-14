package com.example.drones.repository;

import com.example.drones.enums.DroneState;
import com.example.drones.model.Drone;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Sql(value = "/init_data/drone_repository_init_data.sql", config = @SqlConfig)
@Transactional
class DroneRepositoryTest {
    private static final int MIN_BATTERY_LEVEL_TO_LOAD = 25;

    @Autowired
    private DroneRepository droneRepository;

    @Test
    void getAllAvailableDronesTest() {
        // WHEN
        List<Drone> allAvailableDrones = droneRepository.getAllAvailableDrones(MIN_BATTERY_LEVEL_TO_LOAD);

        // THEN
        assertEquals(4, allAvailableDrones.size());
        assertTrue(allAvailableDrones.stream()
                .allMatch(drone -> drone.getDroneState() == DroneState.IDLE));
        assertTrue(allAvailableDrones.stream()
                .allMatch(drone -> drone.getBatteryCapacity() >= MIN_BATTERY_LEVEL_TO_LOAD));
    }
}