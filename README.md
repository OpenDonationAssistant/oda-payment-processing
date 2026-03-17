# ODA Payment Processing
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/OpenDonationAssistant/oda-payment-processing)

## Running with Docker

The Docker image is available from GitHub Container Registry:

```bash
docker pull ghcr.io/opendonationassistant/oda-payment-processing:latest
```

### Required Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `RABBITMQ_HOST` | RabbitMQ server hostname | `localhost` |
| `JDBC_URL` | PostgreSQL JDBC connection URL | `jdbc:postgresql://localhost/postgres?currentSchema=processing` |
| `JDBC_USER` | Database username | `postgres` |
| `JDBC_PASSWORD` | Database password | `postgres` |

### Docker Run Example

```bash
docker run -d \
  --name oda-payment-processing \
  -e RABBITMQ_HOST=rabbitmq \
  -e JDBC_URL=jdbc:postgresql://postgres:5432/postgres?currentSchema=processing \
  -e JDBC_USER=postgres \
  -e JDBC_PASSWORD=your-password \
  ghcr.io/opendonationassistant/oda-payment-processing:latest
```

### Docker Compose Example

```yaml
version: "3.8"
services:
  payment-processing:
    image: ghcr.io/opendonationassistant/oda-payment-processing:latest
    environment:
      RABBITMQ_HOST: rabbitmq
      JDBC_URL: jdbc:postgresql://postgres:5432/postgres?currentSchema=processing
      JDBC_USER: postgres
      JDBC_PASSWORD: postgres
    depends_on:
      - postgres
      - rabbitmq

  postgres:
    image: postgres:16
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: postgres

  rabbitmq:
    image: rabbitmq:3-management
```

### Dependencies

- **PostgreSQL**: Database for persisting payment data
- **RabbitMQ**: Message broker for event-driven processing
