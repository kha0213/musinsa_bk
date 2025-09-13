package com.yl.musinsa2.integration;

import com.yl.musinsa2.service.CategoryCacheInitializer;
import com.yl.musinsa2.service.CategoryCacheService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@TestConfiguration
public class TestConfig {

    @MockitoBean
    private CategoryCacheService categoryCache;

    @MockitoBean
    private CategoryCacheInitializer cacheInitializer;
}
