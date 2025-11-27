package web.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import web.model.Account;
import web.model.SearchCriteria;
import web.service.WebAccountsService;

import java.util.List;
import java.util.logging.Logger;

/**
 * MVC controller for the Web Service that acts as a client to the Accounts microservice.
 *
 * This controller demonstrates the client-side of microservices communication:
 * - Service discovery: Uses WebAccountsService which internally uses Eureka to find "ACCOUNTS-SERVICE"
 * - Load balancing: If multiple Accounts instances exist, requests are automatically distributed
 * - Resilience: If Accounts service is down, requests will fail (consider circuit breaker pattern)
 * - Separation of concerns: Business logic is in WebAccountsService, this handles HTTP/UI concerns
 *
 * This is a typical microservices pattern: a frontend service (this) calls backend services
 * (Accounts) without knowing their exact locations, relying on service discovery.
 *
 * @author Paul Chapman
 */
@Controller
@Tag(
    name = "Web Accounts", 
    description = "MVC endpoints for account search and display. These endpoints render HTML views."
)
public class WebAccountsController {

    private final WebAccountsService accountsService;

    private final Logger logger = Logger.getLogger(WebAccountsController.class
            .getName());

    @Autowired
    public WebAccountsController(WebAccountsService accountsService) {
        this.accountsService = accountsService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setAllowedFields("accountNumber", "searchText");
    }

    @RequestMapping("/accounts")
    @Operation(
        summary = "Account home page",
        description = "Returns the main accounts page HTML view"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Successfully returned home page",
        content = @Content(mediaType = "text/html")
    )
    public String goHome() {
        return "index";
    }

    @RequestMapping("/accounts/{accountNumber}")
    @Operation(
        summary = "View account details by number",
        description = "Retrieves and displays account details for a specific 9-digit account number. " +
                     "This endpoint calls the Accounts microservice via service discovery."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Account found and displayed",
            content = @Content(mediaType = "text/html")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Account not found",
            content = @Content(mediaType = "text/html")
        )
    })
    public String byNumber(Model model,
                           @Parameter(
                               description = "9-digit account number",
                               required = true,
                               example = "123456789"
                           )
                           @PathVariable("accountNumber") String accountNumber) {

        logger.info("web-service byNumber() invoked: " + accountNumber);

        Account account = accountsService.findByNumber(accountNumber);
        logger.info("web-service byNumber() found: " + account);
        model.addAttribute("account", account);
        return "account";
    }

    @RequestMapping("/accounts/owner/{text}")
    @Operation(
        summary = "Search accounts by owner name",
        description = "Searches for accounts where the owner name contains the specified text (case-insensitive). " +
                     "Uses service discovery to call the Accounts microservice."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search completed, results displayed",
            content = @Content(mediaType = "text/html")
        ),
        @ApiResponse(
            responseCode = "404",
            description = "No accounts found matching the search criteria",
            content = @Content(mediaType = "text/html")
        )
    })
    public String ownerSearch(Model model, 
                             @Parameter(
                                 description = "Partial or complete owner name",
                                 required = true,
                                 example = "Keri"
                             )
                             @PathVariable("text") String name) {
        logger.info("web-service byOwner() invoked: " + name);

        List<Account> accounts = accountsService.byOwnerContains(name);
        logger.info("web-service byOwner() found: " + accounts);
        model.addAttribute("search", name);
        if (accounts != null) {
            model.addAttribute("accounts", accounts);
        }
        return "accounts";
    }

    @RequestMapping(value = "/accounts/search", method = RequestMethod.GET)
    @Operation(
        summary = "Display search form",
        description = "Returns the account search form HTML view"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Search form displayed",
        content = @Content(mediaType = "text/html")
    )
    public String searchForm(Model model) {
        model.addAttribute("searchCriteria", new SearchCriteria());
        return "accountSearch";
    }

    @RequestMapping(value = "/accounts/dosearch")
    @Operation(
        summary = "Execute account search",
        description = "Processes search form submission and redirects to appropriate results page. " +
                     "Validates that either account number OR search text is provided (not both)."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Search executed successfully",
            content = @Content(mediaType = "text/html")
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid search criteria - validation errors",
            content = @Content(mediaType = "text/html")
        )
    })
    public String doSearch(Model model, 
                          @Parameter(description = "Search criteria object")
                          SearchCriteria criteria,
                          BindingResult result) {
        logger.info("web-service search() invoked: " + criteria);

        criteria.validate(result);

        if (result.hasErrors()) {
            return "accountSearch";
        }

        String accountNumber = criteria.getAccountNumber();
        if (StringUtils.hasText(accountNumber)) {
            return byNumber(model, accountNumber);
        } else {
            String searchText = criteria.getSearchText();
            return ownerSearch(model, searchText);
        }
    }
}
