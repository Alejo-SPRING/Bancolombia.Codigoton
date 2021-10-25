# Bancolombia.Codigoton
# Intstrucciones de despliegue
1. Clonar el proyecto.
2. Crear la base de datos y iniciar la base de datos.
3. En el archivo: /resources/applications.properties, cambiar los parametros de conexión de la base de datos.
4. En la carpeta: /formatInput, se encuentra el txt de entrada, y donde se puede cambiar los filtros o mesas.
5. Ir con terminal a la carpeta del proyecto.
6. Colocar el comando: mvn clean install.
7. Ir con terminal a la carpeta target, dentro del proyecto.
8. Colocar el comando: java -jar Bancolombia.Codigoton-0.0.1-SNAPSHOT.jar.
9. Ir al navegador o abrir Postman y colocar la url: http://localhost:8080/codeton/organization/dinner
