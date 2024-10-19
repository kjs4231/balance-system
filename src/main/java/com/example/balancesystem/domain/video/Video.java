package com.example.balancesystem.domain.video;

import com.example.balancesystem.domain.ad.Ad;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
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

    @ManyToMany(mappedBy = "videos")
    private List<Ad> ads;

    public void increaseViewCount() {
        this.viewCount++;
    }

    public List<Ad> getAds() {
        return ads;
    }
    public void setTitle(String title) {
        this.title = title;
    }

}