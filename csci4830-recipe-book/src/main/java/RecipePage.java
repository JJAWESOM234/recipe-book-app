
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

		getRecipeInformation(recipeIdSearch);

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
				String additionalInfo = rs.getString("additionalInfo").trim();
				String imageURL = rs.getString("imageURL").trim();
				int userId = rs.getInt("userid");

				System.out.println(recipeId + " | " + recipeName + " | " + recipeType + " | " + createdDate + " | "
						+ ingredientList + " | " + instructions + " | " + additionalInfo + " | " + imageURL + " | "
						+ userId);

				String username = getUser(userId);


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
