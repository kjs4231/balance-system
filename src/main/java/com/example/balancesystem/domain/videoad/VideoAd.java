//package com.example.balancesystem.domain.videoad;
//
//import com.example.balancesystem.domain.ad.Ad;
//import com.example.balancesystem.domain.video.Video;
//import jakarta.persistence.*;
//import lombok.Getter;
//
//@Entity
//@Getter
//@Table(name = "video_ad")
//public class VideoAd {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long videoAdId;
//
//    @ManyToOne
//    @JoinColumn(name = "video_id")
//    private Video video;
//
//    @ManyToOne
//    @JoinColumn(name = "ad_id")
//    private Ad ad;
//
//    @Column(name = "view_count")
//    private Long viewCount = 0L;
//
//    public void increaseViewCount() {
//        this.viewCount++;
//    }
//    public void setVideo(Video video) {
//        this.video = video;
//        if (!video.getVideoAds().contains(this)) {
//            video.getVideoAds().add(this); // 양방향 관계 설정
//        }
//    }
//
//    public void setAd(Ad ad) {
//        this.ad = ad;
//        if (!ad.getVideoAds().contains(this)) {
//            ad.getVideoAds().add(this); // 양방향 관계 설정
//        }
//    }
//    public VideoAd(Video video, Ad ad) {
//        this.video = video;
//        this.ad = ad;
//    }
//    public VideoAd() {
//    }
//}
