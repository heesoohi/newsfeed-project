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
| User           | POST   | 회원 가입                     | `/auth/signup`                                         | ⛔ NO     | ```json<br>{ "email": "string", "username": "string", "password": "string" }<br>``` | ```json<br>{ "code": 200, "status": "OK", "userId": 1 }<br>```                            |
| User           | POST   | 로그인                        | `/auth/login`                                          | ⛔ NO     | ```json<br>{ "email": "string", "password": "string" }<br>```                       | ```json<br>{ "code": 200, "status": "OK", "userId": 1, "accessToken": "Bearer Token data" }<br>``` |
| User           | GET    | 유저 정보 단건 조회           | `/users/{userId}`                                      | ⛔ NO     |                                               | ```json<br>{ "code": 200, "status": "OK", "userId": 1, "name": "이름", "email": "temp@gmail.com", "followerCount": 0, "followingCount": 0 }<br>``` |
| User           | PUT    | 유저 프로필 수정              | `/users`                                               | ✅ OK     | ```json<br>{ "username": "new name" }<br>```         | ```json<br>{ "code": 200, "status": "OK" }<br>```                                          |
| User           | PUT    | 유저 비밀번호 수정            | `/users/password`                                      | ✅ OK     | ```json<br>{ "oldPassword": "string", "newPassword": "string" }<br>```              | ```json<br>{ "code": 200, "status": "OK" }<br>```                                          |
| User           | POST   | 회원 탈퇴                     | `/users/withdraw`                                      | ✅ OK     | ```json<br>{ "password": "string" }<br>```           | ```json<br>{ "code": 200, "status": "OK" }<br>```                                          |
| Post           | POST   | 게시글 작성                   | `/posts`                                               | ✅ OK     | ```json<br>{ "content": "안녕하세요" }<br>```         | ```json<br>{ "code": 200, "status": "OK", "postId": 1 }<br>```                             |
| Post           | GET    | 게시글 단건 조회              | `/posts/{postId}`                                      | ⛔ NO     |                                               | ```json<br>{ "code": 200, "status": "OK", "postId": 1, "content": "안녕하세요", "userName": "이름", "createdAt": "2025-02-19T13:32:30.393461", "updatedAt": "2025-02-19T13:32:30.393461" }<br>``` |
| Post           | GET    | 게시글 다건 조회(수정일 기준) | `/posts?size={size}&page={page}`                      | ⛔ NO     |                                               | ```json<br>{ "code": 200, "status": "OK", "data": [{ "postId": 1, "content": "안녕하세요", "userName": "이름", "createdAt": "2025-02-19T13:32:30", "updatedAt": "2025-02-19T13:32:30" }], "pagination": { "currentPage": 0, "totalPages": 1, "totalElements": 1, "size": 20 } }<br>``` |
| Post           | GET    | 게시글 검색 조회(기간별)      | `/posts/search?page={page}&sort={sort}&startDate={startDate}&endDate={endDate}` | ⛔ NO | | ```json<br>{ "code": 200, "status": "OK", "data": [{ "postId": 7, "content": "게시글 내용", "username": "이름", "createdAt": "2025-03-13T17:04:23.287317", "updatedAt": "2025-03-13T17:04:23.287317" }, ...], "paginationInfo": { "currentPage": 0, "totalPages": 1, "totalElements": 3, "size": 10 } }<br>``` |
| Post           | GET    | 팔로우 중인 유저 게시물 조회  | `/posts-following?size={size}&page={page}`            | ✅ OK     |                                               | ```json<br>{ "code": 200, "status": "OK", "data": [{ "postId": 4, "content": "안녕하세요", "userName": "이름3", "createdAt": "2025-02-19T15:16:16", "updatedAt": "2025-02-19T15:16:16" }, ...], "pagination": { "currentPage": 0, "totalPages": 2, "totalElements": 3, "size": 2 } }<br>``` |
| Post           | PUT    | 게시글 수정                   | `/posts/{postId}`                                      | ✅ OK     | ```json<br>{ "content": "게시글 수정 내용" }<br>```   | ```json<br>{ "code": 200, "status": "OK" }<br>```                                          |
| Post           | DELETE | 게시글 삭제                   | `/posts/{postId}`                                      | ✅ OK     |                                               | ```json<br>{ "code": 200, "status": "OK" }<br>```                                          |
| Follow         | POST   | 팔로우 하기                   | `/follow/{targetUserId}`                               | ✅ OK     |                                               | ```json<br>{ "code": 200, "status": "OK" }<br>```                                          |
| Follow         | DELETE | 팔로우 취소                   | `/follow/{targetUserId}`                               | ✅ OK     |                                               | ```json<br>{ "code": 200, "status": "OK" }<br>```                                          |
| Comment        | POST   | 댓글 작성                     | `/posts/{postId}/comments`                             | ✅ OK     | ```json<br>{ "content": "좋은 댓글" }<br>```          | ```json<br>{ "code": 200, "status": "OK", "commentId": 1 }<br>```                          |
| Comment        | GET    | 댓글 다건 조회                | `/posts/{postId}/comments?size={size}&page={page}`    | ⛔ NO     |                                               | ```json<br>{ "code": 200, "status": "OK", "data": [{ "commentId": 1, "content": "댓글 내용", "postId": 3, "userName": "이름", "createdAt": "2025-02-19T14:57:39.047373", "updatedAt": "2025-02-19T14:57:39.047373" }, ...], "pagination": { "currentPage": 0, "totalPages": 1, "totalElements": 2, "size": 20 } }<br>``` |
| Comment        | PUT    | 댓글 수정                     | `/posts/{postId}/comments/{commentId}`                | ✅ OK     | ```json<br>{ "content": "댓글 수정" }<br>```          | ```json<br>{ "code": 200, "status": "OK" }<br>```                                          |
| Comment        | DELETE | 댓글 삭제                     | `/posts/{postId}/comments/{commentId}`                | ✅ OK     |                                               | ```json<br>{ "code": 200, "status": "OK" }<br>```                                          |
| Like_Post      | POST   | 게시글 좋아요                 | `/posts/{postId}/like`                                 | ✅ OK     |                                               | ```json<br>{ "code": 200, "status": "OK" }<br>```                                          |
| Like_Post      | DELETE | 게시글 좋아요 취소            | `/posts/{postId}/like`                                 | ✅ OK     |                                               | ```json<br>{ "code": 200, "status": "OK" }<br>```                                          |
| Like_Comment   | POST   | 댓글 좋아요                   | `/posts/{postId}/comments/{commentId}/like`           | ✅ OK     |                                               | ```json<br>{ "code": 200, "status": "OK" }<br>```                                          |
| Like_Comment   | DELETE | 댓글 좋아요 취소              | `/posts/{postId}/comments/{commentId}/like`           | ✅ OK     |                                               | ```json<br>{ "code": 200, "status": "OK" }<br>```                                          |


## ERD
![image](https://github.com/user-attachments/assets/f4b1c772-efe9-4662-be2d-0f370ede3877)

## API 동작 예시
