package accounts.web

import accounts.model.Account
import accounts.repository.AccountRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.logging.Logger

/**
 * RESTful controller for the Accounts microservice.
 *
 * This controller exposes REST endpoints that other microservices
 * can call. Key microservices patterns demonstrated:
 * - Service discovery: Other services find this via Eureka using the name "ACCOUNTS-SERVICE"
 * - Stateless API: Each request is independent (RESTful design)
 * - Resource-based URLs: /accounts/{id} follows REST conventions
 * - Error handling: Throws AccountNotFoundException for missing resources
 *
 * This service is consumed by the Web Service, which discovers it via Eureka and
 * calls these endpoints using a @LoadBalanced RestTemplate.
 *
 * @author Paul Chapman
 */
@RestController
@Tag(
    name = "Accounts", 
    description = "Operations for managing and querying bank accounts"
)
class AccountsController @Autowired constructor(accountRepository: AccountRepository) {
    private val logger = Logger.getLogger(
        AccountsController::class.java.name
    )
    private val accountRepository: AccountRepository

    /**
     * Create an instance plugging in the respository of Accounts.
     *
     * @param accountRepository An account repository implementation.
     */
    init {
        this.accountRepository = accountRepository
        logger.info(
            "AccountRepository says system has "
                    + accountRepository.countAccounts() + " accounts"
        )
    }

    /**
     * Fetch an account with the specified account number.
     *
     * @param accountNumber A numeric, 9 digit account number.
     * @return The account if found.
     * @throws AccountNotFoundException If the number is not recognised.
     */
    @RequestMapping("/accounts/{accountNumber}")
    @Operation(
        summary = "Get account by number",
        description = "Retrieves a specific account using its 9-digit account number. " +
                     "This endpoint demonstrates service discovery - it's called by the web service " +
                     "which finds this service via Eureka."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Account found successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Account::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "Account not found - No account exists with the specified number",
                content = [Content()]
            )
        ]
    )
    fun byNumber(
        @Parameter(
            description = "9-digit account number",
            required = true,
            example = "123456789"
        )
        @PathVariable("accountNumber") accountNumber: String
    ): Account {
        logger.info("accounts-service byNumber() invoked: $accountNumber")
        val account = accountRepository.findByNumber(accountNumber)
        logger.info("accounts-service byNumber() found: $account")
        return account ?: throw AccountNotFoundException(accountNumber)
    }

    /**
     * Fetch accounts with the specified name. A partial case-insensitive match
     * is supported. So `http://.../accounts/owner/a` will find any
     * accounts with upper or lower case 'a' in their name.
     *
     * @param partialName
     * @return A non-null, non-empty set of accounts.
     * @throws AccountNotFoundException If there are no matches at all.
     */
    @RequestMapping("/accounts/owner/{name}")
    @Operation(
        summary = "Search accounts by owner name",
        description = "Finds all accounts where the owner's name contains the specified string. " +
                     "The search is case-insensitive and supports partial matches. " +
                     "For example, searching for 'a' will return all accounts with 'a' or 'A' in the owner's name."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Accounts found successfully",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = Account::class)
                )]
            ),
            ApiResponse(
                responseCode = "404",
                description = "No accounts found - No accounts match the specified owner name",
                content = [Content()]
            )
        ]
    )
    fun byOwner(
        @Parameter(
            description = "Partial or complete owner name (case-insensitive)",
            required = true,
            example = "Keri"
        )
        @PathVariable("name") partialName: String
    ): List<Account> {
        logger.info("accounts-service byOwner() invoked: ${accountRepository.javaClass.getName()} for $partialName")
        val accounts: List<Account> = accountRepository.findByOwnerContainingIgnoreCase(partialName)
        logger.info("accounts-service byOwner() found: $accounts")
        if (accounts.isEmpty()) {
            throw AccountNotFoundException(partialName)
        }
        return accounts
    }
}
