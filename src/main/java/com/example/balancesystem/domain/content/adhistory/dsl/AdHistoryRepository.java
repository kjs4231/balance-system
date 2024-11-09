package com.example.balancesystem.domain.content.adhistory.dsl;

import com.example.balancesystem.domain.content.adhistory.AdHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdHistoryRepository extends JpaRepository<AdHistory, Long>, CustomAdHistoryRepository {
}
