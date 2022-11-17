import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
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
 * Servlet implementation class SearchRecipeList
 */
@WebServlet("/SearchRecipeList")
public class SearchRecipeList extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SearchRecipeList() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String recipeName = request.getParameter("search");
		System.out.println(recipeName);
		String tableData = getTableData(request, response, recipeName);
		displayHTML(request, response, recipeName, tableData);

	}

	protected String getTableData(HttpServletRequest request, HttpServletResponse response, String searchRecipeName)
			throws IOException {

		String returnTable = "";

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;

			if (searchRecipeName == null || searchRecipeName.isEmpty()) {
				String selectSQL = "SELECT * FROM recipes";
				preparedStatement = connection.prepareStatement(selectSQL);
			} else {
				String selectSQL = "SELECT * FROM recipes WHERE recipeName LIKE ?";
				String recipeNameLike = searchRecipeName + "%";
				preparedStatement = connection.prepareStatement(selectSQL);
				preparedStatement.setString(1, recipeNameLike);
			}

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				System.out.println("herrwew");
				int recipeId = rs.getInt("recipeId");
				String recipeName = rs.getString("recipeName").trim();
				String recipeType = rs.getString("recipeType").trim();
				int userId = rs.getInt("userid");

				System.out.println(recipeId + " | " + recipeName + " | " + recipeType + " | " + userId);

				String username = getUser(userId);

				returnTable += "<tr>";
				returnTable += "<td>"
						+ "					<form action=\"/csci4830-recipe-book/RecipePage\" method=\"POST\">"
						+ "						<button class=\"link\" type=\"submit\" value=" + recipeId
						+ " name=\"recipeSelection\">" + recipeName + "</button><br>" + "					</form>"
						+ "				</td>";
				returnTable += "<td>" + recipeType + "<br></td>";
				returnTable += "<td>" + username + "<br></td>";
				returnTable += "<td>NYI<br></td>";
				returnTable += "</tr>";

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
		return returnTable;
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	private void displayHTML(HttpServletRequest request, HttpServletResponse response, String recipeName,
			String tableData) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		if (recipeName == null) {
			recipeName = "";
		}

		out.println("<!DOCTYPE html>\r\n" + "<html>\r\n" + "<head>\r\n" + "<style>\r\n" + "#loginPosition {\r\n"
				+ "	float: right;\r\n" + "	display: block;\r\n" + "	color: #f2f2f2;\r\n"
				+ "	text-align: center;\r\n" + "	padding: 12px;\r\n" + "	text-decoration: none;\r\n"
				+ "	font-size: 15px;\r\n" + "}\r\n" + "\r\n" + "table.GeneratedTable {\r\n" + "	width: 75%;\r\n"
				+ "	background-color: #ffffff;\r\n" + "	border-collapse: collapse;\r\n" + "	border-width: 1px;\r\n"
				+ "	border-color: #0d0d0d;\r\n" + "	border-style: solid;\r\n" + "	color: #000000;\r\n" + "}\r\n"
				+ "\r\n" + "table.GeneratedTable td, table.GeneratedTable th {\r\n" + "	border-width: 2px;\r\n"
				+ "	border-color: #0d0d0d;\r\n" + "	border-style: solid;\r\n" + "	padding: 3px;\r\n" + "}\r\n"
				+ "\r\n" + "table.GeneratedTable thead {\r\n" + "	background-color: #ffffff;\r\n" + "}\r\n" + "\r\n"
				+ "button.link {\r\n" + "	background: none !important;\r\n" + "	border: none;\r\n"
				+ "	padding: 0 !important;\r\n" + "	/*optional*/\r\n" + "	font-family: arial, sans-serif;\r\n"
				+ "	/*input has OS specific font-family*/\r\n" + "	color: #069;\r\n"
				+ "	text-decoration: underline;\r\n" + "	cursor: pointer;\r\n" + "}\r\n" + "</style>\r\n"
				+ "<link rel=\"stylesheet\"\r\n"
				+ "	href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css%22%3E\" />\r\n"
				+ "<title>Recipe Homepage</title></head>\r\n" + "<body>\r\n" + "	<nav id=\"loginPosition\">\r\n"
				+ "		<a href=\"/csci4830-recipe-book/login.html\">Login/Register</a> \r\n"
				+ "		<a href=\"/csci4830-recipe-book/CreateRecipe.html\">Create Recipe</a> \r\n" + "	</nav>\r\n"
				+ "	<form class=\"params\" action=\"/csci4830-recipe-book/SearchRecipeList\"\r\n"
				+ "		method=\"POST\">\r\n"
				+ "		<input value=\"" + recipeName + "\" type=\"text\" placeholder=\"Search Your Recipe\" name=\"search\">\r\n"
				+ "		<button type=\"submit\">Search</button>\r\n" + "\r\n"
				+ "		<div class=\"select-wrapper\">\r\n" + "			<label for=\"rating\">Rating:</label>\r\n"
				+ "			<select name=\"rating\">\r\n" + "				<option value=\"nothing\">..</option>\r\n"
				+ "				<option value=\"one\">One</option>\r\n"
				+ "				<option value=\"two\">Two</option>\r\n"
				+ "				<option value=\"three\">Three</option>\r\n"
				+ "				<option value=\"four\">Four</option>\r\n"
				+ "				<option value=\"five\">Five</option>\r\n" + "			</select>\r\n"
				+ "		</div>\r\n" + "		<div class=\"select-wrapper\">\r\n"
				+ "			<label for=\"cat\">Category:</label>\r\n" + "			<select name=\"cat\">\r\n"
				+ "			<option value=\"nothing\">..</option>\r\n"
				+ "				<option value=\"apps\">Appetizer</option>\r\n"
				+ "				<option value=\"bread\">Bread</option>\r\n"
				+ "				<option value=\"bfast\">Breakfast</option>\r\n"
				+ "				<option value=\"dess\">Dessert</option>\r\n"
				+ "				<option value=\"drink\">Drink</option>\r\n"
				+ "				<option value=\"main\">Main Course</option>\r\n"
				+ "				<option value=\"salad\">Salad</option>\r\n"
				+ "				<option value=\"condsauce\">Condiment/Sauce</option>\r\n"
				+ "				<option value=\"side\">Side Dish</option>\r\n"
				+ "				<option value=\"snack\">Snack</option>\r\n"
				+ "				<option value=\"soup\">Soup</option>\r\n"
				+ "				<option value=\"wrapsand\">Wraps/Sandwhich</option>\r\n" + "				\r\n"
				+ "			</select>\r\n" + "		</div>\r\n" + "	</form>\r\n"
				+ "	<h2 class=\"sep\">Browse Recipes:</h2>");
		// Define header and table elements
		out.println("<table class=\"GeneratedTable\">\r\n" + "		<thead>\r\n" + "			<tr>\r\n"
				+ "				<th>Recipe Name</th>\r\n" + "				<th>Category</th>\r\n"
				+ "				<th>Author</th>\r\n" + "				<th>Rating</th>\r\n" + "				\r\n"
				+ "			</tr>\r\n" + "		</thead>\r\n" + "		<tbody>");
		out.println(tableData);

		out.println("</tbody>\r\n" + "	</table>\r\n" + "</body>\r\n" + "</html>");

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
