# Utiliza una imagen base oficial de Java 21, si está disponible. Si no, puedes usar una versión más reciente y asegurarte de que tu código es compatible.
#FROM openjdk:23-jdk-slim
FROM openjdk:8-jdk-slim


# Opcional: Establece una variable de entorno para el puerto que tu aplicación utilizará, esto es útil para documentar qué puertos espera exponer tu aplicación.
ENV APP_PORT=8080

# Expone el puerto que tu aplicación usa (Ajusta este valor según sea necesario)
EXPOSE ${APP_PORT}

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia el archivo JAR de tu aplicación al contenedor. Asegúrate de construir tu JAR antes de construir la imagen Docker.
# Reemplaza `myapp.jar` con el path y nombre de tu archivo JAR.
COPY build/libs/Act04.BDXND-1.0-SNAPSHOT.jar /app/myapp.jar

# Ejecuta tu aplicación Java cuando el contenedor inicie.
# Asegúrate de ajustar el comando si tu aplicación necesita argumentos adicionales.
CMD ["java", "-jar", "/app/myapp.jar"]
