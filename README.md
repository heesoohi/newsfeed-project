# 뉴스피드 프로젝트 - 소셜 미디어 플랫폼
간단한 소셜 미디어 기능을 제공하는 웹 애플리케이션입니다.

## 소개
사용자들이 프로필을 관리하고, 뉴스피드에 게시물을 작성하며, 팔로우와 댓글, 좋아요 기능을 통해 소통할 수 있는 플랫폼입니다. 이 프로젝트는 소셜 네트워킹의 핵심 기능을 구현하며, 안전한 사용자 인증과 데이터 관리를 목표로 개발되었습니다.

## 주요 기능
- 프로필 관리: 사용자 정보 조회 및 수정, 비밀번호 변경 기능
- 뉴스피드: 게시물 작성, 조회, 수정, 삭제 및 페이지네이션
- 사용자 인증: 회원가입, 로그인, JWT 기반 인증
- 팔로우: 다른 사용자를 팔로우하고 그들의 게시물을 뉴스피드에서 확인
- 검색 및 정렬: 게시물 정렬과 기간별 검색
- 댓글: 게시물에 댓글 작성, 수정, 삭제
- 좋아요: 게시물과 댓글에 좋아요 추가/취소
- 테스트: Repository, Service, Controller 유닛 테스트로 50% 커버리지 달성

## API 명세서

