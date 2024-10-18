package com.example.balancesystem.domain.ad;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "Ad")
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adId;

    private int viewCount = 0;

    private int triggerTime; // 광고 재생 시작 시점. 이걸 가지고 마지막 시청 시간이 이거 이상일 경우 카운트 하면 됨.

    private boolean viewed = false; // 광고가 시청되었는지 여부

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void markAsViewed() {
        this.viewed = true;
    }

}
