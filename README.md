# Sistema de Microserviços de Palavras e Etiquetas

## 📋 Descrição

Sistema distribuído desenvolvido em Spring Boot para gerenciar palavras, etiquetas (tags) e seus relacionamentos. O projeto implementa uma arquitetura de microserviços com comunicação assíncrona via RabbitMQ e armazenamento distribuído em PostgreSQL.

## 🏗️ Arquitetura

O sistema é composto por 4 microserviços principais:

### 1. **Word Service** (Serviço de Palavras)
- Gerencia o cadastro e manipulação de palavras
- Banco de dados: PostgreSQL (porta 5432)
- Responsável por operações CRUD de palavras

### 2. **Label Service** (Serviço de Etiquetas)
- Gerencia o cadastro e manipulação de etiquetas/tags
- Banco de dados: PostgreSQL (porta 5433)
- Responsável por operações CRUD de etiquetas

### 3. **Auth Service** (Serviço de Autenticação)
- Gerencia autenticação e autorização de usuários
- Banco de dados: PostgreSQL (porta 5435)
- Responsável pela segurança do sistema

### 4. **API Gateway** (Gateway de APIs)
- Ponto único de entrada para todos os serviços
- Roteia requisições para os microserviços apropriados
- Gerencia políticas de segurança e rate limiting

### Infraestrutura

- **RabbitMQ**: Sistema de mensageria para comunicação assíncrona entre serviços
  - Porta AMQP: 5672
  - Painel de gerenciamento: 15672
- **PostgreSQL**: Bancos de dados separados para cada serviço (princípio de isolamento de dados)

## 🛠️ Tecnologias Utilizadas

- **Java 21**: Linguagem de programação
- **Spring Boot 3.4.1**: Framework principal
- **Spring Web**: Para construção de APIs RESTful
- **PostgreSQL 16**: Banco de dados relacional
- **RabbitMQ 3**: Sistema de mensageria
- **Docker & Docker Compose**: Containerização e orquestração
- **Maven**: Gerenciamento de dependências

## 📦 Pré-requisitos

- Java 21 ou superior
- Maven 3.6+
- Docker e Docker Compose
- Git

## 🚀 Instalação e Execução

### 1. Clonar o Repositório

```bash
git clone <url-do-repositorio>
cd microservicos-palavras
```

### 2. Subir a Infraestrutura com Docker

```bash
docker-compose up -d
```

Este comando irá iniciar:
- 4 instâncias do PostgreSQL (uma para cada serviço)
- 1 instância do RabbitMQ com painel de gerenciamento

### 3. Verificar os Containers

```bash
docker-compose ps
```

Você deve ver todos os containers em execução:
- word-db
- tag-db
- auth-db
- relationship-db (se houver)
- rabbitmq

### 4. Compilar os Microserviços

```bash
# Compilar todos os serviços
mvn clean install

# Ou compilar individualmente
cd wordservice && mvn clean install
cd ../labelservice && mvn clean install
cd ../authservice && mvn clean install
cd ../apigateway && mvn clean install
```

### 5. Executar os Microserviços

```bash
# Word Service
cd wordservice
mvn spring-boot:run

# Label Service (em outro terminal)
cd labelservice
mvn spring-boot:run

# Auth Service (em outro terminal)
cd authservice
mvn spring-boot:run

# API Gateway (em outro terminal)
cd apigateway
mvn spring-boot:run
```

## 🔧 Configuração

### Portas Padrão

| Serviço | Porta |
|---------|-------|
| API Gateway | (a definir) |
| Word Service | (a definir) |
| Label Service | (a definir) |
| Auth Service | (a definir) |
| PostgreSQL (Word DB) | 5432 |
| PostgreSQL (Label DB) | 5433 |
| PostgreSQL (Auth DB) | 5435 |
| PostgreSQL (Relationship DB) | 5434 |
| RabbitMQ AMQP | 5672 |
| RabbitMQ Management | 15672 |

### Variáveis de Ambiente

Cada serviço pode ser configurado através de variáveis de ambiente ou arquivo `application.properties`:

```properties
# Exemplo de configuração de banco de dados
spring.datasource.url=jdbc:postgresql://localhost:5432/word_db
spring.datasource.username=user
spring.datasource.password=password
```

## 📚 Documentação Adicional

- [Arquitetura do Sistema](./docs/ARQUITETURA.md)
- [Guia de Instalação Detalhado](./docs/INSTALACAO.md)
- [API do Word Service](./wordservice/README.md)
- [API do Label Service](./labelservice/README.md)
- [API do Auth Service](./authservice/README.md)
- [Configuração do API Gateway](./apigateway/README.md)

## 🧪 Testes

### Executar todos os testes

```bash
mvn test
```

### Executar testes de um serviço específico

```bash
cd wordservice
mvn test
```

## 🐰 Acessar o Painel do RabbitMQ

1. Acesse: http://localhost:15672
2. Usuário: `guest`
3. Senha: `guest`

## 📊 Monitoramento

O painel do RabbitMQ fornece informações sobre:
- Filas de mensagens
- Exchanges
- Conexões ativas
- Taxa de mensagens
- Estatísticas de consumo

## 🗄️ Banco de Dados

### Conectar aos Bancos de Dados

```bash
# Word Database
psql -h localhost -p 5432 -U user -d word_db

# Label Database
psql -h localhost -p 5433 -U user -d tag_db

# Auth Database
psql -h localhost -p 5435 -U user -d auth_db

# Relationship Database
psql -h localhost -p 5434 -U user -d relationship_db
```

Senha para todos: `password`

## 🔄 Parar os Serviços

### Parar containers Docker

```bash
docker-compose down
```

### Parar e remover volumes (apaga os dados)

```bash
docker-compose down -v
```

## 🤝 Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/nova-funcionalidade`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova funcionalidade'`)
4. Push para a branch (`git push origin feature/nova-funcionalidade`)
5. Abra um Pull Request

## 📝 Licença

Este projeto é um desafio de desenvolvimento de sistema distribuído.

## 👥 Autores

Projeto desenvolvido como desafio técnico de sistema distribuído de palavras, etiquetas e relacionamentos.

## 📧 Suporte

Para questões e suporte, abra uma issue no repositório.

---

**Nota**: Este é um projeto em desenvolvimento. Algumas funcionalidades podem estar em construção.
