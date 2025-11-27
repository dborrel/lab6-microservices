---
title: "Web Engineering 2025-2026"
subtitle: "Lab 6: Microservices"
format:
  html:
    toc: true
    toc-depth: 3
    number-sections: true
    code-fold: true
    code-tools: true
    code-overflow: wrap
    theme: cosmo
  pdf:
    documentclass: article
    classoption: [11pt, a4paper]
    toc: true
    toc-depth: 3
    number-sections: true
    geometry: [margin=2.5cm, headheight=15pt]
    fontsize: 11pt
    linestretch: 1.15
    colorlinks: true
    breakurl: true
    urlcolor: blue
    linkcolor: blue
    citecolor: blue
    hyperrefoptions:
      - linktoc=all
      - bookmarksnumbered=true
      - bookmarksopen=true
    header-includes:
      - |
        \usepackage{helvet}
        \renewcommand{\familydefault}{\sfdefault}
        \usepackage{hyperref}
        \usepackage{fancyhdr}
        \pagestyle{fancy}
        \fancyhf{}
        \fancyhead[L]{Web Engineering 2025-2026}
        \fancyhead[R]{Lab 6: Microservices}
        \fancyfoot[C]{\thepage}
        \renewcommand{\headrulewidth}{0.4pt}
        \usepackage{microtype}
        \usepackage{booktabs}
        \usepackage{array}
        \usepackage{longtable}
        \usepackage{xcolor}
        \definecolor{sectioncolor}{RGB}{44,62,80}
        \usepackage{sectsty}
        \allsectionsfont{\color{sectioncolor}}
        \usepackage{graphicx}
        \DeclareGraphicsExtensions{.pdf,.png,.jpg,.jpeg}
        \usepackage{fvextra}
        \fvset{breaklines=true, breakanywhere=true}
        \DefineVerbatimEnvironment{Highlighting}{Verbatim}{breaklines,breakanywhere,commandchars=\\\{\}}
lang: en
---

Welcome to the sixth lab assignment of the 2025–2026 course! This guide will help you complete the assignment efficiently. Although this guide is command-line oriented, you are welcome to use IDEs like **VS Code**, **IntelliJ IDEA**, or **Eclipse**—all of which fully support the tools we'll be using.

Ensure you have at least **Java 21** installed on your system before getting started.

**Estimated time**: 2 hours.

## Learning Objectives

By completing this assignment, you will:

1. **Understand Microservices Architecture**: Learn the fundamental patterns for building distributed systems
2. **Apply Service Discovery**: Use Netflix Eureka for dynamic service registration and discovery
3. **Configure Centralized Settings**: Manage configuration across services with Spring Cloud Config
4. **Test System Resilience**: Observe how the system behaves during failures and recovery
5. **Document Distributed Systems**: Create clear documentation of system behavior with screenshots and explanations

## System Requirements

For this assignment, we'll be using the following technologies:

1. **Java Version**: The project targets **Java 21** (toolchain)
2. **Programming Languages**: Kotlin 2.2.10 (discovery, config, accounts) and Java (web)
3. **Framework**: Spring Boot 3.5.3 with Spring Cloud 2025.0.0 (Northfields)
4. **Build System**: Gradle 8.5
5. **Code Quality**: Ktlint Gradle plugin for Kotlin formatting

## Getting Started

### Clone the Repository

1. Clone your Lab 6 repository and change into the directory:

   ```bash
   git clone https://github.com/UNIZAR-30246-WebEngineering/lab6-microservices.git
   cd lab6-microservices
   ```

2. Build the project to verify everything compiles:

   ```bash
   ./gradlew clean build
   ```

## Microservices Architecture Background

