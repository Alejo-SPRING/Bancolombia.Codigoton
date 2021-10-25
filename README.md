# Bancolombia.Codigoton
# Intstrucciones de despliegue
1. Clonar el proyecto.
2. Crear la base de datos y iniciar la base de datos.
3. En el archivo: /resources/applications.properties, cambiar los parametros de conexión de la base de datos.
4. Ir con terminal a la carpeta del proyecto.
5. Colocar el comando: mvn clean install.
6. Ir con terminal a la carpeta target, dentro del proyecto.
7. Colocar el comando: java -jar Bancolombia.Codigoton-0.0.1-SNAPSHOT.jar.
8. Ir al navegador o abrir Postman y colocar la url: http://localhost:8080/codeton/organization/dinner
