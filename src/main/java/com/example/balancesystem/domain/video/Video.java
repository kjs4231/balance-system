package com.example.balancesystem.domain.video;

import com.example.balancesystem.domain.ad.Ad;
import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.videoad.VideoAd;
import com.example.balancesystem.domain.videostats.VideoStatistics;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "video")
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoId;

    private String title;

    private int duration;

    private int viewCount = 0;

//    private boolean isPublic = true; // 실제로 사용하지는 않을거임.

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<VideoStatistics> videoStatistics = new ArrayList<>();


    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<VideoAd> videoAds = new ArrayList<>();

    public void increaseViewCount() {
        this.viewCount++;
    }
    public Video() {}
    public List<Ad> getAds() {
        List<Ad> ads = new ArrayList<>();
        for (VideoAd videoAd : videoAds) {
            ads.add(videoAd.getAd());
        }
        return ads;
    }
    public Video(String title, int duration, User owner) {
        this.title = title;
        this.duration = duration;
        this.owner = owner;
    }

}