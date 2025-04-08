#Usando a imagem oficial do Maven para compilar o projeto Java
FROM maven:3.8.4-openjdk-17-slim AS build

#Definindo o diretório de trabalho dentro do container
WORKDIR /app

#Copiando o arquivo POM e os arquivos fonte do projeto
COPY pom.xml .
COPY src ./src

#Compilando o projeto com Maven
RUN mvn clean install -DskipTests

#Usando o OpenJDK para executar a aplicação Java
FROM openjdk:17-jdk-alpine

#Definindo o diretório de trabalho dentro do container
WORKDIR /app

#Copiando o JAR gerado para o diretório de trabalho
COPY --from=build /app/target/tabletrust-0.0.1-SNAPSHOT.jar ./app.jar

# Expose os ports padrão 8080
EXPOSE 8080

# Configurando a aplicação para usar o PostgreSQL
#ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/postgres
#ENV SPRING_DATASOURCE_USERNAME=postgres
#ENV SPRING_DATASOURCE_PASSWORD=102030

# Aguardando o PostgreSQL iniciar antes de iniciar a aplicação
CMD ["java", "-jar", "app.jar"]
