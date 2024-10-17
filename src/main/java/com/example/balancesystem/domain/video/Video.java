package com.example.balancesystem.domain.video;

import com.example.balancesystem.domain.ad.Ad;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
@Table(name = "video")
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoId;

    private String title;

    private int viewCount = 0;

    @ManyToMany
    @JoinTable(
            name = "video_ad",
            joinColumns = @JoinColumn(name = "video_id"),
            inverseJoinColumns = @JoinColumn(name = "ad_id")
    )
    private List<Ad> ads;
}