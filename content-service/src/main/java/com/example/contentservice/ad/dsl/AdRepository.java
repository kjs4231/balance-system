package com.example.contentservice.ad.dsl;

import com.example.contentservice.ad.Ad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface AdRepository extends JpaRepository<Ad, Long>, QuerydslPredicateExecutor<Ad>, CustomAdRepository {

}
