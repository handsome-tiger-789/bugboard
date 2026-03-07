-- ========================================
-- Users 샘플 데이터
-- ========================================

-- 일반 사용자
INSERT INTO users (provider, provider_id, email, name, nickname, role, created_at, updated_at, is_delete)
VALUES ('GOOGLE', 'google_101', 'user1@gmail.com', '김철수', '철수dev', 'USER', NOW(), NOW(), false);

INSERT INTO users (provider, provider_id, email, name, nickname, role, created_at, updated_at, is_delete)
VALUES ('GOOGLE', 'google_102', 'user2@gmail.com', '이영희', '영희코딩', 'USER', NOW(), NOW(), false);

-- 관리자
INSERT INTO users (provider, provider_id, email, name, nickname, role, created_at, updated_at, is_delete)
VALUES ('GOOGLE', 'google_200', 'admin@gmail.com', '박관리', '관리자', 'ADMIN', NOW(), NOW(), false);

-- 탈퇴(삭제)된 사용자
INSERT INTO users (provider, provider_id, email, name, nickname, role, created_at, updated_at, is_delete)
VALUES ('GOOGLE', 'google_103', 'deleted@gmail.com', '최탈퇴', '탈퇴유저', 'USER', NOW(), NOW(), true);

-- ========================================
-- Board 샘플 데이터
-- ========================================

-- 일반 게시글 (user1 작성)
INSERT INTO board (users_id, title, content, view_count, created_at, updated_at, is_delete)
VALUES (1, '첫 번째 게시글입니다', '안녕하세요! 첫 번째 게시글 내용입니다.', 10, NOW(), NOW(), false);

-- 조회수 높은 인기 게시글 (user1 작성)
INSERT INTO board (users_id, title, content, view_count, created_at, updated_at, is_delete)
VALUES (1, '인기 게시글 - 조회수 테스트', '이 게시글은 조회수가 높은 인기 게시글입니다.', 500, NOW(), NOW(), false);

-- 다른 사용자 게시글 (user2 작성)
INSERT INTO board (users_id, title, content, view_count, created_at, updated_at, is_delete)
VALUES (2, '영희의 게시글', '영희가 작성한 게시글입니다.', 3, NOW(), NOW(), false);

-- 관리자 공지 게시글 (admin 작성)
INSERT INTO board (users_id, title, content, view_count, created_at, updated_at, is_delete)
VALUES (3, '[공지] 서비스 이용 안내', '서비스 이용 규칙 안내 게시글입니다.', 100, NOW(), NOW(), false);

-- 삭제된 게시글 (user1 작성, soft delete)
INSERT INTO board (users_id, title, content, view_count, created_at, updated_at, is_delete)
VALUES (1, '삭제된 게시글', '이 게시글은 삭제 처리되었습니다.', 5, NOW(), NOW(), true);

-- 조회수 0인 새 게시글 (user2 작성)
INSERT INTO board (users_id, title, content, view_count, created_at, updated_at, is_delete)
VALUES (2, '방금 작성한 게시글', '방금 작성되어 조회수가 없는 게시글입니다.', 0, NOW(), NOW(), false);

-- ========================================
-- Comment 샘플 데이터
-- ========================================

-- 게시글1에 댓글 (user2가 작성)
INSERT INTO comment (board_id, users_id, content, like_count, created_at, updated_at, is_delete)
VALUES (1, 2, '좋은 글이네요! 잘 읽었습니다.', 3, NOW(), NOW(), false);

-- 게시글1에 댓글 (admin이 작성)
INSERT INTO comment (board_id, users_id, content, like_count, created_at, updated_at, is_delete)
VALUES (1, 3, '관리자 답변입니다.', 0, NOW(), NOW(), false);

-- 게시글2(인기글)에 댓글 여러 개
INSERT INTO comment (board_id, users_id, content, like_count, created_at, updated_at, is_delete)
VALUES (2, 2, '정말 유용한 정보입니다!', 15, NOW(), NOW(), false);

INSERT INTO comment (board_id, users_id, content, like_count, created_at, updated_at, is_delete)
VALUES (2, 3, '추천합니다.', 7, NOW(), NOW(), false);

-- 삭제된 댓글 (게시글1, user2 작성)
INSERT INTO comment (board_id, users_id, content, like_count, created_at, updated_at, is_delete)
VALUES (1, 2, '삭제된 댓글입니다.', 1, NOW(), NOW(), true);

-- 게시글3에 댓글 (user1이 작성)
INSERT INTO comment (board_id, users_id, content, like_count, created_at, updated_at, is_delete)
VALUES (3, 1, '영희님 글 잘 봤습니다!', 0, NOW(), NOW(), false);

-- 좋아요 많은 댓글 (게시글4 공지에 user1 작성)
INSERT INTO comment (board_id, users_id, content, like_count, created_at, updated_at, is_delete)
VALUES (4, 1, '공지 확인했습니다. 감사합니다!', 20, NOW(), NOW(), false);
