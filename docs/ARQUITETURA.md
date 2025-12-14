# Arquitetura do Sistema

## 📋 Índice

1. [Visão Geral](#visão-geral)
2. [Diagrama de Arquitetura](#diagrama-de-arquitetura)
3. [Microserviços](#microserviços)
4. [Padrões Arquiteturais](#padrões-arquiteturais)
5. [Comunicação entre Serviços](#comunicação-entre-serviços)
6. [Banco de Dados](#banco-de-dados)
7. [Segurança](#segurança)
8. [Escalabilidade](#escalabilidade)
9. [Monitoramento e Observabilidade](#monitoramento-e-observabilidade)
10. [Decisões de Design](#decisões-de-design)

## 🎯 Visão Geral

O sistema é uma aplicação distribuída baseada em microserviços para gerenciar palavras, etiquetas (tags) e seus relacionamentos. A arquitetura foi projetada seguindo princípios de:

- **Domain-Driven Design (DDD)**: Cada microserviço representa um domínio de negócio específico
- **Database per Service**: Cada serviço possui seu próprio banco de dados
- **API Gateway Pattern**: Ponto único de entrada para todos os clientes
- **Event-Driven Architecture**: Comunicação assíncrona via eventos
- **CQRS (Command Query Responsibility Segregation)**: Separação de operações de leitura e escrita
- **Circuit Breaker**: Resiliência contra falhas em cascata

### Princípios Fundamentais

1. **Independência**: Cada microserviço pode ser desenvolvido, testado e implantado independentemente
2. **Escalabilidade**: Serviços podem escalar horizontalmente de forma independente
3. **Resiliência**: Falhas em um serviço não afetam outros serviços
4. **Manutenibilidade**: Código organizado e coeso facilita manutenção
5. **Observabilidade**: Logs, métricas e traces centralizados

## 📊 Diagrama de Arquitetura

### Visão de Alto Nível

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENTES                                  │
│  (Web App, Mobile App, APIs Externas)                           │
└────────────────────────┬────────────────────────────────────────┘
                         │
                         │ HTTPS
                         ▼
┌─────────────────────────────────────────────────────────────────┐
│                      API GATEWAY                                 │
│  - Roteamento                                                    │
│  - Autenticação/Autorização                                      │
│  - Rate Limiting                                                 │
│  - Circuit Breaker                                               │
│  - Load Balancing                                                │
└──────┬────────────────┬────────────────┬─────────────────────────┘
       │                │                │
       │ REST           │ REST           │ REST
       ▼                ▼                ▼
┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│    AUTH     │  │    WORD     │  │    LABEL    │
│   SERVICE   │  │   SERVICE   │  │   SERVICE   │
│             │  │             │  │             │
│ - Registro  │  │ - CRUD      │  │ - CRUD      │
│ - Login     │  │   Palavras  │  │   Etiquetas │
│ - JWT       │  │ - Validação │  │ - Relações  │
│ - Roles     │  │ - Busca     │  │ - Hierarquia│
└──────┬──────┘  └──────┬──────┘  └──────┬──────┘
       │                │                │
       │                │                │
       ▼                ▼                ▼
┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│  auth_db    │  │  word_db    │  │  tag_db     │
│ PostgreSQL  │  │ PostgreSQL  │  │ PostgreSQL  │
└─────────────┘  └─────────────┘  └─────────────┘
       │                │                │
       └────────────────┴────────────────┘
                         │
                         │ AMQP
                         ▼
              ┌─────────────────────┐
              │     RABBITMQ        │
              │  Message Broker     │
              │                     │
              │  - Events           │
              │  - Pub/Sub          │
              │  - Queues           │
              └─────────────────────┘
```

### Fluxo de Requisição

```
1. Cliente → API Gateway
2. Gateway valida token (Auth Service)
3. Gateway roteia para serviço apropriado
4. Serviço processa requisição
5. Serviço publica evento (se necessário)
6. Serviço retorna resposta
7. Gateway retorna ao cliente
```

## 🎯 Microserviços

### 1. API Gateway (Porta 8080)

**Responsabilidades:**
- Ponto único de entrada
- Roteamento de requisições
- Autenticação centralizada
- Rate limiting
- Load balancing
- Circuit breaker
- Agregação de respostas

**Tecnologias:**
- Spring Cloud Gateway
- Spring Security
- Resilience4j

**Padrões:**
- Gateway Aggregation
- Gateway Routing
- Gateway Offloading

### 2. Auth Service (Porta 8083)

**Responsabilidades:**
- Autenticação de usuários
- Autorização (RBAC)
- Geração de tokens JWT
- Gerenciamento de sessões
- Recuperação de senha
- Auditoria de acesso

**Tecnologias:**
- Spring Security
- JWT (io.jsonwebtoken)
- BCrypt para hashing de senhas

**Banco de Dados:**
- `auth_db` (PostgreSQL:5435)
- Tabelas: users, roles, user_roles, refresh_tokens

**Eventos Publicados:**
- UserRegisteredEvent
- UserLoggedInEvent
- PasswordChangedEvent

### 3. Word Service (Porta 8081)

**Responsabilidades:**
- CRUD de palavras
- Validação de palavras
- Busca e filtros
- Versionamento de palavras
- Estatísticas de palavras

**Tecnologias:**
- Spring Data JPA
- Hibernate
- PostgreSQL

**Banco de Dados:**
- `word_db` (PostgreSQL:5432)
- Tabelas: words, word_history

**Eventos Publicados:**
- WordCreatedEvent
- WordUpdatedEvent
- WordDeletedEvent

**Eventos Consumidos:**
- LabelDeletedEvent (remove associações)

### 4. Label Service (Porta 8082)

**Responsabilidades:**
- CRUD de etiquetas
- Gerenciamento de relacionamentos palavra-etiqueta
- Hierarquia de etiquetas
- Categorização
- Estatísticas de uso

**Tecnologias:**
- Spring Data JPA
- Hibernate
- PostgreSQL

**Banco de Dados:**
- `tag_db` (PostgreSQL:5433)
- Tabelas: labels, word_labels, label_hierarchy

**Eventos Publicados:**
- LabelCreatedEvent
- LabelUpdatedEvent
- LabelDeletedEvent
- LabelAssignedEvent
- LabelRemovedEvent

**Eventos Consumidos:**
- WordDeletedEvent (remove associações)

## 🏗️ Padrões Arquiteturais

### 1. API Gateway Pattern

**Objetivo:** Fornecer um ponto único de entrada para todos os clientes.

**Vantagens:**
- Simplifica clientes (não precisam conhecer todos os serviços)
- Centraliza autenticação e autorização
- Facilita monitoramento
- Permite versionamento de APIs

**Implementação:**
```java
// Gateway Route Configuration
@Bean
public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("word-service", r -> r.path("/words/**")
            .filters(f -> f.stripPrefix(1)
                           .circuitBreaker(c -> c.setName("wordServiceCB")))
            .uri("http://localhost:8081"))
        .build();
}
```

### 2. Database per Service

**Objetivo:** Cada microserviço possui seu próprio banco de dados.

**Vantagens:**
- Independência de dados
- Escalabilidade independente
- Evita acoplamento
- Permite escolher tecnologia de BD adequada

**Desafios:**
- Transações distribuídas
- Consistência eventual
- Joins entre serviços

**Solução:** Eventos e compensação

### 3. Event-Driven Architecture

**Objetivo:** Comunicação assíncrona via eventos.

**Fluxo de Eventos:**
```
1. Word Service cria palavra
2. Publica WordCreatedEvent no RabbitMQ
3. Label Service consome evento
4. Label Service atualiza estatísticas
5. Analytics Service (futuro) consome para analytics
```

**Configuração RabbitMQ:**
```yaml
rabbitmq:
  exchanges:
    - words.exchange (type: topic)
    - labels.exchange (type: topic)
  queues:
    - word.events.queue
    - label.events.queue
  bindings:
    - queue: word.events.queue
      exchange: words.exchange
      routing-key: word.*
```

**Vantagens:**
- Baixo acoplamento
- Escalabilidade
- Resiliência
- Auditoria

### 4. CQRS (Command Query Responsibility Segregation)

**Objetivo:** Separar operações de leitura e escrita.

**Implementação (Simplificada):**
```
Commands (Write):
- CreateWordCommand
- UpdateWordCommand
- DeleteWordCommand

Queries (Read):
- GetWordQuery
- SearchWordsQuery
- GetWordStatisticsQuery
```

**Vantagens:**
- Otimização independente
- Escalabilidade de leitura
- Modelos especializados

### 5. Circuit Breaker Pattern

**Objetivo:** Prevenir falhas em cascata.

**Estados:**
1. **CLOSED**: Requisições passam normalmente
2. **OPEN**: Serviço com falhas, requisições bloqueadas
3. **HALF_OPEN**: Testando recuperação

**Configuração:**
```yaml
resilience4j:
  circuitbreaker:
    instances:
      wordServiceCB:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
        sliding-window-size: 10
```

### 6. Saga Pattern (Futuro)

Para transações distribuídas:

**Exemplo: Criar palavra com etiqueta**
```
1. Word Service: Cria palavra
2. Publica WordCreatedEvent
3. Label Service: Associa etiqueta
4. Se falhar: Publica CompensationEvent
5. Word Service: Reverte criação
```

## 🔄 Comunicação entre Serviços

### Comunicação Síncrona (REST)

**Quando usar:**
- Operações que requerem resposta imediata
- Validações
- Consultas

**Exemplo:**
```
API Gateway → Auth Service (validar token)
API Gateway → Word Service (buscar palavra)
```

### Comunicação Assíncrona (Eventos)

**Quando usar:**
- Operações que não requerem resposta imediata
- Notificações
- Processamento em background
- Integração entre serviços

**Exemplo:**
```
Word Service → RabbitMQ → Label Service (palavra deletada)
Label Service → RabbitMQ → Analytics Service (estatísticas)
```

### Formato de Eventos

```json
{
  "eventId": "uuid-v4",
  "eventType": "WORD_CREATED",
  "timestamp": "2024-01-15T10:30:00Z",
  "aggregateId": "word-123",
  "aggregateType": "Word",
  "payload": {
    "wordId": 123,
    "word": "exemplo",
    "userId": "user-456"
  },
  "metadata": {
    "correlationId": "req-789",
    "causationId": "event-012",
    "userId": "user-456"
  }
}
```

## 🗄️ Banco de Dados

### Estratégia de Persistência

Cada serviço possui seu próprio banco de dados PostgreSQL com schema isolado.

### Word Service Database

```sql
-- Tabela de palavras
CREATE TABLE words (
    id BIGSERIAL PRIMARY KEY,
    word VARCHAR(255) NOT NULL UNIQUE,
    definition TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

-- Índices para performance
CREATE INDEX idx_word_status ON words(status);
CREATE INDEX idx_word_created ON words(created_at);
CREATE INDEX idx_word_search ON words USING gin(to_tsvector('portuguese', word || ' ' || COALESCE(definition, '')));

-- Histórico de versões
CREATE TABLE word_history (
    id BIGSERIAL PRIMARY KEY,
    word_id BIGINT REFERENCES words(id),
    version INT NOT NULL,
    word VARCHAR(255),
    definition TEXT,
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    changed_by VARCHAR(100)
);
```

### Label Service Database

```sql
-- Tabela de etiquetas
CREATE TABLE labels (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    color VARCHAR(7),
    category VARCHAR(50),
    parent_id BIGINT REFERENCES labels(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) DEFAULT 'ACTIVE'
);

-- Relacionamento palavra-etiqueta
CREATE TABLE word_labels (
    id BIGSERIAL PRIMARY KEY,
    word_id BIGINT NOT NULL,
    label_id BIGINT REFERENCES labels(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    UNIQUE(word_id, label_id)
);

-- Índices
CREATE INDEX idx_word_labels_word ON word_labels(word_id);
CREATE INDEX idx_word_labels_label ON word_labels(label_id);
CREATE INDEX idx_labels_category ON labels(category);
```

### Auth Service Database

```sql
-- Usuários
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    enabled BOOLEAN DEFAULT TRUE,
    account_locked BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP
);

-- Papéis (roles)
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

-- Relacionamento usuário-papel
CREATE TABLE user_roles (
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    role_id BIGINT REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

-- Refresh tokens
CREATE TABLE refresh_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Índices
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
```

### Estratégias de Backup

```bash
# Backup diário automatizado
0 2 * * * pg_dump -h localhost -U user word_db > /backups/word_db_$(date +\%Y\%m\%d).sql
0 2 * * * pg_dump -h localhost -U user tag_db > /backups/tag_db_$(date +\%Y\%m\%d).sql
0 2 * * * pg_dump -h localhost -U user auth_db > /backups/auth_db_$(date +\%Y\%m\%d).sql
```

## 🔒 Segurança

### Camadas de Segurança

```
1. Network Layer: HTTPS, Firewall
2. Gateway Layer: Rate Limiting, DDoS Protection
3. Authentication Layer: JWT, OAuth2
4. Authorization Layer: RBAC
5. Data Layer: Encryption at rest
```

### Autenticação JWT

**Fluxo:**
```
1. User → POST /auth/login
2. Auth Service valida credenciais
3. Auth Service gera JWT (HS256)
4. Client recebe token
5. Client → GET /words (Authorization: Bearer {token})
6. Gateway valida token
7. Gateway extrai claims (userId, roles)
8. Gateway roteia para Word Service com headers
```

**Estrutura do Token:**
```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "sub": "username",
    "userId": 123,
    "roles": ["ROLE_USER"],
    "iat": 1642248000,
    "exp": 1642251600
  }
}
```

### Autorização (RBAC)

**Roles:**
- `ROLE_USER`: Leitura
- `ROLE_CONTRIBUTOR`: Leitura + Escrita
- `ROLE_MODERATOR`: Leitura + Escrita + Moderação
- `ROLE_ADMIN`: Acesso total

**Matriz de Permissões:**

| Endpoint | USER | CONTRIBUTOR | MODERATOR | ADMIN |
|----------|------|-------------|-----------|-------|
| GET /words | ✓ | ✓ | ✓ | ✓ |
| POST /words | ✗ | ✓ | ✓ | ✓ |
| PUT /words/{id} | ✗ | ✓ (próprio) | ✓ | ✓ |
| DELETE /words/{id} | ✗ | ✗ | ✓ | ✓ |
| GET /auth/admin/* | ✗ | ✗ | ✗ | ✓ |

### Proteções Implementadas

1. **Rate Limiting**: 100 req/min por usuário
2. **CORS**: Apenas origens autorizadas
3. **CSRF**: Tokens CSRF em formulários
4. **XSS**: Headers de segurança (CSP)
5. **SQL Injection**: Prepared statements
6. **Password Policy**: Senha forte obrigatória
7. **Account Lockout**: 5 tentativas falhas

## 📈 Escalabilidade

### Escalabilidade Horizontal

```yaml
# docker-compose.scale.yml
services:
  word-service:
    deploy:
      replicas: 3

  label-service:
    deploy:
      replicas: 3

  api-gateway:
    deploy:
      replicas: 2
```

### Load Balancing

**Níveis de balanceamento:**
1. **DNS Round Robin**: Múltiplos IPs
2. **API Gateway**: Balanceia entre instâncias
3. **Database**: Read replicas para leitura

### Caching

**Estratégia de cache:**
```yaml
# Redis para cache distribuído
cache:
  words:
    ttl: 600  # 10 minutos
    max-size: 10000

  labels:
    ttl: 3600  # 1 hora
    max-size: 5000
```

**Padrão Cache-Aside:**
```java
public Word getWord(Long id) {
    // 1. Tentar buscar do cache
    Word cached = cacheService.get("word:" + id);
    if (cached != null) return cached;

    // 2. Buscar do banco
    Word word = wordRepository.findById(id);

    // 3. Armazenar no cache
    cacheService.put("word:" + id, word, 600);

    return word;
}
```

## 📊 Monitoramento e Observabilidade

### Três Pilares

1. **Logs**: Registro de eventos
2. **Métricas**: Medições quantitativas
3. **Traces**: Rastreamento de requisições

### Stack de Monitoramento (Futuro)

```
┌──────────────┐
│   Grafana    │ ← Visualização
└──────┬───────┘
       │
┌──────▼───────┐
│  Prometheus  │ ← Métricas
└──────┬───────┘
       │
┌──────▼───────┐
│  Services    │ ← Exporters
└──────────────┘

┌──────────────┐
│     ELK      │ ← Logs
│ (Elasticsearch,
│  Logstash,   │
│  Kibana)     │
└──────────────┘

┌──────────────┐
│    Jaeger    │ ← Traces
└──────────────┘
```

### Métricas Importantes

**API Gateway:**
- Taxa de requisições (req/s)
- Latência (p50, p95, p99)
- Taxa de erros (%)
- Circuit breaker state

**Microserviços:**
- Tempo de resposta
- Throughput
- Uso de CPU/Memória
- Conexões de BD ativas

**Banco de Dados:**
- Query latency
- Conexões ativas
- Cache hit ratio
- Slow queries

## 🎯 Decisões de Design

### 1. Por que microserviços?

**Vantagens:**
- Escalabilidade independente
- Deploys independentes
- Tecnologias heterogêneas
- Times autônomos

**Trade-offs:**
- Complexidade operacional
- Latência de rede
- Consistência eventual

### 2. Por que PostgreSQL para todos?

**Decisão:** Usar PostgreSQL em todos os serviços.

**Razões:**
- Confiabilidade comprovada
- Suporte a JSONB (flexibilidade)
- Full-text search
- Transações ACID
- Conhecimento da equipe

**Alternativas consideradas:**
- MongoDB (descartado: preferência por SQL)
- MySQL (descartado: menor feature set)

### 3. Por que RabbitMQ?

**Razões:**
- Suporte a múltiplos padrões (pub/sub, routing)
- Confiabilidade (ACKs, persistência)
- Management UI
- Comunidade ativa

**Alternativas:**
- Kafka (descartado: overhead para escala atual)
- Redis Pub/Sub (descartado: sem garantias de entrega)

### 4. Por que JWT?

**Razões:**
- Stateless (escalável)
- Self-contained (sem lookup de BD)
- Padrão da indústria
- Suporte em todas as linguagens

**Trade-offs:**
- Não é possível revogar (solução: refresh tokens)
- Tamanho do token

### 5. Por que Spring Boot?

**Razões:**
- Ecossistema maduro
- Convenção sobre configuração
- Spring Cloud (microserviços)
- Comunidade grande
- Documentação excelente

## 🔮 Roadmap Arquitetural

### Curto Prazo
- [ ] Service Discovery (Eureka/Consul)
- [ ] Distributed Tracing (Jaeger)
- [ ] Centralized Logging (ELK Stack)
- [ ] Redis para cache

### Médio Prazo
- [ ] Kubernetes deployment
- [ ] API Versioning
- [ ] GraphQL Gateway
- [ ] Event Sourcing

### Longo Prazo
- [ ] Multi-region deployment
- [ ] CQRS completo
- [ ] Machine Learning para sugestões
- [ ] Real-time collaboration

---

**Documentação de Arquitetura** - Sistema de Microserviços de Palavras e Etiquetas
