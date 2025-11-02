# CLAUDE.md

Este arquivo fornece orientação ao Claude Code (claude.ai/code) ao trabalhar com código neste repositório.

## Visão Geral do Projeto

Este é um curso de aprendizado progressivo sobre observabilidade OpenTelemetry para Java/Spring Boot. Demonstra como instrumentar microsserviços para coleta de rastreamento, métricas e logs. Cada módulo numerado (01-08) desenvolve conceitos anteriores, com infraestrutura de suporte usando Grafana, Tempo e Prometheus.

## Comandos de Build & Desenvolvimento

### Comandos Maven

```bash
# Compilar todos os módulos e gerar JARs
mvn clean package

# Compilar módulo específico
mvn clean package -f 01-trace-flix/pom.xml

# Executar testes de módulo específico
mvn test -f 07-opentelemetry-spring-boot-starter/pom.xml

# Pular testes durante a compilação
mvn clean package -DskipTests
```

### Comandos Docker & Container

```bash
# Compilar imagens e iniciar todos os serviços (do diretório do módulo)
docker compose up --build

# Iniciar serviços sem recompilar
docker compose up

# Parar e remover containers
docker compose down

# Visualizar logs de serviço específico
docker compose logs -f movie-service
```

### Fluxo de Trabalho Exemplo para Cada Módulo

```bash
cd 02-distributed-tracing
mvn clean package
docker compose up --build
# Aguarde 30s para que os serviços estejam saudáveis
curl http://localhost:8080/api/movies/1
# Abrir Grafana: http://localhost:3000 (admin/admin)
# Visualizar rastreamentos no Grafana sob a fonte de dados Tempo
docker compose down
```

## Tecnologias & Versões Principais

- **Java:** 24-25 (versão alvo no pom.xml)
- **Spring Boot:** 3.5.4 - 3.5.6
- **OpenTelemetry:** 1.52.0 (API/SDK), 2.20.1 (Spring Boot Starter)
- **Infraestrutura:** Grafana 12.0, Tempo 2.8.2, Prometheus 3.5.0, OTel Collector 0.133.0
- **Ferramenta de Build:** Maven 3.x
- **Runtime de Container:** Docker com Docker Compose

## Estrutura do Repositório & Arquitetura

### Organização dos Módulos

| Módulo | Propósito | Conceitos Principais |
|--------|----------|---------------------|
| **01-trace-flix** | Aplicação de microsserviços fundamental | 3 serviços (filme, ator, crítica), chamadas HTTP básicas, BD H2 |
| **02-distributed-tracing** | Coleta & visualização de rastreamentos | OTel Collector, backend Tempo, UI Grafana, propagação de rastreamento |
| **03-sampling-strategies** | 7 abordagens de amostragem diferentes | Baseada em cabeçalho (sempre-on/off, ratio), baseada em cauda (status, latência, atributos) |
| **04-metrics** | Coleta de métricas Prometheus | Atributos estruturados, contadores personalizados, consultas PromQL |
| **05-logs** | Integração de logging estruturado | Propagação de contexto de rastreamento para logs, correlação de logs |
| **06-manual-instrumentation** | Uso direto do SDK OTel | Java puro (sem Spring), API de Span, atributos personalizados |
| **07-spring-boot-starter** | Integração pronta para produção | Decorador @WithSpan, auto-config Spring, recursos avançados |
| **08-on-demand-observability** | Capacidades de debugging dinâmico | OnDemandObservabilityConfig, filtragem de logs em tempo de execução |

### Arquitetura de Microsserviços (Trace-Flix)

**Três serviços Spring Boot com interconexões HTTP:**
- **movie-service** (porta 8080) - Ponto de entrada, chama actor-service & review-service
- **actor-service** (porta 8081) - API de detalhes de atores
- **review-service** (porta 8082) - API de detalhes de críticas

Cada serviço possui:
- Controlador REST tratando endpoints `/api/*`
- Banco de dados H2 em memória com dados de amostra
- Clientes HTTP usando RestClient do Spring
- Instrumentação OTel (agente automático ou manual)

**Dados de Teste Observáveis:**
- IDs de filme 1-7: Respostas rápidas (observe rastreamentos normais)
- IDs de filme 8-9: Atrasos simulados (observe padrões de latência)
- ID de filme 10: Sempre falha com erro 500 (observe rastreamentos de erro)

### Padrões de Integração de Observabilidade

**Instrumentação sem Código:**
- Agente Java OpenTelemetry (auto-configura a partir do ambiente)
- Variáveis de ambiente controlam: nome do serviço, endpoint do exportador, exportadores a ativar

**Instrumentação Manual:**
- Anotação `@WithSpan` em métodos (Spring Boot Starter)
- API `Span.current()` para atributos personalizados de span
- `LongCounter` para métricas estruturadas com atributos
- Appender Logback para contexto de rastreamento em logs

**Propagação de Rastreamento:**
- Cabeçalhos W3C Trace Context automáticos em chamadas HTTP
- Relacionamentos pai-filho de spans mantidos entre serviços

## Tarefas Comuns de Desenvolvimento

### Testando Observabilidade

1. **Visualizar Rastreamentos:**
   - Abrir Grafana em http://localhost:3000
   - Navegar para Explore > Selecionar fonte de dados Tempo
   - Pesquisar por nome do serviço ou ID do rastreamento

