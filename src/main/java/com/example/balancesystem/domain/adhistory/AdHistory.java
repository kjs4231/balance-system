package com.example.balancesystem.domain.adhistory;

import com.example.balancesystem.domain.ad.Ad;
import com.example.balancesystem.domain.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ad_history")
public class AdHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "ad_id")
    private Ad ad;

    private boolean viewed = false;

    @Column(nullable = false)
    private LocalDateTime viewDate;

    public AdHistory(User user, Ad ad, LocalDateTime viewDate) {
        this.user = user;
        this.ad = ad;
        this.viewDate = viewDate;
    }

    public void markAsViewed() {
        this.viewed = true;
    }
}
