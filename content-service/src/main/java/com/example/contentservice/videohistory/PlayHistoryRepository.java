package com.example.contentservice.videohistory;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long>, CustomPlayHistoryRepository {
}
