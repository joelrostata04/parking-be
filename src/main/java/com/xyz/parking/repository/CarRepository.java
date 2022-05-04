package com.xyz.parking.repository;

import com.xyz.parking.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {

    @Query("select c from Car c where c.plateNumber =?1")
    Car findByPlateNumber(String plateNumber);

    @Query("select c from Car c where c.plateNumber =?1 order by c.id desc ")
    List<Car> findByPlateNumberParking(String plateNumber);
}

