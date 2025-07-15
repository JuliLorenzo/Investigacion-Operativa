# API de Gestión de Inventario y Predicción de la Demanda
Este proyecto fue desarrollado como trabajo final para la materia Investigación Operativa. Consiste en una API REST construida con Spring Boot, Gradle y MySQL, orientada a la gestión de inventario, ventas, proveedores y predicción de la demanda mediante distintos métodos estadísticos.

## Funcionalidades principales
- Gestión de artículos, inventario, ventas, proveedores y relaciones artículo–proveedor.
- Registro y consulta de ventas históricas.
- Predicción de la demanda utilizando:
  - Promedio móvil ponderado.
  - Promedio móvil suavizado.
  - Regresión lineal.
  - Estacionalidad.
- Cálculo y análisis del error de los métodos de predicción.
- Selección automática del método más preciso.
- Cálculo de lote óptimo, punto de pedido, stock de seguridad y demanda anual.
- Creación automática de órdenes de compra siguiendo reglas de negocio.
- Gestión de proveedores por artículo, obtención de precios, costos de pedido y tiempos de entrega
- Control de estados de la orden de compra (pendiente, confirmada, finalizada, cancelada).

## Tecnologías utilizadas
- Java 17 – Spring Boot
- Gradle
- MySQL + MySQL Workbench
- JPA/Hibernate
- REST API
- Git y GitHub para control de versiones

## Cómo ejecutar el proyecto
1. Clonar el repositorio.
2. Configurar las credenciales de base de datos en `application.properties`.
3. Ejecutar `./gradlew bootRun` o desde el IDE.

## Acerca del proyecto
Proyecto grupal realizado en el marco de la carrera Ingeniería en Sistemas de Información. Participé activamente en el desarrollo backend, colaborando en la implementación de la lógica de negocio, servicios, controladores y procesos de predicción, automatización de órdenes y gestión de datos.
