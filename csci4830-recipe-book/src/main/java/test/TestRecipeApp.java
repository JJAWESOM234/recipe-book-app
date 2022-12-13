package test;

import java.util.regex.Pattern;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import org.junit.runners.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import org.openqa.selenium.support.ui.Select;

import util.DBConnection;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestRecipeApp {
	private WebDriver driver;
	private String baseUrl;
	private boolean acceptNextAlert = true;
	private StringBuffer verificationErrors = new StringBuffer();
	private String username = "test";
	private String password = "testing";

	@Before
	public void Init() throws Exception {
		System.setProperty("webdriver.chrome.driver",
				"E:\\workspaceCSCI4830\\Project\\csci4830-recipe-book\\src\\main\\webapp\\WEB-INF\\lib\\chromedriver\\chromedriver.exe");
		driver = new ChromeDriver();
		baseUrl = "https://www.google.com/";
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
	}

	@Test
	public void testRecipeHomePage() throws Exception {
		driver.get(
				"http://ec2-3-128-33-102.us-east-2.compute.amazonaws.com:8080/csci4830-recipe-book/SearchRecipeList");

		// Testing HomePage
		WebElement table = driver.findElement(By.xpath("/html/body/table"));

		Assert.assertTrue(table.isDisplayed());

	}

	@Test
	public void testRecipeSeperatePageAndBack() throws Exception {
		driver.get(
				"http://ec2-3-128-33-102.us-east-2.compute.amazonaws.com:8080/csci4830-recipe-book/SearchRecipeList");
		WebElement recipeLink = driver.findElement(By.xpath("/html/body/table/tbody/tr[1]/td[1]/form/button"));

		recipeLink.click();

		WebElement recipeInstructions = driver.findElement(By.xpath("/html/body/div/div[4]/p"));

		String recipeInstructionsText = recipeInstructions.getText();

		Assert.assertEquals("mix water and pancake mix until smooth, fry on skillet.", recipeInstructionsText);
		// Testing going to homepage from recipe page
		WebElement homePageLink = driver.findElement(By.xpath("/html/body/nav/a[1]"));

		homePageLink.click();

		WebElement table = driver.findElement(By.xpath("/html/body/table"));
		Assert.assertTrue(table.isDisplayed());
	}

	@Test
	public void testRecipeSearch() throws Exception {
		driver.get(
				"http://ec2-3-128-33-102.us-east-2.compute.amazonaws.com:8080/csci4830-recipe-book/SearchRecipeList");

		Assert.assertEquals("Cheeseburger",
				driver.findElement(By.xpath("/html/body/table/tbody/tr[3]/td[1]/form/button")).getText());
		Assert.assertEquals("Pancakes",
				driver.findElement(By.xpath("/html/body/table/tbody/tr[1]/td[1]/form/button")).getText());

		driver.findElement(By.xpath("/html/body/form/input")).sendKeys("Panc");
		driver.findElement(By.xpath("/html/body/form/button")).click();

		WebElement tableContentSearchOne = driver.findElement(By.xpath("/html/body/table/tbody/tr/td[1]/form/button"));

		Assert.assertEquals("Pancakes", tableContentSearchOne.getText());

		driver.findElement(By.xpath("/html/body/form/input")).clear();
		driver.findElement(By.xpath("/html/body/form/button")).click();

		Assert.assertEquals("Cheeseburger",
				driver.findElement(By.xpath("/html/body/table/tbody/tr[3]/td[1]/form/button")).getText());
		Assert.assertEquals("Pancakes",
				driver.findElement(By.xpath("/html/body/table/tbody/tr[1]/td[1]/form/button")).getText());

		driver.findElement(By.xpath("/html/body/form/input")).sendKeys("Cheese");
		driver.findElement(By.xpath("/html/body/form/button")).click();

		WebElement tableContentSearchTwo = driver.findElement(By.xpath("/html/body/table/tbody/tr/td[1]/form/button"));

		Assert.assertEquals("Cheeseburger", tableContentSearchTwo.getText());

		driver.findElement(By.xpath("/html/body/form/input")).clear();
		driver.findElement(By.xpath("/html/body/form/button")).click();

		Assert.assertEquals("Cheeseburger",
				driver.findElement(By.xpath("/html/body/table/tbody/tr[3]/td[1]/form/button")).getText());
		Assert.assertEquals("Pancakes",
				driver.findElement(By.xpath("/html/body/table/tbody/tr[1]/td[1]/form/button")).getText());

	}

	@Test
	public void test1RegisterSuccess() throws Exception {
		driver.get(
				"http://ec2-3-128-33-102.us-east-2.compute.amazonaws.com:8080/csci4830-recipe-book/SearchRecipeList");

		driver.findElement(By.xpath("/html/body/nav/a[1]")).click();

		Assert.assertEquals("Login Here", driver.findElement(By.xpath("/html/body/h1")).getText());

		driver.findElement(By.xpath("/html/body/form/table/tbody/tr[3]/td[2]/p/a")).click();

		Assert.assertEquals("Register Here", driver.findElement(By.xpath("/html/body/h1")).getText());
		removeTestingUser(username, password);
		driver.findElement(By.xpath("/html/body/form/table/tbody/tr[1]/td[2]/input")).sendKeys(username);
		driver.findElement(By.xpath("/html/body/form/table/tbody/tr[2]/td[2]/input")).sendKeys(password);
		driver.findElement(By.xpath("/html/body/form/table/tbody/tr[3]/td[1]/input")).click();

		Assert.assertTrue(getLoginUser(username, password));

		driver.findElement(By.xpath("/html/body/h1")).isDisplayed();
		driver.findElement(By.xpath("/html/body/div/a")).click();
		Assert.assertTrue(driver.findElement(By.xpath("/html/body/table")).isDisplayed());
	}

	@Test
	public void test2LoginSuccess() throws Exception {
		driver.get(
				"http://ec2-3-128-33-102.us-east-2.compute.amazonaws.com:8080/csci4830-recipe-book/SearchRecipeList");

		driver.findElement(By.xpath("/html/body/nav/a[1]")).click();

		Assert.assertEquals("Login Here", driver.findElement(By.xpath("/html/body/h1")).getText());

		driver.findElement(By.xpath("/html/body/form/table/tbody/tr[1]/td[2]/input")).sendKeys(username);
		driver.findElement(By.xpath("/html/body/form/table/tbody/tr[2]/td[2]/input")).sendKeys(password);
		driver.findElement(By.xpath("/html/body/form/table/tbody/tr[3]/td[1]/input")).click();

		Assert.assertTrue(getLoginValid(username, password));

	}

	@Test
	public void test3LoginFail() throws Exception {
		driver.get(
				"http://ec2-3-128-33-102.us-east-2.compute.amazonaws.com:8080/csci4830-recipe-book/SearchRecipeList");

		driver.findElement(By.xpath("/html/body/nav/a[1]")).click();

		Assert.assertEquals("Login Here", driver.findElement(By.xpath("/html/body/h1")).getText());

		String failPass = password + "fail";
		driver.findElement(By.xpath("/html/body/form/table/tbody/tr[1]/td[2]/input")).sendKeys(username);
		driver.findElement(By.xpath("/html/body/form/table/tbody/tr[2]/td[2]/input")).sendKeys(failPass);
		driver.findElement(By.xpath("/html/body/form/table/tbody/tr[3]/td[1]/input")).click();

		Assert.assertFalse(getLoginValid(username, failPass));
	}

	@Test
	public void testCreateRecipeSubmit() throws Exception {
		driver.get(
				"http://ec2-3-128-33-102.us-east-2.compute.amazonaws.com:8080/csci4830-recipe-book/SearchRecipeList");
		driver.findElement(By.xpath("/html/body/nav/a[2]")).click();

		String RName = "Baked Potatoes";
		String RType = "Side Dish";
		String IngList = "Potatoes, Salt, Butter";
		String InstList = "Wrap potatoes in tin foil. Place potatoes on pan. Place in oven. Once cooked add butter.";
		String AddInfo = "Be sure to have tin foil";
		String ImgURL = "http://kellyshealthykitchen.files.wordpress.com/2012/11/photo39.jpg?w=500&h=500";
		
		removeTestingRecipe(RName, RType, IngList, InstList, AddInfo, ImgURL);
		
		driver.findElement(By.xpath("/html/body/form/div[1]/input")).sendKeys(RName);
		driver.findElement(By.xpath("/html/body/form/div[2]/input")).sendKeys(RType);
		driver.findElement(By.xpath("/html/body/form/div[3]/textarea")).sendKeys(IngList);
		driver.findElement(By.xpath("/html/body/form/div[4]/textarea")).sendKeys(InstList);
		driver.findElement(By.xpath("/html/body/form/div[5]/textarea")).sendKeys(AddInfo);
		driver.findElement(By.xpath("/html/body/form/div[6]/input")).sendKeys(ImgURL);
		driver.findElement(By.xpath("/html/body/form/button[1]")).click();

		Assert.assertTrue(getRecipeValid(RName, RType, IngList, InstList, AddInfo, ImgURL));
		Assert.assertTrue(driver.findElement(By.xpath("/html/body/h1")).isDisplayed());
		driver.findElement(By.xpath("/html/body/div/a")).click();
		Assert.assertTrue(driver.findElement(By.xpath("/html/body/table")).isDisplayed());
	}
	
	@Test
	public void testCreateRecipeClear() throws Exception {
		driver.get(
				"http://ec2-3-128-33-102.us-east-2.compute.amazonaws.com:8080/csci4830-recipe-book/SearchRecipeList");
		driver.findElement(By.xpath("/html/body/nav/a[2]")).click();

		String RName = "Baked Potatoes";
		String RType = "Side Dish";
		String IngList = "Potatoes, Salt, Butter";
		String InstList = "Wrap potatoes in tin foil. Place potatoes on pan. Place in oven. Once cooked add butter.";
		String AddInfo = "Be sure to have tin foil";
		String ImgURL = "http://kellyshealthykitchen.files.wordpress.com/2012/11/photo39.jpg?w=500&h=500";
		
		removeTestingRecipe(RName, RType, IngList, InstList, AddInfo, ImgURL);
		
		driver.findElement(By.xpath("/html/body/form/div[1]/input")).sendKeys(RName);
		driver.findElement(By.xpath("/html/body/form/div[2]/input")).sendKeys(RType);
		driver.findElement(By.xpath("/html/body/form/div[3]/textarea")).sendKeys(IngList);
		driver.findElement(By.xpath("/html/body/form/div[4]/textarea")).sendKeys(InstList);
		driver.findElement(By.xpath("/html/body/form/div[5]/textarea")).sendKeys(AddInfo);
		driver.findElement(By.xpath("/html/body/form/div[6]/input")).sendKeys(ImgURL);
		driver.findElement(By.xpath("/html/body/form/button[2]")).click();

		String RNameClear = driver.findElement(By.xpath("/html/body/form/div[1]/input")).getText();
		String RTypeClear = driver.findElement(By.xpath("/html/body/form/div[2]/input")).getText();
		String IngListClear = driver.findElement(By.xpath("/html/body/form/div[3]/textarea")).getText();
		String InstListClear = driver.findElement(By.xpath("/html/body/form/div[4]/textarea")).getText();
		String AddInfoClear = driver.findElement(By.xpath("/html/body/form/div[5]/textarea")).getText();
		String ImgURLClear = driver.findElement(By.xpath("/html/body/form/div[6]/input")).getText();
		
		String nothing = "";
		
		Assert.assertEquals(nothing, RNameClear);
		Assert.assertEquals(nothing, RTypeClear);
		Assert.assertEquals(nothing, IngListClear);
		Assert.assertEquals(nothing, InstListClear);
		Assert.assertEquals(nothing, AddInfoClear);
		Assert.assertEquals(nothing, ImgURLClear);
	
		driver.findElement(By.xpath("/html/body/nav/a")).click();
		Assert.assertTrue(driver.findElement(By.xpath("/html/body/table")).isDisplayed());
	}

	@After
	public void tearDown() throws Exception {
		driver.quit();
		String verificationErrorString = verificationErrors.toString();

		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

	private void checkRecipeList() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;

			String selectSQL = "SELECT * FROM recipes";
			preparedStatement = connection.prepareStatement(selectSQL);
			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next() != false) {
				do {
					System.out.println(rs.getString("recipeName") + " | " + rs.getString("recipeType") + " | "
							+ rs.getString("ingredientList") + " | " + rs.getString("instructions") + " | "
							+ rs.getString("additionalInfo") + " | " + rs.getString("imageURL") + "\n\n");
				} while (rs.next());
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private boolean getRecipeValid(String RName, String RType, String IngList, String InstList, String AddInfo,
			String ImgURL) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		boolean retBool = false;
		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;

			String selectSQL = "SELECT * FROM recipes WHERE recipeName LIKE ? AND recipeType LIKE ? AND ingredientList LIKE ? AND instructions LIKE ? AND additionalInfo LIKE ? AND imageURL LIKE ?";
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1, RName);
			preparedStatement.setString(2, RType);
			preparedStatement.setString(3, IngList);
			preparedStatement.setString(4, InstList);
			preparedStatement.setString(5, AddInfo);
			preparedStatement.setString(6, ImgURL);

			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next() != false) {
				do {
					System.out.println(rs.getString("recipeName") + " | " + rs.getString("recipeType") + " | "
							+ rs.getString("ingredientList") + " | " + rs.getString("instructions") + " | "
							+ rs.getString("additionalInfo") + " | " + rs.getString("imageURL"));
					retBool = true;
				} while (rs.next());
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retBool;
	}

	private void removeTestingRecipe(String RName, String RType, String IngList, String InstList, String AddInfo,
			String ImgURL) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;

			String selectSQL = "DELETE FROM recipes WHERE recipeName LIKE ? AND recipeType LIKE ? AND ingredientList LIKE ? AND instructions LIKE ? AND additionalInfo LIKE ? AND imageURL LIKE ?";
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1, RName);
			preparedStatement.setString(2, RType);
			preparedStatement.setString(3, IngList);
			preparedStatement.setString(4, InstList);
			preparedStatement.setString(5, AddInfo);
			preparedStatement.setString(6, ImgURL);

			preparedStatement.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private void removeTestingUser(String user, String pass) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;

			String selectSQL = "DELETE FROM userLogin WHERE username LIKE ? AND password LIKE ?";
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, pass);

			preparedStatement.execute();

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	private boolean getLoginValid(String user, String pass) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		boolean retBool = false;
		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;

			String selectSQL = "SELECT * FROM userLogin WHERE username LIKE ?";
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1, user);

			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next() != false) {
				do {
					if (pass.equals(rs.getString("password"))) {
						retBool = true;
					}
				} while (rs.next());
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retBool;
	}

	private boolean getLoginUser(String user, String pass) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		boolean retBool = false;
		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;

			String selectSQL = "SELECT * FROM userLogin WHERE username LIKE ? AND password LIKE ?";
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1, user);
			preparedStatement.setString(2, pass);

			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next() != false) {
				do {
					System.out.println(rs.getString("username") + " | " + rs.getString("password"));
					retBool = true;
				} while (rs.next());
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retBool;

	}

	private boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	private boolean isAlertPresent() {
		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException e) {
			return false;
		}
	}
}