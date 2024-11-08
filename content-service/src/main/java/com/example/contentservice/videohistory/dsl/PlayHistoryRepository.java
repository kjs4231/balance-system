package com.example.contentservice.videohistory.dsl;

import com.example.contentservice.videohistory.PlayHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long>, CustomPlayHistoryRepository {
}
