# API Gateway - Gateway de APIs

## 📋 Descrição

O API Gateway é o ponto único de entrada para todos os microserviços do sistema. Ele atua como um proxy reverso, roteando requisições HTTP para os serviços apropriados, além de implementar cross-cutting concerns como autenticação, rate limiting, logging e monitoramento.

## 🎯 Responsabilidades

- Roteamento de requisições para microserviços
- Autenticação e autorização centralizada
- Rate limiting e throttling
- Balanceamento de carga
- Cache de respostas
- Transformação de requisições/respostas
- Logging e monitoramento centralizado
- Tratamento de erros unificado
- CORS (Cross-Origin Resource Sharing)
- Compressão de respostas

## 🔧 Tecnologias

- **Java 21**
- **Spring Boot 3.4.1**
- **Spring Cloud Gateway** (gateway reativo)
- **Spring Security** (autenticação)
- **Spring WebFlux** (programação reativa)
- **Resilience4j** (circuit breaker e retry)
- **Micrometer** (métricas)

## 🚀 Arquitetura

```
Cliente → API Gateway → [Auth Service | Word Service | Label Service]
```

## 🗺️ Rotas Configuradas

### Base URL
```
http://localhost:8080
```

### 1. Auth Service

| Método | Rota Original | Rota no Gateway |
|--------|---------------|-----------------|
| POST | /api/auth/register | /auth/register |
| POST | /api/auth/login | /auth/login |
| POST | /api/auth/refresh | /auth/refresh |
| POST | /api/auth/logout | /auth/logout |
| GET | /api/auth/me | /auth/me |

**Exemplo:**
```bash
# Antes (direto no serviço)
POST http://localhost:8083/api/auth/login

# Agora (via gateway)
POST http://localhost:8080/auth/login
```

### 2. Word Service

| Método | Rota Original | Rota no Gateway |
|--------|---------------|-----------------|
| GET | /api/words | /words |
| POST | /api/words | /words |
| GET | /api/words/{id} | /words/{id} |
| PUT | /api/words/{id} | /words/{id} |
| DELETE | /api/words/{id} | /words/{id} |
| GET | /api/words/search | /words/search |

**Exemplo:**
```bash
# Listar palavras via gateway
GET http://localhost:8080/words

# Criar palavra via gateway
POST http://localhost:8080/words
Authorization: Bearer {token}
```

### 3. Label Service

| Método | Rota Original | Rota no Gateway |
|--------|---------------|-----------------|
| GET | /api/labels | /labels |
| POST | /api/labels | /labels |
| GET | /api/labels/{id} | /labels/{id} |
| PUT | /api/labels/{id} | /labels/{id} |
| DELETE | /api/labels/{id} | /labels/{id} |
| POST | /api/labels/{id}/words/{wordId} | /labels/{id}/words/{wordId} |

## ⚙️ Configuração

### application.yml

```yaml
server:
  port: 8080

spring:
  application:
    name: api-gateway

  cloud:
    gateway:
      routes:
        # Auth Service Routes
        - id: auth-service
          uri: http://localhost:8083
          predicates:
            - Path=/auth/**
          filters:
            - StripPrefix=1
            - name: CircuitBreaker
              args:
                name: authCircuitBreaker
                fallbackUri: forward:/fallback/auth

        # Word Service Routes
        - id: word-service
          uri: http://localhost:8081
          predicates:
            - Path=/words/**
          filters:
            - StripPrefix=0
            - AuthenticationFilter
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20

        # Label Service Routes
        - id: label-service
          uri: http://localhost:8082
          predicates:
            - Path=/labels/**
          filters:
            - StripPrefix=0
            - AuthenticationFilter
            - name: RequestRateLimiter
              args:
                redis-rate-limiter.replenishRate: 10
                redis-rate-limiter.burstCapacity: 20

# Configurações de timeout
  cloud:
    gateway:
      httpclient:
        connect-timeout: 5000
        response-timeout: 10s
```

## 🔐 Autenticação

### Fluxo de Autenticação

1. Cliente faz login em `/auth/login`
2. Auth Service retorna JWT token
3. Cliente inclui token nas próximas requisições:
   ```
   Authorization: Bearer {token}
   ```
4. API Gateway valida o token antes de rotear
5. Se válido, requisição é encaminhada ao serviço
6. Se inválido, retorna 401 Unauthorized

### Rotas Públicas (Não requerem autenticação)

- `POST /auth/register`
- `POST /auth/login`
- `POST /auth/refresh`
- `POST /auth/forgot-password`
- `GET /health`
- `GET /actuator/health`

### Rotas Protegidas (Requerem autenticação)

- Todas as rotas de `/words/**`
- Todas as rotas de `/labels/**`
- `GET /auth/me`
- `POST /auth/logout`

## 🛡️ Filtros Implementados

### 1. AuthenticationFilter

Valida o token JWT em todas as requisições protegidas.

```java
// Exemplo de uso
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 2. RateLimitingFilter

Limita o número de requisições por usuário/IP.

**Configuração:**
- Taxa de reposição: 10 requisições/segundo
- Capacidade burst: 20 requisições

**Resposta quando limite excedido (429 Too Many Requests):**
```json
{
  "error": "Too Many Requests",
  "message": "Você excedeu o limite de requisições",
  "retryAfter": 60
}
```

### 3. LoggingFilter

Registra todas as requisições e respostas.

**Informações registradas:**
- Timestamp
- Método HTTP
- URL
- IP do cliente
- User ID (se autenticado)
- Tempo de resposta
- Status code

### 4. CorsFilter

Configura CORS para permitir requisições cross-origin.

```yaml
cors:
  allowed-origins:
    - http://localhost:3000
    - http://localhost:4200
  allowed-methods:
    - GET
    - POST
    - PUT
    - DELETE
    - OPTIONS
  allowed-headers:
    - Authorization
    - Content-Type
  max-age: 3600
