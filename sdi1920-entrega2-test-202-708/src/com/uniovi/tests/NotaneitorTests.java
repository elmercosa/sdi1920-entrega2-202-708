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

	/**
	 * [Prueba9] Hacer click en la opci�n de salir de sesi�n y comprobar que se
	 * redirige a la p�gina de inicio de sesi�n (Login).
	 */
	@Test
	public void test09() {
		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "admin@email.com", "admin");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Inicio de sesi�n como admin", 2);

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba10] Comprobar que el bot�n cerrar sesi�n no est� visible si el usuario
	 * no est� autenticado.
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

	/**
	 * [Prueba12] Hacer una b�squeda con el campo vac�o y comprobar que se muestra
	 * la p�gina que corresponde con el listado usuarios existentes en el sistema.
	 */
	@Test
	public void test12() {
		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "user1@email.com", "user1");

		SeleniumUtils.searchUsers(driver, "");

		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", 2);
		assertTrue(elementos.size() == 4);

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba13] Hacer una b�squeda escribiendo en el campo un texto que no exista
	 * y comprobar que se muestra la p�gina que corresponde, con la lista de
	 * usuarios vac�a.
	 */
	@Test
	public void test13() {
		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "user1@email.com", "user1");

		SeleniumUtils.searchUsers(driver, "wrong");

		SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "wrong", 2);

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba14] Hacer una b�squeda con un texto espec�fico y comprobar que se
	 * muestra la p�gina que corresponde, con la lista de usuarios en los que el
	 * texto especificados sea parte de su nombre, apellidos o de su email.
	 */
	@Test
	public void test14() {
		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "user1@email.com", "user1");

		SeleniumUtils.searchUsers(driver, "user");

		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", 2);
		assertTrue(elementos.size() == 3);

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba15] Desde el listado de usuarios de la aplicaci�n, enviar una
	 * invitaci�n de amistad a un usuario. Comprobar que la solicitud de amistad
	 * aparece en el listado de invitaciones (punto siguiente).
	 */
	@Test
	public void test15() {
		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "user1@email.com", "user1");

		SeleniumUtils.searchUsers(driver, "user2@email.com");

		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", 2);
		assertTrue(elementos.size() == 1);

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "agregar amigo", 2);
		assertTrue(elementos.size() == 1);

		elementos.get(0).click();

		SeleniumUtils.logout(driver);

		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "user2@email.com", "user2");

		elementos = SeleniumUtils.checkElement(driver, "free", "//li[contains(@id, 'users-menu')]/a");
		elementos.get(0).click();

		elementos = SeleniumUtils.checkElement(driver, "free", "//a[contains(@href,'friend/request')]");
		elementos.get(0).click();

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", 2);
		assertTrue(elementos.size() == 1);

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba16] Desde el listado de usuarios de la aplicaci�n, enviar una
	 * invitaci�n de amistad a un usuario al que ya le hab�amos enviado la
	 * invitaci�n previamente. No deber�a dejarnos enviar la invitaci�n, se podr�a
	 * ocultar el bot�n de enviar invitaci�n o notificar que ya hab�a sido enviada
	 * previamente.
	 */
	@Test
	public void test16() {
		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "user1@email.com", "user1");

		SeleniumUtils.searchUsers(driver, "user2@email.com");

		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", 2);
		assertTrue(elementos.size() == 1);

		SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "agregar amigo", 2);

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba17] Mostrar el listado de invitaciones de amistad recibidas. Comprobar
	 * con un listado que contenga varias invitaciones recibidas.
	 */
	@Test
	public void test17() {
		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "user3@email.com", "user3");

		SeleniumUtils.searchUsers(driver, "user2@email.com");

		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", 2);
		assertTrue(elementos.size() == 1);

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "agregar amigo", 2);
		assertTrue(elementos.size() == 1);

		elementos.get(0).click();

		SeleniumUtils.logout(driver);

		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "user2@email.com", "user2");

		elementos = SeleniumUtils.checkElement(driver, "free", "//li[contains(@id, 'users-menu')]/a");
		elementos.get(0).click();

		elementos = SeleniumUtils.checkElement(driver, "free", "//a[contains(@href,'friend/request')]");
		elementos.get(0).click();

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", 2);
		assertTrue(elementos.size() == 2);

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba18] Sobre el listado de invitaciones recibidas. Hacer click en el
	 * bot�n/enlace de una de ellas y comprobar que dicha solicitud desaparece del
	 * listado de invitaciones.
	 */
	@Test
	public void test18() {
		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "user2@email.com", "user2");

		List<WebElement> elementos = SeleniumUtils.checkElement(driver, "free", "//li[contains(@id, 'users-menu')]/a");
		elementos.get(0).click();

		elementos = SeleniumUtils.checkElement(driver, "free", "//a[contains(@href,'friend/request')]");
		elementos.get(0).click();

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", 2);
		assertTrue(elementos.size() == 2);

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "@href", "friend/acept", 2);
		assertTrue(elementos.size() == 2);
		elementos.get(0).click();

		SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "User1", 2);

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba19] Mostrar el listado de amigos de un usuario. Comprobar que el
	 * listado contiene los amigos que deben ser.
	 */
	@Test
	public void test19() {
		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "user2@email.com", "user2");

		List<WebElement> elementos = SeleniumUtils.checkElement(driver, "free", "//li[contains(@id, 'users-menu')]/a");
		elementos.get(0).click();

		elementos = SeleniumUtils.checkElement(driver, "free", "//a[contains(@href,'friend/list')]");
		elementos.get(0).click();

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", 2);
		assertTrue(elementos.size() == 1);

		SeleniumUtils.checkElement(driver, "text", "User1");

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba20] Visualizar al menos cuatro p�ginas en Espa�ol/Ingl�s/Espa�ol
	 * (comprobando que algunas de las etiquetas cambian al idioma correspondiente).
	 * Ejemplo, P�gina principal/Opciones Principales de Usuario/Listado de
	 * Usuarios.
	 */
	@Test
	public void test20() throws Exception {
		SeleniumUtils.EsperaCargaPagina(driver, "text",
				SeleniumUtils.p.getString("welcome.message", Internationalization.getSPANISH()), 2);
		SeleniumUtils.changeLanguage(driver, "btnEnglish");
		SeleniumUtils.EsperaCargaPagina(driver, "text",
				SeleniumUtils.p.getString("welcome.message", Internationalization.getENGLISH()), 2);
		SeleniumUtils.changeLanguage(driver, "btnSpanish");
		SeleniumUtils.EsperaCargaPagina(driver, "text",
				SeleniumUtils.p.getString("welcome.message", Internationalization.getSPANISH()), 2);

		SeleniumUtils.clickOption(driver, "signup", "class", "btn btn-primary");

		SeleniumUtils.EsperaCargaPagina(driver, "text",
				SeleniumUtils.p.getString("signup.message", Internationalization.getSPANISH()), 2);
		SeleniumUtils.changeLanguage(driver, "btnEnglish");
		SeleniumUtils.EsperaCargaPagina(driver, "text",
				SeleniumUtils.p.getString("signup.message", Internationalization.getENGLISH()), 2);
		SeleniumUtils.changeLanguage(driver, "btnSpanish");
		SeleniumUtils.EsperaCargaPagina(driver, "text",
				SeleniumUtils.p.getString("signup.message", Internationalization.getSPANISH()), 2);

		SeleniumUtils.clickOption(driver, "login", "class", "btn btn-primary");

		SeleniumUtils.EsperaCargaPagina(driver, "text",
				SeleniumUtils.p.getString("login.message", Internationalization.getSPANISH()), 2);
		SeleniumUtils.changeLanguage(driver, "btnEnglish");
		SeleniumUtils.EsperaCargaPagina(driver, "text",
				SeleniumUtils.p.getString("login.message", Internationalization.getENGLISH()), 2);
		SeleniumUtils.changeLanguage(driver, "btnSpanish");
		SeleniumUtils.EsperaCargaPagina(driver, "text",
				SeleniumUtils.p.getString("login.message", Internationalization.getSPANISH()), 2);

		SeleniumUtils.fillFormLogin(driver, "user1@email.com", "user1");

		SeleniumUtils.EsperaCargaPagina(driver, "text",
				SeleniumUtils.p.getString("zona.privada.message", Internationalization.getSPANISH()), 2);
		SeleniumUtils.changeLanguage(driver, "btnEnglish");
		SeleniumUtils.EsperaCargaPagina(driver, "text",
				SeleniumUtils.p.getString("zona.privada.message", Internationalization.getENGLISH()), 2);
		SeleniumUtils.changeLanguage(driver, "btnSpanish");
		SeleniumUtils.EsperaCargaPagina(driver, "text",
				SeleniumUtils.p.getString("zona.privada.message", Internationalization.getSPANISH()), 2);

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba21] Intentar acceder sin estar autenticado a la opci�n de listado de
	 * usuarios. Se deber� volver al formulario de login.
	 */
	@Test
	public void test21() throws Exception {
		driver.get("http://localhost:8081/user/list");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Nombre de usuario", 2);
	}

	/**
	 * [Prueba22] Intentar acceder sin estar autenticado a la opci�n de listado de
	 * publicaciones de un usuario est�ndar. Se deber� volver al formulario de
	 * login.
	 */
	@Test
	public void test22() throws Exception {
		driver.get("http://localhost:8081/friend/list");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Nombre de usuario", 2);
	}
}
