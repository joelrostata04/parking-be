package com.xyz.parking.repository;

import com.xyz.parking.model.Parking;
import com.xyz.parking.model.Sizes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface ParkingRepository extends JpaRepository<Parking, Long> {

    @Query("SELECT p from Parking p where p.slotId =?1")
    Parking findBySlotId(String parkingId);

    @Query("SELECT e from Parking e where e.isAvailable =1")
    List<Parking> findAllAvailable();

    @Query("SELECT p from Parking p where p.isAvailable =1 AND p.slotSize =?1")
    List<Parking> findAllAvailableForCarSize(Sizes carSize);

    @Query("select p from Parking p where p.isAvailable =1 AND " +
            "(p.slotSize =?1 or p.slotSize =?2 or p.slotSize =?3)")
    List<Parking> findAllAvailableForSmall(int small, int mid, int large);

    @Query("select p from Parking p where p.isAvailable =1 AND " +
            "(p.slotSize =?1 or p.slotSize =?2)")
    List<Parking> findAllAvailableForMid(int mid, int large);

    @Query("select p from Parking p where p.isAvailable =1 AND " +
            "p.slotSize =?1")
    List<Parking> findAllAvailableForLarge(int num);

    @Query(value = "SELECT p.* FROM Parking p " +
            "inner join EntryDistance e on p.slot_id = e.slot_id " +
            "where p.is_available = 1 and p.slot_size >=0 " +
            "order by CASE WHEN 'distance_a' =?1 THEN e.distance_a " +
            " WHEN ?1 = 'distance_b' THEN e.distance_b ELSE e.distance_c END", nativeQuery = true)
    List<Parking> fetchSlotAvailableforSmall(String entry);

    @Query(value = "SELECT p.* FROM Parking p " +
            "inner join EntryDistance e on p.slot_id = e.slot_id " +
            "where p.is_available = 1 and p.slot_size >=1 " +
            "order by CASE WHEN 'distance_a' =?1 THEN e.distance_a " +
            " WHEN ?1 = 'distance_b' THEN e.distance_b ELSE e.distance_c END", nativeQuery = true)
    List<Parking> fetchSlotAvailableforMid(String entry);

    @Query(value = "SELECT p.* FROM Parking p " +
            "inner join EntryDistance e on p.slot_id = e.slot_id " +
            "where p.is_available = 1 and p.slot_size >=2 " +
            "order by CASE WHEN 'distance_a' =?1 THEN e.distance_a " +
            " WHEN ?1 = 'distance_b' THEN e.distance_b ELSE e.distance_c END", nativeQuery = true)
    List<Parking> fetchSlotAvailableforLarge(String entry);

}
