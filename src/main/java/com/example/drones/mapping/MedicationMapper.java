package com.example.drones.mapping;

import com.example.drones.dto.MedicationDTO;
import com.example.drones.model.Medication;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface MedicationMapper {
    Medication toEntity(MedicationDTO dto);

    List<Medication> toEntities(List<MedicationDTO> dtos);

    @Mapping(target = "droneId", source = "entity.drone.id")
    MedicationDTO toDTO(Medication entity);

    List<MedicationDTO> toDTOs(List<Medication> entities);
}
