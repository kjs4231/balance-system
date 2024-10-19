package com.example.balancesystem.domain.ad;

import com.example.balancesystem.domain.video.Video;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Table(name = "Ad")
@NoArgsConstructor
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adId;

    private int viewCount = 0;

    private int triggerTime; // 광고 재생 시작 시점

    @ManyToMany
    @JoinTable(
            name = "video_ad",
            joinColumns = @JoinColumn(name = "ad_id"),
            inverseJoinColumns = @JoinColumn(name = "video_id")
    )
    private List<Video> videos;

    public void increaseViewCount() {
        this.viewCount++;
    }
}
