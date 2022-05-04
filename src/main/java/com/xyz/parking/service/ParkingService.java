package com.xyz.parking.service;

import com.xyz.parking.model.Car;
import com.xyz.parking.model.Entry;
import com.xyz.parking.model.Parking;
import com.xyz.parking.model.Sizes;

import java.time.LocalDateTime;
import java.util.List;

public interface ParkingService {
    List<Parking> getAllParking();
    List<Parking> getAllAvailableParking();
    List<Parking> getAllAvailableParkingForCarSize(Sizes carSize);
    List<Parking> getAllAvailableParkingForSmall(int small, int mid, int large);
    List<Parking> getAllAvailableParkingForMedium(int mid, int large);
    List<Parking> getAllAvailableParkingForLarge(int size);
    Parking getParkingSlotBySlotId(String parkingId);
    Parking parkSlot(String plateNumber, String parkingId);
    void autoAssignParking(Car car);
    List<Parking> sortedParking(List<Parking> parkings, Entry entry);
    void editTimeOfParking(String PlateNumber,String slotId, LocalDateTime toa);
    List<Parking> availableSlotForSmallByDistance (String entry);
    List<Parking> availableSlotForMidByDistance (String entry);
    List<Parking> availableSlotForLargeByDistance (String entry);
}
