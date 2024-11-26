package com.example.balancesystem.domain.content.playhistory.dsl;

import com.example.balancesystem.domain.content.playhistory.PlayHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long>, CustomPlayHistoryRepository {
}
