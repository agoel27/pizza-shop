/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class PizzaStore {

   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of PizzaStore
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */
   public PizzaStore(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end PizzaStore

   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
    public int executeQueryAndPrintResult(String query) throws SQLException {
      // Creates a statement object
      Statement stmt = this._connection.createStatement();
  
      // Issues the query instruction
      ResultSet rs = stmt.executeQuery(query);
  
      // Obtains the metadata object for the returned result set. The metadata
      // contains row and column info.
      ResultSetMetaData rsmd = rs.getMetaData();
      int numCol = rsmd.getColumnCount();
      int rowCount = 0;
  
      // Create an array to store the maximum width of each column
      int[] columnWidths = new int[numCol];

      // Set the width of each column to at least the length of the column name
      for (int i = 1; i <= numCol; i++)
         columnWidths[i - 1] = rsmd.getColumnName(i).length();
  
      // Determine the maximum width of each column
      while (rs.next()) {
          for (int i = 1; i <= numCol; i++) {
              String columnValue = rs.getString(i);
              if (columnValue != null) {
                  columnWidths[i - 1] = Math.max(columnWidths[i - 1], columnValue.length());
              } else {
                  columnWidths[i - 1] = Math.max(columnWidths[i - 1], 4); // Handle null values with a width of 4
              }
          }
      }
  
      // Reset the result set to the beginning (before we start printing)
      rs.beforeFirst();
  
      // Output the header row
      boolean outputHeader = true;
      while (rs.next()) {
          if (outputHeader) {
               for (int i = 1; i <= numCol; i++) {
                  // Print the column name with the calculated width
                  System.out.print(String.format("%-" + columnWidths[i - 1] + "s | ", rsmd.getColumnName(i)));
               }
               int totalWidth = 0;
               for (int width : columnWidths) {
                  totalWidth += (width + 2);
               }
               System.out.println();
               outputHeader = false;
          }
          
          // Output the data rows with column values aligned
          for (int i = 1; i <= numCol; i++) {
              // Print the column value with the calculated width
              System.out.print(String.format("%-" + columnWidths[i - 1] + "s | ", rs.getString(i)));
          }
          System.out.println();
          rowCount++;
      }
      
      // Close the statement
      stmt.close();
      return rowCount;
   }
  

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            PizzaStore.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      PizzaStore esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the PizzaStore object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new PizzaStore (dbname, dbport, user, "");

         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("\nMAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT\n");
            String authorizedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorizedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorizedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("\nMAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Profile");
                System.out.println("2. Update Profile");
                System.out.println("3. View Menu");
                System.out.println("4. Place Order"); //make sure user specifies which store
                System.out.println("5. View Full Order ID History");
                System.out.println("6. View Past 5 Order IDs");
                System.out.println("7. View Order Information"); //user should specify orderID and then be able to see detailed information about the order
                System.out.println("8. View Stores"); 

                //**the following functionalities should only be able to be used by drivers & managers**
                System.out.println("9. Update Order Status");

                //**the following functionalities should ony be able to be used by managers**
                System.out.println("10. Update Menu");
                System.out.println("11. Update User");

                System.out.println(".........................");
                System.out.println("20. Log out\n");
                switch (readChoice()){
                   case 1: viewProfile(esql, authorizedUser); break;
                   case 2: updateProfile(esql, authorizedUser); break;
                   case 3: viewMenu(esql); break;
                   case 4: placeOrder(esql); break;
                   case 5: viewAllOrders(esql); break;
                   case 6: viewRecentOrders(esql); break;
                   case 7: viewOrderInfo(esql); break;
                   case 8: viewStores(esql); break;
                   case 9: updateOrderStatus(esql); break;
                   case 10: updateMenu(esql); break;
                   case 11: updateUser(esql); break;



                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   public static String getInput(String item) {
      String input = "";
      do {
         System.out.print("Please enter " + item + ": ");
         try {
            input = in.readLine().trim();
            break;
         } catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }
      } while (true);
      return input;
  }

   /*
    * Gets input from user
    */
   public static String getInput(String item, int maxLength) {
      String input = "";
      do {
         System.out.print("Please enter " + item + " (1-" + maxLength + " characters): ");
         try {
            input = in.readLine().trim();
            if(input.isEmpty()) {
               System.out.println(item + " cannot be empty!");
               continue;
            } else if (input.length() > 30) {
               System.out.println(item + " cannot be greater than 30 characters!");
               continue;
            }
            break;
         } catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }
      } while (true);
      return input;
   }

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
    public static void CreateUser(PizzaStore esql){
      String loginInput = "";
      String passwordInput = "";
      String phoneNumInput = "";
      List<List<String>> existingLogins = new ArrayList<>();
      boolean loginExists;

      // get existing logins
      try {
         existingLogins = esql.executeQueryAndReturnResult("SELECT login FROM Users");
      } catch (SQLException e) {
         System.out.println("Error getting existing logins: " + e.getMessage());
         return;
      }

      // get and validate loginInput
      do {
         System.out.print("Please enter login (1-50 characters): ");
         try {
            loginInput = in.readLine().trim();

            if(loginInput.isEmpty()) {
               System.out.println("Login cannot be empty!");
               continue;
            } else if (loginInput.length() > 50) {
               System.out.println("Login cannot be greater than 50 characters!");
               continue;
            }

            // check if login already exists
            loginExists = false;
            for (List<String> row : existingLogins) {
               if (row.get(0).equals(loginInput)) {
                  loginExists = true;
                  break;
               }
            }
            if (loginExists) {
               System.out.println("This login already exists! Please try a different login.");
               continue;
            }
            
            break;
         } catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }
      }while (true);

      // get and validate password
      passwordInput = getInput("password", 30);

      // get and validate phoneNum
      do {
         System.out.print("Please enter phone number: ");
         try {
            phoneNumInput = in.readLine().trim();
            if(phoneNumInput.isEmpty()) {
               System.out.println("Phone number cannot be empty!");
               continue;
            } else if (phoneNumInput.length() > 20) {
               System.out.println("Phone number cannot be greater than 20 characters!");
               continue;
            }
            break;
         } catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }
      }while (true);

      // add new user to db
      try {
         esql.executeUpdate("INSERT INTO Users (login, password, role, favoriteItems, phoneNum) VALUES ('" + loginInput + "', '" + passwordInput + "', 'customer', NULL, '" + phoneNumInput + "');");
         System.out.println("User created successfully!");
      } catch (SQLException e) {
            System.out.println("Error inserting user: " + e.getMessage());
      }
     
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null if the user does not exist
    **/
   public static String LogIn(PizzaStore esql){
      String loginInput = "";
      String passwordInput = "";
      String userPassword = "";
      List<List<String>> loginInfo = new ArrayList<>();

      // get and validate loginInput
      do {
         System.out.print("Please enter login (1-50 characters): ");
         try {
            loginInput = in.readLine().trim();

            if(loginInput.isEmpty()) {
               System.out.println("Login cannot be empty!");
               continue;
            } else if (loginInput.length() > 50) {
               System.out.println("Login cannot be greater than 50 characters!");
               continue;
            }

            // get user login information and authenticate login
            try {
               loginInfo = esql.executeQueryAndReturnResult("SELECT login, password FROM Users WHERE login='" + loginInput + "';");
            } catch (SQLException e) {
               System.out.println("Error getting user login information: " + e.getMessage());
               return null;
            }
            if(loginInfo.isEmpty()) {
               System.out.println(loginInput + " does not exist!");
               return null;
            }

            break;
         } catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }
      }while (true);

      // get and validate passwordInput
      do {
         System.out.print("Please enter password (1-30 characters): ");
         try {
            passwordInput = in.readLine().trim();
            userPassword = loginInfo.get(0).get(1);

            if(passwordInput.isEmpty()) {
               System.out.println("Password cannot be empty!");
               continue;
            } else if (passwordInput.length() > 30) {
               System.out.println("Password cannot be greater than 30 characters!");
               continue;
            }

            if (!passwordInput.equals(userPassword)) {
               System.out.println("Wrong password! Please try again.");
               continue;
            }

            break;
         } catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }
      }while (true);

      System.out.println("Welcome " + loginInput + "!");

      return loginInput;
   }//end LogIn

   
   /*
    * View profile of authorized user
    */
   public static void viewProfile(PizzaStore esql, String authorizedUser) {
      try {
         esql.executeQueryAndPrintResult("SELECT login, password, favoriteItems, phoneNum FROM Users WHERE login='" + authorizedUser + "'");
      } catch (SQLException e) {
         System.out.println("Error getting login information: " + e.getMessage());
         return;
      }
      
   }//end viewProfile

   /*
    * Prompts user if they want to update 'profileItem' in profile
    * @param the thing to update in profile (string)
    */
    public static String updateProfilePrompt(String profileItem) {
      String response = "";
      do {
         System.out.print("Would you like to change your " + profileItem + " (y/n)? ");
         try {
            response = in.readLine();
            if(!response.equals("y") && !response.equals("n")) {
               System.out.println("Please enter 'y' or 'n'.");
               continue;
            }
            break;
         } catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }
      } while(true);
      return response;
    }//end updateProfilePrompt

   /*
    * Allows user to update password, phone number, favorite items
    */
   public static void updateProfile(PizzaStore esql, String authorizedUser) {
      String response = "";
      String passwordInput = "";
      String phoneNumInput = "";
      String favoriteItemsInput = "";

      response = updateProfilePrompt("password");
      if (response.equals("y")) {
         passwordInput = getInput("password", 30);
         try {
            esql.executeUpdate("UPDATE Users SET password='" + passwordInput + "' WHERE login ='" + authorizedUser + "';");
         } catch (SQLException e) {
            System.out.println("Error updating password: " + e.getMessage());
         }
         System.out.println("Successfully updated password!");
      }

      response = updateProfilePrompt("phone number");
      if (response.equals("y")) {
         phoneNumInput = getInput("phone number", 20);
         try {
            esql.executeUpdate("UPDATE Users SET phoneNum='" + phoneNumInput + "' WHERE login ='" + authorizedUser + "';");
         } catch (SQLException e) {
            System.out.println("Error updating phone number: " + e.getMessage());
         }
         System.out.println("Successfully updated phone number!");
      }

      response = updateProfilePrompt("favorite items");
      if (response.equals("y")) {
         favoriteItemsInput = getInput("favorite items");
         try {
            esql.executeUpdate("UPDATE Users SET favoriteItems='" + favoriteItemsInput + "' WHERE login ='" + authorizedUser + "';");
         } catch (SQLException e) {
            System.out.println("Error updating favorite items: " + e.getMessage());
         }
         System.out.println("Successfully updated favorite items!");
      }

   }// end updateProfile

   public static void viewMenu(PizzaStore esql) {
      try {
         esql.executeQueryAndPrintResult("SELECT * FROM Items");
      } catch (SQLException e) {
         System.out.println("Error fetching menu items: " + e.getMessage());
      }
   }
   public static void placeOrder(PizzaStore esql) {}
   public static void viewAllOrders(PizzaStore esql) {}
   public static void viewRecentOrders(PizzaStore esql) {}
   public static void viewOrderInfo(PizzaStore esql) {}
   public static void viewStores(PizzaStore esql) {}
   public static void updateOrderStatus(PizzaStore esql) {}
   public static void updateMenu(PizzaStore esql) {}
   public static void updateUser(PizzaStore esql) {}


}//end PizzaStore

