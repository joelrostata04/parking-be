package com.xyz.parking.service.impl;

import com.xyz.parking.exception.ResourceNotFoundException;
import com.xyz.parking.model.Car;
import com.xyz.parking.model.Parking;
import com.xyz.parking.model.Sizes;
import com.xyz.parking.repository.CarRepository;
import com.xyz.parking.repository.ParkingRepository;
import com.xyz.parking.service.CarService;
import com.xyz.parking.service.PaymentService;
import com.xyz.parking.shared.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.jni.Local;
import org.hibernate.engine.jdbc.Size;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
public class PaymentSericeImpl implements PaymentService {

    private final CarRepository carRepository;
    private final ParkingRepository parkingRepository;
    private final CarService carService;
    int TRUE = AppConstants.TRUE_OR_YES;
    int FALSE = AppConstants.FALSE_OR_NO;
    int hour = AppConstants.HOUR;

    public PaymentSericeImpl(CarRepository carRepository, ParkingRepository parkingRepository, CarService carService) {
        this.carRepository = carRepository;
        this.parkingRepository = parkingRepository;
        this.carService = carService;
    }

    @Override
    public Car exitCar(String plateNumber, String parkingId, LocalDateTime exit) {
        LocalDateTime timeOfExit = exit != null ? exit : LocalDateTime.now();
        Car exitingCar = carRepository.findByPlateNumberParking(plateNumber).get(0);
        Parking parkedSlot = parkingRepository.findBySlotId(parkingId);
        if (exitingCar.getIsParked() != 1 || !exitingCar.getPlateNumber().equals(plateNumber)){
            throw new ResourceNotFoundException("Car","Id",exitingCar.getId());
        } else {
            if (exitingCar.getIsContinue() == TRUE){
                Duration lastDuration = Duration.between(exitingCar.getToa(), timeOfExit);
                float allTotalHours =(float) lastDuration.toMinutes() / hour;
                Integer totalPrice = computeParkingHours((long)Math.ceil(allTotalHours)
                        ,parkedSlot.getSlotSize()) - exitingCar.getPriceToPay();
                log.info("REPARKING TOTAL PRICE: {}",totalPrice);
                exitingCar.setIsContinue(FALSE);
                exitingCar.setPriceToPay(totalPrice);
            } else {
                LocalDateTime timeOfParking = exitingCar.getToa();
                Duration duration = Duration.between(timeOfParking, timeOfExit);
                float totalHours = (float) duration.toMinutes() / hour;
                log.info("TOTAL HOURS: {}",totalHours);
                log.info("HOURS ROUNDED: {}",Math.ceil(totalHours));
                exitingCar.setPriceToPay(computeParkingHours((long)Math.ceil(totalHours), parkedSlot.getSlotSize()));
                log.info("Price to Pay : {}",computeParkingHours((long)Math.ceil(totalHours),
                        parkedSlot.getSlotSize()));
            }
            exitingCar.setIsParked(FALSE);
            exitingCar.setToe(timeOfExit);
            parkedSlot.setToe(timeOfExit);
            parkedSlot.setIsAvailable(TRUE);
            parkedSlot.setToa(null);
            parkedSlot.setCarSize(null);
            parkedSlot.setCarId(null);
            parkedSlot.setPlateNumber(null);
            carRepository.save(exitingCar);
            parkingRepository.save(parkedSlot);
        }
        return exitingCar;
    }

    private Integer computeParkingHours(long hours, Integer slotSize){
        Integer firstThreeHoursPrice = AppConstants.FIRST_THREE_HOURS_PRICE;
        Integer full24HoursPrice = AppConstants.FULL_24_HOURS_PRICE;
        int full24Hours = AppConstants.FULL_24_HOURS;
        int firstThreeHours = AppConstants.FIRST_THREE_HOURS;
        int total = 0;
        if (hours <= firstThreeHours){
            total += firstThreeHoursPrice;
        } else if (hours < full24Hours){
            Integer sTime = (int) hours-firstThreeHours;
            total = firstThreeHoursPrice + succeedingHours(sTime,slotSize);
        } else {
            boolean moreThanTwo24hours = hours >= (full24Hours * 2L);
            if (moreThanTwo24hours){
                int c24Hours = (int) hours / full24Hours;
                int cTotalHours = full24Hours * c24Hours;
                Integer m24Hours = (int) hours >= cTotalHours ? (int) hours - cTotalHours : 0;
                total = hours  == (long) full24Hours * c24Hours ? (full24HoursPrice * c24Hours) :
                        ((full24HoursPrice * c24Hours) + succeedingHours(m24Hours,slotSize));
                log.info("CHOURS: {}, {}, {}",c24Hours,m24Hours,total);
            } else {
                Integer isS24Hours = hours > full24Hours ? (int) hours-full24Hours:0;
                total = hours == full24Hours ? full24HoursPrice : (full24HoursPrice + succeedingHours(isS24Hours,slotSize));
            }
        }
        return total;
    }

    public Integer succeedingHours(Integer sTime,Integer slotSize){
        int small = AppConstants.SMALL_SLOT_SLIZE;
        int mid = AppConstants.MID_SLOT_SLIZE;
        int large = AppConstants.LARGE_SLOT_SLIZE;
        Integer succeedingSmallHours = AppConstants.SMALL_SUCCEEDING_HOURS;
        Integer succeedingMidHours = AppConstants.MID_SUCCEEDING_HOURS;
        Integer succeedingLargeHours = AppConstants.LARGE_SUCCEEDING_HOURS;
        int costOfSucceedingTime = 0;
        costOfSucceedingTime = sTime*(slotSize == small? succeedingSmallHours: slotSize == mid? succeedingMidHours:succeedingLargeHours);
        return costOfSucceedingTime;
    }
}
