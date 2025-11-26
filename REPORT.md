# Lab 6 Microservices - Project Report
# David Borrel Seral - 871643

## 1. Configuration Setup

**Configuration Repository**: https://github.com/dborrel/lab6-microservices

Describe the changes you made to the configuration:

- **What did you modify in `accounts-service.yml`?**
    He modificado `accounts-service.yml` cambiando el puerto de 3333 a 2222 en la tarea 3.
    Para realizar el bonus RESTful API Documentation he vuelto a la configuración inicial.
- **Why is externalized configuration useful in microservices?**
    - Permite cambiar la configuración sin tener que recompilar el código.
    - Permite realizar una gestión centralizadad desde un único punto de todos los microservicios.
    - Permite ejecutar multiples instancias con distintos puertos.

---

## 2. Service Registration (Task 1)

### Accounts Service Registration

![Accounts Registration Log](docs/screenshots/accounts-registration.png)

**Explain what happens during service registration.**
    1. Lee su configuración del Config Server.
    2. Se registra en Eureka enviando su nombre, IP y puerto.
    3. Recibe el código 204 confirmando que se ha registrado con éxito.
    4. Empieza a enviar heartbeats cada 30 segundos. 

### Web Service Registration

![Web Registration Log](docs/screenshots/web-registration.png)

**Explain how the web service discovers the accounts service.**
    1. Se registra en Eureka de la misma forma que el servicio Accounts.
    2. Utiliza `@LoadBlanced RestTemplate` para llamar a `http://ACCOUNTS-SERVICE/accounts`.
    3. Spring Cloud LoadBalancer realiza la consulta a Eureka para obtener las instancias disponibles.
    4. Selecciona una instancia automáticamente y reemplaza el nombre lógico por la IP:puerto real

---

## 3. Eureka Dashboard (Task 2)

![Eureka Dashboard](docs/screenshots/eureka-dashboard.png)

**Describe what the Eureka dashboard shows:**
    El Eureka Dashboard proporciona una vista general del estado del servidor Eureka y de las instancias registradas dentro del sistema de microservicios. La información presentada permite monitorizar disponibilidad, salud de los servicios, y el estado general del entorno.

    Contiene tres secciones:
        - System Status - Muestra el estado general del servidor Eureka (Environment, Data center, Uptime, etc.)
        - DS Replicas - Lista las instancias registradas en Eureka.
        - General Info - Contiene datos internos del servidor Eureka (total-avail-memory, num-of-cpus, etc.)
        - Instance Info - Información específica sobre la propia instancia del servidor Eureka (ipAddr, status)

**- Which services are registered?**
    - ACCOUNTS-SERVICE
    - CONFIGSERVER
    - WEB-SERVICE
**- What information does Eureka track for each instance?**
    - Nombre de la aplicación (Application ID)
    - AMIs (Amazon Machine Images)
    - Availability Zones
    - Estado de la instancia (UP, DOWN)
    - Dirección de la instancia (IP:app-name:port)

---

## 4. Multiple Instances (Task 4)

![Multiple Instances](docs/screenshots/multiple-instances.png)

Answer the following questions:

- **What happens when you start a second instance of the accounts service?**
    La segunda instancia se registra en Eureka con el mismo nombre (ACCOUNTS-SERVICE) pero distinto Instance ID, la primera instancia se registra en el puerto 3333 y la segunda en el 2222. Ambas instancias coexisten independientemente.
- **How does Eureka handle multiple instances?**
    Eureka mantiene una lista de todas las instancias bajo el mismo nombre de servicio, pero cada instancia tiene un ID único se monitorizan de forma independiente.
- **How does client-side load balancing work with multiple instances?**
    El Web Service obtiene un listado con todas las instancias disponibles de Eureka, de forma que Spring Cloud LoadBalancer usa Round Robin por defecto: alterna entre instancias secuncialmente (Request 1 -> 3333, Request 2 -> 2222, Request 3 -> 3333, etc.)

---

## 5. Service Failure Analysis (Task 5)

### Initial Failure

![Error Screenshot](docs/screenshots/failure-error.png)

Describe what happens immediately after stopping the accounts service on port 3333.

### Eureka Instance Removal

![Instance Removal](docs/screenshots/instance-removal.png)

Explain how Eureka detects and removes the failed instance:

- How long did it take for Eureka to remove the dead instance?
- What mechanism does Eureka use to detect failures?

---

## 6. Service Recovery Analysis (Task 6)

![Recovery State](docs/screenshots/recovery.png)

Answer the following questions:

- Why does the web service eventually recover?
- How long did recovery take?
- What role does client-side caching play in the recovery process?

---

## 7. Conclusions

Summarize what you learned about:

- Microservices architecture
- Service discovery with Eureka
- System resilience and self-healing
- Challenges you encountered and how you solved them

---

## 8. AI Disclosure

**Did you use AI tools?** (ChatGPT, Copilot, Claude, etc.)

- If YES: Which tools? What did they help with? What did you do yourself?
- If NO: Write "No AI tools were used."

**Important**: Explain your own understanding of microservices patterns and Eureka behavior, even if AI helped you write parts of this report.

---

## Additional Notes

Any other observations or comments about the assignment.

