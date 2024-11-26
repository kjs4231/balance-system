package com.example.balancesystem.domain.content.video;

import lombok.Builder;
import lombok.Getter;

@Getter
public class VideoDto {

    private final String title;
    private final int duration;
    private final Long ownerId;

    @Builder
    public VideoDto(String title, int duration, Long ownerId) {
        this.title = title;
        this.duration = duration;
        this.ownerId = ownerId;
    }

    public VideoDto() {
        this.title = null;
        this.duration = 0;
        this.ownerId = null;
    }
}
