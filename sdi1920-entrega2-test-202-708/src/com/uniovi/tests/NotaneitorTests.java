package com.uniovi.tests;

import static org.junit.Assert.assertTrue;

//Paquetes Java
import java.util.List;

//Paquetes JUnit 
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
//Paquetes Selenium 
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

//Paquetes con los Page Object
import com.uniovi.tests.pageobjects.PO_View;
//Paquetes Utilidades de Testing Propias
import com.uniovi.tests.util.SeleniumUtils;

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
	public void PR01() {
		SeleniumUtils.clickOption(driver, "/registrarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormRegister(driver, "prueba1@prueba.com", "Prueba1", "Prueba1", "123456", "123456");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Identificación de usuario", 2);
	}

	/**
	 * [Prueba2] Registro de Usuario con datos inv�lidos (email vac�o, nombre vac�o,
	 * apellidos vac�os).
	 */
	@Test
	public void PR02() {
		SeleniumUtils.clickOption(driver, "/registrarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormRegister(driver, "", "Prueba2", "Prueba2", "123456", "123456");

		SeleniumUtils.textoPresentePagina(driver, "Registrar usuario");

		SeleniumUtils.fillFormRegister(driver, "prueba2@prueba.com", "", "Prueba2", "123456", "123456");

		SeleniumUtils.textoPresentePagina(driver, "Registrar usuario");

		SeleniumUtils.fillFormRegister(driver, "prueba2@prueba.com", "Prueba2", "", "123456", "123456");

		SeleniumUtils.textoPresentePagina(driver, "Registrar usuario");
	}

	/**
	 * [Prueba3] Registro de Usuario con datos inv�lidos (repetici�n de contrase�a
	 * inv�lida).
	 */
	@Test
	public void PR03() {
		SeleniumUtils.clickOption(driver, "/registrarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormRegister(driver, "prueba3@prueba.com", "Prueba3", "Prueba3", "123456", "123457");

		SeleniumUtils.textoPresentePagina(driver, "Las contraseñas no coinciden");
	}

	/**
	 * [Prueba4] Registro de Usuario con datos inv�lidos (email existente).
	 */
	@Test
	public void PR04() {
		SeleniumUtils.clickOption(driver, "/registrarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormRegister(driver, "prueba1@prueba.com", "Prueba1", "Prueba1", "123456", "123456");

		SeleniumUtils.textoPresentePagina(driver,
				"Ya está existe un usuario registrado con ese email. Por favor utilice otro");
	}

	/**
	 * [Prueba5] Inicio de sesi�n con datos v�lidos (usuario est�ndar).
	 */
	@Test
	public void PR05() {
		SeleniumUtils.clickOption(driver, "/identificarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "prueba1@prueba.com", "123456");

		SeleniumUtils.textoPresentePagina(driver, "Lista de usuarios");

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba6] Inicio de sesi�n con datos inv�lidos (usuario est�ndar, campo email
	 * y contrase�a vac�os).
	 */
	@Test
	public void PR06() {
		SeleniumUtils.clickOption(driver, "/identificarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "", "user");

		SeleniumUtils.textoPresentePagina(driver, "Identificación de usuario");

		SeleniumUtils.fillFormLogin(driver, "user1@email.com", "");

		SeleniumUtils.textoPresentePagina(driver, "Identificación de usuario");
	}

	/**
	 * [Prueba7] Inicio de sesión con datos inválidos (usuario estándar, email
	 * existente, pero contraseña incorrecta).
	 */
	@Test
	public void PR07() {
		SeleniumUtils.clickOption(driver, "/identificarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "prueba1@prueba.com", "wrongPassword");

		SeleniumUtils.textoPresentePagina(driver, "Email o password incorrecto ");
	}

	/**
	 * [Prueba8] Inicio de sesión con datos inválidos (usuario estándar, email no
	 * existente y contraseña no vacía).
	 */
	@Test
	public void PR08() {
		SeleniumUtils.clickOption(driver, "/identificarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "prueba2@prueba.com", "wrongPassword");

		SeleniumUtils.textoPresentePagina(driver, "Email o password incorrecto ");
	}

	/**
	 * [Prueba9] Hacer click en la opci�n de salir de sesi�n y comprobar que se
	 * redirige a la p�gina de inicio de sesi�n (Login).
	 */
	@Test
	public void PR09() {
		SeleniumUtils.clickOption(driver, "/identificarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "prueba1@prueba.com", "123456");

		SeleniumUtils.textoPresentePagina(driver, "Lista de usuarios");

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba10] Comprobar que el bot�n cerrar sesi�n no est� visible si el usuario
	 * no est� autenticado.
	 */
	@Test
	public void PR10() {
		SeleniumUtils.EsperaCargaPaginaNoTexto(driver, "Desconectar", 2);
	}

	/**
	 * [Prueba11] Mostrar el listado de usuarios y comprobar que se muestran todos
	 * los que existen en el sistema.
	 */
	@Test
	public void PR11() {
		SeleniumUtils.clickOption(driver, "/identificarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "prueba1@prueba.com", "123456");

		List<WebElement> elementos = SeleniumUtils.checkElement(driver, "free",
				"//a[contains(@href,'usuarios/lista')]");
		elementos.get(0).click();

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", 2);
		assertTrue(elementos.size() == 5);

		elementos = SeleniumUtils.checkElement(driver, "free", "//a[contains(@href,'usuarios/lista?pg=2')]");
		elementos.get(0).click();

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", 2);
		assertTrue(elementos.size() == 5);

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba12] Hacer una b�squeda con el campo vac�o y comprobar que se muestra
	 * la p�gina que corresponde con el listado usuarios existentes en el sistema.
	 */
	@Test
	public void PR12() {
		SeleniumUtils.clickOption(driver, "/identificarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "prueba1@prueba.com", "123456");

		SeleniumUtils.searchUsers(driver, "");

		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", 2);
		assertTrue(elementos.size() == 5);

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba13] Hacer una b�squeda escribiendo en el campo un texto que no exista
	 * y comprobar que se muestra la p�gina que corresponde, con la lista de
	 * usuarios vac�a.
	 */
	@Test
	public void PR13() {
		SeleniumUtils.clickOption(driver, "/identificarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "prueba1@prueba.com", "123456");

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
	public void PR14() {
		SeleniumUtils.clickOption(driver, "/identificarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "prueba1@prueba.com", "123456");

		SeleniumUtils.searchUsers(driver, "Prueba");

		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", 2);
		assertTrue(elementos.size() == 1);

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba15] Desde el listado de usuarios de la aplicaci�n, enviar una
	 * invitaci�n de amistad a un usuario. Comprobar que la solicitud de amistad
	 * aparece en el listado de invitaciones (punto siguiente).
	 */
	@Test
	public void PR15() {
		SeleniumUtils.clickOption(driver, "/identificarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "prueba1@prueba.com", "123456");

		SeleniumUtils.searchUsers(driver, "Jaime");

		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", 2);
		assertTrue(elementos.size() == 1);

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "agregar amigo", 2);
		assertTrue(elementos.size() == 1);

		elementos.get(0).click();

		SeleniumUtils.textoPresentePagina(driver, "Peticion enviada ");

		SeleniumUtils.logout(driver);

		SeleniumUtils.clickOption(driver, "/identificarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "jaime@jaime.com", "123456");

		elementos = SeleniumUtils.checkElement(driver, "free", "//a[contains(@href,'amigo/peticiones/lista')]");
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
	public void PR16() {
		SeleniumUtils.clickOption(driver, "/identificarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "prueba1@prueba.com", "123456");

		SeleniumUtils.searchUsers(driver, "Jaime");

		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", 2);
		assertTrue(elementos.size() == 1);

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "agregar amigo", 2);
		assertTrue(elementos.size() == 1);

		elementos.get(0).click();

		SeleniumUtils.textoPresentePagina(driver, "Ya ha enviado una peticion de amistad a este usuario");

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba17] Mostrar el listado de invitaciones de amistad recibidas. Comprobar
	 * con un listado que contenga varias invitaciones recibidas.
	 */
	@Test
	public void PR17() {
		SeleniumUtils.clickOption(driver, "/identificarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "peticion@peticion.com", "123456");

		SeleniumUtils.searchUsers(driver, "Jaime");

		List<WebElement> elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", 2);
		assertTrue(elementos.size() == 1);

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "agregar amigo", 2);
		assertTrue(elementos.size() == 1);

		elementos.get(0).click();

		SeleniumUtils.logout(driver);

		SeleniumUtils.clickOption(driver, "/identificarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "jaime@jaime.com", "123456");

		elementos = SeleniumUtils.checkElement(driver, "free", "//a[contains(@href,'amigo/peticiones/lista')]");
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
	public void PR18() {
		SeleniumUtils.clickOption(driver, "/identificarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "jaime@jaime.com", "123456");

		List<WebElement> elementos = SeleniumUtils.checkElement(driver, "free",
				"//a[contains(@href,'amigo/peticiones/lista')]");
		elementos.get(0).click();

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", 2);
		assertTrue(elementos.size() == 2);

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "text", "aceptar", 2);
		assertTrue(elementos.size() == 2);

		elementos.get(1).click();
		
		SeleniumUtils.textoNoPresentePagina(driver, "Prueba1");
		
		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba19] Mostrar el listado de amigos de un usuario. Comprobar que el
	 * listado contiene los amigos que deben ser.
	 */
	@Test
	public void PR19() {
		SeleniumUtils.clickOption(driver, "/identificarse", "class", "btn btn-primary");

		SeleniumUtils.fillFormLogin(driver, "jaime@jaime.com", "123456");

		List<WebElement> elementos = SeleniumUtils.checkElement(driver, "free", "//a[contains(@href,'amigo/lista')]");
		elementos.get(0).click();

		elementos = SeleniumUtils.EsperaCargaPagina(driver, "free", "//tbody/tr", 2);
		assertTrue(elementos.size() == 1);

		SeleniumUtils.textoPresentePagina(driver, "Prueba1");

		SeleniumUtils.logout(driver);
	}

	/**
	 * [Prueba20] Intentar acceder sin estar autenticado a la opción de listado de
	 * usuarios. Se deberá volver al formulario de login.
	 */
	@Test
	public void PR20() throws Exception {
		driver.get(URL + "/usuarios/lista");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Identificación de usuario", 2);
	}

	/**
	 * [Prueba21] Intentar acceder sin estar autenticado a la opción de listado de
	 * invitaciones de amistad recibida de un usuario estándar. Se deberá volver al
	 * formulario de login.
	 */
	@Test
	public void PR21() throws Exception {
		driver.get(URL + "/amigo/peticiones/lista");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Identificación de usuario", 2);
	}

	/**
	 * [Prueba22] Intentar acceder estando autenticado como usuario standard a la
	 * lista de amigos de otro usuario. Se deberá mostrar un mensaje de acción
	 * indebida.
	 */
	@Test
	public void PR22() throws Exception {
		driver.get(URL + "/amigo/lista");

		SeleniumUtils.EsperaCargaPagina(driver, "text", "Identificación de usuario", 2);
	}
}
