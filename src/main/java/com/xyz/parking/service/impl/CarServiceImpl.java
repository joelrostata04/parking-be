package com.xyz.parking.service.impl;

import com.xyz.parking.exception.ResourceNotFoundException;
import com.xyz.parking.model.Car;
import com.xyz.parking.repository.CarRepository;
import com.xyz.parking.service.CarService;
import com.xyz.parking.service.ParkingService;
import com.xyz.parking.shared.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;
    private ParkingService parkingService;
    public CarServiceImpl(CarRepository carRepository, ParkingService parkingService) {
        super();
        this.carRepository = carRepository;
        this.parkingService = parkingService;
    }

    @Override
    public Car saveCar(Car car, LocalDateTime toa) {
        LocalDateTime lt = toa != null ? toa : LocalDateTime.now();
        car.setToa(car.getToa() == null ? lt : car.getToa());
        log.info("SAVING NEW CAR: {}",car);
        carRepository.save(car);
        parkingService.autoAssignParking(car);
        // return carRepository.save(car);
        return car;
    }

    @Override
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    @Override
    public Car getCarById(long id) {
        return carRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Car","Id", id));
    }

    @Override
    public Car getCarByPlateNumber(String plateNumber) {
        Car car = carRepository.findByPlateNumber(plateNumber);
        if (!car.getPlateNumber().equals(plateNumber)){
            throw new ResourceNotFoundException("Plate","Number",plateNumber);
        } else {
            return carRepository.findByPlateNumber(plateNumber);
        }
    }

    @Override
    public Car updateCar(Car car, long id) {
        Car parkingCar = carRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Car","Id", id));
        parkingCar.setIsParked(car.getIsParked());
        carRepository.save(parkingCar);
        return parkingCar;
    }

    @Override
    public Car updateCarRepark(Car car, LocalDateTime toa) {
        int TRUE = AppConstants.TRUE_OR_YES;
        LocalDateTime now = toa != null ? toa : LocalDateTime.now();
        Car parkingCar = carRepository.findByPlateNumberParking(car.getPlateNumber()).get(0);
        if (checkIfWithinHourOfParking(parkingCar,now)){
            parkingCar.setIsContinue(1);
            parkingCar.setToa(parkingCar.getToa());
            log.info("SAVING REPARKING CAR: {}",parkingCar);
            parkingService.autoAssignParking(car);
            return carRepository.save(parkingCar);
        } else {
            Car newCar = new Car();
            newCar.setPlateNumber(car.getPlateNumber());
            newCar.setCarSize(car.getCarSize());
            newCar.setEntry(car.getEntry());
            newCar.setIsParked(TRUE);
            return saveCar(newCar,now);
        }
    }

    @Override
    public void deleteCarById(long id) {
        carRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Car","Id", id));
        carRepository.deleteById(id);
    }

    @Override
    public Boolean checkIfRepark(Car car) {
        Boolean repark;
        List<Car> findCar = carRepository.findByPlateNumberParking(car.getPlateNumber());
        if(findCar.isEmpty()){
            repark = Boolean.FALSE;
        } else {
            repark = Boolean.TRUE;
        }
        return repark;
    }

    @Override
    public Boolean checkIfWithinHourOfParking(Car car,LocalDateTime now) {
        LocalDateTime existingToe = car.getToe();
        Duration duration = Duration.between(existingToe,now);
        return duration.toMinutes() < AppConstants.HOUR;
    }
}
