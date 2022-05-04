package com.xyz.parking.controller;

import com.xyz.parking.model.Car;
import com.xyz.parking.model.Parking;
import com.xyz.parking.model.Sizes;
import com.xyz.parking.service.CarService;
import com.xyz.parking.service.ParkingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.DELETE, RequestMethod.PUT})
@Slf4j
@RestController
@RequestMapping("/api/cars")
public class CarController {

    private final CarService carService;
    private final ParkingService parkingService;

    public CarController(CarService carService, ParkingService parkingService) {
        this.carService = carService;
        this.parkingService = parkingService;
    }


    @PostMapping
    public ResponseEntity<?> saveCar(@RequestBody Car car,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toa){
        synchronized (this){
            log.info("CHECK FOR REPARKING: {}",carService.checkIfRepark(car));
            if (carService.checkIfRepark(car)){
                carService.updateCarRepark(car,toa);
            } else {
                carService.saveCar(car, toa);
            }
            return new ResponseEntity<>("CAR IS PARKED",HttpStatus.OK);
        }

    }

    @GetMapping
    public List<Car> getAllCars(){
        return carService.getAllCars();
    }

    @GetMapping("{id}")
    public ResponseEntity<Car> getCarById(@PathVariable("id") long carId) {
        return new ResponseEntity<>(carService.getCarById(carId), HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<Car> UpdateCar(@PathVariable("id") long id, @RequestBody Car car) {
        return new ResponseEntity<>(carService.updateCar(car,id), HttpStatus.OK);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteCar(@PathVariable("id")long id){
        carService.deleteCarById(id);
        return  new ResponseEntity<>("Car is deleted!",HttpStatus.OK);
    }

    @GetMapping("/plateNumber")
    public ResponseEntity<Car> getCarByPlateNumber(@RequestParam String plateNumber){
        return new ResponseEntity<>(carService.getCarByPlateNumber(plateNumber),HttpStatus.OK);
    }

}
