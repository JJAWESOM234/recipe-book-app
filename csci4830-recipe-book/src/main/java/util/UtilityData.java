package util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtilityData {
	private static String username;
	private static String password;
	private static int recipeID;
	private static boolean ratingNew;

	public static String getUsername() {
		return username;
	}

	public static void setUsername(String user) {
		if (user != null && !(user.isEmpty())) {
			username = user;
		} else {
			username = null;
		}
	}

	public static String getPassword() {
		return password;
	}

	public static void setPassword(String pass) {
		if (pass != null && !(pass.isEmpty())) {
			password = pass;
		} else {
			password = null;
		}
	}

	public static boolean validUser() {
		if (username != null && password != null) {
			return true;
		} else {
			return false;
		}
	}

	public static void userLogOut() {
		username = null;
		password = null;
	}

	public static int getUserId() {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		int userId = -1;
		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;

			String selectSQL = "SELECT * FROM userLogin WHERE username = ? AND password = ?";
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			System.out.println(username);

			ResultSet rs = preparedStatement.executeQuery();

			while (rs.next()) {
				userId = rs.getInt("userid");
			}
			return userId;

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userId;
	}
//	public static int getUserId(String user, String pass) {
//		Connection connection = null;
//		PreparedStatement preparedStatement = null;
//
//		int userId = -1;
//		try {
//			DBConnection.getDBConnection();
//			connection = DBConnection.connection;
//
//			String selectSQL = "SELECT * FROM userLogin WHERE username = ? AND password = ?";
//			preparedStatement = connection.prepareStatement(selectSQL);
//			preparedStatement.setString(1, user);
//			preparedStatement.setString(1, pass);
//
//			ResultSet rs = preparedStatement.executeQuery();
//
//			while (rs.next()) {
//				userId = rs.getInt("userid");
//			}
//			return userId;
//
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//		return userId;
//	}

	public static int getRecipeID() {
		return recipeID;
	}

	public static void setRecipeID(int recipeID) {
		UtilityData.recipeID = recipeID;
	}

	public static int getUserRatingPerRecipe(int recipeId) {
		Connection connection = null;
		PreparedStatement preparedStmt = null;
		int retRating = -1;
		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;
			int userId = getUserId();
			String selectSQL = "SELECT rating FROM ratings WHERE recipeId = ? AND userId = ?";
			preparedStmt = connection.prepareStatement(selectSQL);
			preparedStmt.setInt(1, recipeId);
			preparedStmt.setInt(2, userId);
			System.out.println("***" + userId);

			ResultSet rs = preparedStmt.executeQuery();

			if (rs.next() != false) {
				do {
					retRating =  rs.getInt("rating");
					
				}while (rs.next());
			} else {
				retRating = -1;
			}

			rs.close();
			preparedStmt.close();
			connection.close();
			return retRating;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retRating;
	}

	public static double getRatingAvg(int recipeId) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		double ratingAvg = 0.0;
		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;

			String selectSQL = "SELECT * FROM ratings WHERE recipeId = ?";
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, recipeId);

			ResultSet rs = preparedStatement.executeQuery();

			int ratingSum = 0;
			int ratingCount = 0;
			while (rs.next()) {
				ratingSum += rs.getInt("rating");
				ratingCount++;

			}
			if (ratingCount != 0) {
				ratingAvg = roundAvoid(((double) ratingSum / (double) ratingCount), 1);
			}
			return ratingAvg;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ratingAvg;
	}

	public static double getRatingAvg(int recipeId, int newRating) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		double ratingAvg = 0.0;
		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;

			String selectSQL = "SELECT * FROM ratings WHERE recipeId = ?";
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setInt(1, recipeId);

			ResultSet rs = preparedStatement.executeQuery();

			int ratingSum = 0;
			int ratingCount = 0;
			while (rs.next()) {

				ratingSum += rs.getInt("rating");
				ratingCount++;
				System.out.println(rs.getInt("rating") + " || " + ratingSum + " || " + ratingCount);
			}
			ratingSum += newRating;
			ratingCount++;

			System.out.println(newRating + " ||| " + ratingSum + " ||| " + ratingCount);

			ratingAvg = roundAvoid(((double) ratingSum / (double) ratingCount), 1);

			return ratingAvg;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ratingAvg;
	}

	public static String getRatingOptions(String rating) {
		String selectOptionsRating = "";
		switch (rating) {
		case "nothing":
			selectOptionsRating += "<option value=\"nothing\" selected>..</option>\r\n"
					+ "				<option value=\"1\">One</option>\r\n"
					+ "				<option value=\"2\">Two</option>\r\n"
					+ "				<option value=\"3\">Three</option>\r\n"
					+ "				<option value=\"4\">Four</option>\r\n"
					+ "				<option value=\"5\">Five</option>\r\n";
			break;
		case "1":
			selectOptionsRating += "<option value=\"nothing\">..</option>\r\n"
					+ "				<option value=\"1\" selected>One</option>\r\n"
					+ "				<option value=\"2\">Two</option>\r\n"
					+ "				<option value=\"3\">Three</option>\r\n"
					+ "				<option value=\"4\">Four</option>\r\n"
					+ "				<option value=\"5\">Five</option>\r\n";
			break;
		case "2":
			selectOptionsRating += "<option value=\"nothing\">..</option>\r\n"
					+ "				<option value=\"1\">One</option>\r\n"
					+ "				<option value=\"2\" selected>Two</option>\r\n"
					+ "				<option value=\"3\">Three</option>\r\n"
					+ "				<option value=\"4\">Four</option>\r\n"
					+ "				<option value=\"5\">Five</option>\r\n";
			break;
		case "3":
			selectOptionsRating += "<option value=\"nothing\">..</option>\r\n"
					+ "				<option value=\"1\">One</option>\r\n"
					+ "				<option value=\"2\">Two</option>\r\n"
					+ "				<option value=\"3\" selected>Three</option>\r\n"
					+ "				<option value=\"4\">Four</option>\r\n"
					+ "				<option value=\"5\">Five</option>\r\n";
			break;
		case "4":
			selectOptionsRating += "<option value=\"nothing\">..</option>\r\n"
					+ "				<option value=\"1\">One</option>\r\n"
					+ "				<option value=\"2\">Two</option>\r\n"
					+ "				<option value=\"3\">Three</option>\r\n"
					+ "				<option value=\"4\" selected>Four</option>\r\n"
					+ "				<option value=\"5\">Five</option>\r\n";
			break;
		case "5":
			selectOptionsRating += "<option value=\"nothing\">..</option>\r\n"
					+ "				<option value=\"1\">One</option>\r\n"
					+ "				<option value=\"2\">Two</option>\r\n"
					+ "				<option value=\"3\">Three</option>\r\n"
					+ "				<option value=\"4\">Four</option>\r\n"
					+ "				<option value=\"5\" selected>Five</option>\r\n";
			break;
		}
		return selectOptionsRating;
	}

	private static double roundAvoid(double value, int places) {
		double scale = Math.pow(10, places);
		return Math.round(value * scale) / scale;
	}

	public static boolean checkValidNewRating(int recipeId, int userId) {
		Connection connection = null;
		PreparedStatement preparedStmt = null;
		boolean retBool = false;
		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;

			String selectSQL = "SELECT * FROM ratings WHERE recipeId = ? AND userId = ?";
			preparedStmt = connection.prepareStatement(selectSQL);
			preparedStmt.setInt(1, recipeId);
			preparedStmt.setInt(2, userId);

			ResultSet rs = preparedStmt.executeQuery();

			if (rs.next() != false) {
				// Yes this combination exists
				retBool = false;
			} else {
				// no there is no combination
				retBool = true;
			}

			rs.close();
			preparedStmt.close();
			connection.close();
			return retBool;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retBool;
	}

}
