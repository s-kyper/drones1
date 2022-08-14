package com.example.drones.repository;

import com.example.drones.model.Drone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DroneRepository extends JpaRepository<Drone, Long> {
    @Query(value = "select * from drones where drone_state = 'IDLE' and battery_capacity > :minBatteryLevel", nativeQuery = true)
    List<Drone> getAllAvailableDrones(int minBatteryLevel);
}
