-- MUSINSA 카테고리 초기 데이터
-- 데이터가 없을 때만 실행되도록 조건부 삽입
-- ID는 자동 생성되도록 하여 JPA와 충돌 방지

-- 대분류 카테고리 - ID 자동 생성
INSERT INTO categories (name, description, parent_id, is_active)
SELECT '남성', '남성 의류 및 잡화', NULL, TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '남성' AND parent_id IS NULL);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '여성', '여성 의류 및 잡화', NULL, TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '여성' AND parent_id IS NULL);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '키즈', '아동 의류 및 잡화', NULL, TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '키즈' AND parent_id IS NULL);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '신발', '모든 종류의 신발', NULL, TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '신발' AND parent_id IS NULL);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '액세서리', '가방, 지갑, 시계 등', NULL, TRUE
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE name = '액세서리' AND parent_id IS NULL);

-- 남성 중분류 - 부모 ID를 동적으로 찾아서 삽입
INSERT INTO categories (name, description, parent_id, is_active)
SELECT '상의', '남성 상의', p.id, TRUE
FROM categories p
WHERE p.name = '남성' AND p.parent_id IS NULL
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '상의' AND c.parent_id = p.id
);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '하의', '남성 하의', p.id, TRUE
FROM categories p
WHERE p.name = '남성' AND p.parent_id IS NULL
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '하의' AND c.parent_id = p.id
);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '아우터', '남성 아우터', p.id, TRUE
FROM categories p
WHERE p.name = '남성' AND p.parent_id IS NULL
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '아우터' AND c.parent_id = p.id
);

-- 남성 상의 소분류
INSERT INTO categories (name, description, parent_id, is_active)
SELECT '티셔츠', '남성 티셔츠', p.id, TRUE
FROM categories p
WHERE p.name = '상의' AND p.parent_id = (SELECT id FROM categories WHERE name = '남성' AND parent_id IS NULL)
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '티셔츠' AND c.parent_id = p.id
);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '셔츠', '남성 셔츠', p.id, TRUE
FROM categories p
WHERE p.name = '상의' AND p.parent_id = (SELECT id FROM categories WHERE name = '남성' AND parent_id IS NULL)
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '셔츠' AND c.parent_id = p.id
);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '니트/스웨터', '남성 니트/스웨터', p.id, TRUE
FROM categories p
WHERE p.name = '상의' AND p.parent_id = (SELECT id FROM categories WHERE name = '남성' AND parent_id IS NULL)
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '니트/스웨터' AND c.parent_id = p.id
);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '후드티', '남성 후드티', p.id, TRUE
FROM categories p
WHERE p.name = '상의' AND p.parent_id = (SELECT id FROM categories WHERE name = '남성' AND parent_id IS NULL)
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '후드티' AND c.parent_id = p.id
);

-- 남성 하의 소분류
INSERT INTO categories (name, description, parent_id, is_active)
SELECT '진', '남성 청바지', p.id, TRUE
FROM categories p
WHERE p.name = '하의' AND p.parent_id = (SELECT id FROM categories WHERE name = '남성' AND parent_id IS NULL)
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '진' AND c.parent_id = p.id
);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '팬츠', '남성 팬츠', p.id, TRUE
FROM categories p
WHERE p.name = '하의' AND p.parent_id = (SELECT id FROM categories WHERE name = '남성' AND parent_id IS NULL)
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '팬츠' AND c.parent_id = p.id
);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '반바지', '남성 반바지', p.id, TRUE
FROM categories p
WHERE p.name = '하의' AND p.parent_id = (SELECT id FROM categories WHERE name = '남성' AND parent_id IS NULL)
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '반바지' AND c.parent_id = p.id
);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '조거팬츠', '남성 조거팬츠', p.id, TRUE
FROM categories p
WHERE p.name = '하의' AND p.parent_id = (SELECT id FROM categories WHERE name = '남성' AND parent_id IS NULL)
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '조거팬츠' AND c.parent_id = p.id
);

