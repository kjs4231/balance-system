package com.example.balancesystem.domain.adhistory;

import com.example.balancesystem.domain.ad.Ad;
import com.example.balancesystem.domain.user.User;
import com.example.balancesystem.domain.video.Video;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ad_history")
public class AdHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adHistoryId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "ad_id", nullable = false)
    private Ad ad;

    @ManyToOne
    @JoinColumn(name = "video_id", nullable = false)
    private Video video;

    @Column(nullable = false)
    private LocalDate viewDate;

    public AdHistory(User user, Ad ad, Video video, LocalDate viewDate) {
        this.user = user;
        this.ad = ad;
        this.video = video;
        this.viewDate = viewDate;
    }
}
