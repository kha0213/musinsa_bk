-- 카테고리 테이블 스키마 정의 (최종 간소화 버전)
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    parent_id BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIME,
    updated_at TIMESTAMP DEFAULT CURRENT_TIME,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    code VARCHAR(20) UNIQUE,
    store_code VARCHAR(50),
    store_title VARCHAR(100),
    group_title VARCHAR(100),
    display_order INTEGER NOT NULL DEFAULT 0,
    gender_filter VARCHAR(10) NOT NULL DEFAULT 'A',  -- A: 전체, M: 남성, F: 여성
    
    FOREIGN KEY (parent_id) REFERENCES categories(id)
);

-- 인덱스 생성 (성능 최적화)
CREATE INDEX IF NOT EXISTS idx_categories_parent_id ON categories(parent_id);
CREATE INDEX IF NOT EXISTS idx_categories_is_active ON categories(is_active);
CREATE INDEX IF NOT EXISTS idx_categories_name ON categories(name);
CREATE INDEX IF NOT EXISTS idx_categories_code ON categories(code);
CREATE INDEX IF NOT EXISTS idx_categories_gender_filter ON categories(gender_filter);
CREATE INDEX IF NOT EXISTS idx_categories_display_order ON categories(display_order);