```

## 🔄 Circuit Breaker

Implementado com Resilience4j para proteção contra falhas em cascata.

### Configuração

```yaml
resilience4j:
  circuitbreaker:
    instances:
      authCircuitBreaker:
        registerHealthIndicator: true
        slidingWindowSize: 10
        minimumNumberOfCalls: 5
        failureRateThreshold: 50
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 3
```

### Estados do Circuit Breaker

1. **CLOSED**: Funcionamento normal
2. **OPEN**: Serviço com falhas, requisições vão para fallback
3. **HALF_OPEN**: Testando recuperação do serviço

### Fallback Endpoints

```bash
# Quando Auth Service está indisponível
GET /fallback/auth
→ Retorna: {"error": "Auth Service temporariamente indisponível"}

# Quando Word Service está indisponível
GET /fallback/words
→ Retorna: {"error": "Word Service temporariamente indisponível"}
```

## ⚡ Rate Limiting

### Configuração por Rota

```yaml
rate-limits:
  # Rotas públicas: 20 req/min
  public:
    replenishRate: 20
    burstCapacity: 40

  # Rotas autenticadas: 100 req/min
  authenticated:
    replenishRate: 100
    burstCapacity: 150

  # Rotas de admin: 200 req/min
  admin:
    replenishRate: 200
    burstCapacity: 300
```

### Headers de Rate Limit

Todas as respostas incluem headers informativos:

```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 87
X-RateLimit-Reset: 1642251600
```

## 📊 Monitoramento

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

**Resposta:**
```json
{
  "status": "UP",
  "components": {
    "gateway": {
      "status": "UP"
    },
    "authService": {
      "status": "UP"
    },
    "wordService": {
      "status": "UP"
    },
    "labelService": {
      "status": "UP"
    }
  }
}
```

### Métricas

```bash
# Métricas do Prometheus
curl http://localhost:8080/actuator/prometheus

# Métricas gerais
curl http://localhost:8080/actuator/metrics
```

**Métricas disponíveis:**
- `gateway.requests.total`: Total de requisições
- `gateway.requests.duration`: Duração das requisições
- `gateway.circuit.breaker.state`: Estado dos circuit breakers
- `gateway.rate.limit.exceeded`: Requisições bloqueadas por rate limit

### Dashboard de Rotas

```bash
curl http://localhost:8080/actuator/gateway/routes
```

## 🏃 Como Executar

### 1. Pré-requisitos

Certifique-se de que todos os microserviços estão rodando:

```bash
# Auth Service na porta 8083
# Word Service na porta 8081
# Label Service na porta 8082
```

### 2. Executar o Gateway

```bash
cd apigateway
mvn spring-boot:run
```

### 3. Testar o Gateway

```bash
# Health check
curl http://localhost:8080/actuator/health

# Login (rota pública)
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teste",
    "password": "Teste@123"
  }'

# Listar palavras (rota protegida)
curl http://localhost:8080/words \
  -H "Authorization: Bearer {token}"
```

## 🧪 Testes

```bash
# Testes unitários
mvn test

# Testes de integração
mvn verify

# Teste de carga (com Apache Bench)
ab -n 1000 -c 10 http://localhost:8080/words
```

## 🔍 Tratamento de Erros

### Erros Globais

O gateway padroniza todos os erros:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Erro ao processar requisição",
  "path": "/words/123",
  "traceId": "abc123def456"
}
```

### Códigos de Status

| Status | Descrição |
|--------|-----------|
| 200 | OK |
| 201 | Created |
| 204 | No Content |
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 409 | Conflict |
| 429 | Too Many Requests |
| 500 | Internal Server Error |
| 502 | Bad Gateway (serviço indisponível) |
| 503 | Service Unavailable |
| 504 | Gateway Timeout |

## 🛠️ Estrutura de Pacotes

```
com.palavras.etiquetas.apigateway
├── config/              # Configurações
│   ├── GatewayConfig    # Configuração de rotas
│   ├── SecurityConfig   # Configuração de segurança
│   └── CorsConfig       # Configuração CORS
├── filter/              # Filtros customizados
│   ├── AuthenticationFilter
│   ├── LoggingFilter
│   └── RateLimitFilter
├── fallback/            # Controllers de fallback
├── exception/           # Tratamento de exceções
└── util/                # Utilitários
```

## 📈 Performance

### Cache

```yaml
spring:
  cache:
    type: redis
    redis:
      time-to-live: 600000 # 10 minutos
```

### Compressão

```yaml
server:
  compression:
    enabled: true
    mime-types:
      - application/json
      - application/xml
      - text/html
      - text/plain
```

## 📝 Melhorias Futuras

- [ ] Service Discovery com Eureka/Consul
- [ ] Balanceamento de carga dinâmico
- [ ] Cache distribuído com Redis
- [ ] API versioning
- [ ] Request/Response transformation
- [ ] GraphQL gateway
- [ ] WebSocket support

## 🤝 Contribuindo

Veja o [README principal](../README.md) para instruções de contribuição.

---

**API Gateway** - Parte do Sistema de Microserviços de Palavras e Etiquetas
