# Lab 6 Microservices - Project Report
# David Borrel Seral - 871643

## 1. Configuration Setup

**Configuration Repository**: https://github.com/dborrel/lab6-microservices

Describe the changes you made to the configuration:

- What did you modify in `accounts-service.yml`?
- Why is externalized configuration useful in microservices?

---

## 2. Service Registration (Task 1)

### Accounts Service Registration

![Accounts Registration Log](docs/screenshots/accounts-registration.png)

Explain what happens during service registration.

### Web Service Registration

![Web Registration Log](docs/screenshots/web-registration.png)

Explain how the web service discovers the accounts service.

---

## 3. Eureka Dashboard (Task 2)

![Eureka Dashboard](docs/screenshots/eureka-dashboard.png)

Describe what the Eureka dashboard shows:

- Which services are registered?
- What information does Eureka track for each instance?

---

## 4. Multiple Instances (Task 4)

![Multiple Instances](docs/screenshots/multiple-instances.png)

Answer the following questions:

- What happens when you start a second instance of the accounts service?
- How does Eureka handle multiple instances?
- How does client-side load balancing work with multiple instances?

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

## 9. Bonus 1: RESTful API Documentation

**Implementación:** SpringDoc OpenAPI 3 con Swagger UI

### Detalles de implementación
- Se ha añadido la de dependecia de SpringDoc a los servicios `acounts` y `web`.
- Se ha creado la clase `OpenApiConfig` en los dos servicios para configurar los metadatos de la API.
- Se han modificado los controladores para añadir anotaciones OpenAPI para documentación detallada.
- Se han habilitado pruebas de API interactivas a través de Swagger UI.

### Screenshots

#### Accounts API Documentation
![Accounts API Documentation](docs/screenshots/swagger-accounts.png)

#### Web API Documentation
![Web API Documentation](docs/screenshots/swagger-web.png)

#### API Testing Example
![API Testing Example](docs/screenshots/swagger-test.png)

### Beneficios
- **Documentación interactiva**: Los desarrolladores pueden probar los endpoints directamente desde el navegador.
- **Auto-generado**: La documentación se actualiza atuomáticamente cuando hay cambios en el código.
- **Reduce el trabajo manual**: No es necesaria mantener una documentación manual de la API, de esta forma se realiza automaticamente.

### Endpoints de acceso
- Accounts Service: http://localhost:3333/swagger-ui.html
- Web Service: http://localhost:4444/swagger-ui.html

---
## 9. Bonus 2: 10. Circuit Breaker Pattern

**Implementación:** Spring Cloud Circuit Breaker con Resilience4j

### Descripción
Se ha implementado el patrón Circuit Breaker para prevenir fallos en cascada cuando el servicio de accounts no está disponible.

### Detalles de implementación

#### 1. Dependencias añadidas
Se han añadido las dependencias de Resilence4j en el `build.gradle.kts` del servicio Web.

#### 2. Configuración en `application.yml`
La configuración define un circuit breaker llamado accountsService en Resilience4j que se activará cuando el 50% de las últimas 10 llamadas fallen, siempre que haya al menos 5 llamadas evaluadas. Una vez abierto, permanecerá 10 segundos antes de permitir hasta 3 llamadas de prueba en estado half-open para comprobar si el servicio se ha recuperado.

#### 3. Modificaciones en `WebAccountsService`
- Inyección de `CircuitBreakerFactory<?, ?>`
- Métodos `findByNumber()` y `byOwnerContains()` envueltos con circuit breaker
- Métodos de fallback que devuelven respuestas por defecto cuando el servicio falla

### Screenshots

#### Estado CLOSED (funcionamiento normal)
![Circuit Breaker Closed](docs/screenshots/circuit-breaker-closed.png)

#### Estado OPEN (servicio caído)
![Circuit Breaker Closed](docs/screenshots/circuit-breaker-open.png)

#### Estado HALF-OPEN (probando recuperación)
![Circuit Breaker Half-Open](docs/screenshots/circuit-breaker-half-open.png)

#### Eventos del circuit breaker
![Circuit Breaker Events](docs/screenshots/circuit-breaker-events.png)

### Estados del Circuit Breaker
| Estado | Descripción | Comportamiento |
|--------|-------------|----------------|
| **CLOSED** | Funcionamiento normal | Todas las peticiones pasan al servicio accounts |
| **OPEN** | Servicio fallando | Peticiones fallan inmediatamente con respuesta de fallback |
| **HALF_OPEN** | Probando recuperación | Permite algunas peticiones para verificar si el servicio se recuperó |

### Endpoints de monitorización
- Circuit Breakers: http://localhost:4444/actuator/circuitbreakers
- Eventos: http://localhost:4444/actuator/circuitbreakerevents
- Métricas: http://localhost:4444/actuator/metrics/resilience4j.circuitbreaker.state

### Beneficios
- **Prevención de fallos en cascada**: Produce fallos rápidos sin sobrecargar servicios caídos
- **Degradación controlada**: Respuestas de fallback en lugar de errores
- **Recuperación automática**: Detecta cuando el servicio vuelve a estar disponible

### Referencias
- https://www.geeksforgeeks.org/advance-java/spring-boot-circuit-breaker-pattern-with-resilience4j/
- https://www.baeldung.com/spring-boot-resilience4j
- https://spring.io/projects/spring-cloud-circuitbreaker
- https://www.youtube.com/watch?v=OxGr2eB911s
---
## Additional Notes

Any other observations or comments about the assignment.

