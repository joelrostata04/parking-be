package com.xyz.parking.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@Entity
@Table(name = "CARS")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Column(name = "ENTRY",nullable = false)
    private Entry entry;
    @Enumerated(EnumType.STRING)
    @Column(name = "CAR_SIZE",nullable = false)
    private Sizes carSize;
    @Column(name = "PLATE_NUMBER",nullable = false)
    private String plateNumber;
    @Column(name = "TOA")
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private LocalDateTime toa;
    @Column(name = "TOE")
    @JsonFormat(pattern = "YYYY-MM-dd HH:mm:ss")
    private LocalDateTime toe;
    @Column(name = "isParked")
    private int isParked;
    @Column(name = "PRICE_TO_PAY")
    private Integer priceToPay;
    @Column(name = "IS_CONTINUE")
    private int isContinue;

}
