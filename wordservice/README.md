# Word Service - Serviço de Palavras

## 📋 Descrição

O Word Service é um microserviço responsável por gerenciar todas as operações relacionadas a palavras no sistema. Ele fornece uma API RESTful para criar, ler, atualizar e deletar palavras, além de permitir buscas e filtros personalizados.

## 🎯 Responsabilidades

- Cadastro de novas palavras
- Consulta de palavras (individual e listagem)
- Atualização de informações de palavras
- Exclusão de palavras
- Busca e filtros de palavras
- Validação de dados de palavras
- Integração com o sistema de mensageria para eventos

## 🔧 Tecnologias

- **Java 21**
- **Spring Boot 3.4.1**
- **Spring Data JPA** (para persistência)
- **PostgreSQL** (banco de dados)
- **Spring AMQP** (integração com RabbitMQ)
- **Spring Validation** (validação de dados)

## 🗄️ Banco de Dados

### Configuração

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/word_db
spring.datasource.username=user
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Modelo de Dados

#### Tabela: `words`

| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | BIGINT | Identificador único (auto-incremento) |
| word | VARCHAR(255) | A palavra em si (único, não nulo) |
| definition | TEXT | Definição da palavra |
| created_at | TIMESTAMP | Data de criação |
| updated_at | TIMESTAMP | Data da última atualização |
| created_by | VARCHAR(100) | Usuário que criou |
| status | VARCHAR(20) | Status (ACTIVE, INACTIVE, DELETED) |

## 🚀 Endpoints da API

### Base URL
```
http://localhost:8081
```

### 1. Criar Palavra

**POST** `/api/words`

```json
{
  "word": "exemplo",
  "definition": "Aquilo que serve de modelo ou que pode ser imitado"
}
```

**Resposta (201 Created)**
```json
{
  "id": 1,
  "word": "exemplo",
  "definition": "Aquilo que serve de modelo ou que pode ser imitado",
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00",
  "status": "ACTIVE"
}
```

### 2. Listar Todas as Palavras

**GET** `/api/words`

**Parâmetros de Query:**
- `page` (opcional): número da página (padrão: 0)
- `size` (opcional): tamanho da página (padrão: 20)
- `sort` (opcional): campo de ordenação (padrão: word,asc)

**Resposta (200 OK)**
```json
{
  "content": [
    {
      "id": 1,
      "word": "exemplo",
      "definition": "Aquilo que serve de modelo",
      "status": "ACTIVE"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 20,
  "number": 0
}
```

### 3. Buscar Palavra por ID

**GET** `/api/words/{id}`

**Resposta (200 OK)**
```json
{
  "id": 1,
  "word": "exemplo",
  "definition": "Aquilo que serve de modelo ou que pode ser imitado",
  "createdAt": "2024-01-15T10:30:00",
  "status": "ACTIVE"
}
```

### 4. Atualizar Palavra

**PUT** `/api/words/{id}`

```json
{
  "word": "exemplo",
  "definition": "Definição atualizada"
}
```

**Resposta (200 OK)**
```json
{
  "id": 1,
  "word": "exemplo",
  "definition": "Definição atualizada",
  "updatedAt": "2024-01-15T11:00:00"
}
```

### 5. Deletar Palavra

**DELETE** `/api/words/{id}`

**Resposta (204 No Content)**

### 6. Buscar Palavras

**GET** `/api/words/search?q={termo}`

**Parâmetros:**
- `q`: termo de busca

**Resposta (200 OK)**
```json
[
  {
    "id": 1,
    "word": "exemplo",
    "definition": "Aquilo que serve de modelo"
  }
]
```

## 📨 Eventos e Mensageria

### Eventos Publicados

O Word Service publica os seguintes eventos no RabbitMQ:

#### 1. WordCreatedEvent
```json
{
  "eventType": "WORD_CREATED",
  "wordId": 1,
  "word": "exemplo",
  "timestamp": "2024-01-15T10:30:00",
  "userId": "user123"
}
```

#### 2. WordUpdatedEvent
```json
{
  "eventType": "WORD_UPDATED",
  "wordId": 1,
  "word": "exemplo",
  "changes": ["definition"],
  "timestamp": "2024-01-15T11:00:00"
}
```

#### 3. WordDeletedEvent
```json
{
  "eventType": "WORD_DELETED",
  "wordId": 1,
  "timestamp": "2024-01-15T12:00:00"
}
```

### Configuração do RabbitMQ

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

## 🏃 Como Executar

### 1. Pré-requisitos

Certifique-se de que o banco de dados PostgreSQL está rodando:

```bash
docker-compose up -d word-db
```

### 2. Configurar Variáveis de Ambiente (Opcional)

```bash
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/word_db
export SPRING_DATASOURCE_USERNAME=user
export SPRING_DATASOURCE_PASSWORD=password
```

### 3. Executar o Serviço

```bash
cd wordservice
mvn spring-boot:run
```

ou

```bash
mvn clean package
java -jar target/word-service-0.0.1-SNAPSHOT.jar
```

## 🧪 Testes

### Executar Todos os Testes

```bash
mvn test
```

### Executar Testes de Integração

```bash
mvn verify
```

### Cobertura de Testes

```bash
mvn test jacoco:report
```

O relatório estará disponível em: `target/site/jacoco/index.html`

## 📊 Health Check

O serviço expõe endpoints de health check do Spring Actuator:

```bash
# Health check
curl http://localhost:8081/actuator/health

# Informações do serviço
curl http://localhost:8081/actuator/info
```

## 🔍 Validações

### Regras de Validação

- **word**:
  - Não pode ser nulo ou vazio
  - Deve ter entre 1 e 255 caracteres
  - Deve ser único no sistema

- **definition**:
  - Opcional
  - Máximo de 5000 caracteres

### Exemplos de Erros

**400 Bad Request** - Dados inválidos
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Palavra não pode ser vazia",
  "path": "/api/words"
}
```

**404 Not Found** - Palavra não encontrada
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Palavra com ID 999 não encontrada",
  "path": "/api/words/999"
}
```

**409 Conflict** - Palavra duplicada
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Palavra 'exemplo' já existe no sistema",
  "path": "/api/words"
}
```

## 📈 Monitoramento

### Métricas Disponíveis

- Total de palavras cadastradas
- Taxa de requisições por segundo
- Tempo médio de resposta
- Taxa de erros
- Conexões de banco de dados ativas

### Logs

O serviço utiliza SLF4J com Logback. Configuração em `application.properties`:

```properties
logging.level.com.palavras.etiquetas.wordservice=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %msg%n
```

## 🔒 Segurança

- Integração com Auth Service para autenticação
- Validação de tokens JWT
- Rate limiting configurável
- CORS configurado

## 🛠️ Desenvolvimento

### Estrutura de Pacotes

```
com.palavras.etiquetas.wordservice
├── controller/     # Controllers REST
├── service/        # Lógica de negócio
├── repository/     # Acesso a dados
├── model/          # Entidades JPA
├── dto/            # Data Transfer Objects
├── event/          # Eventos e mensageria
├── exception/      # Tratamento de exceções
└── config/         # Configurações
```

## 📝 To-Do / Melhorias Futuras

- [ ] Implementar cache com Redis
- [ ] Adicionar suporte a busca por similaridade
- [ ] Implementar versionamento de palavras
- [ ] Adicionar suporte a múltiplos idiomas
- [ ] Implementar auditoria completa

## 🤝 Contribuindo

Veja o [README principal](../README.md) para instruções de contribuição.

---

**Word Service** - Parte do Sistema de Microserviços de Palavras e Etiquetas
