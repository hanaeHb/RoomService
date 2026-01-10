package com.example.roomservice.Service;

import com.example.roomservice.DTO.RequestDtoRoom;
import com.example.roomservice.DTO.ResponseDtoRoom;
import com.example.roomservice.Mapper.RoomMapper;
import com.example.roomservice.Repository.RoomReposiitory;
import com.example.roomservice.entity.Room;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final RoomReposiitory roomRepository;

    public RoomService(RoomReposiitory roomRepository) {
        this.roomRepository = roomRepository;
    }

    // List all rooms
    public List<ResponseDtoRoom> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(RoomMapper::entityToDto)
                .collect(Collectors.toList());
    }

    // Get room by ID
    public ResponseDtoRoom getRoomById(int id) {
        Optional<Room> room = roomRepository.findById(id);
        return room.map(RoomMapper::entityToDto).orElse(null);
    }

    // Create new room
    public ResponseDtoRoom createRoom(RequestDtoRoom dto) {
        Room room = RoomMapper.dtoToEntity(dto);
        Room savedRoom = roomRepository.save(room); // save in DB
        return RoomMapper.entityToDto(savedRoom);
    }

    // Update room
    public ResponseDtoRoom updateRoom(int id, RequestDtoRoom dto) {
        Optional<Room> roomOpt = roomRepository.findById(id);
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            room.setNumero(dto.getNumero());
            room.setType(dto.getType());
            room.setPrix(dto.getPrix());
            room.setEtat(dto.getEtat());
            room.setDescription(dto.getDescription());
            room.setImage(dto.getImage());
            room.setTaux(dto.getTaux());
            Room updatedRoom = roomRepository.save(room); // update in DB
            return RoomMapper.entityToDto(updatedRoom);
        }
        return null;
    }

    // Delete room
    public boolean deleteRoom(int id) {
        if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Change room state (libre <-> occupée)
    public ResponseDtoRoom toggleRoomState(int id) {
        Optional<Room> roomOpt = roomRepository.findById(id);
        if (roomOpt.isPresent()) {
            Room room = roomOpt.get();
            room.setEtat(room.getEtat().equalsIgnoreCase("libre") ? "occupée" : "libre");
            Room updatedRoom = roomRepository.save(room); // save change in DB
            return RoomMapper.entityToDto(updatedRoom);
        }
        return null;
    }
}
