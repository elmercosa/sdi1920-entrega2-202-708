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

//Ordenamos las pruebas por el nombre del mÃ©todo
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NotaneitorTests {

	// En Windows (Debe ser la versión 65.0.1 y desactivar las actualizacioens
	// automáticas)):
	static String PathFirefox65 = "C:\\Program Files\\Mozilla Firefox\\firefox.exe";
	static String Geckdriver024 = "D:\\Jaime\\Repositorios\\sdi1920-entrega2-202-708\\sdi1920-entrega2-test-202-708\\geckodriver024win64.exe";

	// Común a Windows y a MACOSX
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
		// Fijamos el timeout en cada opciÃ³n de carga de una vista. 2 segundos.
		PO_View.setTimeout(3);

	}

	@AfterClass
	static public void end() {
		// Cerramos el navegador al finalizar las pruebas
		driver.quit();
	}

	/**
	 * [Prueba1] Registro de Usuario con datos válidos.
	 */
	@Test
	public void test01() {
		SeleniumUtils.clickOption(driver, "signup", "class", "btn btn-primary");

		SeleniumUtils.fillFormRegister(driver, "prueba1@prueba.com", "Prueba1", "Prueba1", "123456", "123456");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Nuevo usuario registrado", 2);

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba2] Registro de Usuario con datos inválidos (email vacío, nombre vacío,
	 * apellidos vacíos).
	 */
	@Test
	public void test02() {
		SeleniumUtils.clickOption(driver, "signup", "class", "btn btn-primary");

		SeleniumUtils.fillFormRegister(driver, "", "Prueba2", "Prueba2", "123456", "123456");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Regístrate como usuario", 2);

		SeleniumUtils.fillFormRegister(driver, "prueba2@prueba.com", "", "Prueba2", "123456", "123456");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Regístrate como usuario", 2);

		SeleniumUtils.fillFormRegister(driver, "prueba2@prueba.com", "Prueba2", "", "123456", "123456");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Regístrate como usuario", 2);
	}

	/**
	 * [Prueba3] Registro de Usuario con datos inválidos (repetición de contraseña
	 * inválida).
	 */
	@Test
	public void test03() {
		SeleniumUtils.clickOption(driver, "signup", "class", "btn btn-primary");

		SeleniumUtils.fillFormRegister(driver, "prueba3@prueba.com", "Prueba3", "Prueba3", "123456", "123457");

		SeleniumUtils.checkKey(driver, "Error.signup.passwordConfirm.coincidence", Internationalization.getSPANISH());
	}

	/**
	 * [Prueba4] Registro de Usuario con datos inválidos (email existente).
	 */
	@Test
	public void test04() {
		SeleniumUtils.clickOption(driver, "signup", "class", "btn btn-primary");

		SeleniumUtils.fillFormRegister(driver, "prueba1@prueba.com", "Prueba1", "Prueba1", "123456", "123456");

		SeleniumUtils.checkKey(driver, "Error.signup.email.duplicate", Internationalization.getSPANISH());
	}

	/**
	 * [Prueba5] Inicio de sesión con datos válidos (administrador).
	 */
	@Test
	public void test05() {
		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "admin@email.com", "admin");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Inicio de sesión como admin", 2);

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba6] Inicio de sesión con datos válidos (usuario estándar).
	 */
	@Test
	public void test06() {
		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "user1@email.com", "user1");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Inicio de sesión como user", 2);

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba7] Inicio de sesión con datos inválidos (usuario estándar, campo email
	 * y contraseña vacíos).
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
	 * [Prueba8] Inicio de sesión con datos válidos (usuario estándar, email
	 * existente, pero contraseña incorrecta).
	 */
	@Test
	public void test08() {
		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "user1@email.com", "wrongPassword");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Your username and password is invalid.", 2);
	}
	
	/**
	 * [Prueba9] Hacer click en la opción de salir de sesión y comprobar que se
	 * redirige a la página de inicio de sesión (Login).
	 */
	@Test
	public void test09() {
		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "admin@email.com", "admin");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Inicio de sesión como admin", 2);

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba10] Comprobar que el botón cerrar sesión no está visible si el usuario
	 * no está autenticado.
	 */
	@Test
	public void test10() {
		SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "Desconectar", 2);
	}
	
	/**
	 * [Prueba11] Mostrar el listado de usuarios y comprobar que se muestran todos
	 * los que existen en el sistema.
	 */
	@Test
	public void test11() {
		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "user1@email.com", "user1");

		List<WebElement> elementos = SeleniumUtils.checkElement(driver, "free", "//li[contains(@id, 'users-menu')]/a");
		elementos.get(0).click();

		elementos = SeleniumUtils.checkElement(driver, "free", "//a[contains(@href,'user/list')]");
		elementos.get(0).click();

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", 2);
		assertTrue(elementos.size() == 4);

		SeleniumUtils.logout(driver);
	}
}
