package ru.netology.sql.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;
import ru.netology.sql.data.DataHelper;

import static com.codeborne.selenide.Selenide.page;

public class LoginPage {
    @FindBy(css = "[data-test-id=login] input")
    private SelenideElement loginField;
    @FindBy(css = "[data-test-id=password] input")
    private SelenideElement passwordField;
    @FindBy(css = "[data-test-id=action-login]")
    private SelenideElement loginButton;
    @FindBy(css = "[data-test-id=error-notification] .notification__content")
    private SelenideElement errorNotification;

    public void login(DataHelper.AuthInfo info) {
        loginField.setValue(info.getLogin());
        passwordField.setValue(info.getPassword());
        loginButton.click();
    }

    public VerificationPage validLogin(DataHelper.AuthInfo info) {
        login(info);
        return new VerificationPage();
    }

    public void checkErrorNotification(String expectedText) {
        errorNotification.should(Condition.visible).shouldHave(Condition.text(expectedText));
    }

    public void clearFilds() {
        loginField.press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
        passwordField.press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE);
    }
}