🔗 [노션으로 보기](https://teamsparta.notion.site/1b22dc3ef51480b6a10ff709d18b854a?v=1b22dc3ef5148189b459000cf825341a)

| 카테고리       | 메서드 | 기능                          | 엔드포인트                                              | 인증 필요 | 요청 바디                                      | 응답 바디                                                                                   |
|----------------|--------|-------------------------------|--------------------------------------------------------|-----------|-----------------------------------------------|--------------------------------------------------------------------------------------------|
| User           | POST   | 회원 가입                     | `/auth/signup`                                         | ⛔ NO     | ```json { "email": "string", "username": "string", "password": "string" } ``` | ```json { "code": 200, "status": "OK", "userId": 1 } ```                            |
| User           | POST   | 로그인                        | `/auth/login`                                          | ⛔ NO     | ```json { "email": "string", "password": "string" } ```                       | ```json { "code": 200, "status": "OK", "userId": 1, "accessToken": "Bearer Token data" } ``` |
| User           | GET    | 유저 정보 단건 조회           | `/users/{userId}`                                      | ⛔ NO     |                                               | ```json { "code": 200, "status": "OK", "userId": 1, "name": "이름", "email": "temp@gmail.com", "followerCount": 0, "followingCount": 0 } ``` |
| User           | PUT    | 유저 프로필 수정              | `/users`                                               | ✅ OK     | ```json { "username": "new name" } ```         | ```json { "code": 200, "status": "OK" } ```                                          |
| User           | PUT    | 유저 비밀번호 수정            | `/users/password`                                      | ✅ OK     | ```json { "oldPassword": "string", "newPassword": "string" } ```              | ```json { "code": 200, "status": "OK" } ```                                          |
| User           | POST   | 회원 탈퇴                     | `/users/withdraw`                                      | ✅ OK     | ```json { "password": "string" } ```           | ```json { "code": 200, "status": "OK" } ```                                          |
| Post           | POST   | 게시글 작성                   | `/posts`                                               | ✅ OK     | ```json { "content": "안녕하세요" } ```         | ```json { "code": 200, "status": "OK", "postId": 1 } ```                             |
| Post           | GET    | 게시글 단건 조회              | `/posts/{postId}`                                      | ⛔ NO     |                                               | ```json { "code": 200, "status": "OK", "postId": 1, "content": "안녕하세요", "userName": "이름", "createdAt": "2025-02-19T13:32:30.393461", "updatedAt": "2025-02-19T13:32:30.393461" } ``` |
| Post           | GET    | 게시글 다건 조회(수정일 기준) | `/posts?size={size}&page={page}`                      | ⛔ NO     |                                               | ```json { "code": 200, "status": "OK", "data": [{ "postId": 1, "content": "안녕하세요", "userName": "이름", "createdAt": "2025-02-19T13:32:30", "updatedAt": "2025-02-19T13:32:30" }], "pagination": { "currentPage": 0, "totalPages": 1, "totalElements": 1, "size": 20 } } ``` |
| Post           | GET    | 게시글 검색 조회(기간별)      | `/posts/search?page={page}&sort={sort}&startDate={startDate}&endDate={endDate}` | ⛔ NO | | ```json { "code": 200, "status": "OK", "data": [{ "postId": 7, "content": "게시글 내용", "username": "이름", "createdAt": "2025-03-13T17:04:23.287317", "updatedAt": "2025-03-13T17:04:23.287317" }, ...], "paginationInfo": { "currentPage": 0, "totalPages": 1, "totalElements": 3, "size": 10 } } ``` |
| Post           | GET    | 팔로우 중인 유저 게시물 조회  | `/posts-following?size={size}&page={page}`            | ✅ OK     |                                               | ```json { "code": 200, "status": "OK", "data": [{ "postId": 4, "content": "안녕하세요", "userName": "이름3", "createdAt": "2025-02-19T15:16:16", "updatedAt": "2025-02-19T15:16:16" }, ...], "pagination": { "currentPage": 0, "totalPages": 2, "totalElements": 3, "size": 2 } } ``` |
| Post           | PUT    | 게시글 수정                   | `/posts/{postId}`                                      | ✅ OK     | ```json { "content": "게시글 수정 내용" } ```   | ```json { "code": 200, "status": "OK" } ```                                          |
| Post           | DELETE | 게시글 삭제                   | `/posts/{postId}`                                      | ✅ OK     |                                               | ```json { "code": 200, "status": "OK" } ```                                          |
| Follow         | POST   | 팔로우 하기                   | `/follow/{targetUserId}`                               | ✅ OK     |                                               | ```json { "code": 200, "status": "OK" } ```                                          |
| Follow         | DELETE | 팔로우 취소                   | `/follow/{targetUserId}`                               | ✅ OK     |                                               | ```json { "code": 200, "status": "OK" } ```                                          |
| Comment        | POST   | 댓글 작성                     | `/posts/{postId}/comments`                             | ✅ OK     | ```json { "content": "좋은 댓글" } ```          | ```json { "code": 200, "status": "OK", "commentId": 1 } ```                          |
| Comment        | GET    | 댓글 다건 조회                | `/posts/{postId}/comments?size={size}&page={page}`    | ⛔ NO     |                                               | ```json { "code": 200, "status": "OK", "data": [{ "commentId": 1, "content": "댓글 내용", "postId": 3, "userName": "이름", "createdAt": "2025-02-19T14:57:39.047373", "updatedAt": "2025-02-19T14:57:39.047373" }, ...], "pagination": { "currentPage": 0, "totalPages": 1, "totalElements": 2, "size": 20 } } ``` |
| Comment        | PUT    | 댓글 수정                     | `/posts/{postId}/comments/{commentId}`                | ✅ OK     | ```json { "content": "댓글 수정" } ```          | ```json { "code": 200, "status": "OK" } ```                                          |
| Comment        | DELETE | 댓글 삭제                     | `/posts/{postId}/comments/{commentId}`                | ✅ OK     |                                               | ```json { "code": 200, "status": "OK" } ```                                          |
| Like_Post      | POST   | 게시글 좋아요                 | `/posts/{postId}/like`                                 | ✅ OK     |                                               | ```json { "code": 200, "status": "OK" } ```                                          |
| Like_Post      | DELETE | 게시글 좋아요 취소            | `/posts/{postId}/like`                                 | ✅ OK     |                                               | ```json { "code": 200, "status": "OK" } ```                                          |
| Like_Comment   | POST   | 댓글 좋아요                   | `/posts/{postId}/comments/{commentId}/like`           | ✅ OK     |                                               | ```json { "code": 200, "status": "OK" } ```                                          |
| Like_Comment   | DELETE | 댓글 좋아요 취소              | `/posts/{postId}/comments/{commentId}/like`           | ✅ OK     |                                               | ```json { "code": 200, "status": "OK" } ```                                          |

## ERD
![image](https://github.com/user-attachments/assets/f4b1c772-efe9-4662-be2d-0f370ede3877)

## API 동작 예시

- 회원가입
<img width="825" alt="image" src="https://github.com/user-attachments/assets/8bb7417c-eb58-4bd6-9491-90ba190e8d43" />

- 로그인
<img width="807" alt="image" src="https://github.com/user-attachments/assets/a27e88e1-8595-499a-81a4-93c07e5bea68" />

- 프로필 조회(유저 정보 단건 조회)
  <img width="814" alt="image" src="https://github.com/user-attachments/assets/3e3b1ae9-7ff6-49e4-9a56-09089319602f" />

- 유저 정보 수정
  <img width="813" alt="image" src="https://github.com/user-attachments/assets/ed6d0681-3708-4aa0-8cee-af893b4725f1" />

- 유저 비밀번호 수정
<img width="807" alt="image" src="https://github.com/user-attachments/assets/fc073e6a-77c1-479b-be3f-ba476f026b59" />

-회원 탈퇴
<img width="819" alt="image" src="https://github.com/user-attachments/assets/2aaf51fc-e593-4d19-a21d-139f0e0177e9" />

- 게시글 생성
<img width="815" alt="image" src="https://github.com/user-attachments/assets/175f2f97-3301-4448-9c58-d79e177dfce4" />

- 게시글 단건 조회
<img width="816" alt="image" src="https://github.com/user-attachments/assets/2817cde4-14a7-491a-ab1e-faa3d6483c7d" />

- 게시글 다건 조회
<img width="820" alt="image" src="https://github.com/user-attachments/assets/11fa237f-787f-4eb6-8689-4f383f5c4089" />

- 게시글 다건 조회 - 수정일 기준 정렬
<img width="814" alt="image" src="https://github.com/user-attachments/assets/be631fe8-55c2-493a-b646-dab917a82691" />

- 게시글 다건 조회 - 좋아요 순 정렬
<img width="812" alt="image" src="https://github.com/user-attachments/assets/55248f28-4a40-4e96-ae8b-44575b2aa0cc" />

- 게시글 다건 조회 - 기간별 검색
<img width="819" alt="image" src="https://github.com/user-attachments/assets/d2ce9ce6-77ef-46b9-ae80-c30e35217f0d" />

- 게시글 수정
<img width="812" alt="image" src="https://github.com/user-attachments/assets/dd72479e-9640-4b24-b6b7-1a4d2543d121" />

- 게시글 삭제
<img width="817" alt="image" src="https://github.com/user-attachments/assets/9dcedb00-dbe7-4a63-98e6-3e37c0dc0e02" />

- 팔로우
<img width="807" alt="image" src="https://github.com/user-attachments/assets/c7eed16b-22a8-4059-9404-e4059102f89f" />

- 언팔로우
<img width="805" alt="image" src="https://github.com/user-attachments/assets/3d415b2c-7166-4cf3-9814-4819eca1f947" />

- 팔로잉의 게시글 다건 조회
<img width="809" alt="image" src="https://github.com/user-attachments/assets/8842f8d3-4993-479f-975d-b4dfee802b4f" />

- 댓글 생성
<img width="812" alt="image" src="https://github.com/user-attachments/assets/31095194-03e4-4463-ba08-8a12ff98cff4" />

- 댓글 다건 조회
<img width="807" alt="image" src="https://github.com/user-attachments/assets/4e1d1657-55f0-41f4-a844-911bbf0f836f" />

- 댓글 수정
<img width="814" alt="image" src="https://github.com/user-attachments/assets/4558d0a6-9cc9-45cd-a9e8-b0fcaa370b93" />

- 댓글 삭제
<img width="817" alt="image" src="https://github.com/user-attachments/assets/801087f0-3d9c-4461-a176-5c96a524a9fd" />

- 게시글 좋아요
<img width="812" alt="image" src="https://github.com/user-attachments/assets/5e068144-d357-4a49-8680-2d5d7a37679d" />

- 게시글 좋아요 취소
<img width="810" alt="image" src="https://github.com/user-attachments/assets/8d2bd33d-8d08-400c-86ed-0a75841c2b1a" />

- 댓글 좋아요
<img width="811" alt="image" src="https://github.com/user-attachments/assets/eb0a5349-fe69-4327-bcaf-51ede7eb7e68" />

- 댓글 좋아요 취소
- <img width="806" alt="image" src="https://github.com/user-attachments/assets/5da38339-4077-4409-9a3e-58c99677b973" />
