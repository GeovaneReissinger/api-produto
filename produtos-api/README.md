# produtos-api

API REST para gerenciamento de produtos e categorias, com autenticação JWT.
Desenvolvida em **Java 25 + Spring Boot + Spring Data JPA + PostgreSQL**, usada como
API-base nas aulas de desenvolvimento Back-End e de testes de API.

A aplicação roda por padrão na porta **8080** (`http://localhost:8080`).

## Dados de teste

Esta seção descreve o passo a passo completo para colocar o projeto no ar do zero,
com um usuário e alguns produtos/categorias já cadastrados para uso nos exercícios
de aula.

### 1. Criar o banco no PostgreSQL

Crie um banco chamado `produtos_db` (ou outro nome de sua preferência — nesse caso,
ajuste `spring.datasource.url` em `src/main/resources/application.properties`):

```sql
CREATE DATABASE produtos_db;
```

### 2. Configurar o PostgreSQL

Por padrão, a aplicação se conecta em:

```
jdbc:postgresql://localhost:5432/produtos_db
```

com o usuário `postgres`. Ajuste `spring.datasource.url` e
`spring.datasource.username` em `application.properties` se o seu ambiente for
diferente.

### 3. Configurar a variável de ambiente `DB_PASSWORD`

A senha do banco **não fica hardcoded** no `application.properties`. Ela vem da
variável de ambiente `DB_PASSWORD`:

```bash
export DB_PASSWORD="sua-senha-do-postgres"
```

Se `DB_PASSWORD` não for definida, é usado um valor padrão apenas para facilitar a
execução local (veja o comentário em `application.properties`). **Em qualquer
ambiente real, defina sempre `DB_PASSWORD` explicitamente.**

### 4. Configurar a variável de ambiente `JWT_SECRET`

A chave usada para assinar os tokens JWT vem da variável de ambiente `JWT_SECRET`:

```bash
export JWT_SECRET="uma-chave-base64-forte-e-secreta"
```

Assim como `DB_PASSWORD`, existe um valor padrão apenas para uso local/didático. Em
produção, `JWT_SECRET` deve ser configurada externamente (nunca versionada no
código).

### 5. Executar a aplicação

```bash
./mvnw spring-boot:run
```

Na primeira execução, o Hibernate cria automaticamente as tabelas `usuario`,
`categoria` e `produto` (`spring.jpa.hibernate.ddl-auto=update`).

### 6. Criar o usuário de teste

Com a aplicação já tendo subido ao menos uma vez (tabelas criadas), rode o script
[`usuario-teste.sql`](usuario-teste.sql):

```bash
psql -h localhost -U postgres -d produtos_db -f usuario-teste.sql
```

Isso cria o usuário:

- **username:** `paulo`
- **password:** `senha123`

A senha é gravada como hash BCrypt (nunca em texto puro). Para gerar o hash de
outro usuário/senha, use qualquer gerador de hash BCrypt com custo 10 (ex.: uma
biblioteca BCrypt na linguagem de sua preferência) e copie o resultado para um
novo `INSERT` seguindo o modelo de [`usuario-teste.sql`](usuario-teste.sql).

### 7. Criar categorias e produtos de teste (opcional)

Para ter alguns dados de exemplo, use o arquivo [`produtos-seed.json`](produtos-seed.json)
como referência e cadastre as categorias e produtos manualmente pelo Swagger
(`POST /categorias` e `POST /produtos`, autenticado com o JWT do usuário de teste).
Como os ids são gerados automaticamente pelo banco, eles não são previsíveis
(diferente de um script SQL com ids fixos).

### 8. Acessar o Swagger

Com a aplicação rodando, a documentação interativa fica em:

```
http://localhost:8080/swagger-ui.html
```

### 9. Fazer login

```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{"username":"paulo","password":"senha123"}'
```

A resposta (`200 OK`) traz o token JWT no corpo, em texto puro.

### 10. Usar o JWT nas requisições

Envie o token no header `Authorization`, com o prefixo `Bearer `:

```bash
curl http://localhost:8080/produtos \
  -H "Authorization: Bearer <token retornado no login>"
```

No Swagger, clique em **Authorize** e informe apenas o token (sem o prefixo
`Bearer `, que é adicionado automaticamente).

Todos os endpoints exigem autenticação, exceto `POST /login` e a própria
documentação Swagger/OpenAPI.