**[Microservices](http://martinfowler.com/articles/microservices.html)** is an architectural style that structures an application as a collection of loosely coupled services. Each service is:

- **Independently deployable**: Can be updated without affecting other services
- **Organized around business capabilities**: Each service handles a specific domain
- **Owned by a small team**: Enables autonomous development and deployment

### Why Microservices Matter

In modern distributed systems, microservices provide:

- **Scalability**: Scale individual services based on demand
- **Resilience**: Failure in one service doesn't cascade to others
- **Technology diversity**: Use the best technology for each service
- **Independent deployment**: Deploy updates without system-wide releases

This lab demonstrates these concepts using Spring Cloud components.

## Reference Documentation

### Spring Cloud Documentation

Essential documentation for this lab:

- **[Spring Cloud Netflix](https://spring.io/projects/spring-cloud-netflix)**: Service discovery with Eureka
- **[Eureka Wiki](https://github.com/Netflix/eureka/wiki)**: Netflix Eureka documentation
- **[Spring Cloud Config](https://spring.io/projects/spring-cloud-config)**: Centralized configuration management
- **[Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)**: Production-ready features

### Architecture Patterns

Patterns demonstrated in this lab:

| Pattern | Implementation | Description |
|---------|---------------|-------------|
| Service Discovery | Eureka Server | Services register and discover each other dynamically |
| Client-Side Load Balancing | Eureka Client | Clients choose which instance to call |
| Externalized Configuration | Config Server | Configuration stored outside application code |
| Health Checking | Actuator | Services report their health status |

## Microservices Infrastructure

This project is based on the [Microservices with Spring](https://spring.io/blog/2015/07/14/microservices-with-spring) guide by Paul Chapman. It contains four services:

### Service Discovery (`discovery`)

The **Eureka Server** provides service registration and discovery.

- **Port**: 8761
- **Dashboard**: <http://localhost:8761>
- **Language**: Kotlin

**Mandatory for Windows users**: keep the following setting in `discovery/src/main/resources/application.yml`:

- `eureka.server.enable-self-preservation: false`

This setting make Eureka evict failed instances quickly so that the failure and recovery experiments behave as described in this guide.
On Windows it is required for the lab to work reliably. On other operating systems it is not necessary.

```bash
./gradlew discovery:bootRun
```

Services register themselves with Eureka, and clients query Eureka to find service instances. This enables dynamic scaling and failover without hardcoded URLs.

### Configuration Server (`config`)

The **Spring Cloud Config Server** provides centralized configuration.

- **Port**: 8762
- **Config Location**: `config/config/` directory (native file system, served from `file:${user.dir}/config` as configured in `config/src/main/resources/application.yml`)
- **Language**: Kotlin

```bash
./gradlew config:bootRun
```

Configuration files are stored locally in the `config/config/` directory. Each service has its own YAML file (e.g., `accounts-service.yml`, `web-service.yml`). Services fetch their configuration from the Config Server at startup.

### Accounts Service (`accounts`)

A RESTful service providing account data with an in-memory database.

- **Port**: 3333 (configurable via Config Server)
- **Service Name**: `ACCOUNTS-SERVICE`
- **Language**: Kotlin

```bash
./gradlew accounts:bootRun
```

After startup, this service registers with Eureka and fetches configuration from the Config Server. You can verify registration in the Eureka dashboard after 10-20 seconds.

### Web Service (`web`)

An MVC frontend that consumes the Accounts Service.

- **Port**: 4444
- **Service Name**: `WEB-SERVICE`
- **Language**: Java

```bash
./gradlew web:bootRun
```

This service uses Eureka to discover `ACCOUNTS-SERVICE` instances. Spring automatically configures a `RestTemplate` that uses the discovery service for load-balanced calls.

## Service Startup Order

Services must be started in this order due to dependencies:

1. **Discovery** (Eureka) - Other services need it to register
2. **Config** - Services need it to fetch configuration
3. **Accounts** - Backend service
4. **Web** - Frontend that calls Accounts

Wait for each service to fully start before starting the next one. Watch for log messages indicating successful startup.

## Assignment Tasks

### Task 1: Service Registration Verification

1. Start the `discovery` service (wait for full startup)
2. Start the `config` service
3. Start the `accounts` service (port 3333)
4. Start the `web` service (port 4444)
5. Verify both services register in Eureka

**Required**: Provide **2 log screenshots** showing successful registration of both services.

**Expected log output** (accounts):

```
DiscoveryClient_ACCOUNTS-SERVICE/... - registration status: 204
```

**Expected log output** (web):

```
DiscoveryClient_WEB-SERVICE/... - registration status: 204
```

### Task 2: Eureka Dashboard Verification

1. Open the Eureka dashboard at <http://localhost:8761>
2. Verify both `ACCOUNTS-SERVICE` and `WEB-SERVICE` appear under "Instances currently registered with Eureka"

**Required**: Provide a **screenshot of the Eureka dashboard** showing both registered services.

### Task 3: Update Configuration

1. Change the accounts service port from 3333 to 2222
2. Edit the local configuration file `config/config/accounts-service.yml`:

   ```yaml
   server:
     port: 2222
   ```

3. Save the file
4. Restart the `config` service to load the updated configuration

### Task 4: Run Multiple Instances

1. Start a **second instance** of the accounts service
2. This new instance should use port 2222 (from the updated configuration)

**Required**: Answer the following questions:

- What happens when you start the second instance?
- How does Eureka handle multiple instances of the same service?
- Provide a **screenshot of the Eureka dashboard** showing both instances

**Expected behavior**: Eureka should show two instances of `ACCOUNTS-SERVICE` (ports 3333 and 2222).

### Task 5: Service Failure Test

1. Stop the accounts service running on port 3333 (Ctrl+C)
2. Send requests to the web service at <http://localhost:4444>
3. Observe the behavior

**Required**: Answer the following questions:

- What happens to requests immediately after stopping the service?
- Does the web service eventually recover? How long does it take?
- Provide **screenshots** including the Eureka dashboard and any error messages

**Expected behavior**: Initially, some requests may fail. Eureka will eventually remove the dead instance (after heartbeat timeout), and requests will be routed to the remaining instance.

### Task 6: Service Recovery Analysis

1. After the failed instance is removed from Eureka, verify the web service resumes normal operation
2. Confirm requests are being served by the remaining accounts instance (port 2222)

**Required**:

- Explain why the web service recovers
- Provide **screenshots** showing successful operation and the Eureka dashboard

## Self-Verification Checklist

Before submitting, verify your solution:

- [ ] **Configuration Files**: Located in `config/config/` directory
- [ ] **Service Registration**: Both services register successfully (log screenshots)
- [ ] **Eureka Dashboard**: Shows all registered services (screenshot)
- [ ] **Port Configuration**: Successfully changed accounts port to 2222
- [ ] **Multiple Instances**: Ran two instances of accounts service (screenshot)
- [ ] **Failure Test**: Documented behavior when service fails (screenshots + explanation)
- [ ] **Recovery Test**: Documented recovery behavior (screenshots + explanation)
- [ ] **REPORT.md**: Created with all required sections

## Submission Requirements

### Moodle Submission

Submit a zip file containing:

- **Documentation**: `REPORT.md` with complete analysis and screenshots
- **Source code**: Any modified source files

### GitHub Repository

Ensure your `main` branch contains:

- All configuration file changes in `config/config/` committed with clear messages
- Complete `REPORT.md` file in the repository root
- All required screenshots in the `docs` directory

### REPORT.md Requirements

Your `REPORT.md` must include:

1. **Configuration Setup**:
   - Description of configuration files in `config/config/`
   - Description of changes made to configuration

2. **Service Registration** (Task 1):
   - Log screenshots showing registration
   - Brief explanation of the registration process

3. **Eureka Dashboard** (Task 2):
   - Dashboard screenshot with both services
   - Explanation of what the dashboard shows

4. **Multiple Instances** (Task 4):
   - Dashboard screenshot with multiple instances
   - Explanation of how Eureka handles multiple instances
   - How does client-side load balancing work?

5. **Failure Analysis** (Task 5):
   - Screenshots of error behavior
   - Dashboard screenshot showing instance removal
   - Explanation of the failure detection mechanism

6. **Recovery Analysis** (Task 6):
   - Screenshots of recovered state
   - Explanation of why recovery works
   - How long did recovery take?

7. **AI Disclosure** (required):
   - Which AI tools did you use (if any)?
   - What was AI-generated vs. your original work?
   - How did you verify AI-generated content?

## Assessment Criteria

Your submission will be evaluated on:

**To Pass:**

- Configuration files properly modified in `config/config/`
- All required screenshots provided with clear explanations
- REPORT.md contains all required sections
- Demonstrates understanding of service discovery and resilience

## Tips for Success

- **Start Services in Order**: Discovery → Config → Accounts → Web
- **Wait for Startup**: Each service needs time to fully initialize
- **Watch the Logs**: Log messages tell you exactly what's happening
- **Use the Dashboard**: Eureka dashboard is your best debugging tool
- **Take Screenshots Early**: Capture states before they change
- **Read Error Messages**: They usually explain what went wrong
- **Commit Often**: Small, frequent commits help track your progress
- **Ask Questions**: If stuck, use the references or ask your instructor

## Troubleshooting

### Services Not Registering

- Verify Discovery service is running first
- Check that `eureka.client.serviceUrl.defaultZone` is correct in application.yml
- Wait 30 seconds for registration to propagate

### Config Not Loading

- Verify Config service is running
- Check that configuration files exist in `config/config/` directory
- Verify the application name matches the config file name (e.g., `accounts-service.yml` for `spring.application.name=accounts-service`)

### Web Service Can't Find Accounts

- Check Eureka dashboard for registered instances
- Verify the service name matches (`ACCOUNTS-SERVICE`)
- Wait for Eureka cache to update (up to 30 seconds)

---

By following this guide and understanding microservices patterns, you will complete the assignment and gain valuable skills in designing distributed systems. Feel free to reach out if you have any questions or need assistance.

Good luck with your assignment!
