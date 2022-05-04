package com.xyz.parking.controller;

import com.xyz.parking.model.Car;
import com.xyz.parking.model.Parking;
import com.xyz.parking.service.ParkingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
@RestController
@RequestMapping("/api/park")
public class ParkingController {

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    private final ParkingService parkingService;

    @GetMapping("/all")
    public List<Parking> getAllParking(){
        return parkingService.getAllParking();
    }

    @PutMapping
    public ResponseEntity<?> updateSlot(@RequestParam String plateNumber, @RequestParam String parkingId){
        synchronized (this){
            return new ResponseEntity<>(parkingService.parkSlot(plateNumber,parkingId), HttpStatus.OK);
        }
    }

    @PutMapping("/edit")
    public void editTime(@RequestParam String plateNumber, String slotId,
                         @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)  LocalDateTime toa){
        parkingService.editTimeOfParking(plateNumber,slotId,toa);
    }
}
