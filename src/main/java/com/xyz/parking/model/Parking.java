package com.xyz.parking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "PARKING")
public class Parking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "SLOT_ID")
    private String slotId;
    @Column(name = "SLOT_SIZE")
    private Integer slotSize;
    @Column(name = "CAR_ID", nullable = true)
    private Long carId;
    @Enumerated(EnumType.STRING)
    @Column(name = "CAR_SIZE")
    private Sizes carSize;
    @Column(name = "TOA")
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private LocalDateTime toa;
    @Column(name = "TOE")
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private LocalDateTime toe;
    @Column(name= "IS_AVAILABLE")
    private int isAvailable;
    @Column(name= "PLATE_NUMBER")
    private String plateNumber;
}
