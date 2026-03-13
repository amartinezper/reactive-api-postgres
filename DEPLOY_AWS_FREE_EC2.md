# Despliegue gratis en AWS con una sola EC2 micro

Si tu prioridad es mantener el despliegue gratis o lo mas cercano posible a cero costo, la opcion recomendada para esta app es:

1. Crear una sola instancia `EC2 micro`.
2. Instalar Docker y Docker Compose.
3. Ejecutar la API y PostgreSQL en la misma instancia con `docker compose`.

Esto evita costos adicionales de:

- `Application Load Balancer`
- `ECS Fargate`
- `NAT Gateway`
- una base `RDS` separada

## Variables de entorno

La app local y en EC2 usa el archivo `.env` basado en `.env.example`.

## Comandos base en la instancia

```bash
sudo apt update
sudo apt install -y docker.io docker-compose-v2 git
sudo usermod -aG docker $USER
newgrp docker
docker --version
docker compose version
```

## Despliegue

```bash
git clone <TU_REPO>
cd reactive-api-postgres
cp .env.example .env
```

Edita `.env` y cambia `DB_PASSWORD`.

Luego:

```bash
docker compose up -d --build
docker compose ps
curl http://localhost:8080/actuator/health
```

## Exposicion publica

Abre `8080/tcp` en el Security Group de la EC2 y prueba:

```bash
curl http://<IP_PUBLICA>:8080/actuator/health
curl http://<IP_PUBLICA>:8080/api/products
```

## Operacion basica

```bash
docker compose logs -f app
docker compose restart
git pull
docker compose up -d --build
```
