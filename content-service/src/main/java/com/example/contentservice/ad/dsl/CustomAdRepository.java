package com.example.contentservice.ad.dsl;

import com.example.contentservice.ad.Ad;

import java.util.List;

public interface CustomAdRepository {
    List<Ad> findAll();
}
