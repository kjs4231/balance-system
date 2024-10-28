package com.example.balancesystem.domain.video;

import com.example.balancesystem.domain.adhistory.AdHistory;
import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.videohistory.PlayHistory;
import com.example.balancesystem.domain.videostats.VideoStatistics;
import jakarta.persistence.*;
import lombok.Getter;

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
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;


    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<VideoStatistics> videoStatistics = new ArrayList<>();

    @OneToMany(mappedBy = "video", cascade = CascadeType.ALL)
    private List<PlayHistory> playHistories = new ArrayList<>();

    @OneToMany(mappedBy = "video")
    private List<AdHistory> adHistories;

    public void increaseViewCount() {
        this.viewCount++;
    }

    public Video() {}

    public Video(String title, int duration, User owner) {
        this.title = title;
        this.duration = duration;
        this.owner = owner;
    }
}
