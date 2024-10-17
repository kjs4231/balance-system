package com.example.balancesystem.domain.ad;


import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "Ad")
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adId;

    private int viewCount = 0;


}