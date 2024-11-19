# Plataforma de Gestión de Ideas Empresariales

## Descripción
Esta plataforma optimiza la gestión de ideas y retos empresariales, mejorando la experiencia del usuario con un diseño intuitivo y soporte personalizado. Facilita la colaboración entre empleados, promotores y gerentes, asegurando el éxito de las propuestas desde su creación hasta su implementación.

## Características Principales
- **Registro y Acceso de Usuarios:** Empleados, promotores y gerentes se registran y acceden con credenciales únicas.
- **Subida y Gestión de Ideas:** Los empleados pueden proponer, editar y eliminar ideas hasta ser revisadas por un promotor.
- **Acompañamiento por Promotores:** Los promotores reciben notificaciones de nuevas ideas y apoyan en su desarrollo.
- **Evaluación por Gerentes:** Los gerentes aprueban o rechazan ideas con retroalimentación.
- **Asignación de Compensación:** Las ideas aprobadas generan compensaciones automáticas para los empleados.
- **Notificaciones por Correo:** Los usuarios reciben actualizaciones vía correo electrónico sobre el estado de las ideas.
- **Visibilidad y Reportes:** Informes y métricas disponibles para empleados, promotores y gerentes.
- **Base de Datos Centralizada:** Toda la información se almacena en una base de datos accesible desde cualquier sede.

## Tecnologías Utilizadas
- **Frontend:** HTML
- **Backend:** Spring Boot
- **Base de Datos:** H2
- **Notificaciones:** SMTP para correos electrónicos

# Proyecto Spring Boot - Instrucciones de Instalación y Ejecución

Este documento describe los requisitos y los pasos necesarios para ejecutar el proyecto de Spring Boot.

---

## **Requisitos del Sistema**

1. **Java Development Kit (JDK)**
   - Versión mínima: **17**
   - [Descargar JDK](https://www.oracle.com/java/technologies/javase-downloads.html)

2. **Maven**
   - Versión mínima: **3.8.1**
   - [Descargar Maven](https://maven.apache.org/download.cgi)

3. **Base de Datos**
   - **MySQL**
     - Versión mínima: **8.0**
     - [Descargar MySQL](https://dev.mysql.com/downloads/)

4. **IDE (Opcional)**
   - **IntelliJ IDEA** o **Eclipse IDE**
   - Asegúrate de que el IDE soporte proyectos de Maven.

5. **Postman (Opcional)**
   - Herramienta para probar las APIs.
   - [Descargar Postman](https://www.postman.com/downloads/)

---

## **Configuración de la Base de Datos**

1. **Crear una base de datos en MySQL**
   - Nombre sugerido: `spring_project_db`
   - Crear con el comando:
     ```sql
     CREATE DATABASE spring_project_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
     ```

2. **Actualizar credenciales en el archivo `application.properties`**
   - Ubicación: `src/main/resources/application.properties`
   - Configurar las siguientes propiedades con tus credenciales:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/spring_project_db
     spring.datasource.username=tu_usuario
     spring.datasource.password=tu_contraseña

     # Configuración adicional
     spring.jpa.hibernate.ddl-auto=update
     spring.jpa.show-sql=true
     spring.jpa.properties.hibernate.format_sql=true
     ```

---

## **Instrucciones para Ejecutar el Proyecto**

### **1. Clonar el Repositorio**

- Clona el proyecto desde GitHub:
  ```bash
  git clone https://github.com/usuario/proyecto-springboot.git
  cd proyecto-springboot
  ```

---

### **2. Compilar el Proyecto**

- Usa Maven para descargar las dependencias y compilar el proyecto:
  ```bash
  mvn clean install
  ```

---

### **3. Ejecutar el Proyecto**

- Usa el siguiente comando para iniciar la aplicación:
  ```bash
  mvn spring-boot:run
  ```

- Alternativamente, ejecuta el archivo generado en `target/`:
  ```bash
  java -jar target/proyecto-springboot-0.0.1-SNAPSHOT.jar
  ```

---

### **4. Acceder a la Aplicación**

- La aplicación estará disponible en:
  ```
  http://localhost:8100
  ```

---

## **API Endpoints Principales**

| Método HTTP | Endpoint                     | Descripción                          |
|-------------|------------------------------|--------------------------------------|
| `GET`       | `/proponente`               | Página principal del proponente.     |
| `GET`       | `/proponente/ideas`         | Lista de ideas del proponente.       |
| `POST`      | `/proponente/idea`          | Crea una nueva idea.                 |
| `GET`       | `/proponente/perfil`        | Muestra el perfil del proponente.    |

---

## **Notas**

- **Errores comunes**:
  - **`NoSuchElementException`**: Verifica que las relaciones de datos (como gerencias y áreas) estén correctamente configuradas en la base de datos.
  - **`HTTP 404` o `500`**: Asegúrate de que las tablas y las credenciales de la base de datos sean correctas.
  
- **Configuración avanzada**:
  - Si necesitas cambiar el puerto de la aplicación, edita `application.properties`:
    ```properties
    server.port=8100
    ```

- **Soporte**:
  - Si tienes problemas para ejecutar el proyecto, revisa los logs o contacta con el equipo de desarrollo.

---

¡Gracias por usar nuestro proyecto! 😊
