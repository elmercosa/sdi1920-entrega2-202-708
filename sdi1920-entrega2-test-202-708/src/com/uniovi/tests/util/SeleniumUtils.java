package com.uniovi.tests.util;

import java.util.List;

import static org.junit.Assert.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SeleniumUtils {

	protected static Internationalization p = new Internationalization("messages");
	
	/**
	 * Aborta si el "texto" no está presente en la página actual
	 * 
	 * @param driver: apuntando al navegador abierto actualmente.
	 * @param texto: texto a buscar
	 */
	static public void textoPresentePagina(WebDriver driver, String texto) {
		List<WebElement> list = driver.findElements(By.xpath("//*[contains(text(),'" + texto + "')]"));
		assertTrue("Texto " + texto + " no localizado!", list.size() > 0);
	}

	/**
	 * Aborta si el "texto" está presente en la página actual
	 * 
	 * @param driver: apuntando al navegador abierto actualmente.
	 * @param texto: texto a buscar
	 */
	static public void textoNoPresentePagina(WebDriver driver, String texto) {
		List<WebElement> list = driver.findElements(By.xpath("//*[contains(text(),'" + texto + "')]"));
		assertTrue("Texto " + texto + " aun presente !", list.size() == 0);
	}

	/**
	 * Aborta si el "texto" está presente en la página actual tras timeout
	 * segundos.
	 * 
	 * @param driver: apuntando al navegador abierto actualmente.
	 * @param texto: texto a buscar
	 * @param timeout: el tiempo máximo que se esperará por la aparición del
	 *        texto a buscar
	 */
	static public void EsperaCargaPaginaNoTexto(WebDriver driver, String texto, int timeout) {
		Boolean resultado = (new WebDriverWait(driver, timeout)).until(
				ExpectedConditions.invisibilityOfElementLocated(By.xpath("//*[contains(text(),'" + texto + "')]")));

		assertTrue(resultado);
	}

	/**
	 * Espera por la visibilidad de un elemento/s en la vista actualmente cargandose
	 * en driver. Para ello se empleará una consulta xpath.
	 * 
	 * @param driver: apuntando al navegador abierto actualmente.
	 * @param xpath: consulta xpath.
	 * @param timeout: el tiempo máximo que se esperará por la aparición del
	 *        elemento a buscar con xpath
	 * @return Se retornará la lista de elementos resultantes de la búsqueda con
	 *         xpath.
	 */
	static public List<WebElement> EsperaCargaPaginaxpath(WebDriver driver, String xpath, int timeout) {
		WebElement resultado = (new WebDriverWait(driver, timeout))
				.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
		assertTrue(resultado != null);
		List<WebElement> elementos = driver.findElements(By.xpath(xpath));

		return elementos;
	}

	/**
	 * Espera por la visibilidad de un elemento/s en la vista actualmente cargandose
	 * en driver. Para ello se empleará una consulta xpath según varios
	 * criterios..
	 * 
	 * @param driver: apuntando al navegador abierto actualmente.
	 * @param criterio: "id" or "class" or "text" or "@attribute" or "free". Si el
	 *        valor de criterio es free es una expresion xpath completa.
	 * @param text: texto correspondiente al criterio.
	 * @param timeout: el tiempo máximo que se esperará por la apareción del
	 *        elemento a buscar con criterio/text.
	 * @return Se retornará la lista de elementos resultantes de la búsqueda.
	 */
	static public List<WebElement> EsperaCargaPagina(WebDriver driver, String criterio, String text, int timeout) {
		String busqueda;
		if (criterio.equals("id"))
			busqueda = "//*[contains(@id,'" + text + "')]";
		else if (criterio.equals("class"))
			busqueda = "//*[contains(@class,'" + text + "')]";
		else if (criterio.equals("text"))
			busqueda = "//*[contains(text(),'" + text + "')]";
		else if (criterio.equals("free"))
			busqueda = text;
		else
			busqueda = "//*[contains(" + criterio + ",'" + text + "')]";

		return EsperaCargaPaginaxpath(driver, busqueda, timeout);
	}

	/**
	 * PROHIBIDO USARLO PARA VERSIÓN FINAL. Esperar "segundos" durante la ejecucion
	 * del navegador
	 * 
	 * @param driver: apuntando al navegador abierto actualmente.
	 * @param segundos: Segundos de bloqueo de la ejecución en el navegador.
	 */
	static public void esperarSegundos(WebDriver driver, int segundos) {

		synchronized (driver) {
			try {
				driver.wait(segundos * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	static public List<WebElement> checkKey(WebDriver driver, String key, int locale) {
		List<WebElement> elementos = EsperaCargaPagina(driver, "text", p.getString(key, locale), 2);
		return elementos;
	}

	static public List<WebElement> checkElement(WebDriver driver, String type, String text) {
		List<WebElement> elementos = EsperaCargaPagina(driver, type, text, 2);
		return elementos;
	}

	static public void clickOption(WebDriver driver, String textOption, String criterio, String textoDestino) {
		// CLickamos en la opci�n de registro y esperamos a que se cargue el enlace de
		// Registro.
		List<WebElement> elementos = EsperaCargaPagina(driver, "@href", textOption, 2);
		// Tiene que haber un s�lo elemento.
		assertTrue(elementos.size() == 1);
		// Ahora lo clickamos
		elementos.get(0).click();
		// Esperamos a que sea visible un elemento concreto
		elementos = EsperaCargaPagina(driver, criterio, textoDestino, 2);
		// Tiene que haber un s�lo elemento.
		assertTrue(elementos.size() == 1);
	}

	static public void fillFormRegister(WebDriver driver, String emailp, String namep, String lastnamep,
			String passwordp, String passwordconfp) {
		WebElement email = driver.findElement(By.name("email"));
		email.click();
		email.clear();
		email.sendKeys(emailp);
		WebElement name = driver.findElement(By.name("name"));
		name.click();
		name.clear();
		name.sendKeys(namep);
		WebElement lastname = driver.findElement(By.name("lastName"));
		lastname.click();
		lastname.clear();
		lastname.sendKeys(lastnamep);
		WebElement password = driver.findElement(By.name("password"));
		password.click();
		password.clear();
		password.sendKeys(passwordp);
		WebElement passwordConfirm = driver.findElement(By.name("passwordConfirm"));
		passwordConfirm.click();
		passwordConfirm.clear();
		passwordConfirm.sendKeys(passwordconfp);
		// Pulsar el boton de Alta.
		By boton = By.className("btn");
		driver.findElement(boton).click();
	}
	
	static public void logout(WebDriver driver) {
		clickOption(driver, "logout", "text", "You have been logged out successfully.");

		checkElement(driver, "text", "You have been logged out successfully.");
	}
	
	static public void fillFormLogin(WebDriver driver, String usernamep, String passwordp) {
		WebElement username = driver.findElement(By.name("username"));
		username.click();
		username.clear();
		username.sendKeys(usernamep);
		WebElement password = driver.findElement(By.name("password"));
		password.click();
		password.clear();
		password.sendKeys(passwordp);
		// Pulsar el boton de Alta.
		By boton = By.className("btn");
		driver.findElement(boton).click();
	}
	
	static public void searchUsers(WebDriver driver, String text) {
		List<WebElement> elementos = checkElement(driver, "free", "//li[contains(@id, 'users-menu')]/a");
		elementos.get(0).click();

		elementos = checkElement(driver, "free", "//a[contains(@href,'user/list')]");
		elementos.get(0).click();

		WebElement search = driver.findElement(By.name("searchText"));
		search.click();
		search.clear();
		search.sendKeys(text);
		By boton = By.className("btn");
		driver.findElement(boton).click();
	}
}
