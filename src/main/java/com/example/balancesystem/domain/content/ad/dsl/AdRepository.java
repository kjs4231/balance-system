package com.example.balancesystem.domain.content.ad.dsl;

import com.example.balancesystem.domain.content.ad.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface AdRepository extends JpaRepository<Ad, Long>, QuerydslPredicateExecutor<Ad>, CustomAdRepository {

}
