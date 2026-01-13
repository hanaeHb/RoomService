package com.example.roomservice.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class RoomStatsDto {

    private long totalRooms;
    private long availableRooms;
    private long occupiedRooms;
    private long maintenanceRooms;

    private long singleRooms;
    private long doubleRooms;
    private long suiteRooms;

    private double averagePrice;




}