-- 여성 중분류
INSERT INTO categories (name, description, parent_id, is_active)
SELECT '상의', '여성 상의', p.id, TRUE
FROM categories p
WHERE p.name = '여성' AND p.parent_id IS NULL
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '상의' AND c.parent_id = p.id
);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '하의', '여성 하의', p.id, TRUE
FROM categories p
WHERE p.name = '여성' AND p.parent_id IS NULL
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '하의' AND c.parent_id = p.id
);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '원피스', '여성 원피스', p.id, TRUE
FROM categories p
WHERE p.name = '여성' AND p.parent_id IS NULL
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '원피스' AND c.parent_id = p.id
);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '아우터', '여성 아우터', p.id, TRUE
FROM categories p
WHERE p.name = '여성' AND p.parent_id IS NULL
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '아우터' AND c.parent_id = p.id
);

-- 여성 상의 소분류
INSERT INTO categories (name, description, parent_id, is_active)
SELECT '티셔츠', '여성 티셔츠', p.id, TRUE
FROM categories p
WHERE p.name = '상의' AND p.parent_id = (SELECT id FROM categories WHERE name = '여성' AND parent_id IS NULL)
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '티셔츠' AND c.parent_id = p.id
);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '블라우스', '여성 블라우스', p.id, TRUE
FROM categories p
WHERE p.name = '상의' AND p.parent_id = (SELECT id FROM categories WHERE name = '여성' AND parent_id IS NULL)
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '블라우스' AND c.parent_id = p.id
);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '니트/가디건', '여성 니트/가디건', p.id, TRUE
FROM categories p
WHERE p.name = '상의' AND p.parent_id = (SELECT id FROM categories WHERE name = '여성' AND parent_id IS NULL)
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '니트/가디건' AND c.parent_id = p.id
);

-- 신발 중분류
INSERT INTO categories (name, description, parent_id, is_active)
SELECT '스니커즈', '운동화/스니커즈', p.id, TRUE
FROM categories p
WHERE p.name = '신발' AND p.parent_id IS NULL
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '스니커즈' AND c.parent_id = p.id
);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '구두', '정장구두/구두', p.id, TRUE
FROM categories p
WHERE p.name = '신발' AND p.parent_id IS NULL
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '구두' AND c.parent_id = p.id
);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '부츠', '부츠', p.id, TRUE
FROM categories p
WHERE p.name = '신발' AND p.parent_id IS NULL
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '부츠' AND c.parent_id = p.id
);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '샌들', '샌들/슬리퍼', p.id, TRUE
FROM categories p
WHERE p.name = '신발' AND p.parent_id IS NULL
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '샌들' AND c.parent_id = p.id
);

-- 액세서리 중분류
INSERT INTO categories (name, description, parent_id, is_active)
SELECT '가방', '백팩, 토트백, 크로스백 등', p.id, TRUE
FROM categories p
WHERE p.name = '액세서리' AND p.parent_id IS NULL
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '가방' AND c.parent_id = p.id
);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '시계', '손목시계, 스마트워치', p.id, TRUE
FROM categories p
WHERE p.name = '액세서리' AND p.parent_id IS NULL
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '시계' AND c.parent_id = p.id
);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '지갑', '지갑, 카드지갑', p.id, TRUE
FROM categories p
WHERE p.name = '액세서리' AND p.parent_id IS NULL
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '지갑' AND c.parent_id = p.id
);

INSERT INTO categories (name, description, parent_id, is_active)
SELECT '주얼리', '목걸이, 팔찌, 반지 등', p.id, TRUE
FROM categories p
WHERE p.name = '액세서리' AND p.parent_id IS NULL
AND NOT EXISTS (
    SELECT 1 FROM categories c 
    WHERE c.name = '주얼리' AND c.parent_id = p.id
);
