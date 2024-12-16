# Usa un'immagine Java (JRE) leggera come base
FROM openjdk:17-slim

# Imposta la directory di lavoro nel container
WORKDIR /app

# Copia il file .jar dal tuo sistema al container
COPY target/ProvaAPI-0.0.1-SNAPSHOT.jar app.jar

# Comando di default per eseguire il file .jar
CMD ["java", "-jar", "app.jar"]
