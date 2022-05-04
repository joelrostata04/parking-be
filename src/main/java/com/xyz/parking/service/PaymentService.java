package com.xyz.parking.service;

import com.xyz.parking.model.Car;

import java.time.LocalDateTime;

public interface PaymentService {
    Car exitCar(String plateNumber, String parkingId, LocalDateTime exit);
}
