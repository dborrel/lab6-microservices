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

![Accounts API Documentation](docs/screenshots/swagger-accounts.png)

![Web API Documentation](docs/screenshots/swagger-web.png)

![API Testing Example](docs/screenshots/swagger-test.png)

### Beneficios
- **Documentación interactiva**: Los desarrolladores pueden probar los endpoints directamente desde el navegador.
- **Auto-generado**: La documentación se actualiza atuomaticamente cuando hay cambios en el código.
- **Reduce el trabajo manual**: No es necesaria mantener una documentación manual de la API, de esta forma se realiza automaticamente.

### Endpoints de acceso
- Accounts Service: http://localhost:3333/swagger-ui.html
- Web Service: http://localhost:4444/swagger-ui.html

---
## Additional Notes

Any other observations or comments about the assignment.

