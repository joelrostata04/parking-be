package com.xyz.parking.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ENTRYDISTANCE")
public class EntryDistance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ENTRY")
    private String entry;
    @Column(name = "SLOT_ID")
    private String slotId;
    @Column(name = "DISTANCE_A")
    private Integer distanceA;
    @Column(name = "DISTANCE_B")
    private Integer distanceB;
    @Column(name = "DISTANCE_C")
    private Integer distanceC;
    @Column(name = "IS_AVAILABLE")
    private int isAvailable;
}
