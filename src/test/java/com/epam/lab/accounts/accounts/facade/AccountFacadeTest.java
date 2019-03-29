
package com.epam.lab.accounts.accounts.facade;

import com.epam.lab.accounts.accounts.converter.AccountConverter;
import com.epam.lab.accounts.accounts.dto.AccountDTO;
import com.epam.lab.accounts.accounts.model.requests.CreateUpdateAccountRequest;
import com.epam.lab.accounts.accounts.service.AccountService;
import com.epam.lab.accounts.accounts.service.ErrorsService;
import com.epam.lab.accounts.accounts.service.SessionService;
import com.epam.lab.accounts.accounts.service.UserService;
import com.epam.lab.accounts.accounts.validator.CreateAccountRequestRequestValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountFacadeTest {
    @Mock
    private AccountService accountService;
    @Mock
    private UserService userService;
    @Mock
    private SessionService sessionService;
    private ErrorsService errorsService = new ErrorsService();
    private AccountFacade accountFacade = new AccountFacade();
    private MockHttpSession httpSession = new MockHttpSession();
    private CreateAccountRequestRequestValidator validator = new CreateAccountRequestRequestValidator();
    private AccountConverter converter = new AccountConverter();

    @Before
    public void setFields() {
        setField(errorsService, "sessionService", sessionService);
        setField(validator, "accountService", accountService);
        setField(validator, "errorsService", errorsService);
        setField(accountFacade, "createAccountRequestValidator", validator);
        setField(accountFacade, "sessionService", sessionService);
        setField(accountFacade, "accountService", accountService);
        setField(accountFacade, "userService", userService);
        setField(accountFacade, "accountConverter", converter);
        setField(sessionService, "session", httpSession);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testHandleCreateAccount() {
        // GIVEN
        final CreateUpdateAccountRequest createUpdateRequest = getDefaultCreateRequest();
        final AccountDTO account = getDefaultSessionAccount();
        // WHEN
        when(accountService.isAccountExistsForAccountCode(account.getCode())).thenReturn(false);
        // THEN
        validator.validate(createUpdateRequest);
        verify(accountService).createAccountForCurrentUser(account);
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateAccountNegativeTest() {
        final CreateUpdateAccountRequest createUpdateAccountRequest = getIllegalUpdateAccountRequest();
        when(accountService.isAccountExistsForAccountCode(createUpdateAccountRequest.getAccountCode()))
                .thenReturn(true);
        accountFacade.handleCreateOrUpdateAccountRequest(createUpdateAccountRequest);
    }

    public AccountDTO createAccount(BigDecimal balance, String code, String img, String name) {
        final AccountDTO accountModel = new AccountDTO();
        accountModel.setBalance(balance);
        accountModel.setCode(code);
        accountModel.setImg(img);
        accountModel.setName(name);
        return accountModel;
    }

    public AccountDTO getDefaultSessionAccount() {
        final AccountDTO accountModel = new AccountDTO();
        accountModel.setBalance(new BigDecimal(321));
        accountModel.setCode("dean.guitars");
        accountModel.setImg("img.jpg");
        accountModel.setName("Dave Mustataine");
        return accountModel;
    }

    public CreateUpdateAccountRequest getDefaultUpdateRequest() {
        final AccountDTO acoountModel = getDefaultSessionAccount();
        final CreateUpdateAccountRequest createUpdateRequest = new CreateUpdateAccountRequest();
        createUpdateRequest.setAccountBalance(new BigDecimal(123));
        createUpdateRequest.setAccountCode(acoountModel.getCode());
        createUpdateRequest.setAccountImage(acoountModel.getImg());
        createUpdateRequest.setAccountName(acoountModel.getName());
        return createUpdateRequest;
    }

    public CreateUpdateAccountRequest getDefaultCreateRequest() {
        final AccountDTO acoountModel = getDefaultSessionAccount();
        final CreateUpdateAccountRequest createUpdateRequest = new CreateUpdateAccountRequest();
        createUpdateRequest.setAccountBalance(acoountModel.getBalance());
        createUpdateRequest.setAccountCode("code");
        createUpdateRequest.setAccountImage(acoountModel.getImg());
        createUpdateRequest.setAccountName(acoountModel.getName());
        return createUpdateRequest;
    }

    private CreateUpdateAccountRequest getIllegalUpdateAccountRequest() {
//with present account data's
        final AccountDTO accountDTO = createAccount(new BigDecimal(2500000.0), "nokia-lumia", "Nokia Lumia Department",
                "https://upload.wikimedia.org/wikipedia/commons/thumb/f/f1/Nokia_Lumia_logo.svg/1280px-Nokia_Lumia_logo.svg.png");

        final CreateUpdateAccountRequest updateAccountRequest = new CreateUpdateAccountRequest();
        updateAccountRequest.setAccountCode(accountDTO.getCode());
        updateAccountRequest.setAccountBalance(accountDTO.getBalance());
        updateAccountRequest.setAccountName(accountDTO.getName());
        updateAccountRequest.setAccountImage("img.png");
        return updateAccountRequest;
    }
}