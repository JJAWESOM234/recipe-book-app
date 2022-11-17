
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

		String userName = request.getParameter("user");
		String password = request.getParameter("pass");
		
		// Add username and password to the DB 
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		
		try { 
			DBConnection.getDBConnection();
			connection = DBConnection.connection;
			
			 String insertSQL = "INSERT INTO userLogin (username, password) "
	        	  		+ "VALUES ('" + userName + "', '" + password + "')";
			 preparedStatement = connection.prepareStatement(insertSQL);
	         preparedStatement.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		displayHTML(request, response, userName);


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
	
	private void displayHTML(HttpServletRequest request, HttpServletResponse response, String userName) throws IOException
	{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String title = "Account Registered!";
		String docType = "<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">\n"; //
		out.println(docType + //
				"<html>\n" + //
				"<head><title>" + title + "</title> <style> .container{text-align: center;} </style></head>\n" + //
				"<body bgcolor=\"#f0f0f0\">\n" + //
				"<h1 align=\"center\">" + title + "</h1>\n");
		out.println("<div class=\"container\"><a href=" + request.getContextPath() + "/SearchRecipeList>Continue to Recipe List</a></div> <br>");
		out.println("</body></html>");
	}

}
