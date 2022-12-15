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
		String recipeRating = request.getParameter("rating");
		String recipeCategory = request.getParameter("cat");
		System.out.println(recipeName + " | " + recipeRating + " | " + recipeCategory);
		String tableData = getTableData(request, response, recipeName, recipeRating, recipeCategory);
		displayHTML(request, response, recipeName, tableData, recipeRating, recipeCategory);

	}

	protected String getTableData(HttpServletRequest request, HttpServletResponse response, String searchRecipeName,
			String recipeRating, String recipeCategory) throws IOException {

		String returnTable = "";

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		
		
		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;

			if (searchRecipeName == null || searchRecipeName.isEmpty() && recipeRating.equals("nothing")
					&& recipeCategory.equals("nothing")) {
				String selectSQL = "SELECT * FROM recipes";
				preparedStatement = connection.prepareStatement(selectSQL);

			} else {
				if (recipeCategory.equals("nothing")) {
					recipeCategory = "";
				}
				if (recipeRating.equals("nothing")) {
					recipeRating = "";
				}
				String selectSQL = "SELECT * FROM recipes WHERE ";
				int numberParam = 0;
				boolean RName = false;
				boolean RType = false;
				boolean RRating = false;
				if (searchRecipeName != null && !(searchRecipeName.isEmpty())) {
					selectSQL += "recipeName LIKE ?";
					numberParam++;
					RName = true;
				}

				if (recipeCategory != "") {
					if (numberParam > 0) {
						selectSQL += " AND ";
					}
					System.out.println(recipeCategory);
					selectSQL += "recipeType LIKE ?";
					numberParam++;
					RType = true;
				}

				if (recipeRating != "") {
					if (numberParam > 0) {
						selectSQL += " AND ";
					}
					selectSQL += "rating >= ?";
					numberParam++;
					RRating = true;
				}
				System.out.println(selectSQL);
				String recipeNameLike = searchRecipeName + "%";
				preparedStatement = connection.prepareStatement(selectSQL);

				for (int i = 1; i <= numberParam; i++) {
					if (RName) {
						System.out.println("RName");
						preparedStatement.setString(i, recipeNameLike);
						RName = false;
					} else if (RType) {
						System.out.println("RType");
						preparedStatement.setString(i, recipeCategory);
						RType = false;
					} else if (RRating) {
						System.out.println("RRating");
						// This will have to be a double 
						preparedStatement.setInt(i, Integer.parseInt(recipeRating));
						RRating = false;
					}
				}

			}

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				int recipeId = rs.getInt("recipeId");
				String recipeName = rs.getString("recipeName").trim();
				String recipeType = rs.getString("recipeType").trim();
				int userId = rs.getInt("userid");
				double rating = rs.getDouble("rating");
				System.out.println("**********" + rating  + "***********");
				//double rating = util.UtilityData.getRatingAvg(recipeId);

				System.out
						.println(recipeId + " | " + recipeName + " | " + recipeType + " | " + userId + " | " + rating + " | " + rs.getString("additionalInfo"));

				String username = getUser(userId);

				returnTable += "<tr>";
				returnTable += "<td>"
						+ "					<form action=\"/csci4830-recipe-book/RecipePage\" method=\"POST\">"
						+ "						<button class=\"link\" type=\"submit\" value=" + recipeId
						+ " name=\"recipeSelection\">" + recipeName + "</button><br>" + "					</form>"
						+ "				</td>";
				returnTable += "<td>" + recipeType + "<br></td>";
				returnTable += "<td>" + username + "<br></td>";
				if (rating > 0)
				{
					returnTable += "<td>" + rating + "<br></td>";	
				}
				else
				{
					returnTable += "<td>No Ratings<br></td>";
				}
				
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
		return "";
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	private void displayHTML(HttpServletRequest request, HttpServletResponse response, String recipeName,
			String tableData, String rating, String category) throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		if (recipeName == null) {
			recipeName = "";
		}
		if (rating == null) {
			rating = "nothing";
		}
		if (category == null) {
			category = "nothing";
		}

		String selectOptionsRating = util.UtilityData.getRatingOptions(rating);
		String selectCategoryRating = getCategoryOptions(category);
		String[] loginVisibility = getCreateRecipeVis();

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
				+ "<link rel=\"stylesheet\"\r\n"
				+ "	href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css%22%3E\" />\r\n"
				+ "<title>Recipe Homepage</title></head>\r\n" + "<body>\r\n" + "	<nav id=\"loginPosition\">\r\n"
				+ loginVisibility[0] + "&emsp;" + loginVisibility[1]
				+ "	</nav>\r\n" + "	<form class=\"params\" action=\"/csci4830-recipe-book/SearchRecipeList\"\r\n"
				+ "		method=\"POST\">\r\n" + "		<input value=\"" + recipeName
				+ "\" type=\"text\" placeholder=\"Search Your Recipe\" name=\"search\">\r\n"
				+ "		<button type=\"submit\">Search/Filter</button>\r\n" + "\r\n"
				+ "		<div class=\"select-wrapper\">\r\n" + "			<label for=\"rating\">Rating:</label>\r\n"
				+ "			<select name=\"rating\">\r\n" + "				" + selectOptionsRating
				+ "			</select>\r\n" + "		</div>\r\n" + "		<div class=\"select-wrapper\">\r\n"
				+ "			<label for=\"cat\">Category:</label>\r\n" + "			<select name=\"cat\">"
				+ selectCategoryRating + "</select>\r\n" + "		</div>\r\n" + "	</form>\r\n"
				+ "	<h2 class=\"sep\">Browse Recipes:</h2>");
		// Define header and table elements
		out.println("<table class=\"GeneratedTable\">\r\n" + "		<thead>\r\n" + "			<tr>\r\n"
				+ "				<th>Recipe Name</th>\r\n" + "				<th>Category</th>\r\n"
				+ "				<th>Author</th>\r\n" + "				<th>Rating</th>\r\n" + "				\r\n"
				+ "			</tr>\r\n" + "		</thead>\r\n" + "		<tbody>");
		out.println(tableData);

		out.println("</tbody>\r\n" + "	</table>\r\n" + "</body>\r\n" + "</html>");

	}

	

	private String getCategoryOptions(String category) {
		String selectOptionsCategory = "";
		switch (category) {
		case "nothing":
			selectOptionsCategory += "<option value=\"nothing\" selected>..</option>\r\n"
					+ "				<option value=\"Appetizer\">Appetizer</option>\r\n"
					+ "				<option value=\"Bread\">Bread</option>\r\n"
					+ "				<option value=\"Breakfast\">Breakfast</option>\r\n"
					+ "				<option value=\"Dessert\">Dessert</option>\r\n"
					+ "				<option value=\"Drink\">Drink</option>\r\n"
					+ "				<option value=\"Main Course\">Main Course</option>\r\n"
					+ "				<option value=\"Salad\">Salad</option>\r\n"
					+ "				<option value=\"Condiment/Sauce\">Condiment/Sauce</option>\r\n"
					+ "				<option value=\"Side Dish\">Side Dish</option>\r\n"
					+ "				<option value=\"Snack\">Snack</option>\r\n"
					+ "				<option value=\"Soup\">Soup</option>\r\n"
					+ "				<option value=\"Wraps/Sandwhich\">Wraps/Sandwhich</option>\r\n";
			break;
		case "Appetizer":
			selectOptionsCategory += "<option value=\"nothing\">..</option>\r\n"
					+ "				<option value=\"Appetizer\" selected>Appetizer</option>\r\n"
					+ "				<option value=\"Bread\">Bread</option>\r\n"
					+ "				<option value=\"Breakfast\">Breakfast</option>\r\n"
					+ "				<option value=\"Dessert\">Dessert</option>\r\n"
					+ "				<option value=\"Drink\">Drink</option>\r\n"
					+ "				<option value=\"Main Course\">Main Course</option>\r\n"
					+ "				<option value=\"Salad\">Salad</option>\r\n"
					+ "				<option value=\"Condiment/Sauce\">Condiment/Sauce</option>\r\n"
					+ "				<option value=\"Side Dish\">Side Dish</option>\r\n"
					+ "				<option value=\"Snack\">Snack</option>\r\n"
					+ "				<option value=\"Soup\">Soup</option>\r\n"
					+ "				<option value=\"Wraps/Sandwhich\">Wraps/Sandwhich</option>\r\n";
			break;
		case "Bread":
			selectOptionsCategory += "<option value=\"nothing\">..</option>\r\n"
					+ "				<option value=\"Appetizer\">Appetizer</option>\r\n"
					+ "				<option value=\"Bread\" selected>Bread</option>\r\n"
					+ "				<option value=\"Breakfast\">Breakfast</option>\r\n"
					+ "				<option value=\"Dessert\">Dessert</option>\r\n"
					+ "				<option value=\"Drink\">Drink</option>\r\n"
					+ "				<option value=\"Main Course\">Main Course</option>\r\n"
					+ "				<option value=\"Salad\">Salad</option>\r\n"
					+ "				<option value=\"Condiment/Sauce\">Condiment/Sauce</option>\r\n"
					+ "				<option value=\"Side Dish\">Side Dish</option>\r\n"
					+ "				<option value=\"Snack\">Snack</option>\r\n"
					+ "				<option value=\"Soup\">Soup</option>\r\n"
					+ "				<option value=\"Wraps/Sandwhich\">Wraps/Sandwhich</option>\r\n";
			break;
		case "Breakfast":
			selectOptionsCategory += "<option value=\"nothing\">..</option>\r\n"
					+ "				<option value=\"Appetizer\">Appetizer</option>\r\n"
					+ "				<option value=\"Bread\">Bread</option>\r\n"
					+ "				<option value=\"Breakfast\" selected>Breakfast</option>\r\n"
					+ "				<option value=\"Dessert\">Dessert</option>\r\n"
					+ "				<option value=\"Drink\">Drink</option>\r\n"
					+ "				<option value=\"Main Course\">Main Course</option>\r\n"
					+ "				<option value=\"Salad\">Salad</option>\r\n"
					+ "				<option value=\"Condiment/Sauce\">Condiment/Sauce</option>\r\n"
					+ "				<option value=\"Side Dish\">Side Dish</option>\r\n"
					+ "				<option value=\"Snack\">Snack</option>\r\n"
					+ "				<option value=\"Soup\">Soup</option>\r\n"
					+ "				<option value=\"Wraps/Sandwhich\">Wraps/Sandwhich</option>\r\n";
			break;
		case "Dessert":
			selectOptionsCategory += "<option value=\"nothing\">..</option>\r\n"
					+ "				<option value=\"Appetizer\">Appetizer</option>\r\n"
					+ "				<option value=\"Bread\">Bread</option>\r\n"
					+ "				<option value=\"Breakfast\">Breakfast</option>\r\n"
					+ "				<option value=\"Dessert\" selected>Dessert</option>\r\n"
					+ "				<option value=\"Drink\">Drink</option>\r\n"
					+ "				<option value=\"Main Course\">Main Course</option>\r\n"
					+ "				<option value=\"Salad\">Salad</option>\r\n"
					+ "				<option value=\"Condiment/Sauce\">Condiment/Sauce</option>\r\n"
					+ "				<option value=\"Side Dish\">Side Dish</option>\r\n"
					+ "				<option value=\"Snack\">Snack</option>\r\n"
					+ "				<option value=\"Soup\">Soup</option>\r\n"
					+ "				<option value=\"Wraps/Sandwhich\">Wraps/Sandwhich</option>\r\n";
			break;
		case "Drink":
			selectOptionsCategory += "<option value=\"nothing\">..</option>\r\n"
					+ "				<option value=\"Appetizer\">Appetizer</option>\r\n"
					+ "				<option value=\"Bread\">Bread</option>\r\n"
					+ "				<option value=\"Breakfast\">Breakfast</option>\r\n"
					+ "				<option value=\"Dessert\">Dessert</option>\r\n"
					+ "				<option value=\"Drink\" selected>Drink</option>\r\n"
					+ "				<option value=\"Main Course\">Main Course</option>\r\n"
					+ "				<option value=\"Salad\">Salad</option>\r\n"
					+ "				<option value=\"Condiment/Sauce\">Condiment/Sauce</option>\r\n"
					+ "				<option value=\"Side Dish\">Side Dish</option>\r\n"
					+ "				<option value=\"Snack\">Snack</option>\r\n"
					+ "				<option value=\"Soup\">Soup</option>\r\n"
					+ "				<option value=\"Wraps/Sandwhich\">Wraps/Sandwhich</option>\r\n";
			break;
		case "Main Course":
			selectOptionsCategory += "<option value=\"nothing\">..</option>\r\n"
					+ "				<option value=\"Appetizer\">Appetizer</option>\r\n"
					+ "				<option value=\"Bread\">Bread</option>\r\n"
					+ "				<option value=\"Breakfast\">Breakfast</option>\r\n"
					+ "				<option value=\"Dessert\">Dessert</option>\r\n"
					+ "				<option value=\"Drink\">Drink</option>\r\n"
					+ "				<option value=\"Main Course\" selected>Main Course</option>\r\n"
					+ "				<option value=\"Salad\">Salad</option>\r\n"
					+ "				<option value=\"Condiment/Sauce\">Condiment/Sauce</option>\r\n"
					+ "				<option value=\"Side Dish\">Side Dish</option>\r\n"
					+ "				<option value=\"Snack\">Snack</option>\r\n"
					+ "				<option value=\"Soup\">Soup</option>\r\n"
					+ "				<option value=\"Wraps/Sandwhich\">Wraps/Sandwhich</option>\r\n";
			break;
		case "Salad":
			selectOptionsCategory += "<option value=\"nothing\">..</option>\r\n"
					+ "				<option value=\"Appetizer\">Appetizer</option>\r\n"
					+ "				<option value=\"Bread\">Bread</option>\r\n"
					+ "				<option value=\"Breakfast\">Breakfast</option>\r\n"
					+ "				<option value=\"Dessert\">Dessert</option>\r\n"
					+ "				<option value=\"Drink\">Drink</option>\r\n"
					+ "				<option value=\"Main Course\">Main Course</option>\r\n"
					+ "				<option value=\"Salad\" selected>Salad</option>\r\n"
					+ "				<option value=\"Condiment/Sauce\">Condiment/Sauce</option>\r\n"
					+ "				<option value=\"Side Dish\">Side Dish</option>\r\n"
					+ "				<option value=\"Snack\">Snack</option>\r\n"
					+ "				<option value=\"Soup\">Soup</option>\r\n"
					+ "				<option value=\"Wraps/Sandwhich\">Wraps/Sandwhich</option>\r\n";
			break;
		case "Condiment/Sauce":
			selectOptionsCategory += "<option value=\"nothing\">..</option>\r\n"
					+ "				<option value=\"Appetizer\">Appetizer</option>\r\n"
					+ "				<option value=\"Bread\">Bread</option>\r\n"
					+ "				<option value=\"Breakfast\">Breakfast</option>\r\n"
					+ "				<option value=\"Dessert\">Dessert</option>\r\n"
					+ "				<option value=\"Drink\">Drink</option>\r\n"
					+ "				<option value=\"Main Course\">Main Course</option>\r\n"
					+ "				<option value=\"Salad\">Salad</option>\r\n"
					+ "				<option value=\"Condiment/Sauce\" selected>Condiment/Sauce</option>\r\n"
					+ "				<option value=\"Side Dish\">Side Dish</option>\r\n"
					+ "				<option value=\"Snack\">Snack</option>\r\n"
					+ "				<option value=\"Soup\">Soup</option>\r\n"
					+ "				<option value=\"Wraps/Sandwhich\">Wraps/Sandwhich</option>\r\n";
			break;
		case "Side Dish":
			selectOptionsCategory += "<option value=\"nothing\">..</option>\r\n"
					+ "				<option value=\"Appetizer\">Appetizer</option>\r\n"
					+ "				<option value=\"Bread\">Bread</option>\r\n"
					+ "				<option value=\"Breakfast\">Breakfast</option>\r\n"
					+ "				<option value=\"Dessert\">Dessert</option>\r\n"
					+ "				<option value=\"Drink\">Drink</option>\r\n"
					+ "				<option value=\"Main Course\">Main Course</option>\r\n"
					+ "				<option value=\"Salad\">Salad</option>\r\n"
					+ "				<option value=\"Condiment/Sauce\">Condiment/Sauce</option>\r\n"
					+ "				<option value=\"Side Dish\" selected>Side Dish</option>\r\n"
					+ "				<option value=\"Snack\">Snack</option>\r\n"
					+ "				<option value=\"Soup\">Soup</option>\r\n"
					+ "				<option value=\"Wraps/Sandwhich\">Wraps/Sandwhich</option>\r\n";
			break;
		case "Snack":
			selectOptionsCategory += "<option value=\"nothing\">..</option>\r\n"
					+ "				<option value=\"Appetizer\">Appetizer</option>\r\n"
					+ "				<option value=\"Bread\">Bread</option>\r\n"
					+ "				<option value=\"Breakfast\">Breakfast</option>\r\n"
					+ "				<option value=\"Dessert\">Dessert</option>\r\n"
					+ "				<option value=\"Drink\">Drink</option>\r\n"
					+ "				<option value=\"Main Course\">Main Course</option>\r\n"
					+ "				<option value=\"Salad\">Salad</option>\r\n"
					+ "				<option value=\"Condiment/Sauce\">Condiment/Sauce</option>\r\n"
					+ "				<option value=\"Side Dish\">Side Dish</option>\r\n"
					+ "				<option value=\"Snack\" selected>Snack</option>\r\n"
					+ "				<option value=\"Soup\">Soup</option>\r\n"
					+ "				<option value=\"Wraps/Sandwhich\">Wraps/Sandwhich</option>\r\n";
			break;
		case "Soup":
			selectOptionsCategory += "<option value=\"nothing\">..</option>\r\n"
					+ "				<option value=\"Appetizer\">Appetizer</option>\r\n"
					+ "				<option value=\"Bread\">Bread</option>\r\n"
					+ "				<option value=\"Breakfast\">Breakfast</option>\r\n"
					+ "				<option value=\"Dessert\">Dessert</option>\r\n"
					+ "				<option value=\"Drink\">Drink</option>\r\n"
					+ "				<option value=\"Main Course\">Main Course</option>\r\n"
					+ "				<option value=\"Salad\">Salad</option>\r\n"
					+ "				<option value=\"Condiment/Sauce\">Condiment/Sauce</option>\r\n"
					+ "				<option value=\"Side Dish\">Side Dish</option>\r\n"
					+ "				<option value=\"Snack\">Snack</option>\r\n"
					+ "				<option value=\"Soup\" selected>Soup</option>\r\n"
					+ "				<option value=\"Wraps/Sandwhich\">Wraps/Sandwhich</option>\r\n";
			break;
		case "Wraps/Sandwhich":
			selectOptionsCategory += "<option value=\"nothing\">..</option>\r\n"
					+ "				<option value=\"Appetizer\">Appetizer</option>\r\n"
					+ "				<option value=\"Bread\">Bread</option>\r\n"
					+ "				<option value=\"Breakfast\">Breakfast</option>\r\n"
					+ "				<option value=\"Dessert\">Dessert</option>\r\n"
					+ "				<option value=\"Drink\">Drink</option>\r\n"
					+ "				<option value=\"Main Course\">Main Course</option>\r\n"
					+ "				<option value=\"Salad\">Salad</option>\r\n"
					+ "				<option value=\"Condiment/Sauce\">Condiment/Sauce</option>\r\n"
					+ "				<option value=\"Side Dish\">Side Dish</option>\r\n"
					+ "				<option value=\"Snack\">Snack</option>\r\n"
					+ "				<option value=\"Soup\">Soup</option>\r\n"
					+ "				<option value=\"Wraps/Sandwhich\" selected>Wraps/Sandwhich</option>\r\n";
			break;
		}
		return selectOptionsCategory;
	}

	private String[] getCreateRecipeVis() {

		String[] returnHTML = new String[2];
		if (util.UtilityData.validUser()) {
			returnHTML[0] = "<a style=\"text-decoration:none;color:white;\" href=\"/csci4830-recipe-book/LogOut\">Log Out</a>";
			returnHTML[1] = "<a style=\"text-decoration:none;color:white;\" href=\"/csci4830-recipe-book/CreateRecipe.html\">Create Recipe</a> ";
		} else {
			returnHTML[0] = "<a style=\"text-decoration:none;color:white;\" href=\"/csci4830-recipe-book/login.html\">Login/Register</a>";
			returnHTML[1] = "<a style=\"display:none;text-decoration:none;color:white;\" href=\"/csci4830-recipe-book/CreateRecipe.html\">Create Recipe</a> ";
		}
		return returnHTML;
	}
	
	
	
	
	
	

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
