package com.example.drones.mapping;

import com.example.drones.dto.DroneDTO;
import com.example.drones.dto.DroneRegisterDTO;
import com.example.drones.model.Drone;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(uses = MedicationMapper.class)
public interface DroneMapper {
    Drone toEntity(DroneDTO dto);

    DroneDTO toDTO(Drone entity);

    List<DroneDTO> toDTOs(List<Drone> entities);

    Drone toEntity(DroneRegisterDTO entity);
}
