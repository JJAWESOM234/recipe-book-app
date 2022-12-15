
import java.io.IOException;
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
 * Servlet implementation class RateRecipe
 */
@WebServlet("/RateRecipe")
public class RateRecipe extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public RateRecipe() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String ratingIn = request.getParameter("rating");
		int rating = -1;
		if (!(ratingIn.equals("nothing"))) {
			rating = Integer.parseInt(ratingIn);
		} else {
			response.sendRedirect(request.getContextPath() + "/RecipePage");
			return;
		}

		Connection connection = null;
		PreparedStatement preparedStmt = null;

		String insertSql = "INSERT INTO ratings (rating, recipeId, userId) values (?, ?, ?)";
		int recipeId = util.UtilityData.getRecipeID();
		int userId = util.UtilityData.getUserId();

		if (util.UtilityData.checkValidNewRating(recipeId, userId)) {
			try {

				DBConnection.getDBConnection();
				connection = DBConnection.connection;
				preparedStmt = connection.prepareStatement(insertSql);
				preparedStmt.setDouble(1, rating);
				preparedStmt.setInt(2, recipeId);
				preparedStmt.setInt(3, userId);

			
				updateRecipeTable(rating, recipeId);
				preparedStmt.execute();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else {
			
			String updateSql = "UPDATE ratings SET rating = ? WHERE recipeId = ? AND userId = ?";
			try {
				DBConnection.getDBConnection();
				connection = DBConnection.connection;
				preparedStmt = connection.prepareStatement(updateSql);
				preparedStmt.setInt(1, rating);
				preparedStmt.setInt(2, recipeId);
				preparedStmt.setInt(3, userId);

				
				updateRecipeTable(rating, recipeId);
				preparedStmt.execute();
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		response.sendRedirect(request.getContextPath() + "/RecipePage");

	}

	

	private void updateRecipeTable(int rating, int recipeId) {
		double ratingUpdated = util.UtilityData.getRatingAvg(recipeId, rating);

		Connection connection = null;
		String insertSql = "UPDATE recipes SET rating = ? WHERE recipeId = ?";
		System.out.println("why" + ratingUpdated);
		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;
			PreparedStatement preparedStmt = connection.prepareStatement(insertSql);
			preparedStmt.setDouble(1, ratingUpdated);
			preparedStmt.setInt(2, recipeId);

			preparedStmt.execute();
			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
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

}
