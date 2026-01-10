package com.example.roomservice.Controller;

import com.example.roomservice.DTO.RequestDtoRoom;
import com.example.roomservice.DTO.ResponseDtoRoom;
import com.example.roomservice.Service.RoomService;
import com.example.roomservice.Config.RsaKeys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    @Autowired
    private RsaKeys rsaKeys; // public key already configured

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadRoomImage(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        try {
            // Folder li ghadi tstore fih images
            String uploadDir = "uploads/";
            Path path = Paths.get(uploadDir + file.getOriginalFilename());
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            // URL li Angular tdisplaya
            String imageUrl = "/uploads/" + file.getOriginalFilename();
            return ResponseEntity.ok(imageUrl);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving file");
        }
    }

    // ================= GET ALL ROOMS (any USER) =================
    @GetMapping
    public ResponseEntity<List<ResponseDtoRoom>> getAllRooms() {
        List<ResponseDtoRoom> rooms = roomService.getAllRooms();
        return ResponseEntity.ok(rooms);
    }

    // ================= GET ROOM BY ID (any USER) =================
    @GetMapping("/{id}")
    public ResponseEntity<ResponseDtoRoom> getRoomById(@PathVariable int id) {
        ResponseDtoRoom room = roomService.getRoomById(id);
        if (room != null) {
            return ResponseEntity.ok(room);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // ================= HELPER METHOD =================
    private boolean isManager(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return false;

        String token = authHeader.substring(7); // remove "Bearer "

        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(rsaKeys.publicKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            Object rolesObj = claims.get("roles");
            if (rolesObj instanceof java.util.List<?> rolesList) {
                return rolesList.contains("MANAGER");
            }

            return false;
        } catch (Exception e) {
            return false; // token invalid or expired
        }
    }

    // ================= CREATE ROOM (MANAGER only) =================
    @PostMapping
    public ResponseEntity<ResponseDtoRoom> createRoom(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody RequestDtoRoom dto) {

        if (!isManager(authHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        ResponseDtoRoom room = roomService.createRoom(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    // ================= UPDATE ROOM (MANAGER only) =================
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDtoRoom> updateRoom(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int id,
            @RequestBody RequestDtoRoom dto) {

        if (!isManager(authHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        ResponseDtoRoom updated = roomService.updateRoom(id, dto);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    // ================= DELETE ROOM (MANAGER only) =================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoom(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int id) {

        if (!isManager(authHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        boolean deleted = roomService.deleteRoom(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ================= TOGGLE ROOM STATE (MANAGER only) =================
    @PatchMapping("/{id}/etat")
    public ResponseEntity<ResponseDtoRoom> toggleRoomState(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int id) {

        if (!isManager(authHeader)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        ResponseDtoRoom room = roomService.toggleRoomState(id);
        if (room != null) {
            return ResponseEntity.ok(room);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
