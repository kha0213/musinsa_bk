-- MUSINSA 스타일 카테고리 초기 데이터 (최종 간소화 버전)
-- store 관련 필드들 제거됨

-- 대분류 카테고리 (무신사 메인 카테고리들)
INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '상의', '상의 카테고리', NULL, TRUE, '001', 1, 'A', ''
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE code = '001');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '아우터', '아우터 카테고리', NULL, TRUE, '002', 2, 'A', ''
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE code = '002');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '바지', '바지 카테고리', NULL, TRUE, '003', 3, 'A', ''
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE code = '003');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title, store_code)
SELECT '신발', '신발 카테고리', NULL, TRUE, '103', 4, 'A', '', 'sneaker'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE code = '103');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '가방', '가방 카테고리', NULL, TRUE, '004', 5, 'A', ''
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE code = '004');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title, store_code)
SELECT '뷰티', '뷰티 카테고리', NULL, TRUE, '104', 6, 'A', '', 'beauty'
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE code = '104');

-- 상의 하위 카테고리
INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '반소매 티셔츠', '반소매 티셔츠', p.id, TRUE, '001001', 1, 'A', ''
FROM categories p WHERE p.code = '001'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '001001');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '셔츠/블라우스', '셔츠/블라우스', p.id, TRUE, '001002', 2, 'A', ''
FROM categories p WHERE p.code = '001'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '001002');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '피케/카라 티셔츠', '피케/카라 티셔츠', p.id, TRUE, '001003', 3, 'A', ''
FROM categories p WHERE p.code = '001'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '001003');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '후드 티셔츠', '후드 티셔츠', p.id, TRUE, '001004', 4, 'A', ''
FROM categories p WHERE p.code = '001'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '001004');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '맨투맨/스웨트', '맨투맨/스웨트', p.id, TRUE, '001005', 5, 'A', ''
FROM categories p WHERE p.code = '001'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '001005');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '니트/스웨터', '니트/스웨터', p.id, TRUE, '001006', 6, 'A', ''
FROM categories p WHERE p.code = '001'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '001006');

-- 아우터 하위 카테고리
INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '블루종/MA-1', '블루종/MA-1', p.id, TRUE, '002001', 1, 'A', ''
FROM categories p WHERE p.code = '002'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '002001');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '레더/라이더스 재킷', '레더/라이더스 재킷', p.id, TRUE, '002002', 2, 'A', ''
FROM categories p WHERE p.code = '002'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '002002');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '후드 집업', '후드 집업', p.id, TRUE, '002022', 3, 'A', ''
FROM categories p WHERE p.code = '002'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '002022');

-- 바지 하위 카테고리
INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '데님 팬츠', '데님 팬츠', p.id, TRUE, '003002', 1, 'A', ''
FROM categories p WHERE p.code = '003'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '003002');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '트레이닝/조거 팬츠', '트레이닝/조거 팬츠', p.id, TRUE, '003004', 2, 'A', ''
FROM categories p WHERE p.code = '003'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '003004');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '코튼 팬츠', '코튼 팬츠', p.id, TRUE, '003007', 3, 'A', ''
FROM categories p WHERE p.code = '003'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '003007');

-- 신발 하위 카테고리 (품목별 그룹)
INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '스니커즈', '스니커즈', p.id, TRUE, '103004', 1, 'A', '품목별'
FROM categories p WHERE p.code = '103'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '103004');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '부츠/워커', '부츠/워커', p.id, TRUE, '103002', 2, 'A', '품목별'
FROM categories p WHERE p.code = '103'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '103002');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '구두', '구두', p.id, TRUE, '103001', 3, 'A', '품목별'
FROM categories p WHERE p.code = '103'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '103001');

-- 신발 인기 라인업 그룹
INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '나이키 에어포스 1', '나이키 에어포스 1', p.id, TRUE, '103008', 1, 'A', '인기 라인업'
FROM categories p WHERE p.code = '103'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '103008');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '아디다스 삼바 OG', '아디다스 삼바 OG', p.id, TRUE, '103009', 2, 'A', '인기 라인업'
FROM categories p WHERE p.code = '103'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '103009');

-- 가방 하위 카테고리
INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '백팩', '백팩', p.id, TRUE, '004001', 1, 'A', ''
FROM categories p WHERE p.code = '004'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '004001');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '메신저/크로스 백', '메신저/크로스 백', p.id, TRUE, '004002', 2, 'A', ''
FROM categories p WHERE p.code = '004'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '004002');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '숄더백', '숄더백', p.id, TRUE, '004003', 3, 'A', ''
FROM categories p WHERE p.code = '004'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '004003');

-- 뷰티 하위 카테고리
INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '스킨케어', '스킨케어', p.id, TRUE, '104001', 1, 'A', ''
FROM categories p WHERE p.code = '104'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '104001');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '마스크팩', '마스크팩', p.id, TRUE, '104013', 2, 'A', ''
FROM categories p WHERE p.code = '104'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '104013');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '베이스메이크업', '베이스메이크업', p.id, TRUE, '104014', 3, 'A', ''
FROM categories p WHERE p.code = '104'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '104014');

-- 성별별 카테고리 샘플
INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '원피스/스커트', '원피스/스커트', NULL, TRUE, '100', 7, 'F', ''
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE code = '100');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '미니원피스', '미니원피스', p.id, TRUE, '100001', 1, 'F', ''
FROM categories p WHERE p.code = '100'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '100001');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '미디원피스', '미디원피스', p.id, TRUE, '100002', 2, 'F', ''
FROM categories p WHERE p.code = '100'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '100002');

-- 남성 전용 카테고리 추가
INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '남성 정장', '남성 정장', NULL, TRUE, '200', 8, 'M', ''
WHERE NOT EXISTS (SELECT 1 FROM categories WHERE code = '200');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '정장 재킷', '정장 재킷', p.id, TRUE, '200001', 1, 'M', ''
FROM categories p WHERE p.code = '200'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '200001');

INSERT INTO categories (name, description, parent_id, is_active, code, display_order, gender_filter, group_title)
SELECT '정장 바지', '정장 바지', p.id, TRUE, '200002', 2, 'M', ''
FROM categories p WHERE p.code = '200'
AND NOT EXISTS (SELECT 1 FROM categories WHERE code = '200002');
