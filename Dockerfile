FROM openjdk:8
COPY /target/*-jar-with-dependencies.jar /app/app.jar
ADD https://raw.githubusercontent.com/vishnubob/wait-for-it/master/wait-for-it.sh /
RUN chmod +x wait-for-it.sh
CMD ["./wait-for-it.sh", "mariadb:3306", "--timeout=30", "--strict", "--", "java", "-cp", "/app/app.jar", "ast.projects.appbudget.App"]
