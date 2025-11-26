package web.service;

import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import web.model.Account;

import jakarta.annotation.PostConstruct;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service layer that encapsulates communication with the Accounts microservice.
 *
 * This class demonstrates service-to-service communication in
 * microservices architecture:
 * - Uses RestTemplate (configured with @LoadBalanced) to make HTTP calls
 * - Service discovery: The serviceUrl contains a logical name (e.g., "ACCOUNTS-SERVICE")
 *   that Eureka resolves to actual instance URLs
 * - Load balancing: When multiple instances exist, requests are automatically
 *   distributed across them
 * - Resilience: If one instance fails, Eureka routes requests to healthy instances
 *
 * This pattern hides the complexity of service discovery from the controller layer.
 *
 * @author Paul Chapman
 */
public class WebAccountsService {

    private final RestTemplate restTemplate;
    private final String serviceUrl;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    private final Logger logger = Logger.getLogger(WebAccountsService.class.getName());

    public WebAccountsService(String serviceUrl, RestTemplate restTemplate, 
                             CircuitBreakerFactory<?, ?> circuitBreakerFactory) {
        this.serviceUrl = serviceUrl.startsWith("http") ? serviceUrl : "http://" + serviceUrl;
        this.restTemplate = restTemplate;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    /**
     * Educational method demonstrating how RestTemplate uses Eureka for service discovery.
     *
     * The RestTemplate works because it uses a custom request-factory
     * that integrates with Spring Cloud LoadBalancer (replacing Ribbon in newer versions).
     * When you make a request to a service name like "ACCOUNTS-SERVICE":
     * 1. Spring Cloud intercepts the request
     * 2. Queries Eureka for available instances
     * 3. Selects an instance (load balancing)
     * 4. Replaces the service name with the actual URL
     * 5. Makes the HTTP request
     *
     * This method logs the request factory to show that it's not a standard RestTemplate.
     * This method exists purely for educational purposes to demonstrate the integration.
     */
    @PostConstruct
    public void demoOnly() {
        // Can't do this in the constructor because the RestTemplate injection
        // happens afterwards.
        logger.warning("The RestTemplate request factory is "
                + restTemplate.getRequestFactory());
    }

    /**
     * Finds an account by number with circuit breaker protection.
     * Falls back to a default account if the service is unavailable.
     */
    public Account findByNumber(String accountNumber) {
        logger.info("findByNumber() invoked: for " + accountNumber);
        
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("accountsService");
        
        return circuitBreaker.run(
            () -> restTemplate.getForObject(serviceUrl + "/accounts/{number}", 
                                           Account.class, accountNumber),
            throwable -> {
                logger.warning("Circuit breaker fallback for findByNumber: " + throwable.getMessage());
                return getFallbackAccount(accountNumber);
            }
        );
    }

    /**
     * Finds accounts by owner name with circuit breaker protection.
     * Returns empty list if the service is unavailable.
     */
    public List<Account> byOwnerContains(String name) {
        logger.info("byOwnerContains() invoked: for " + name);
        
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("accountsService");
        
        return circuitBreaker.run(
            () -> {
                try {
                    Account[] accounts = restTemplate.getForObject(
                        serviceUrl + "/accounts/owner/{name}", 
                        Account[].class, name);
                    
                    if (accounts == null || accounts.length == 0) {
                        return null;
                    }
                    return Arrays.asList(accounts);
                } catch (HttpClientErrorException e) {
                    return null;
                }
            },
            throwable -> {
                logger.warning("Circuit breaker fallback for byOwnerContains: " + throwable.getMessage());
                return getFallbackAccountList(name);
            }
        );
    }

    /**
     * Fallback method when findByNumber fails.
     * Returns a default account indicating service unavailability.
     */
    private Account getFallbackAccount(String accountNumber) {
        Account fallback = new Account();
        fallback.setNumber(accountNumber);
        fallback.setOwner("Service Unavailable");
        fallback.setBalance(BigDecimal.ZERO);
        return fallback;
    }

    /**
     * Fallback method when byOwnerContains fails.
     * Returns an empty list to prevent null pointer exceptions.
     */
    private List<Account> getFallbackAccountList(String name) {
        return List.of();
    }
}
