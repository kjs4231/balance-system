package com.example.contentservice.ad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface AdRepository extends JpaRepository<Ad, Long>, QuerydslPredicateExecutor<Ad>, CustomAdRepository {
}
