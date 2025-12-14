# Auth Service - Serviço de Autenticação

## 📋 Descrição

O Auth Service é um microserviço responsável por gerenciar toda a autenticação e autorização do sistema. Implementa autenticação baseada em JWT (JSON Web Tokens) e fornece endpoints para registro, login, gerenciamento de usuários e controle de acesso.

## 🎯 Responsabilidades

- Registro de novos usuários
- Autenticação de usuários (login)
- Geração e validação de tokens JWT
- Gerenciamento de sessões
- Controle de permissões e papéis (roles)
- Renovação de tokens (refresh tokens)
- Recuperação de senha
- Gerenciamento de perfis de usuário

## 🔧 Tecnologias

- **Java 21**
- **Spring Boot 3.4.1**
- **Spring Security** (autenticação e autorização)
- **Spring Data JPA** (persistência)
- **PostgreSQL** (banco de dados)
- **JWT (JSON Web Tokens)** (tokens de autenticação)
- **BCrypt** (criptografia de senhas)
- **Spring Validation** (validação de dados)

## 🗄️ Banco de Dados

### Configuração

```properties
spring.datasource.url=jdbc:postgresql://localhost:5435/auth_db
spring.datasource.username=user
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Modelo de Dados

#### Tabela: `users`

| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | BIGINT | Identificador único (auto-incremento) |
| username | VARCHAR(50) | Nome de usuário (único, não nulo) |
| email | VARCHAR(255) | Email (único, não nulo) |
| password | VARCHAR(255) | Senha criptografada (BCrypt) |
| first_name | VARCHAR(100) | Primeiro nome |
| last_name | VARCHAR(100) | Sobrenome |
| enabled | BOOLEAN | Conta ativa (padrão: true) |
| account_locked | BOOLEAN | Conta bloqueada (padrão: false) |
| created_at | TIMESTAMP | Data de criação |
| updated_at | TIMESTAMP | Data da última atualização |
| last_login | TIMESTAMP | Último login |

#### Tabela: `roles`

| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | BIGINT | Identificador único |
| name | VARCHAR(50) | Nome do papel (ROLE_USER, ROLE_ADMIN, etc.) |
| description | TEXT | Descrição do papel |

#### Tabela: `user_roles`

| Campo | Tipo | Descrição |
|-------|------|-----------|
| user_id | BIGINT | ID do usuário |
| role_id | BIGINT | ID do papel |

#### Tabela: `refresh_tokens`

| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | BIGINT | Identificador único |
| user_id | BIGINT | ID do usuário |
| token | VARCHAR(255) | Token de refresh (único) |
| expiry_date | TIMESTAMP | Data de expiração |
| created_at | TIMESTAMP | Data de criação |

## 🚀 Endpoints da API

### Base URL
```
http://localhost:8083
```

### 1. Registrar Novo Usuário

**POST** `/api/auth/register`

```json
{
  "username": "joaosilva",
  "email": "joao.silva@example.com",
  "password": "SenhaSegura@123",
  "firstName": "João",
  "lastName": "Silva"
}
```

**Resposta (201 Created)**
```json
{
  "id": 1,
  "username": "joaosilva",
  "email": "joao.silva@example.com",
  "firstName": "João",
  "lastName": "Silva",
  "roles": ["ROLE_USER"],
  "message": "Usuário registrado com sucesso!"
}
```

### 2. Login

**POST** `/api/auth/login`

```json
{
  "username": "joaosilva",
  "password": "SenhaSegura@123"
}
```

**Resposta (200 OK)**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "username": "joaosilva",
    "email": "joao.silva@example.com",
    "roles": ["ROLE_USER"]
  }
}
```

### 3. Renovar Token

**POST** `/api/auth/refresh`

```json
{
  "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

**Resposta (200 OK)**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "660e8400-e29b-41d4-a716-446655440001",
  "tokenType": "Bearer",
  "expiresIn": 3600
}
```

### 4. Logout

**POST** `/api/auth/logout`

**Headers:**
```
Authorization: Bearer {token}
```

**Resposta (200 OK)**
```json
{
  "message": "Logout realizado com sucesso"
}
```

### 5. Obter Perfil do Usuário

**GET** `/api/auth/me`

**Headers:**
```
Authorization: Bearer {token}
```

**Resposta (200 OK)**
```json
{
  "id": 1,
  "username": "joaosilva",
  "email": "joao.silva@example.com",
  "firstName": "João",
  "lastName": "Silva",
  "roles": ["ROLE_USER"],
  "lastLogin": "2024-01-15T10:30:00"
}
```

### 6. Atualizar Perfil

**PUT** `/api/auth/me`

**Headers:**
```
Authorization: Bearer {token}
```

```json
{
  "firstName": "João Pedro",
  "lastName": "Silva Santos",
  "email": "joao.pedro@example.com"
}
```

**Resposta (200 OK)**

### 7. Alterar Senha

**POST** `/api/auth/change-password`

**Headers:**
```
Authorization: Bearer {token}
```

```json
{
  "currentPassword": "SenhaSegura@123",
  "newPassword": "NovaSenhaSegura@456"
}
```

**Resposta (200 OK)**
```json
{
  "message": "Senha alterada com sucesso"
}
```

### 8. Solicitar Recuperação de Senha

**POST** `/api/auth/forgot-password`

```json
{
  "email": "joao.silva@example.com"
}
```

**Resposta (200 OK)**
```json
{
  "message": "Email de recuperação enviado"
}
```

