package com.example.balancesystem.domain.adhistory;

import com.example.balancesystem.domain.ad.Ad;
import com.example.balancesystem.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdHistoryService {

    private final AdHistoryRepository adHistoryRepository;

    @Transactional(readOnly = true)
    public boolean hasUserViewedAd(User user, Ad ad) {
        return adHistoryRepository.existsByUserAndAd(user, ad);
    }

    @Transactional
    public void saveAdHistory(User user, Ad ad) {
        AdHistory adHistory = new AdHistory(user, ad);
        adHistoryRepository.save(adHistory);
    }
}