# Back-STARS
SK쉴더스 루키즈 최종프로젝트 백엔드 리포지토리 입니다.


#### 로컬에서 postgreSQL 테스트
1. Docker 이미지 다운로드 및 컨테이너 실행
    `docker pull postgres:latest`

2. PostgreSQL 컨테이너 실행
    `docker run --name my-postgres -e POSTGRES_USER=admin -e POSTGRES_PASSWORD=admin -e POSTGRES_DB=star -p 5432:5432 -d postgres:latest`

3. Spring Boot 애플리케이션 설정
    - 이제, Spring Boot 애플리케이션에서 PostgreSQL과 연결 설정. application.properties에 PostgreSQL 데이터베이스 설정 추가
    ```
    spring.datasource.url=jdbc:postgresql://localhost:5432/star
    spring.datasource.username=admin
    spring.datasource.password=admin
    spring.datasource.driver-class-name=org.postgresql.Driver
    spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
    spring.jpa.hibernate.ddl-auto=update
    ```