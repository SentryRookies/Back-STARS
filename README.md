# Back-STARS
SK쉴더스 루키즈 최종프로젝트 백엔드 리포지토리 입니다.

<details>
<summary>로컬에서 postgreSQL 테스트</summary>

1. Docker 이미지 다운로드 및 컨테이너 실행
    `docker pull postgres:latest`

2. PostgreSQL 컨테이너 실행
    `docker run --name my-postgres -e POSTGRES_USER=root -e POSTGRES_PASSWORD=admin -e POSTGRES_DB=stars_db -p 5432:5432 -d postgres:latest`

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

</details>


<details>
<summary>PostgreSQL과 프로젝트 같이 띄우는 법</summary>

1. 프로젝트 최상위 디렉토리 (/place-service, docker-compose.yml과 Dockerfile이 있는 위치)에서 docker compose up --build 실행
2. 빌드가 완료되면 자동으로 Spring 로고가 나오면서 실행됨
3. 다른 cmd 창에서 docker exec -it my_postgres bash 실행
4. psql -U root -d stars_db 실행하면 접속이 될 것임
5. 접속한 후 \dt 로 테이블 존재 확인 가능, select 문으로 데이터 확인 가능
+ select * from area; 로 결과를 본 후, q 를 눌러야 다시 명령창으로 돌아갈 수 있음

</details>