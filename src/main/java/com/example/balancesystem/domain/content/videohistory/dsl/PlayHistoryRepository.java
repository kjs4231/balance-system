package com.example.balancesystem.domain.content.videohistory.dsl;

import com.example.balancesystem.domain.content.videohistory.PlayHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long>, CustomPlayHistoryRepository {
}
