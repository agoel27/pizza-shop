# Bear Bites

## Running the Project

1. **Unzip the file**: Extract the project files to your desired directory.
2. **Set up the PostgreSQL database**:
   - Create and start your PostgreSQL database as `<netID>_project_3_DB`.
3. **Prepare the data**:
   - Replace the paths in `load_data.sql` with your `data/` directory path.
4. **Change to your project directory**:
   - Navigate to the project root directory in your terminal.
5. **Initialize the database**:
   - Run the following command to create and populate the database:
     ```bash
     source sql/scripts/create_db.sh
     ```
6. **Compile and run the Java program**:
   - Compile the Java code by running:
     ```bash
     source java/scripts/compile.sh
     ```
   - Run the program using:
     ```bash
     java -classpath java/bin PizzaStore <dbname> <port> <user>
     ```
     Replace `<dbname>`, `<port>`, and `<user>` with your database name, port, and username, respectively.

---

## Implementation Description

The **Bear Bites System** is a Java console application that interacts with a PostgreSQL database to provide functionality for customers, managers, and drivers. Key features include:

- **User Roles**:
  - **Customers**: Can create accounts, log in, view and update their profiles, browse the menu, place orders, and view order history.
  - **Managers**: Can update the menu, manage users, and view all orders.
  - **Drivers**: Can update order statuses.

- **Database Interaction**:
  - The system uses JDBC to connect to the PostgreSQL database.
  - SQL queries are executed to fetch and modify data, ensuring efficient and secure database operations.

- **Modular Design**:
  - The code is organized into modular methods (e.g., `viewMenu`, `placeOrder`, `updateOrderStatus`) for clean and maintainable structure.
  - A text-based user interface provides an intuitive and interactive experience.

---

## Dependencies

- **PostgreSQL**: Ensure PostgreSQL is installed and running on your system.
- **Java Development Kit (JDK)**: Ensure JDK is installed to compile and run the Java code.
- **PostgreSQL JDBC Driver**: Included in the project for database connectivity.

---

## File Structure

- **sql/**: Contains SQL scripts for database setup and data loading.
  - `create_db.sh`: Script to create and populate the database.
  - `load_data.sql`: SQL script to load data into the database.
- **java/**: Contains Java source code and scripts.
  - `scripts/`: Java source files.
  - `classes/`: Compiled Java classes.
  - `scripts/compile.sh`: Script to compile the Java code.
- **data/**: Contains CSV files for initial data loading.
