package com.example.roomservice.Mapper;

import com.example.roomservice.DTO.RequestDtoRoom;
import com.example.roomservice.DTO.ResponseDtoRoom;
import com.example.roomservice.entity.Room;
import org.springframework.stereotype.Component;


@Component
public class RoomMapper {

    public static Room dtoToEntity(RequestDtoRoom dto) {
        Room room = new Room();
        room.setId(null);
        room.setNumero(dto.getNumero());
        room.setType(dto.getType());
        room.setPrix(dto.getPrix());
        room.setEtat(dto.getEtat());
        room.setDescription(dto.getDescription());
        room.setImage(dto.getImage());
        room.setTaux(dto.getTaux());
        return room;
    }

    public static ResponseDtoRoom entityToDto(Room entity) {
        ResponseDtoRoom dto = new ResponseDtoRoom();
        dto.setId(entity.getId());
        dto.setNumero(entity.getNumero());
        dto.setType(entity.getType());
        dto.setPrix(entity.getPrix());
        dto.setEtat(entity.getEtat());
        dto.setDescription(entity.getDescription());
        dto.setImage(entity.getImage());
        dto.setTaux(entity.getTaux());
        return dto;
    }
}
