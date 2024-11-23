package com.example.balancesystem.domain.content.video;

import com.example.balancesystem.domain.content.adhistory.AdHistory;
import com.example.balancesystem.domain.content.playhistory.PlayHistory;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "video", indexes = {
        @Index(name = "idx_video_view_ad_count", columnList = "videoId, viewCount, adViewCount")
})
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoId;
    private String title;
    private int duration;
    private int viewCount = 0;
    private int adViewCount = 0;

    private Long ownerId;

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<PlayHistory> playHistories = new ArrayList<>();

    @OneToMany(mappedBy = "video")
    private List<AdHistory> adHistories;

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseAdViewCount() {
        this.adViewCount++;
    }

    public Video() {}

    public Video(String title, int duration, Long ownerId) {
        this.title = title;
        this.duration = duration;
        this.ownerId = ownerId;
    }

    public void increaseViewCountBy(int count) {
        this.viewCount += count;
    }

    public void increaseAdViewCountBy(int count) {
        this.adViewCount += count;
    }

}
