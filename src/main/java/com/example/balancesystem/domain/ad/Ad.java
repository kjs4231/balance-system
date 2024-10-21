package com.example.balancesystem.domain.ad;

import com.example.balancesystem.domain.video.Video;
import com.example.balancesystem.domain.videoad.VideoAd;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "Ad")
@NoArgsConstructor
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adId;

    @OneToMany(mappedBy = "ad", cascade = CascadeType.ALL)
    private List<VideoAd> videoAds = new ArrayList<>();
}
