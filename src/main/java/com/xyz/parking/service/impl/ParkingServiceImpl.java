package com.xyz.parking.service.impl;

import com.xyz.parking.exception.ResourceNotFoundException;
import com.xyz.parking.model.*;
import com.xyz.parking.repository.CarRepository;
import com.xyz.parking.repository.EntryDistanceRepository;
import com.xyz.parking.repository.ParkingRepository;
import com.xyz.parking.service.ParkingService;
import com.xyz.parking.shared.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ParkingServiceImpl implements ParkingService {

    private final ParkingRepository parkingRepository;
    private final CarRepository carRepository;
    private final EntryDistanceRepository entryDistanceRepository;

    public ParkingServiceImpl(ParkingRepository parkingRepository, CarRepository carRepository,
                              EntryDistanceRepository entryDistanceRepository) {
        super();
        this.carRepository = carRepository;
        this.parkingRepository = parkingRepository;
        this.entryDistanceRepository = entryDistanceRepository;
    }


    @Override
    public List<Parking> getAllParking() {
        return parkingRepository.findAll();
    }

    @Override
    public List<Parking> getAllAvailableParking() {
        return parkingRepository.findAllAvailable();
    }

    @Override
    public List<Parking> getAllAvailableParkingForCarSize(Sizes carSize) {
        return parkingRepository.findAllAvailableForCarSize(carSize);
    }

    @Override
    public List<Parking> getAllAvailableParkingForSmall(int small,int mid, int large) {
        log.info("SLOT SIZES, {}{}{}",small,mid,large);
        return parkingRepository.findAllAvailableForSmall(small,mid,large);
    }

    @Override
    public List<Parking> getAllAvailableParkingForMedium(int mid, int large) {
        return parkingRepository.findAllAvailableForMid(mid, large);
    }

    @Override
    public List<Parking> getAllAvailableParkingForLarge(int number) {
        return parkingRepository.findAllAvailableForLarge(number);
    }

    @Override
    public Parking getParkingSlotBySlotId(String parkingId) {
        return parkingRepository.findBySlotId(parkingId);
    }

    @Override
    public Parking parkSlot(String plateNumber, String parkingId) {
        int yes = AppConstants.TRUE_OR_YES;
        Parking availableSlot = parkingRepository.findBySlotId(parkingId);
        if (!availableSlot.getSlotId().equals(parkingId)){
            throw new ResourceNotFoundException("Parking","id",parkingId);
        } else {
            Car parkingCar = carRepository.findByPlateNumberParking(plateNumber).get(0);
            log.info("PARKING CAR: {}",parkingCar);
            if (!parkingCar.getPlateNumber().equals(plateNumber) && parkingCar.getIsParked() != yes){
                throw new ResourceNotFoundException("Plate","Number",plateNumber);
            } else {
                if (availableSlot.getIsAvailable() == yes){
                    availableSlot.setIsAvailable(AppConstants.FALSE_OR_NO);
                    availableSlot.setCarId(parkingCar.getId());
                    availableSlot.setCarSize(parkingCar.getCarSize());
                    availableSlot.setToa(parkingCar.getToa());
                    availableSlot.setPlateNumber(parkingCar.getPlateNumber());
                    parkingCar.setIsParked(yes);
                    carRepository.save(parkingCar);
                    parkingRepository.save(availableSlot);
                    return availableSlot;
                }
            }
        }
        return availableSlot;
    }

    @Override
    public void autoAssignParking(Car car) {
        synchronized (this){
            int small = AppConstants.SMALL_SLOT_SLIZE;
            int mid = AppConstants.MID_SLOT_SLIZE;
            int large = AppConstants.LARGE_SLOT_SLIZE;
            String entry = car.getEntry() == Entry.A ? AppConstants.DISTANCE_A
                    : car.getEntry() == Entry.B ? AppConstants.DISTANCE_B : AppConstants.DISTANCE_C;
            List <Parking> availableParking;
            Parking assignedParkingSlot;
            boolean isSmall = car.getCarSize().equals(Sizes.SMALL);
            boolean isMedium = car.getCarSize().equals(Sizes.MEDIUM);
            availableParking = isSmall ? this.availableSlotForSmallByDistance(entry) : isMedium ?
                    this.availableSlotForMidByDistance(entry) :
                    this.availableSlotForLargeByDistance(entry);
            log.info("distance {}",availableParking);
            log.info("nearest {}",availableParking.get(0));
            assignedParkingSlot = availableParking.get(0);
            this.parkSlot(car.getPlateNumber(), assignedParkingSlot.getSlotId());
            log.info("ASSIGNED SLOT, {}",assignedParkingSlot);
        }
    }

    @Override
    public List<Parking> sortedParking(List<Parking> parkings, Entry entry) {
        List<Parking> sortedSlots = new ArrayList<>();
        String secondEntry = entry == Entry.B ? Entry.C.toString() : entry == Entry.C ? Entry.B.toString() : Entry.A.toString();
        String thirdEntry = secondEntry.equals(Entry.C.toString()) ? Entry.B.toString() : Entry.A.toString();
        if (entry == Entry.A){
            sortedSlots = parkings;
        } else {
            for (Parking park:  parkings) {
                if (park.getSlotId().contains(entry.toString())){
                    sortedSlots.add(park);
                }
            }
            for (Parking secondOption:  parkings) {
                if (secondOption.getSlotId().contains(secondEntry)){
                    sortedSlots.add(secondOption);
                }
            }
            for (Parking thirdOption:  parkings) {
                if (thirdOption.getSlotId().contains(thirdEntry)){
                    sortedSlots.add(thirdOption);
                }
            }
        }
        log.info("SORTED SLOTS: {}",sortedSlots);
        return sortedSlots;
    }

    @Override
    public void editTimeOfParking(String plateNumber,String slotId, LocalDateTime toa) {
        Car parkingCar = carRepository.findByPlateNumberParking(plateNumber).get(0);
        Parking parkedSlot = parkingRepository.findBySlotId(slotId);
        parkingCar.setToa(toa);
        parkedSlot.setToa(toa);
        carRepository.save(parkingCar);
        parkingRepository.save(parkedSlot);
    }

    @Override
    public List<Parking> availableSlotForSmallByDistance(String entry) {
        return parkingRepository.fetchSlotAvailableforSmall(entry);
    }

    @Override
    public List<Parking> availableSlotForMidByDistance(String entry) {
        return parkingRepository.fetchSlotAvailableforMid(entry);
    }

    @Override
    public List<Parking> availableSlotForLargeByDistance(String entry) {
        return parkingRepository.fetchSlotAvailableforLarge(entry);
    }
}
