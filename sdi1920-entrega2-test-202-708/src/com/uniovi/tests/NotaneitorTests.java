package com.uniovi.tests;

//Paquetes Java
import java.util.List;
//Paquetes JUnit 
import org.junit.*;
import org.junit.runners.MethodSorters;
import static org.junit.Assert.assertTrue;
//Paquetes Selenium 
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.*;
import org.openqa.selenium.support.ui.Sleeper;

import com.uniovi.tests.util.Internationalization;
//Paquetes Utilidades de Testing Propias
import com.uniovi.tests.util.SeleniumUtils;
//Paquetes con los Page Object
import com.uniovi.tests.pageobjects.*;

//Ordenamos las pruebas por el nombre del método
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NotaneitorTests {

	// En Windows (Debe ser la versi�n 65.0.1 y desactivar las actualizacioens
	// autom�ticas)):
	static String PathFirefox65 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
	static String Geckdriver024 = "D:\\Jaime\\Repositorios\\sdi1920-entrega2-202-708\\sdi1920-entrega2-test-202-708\\geckodriver024win64.exe";

	// Com�n a Windows y a MACOSX
	static WebDriver driver = getDriver(PathFirefox65, Geckdriver024);
	static String URL = "http://localhost:8081";

	public static WebDriver getDriver(String PathFirefox, String Geckdriver) {
		System.setProperty("webdriver.firefox.bin", PathFirefox);
		System.setProperty("webdriver.gecko.driver", Geckdriver);
		WebDriver driver = new FirefoxDriver();
		return driver;
	}

	@Before
	public void setUp() {
		driver.navigate().to(URL);
	}

	@After
	public void tearDown() {
		driver.manage().deleteAllCookies();
	}

	@BeforeClass
	static public void begin() {
		// COnfiguramos las pruebas.
		// Fijamos el timeout en cada opción de carga de una vista. 2 segundos.
		PO_View.setTimeout(3);

	}

	@AfterClass
	static public void end() {
		// Cerramos el navegador al finalizar las pruebas
		driver.quit();
	}

	/**
	 * [Prueba1] Registro de Usuario con datos v�lidos.
	 */
	@Test
	public void test01() {
		SeleniumUtils.clickOption(driver, "signup", "class", "btn btn-primary");

		SeleniumUtils.fillFormRegister(driver, "prueba1@prueba.com", "Prueba1", "Prueba1", "123456", "123456");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Nuevo usuario registrado", 2);

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba2] Registro de Usuario con datos inv�lidos (email vac�o, nombre vac�o,
	 * apellidos vac�os).
	 */
	@Test
	public void test02() {
		SeleniumUtils.clickOption(driver, "signup", "class", "btn btn-primary");

		SeleniumUtils.fillFormRegister(driver, "", "Prueba2", "Prueba2", "123456", "123456");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Reg�strate como usuario", 2);

		SeleniumUtils.fillFormRegister(driver, "prueba2@prueba.com", "", "Prueba2", "123456", "123456");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Reg�strate como usuario", 2);

		SeleniumUtils.fillFormRegister(driver, "prueba2@prueba.com", "Prueba2", "", "123456", "123456");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Reg�strate como usuario", 2);
	}

	/**
	 * [Prueba3] Registro de Usuario con datos inv�lidos (repetici�n de contrase�a
	 * inv�lida).
	 */
	@Test
	public void test03() {
		SeleniumUtils.clickOption(driver, "signup", "class", "btn btn-primary");

		SeleniumUtils.fillFormRegister(driver, "prueba3@prueba.com", "Prueba3", "Prueba3", "123456", "123457");

		SeleniumUtils.checkKey(driver, "Error.signup.passwordConfirm.coincidence", Internationalization.getSPANISH());
	}

	/**
	 * [Prueba4] Registro de Usuario con datos inv�lidos (email existente).
	 */
	@Test
	public void test04() {
		SeleniumUtils.clickOption(driver, "signup", "class", "btn btn-primary");

		SeleniumUtils.fillFormRegister(driver, "prueba1@prueba.com", "Prueba1", "Prueba1", "123456", "123456");

		SeleniumUtils.checkKey(driver, "Error.signup.email.duplicate", Internationalization.getSPANISH());
	}

	/**
	 * [Prueba5] Inicio de sesi�n con datos v�lidos (administrador).
	 */
	@Test
	public void test05() {
		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "admin@email.com", "admin");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Inicio de sesi�n como admin", 2);

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba6] Inicio de sesi�n con datos v�lidos (usuario est�ndar).
	 */
	@Test
	public void test06() {
		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "user1@email.com", "user1");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Inicio de sesi�n como user", 2);

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba7] Inicio de sesi�n con datos inv�lidos (usuario est�ndar, campo email
	 * y contrase�a vac�os).
	 */
	@Test
	public void test07() {
		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "", "user");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Your username and password is invalid.", 2);

		SeleniumUtils.fillFormLogin(driver, "user1@email.com", "");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Your username and password is invalid.", 2);
	}

	/**
	 * [Prueba8] Inicio de sesi�n con datos v�lidos (usuario est�ndar, email
	 * existente, pero contrase�a incorrecta).
	 */
	@Test
	public void test08() {
		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "user1@email.com", "wrongPassword");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Your username and password is invalid.", 2);
	}
}
