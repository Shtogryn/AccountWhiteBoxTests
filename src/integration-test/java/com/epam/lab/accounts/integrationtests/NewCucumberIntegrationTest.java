package com.epam.lab.accounts.integrationtests;

import com.epam.lab.accounts.accounts.model.UserModel;
import com.epam.lab.accounts.accounts.service.UserService;
import com.epam.lab.accounts.integrationtests.config.CucumberUtils;
import io.cucumber.datatable.DataTable;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.assertTrue;

import static org.junit.Assert.assertFalse;
public class NewCucumberIntegrationTest {
    @Autowired
    private UserService userService;

      @Test(expected = IllegalArgumentException.class)
    public void userNotFoundInBD(final DataTable dataTable) {
        final UserModel expectedUserModel = CucumberUtils.toObject(dataTable, UserModel.class);
        final UserModel actualUserModel = userService.getUserModelForEmail(expectedUserModel.getEmail());

        assertTrue(actualUserModel.getEmail().equals(expectedUserModel.getEmail()));
        assertFalse(actualUserModel.getPassword().equals(expectedUserModel.getPassword()));
    }
}

