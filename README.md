# GitMatch_Api
## Como rodar

1. Subir o Docker:

docker-compose up -d

2. Configurar credenciais no `src/main/resources/application.properties`

3. Rodar a aplicação:

./mvnw spring-boot:run


## Endpoints

- POST /api/auth/login
- POST /api/usuarios/cadastro
- GET /api/usuarios
