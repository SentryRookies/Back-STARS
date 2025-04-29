# 멀티스테이징 도커 파일 구성

# 소스코드를 빌드
# 빌드의 결과물 jar -> 가동
# 실습, 위의 목적을 달성하는 도커 파일 생성

# 스테이지 1-> jar를 생성(빌드), 과도기적 이미지, 용량이 커도 무방
# 그레이들 빌드 도구와 자바가 세팅된 이미지로부터 이미지 구성
FROM gradle:7.6-jdk AS builder

# 워킹 디렉토리(리눅스 기반 설정) 지정
WORKDIR /app

# 호스트 OS에서 백엔드 원소스 전체 카피

COPY . .

# 빌드
RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test --no-daemon


# 최종 산출물 : /app/build/libs/*SNAPSHOT.jar

# 스테이지 2 -> jar를 가동  -> 경량!! -> 배포속도가 상승
# JDK17이 설치된 경량 버전의 리눅스 준비
FROM openjdk:17-jdk-slim

# 컨테이너 내부에 작업디렉토리 지정
WORKDIR /app

# jar 복사
# 스테이지1으로부터 특정위치에 존재하는 산출물을 현재 컨테이너의 작업디렉토리 루트에 복사

COPY --from=builder /app/build/libs/*SNAPSHOT.jar ./congestion.jar
# 포트지정

# env 파일 추가
COPY .env .env

EXPOSE 8082

# 서버가동
CMD ["java", "-jar", "congestion.jar"]