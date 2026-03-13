# Despliegue en AWS con ECS Fargate + RDS PostgreSQL

> Nota: esta arquitectura no es la recomendada si tu objetivo es mantenerte en la capa gratuita o en costo cero. Para eso usa `EC2 micro + docker compose` y revisa `DEPLOY_AWS_FREE_EC2.md`.

Esta aplicacion ya usa PostgreSQL con R2DBC y se puede desplegar en AWS sin cambios de codigo. La ruta recomendada es:

1. Crear una base de datos en Amazon RDS for PostgreSQL.
2. Subir la imagen Docker a Amazon ECR.
3. Ejecutar la API en Amazon ECS Fargate.
4. Exponerla por internet con un Application Load Balancer.

## Arquitectura recomendada

- `RDS PostgreSQL` en subredes privadas.
- `ECS Fargate` en subredes privadas o publicas segun tu preferencia.
- `Application Load Balancer` en subredes publicas.
- `Security Groups`:
  - ALB permite `80/tcp` desde internet.
  - ECS permite `8080/tcp` solo desde el ALB.
  - RDS permite `5432/tcp` solo desde ECS.

## Variables de entorno que usara la app

La app necesita estas variables en la definicion de la tarea de ECS:

- `PORT=8080`
- `SPRING_R2DBC_URL=r2dbc:postgresql://<RDS_ENDPOINT>:5432/reactive_db`
- `SPRING_R2DBC_USERNAME=<usuario>`
- `SPRING_R2DBC_PASSWORD=<password>`
- `JAVA_TOOL_OPTIONS=-Xms256m -Xmx512m`

## 1. Crear RDS PostgreSQL

Configura una instancia de PostgreSQL y anota:

- endpoint
- puerto
- nombre de base de datos
- usuario
- password

Puedes usar `reactive_db` como nombre de base de datos.

## 2. Crear el repositorio ECR

```bash
aws ecr create-repository --repository-name reactive-api-postgres
```

## 3. Construir y subir la imagen

Reemplaza `<AWS_ACCOUNT_ID>` y `<AWS_REGION>`:

```bash
aws ecr get-login-password --region <AWS_REGION> | docker login --username AWS --password-stdin <AWS_ACCOUNT_ID>.dkr.ecr.<AWS_REGION>.amazonaws.com
docker build -t reactive-api-postgres .
docker tag reactive-api-postgres:latest <AWS_ACCOUNT_ID>.dkr.ecr.<AWS_REGION>.amazonaws.com/reactive-api-postgres:latest
docker push <AWS_ACCOUNT_ID>.dkr.ecr.<AWS_REGION>.amazonaws.com/reactive-api-postgres:latest
```

## 4. Crear el cluster y servicio ECS Fargate

En ECS:

1. Crea un cluster.
2. Crea una task definition de tipo `Fargate`.
3. Usa la imagen de ECR.
4. Configura el puerto del contenedor en `8080`.
5. Define las variables de entorno.
6. Asigna CPU y memoria, por ejemplo `0.5 vCPU` y `1 GB RAM`.
7. Crea un servicio con al menos 1 tarea.
8. Asocia un `Application Load Balancer`.

## 5. Health check

Configura el health check del target group con:

- path: `/actuator/health`
- port: `8080`

## 6. Verificacion

Cuando el servicio quede estable:

```bash
curl http://<ALB_DNS>/actuator/health
curl http://<ALB_DNS>/api/products
```

## Actualizacion

Cada vez que cambies la app:

1. construyes la nueva imagen
2. la subes a ECR
3. fuerzas un nuevo deployment del servicio ECS

## Desarrollo local

Para seguir trabajando localmente:

```bash
copy .env.example .env
docker compose up -d --build
```
