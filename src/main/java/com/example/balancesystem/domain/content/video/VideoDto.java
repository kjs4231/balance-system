package com.example.balancesystem.domain.content.video;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VideoDto {

    private String title;
    private int duration;
    private Long ownerId;

    public VideoDto(String title, int duration, Long ownerId) {
        this.title = title;
        this.duration = duration;
        this.ownerId = ownerId;
    }

}