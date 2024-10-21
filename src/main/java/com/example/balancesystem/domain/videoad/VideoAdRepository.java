package com.example.balancesystem.domain.videoad;

import com.example.balancesystem.domain.ad.Ad;
import com.example.balancesystem.domain.video.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface VideoAdRepository extends JpaRepository<VideoAd, Long> {

    Optional<VideoAd> findByVideoAndAd(Video video, Ad ad);
}
