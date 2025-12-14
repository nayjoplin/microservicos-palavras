# Label Service - Serviço de Etiquetas

## 📋 Descrição

O Label Service é um microserviço responsável por gerenciar todas as operações relacionadas a etiquetas (tags) no sistema. Ele fornece uma API RESTful para criar, ler, atualizar e deletar etiquetas, além de gerenciar relacionamentos entre etiquetas e palavras.

## 🎯 Responsabilidades

- Cadastro de novas etiquetas
- Consulta de etiquetas (individual e listagem)
- Atualização de informações de etiquetas
- Exclusão de etiquetas
- Busca e filtros de etiquetas
- Gerenciamento de relacionamentos etiqueta-palavra
- Categorização e hierarquia de etiquetas
- Integração com sistema de mensageria

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
spring.datasource.url=jdbc:postgresql://localhost:5433/tag_db
spring.datasource.username=user
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Modelo de Dados

#### Tabela: `labels`

| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | BIGINT | Identificador único (auto-incremento) |
| name | VARCHAR(100) | Nome da etiqueta (único, não nulo) |
| description | TEXT | Descrição da etiqueta |
| color | VARCHAR(7) | Cor em hexadecimal (ex: #FF5733) |
| category | VARCHAR(50) | Categoria da etiqueta |
| parent_id | BIGINT | ID da etiqueta pai (para hierarquia) |
| created_at | TIMESTAMP | Data de criação |
| updated_at | TIMESTAMP | Data da última atualização |
| status | VARCHAR(20) | Status (ACTIVE, INACTIVE, DELETED) |

#### Tabela: `word_labels` (Relacionamento)

| Campo | Tipo | Descrição |
|-------|------|-----------|
| id | BIGINT | Identificador único |
| word_id | BIGINT | ID da palavra |
| label_id | BIGINT | ID da etiqueta |
| created_at | TIMESTAMP | Data de criação |
| created_by | VARCHAR(100) | Usuário que criou |

## 🚀 Endpoints da API

### Base URL
```
http://localhost:8082
```

### 1. Criar Etiqueta

**POST** `/api/labels`

```json
{
  "name": "substantivo",
  "description": "Classe gramatical que nomeia seres e objetos",
  "color": "#4CAF50",
  "category": "gramatica"
}
```

**Resposta (201 Created)**
```json
{
  "id": 1,
  "name": "substantivo",
  "description": "Classe gramatical que nomeia seres e objetos",
  "color": "#4CAF50",
  "category": "gramatica",
  "createdAt": "2024-01-15T10:30:00",
  "status": "ACTIVE"
}
```

### 2. Listar Todas as Etiquetas

**GET** `/api/labels`

**Parâmetros de Query:**
- `page` (opcional): número da página (padrão: 0)
- `size` (opcional): tamanho da página (padrão: 20)
- `category` (opcional): filtrar por categoria
- `sort` (opcional): campo de ordenação

**Resposta (200 OK)**
```json
{
  "content": [
    {
      "id": 1,
      "name": "substantivo",
      "description": "Classe gramatical",
      "color": "#4CAF50",
      "category": "gramatica"
    }
  ],
  "totalElements": 1,
  "totalPages": 1
}
```

### 3. Buscar Etiqueta por ID

**GET** `/api/labels/{id}`

**Resposta (200 OK)**
```json
{
  "id": 1,
  "name": "substantivo",
  "description": "Classe gramatical que nomeia seres e objetos",
  "color": "#4CAF50",
  "category": "gramatica",
  "wordCount": 150
}
```

### 4. Atualizar Etiqueta

**PUT** `/api/labels/{id}`

```json
{
  "name": "substantivo",
  "description": "Descrição atualizada",
  "color": "#2196F3"
}
```

**Resposta (200 OK)**

### 5. Deletar Etiqueta

**DELETE** `/api/labels/{id}`

**Resposta (204 No Content)**

### 6. Associar Etiqueta a Palavra

**POST** `/api/labels/{labelId}/words/{wordId}`

**Resposta (201 Created)**
```json
{
  "id": 1,
  "labelId": 1,
  "wordId": 5,
  "createdAt": "2024-01-15T10:30:00"
}
```

### 7. Remover Associação

**DELETE** `/api/labels/{labelId}/words/{wordId}`

**Resposta (204 No Content)**

### 8. Listar Palavras de uma Etiqueta

**GET** `/api/labels/{id}/words`

**Resposta (200 OK)**
```json
[
  {
    "wordId": 5,
    "word": "exemplo",
    "assignedAt": "2024-01-15T10:30:00"
  }
]
```

### 9. Buscar Etiquetas por Categoria

**GET** `/api/labels/category/{category}`

**Resposta (200 OK)**
```json
[
  {
    "id": 1,
    "name": "substantivo",
    "category": "gramatica"
  }
]
```

### 10. Criar Hierarquia de Etiquetas

**POST** `/api/labels/{parentId}/children`

```json
{
  "name": "substantivo-proprio",
  "description": "Substantivo que nomeia seres específicos"
}
```

## 📨 Eventos e Mensageria

### Eventos Publicados

#### 1. LabelCreatedEvent
```json
{
  "eventType": "LABEL_CREATED",
  "labelId": 1,
  "name": "substantivo",
  "timestamp": "2024-01-15T10:30:00"
}
```

#### 2. LabelAssignedEvent
```json
{
  "eventType": "LABEL_ASSIGNED",
  "labelId": 1,
  "wordId": 5,
  "timestamp": "2024-01-15T10:30:00"
}
```

#### 3. LabelRemovedEvent
```json
{
  "eventType": "LABEL_REMOVED",
  "labelId": 1,
  "wordId": 5,
  "timestamp": "2024-01-15T11:00:00"
}
```

### Eventos Consumidos

O Label Service consome eventos do Word Service:
- `WordDeletedEvent`: Remove todas as associações quando uma palavra é deletada

### Configuração do RabbitMQ

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest

# Exchanges e Queues
rabbitmq.exchange.labels=labels.exchange
rabbitmq.queue.label-events=label.events.queue
```

## 🏃 Como Executar

### 1. Pré-requisitos

```bash
# Subir banco de dados
docker-compose up -d tag-db

# Subir RabbitMQ
docker-compose up -d rabbitmq
```

### 2. Executar o Serviço

```bash
cd labelservice
mvn spring-boot:run
```

## 🧪 Testes

```bash
# Testes unitários
mvn test

# Testes de integração
mvn verify

# Cobertura
mvn test jacoco:report
```

## 🔍 Validações

### Regras de Validação

- **name**:
  - Não pode ser nulo ou vazio
  - Entre 1 e 100 caracteres
  - Deve ser único
  - Somente letras, números e hífens

- **color**:
  - Formato hexadecimal (#RRGGBB)
  - Opcional (padrão: #808080)

- **category**:
  - Máximo 50 caracteres
  - Opcional

### Categorias Pré-definidas

- `gramatica`: Classes gramaticais
- `semantica`: Aspectos semânticos
- `origem`: Etimologia e origem
- `uso`: Contexto de uso
- `custom`: Categorias personalizadas

## 📊 Estatísticas

### Endpoint de Estatísticas

**GET** `/api/labels/stats`

```json
{
  "totalLabels": 50,
  "totalAssociations": 500,
  "categoriesCount": {
    "gramatica": 15,
    "semantica": 10,
    "origem": 8
  },
  "mostUsedLabels": [
    {
      "id": 1,
      "name": "substantivo",
      "usageCount": 150
    }
  ]
}
```

## 🎨 Cores e Categorias

### Cores Sugeridas por Categoria

- **Gramática**: Verde (#4CAF50)
- **Semântica**: Azul (#2196F3)
- **Origem**: Laranja (#FF9800)
- **Uso**: Roxo (#9C27B0)

## 🔒 Segurança

- Autenticação via Auth Service
- Autorização baseada em papéis
- Validação de propriedade de etiquetas
- Rate limiting

## 🛠️ Estrutura de Pacotes

```
com.palavras.etiquetas.labelservice
├── controller/        # Controllers REST
├── service/          # Lógica de negócio
├── repository/       # Acesso a dados
├── model/            # Entidades JPA
├── dto/              # Data Transfer Objects
├── event/            # Eventos e mensageria
│   ├── publisher/    # Publicadores de eventos
│   └── consumer/     # Consumidores de eventos
├── exception/        # Tratamento de exceções
├── config/           # Configurações
└── util/             # Utilitários
```

## 📈 Monitoramento

### Health Check

```bash
curl http://localhost:8082/actuator/health
```

### Métricas

- Total de etiquetas por categoria
- Taxa de associações/desassociações
- Etiquetas mais utilizadas
- Performance de queries

## 📝 Melhorias Futuras

- [ ] Sistema de sugestão de etiquetas
- [ ] Detecção automática de etiquetas similares
- [ ] Merge de etiquetas duplicadas
- [ ] Importação/exportação em massa
- [ ] API de auto-complete para busca

## 🤝 Contribuindo

Veja o [README principal](../README.md) para instruções de contribuição.

---

**Label Service** - Parte do Sistema de Microserviços de Palavras e Etiquetas
