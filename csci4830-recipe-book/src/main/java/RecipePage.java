
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.DBConnection;

/**
 * Servlet implementation class RecipePage
 */
@WebServlet("/RecipePage")
public class RecipePage extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RecipePage() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String recipeIdParam = request.getParameter("recipeSelection");
		int recipeIdSearch;
		if (recipeIdParam == null) {
			recipeIdSearch = util.UtilityData.getRecipeID();
		} else {
			recipeIdSearch = Integer.parseInt(recipeIdParam);
		}

		boolean newRating = util.UtilityData.checkValidNewRating(recipeIdSearch, util.UtilityData.getUserId());
		String recipeData = getRecipeInformation(recipeIdSearch, newRating);

		if (newRating) {
			displayHTMLNoRating(request, response, recipeData);

		} else {
			displayHTMLHasRating(request, response, recipeData);
		}

	}

	private String getRecipeInformation(int recipeIdSearch, boolean newRating) {
		String returnHTML = "";

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;

			String selectSQL = "SELECT * FROM recipes WHERE recipeid = ?";
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, recipeIdSearch);

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				int recipeId = rs.getInt("recipeId");

				util.UtilityData.setRecipeID(recipeId);

				String recipeName = rs.getString("recipeName").trim();
				String recipeType = rs.getString("recipeType").trim();
				Date createdDate = rs.getDate("createdDate");
				String ingredientList = rs.getString("ingredientList").trim();
				String instructions = rs.getString("instructions").trim();

				String additionalInfo = "";
				if (rs.getString("additionalInfo") != null) {
					additionalInfo = rs.getString("additionalInfo").trim();
				}
				String imageURL = "";
				if (rs.getString("imageURL") != null) {
					imageURL = rs.getString("imageURL").trim();
					System.out.println(imageURL);
				}

				int userId = rs.getInt("userid");

				System.out.println(recipeId + " | " + recipeName + " | " + recipeType + " | " + createdDate + " | "
						+ ingredientList + " | " + instructions + " | " + additionalInfo + " | " + imageURL + " | "
						+ userId);

				String username = getUser(userId);

				returnHTML += "<div style=\"background: rgb(0 0 0 / 15%);width: fit-content;text-align:left;\" class=\"recipe-info\">\r\n"
						+ "		<div class=\"header\">\r\n" + "			<div class=\"name-cat\">";
				returnHTML += "<h1 class=\"name-cat-0\">" + recipeName + "</h1>";
				returnHTML += "<span class=\"name-cat-0\">(" + recipeType + ")</span>";
				returnHTML += "</div>";
				returnHTML += "<p>By: " + username + "</p>";
				returnHTML += "<p>Created on: " + createdDate + "</p>";
				returnHTML += "</div>";

				if (imageURL != null && !(imageURL.equals(""))) {
					returnHTML += "<div class=\"image-container\">";
					returnHTML += "<img align=\"right\" width=\"200px\" height=\"200px\"\r\n src=\"" + imageURL
							+ "\"></div>";
				}

				returnHTML += "<div class=\"ingredients\">\r\n" + "			<h3>Ingredient List</h3>\r\n"
						+ "			<ul>";
				// loop
				String[] ingredient = ingredientList.split(",");
				for (int i = 0; i < ingredient.length; i++) {
					returnHTML += "<li>" + ingredient[i] + "</li>";
				}

				returnHTML += "</ul>\r\n" + "\r\n" + "		</div>\r\n" + "		<div class=\"instructions\">\r\n"
						+ "			<h3>Instructions</h3>";
				returnHTML += "<p>" + instructions + "</p>";
				returnHTML += "</div>";

				if (additionalInfo != null && !(additionalInfo.equals(""))) {
					returnHTML += "<div class=\"a-info\">\r\n" + "			<h3>Additional Material</h3>";
					returnHTML += "<p>" + additionalInfo + "</p></div>";
				}

				if (util.UtilityData.validUser()) {
					String userRating = String.valueOf(util.UtilityData.getUserRatingPerRecipe(recipeId));
					if (newRating) {
						returnHTML += "<div class=\"rating\">\r\n" + "		<div>\r\n"
								+ "			<form class=\"params\" action=\"/csci4830-recipe-book/RateRecipe\"\r\n"
								+ "		method=\"POST\">\r\n" + "				<h3>Give This Recipe a Rating!</h3>\r\n"
								+ "				<label for=\"rating\">Rating:</label> \r\n"
								+ "				<select id=\"rating\" name=\"rating\">\r\n"
								+ "					<option value=\"nothing\">..</option>\r\n"
								+ "					<option value=\"1\">One</option>\r\n"
								+ "					<option value=\"2\">Two</option>\r\n"
								+ "					<option value=\"3\">Three</option>\r\n"
								+ "					<option value=\"4\">Four</option>\r\n"
								+ "					<option value=\"5\">Five</option>\r\n"
								+ "				</select>\r\n"
								+ "				<button type=\"submit\">Submit</button>\r\n" + "			</form>\r\n"
								+ "		</div>";
					} else {
						returnHTML += "<div class=\"rating\">\r\n" + "		<div>\r\n"
								+ "			<form class=\"params\" action=\"/csci4830-recipe-book/RateRecipe\"\r\n"
								+ "		method=\"POST\">\r\n" + "				<h3>Update your rating!</h3>\r\n"
								+ "				<label for=\"rating\">Rating:</label> \r\n"
								+ "				<select id=\"rating\" name=\"rating\">\r\n"
								+ util.UtilityData.getRatingOptions(userRating) + "				</select>\r\n"
								+ "				<button type=\"submit\">Submit</button>\r\n" + "			</form>\r\n"
								+ "		</div>";
					}

				}

			}
			rs.close();
			preparedStatement.close();
			connection.close();

		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}

		}
		return returnHTML;
	}

	private void displayHTMLNoRating(HttpServletRequest request, HttpServletResponse response, String recipeData)
			throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		String loginVis = getRecipePageVis();
		out.println("<!DOCTYPE html>\r\n" + "<html>\r\n" + "<head>\r\n" + "<style>\r\n" + "\r\n" + "body {\r\n"
				+ "      background-image: url('pic.png');\r\n" + "      background-size:cover;\r\n" + "    }\r\n"
				+ "\r\n" + "\r\n" + "    a:link{\r\n" + "\r\n" + "color: white;\r\n" + "\r\n" + "\r\n" + "\r\n"
				+ "}\r\n" + "\r\n" + "div{\r\n" + "\r\n" + "    color: white;\r\n" + "    padding-top: 10px;\r\n"
				+ "}\r\n" + "\r\n" + "h2{\r\n" + "    color: white;\r\n" + "    position: center;\r\n" + "}\r\n"
				+ "\r\n" + "#loginPosition {\r\n" + "	float: right;\r\n" + "	display: block;\r\n"
				+ "	color: #f2f2f2;\r\n" + "	text-align: center;\r\n" + "	padding: 12px;\r\n"
				+ "	text-decoration: none;\r\n" + "	font-size: 15px;\r\n" + "}\r\n" + "\r\n"
				+ "table.GeneratedTable {\r\n" + "	width: 75%;\r\n" + "	background-color: #ffffff;\r\n"
				+ "	border-collapse: collapse;\r\n" + "	border-width: 1px;\r\n" + "	border-color: #0d0d0d;\r\n"
				+ "	border-style: solid;\r\n" + "	color: #000000;\r\n" + "}\r\n" + "\r\n"
				+ "table.GeneratedTable td, table.GeneratedTable th {\r\n" + "	border-width: 2px;\r\n"
				+ "	border-color: #0d0d0d;\r\n" + "	border-style: solid;\r\n" + "	padding: 3px;\r\n" + "}\r\n"
				+ "\r\n" + "table.GeneratedTable thead {\r\n" + "	background-color: #ffffff;\r\n" + "}\r\n" + "\r\n"
				+ "button.link {\r\n" + "	background: none !important;\r\n" + "	border: none;\r\n"
				+ "	padding: 0 !important;\r\n" + "	/*optional*/\r\n" + "	font-family: arial, sans-serif;\r\n"
				+ "	/*input has OS specific font-family*/\r\n" + "	color: #069;\r\n"
				+ "	text-decoration: underline;\r\n" + "	cursor: pointer;\r\n" + "}\r\n" + "</style>"
				+ "<meta charset=\"UTF-8\">\r\n" + "<title>Recipe Name</title>\r\n" + "</head>\r\n" + "\r\n" + "\r\n"
				+ "<body style=\"text-align: -webkit-center;\"> <nav id=\"loginPosition\">\r\n" + loginVis
				+ "&emsp;<a style=\"text-decoration:none;color:white;\" href=\"/csci4830-recipe-book/SearchRecipeList\">Home Page</a>"
				+ "	</nav>");

		out.println(recipeData);

		out.println("</div>\r\n" + "\r\n" + "\r\n" + "\r\n" + "\r\n" + "</body>\r\n" + "</html>");

	}

	private void displayHTMLHasRating(HttpServletRequest request, HttpServletResponse response, String recipeData)
			throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String loginVis = getRecipePageVis();
		out.println("<!DOCTYPE html>\r\n" + "<html>\r\n" + "<head>\r\n" + "<style>\r\n" + "\r\n" + "body {\r\n"
				+ "      background-image: url('pic.png');\r\n" + "      background-size:cover;\r\n" + "    }\r\n"
				+ "\r\n" + "\r\n" + "    a:link{\r\n" + "\r\n" + "color: white;\r\n" + "\r\n" + "\r\n" + "\r\n"
				+ "}\r\n" + "\r\n" + "div{\r\n" + "\r\n" + "    color: white;\r\n" + "    padding-top: 10px;\r\n"
				+ "}\r\n" + "\r\n" + "h2{\r\n" + "    color: white;\r\n" + "    position: center;\r\n" + "}\r\n"
				+ "\r\n" + "#loginPosition {\r\n" + "	float: right;\r\n" + "	display: block;\r\n"
				+ "	color: #f2f2f2;\r\n" + "	text-align: center;\r\n" + "	padding: 12px;\r\n"
				+ "	text-decoration: none;\r\n" + "	font-size: 15px;\r\n" + "}\r\n" + "\r\n"
				+ "table.GeneratedTable {\r\n" + "	width: 75%;\r\n" + "	background-color: #ffffff;\r\n"
				+ "	border-collapse: collapse;\r\n" + "	border-width: 1px;\r\n" + "	border-color: #0d0d0d;\r\n"
				+ "	border-style: solid;\r\n" + "	color: #000000;\r\n" + "}\r\n" + "\r\n"
				+ "table.GeneratedTable td, table.GeneratedTable th {\r\n" + "	border-width: 2px;\r\n"
				+ "	border-color: #0d0d0d;\r\n" + "	border-style: solid;\r\n" + "	padding: 3px;\r\n" + "}\r\n"
				+ "\r\n" + "table.GeneratedTable thead {\r\n" + "	background-color: #ffffff;\r\n" + "}\r\n" + "\r\n"
				+ "button.link {\r\n" + "	background: none !important;\r\n" + "	border: none;\r\n"
				+ "	padding: 0 !important;\r\n" + "	/*optional*/\r\n" + "	font-family: arial, sans-serif;\r\n"
				+ "	/*input has OS specific font-family*/\r\n" + "	color: #069;\r\n"
				+ "	text-decoration: underline;\r\n" + "	cursor: pointer;\r\n" + "}\r\n" + "</style>"
				+ "<meta charset=\"UTF-8\">\r\n" + "<title>Recipe Name</title>\r\n" + "</head>\r\n" + "\r\n" + "\r\n"
				+ "<body style=\"text-align: -webkit-center;\">\r\n" + "	<nav id=\"loginPosition\">\r\n" + loginVis
				+ "&emsp;<a style=\"text-decoration:none;color:white;\" href=\"/csci4830-recipe-book/SearchRecipeList\">Home Page</a>"
				+ "	</nav>");

		out.println(recipeData);

		out.println("</div>\r\n" + "\r\n" + "\r\n" + "\r\n" + "\r\n" + "</body>\r\n" + "</html>");

	}

	private String getRecipePageVis() {

		String returnHTML = "";
		if (util.UtilityData.validUser()) {
			returnHTML = "<a style=\"text-decoration:none;color:white;display:none\" href=\"/csci4830-recipe-book/login.html\">Login/Register</a>";
		} else {

			returnHTML = "<a style=\"text-decoration:none;color:white;\" href=\"/csci4830-recipe-book/login.html\">Login/Register</a>";
		}
		return returnHTML;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	private String getUser(int userId) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;

			String selectSQL = "SELECT * FROM userLogin WHERE userid = ?";
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, userId);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				String username = rs.getString("username").trim();
				System.out.println(username);
				return username;
			}

		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (preparedStatement != null)
					preparedStatement.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
		return "Bob(Default)";
	}

}
