
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import util.DBConnection;

/**
 * Servlet implementation class CreateRecipe
 */
@WebServlet("/CreateRecipe")
public class CreateRecipe extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateRecipe() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String recipeName = request.getParameter("RName");
		String recipeType = request.getParameter("RType");
		String ingredientList = request.getParameter("IngrList");
		String intructions = request.getParameter("Inst");
		String additionalInfo = request.getParameter("AddInfo");
		String imageURL = request.getParameter("IURL");

		java.util.Date date = new java.util.Date();
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());

		Connection connection = null;
		String insertSql = " INSERT INTO recipes (recipeId, recipeName, recipeType, createdDate, visibility, ingredientList, instructions, additionalInfo, imageURL, userid, rating) values (default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;
			PreparedStatement preparedStmt = connection.prepareStatement(insertSql);
			preparedStmt.setString(1, recipeName);
			preparedStmt.setString(2, recipeType);
			preparedStmt.setDate(3, sqlDate);
			preparedStmt.setInt(4, 1);
			preparedStmt.setString(5, ingredientList);
			preparedStmt.setString(6, intructions);
			preparedStmt.setString(7, additionalInfo);
			preparedStmt.setString(8, imageURL);
			preparedStmt.setInt(9, 2);
			preparedStmt.setInt(10, 0);
			preparedStmt.execute();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		displayHTML(request, response);

	}
	
	private void displayHTML(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String title = "Recipe Created!";
		String docType = "<!doctype html public \"-//w3c//dtd html 4.0 transitional//en\">\n"; //
		out.println(docType + //
				"<html>\n" + //
				"<head><title>" + title + "</title> <style> .container{text-align: center;} </style></head>\n" + //
				"<body bgcolor=\"#f0f0f0\">\n" + //
				"<h1 align=\"center\">" + title + "</h1>\n");
		out.println("<div class=\"container\"><a href=" + request.getContextPath() + "/SearchRecipeList>Continue to Recipe List</a></div> <br>");
		out.println("</body></html>");
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
