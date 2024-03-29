package util;
import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
   public static Connection connection = null;

   public static void getDBConnection() {
      System.out.println("-------- MySQL JDBC Connection Testing ------------");
      try {
         Class.forName("com.mysql.cj.jdbc.Driver");
      } catch (ClassNotFoundException e) {
         System.out.println("Where is your MySQL JDBC Driver?");
         e.printStackTrace();
         return;
      }
      System.out.println("MySQL JDBC Driver Registered!");

      connection = null;
      try {
         UtilProp.loadProperty();
         connection = DriverManager.getConnection(getURL(), getUserName(), getPassword());
      } catch (Exception e) {
         System.out.println("Connection Failed! Check output console");
         e.printStackTrace();
         return;
      }

      if (connection != null) {
         System.out.println("You made it, take control your database now!");
      } else {
         System.out.println("Failed to make connection!");
      }
   }

   static String getURL() {
      String url = UtilProp.getProp("url");
      System.out.println("[DBG] URL: " + url);
      return url;
   }

   static String getUserName() {
      String usr = UtilProp.getProp("user");
      System.out.println("[DBG] Username: " + usr);
      return usr;
   }

   static String getPassword() {
      String pwd = UtilProp.getProp("password");
//      System.out.println("[DBG] Password: " + pwd);
      return pwd;
   }
}