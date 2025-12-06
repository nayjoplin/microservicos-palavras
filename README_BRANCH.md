# Sistema Distribuído de Palavras, Etiquetas e Relacionamentos

## Equipe

- **nayjoplin**

## Arquitetura

Este projeto implementa um sistema distribuído de microserviços em Java 21 usando Spring Boot 3.2.0. O sistema é composto por 4 microserviços principais que se comunicam via REST APIs e eventos assíncronos através do RabbitMQ.

### Componentes

1. **Auth-Service** (Porta 4000)
   - Responsável pela autenticação e autorização via JWT
   - Gerencia usuários e emite tokens de acesso
   - Valida tokens para os demais serviços

2. **Word-Service** (Porta 4001)
   - Gerencia o cadastro de palavras (CRUD completo)
   - Publica eventos de exclusão no RabbitMQ
   - Valida requisições via Auth-Service
   - Consulta relacionamentos via Relationship-Service

3. **Tag-Service** (Porta 4002)
   - Gerencia o cadastro de etiquetas (CRUD completo)
   - Publica eventos de exclusão no RabbitMQ
   - Valida requisições via Auth-Service
   - Consulta relacionamentos via Relationship-Service

4. **Relationship-Service** (Porta 4003)
   - Gerencia relacionamentos entre palavras e etiquetas
   - Consome eventos de exclusão para manter consistência
   - Valida existência de palavras e etiquetas antes de criar relacionamentos
   - Valida requisições via Auth-Service

### Infraestrutura

- **PostgreSQL**: 4 instâncias isoladas (uma para cada serviço)
  - auth-db: porta 5435
  - word-db: porta 5432
  - tag-db: porta 5433
  - relationship-db: porta 5434

- **RabbitMQ**: Sistema de mensageria para eventos assíncronos
  - Porta AMQP: 5672
  - Porta Management: 15672
  - Filas: `palavras.excluidas` e `etiquetas.excluidas`

## Endpoints Principais

### Auth-Service (http://localhost:4000)

- `POST /auth/login` - Autenticação de usuário
  - Body: `{"email": "admin@example.com", "password": "admin123"}`
  - Retorna: `{"token": "...", "email": "...", "profile": "..."}`

- `GET /auth/validate` - Validação de token JWT
  - Header: `Authorization: Bearer <token>`
  - Retorna: `{"valid": true, "email": "...", "profile": "..."}`

### Word-Service (http://localhost:4001)

- `GET /palavras` - Lista todas as palavras
- `GET /palavras/{id}` - Busca palavra por ID
- `GET /palavras/existe/{id}` - Verifica existência de palavra
- `GET /palavras/{id}/etiquetas` - Lista etiquetas da palavra
- `POST /palavras` - Cria nova palavra
  - Body: `{"texto": "exemplo"}`
- `PUT /palavras/{id}` - Atualiza palavra
- `DELETE /palavras/{id}` - Remove palavra (publica evento no RabbitMQ)

### Tag-Service (http://localhost:4002)

- `GET /etiquetas` - Lista todas as etiquetas
- `GET /etiquetas/{id}` - Busca etiqueta por ID
- `GET /etiquetas/existe/{id}` - Verifica existência de etiqueta
- `GET /etiquetas/{id}/palavras` - Lista palavras da etiqueta
- `POST /etiquetas` - Cria nova etiqueta
  - Body: `{"nome": "categoria"}`
- `PUT /etiquetas/{id}` - Atualiza etiqueta
- `DELETE /etiquetas/{id}` - Remove etiqueta (publica evento no RabbitMQ)

### Relationship-Service (http://localhost:4003)

- `GET /relacionamentos` - Lista todos os relacionamentos
- `GET /relacionamentos/palavras/{idPalavra}` - Lista IDs das etiquetas de uma palavra
- `GET /relacionamentos/etiquetas/{idEtiqueta}` - Lista IDs das palavras de uma etiqueta
- `POST /relacionamentos` - Cria relacionamento
  - Body: `{"idPalavra": 1, "idEtiqueta": 1}`
- `DELETE /relacionamentos/{id}` - Remove relacionamento

**Nota**: Todos os endpoints (exceto `/auth/login`) requerem autenticação via header:
```
Authorization: Bearer <token>
```

## Credenciais Pré-cadastradas

O sistema vem com dois usuários pré-cadastrados no Auth-Service:

1. **Administrador**
   - Email: `admin@example.com`
   - Senha: `admin123`
   - Profile: `admin`

2. **Usuário Padrão**
   - Email: `user@example.com`
   - Senha: `user123`
   - Profile: `usuario`

## Como Executar

### Pré-requisitos

- Docker e Docker Compose instalados
- Portas 4000-4003, 5432-5435, 5672 e 15672 disponíveis

### Execução

1. Clone o repositório
2. No diretório raiz do projeto, execute:

```bash
docker-compose up --build
```

Este comando irá:
- Construir as imagens Docker dos 4 serviços
- Inicializar 4 instâncias PostgreSQL
- Inicializar o RabbitMQ
- Iniciar todos os microserviços com suas dependências

### Aguarde a Inicialização

Os serviços podem levar alguns minutos para inicializar completamente. Observe os logs para confirmar que todos estão prontos.

## Fluxos Principais

### 1. Autenticação

```bash
# Fazer login
curl -X POST http://localhost:4000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123"}'

# Resposta contém o token JWT
```

### 2. Criar Palavra

```bash
# Criar uma palavra (use o token obtido no login)
curl -X POST http://localhost:4001/palavras \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <seu-token>" \
  -d '{"texto":"microservicos"}'
```

### 3. Criar Etiqueta

```bash
# Criar uma etiqueta
curl -X POST http://localhost:4002/etiquetas \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <seu-token>" \
  -d '{"nome":"tecnologia"}'
```

### 4. Criar Relacionamento

```bash
# Criar relacionamento entre palavra e etiqueta
# O sistema valida automaticamente se ambos existem
curl -X POST http://localhost:4003/relacionamentos \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <seu-token>" \
  -d '{"idPalavra":1,"idEtiqueta":1}'
```

### 5. Consultar Relacionamentos

```bash
# Listar etiquetas de uma palavra
curl -X GET http://localhost:4001/palavras/1/etiquetas \
  -H "Authorization: Bearer <seu-token>"

# Listar palavras de uma etiqueta
curl -X GET http://localhost:4002/etiquetas/1/palavras \
  -H "Authorization: Bearer <seu-token>"
```

### 6. Deletar e Manter Consistência

```bash
# Deletar uma palavra
# Isso automaticamente remove todos os relacionamentos via RabbitMQ
curl -X DELETE http://localhost:4001/palavras/1 \
  -H "Authorization: Bearer <seu-token>"
```

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring AMQP (RabbitMQ)**
- **Spring WebFlux (WebClient)**
- **PostgreSQL 16**
- **RabbitMQ 3**
- **Docker & Docker Compose**
- **Maven**
- **JWT (jjwt 0.12.3)**

## Observações

- Todos os serviços implementam validação de token JWT
- A comunicação entre serviços é realizada via REST APIs síncronas
- Eventos de exclusão são processados de forma assíncrona via RabbitMQ
- Cada serviço possui seu próprio banco de dados PostgreSQL isolado
- O sistema garante consistência eventual através dos eventos de mensageria
