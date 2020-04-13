FROM openjdk:8-jdk-alpine
COPY build/dependency/BOOT-INF/lib /app/lib
COPY build/dependency/META-INF /app/META-INF
COPY build/dependency/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","com.bits.Application"]
