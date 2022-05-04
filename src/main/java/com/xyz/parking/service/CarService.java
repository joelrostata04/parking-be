package com.xyz.parking.service;

import com.xyz.parking.model.Car;
import com.xyz.parking.model.Parking;
import org.apache.tomcat.jni.Local;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface CarService {
    Car saveCar(Car car, LocalDateTime toa);
    List<Car> getAllCars();
    Car getCarById(long  id);
    Car getCarByPlateNumber(String plateNumber);
    Car updateCar(Car car, long id);
    Car updateCarRepark(Car car, LocalDateTime now);
    void deleteCarById(long id);
    Boolean checkIfRepark(Car car);
    Boolean checkIfWithinHourOfParking(Car car, LocalDateTime now);
}
