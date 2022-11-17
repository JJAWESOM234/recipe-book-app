
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
		int recipeIdSearch = Integer.parseInt(request.getParameter("recipeSelection"));
		System.out.println(recipeIdSearch);

		String recipeData = getRecipeInformation(recipeIdSearch);
		displayHTML(request, response, recipeData);

	}

	private String getRecipeInformation(int recipeIdSearch) {
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

				returnHTML += "<div class=\"recipe-info\">\r\n" + "		<div class=\"header\">\r\n"
						+ "			<div class=\"name-cat\">";
				returnHTML += "<h1 class=\"name-cat-0\">" + recipeName + "</h1>";
				returnHTML += "<span class=\"name-cat-0\">(" + recipeType + ")</span>";
				returnHTML += "</div>";
				returnHTML += "<p>By: " + username + "</p>";
				returnHTML += "<p>Created on: " + createdDate + "</p>";
				returnHTML += "</div>";

				if (imageURL != "") {
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

				if (additionalInfo != "") {
					returnHTML += "<div class=\"a-info\">\r\n" + "			<h3>Additional Material</h3>";
					returnHTML += "<p>" + additionalInfo + "</p></div>";
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

	private void displayHTML(HttpServletRequest request, HttpServletResponse response, String recipeData)
			throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		out.println("<!DOCTYPE html>\r\n" + "<html>\r\n" + "<head>\r\n" + "<style>\r\n" + "#loginPosition {\r\n"
				+ "	float: right;\r\n" + "	display: block;\r\n" + "	color: #f2f2f2;\r\n"
				+ "	text-align: center;\r\n" + "	padding: 12px;\r\n" + "	text-decoration: none;\r\n"
				+ "	font-size: 15px;\r\n" + "}\r\n" + "</style>\r\n" + "<meta charset=\"UTF-8\">\r\n"
				+ "<title>Recipe Name</title>\r\n" + "</head>\r\n" + "\r\n" + "\r\n" + "<body>\r\n"
				+ "	<nav id=\"loginPosition\">\r\n"
				+ "		<a href=\"/csci4830-recipe-book/SearchRecipeList\">Home Page</a> <a\r\n"
				+ "			href=\"/csci4830-recipe-book/login.html\">Login</a>\r\n" + "	</nav>");

		out.println(recipeData);

		out.println("</div>\r\n" + "\r\n" + "\r\n" + "\r\n" + "\r\n" + "</body>\r\n" + "</html>");

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
