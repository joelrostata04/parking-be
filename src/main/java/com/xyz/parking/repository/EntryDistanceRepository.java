package com.xyz.parking.repository;

import com.xyz.parking.model.EntryDistance;
import com.xyz.parking.model.Parking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EntryDistanceRepository extends JpaRepository<EntryDistance, Long> {

    @Query("SELECT d from EntryDistance d where d.slotId =?1")
    EntryDistance findBySlotId(String parkingId);
}
