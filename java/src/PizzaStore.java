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
 import java.util.Map;
 import java.util.ArrayList;
 import java.util.HashMap;
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
            String role = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: 
                  authorizedUser = LogIn(esql);
                  break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorizedUser != null) {
               role = getRole(esql, authorizedUser);
               if (!role.trim().equals("customer")) {
                  System.out.println("Logged in as a " + role);
               } 
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
                if (role.trim().equals("driver") || role.trim().equals("manager")) System.out.println("9. Update Order Status");

                //**the following functionalities should ony be able to be used by managers**
                if (role.trim().equals("manager")) {
                  System.out.println("10. Update Menu");
                  System.out.println("11. Update User");
                }

                System.out.println(".........................");
                System.out.println("20. Log out\n");
                switch (readChoice()){
                   case 1: viewProfile(esql, authorizedUser); break;
                   case 2: updateProfile(esql, authorizedUser); break;
                   case 3: viewMenu(esql); break;
                   case 4: placeOrder(esql, authorizedUser); break;
                   case 5: viewAllOrders(esql, authorizedUser, role); break;
                   case 6: viewRecentOrders(esql, authorizedUser, role); break;
                   case 7: viewOrderInfo(esql, authorizedUser, role); break;
                   case 8: viewStores(esql); break;
                   case 9: if (role.trim().equals("customer")) System.out.println("Unrecognized choice!");
                     else updateOrderStatus(esql); break;
                   case 10: if (role.trim().equals("customer") || role.trim().equals("driver")) System.out.println("Unrecognized choice!");
                     else updateMenu(esql); break;
                   case 11: if (role.trim().equals("customer") || role.trim().equals("driver")) System.out.println("Unrecognized choice!");
                     else updateUser(esql); break;

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

 
    private static String getRole(PizzaStore psql, String authorizedUser) {
       try {
          String role = psql.executeQueryAndReturnResult("SELECT role FROM Users WHERE login='" + authorizedUser + "'").get(0).get(0);
          return role;
       }
       catch (SQLException e){
          System.out.println("Error fetching role: " + e.getMessage());
       }
       return "";
    }
 
    public static void Greeting(){
       System.out.println(
          "\n\n*******************************************************\n" +
          "              User Interface      	               \n" +
          "*******************************************************\n");
    }//end Greeting
 
    public static String getStringInput(String prompt) {
       String input = "";
       do {
          System.out.print(prompt);
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
    public static String getStringInput(String item, int maxLength) {
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
                if (row.get(0).trim().equals(loginInput)) {
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
       passwordInput = getStringInput("password", 30);
 
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
 
             if (!passwordInput.trim().equals(userPassword)) {
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
     public static String getYNInput(String prompt) {
       String response = "";
       do {
          System.out.print(prompt + " (y/n)? ");
          try {
             response = in.readLine();
             if(!response.trim().equals("y") && !response.trim().equals("n")) {
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
 
       response = getYNInput("Would you like to update your password");
       if (response.trim().equals("y")) {
          passwordInput = getStringInput("password", 30);
          try {
             esql.executeUpdate("UPDATE Users SET password='" + passwordInput + "' WHERE login ='" + authorizedUser + "';");
          } catch (SQLException e) {
             System.out.println("Error updating password: " + e.getMessage());
          }
          System.out.println("Successfully updated password!");
       }
 
       response = getYNInput("Would you like to update your phone number");
       if (response.trim().equals("y")) {
          phoneNumInput = getStringInput("phone number", 20);
          try {
             esql.executeUpdate("UPDATE Users SET phoneNum='" + phoneNumInput + "' WHERE login ='" + authorizedUser + "';");
          } catch (SQLException e) {
             System.out.println("Error updating phone number: " + e.getMessage());
          }
          System.out.println("Successfully updated phone number!");
       }
 
       response = getYNInput("Would you like to update your favorite items");
       if (response.trim().equals("y")) {
          favoriteItemsInput = getStringInput("Please enter your favorite items: ");
          try {
             esql.executeUpdate("UPDATE Users SET favoriteItems='" + favoriteItemsInput + "' WHERE login ='" + authorizedUser + "';");
          } catch (SQLException e) {
             System.out.println("Error updating favorite items: " + e.getMessage());
          }
          System.out.println("Successfully updated favorite items!");
       }
 
    }// end updateProfile
 
    /*
     * Show menu
     */
    public static void viewMenu(PizzaStore esql) {
       try {
          while (true) {
             System.out.println("\nPlease select the type of menu you would like to see:");
             System.out.println("1. All items");
             System.out.println("2. Entrees");
             System.out.println("3. Sides");
             System.out.println("4. Drinks");
             System.out.println("5. Items under a certain price");
             System.out.println("9. Quit");
             String filter = "";
             switch (readChoice()){
                case 1: break;
                case 2: filter = " WHERE typeOfItem = 'entree'"; break;
                case 3: filter = " WHERE typeOfItem = 'sides'"; break;
                case 4: filter = " WHERE typeOfItem = 'drinks'"; break;
                case 5: 
                  double maxPrice = getMoneyInput("Please enter the maximum price: ");
                  filter = " WHERE price <= " + maxPrice;
                  break;
                case 9: return;
                default: System.out.println("Unrecognized choice!"); continue;
             }
 
             String order = "";
             System.out.println("\nIn what order would you like the menu?");
             System.out.println("1. Default order");
             System.out.println("2. Price (low to high)");
             System.out.println("3. Price (high to low)");
             switch (readChoice()) {
                case 1: break;
                case 2: order = " ORDER BY price ASC"; break;
                case 3: order = " ORDER BY price DESC"; break;
                default: System.out.println("Unrecognized choice!"); continue;
             }
             System.out.println(
           "\n*******************************************************\n" +
           "              Our Offerings         	               \n" +
           "*******************************************************");
             int rowCount = esql.executeQueryAndPrintResult("SELECT typeOfItem as \"Type\", itemName AS \"Item\", price AS \"Price\", description AS \"Description\", ingredients AS \"Ingredients\" FROM Items" + filter + order + ";");
             if (rowCount == 0) {
                System.out.println("No items found for the selected criteria!");
             }
           }
       } catch (SQLException e) {
          System.out.println("Error fetching menu items: " + e.getMessage());
       }
    }
 
    public static int getIntInput(String prompt) {
       int input;
       do {
          System.out.print(prompt);
          try {
             input = Integer.parseInt(in.readLine());
             if (input <= 0) {
                System.out.println("Your input is invalid!");
                continue;
             }
             break;
          }catch (Exception e) {
             System.out.println("Your input is invalid!");
             continue;
          }
       }while (true);
       return input;
    }//end getIntInput
 
    public static double getMoneyInput(String prompt) {
       double input;
       do {
          System.out.print(prompt);
          try {
             input = Double.parseDouble(in.readLine());
             if (input < 0) {
                System.out.println("Amount cannot be negative!");
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
 
    public static void placeOrder(PizzaStore esql, String authorizedUser) {
       int storeIDInput;
       String itemInput = "";
       int quantityInput;
       List<List<String>> itemNames = new ArrayList<>();
       String response = "";
       Map<String, Integer> orderMap = new HashMap<>();
 
       // get store ID from user and check if store exists
       do {
          storeIDInput = getIntInput("\nPlease enter the store ID that you want to order from: ");
          try {
             if (esql.executeQuery("SELECT * FROM Store WHERE storeID=" + storeIDInput + ";") == 0) {
                System.out.println("StoreID " + storeIDInput + " does not exist!");
                continue;
             }
             break;
          } catch (SQLException e) {
             System.out.println("Error fetching store IDs: " + e.getMessage());
             return;
          }
       } while(true);
 
       // get all item names
       try {
          itemNames = esql.executeQueryAndReturnResult("SELECT itemName FROM Items;");
       } catch (SQLException e) {
          System.out.println("Error fetching menu item names: " + e.getMessage());
       }
 
       //  get user's order
       do {
          do {
             itemInput = getStringInput("\nPlease enter the name of the item you want to order : ");
             boolean itemExists = false;
             for (List<String> row : itemNames) {
                if (row.get(0).trim().equals(itemInput)) {
                   itemExists = true;
                   break;
                }
             }
             if (itemExists)
                break;
             System.out.println("Item " + itemInput + " does not exist!");
          } while (true);
 
          quantityInput = getIntInput("Please enter the quantity of " + itemInput + " you want to order: ");
          orderMap.put(itemInput, orderMap.getOrDefault(itemInput, 0) + quantityInput);
          response = getYNInput("Would you like to order more items");
 
       } while (response.trim().equals("y"));
 
       // calculate total order price
       long totalPriceInCents = 0;
       System.out.println("\nYou ordered:");
       for (Map.Entry<String, Integer> entry : orderMap.entrySet()) {
          String itemName = entry.getKey();
          int quantity = entry.getValue();
          try {
             String itemPrice = esql.executeQueryAndReturnResult("SELECT price FROM Items WHERE itemName='" + itemName + "'").get(0).get(0);
             System.out.println("Item: " + itemName + ", Quantity: " + quantity + ", Price: " + itemPrice);
             totalPriceInCents += Math.round(Double.parseDouble(itemPrice) * 100) * quantity;
          } catch (SQLException e) {
             System.out.println("Error fetching " + itemName + "'s price: " + e.getMessage());
             return;
          }
       }
       System.out.println("The total price is: $" + (totalPriceInCents / 100) + "." + (totalPriceInCents % 100));
 
       // add order to FoodOrder table
       int orderID;
       try {
          orderID = Integer.parseInt(esql.executeQueryAndReturnResult("SELECT orderID FROM FoodOrder ORDER BY orderID DESC LIMIT 1").get(0).get(0)) + 1;
       } catch (Exception e) {
          System.out.println("Error getting previous orderID from FoodOrder table: " + e.getMessage());
          return;
       }
       try {
          esql.executeUpdate("INSERT INTO FoodOrder (orderID, login, storeID, totalPrice, orderTimestamp, orderStatus) VALUES ('" + orderID + "', '" + authorizedUser + "', '" + storeIDInput + "', '" + totalPriceInCents/100.0 + "', 'NOW()', 'incomplete');");
       } catch (Exception e) {
          System.out.println("Error pushing order to FoodOrder table: " + e.getMessage());
          return;
       }
 
       // add order to ItemsInOrder table
       for (Map.Entry<String, Integer> entry : orderMap.entrySet()) {
          String itemName = entry.getKey();
          int quantity = entry.getValue();
          try {
             esql.executeUpdate("INSERT INTO ItemsInOrder (orderID, itemName, quantity) VALUES ('" + orderID + "', '" + itemName + "', '" + quantity + "');");
          } catch (SQLException e) {
             System.out.println("Error pushing " + itemName + " into ItemsInOrder table: " + e.getMessage());
             return;
          }
       }
    }//end placeOrder
 
    public static void viewAllOrders(PizzaStore esql, String authorizedUser, String role) {
      boolean canSeeAllOrders = role.trim().equals("manager") || role.trim().equals("driver");
      String restriction = canSeeAllOrders ? "" : " WHERE login='" + authorizedUser + "'";

      try {
         esql.executeQueryAndPrintResult("SELECT orderId FROM FoodOrder" + restriction + ";");
      } catch (SQLException e) {
         System.out.println("Error fetching order information: " + e.getMessage());
         return;
      }
   }

   public static void viewRecentOrders(PizzaStore esql, String authorizedUser, String role) {
      boolean canSeeAllOrders = role.trim().equals("manager") || role.trim().equals("driver");
      String restriction = canSeeAllOrders ? "" : " WHERE login='" + authorizedUser + "'";

      try {
         esql.executeQueryAndPrintResult("SELECT orderId FROM FoodOrder" + restriction + " ORDER BY orderTimestamp DESC LIMIT 5;");
      } catch (SQLException e) {
         System.out.println("Error fetching order information: " + e.getMessage());
         return;
      }
   }

   public static void viewOrderInfo(PizzaStore esql, String authorizedUser, String role) {
      boolean canSeeAllOrders = role.trim().equals("manager") || role.trim().equals("driver");
      List<List<String>> orderIDs = new ArrayList<>();
      String orderId;
      boolean orderExists = false;

      // get all orderIDs
      try {
         orderIDs = esql.executeQueryAndReturnResult("SELECT orderID FROM FoodOrder");
      } catch (SQLException e) {
         System.out.println("Error fetching orderIDs: " + e.getMessage());
         return;
      }

      // get order ID input and check it it exists
      do {
         orderId = String.valueOf(getIntInput("Please enter the Order ID: "));
         for (List<String> row : orderIDs) {
            if (row.get(0).trim().equals(orderId)) {
               orderExists = true;
               break;
            }
         }
         if (!orderExists) {
            System.out.println("Order ID " + orderId + " does not exist!");
            continue;
         } else {
            break;
         }
      } while (true);


      if (!canSeeAllOrders) {
         try {
            int count = esql.executeQuery("SELECT * FROM FoodOrder WHERE orderId=" + orderId + " AND login='" + authorizedUser + "';");
            System.out.println(count);
            if (count == 0) {
               System.out.println("You do not have access to this order.");
               return;
            }
         } catch (SQLException e) {
            System.out.println("Error checking order ownership: " + e.getMessage());
            return;
         }
      }

      try {
         esql.executeQueryAndPrintResult("SELECT orderId AS \"Order ID\", orderStatus AS \"Status\", orderTimestamp AS \"Order Timestamp\" FROM FoodOrder WHERE orderId=" + orderId + ";");
         esql.executeQueryAndPrintResult("SELECT itemName AS \"Order Items\", quantity AS \"Quantity\" FROM ItemsInOrder WHERE orderId=" + orderId + ";");
      } catch (SQLException e) {
         System.out.println("Error fetching order information: " + e.getMessage());
         return;
      }
   }


    public static void viewStores(PizzaStore esql) {
       // display all stores
       try {
          esql.executeQueryAndPrintResult("SELECT * FROM Store");
       } catch (SQLException e) {
          System.out.println("Error fetching store information: " + e.getMessage());
          return;
       }
    }

    public static void updateOrderStatus(PizzaStore esql) {
      String orderIDInput;
      String statusInput = "";
      boolean orderExists = false;
      List<List<String>> orderIDs = new ArrayList<>();

      // get all orderIDs
      try {
         orderIDs = esql.executeQueryAndReturnResult("SELECT orderID FROM FoodOrder");
      } catch (SQLException e) {
         System.out.println("Error fetching orderIDs: " + e.getMessage());
         return;
      }

      // get order ID input and check it it exists
      do {
         orderIDInput = String.valueOf(getIntInput("Please enter the Order ID for the order you want to update: "));
         for (List<String> row : orderIDs) {
            if (row.get(0).trim().equals(orderIDInput)) {
               orderExists = true;
               break;
            }
         }
         if (!orderExists) {
            System.out.println("Order ID " + orderIDInput + " does not exist!");
            continue;
         } else {
            break;
         }

      } while (true);

      // get order status and change order status
      statusInput = getStringInput("Please enter the status you want to change the order to: ");
      try {
         esql.executeUpdate("UPDATE FoodOrder SET orderStatus ='" + statusInput + "' WHERE orderID=" + orderIDInput + ";");
      } catch (SQLException e) {
         System.out.println("Error updating order status: " + e.getMessage());
         return;
      }
    }

    public static void updateMenu(PizzaStore esql) {}
    public static void updateUser(PizzaStore esql) {}
 
 
 }//end PizzaStore
 
 