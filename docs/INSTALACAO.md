# Guia de Instalação Detalhado

## 📋 Índice

1. [Requisitos do Sistema](#requisitos-do-sistema)
2. [Instalação do Java](#instalação-do-java)
3. [Instalação do Maven](#instalação-do-maven)
4. [Instalação do Docker](#instalação-do-docker)
5. [Configuração do Ambiente](#configuração-do-ambiente)
6. [Instalação dos Microserviços](#instalação-dos-microserviços)
7. [Verificação da Instalação](#verificação-da-instalação)
8. [Solução de Problemas](#solução-de-problemas)

## 📦 Requisitos do Sistema

### Requisitos Mínimos

- **Sistema Operacional**: Linux, macOS ou Windows 10/11
- **RAM**: 8 GB (recomendado: 16 GB)
- **Disco**: 10 GB de espaço livre
- **Processador**: Dual-core 2.0 GHz ou superior

### Software Necessário

- Java Development Kit (JDK) 21
- Apache Maven 3.6+
- Docker 20.10+
- Docker Compose 2.0+
- Git 2.30+
- Editor de código (VS Code, IntelliJ IDEA, etc.)

## ☕ Instalação do Java

### Linux (Ubuntu/Debian)

```bash
# Atualizar repositórios
sudo apt update

# Instalar OpenJDK 21
sudo apt install openjdk-21-jdk -y

# Verificar instalação
java -version
javac -version
```

### Linux (Fedora/RHEL)

```bash
# Instalar OpenJDK 21
sudo dnf install java-21-openjdk java-21-openjdk-devel -y

# Verificar instalação
java -version
```

### macOS

```bash
# Usando Homebrew
brew install openjdk@21

# Adicionar ao PATH
echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Verificar instalação
java -version
```

### Windows

1. Baixar o instalador do OpenJDK 21 de: https://adoptium.net/
2. Executar o instalador
3. Adicionar JAVA_HOME às variáveis de ambiente:
   - Variável: `JAVA_HOME`
   - Valor: `C:\Program Files\Eclipse Adoptium\jdk-21.x.x`
4. Adicionar ao PATH: `%JAVA_HOME%\bin`
5. Verificar no CMD: `java -version`

### Configurar JAVA_HOME

**Linux/macOS:**
```bash
# Adicionar ao ~/.bashrc ou ~/.zshrc
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH=$JAVA_HOME/bin:$PATH

# Recarregar configuração
source ~/.bashrc  # ou source ~/.zshrc
```

**Windows:**
```cmd
setx JAVA_HOME "C:\Program Files\Eclipse Adoptium\jdk-21.x.x"
setx PATH "%PATH%;%JAVA_HOME%\bin"
```

## 🔨 Instalação do Maven

### Linux (Ubuntu/Debian)

```bash
# Instalar Maven
sudo apt install maven -y

# Verificar instalação
mvn -version
```

### macOS

```bash
# Usando Homebrew
brew install maven

# Verificar instalação
mvn -version
```

### Windows

1. Baixar Maven de: https://maven.apache.org/download.cgi
2. Extrair para `C:\Program Files\Apache\maven`
3. Adicionar variável de ambiente:
   - Variável: `MAVEN_HOME`
   - Valor: `C:\Program Files\Apache\maven`
4. Adicionar ao PATH: `%MAVEN_HOME%\bin`
5. Verificar: `mvn -version`

### Configurar Maven (Opcional)

Editar `~/.m2/settings.xml` (Linux/macOS) ou `C:\Users\{username}\.m2\settings.xml` (Windows):

```xml
<settings>
  <localRepository>${user.home}/.m2/repository</localRepository>
  <mirrors>
    <!-- Mirror brasileiro para melhor performance -->
    <mirror>
      <id>central-br</id>
      <url>https://repo1.maven.org/maven2</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
  </mirrors>
</settings>
```

## 🐳 Instalação do Docker

### Linux (Ubuntu/Debian)

```bash
# Remover versões antigas
sudo apt remove docker docker-engine docker.io containerd runc

# Instalar dependências
sudo apt update
sudo apt install ca-certificates curl gnupg lsb-release -y

# Adicionar chave GPG do Docker
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

# Adicionar repositório
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# Instalar Docker
sudo apt update
sudo apt install docker-ce docker-ce-cli containerd.io docker-compose-plugin -y

# Adicionar usuário ao grupo docker (evita usar sudo)
sudo usermod -aG docker $USER

# Recarregar grupos (ou fazer logout/login)
newgrp docker

# Verificar instalação
docker --version
docker compose version
```

### macOS

```bash
# Baixar Docker Desktop de:
# https://www.docker.com/products/docker-desktop/

# Ou usando Homebrew
brew install --cask docker

# Verificar instalação
docker --version
docker compose version
```

### Windows

1. Baixar Docker Desktop de: https://www.docker.com/products/docker-desktop/
2. Executar o instalador
3. Reiniciar o computador
4. Iniciar Docker Desktop
5. Verificar no PowerShell: `docker --version`

### Testar Docker

```bash
# Executar container de teste
docker run hello-world

# Deve exibir mensagem de sucesso
```

## 🔧 Configuração do Ambiente

### 1. Clonar o Repositório

```bash
# HTTPS
git clone https://github.com/seu-usuario/microservicos-palavras.git

# SSH
git clone git@github.com:seu-usuario/microservicos-palavras.git

# Entrar no diretório
cd microservicos-palavras
```

### 2. Configurar Variáveis de Ambiente

Criar arquivo `.env` na raiz do projeto:

```bash
# Banco de Dados
POSTGRES_USER=user
POSTGRES_PASSWORD=password

# JWT Secret (use uma chave forte em produção!)
JWT_SECRET=sua-chave-secreta-super-segura-mude-em-producao

# Portas dos Serviços
API_GATEWAY_PORT=8080
WORD_SERVICE_PORT=8081
LABEL_SERVICE_PORT=8082
AUTH_SERVICE_PORT=8083

# RabbitMQ
RABBITMQ_USER=guest
RABBITMQ_PASS=guest
```

### 3. Configurar application.properties (Opcional)

Cada microserviço pode ter configurações específicas em:
- `wordservice/src/main/resources/application.properties`
- `labelservice/src/main/resources/application.properties`
- `authservice/src/main/resources/application.properties`
- `apigateway/src/main/resources/application.properties`

Exemplo de configuração personalizada:

```properties
# application.properties
server.port=8081
spring.datasource.url=jdbc:postgresql://localhost:5432/word_db
spring.datasource.username=${DB_USER:user}
spring.datasource.password=${DB_PASSWORD:password}
```

## 🚀 Instalação dos Microserviços

### Passo 1: Subir a Infraestrutura

```bash
# Subir todos os containers em background
docker-compose up -d

# Verificar status
docker-compose ps

# Ver logs
docker-compose logs -f
```

**Containers que devem estar rodando:**
- word-db (PostgreSQL)
- tag-db (PostgreSQL)
- auth-db (PostgreSQL)
- relationship-db (PostgreSQL)
- rabbitmq

### Passo 2: Verificar Bancos de Dados

```bash
# Conectar ao banco do Word Service
docker exec -it microservicos-palavras-word-db-1 psql -U user -d word_db

# Dentro do psql:
\dt  # Listar tabelas (pode estar vazio inicialmente)
\q   # Sair

# Conectar ao banco do Label Service
docker exec -it microservicos-palavras-tag-db-1 psql -U user -d tag_db

# Conectar ao banco do Auth Service
docker exec -it microservicos-palavras-auth-db-1 psql -U user -d auth_db
```

### Passo 3: Compilar os Microserviços

#### Compilação Completa (Todos os Serviços)

```bash
# Na raiz do projeto
mvn clean install -DskipTests

# Com testes
mvn clean install
```

#### Compilação Individual

```bash
# Word Service
cd wordservice
mvn clean package
cd ..

# Label Service
cd labelservice
mvn clean package
cd ..

# Auth Service
cd authservice
mvn clean package
cd ..

# API Gateway
cd apigateway
mvn clean package
cd ..
```

### Passo 4: Executar os Microserviços

#### Opção 1: Executar com Maven (Desenvolvimento)

Abra 4 terminais diferentes:

**Terminal 1 - Auth Service:**
```bash
cd authservice
mvn spring-boot:run
```

**Terminal 2 - Word Service:**
```bash
cd wordservice
mvn spring-boot:run
```

**Terminal 3 - Label Service:**
```bash
cd labelservice
mvn spring-boot:run
```

**Terminal 4 - API Gateway:**
```bash
cd apigateway
mvn spring-boot:run
```

#### Opção 2: Executar JARs Compilados

```bash
# Auth Service
java -jar authservice/target/auth-service-0.0.1-SNAPSHOT.jar &

# Word Service
java -jar wordservice/target/word-service-0.0.1-SNAPSHOT.jar &

# Label Service
java -jar labelservice/target/label-service-0.0.1-SNAPSHOT.jar &

# API Gateway
java -jar apigateway/target/api-gateway-0.0.1-SNAPSHOT.jar &
```

#### Opção 3: Script de Inicialização

Criar arquivo `start-services.sh`:

```bash
#!/bin/bash

echo "Iniciando microserviços..."

# Cores para output
GREEN='\033[0;32m'
NC='\033[0m' # No Color

# Auth Service
echo -e "${GREEN}Iniciando Auth Service...${NC}"
cd authservice && mvn spring-boot:run > logs/auth-service.log 2>&1 &
AUTH_PID=$!

# Word Service
echo -e "${GREEN}Iniciando Word Service...${NC}"
cd ../wordservice && mvn spring-boot:run > logs/word-service.log 2>&1 &
WORD_PID=$!

# Label Service
echo -e "${GREEN}Iniciando Label Service...${NC}"
cd ../labelservice && mvn spring-boot:run > logs/label-service.log 2>&1 &
LABEL_PID=$!

# API Gateway
echo -e "${GREEN}Iniciando API Gateway...${NC}"
cd ../apigateway && mvn spring-boot:run > logs/api-gateway.log 2>&1 &
GATEWAY_PID=$!

echo "Todos os serviços foram iniciados!"
echo "PIDs: Auth=$AUTH_PID Word=$WORD_PID Label=$LABEL_PID Gateway=$GATEWAY_PID"
```

Tornar executável e rodar:
```bash
chmod +x start-services.sh
./start-services.sh
```

## ✅ Verificação da Instalação

### 1. Verificar Health Checks

```bash
# API Gateway
curl http://localhost:8080/actuator/health

# Auth Service
curl http://localhost:8083/actuator/health

# Word Service
curl http://localhost:8081/actuator/health

# Label Service
curl http://localhost:8082/actuator/health
```

**Resposta esperada:**
```json
{
  "status": "UP"
}
```

### 2. Testar Fluxo Completo

```bash
# 1. Registrar usuário
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teste",
    "email": "teste@example.com",
    "password": "Teste@123",
    "firstName": "Usuário",
    "lastName": "Teste"
  }'

# 2. Fazer login
TOKEN=$(curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "teste",
    "password": "Teste@123"
  }' | jq -r '.accessToken')

echo "Token: $TOKEN"

# 3. Criar uma palavra
curl -X POST http://localhost:8080/words \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "word": "teste",
    "definition": "Palavra de teste"
  }'

# 4. Listar palavras
curl http://localhost:8080/words \
  -H "Authorization: Bearer $TOKEN"

# 5. Criar uma etiqueta
curl -X POST http://localhost:8080/labels \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "name": "substantivo",
    "description": "Classe gramatical",
    "color": "#4CAF50"
  }'
```

### 3. Verificar RabbitMQ

```bash
# Acessar painel web
# URL: http://localhost:15672
# Usuário: guest
# Senha: guest
```

### 4. Verificar Logs

```bash
# Logs do Docker Compose
docker-compose logs -f

# Logs de um serviço específico
docker-compose logs -f word-db
docker-compose logs -f rabbitmq

# Logs dos microserviços (se usando script)
tail -f logs/auth-service.log
tail -f logs/word-service.log
```

## 🔍 Solução de Problemas

### Problema: Porta já em uso

**Erro:**
```
Port 8080 is already in use
```

**Solução:**
```bash
# Encontrar processo usando a porta
lsof -i :8080  # Linux/macOS
netstat -ano | findstr :8080  # Windows

# Matar o processo
kill -9 <PID>  # Linux/macOS
taskkill /PID <PID> /F  # Windows

# Ou mudar a porta no application.properties
server.port=8090
```

### Problema: Erro de conexão com banco de dados

**Erro:**
```
Connection refused: localhost:5432
```

**Solução:**
```bash
# Verificar se containers estão rodando
docker-compose ps

# Reiniciar containers
docker-compose restart word-db

# Ver logs do banco
docker-compose logs word-db

# Verificar se o banco está aceitando conexões
docker exec -it microservicos-palavras-word-db-1 pg_isready -U user
```

### Problema: Erro de compilação Maven

**Erro:**
```
Failed to execute goal
```

**Solução:**
```bash
# Limpar cache do Maven
mvn clean

# Atualizar dependências
mvn dependency:purge-local-repository

# Compilar novamente
mvn clean install -U
```

### Problema: Java version incompatível

**Erro:**
```
Unsupported class file major version
```

**Solução:**
```bash
# Verificar versão do Java
java -version

# Se não for Java 21, instalar a versão correta
# Ver seção de instalação do Java acima

# Verificar JAVA_HOME
echo $JAVA_HOME
```

### Problema: Docker sem permissão (Linux)

**Erro:**
```
permission denied while trying to connect to the Docker daemon
```

**Solução:**
```bash
# Adicionar usuário ao grupo docker
sudo usermod -aG docker $USER

# Reiniciar sessão ou executar
newgrp docker

# Testar
docker ps
```

### Problema: RabbitMQ não conecta

**Erro:**
```
Connection refused: localhost:5672
```

**Solução:**
```bash
# Verificar se RabbitMQ está rodando
docker-compose ps rabbitmq

# Ver logs
docker-compose logs rabbitmq

# Reiniciar RabbitMQ
docker-compose restart rabbitmq

# Aguardar inicialização (pode levar ~30s)
sleep 30
```

### Logs de Debug

Ativar logs detalhados em `application.properties`:

```properties
logging.level.root=INFO
logging.level.com.palavras.etiquetas=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

## 📞 Suporte

Se os problemas persistirem:

1. Verificar logs completos de todos os serviços
2. Verificar conectividade de rede
3. Confirmar versões de software
4. Abrir issue no repositório com:
   - Descrição do erro
   - Logs relevantes
   - Versões de software
   - Sistema operacional

## 🎉 Próximos Passos

Após instalação bem-sucedida:

1. Ler a [Documentação de Arquitetura](./ARQUITETURA.md)
2. Explorar os endpoints das APIs
3. Configurar ambiente de desenvolvimento
4. Contribuir com o projeto

---

**Guia de Instalação** - Sistema de Microserviços de Palavras e Etiquetas