### 9. Validar Token

**POST** `/api/auth/validate`

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Resposta (200 OK)**
```json
{
  "valid": true,
  "username": "joaosilva",
  "roles": ["ROLE_USER"],
  "expiresAt": "2024-01-15T14:30:00"
}
```

## 🔐 JWT (JSON Web Tokens)

### Configuração

```properties
# Chave secreta (use uma chave forte em produção)
jwt.secret=${JWT_SECRET:minha-chave-secreta-super-segura-123}

# Tempo de expiração do access token (1 hora)
jwt.expiration=3600000

# Tempo de expiração do refresh token (7 dias)
jwt.refresh.expiration=604800000
```

### Estrutura do Token

#### Header
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

#### Payload
```json
{
  "sub": "joaosilva",
  "userId": 1,
  "roles": ["ROLE_USER"],
  "iat": 1642248000,
  "exp": 1642251600
}
```

### Como Usar o Token

Incluir o token no header `Authorization` de todas as requisições protegidas:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

## 👥 Papéis e Permissões

### Papéis Padrão

| Papel | Descrição | Permissões |
|-------|-----------|------------|
| ROLE_USER | Usuário comum | Leitura de palavras e etiquetas |
| ROLE_CONTRIBUTOR | Contribuidor | Criação e edição de palavras/etiquetas |
| ROLE_MODERATOR | Moderador | Aprovação e moderação de conteúdo |
| ROLE_ADMIN | Administrador | Acesso total ao sistema |

### Endpoints de Administração

#### Listar Todos os Usuários (ADMIN)

**GET** `/api/auth/admin/users`

**Resposta (200 OK)**
```json
{
  "content": [
    {
      "id": 1,
      "username": "joaosilva",
      "email": "joao.silva@example.com",
      "roles": ["ROLE_USER"],
      "enabled": true
    }
  ],
  "totalElements": 1
}
```

#### Atualizar Papéis do Usuário (ADMIN)

**PUT** `/api/auth/admin/users/{id}/roles`

```json
{
  "roles": ["ROLE_USER", "ROLE_CONTRIBUTOR"]
}
```

#### Bloquear/Desbloquear Usuário (ADMIN)

**PUT** `/api/auth/admin/users/{id}/lock`

```json
{
  "locked": true
}
```

## 🔒 Segurança

### Criptografia de Senhas

- Algoritmo: **BCrypt**
- Força: 12 rounds (configurável)

### Políticas de Senha

- Mínimo de 8 caracteres
- Pelo menos uma letra maiúscula
- Pelo menos uma letra minúscula
- Pelo menos um número
- Pelo menos um caractere especial

### Proteções

- **Rate Limiting**: Máximo de 5 tentativas de login em 15 minutos
- **Bloqueio de Conta**: Após 5 tentativas falhas consecutivas
- **CORS**: Configurado para permitir apenas origens autorizadas
- **CSRF**: Proteção habilitada
- **XSS**: Headers de segurança configurados

## 🏃 Como Executar

### 1. Pré-requisitos

```bash
docker-compose up -d auth-db
```

### 2. Configurar Variáveis de Ambiente

```bash
export JWT_SECRET=sua-chave-secreta-super-segura
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5435/auth_db
```

### 3. Executar o Serviço

```bash
cd authservice
mvn spring-boot:run
```

## 🧪 Testes

```bash
# Testes unitários
mvn test

# Testes de integração
mvn verify
```

### Exemplos de Testes

```bash
# Testar registro
curl -X POST http://localhost:8083/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teste",
    "email": "teste@example.com",
    "password": "Teste@123"
  }'

# Testar login
curl -X POST http://localhost:8083/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teste",
    "password": "Teste@123"
  }'
```

## 📊 Monitoramento

### Health Check

```bash
curl http://localhost:8083/actuator/health
```

### Métricas

- Total de usuários registrados
- Taxa de logins bem-sucedidos/falhos
- Tokens ativos
- Tentativas de acesso bloqueadas

## 🔍 Validações

### Erros Comuns

**400 Bad Request** - Dados inválidos
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Email inválido",
  "path": "/api/auth/register"
}
```

**401 Unauthorized** - Credenciais inválidas
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Credenciais inválidas"
}
```

**403 Forbidden** - Sem permissão
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Acesso negado"
}
```

**409 Conflict** - Usuário já existe
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Usuário ou email já cadastrado"
}
```

## 🛠️ Estrutura de Pacotes

```
com.palavras.etiquetas.authservice
├── controller/        # Controllers REST
├── service/          # Lógica de negócio
├── repository/       # Acesso a dados
├── model/            # Entidades JPA
├── dto/              # Data Transfer Objects
├── security/         # Configurações de segurança
│   ├── jwt/         # Utilitários JWT
│   └── filter/      # Filtros de segurança
├── exception/        # Tratamento de exceções
├── config/           # Configurações
└── util/             # Utilitários
```

## 📝 Melhorias Futuras

- [ ] Autenticação OAuth2 (Google, Facebook)
- [ ] Autenticação de dois fatores (2FA)
- [ ] Logs de auditoria de acesso
- [ ] Sistema de permissões granulares
- [ ] Integração com LDAP/Active Directory
- [ ] Sessões distribuídas com Redis

## 🤝 Contribuindo

Veja o [README principal](../README.md) para instruções de contribuição.

---

**Auth Service** - Parte do Sistema de Microserviços de Palavras e Etiquetas
