package com.example.contentservice.adhistory.dsl;

import com.example.contentservice.adhistory.AdHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdHistoryRepository extends JpaRepository<AdHistory, Long>, CustomAdHistoryRepository {
}
