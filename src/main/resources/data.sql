-- 대분류 카테고리 (1-5) - 데이터가 없을 때만 삽입
INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 1, '남성', '남성 의류 및 잡화', NULL, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 1);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 2, '여성', '여성 의류 및 잡화', NULL, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 2);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 3, '키즈', '아동 의류 및 잡화', NULL, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 3);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 4, '신발', '모든 종류의 신발', NULL, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 4);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 5, '액세서리', '가방, 지갑, 시계 등', NULL, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 5);

-- 남성 중분류 (6-8)
INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 6, '상의', '남성 상의', 1, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 6);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 7, '하의', '남성 하의', 1, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 7);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 8, '아우터', '남성 아우터', 1, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 8);

-- 남성 상의 소분류 (9-12)
INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 9, '티셔츠', '남성 티셔츠', 6, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 9);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 10, '셔츠', '남성 셔츠', 6, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 10);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 11, '니트/스웨터', '남성 니트/스웨터', 6, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 11);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 12, '후드티', '남성 후드티', 6, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 12);

-- 남성 하의 소분류 (13-16)
INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 13, '진', '남성 청바지', 7, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 13);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 14, '팬츠', '남성 팬츠', 7, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 14);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 15, '반바지', '남성 반바지', 7, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 15);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 16, '조거팬츠', '남성 조거팬츠', 7, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 16);

-- 여성 중분류 (17-20)
INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 17, '상의', '여성 상의', 2, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 17);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 18, '하의', '여성 하의', 2, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 18);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 19, '원피스', '여성 원피스', 2, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 19);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 20, '아우터', '여성 아우터', 2, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 20);

-- 여성 상의 소분류 (21-23)
INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 21, '티셔츠', '여성 티셔츠', 17, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 21);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 22, '블라우스', '여성 블라우스', 17, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 22);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 23, '니트/가디건', '여성 니트/가디건', 17, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 23);

-- 신발 중분류 (24-27)
INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 24, '스니커즈', '운동화/스니커즈', 4, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 24);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 25, '구두', '정장구두/구두', 4, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 25);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 26, '부츠', '부츠', 4, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 26);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 27, '샌들', '샌들/슬리퍼', 4, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 27);

-- 액세서리 중분류 (28-31)
INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 28, '가방', '백팩, 토트백, 크로스백 등', 5, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 28);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 29, '시계', '손목시계, 스마트워치', 5, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 29);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 30, '지갑', '지갑, 카드지갑', 5, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 30);

INSERT INTO categories (id, name, description, parent_id, created_at, updated_at, is_active)
SELECT 31, '주얼리', '목걸이, 팔찌, 반지 등', 5, NOW(), NOW(), TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE id = 31);