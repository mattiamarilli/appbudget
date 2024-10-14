FROM openjdk:8-jre

# Installo Xvfb e le dipendenze grafiche
RUN apt-get update && apt-get install -y \
    xvfb \
    libfontconfig1 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libxext6 \
    && rm -rf /var/lib/apt/lists/*

# Copio il jar
ARG jarToCopy
COPY /target/$jarToCopy /app/app.jar

# Creo script per avviare Xvfb e l'applicazione
RUN echo '#!/bin/bash\n\
Xvfb :99 -screen 0 1024x768x16 &\n\
export DISPLAY=:99\n\
java -jar /app/app.jar mariadb\n' > /app/run.sh

# Rendo eseguibile lo script
RUN chmod +x /app/run.sh

# Esegui lo script
CMD ["/app/run.sh"]
