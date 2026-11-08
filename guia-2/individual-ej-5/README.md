# Product App

Aplicación local con frontend React, API Spring Boot y base H2.

Para un recorrido de prueba paso a paso, consulta [GUIA_PRUEBA.md](GUIA_PRUEBA.md).

## Requisitos

- Java 17 o superior
- Node.js 18 o superior y npm

## Iniciar

1. En una terminal, iniciar la API: `cd backend` y ejecutar `./mvnw spring-boot:run` (Windows: `mvnw.cmd spring-boot:run`).
2. En otra terminal, iniciar React: `cd frontend`, ejecutar `npm install` la primera vez y luego `npm start`.
3. Abrir <http://localhost:3000>.

La API escucha en `http://localhost:8080`. Usa H2 como base local persistida en `backend/data/productdb`; no hace falta instalar ni iniciar un servidor de base de datos. La base comienza vacía; crea un usuario desde **Registration** para usar el formulario de acceso. Las tablas se crean automáticamente. Para usar otra base, define `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` y `DB_DRIVER` antes de iniciar el backend.

Para detener MySQL: `docker compose down`. Los datos se conservan en el volumen `productdb-data`; para eliminarlos, usa `docker compose down -v`.
