# 도서 관리 서비스 (cdri-book)

이 프로젝트는 도서 정보를 검색하고 수정할 수 있는 간단한 도서 관리 시스템의 백엔드 서비스입니다.

## ☄️ 요구사항

- 도서는 하나 이상의 카테고리에 속할 수 있다.
- 도서는 지은이, 제목의 정보를 가지고 있다.
- 신규 도서는 항상 카테고리가 필요하다.
- 도서는 훼손 또는 분실 등의 이유로 대여가 중단 될 수 있다.
- 도서는 카테고리가 변경될 수 있다.
- 카테고리 별로 도서를 검색 할 수 있다.
- 지은이와 제목으로 도서를 검색 할 수 있다.

## 🛠 기술 스택

- **언어**: Java 21
- **프레임워크**: Spring Boot 3.5.9
- **데이터베이스**: MySQL 8.0
- **ORM/쿼리**: Spring Data JPA, QueryDSL 5.0.0
- **문서화**: Springdoc OpenAPI (Swagger UI) 2.8.5
- **빌드 도구**: Gradle
- **가상화**: Docker, Docker Compose

## 🚀 시작하기

### 필수 조건

- Docker 및 Docker Compose
- Java 21 (로컬 빌드 시)

### 실행 방법 (Docker Compose 사용)

프로젝트 루트 디렉토리에서 제공된 스크립트를 사용하여 환경을 구축하고 실행할 수 있습니다.

#### 1. 애플리케이션 빌드 및 실행
```bash
./script/docker/docker-build-run.sh
```
또는 Windows인 경우:
```batch
script\docker\docker-build-run.bat
```
이 스크립트는 프로젝트를 빌드하여 Docker 이미지를 생성하고, MySQL 데이터베이스와 애플리케이션 컨테이너를 함께 실행합니다.

#### 2. 서비스 중지 및 제거
```bash
./script/docker/docker-down.sh
```
또는 Windows인 경우:
```batch
script\docker\docker-down.bat
```

## 🔌 포트/접속 정보 (Docker Compose 기준)

- **애플리케이션(Base URL)**: `http://localhost:8888`
- **DB (호스트에서 접근 시)**: `localhost:63306`
    - 예: DB 툴에서 접속할 때는 호스트 포트 `63306`을 사용합니다.
- **DB (컨테이너 내부 네트워크에서 접근 시)**: `db:3306`
    - 예: app 컨테이너는 DB 컨테이너를 `db` 호스트명으로 접근합니다.

## 📖 API 문서 (Swagger)

애플리케이션이 실행 중일 때 아래 URL을 통해 API 문서를 확인하고 테스트할 수 있습니다.

- **Swagger UI**: [http://localhost:8888/swagger-ui.html](http://localhost:8888/swagger-ui.html)
- **API Docs (JSON)**: [http://localhost:8888/api-docs](http://localhost:8888/api-docs)

> **참고**: Docker Compose 실행 시 애플리케이션은 8888 포트로 호스트와 연결되어 있습니다.

## ✨ 주요 기능

- **책 검색**: 제목, 카테고리, 저자명, 대출 가능 여부 등을 조건으로 책을 검색할 수 있습니다. (커서 기반 페이지네이션 지원)
- **책 정보 수정**: 특정 책의 카테고리 정보를 수정할 수 있습니다.

## 📂 프로젝트 구조

- `src/main/java/cdri/api`: 컨트롤러 및 요청/응답 DTO
- `src/main/java/cdri/domain`: 비즈니스 로직 및 도메인 엔티티/서비스
- `src/main/java/cdri/infra`: 데이터베이스 접근 및 외부 연동 (JPA, QueryDSL)
- `script/docker`: Docker 관련 설정 및 실행 스크립트
- `script/mysql`: 데이터베이스 초기화(DDL, DML) 스크립트
