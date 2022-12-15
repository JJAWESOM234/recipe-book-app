
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
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Register() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String username = request.getParameter("user");
		String password = request.getParameter("pass");

		username = username.replaceAll("\\s+", "");
		password = password.replaceAll("\\s+", "");
		// Add username and password to the DB
		Connection connection = null;
	

		if (checkValidNewUser(username)) {
			try {
				DBConnection.getDBConnection();
				connection = DBConnection.connection;

				String insertSql = "INSERT INTO userLogin (username, password) values (?, ?)";
				PreparedStatement preparedStmt = connection.prepareStatement(insertSql);
				preparedStmt.setString(1, username);
				preparedStmt.setString(2, password);
				preparedStmt.execute();
				connection.close();
				util.UtilityData.setUsername(username);
				util.UtilityData.setPassword(password);
				preparedStmt.close();
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			displayHTMLSucess(request, response, username);
		} else {
			displayHTMLFail(request, response, username);

		}

		// Redirect to recipe list page, should be conditional based on valid login
//				response.sendRedirect(request.getContextPath() + "/Search.html");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);

	}

	private boolean checkValidNewUser(String user) {

		Connection connection = null;
		PreparedStatement preparedStatement = null;
		boolean retBool = false;
		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;

			String selectSQL = "SELECT * FROM userLogin WHERE username = ?";
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1, user);

			ResultSet rs = preparedStatement.executeQuery();

			if (rs.next() != false) {
				// Yes this username exists
				retBool = false;

			} else {
				// no there is no user
				retBool = true;
			}

			return retBool;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return retBool;
	}

	private void displayHTMLSucess(HttpServletRequest request, HttpServletResponse response, String userName)
			throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String title = "Account Registered!";
		String docType = "<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">\n"; //
		out.println(docType + //
				"<html>\n" + //
				"<head><title>" + title + "</title> <style> .container{text-align: center;} </style></head>\n" + //
				"<body bgcolor=\"#f0f0f0\">\n" + //
				"<h1 align=\"center\">" + title + "</h1>\n");
		out.println("<div class=\"container\"><a href=" + request.getContextPath()
				+ "/SearchRecipeList>Continue to Recipe List</a></div> <br>");
		out.println("</body></html>");
	}

	private void displayHTMLFail(HttpServletRequest request, HttpServletResponse response, String userName)
			throws IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String title = "Registration Failed";
		String docType = "<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">\n"; //
		out.println(docType + //
				"<html>\n" + //
				"<head><title>" + title + "</title> <style> .container{text-align: center;} </style></head>\n" + //
				"<body bgcolor=\"#f0f0f0\">\n" + //
				"<h1 align=\"center\">" + title + "</h1>\n");
		out.println("<div class=\"container\"><p>Username is already in use.</p><a href=" + request.getContextPath()
				+ "/register.html>Try again</a></div> <br>");
		out.println("</body></html>");
	}

}
