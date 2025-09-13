package com.yl.musinsa2.service;

import com.yl.musinsa2.dto.CategoryDto;
import com.yl.musinsa2.entity.Category;
import com.yl.musinsa2.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class CategoryCacheInitializer {
    
    private final CategoryRepository categoryRepository;
    private final CategoryCacheService categoryCache;
    
    @EventListener(ApplicationReadyEvent.class)
    public void initializeCache() {
        log.info("카테고리 캐시 초기화 시작...");
        
        try {
            // 기존 캐시 초기화
            categoryCache.clearAllCache();
            
            // DB에서 모든 카테고리 조회
            List<Category> allCategories = categoryRepository.findAll();
            
            // 캐시에 저장
            for (Category category : allCategories) {
                CategoryDto categoryDto = CategoryDto.from(category);
                categoryCache.addCategory(categoryDto);
            }
            
            log.info("카테고리 캐시 초기화 완료 - 총 {} 개의 카테고리", allCategories.size());
            
        } catch (Exception e) {
            log.error("카테고리 캐시 초기화 실패", e);
        }
    }
    
    /**
     * 수동 캐시 초기화 (필요시 호출)
     */
    public void reinitializeCache() {
        log.info("수동 카테고리 캐시 재초기화 시작...");
        initializeCache();
    }
}
