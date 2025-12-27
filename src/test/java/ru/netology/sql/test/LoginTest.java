package ru.netology.sql.test;

import com.codeborne.selenide.Selenide;
import com.github.javafaker.Faker;
import org.apache.commons.dbutils.QueryRunner;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.sql.data.DataHelper;
import ru.netology.sql.data.SQLHelper;
import ru.netology.sql.page.LoginPage;

import static ru.netology.sql.data.DataHelper.getRandomPassword;
import static ru.netology.sql.data.SQLHelper.cleanAllTables;
import static ru.netology.sql.data.SQLHelper.cleanAuthCode;

public class LoginTest {
    private LoginPage loginPage;
    DataHelper.AuthInfo authInfo = DataHelper.getAuthInfo();

    @BeforeEach
    void setUp() {
        loginPage = Selenide.open("http://localhost:9999", LoginPage.class);
    }

    @AfterEach
    void tearDownAuthCode() {
        cleanAuthCode();
    }

    @AfterAll
    static void tearDownAll() {
        cleanAllTables();
    }

    @Test
    void shouldSuccessfullyLogin() { // должен быть успешный логин с валидными данными
        var verificationPage = loginPage.validLogin(authInfo); // вводим валидные имя и пароль, нажимаем кнопку, входим на страницу верификации
        var verificationCode = SQLHelper.getVerificationCode(); // получаем код верификации из БД
        var dashBoardPage = verificationPage.validVerify(String.valueOf(verificationCode)); // вводим код в поле, нажимаем кнопку, переходим в Личный кабинет, виден заголовок
    }

    @Test
    void shouldGetErrorNotificationWithInvalidLogin() { // должен получить уведомление об ошибке при невалидном логине
        var authInfo = DataHelper.getAuthInfo(); // создаём переменную, в которой поменяем логин на рандомный
        var authInfoWithInvalidLogin = new DataHelper.AuthInfo(DataHelper.getRandomLogin(), authInfo.getPassword()); // создаём данные для авторизации с рандомным логином
        loginPage.login(authInfo); // вводим данные для авторизации, нажимаем кнопку
        loginPage.checkErrorNotification("Ошибка! Неверно указан логин или пароль"); //получаем уведомление
    }

    @Test
    void shouldGetErrorNotificationWithInvalidPassword() { // должен получить уведомление об ошибке при невалидном пароле
        var authInfo = DataHelper.getAuthInfo(); // создаём переменную, в которой поменяем пароль на рандомный
        var authInfoWithInvalidPassword = new DataHelper.AuthInfo(authInfo.getLogin(), DataHelper.getRandomPassword()); // создаём данные для авторизации с рандомным паролем
        loginPage.login(authInfo); // вводим данные для авторизации, нажимаем кнопку
        loginPage.checkErrorNotification("Ошибка! Неверно указан логин или пароль"); //получаем уведомление
    }

    @Test
    void shouldGetErrorNotificationWithInvalidCode() { // должен получить уведомление об ошибке при невалидном коде подтверждения
        var verificationPage = loginPage.validLogin(authInfo); // вводим валидные имя и пароль, нажимаем кнопку, входим на страницу верификации        var verificationCode = DataHelper.getRandomVerificationCode(); // получаем рандомный код верификации
        var verificationCode = DataHelper.getRandomVerificationCode(); // получаем рандомный код верификации
        verificationPage.verify(String.valueOf(verificationCode)); // вводим рандомный код, нажимаем кнопку
        verificationPage.checkErrorNotification("Ошибка! Неверно указан код! Попробуйте ещё раз."); //получаем уведомление
    }

    @Test
    void shouldBlockSUTAfterThreeTimeInvalidPasswordInput() { // система должна заблокироваться после 3-кратного ввода неверного пароля
        var authInfo = DataHelper.getAuthInfo(); // создаём переменную, в которой будем менять пароль на рандомный
        var authInfo1 = new DataHelper.AuthInfo(authInfo.getLogin(), DataHelper.getRandomPassword()); // создаём 3 пары "логин-(неверный пароль)"
        var authInfo2 = new DataHelper.AuthInfo(authInfo.getLogin(), DataHelper.getRandomPassword());
        var authInfo3 = new DataHelper.AuthInfo(authInfo.getLogin(), DataHelper.getRandomPassword());
        loginPage.login(authInfo1); // попытка 1 войти с неверным паролем
        loginPage.clearFilds(); // очищаем поля после каждой попытки
        loginPage.login(authInfo2); // попытка 2 войти с неверным паролем
        loginPage.clearFilds();
        loginPage.login(authInfo3); // попытка 3 войти с неверным паролем
        loginPage.checkErrorNotification("Система заблокирована"); // проверяем уведомление о блокировке
    }
}
