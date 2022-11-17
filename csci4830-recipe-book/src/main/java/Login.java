
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
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login() {
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
		String userName = request.getParameter("user");
		String password = request.getParameter("pass");

		response.getWriter().append(userName + " " + password);
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			DBConnection.getDBConnection();
			connection = DBConnection.connection;

			String selectSQL = "SELECT * FROM userLogin WHERE username LIKE ?";
			preparedStatement = connection.prepareStatement(selectSQL);
			preparedStatement.setString(1, userName);

			ResultSet rs = preparedStatement.executeQuery();
			
			if (rs.next() != false) {
				do {
					System.out.println("john");
					if (password.equals(rs.getString("password"))) {
						response.getWriter().append("Login Succesful");
						response.sendRedirect(request.getContextPath() + "/SearchRecipeList");
					}
				} while (rs.next());
			} else {
				response.getWriter().append("Login Failed");
				response.sendRedirect(request.getContextPath() + "/login.html");
			}

		} catch (SQLException e) {
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