2. **Testar Serviço de Filmes:**
   ```bash
   curl http://localhost:8080/api/movies/1      # Sucesso
   curl http://localhost:8080/api/movies/10     # Caso de erro
   ```

3. **Visualizar Métricas Prometheus:**
   - Abrir http://localhost:9090
   - Consulta: `rate(movie_view_count_total[1m])`

4. **Usar Coleção Postman:**
   - Coleção disponível em `postman/collections/`
   - Requisições pré-configuradas para todos os módulos

### Dicas Específicas de Módulos

**03-Sampling Strategies:**
- Cada subdiretório é autossuficiente com seu próprio docker-compose
- Demonstra trade-offs entre amostragem baseada em cabeçalho vs cauda
- Compare rastreamentos no Grafana para ver quais spans são amostrados

**07-Spring Boot Starter:**
- Configuração padrão auto-ativa todos os exportadores (rastreamentos, métricas, logs)
- Use `management.otlp.tracing.endpoint` para apontar para o coletor
- `@WithSpan` é a forma preferida de adicionar spans personalizados no Spring

**08-On-Demand Observability:**
- Caso de uso avançado para debugging em produção sem alterar código
- OnDemandObservabilityConfig ativa mudanças dinâmicas de nível de log em tempo de execução
- Útil para amostragem baseada em cauda de rastreamentos de erro

## Referência de Arquivos de Configuração

### Configuração OTel

- **opentelemetry-config.properties** - Propriedades de inicialização do agente Java (nome do serviço, exportadores)
- **collector-config.yaml** - Receptores/processadores/exportadores do OTel Collector
- **prometheus.yaml** - Endpoints de raspagem Prometheus
- **tempo.yaml** - Armazenamento de rastreamento backend Tempo (receptores OTLP nas portas 4317/4318)

### Configuração de Aplicação

- **application.properties** - Configurações de app Spring Boot (porta, nível de logging)
- **docker-compose.yaml** - Orquestração de serviços por módulo
- **Dockerfile** - Build multi-estágio usando Eclipse Temurin Java 25

### Variáveis de Ambiente Principais

```
OTEL_SERVICE_NAME=movie-service              # Identificação do serviço
OTEL_EXPORTER_OTLP_ENDPOINT=http://collector:4317  # Endpoint exportador OTLP
OTEL_TRACES_EXPORTER=otlp                    # Ativar exportação de rastreamentos
OTEL_METRICS_EXPORTER=prometheus             # Ativar exportação de métricas
OTEL_LOGS_EXPORTER=otlp                      # Ativar exportação de logs
```

## Conceitos de Observabilidade por Módulo

### Módulos 01-02: Fundamentos de Rastreamento
- Composição de rastreamento distribuído (spans pai/filho)
- Propagação de contexto de rastreamento entre serviços
- Armazenamento de backend (Tempo) e visualização (Grafana)

### Módulo 03: Trade-offs de Estratégia de Amostragem
- Amostragem baseada em cabeçalho: Rápida, mas pode perder erros
- Amostragem baseada em cauda: Melhor captura de erros, mas requer buffer
- Decisões de amostragem baseadas em atributos de rastreamento, latência, status HTTP

### Módulo 04: Métricas & Atributos Estruturados
- Métricas de contador com cardinalidade (serviço, endpoint, status)
- Raspagem do Prometheus e consultas PromQL
- Contexto estrutural em métricas (evitar cardinalidade ilimitada)

### Módulo 05: Correlação Entre Sinais
- ID de rastreamento em registros de log estruturado
- Informações contextuais em logs (serviço, ID de span)
- Alternância entre rastreamentos e logs no Grafana

### Módulos 06-07: Abordagens de Instrumentação
- Uso manual do SDK vs convenções do Spring Boot starter
- Decorador @WithSpan para criação automática de spans
- Atributos e eventos de span personalizados

### Módulo 08: Capacidades de Produção
- Observabilidade sob demanda sem mudanças de código
- Filtragem dinâmica para dados de alta cardinalidade
- Otimização de custos para implantações em larga escala

## Notas sobre o Design do Curso

- Este é um **curso educacional**, não uma implantação de produção
- Todos os bancos de dados estão em memória (H2), dados são redefinidos ao reiniciar
- Credenciais Grafana: admin/admin (mudar em produção)
- Docker Compose é usado para desenvolvimento local, não recomendado para produção
- Cada módulo é executável independentemente (docker-compose autossuficiente)
- Pré-requisitos: Docker, Docker Compose, Java 24+, Maven 3.x

## Problemas Importantes

1. **Conflitos de Porta:** Cada módulo usa diferentes intervalos de portas. Verifique docker-compose.yaml se os containers falharem ao iniciar.
2. **Configuração do Tempo:** Os endpoints do distribuidor OTLP devem ser `0.0.0.0` (não `tempo:4317`) para aceitar conexões externas.
3. **Agente Java:** Ao usar o agente Java, certifique-se de que está no classpath antes do JAR da aplicação.
4. **Spring Boot Starter:** Auto-configuração é opinionada. Verifique `application.properties` para substituições.
5. **Propagação de Rastreamento:** Requer configuração explícita do cliente HTTP ou auto-instrumentação via agente Java.
