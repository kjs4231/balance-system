package com.example.balancesystem.domain.content.ad.dsl;

import com.example.balancesystem.domain.content.ad.Ad;

import java.util.List;

public interface CustomAdRepository {
    List<Ad> findAll();
}
