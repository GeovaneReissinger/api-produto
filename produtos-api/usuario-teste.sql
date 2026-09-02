-- Usuário de teste para login (Aula 7 — Spring Security + JWT)
-- A tabela "usuario" é criada automaticamente pelo Hibernate (ddl-auto=update)
-- na primeira vez que a aplicação sobe. Rode este INSERT depois disso.

-- username: paulo
-- password: senha123 (hash BCrypt abaixo — NUNCA insira senha em texto puro)
INSERT INTO usuario (username, password, role)
VALUES ('paulo', '$2a$10$gaJAVE0VXRUVYu1/L6tLIOKGLY/RXw6AFJv3uPv9CiHKgbeoojTAe', 'USER');

-- Para gerar o hash de outro usuário/senha, use qualquer gerador de hash
-- BCrypt com custo 10 e copie o resultado para um novo INSERT.
