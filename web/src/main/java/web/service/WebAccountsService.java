package web.service;

import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import web.model.Account;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class WebAccountsService {

    private final RestTemplate restTemplate;
    private final String serviceUrl;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    private final RetryRegistry retryRegistry;
    private final Logger logger = Logger.getLogger(WebAccountsService.class.getName());

    public WebAccountsService(String serviceUrl, RestTemplate restTemplate, 
                             CircuitBreakerFactory<?, ?> circuitBreakerFactory,
                             RetryRegistry retryRegistry) {
        this.serviceUrl = serviceUrl.startsWith("http") ? serviceUrl : "http://" + serviceUrl;
        this.restTemplate = restTemplate;
        this.circuitBreakerFactory = circuitBreakerFactory;
        this.retryRegistry = retryRegistry;
    }

    @PostConstruct
    public void demoOnly() {
        logger.warning("The RestTemplate request factory is "
                + restTemplate.getRequestFactory());
    }

    /**
     * Finds an account by number with circuit breaker and retry protection.
     * Applies retry with jitter before falling back to default account.
     */
    public Account findByNumber(String accountNumber) {
        logger.info("findByNumber() invoked: for " + accountNumber);
        
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("accountsService");
        Retry retry = retryRegistry.retry("accountsService");
        
        // Decorar la llamada con retry
        Supplier<Account> decoratedSupplier = Retry.decorateSupplier(
            retry,
            () -> restTemplate.getForObject(serviceUrl + "/accounts/{number}", 
                                           Account.class, accountNumber)
        );
        
        return circuitBreaker.run(
            decoratedSupplier,
            throwable -> {
                logger.warning("Circuit breaker fallback for findByNumber: " + throwable.getMessage());
                return getFallbackAccount(accountNumber);
            }
        );
    }

    /**
     * Finds accounts by owner name with circuit breaker and retry protection.
     * Returns empty list if the service is unavailable after retries.
     */
    public List<Account> byOwnerContains(String name) {
        logger.info("byOwnerContains() invoked: for " + name);
        
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("accountsService");
        Retry retry = retryRegistry.retry("accountsService");
        
        Supplier<List<Account>> decoratedSupplier = Retry.decorateSupplier(
            retry,
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
            }
        );
        
        return circuitBreaker.run(
            decoratedSupplier,
            throwable -> {
                logger.warning("Circuit breaker fallback for byOwnerContains: " + throwable.getMessage());
                return getFallbackAccountList(name);
            }
        );
    }

    private Account getFallbackAccount(String accountNumber) {
        Account fallback = new Account();
        fallback.setNumber(accountNumber);
        fallback.setOwner("Service Unavailable");
        fallback.setBalance(BigDecimal.ZERO);
        return fallback;
    }

    private List<Account> getFallbackAccountList(String name) {
        return List.of();
    }
}
