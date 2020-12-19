import java.io.FileInputStream;
import java.sql.*;
import java.util.Properties;

/**
 * Manage connection to database and perform SQL statements.
 */
public class BankingSystem {
	// Connection properties
	private static String driver;
	private static String url;
	private static String username;
	private static String password;
	
	// JDBC Objects
	private static Connection con;
	private static Statement stmt;
	private static ResultSet rs;

	static boolean withdrawSuccess;
	static boolean depositSuccess;

	/**
	 * Initialize database connection given properties file.
	 * @param filename name of properties file
	 */
	public static void init(String filename) {
		try {
			Properties props = new Properties();						// Create a new Properties object
			FileInputStream input = new FileInputStream(filename);	// Create a new FileInputStream object using our filename parameter
			props.load(input);										// Load the file contents into the Properties object
			driver = props.getProperty("jdbc.driver");				// Load the driver
			url = props.getProperty("jdbc.url");						// Load the url
			username = props.getProperty("jdbc.username");			// Load the username
			password = props.getProperty("jdbc.password");			// Load the password
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Test database connection.
	 */
	public static void testConnection() {
		System.out.println(":: TEST - CONNECTING TO DATABASE");
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			con.close();
			System.out.println(":: TEST - SUCCESSFULLY CONNECTED TO DATABASE");
			} catch (Exception e) {
				System.out.println(":: TEST - FAILED CONNECTED TO DATABASE");
				e.printStackTrace();
			}
	  }

	/**
	 * Create a new customer.
	 * @param name customer name
	 * @param gender customer gender
	 * @param age customer age
	 * @param pin customer pin
	 */
	public static void newCustomer(String name, String gender, String age, String pin)
	{
		System.out.println(":: CREATE NEW CUSTOMER - RUNNING");
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			if((!gender.equals("M")) && (!gender.equals("F")))
				System.out.println(":: CREATE NEW CUSTOMER - ERROR - INVALID GENDER");
			else if(!(age.matches("[0-9]+")))
				System.out.println(":: CREATE NEW CUSTOMER - ERROR - INVALID AGE");
			else if(Integer.valueOf(age) <= 0)
				System.out.println(":: CREATE NEW CUSTOMER - ERROR - INVALID AGE");
			else if(!(pin.matches("[0-9]+")))
				System.out.println(":: CREATE NEW CUSTOMER - ERROR - INVALID PIN");
			else if(Integer.valueOf(pin) < 0)
				System.out.println(":: CREATE NEW CUSTOMER - ERROR - INVALID PIN");
			else {
				String query = "INSERT INTO P1.CUSTOMER(NAME, GENDER, AGE, PIN) VALUES (?, ?, ?, ?)";
				PreparedStatement prepStmt = con.prepareStatement(query);
				prepStmt.setString(1, name);
				prepStmt.setString(2, gender);
				prepStmt.setString(3, age);
				prepStmt.setString(4, pin);
				prepStmt.executeUpdate();
				System.out.println(":: CREATE NEW CUSTOMER - SUCCESS");
				prepStmt.close();
			}

			con.close();

		} catch (Exception e) {
			System.out.println(":: CREATE NEW CUSTOMER - FAILURE");
			e.printStackTrace();
		}

	}

	/**
	 * Open a new account.
	 * @param id customer id
	 * @param type type of account
	 * @param amount initial deposit amount
	 */
	public static void openAccount(String id, String type, String amount)
	{
		System.out.println(":: OPEN ACCOUNT - RUNNING");
		try{
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			String query = "SELECT COUNT(*) FROM P1.CUSTOMER WHERE ID = ?";
			PreparedStatement prepStmt = con.prepareStatement(query);
			prepStmt.setInt(1, Integer.valueOf(id));
			ResultSet rs = prepStmt.executeQuery();
			rs.next();
			int numCustomers = rs.getInt(1);

			if(numCustomers == 0)
				System.out.println(":: OPEN ACCOUNT - ERROR - INVALID CUSTOMER ID");
			else if(!(amount.matches("[0-9]+")))
				System.out.println(":: OPEN ACCOUNT - ERROR - INVALID BALANCE");
			else if(Integer.valueOf(amount) < 0)
				System.out.println(":: OPEN ACCOUNT - ERROR - INVALID BALANCE");
			else if((!type.equals("C")) && (!type.equals("S")))
				System.out.println(":: OPEN ACCOUNT - ERROR - INVALID ACCOUNT TYPE");
			else
			{
				query = "INSERT INTO P1.ACCOUNT(ID, BALANCE, TYPE, STATUS) VALUES(?, ?, ?, ?)";
				prepStmt = con.prepareStatement(query);
				prepStmt.setInt(1, Integer.valueOf(id));
				prepStmt.setInt(2, Integer.valueOf(amount));
				prepStmt.setString(3, type);
				prepStmt.setString(4, "A");
				prepStmt.executeUpdate();
				System.out.println(":: OPEN ACCOUNT - SUCCESS");
			}
			rs.close();
			prepStmt.close();
			con.close();

		}
		catch (Exception e)
		{
			System.out.println(":: OPEN ACCOUNT - FAILURE");
			e.printStackTrace();
		}

	}

	/**
	 * Close an account.
	 * @param accNum account number
	 */
	public static void closeAccount(String accNum)
	{
		System.out.println(":: CLOSE ACCOUNT - RUNNING");
		try{
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			String query = "SELECT COUNT(*) FROM P1.ACCOUNT WHERE NUMBER = ? AND STATUS = ?";
			PreparedStatement prepStmt = con.prepareStatement(query);
			prepStmt.setInt(1, Integer.valueOf(accNum));
			prepStmt.setString(2, "A");
			ResultSet rs = prepStmt.executeQuery();
			rs.next();
			int numAccounts = rs.getInt(1);

			if(numAccounts == 0)
				System.out.println(":: CLOSE ACCOUNT - ERROR - INVALID ACCOUNT NUMBER");
			else
			{
				query = "UPDATE P1.ACCOUNT SET STATUS = ?, BALANCE = 0" +
						" WHERE NUMBER = ?";
				prepStmt = con.prepareStatement(query);
				prepStmt.setString(1, "I");
				prepStmt.setInt(2, Integer.valueOf(accNum));
				prepStmt.executeUpdate();
				System.out.println(":: CLOSE ACCOUNT - SUCCESS");
			}
			rs.close();
			prepStmt.close();
			con.close();

		}
		catch (Exception e)
		{
			System.out.println(":: CLOSE ACCOUNT - FAILURE");
			e.printStackTrace();
		}

	}

	/**
	 * Deposit into an account.
	 * @param accNum account number
	 * @param amount deposit amount
	 */
	public static void deposit(String accNum, String amount)
	{
		System.out.println(":: DEPOSIT - RUNNING");
		depositSuccess = false;
		try{
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			String query = "SELECT COUNT(*) FROM P1.ACCOUNT WHERE NUMBER = ? AND STATUS = ?";
			PreparedStatement prepStmt = con.prepareStatement(query);
			prepStmt.setInt(1, Integer.valueOf(accNum));
			prepStmt.setString(2, "A");
			ResultSet rs = prepStmt.executeQuery();
			rs.next();
			int numAccounts = rs.getInt(1);

			if(numAccounts == 0)
				System.out.println(":: DEPOSIT - ERROR - INVALID ACCOUNT NUMBER");
			else if(!(amount.matches("[0-9]+")))
				System.out.println(":: DEPOSIT - ERROR - INVALID AMOUNT");
			else if(Integer.valueOf(amount) < 0)
				System.out.println(":: DEPOSIT - ERROR - INVALID AMOUNT");
			else
			{
				query = "UPDATE P1.ACCOUNT SET BALANCE = BALANCE + ?" +
						" WHERE NUMBER = ?";
				prepStmt = con.prepareStatement(query);
				prepStmt.setInt(1, Integer.valueOf(amount));
				prepStmt.setInt(2, Integer.valueOf(accNum));
				prepStmt.executeUpdate();
				System.out.println(":: DEPOSIT - SUCCESS");
				depositSuccess = true;
			}
			rs.close();
			prepStmt.close();
			con.close();

		}
		catch(Exception e)
		{
			System.out.println(":: DEPOSIT - FAILURE");
			e.printStackTrace();
		}

	}

	/**
	 * Withdraw from an account.
	 * @param accNum account number
	 * @param amount withdraw amount
	 */
	public static void withdraw(String accNum, String amount)
	{
		System.out.println(":: WITHDRAW - RUNNING");
		withdrawSuccess = false;
		try{
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			String query = "SELECT COUNT(*) FROM P1.ACCOUNT WHERE NUMBER = ? AND STATUS = ?";
			PreparedStatement prepStmt = con.prepareStatement(query);
			prepStmt.setInt(1, Integer.valueOf(accNum));
			prepStmt.setString(2, "A");
			ResultSet rs = prepStmt.executeQuery();
			rs.next();
			int numAccounts = rs.getInt(1);
			if(numAccounts == 0)
				System.out.println(":: WITHDRAW - ERROR - INVALID ACCOUNT NUMBER");
			else if(!(amount.matches("[0-9]+")))
				System.out.println(":: WITHDRAW - ERROR - INVALID AMOUNT");
			else if(Integer.valueOf(amount) < 0)
				System.out.println(":: WITHDRAW - ERROR - INVALID AMOUNT");
            else
			{
				query = "SELECT (BALANCE - ?) FROM P1.ACCOUNT WHERE NUMBER = ?";
				prepStmt = con.prepareStatement(query);
				prepStmt.setInt(1, Integer.valueOf(amount));
				prepStmt.setInt(2, Integer.valueOf(accNum));
				rs = prepStmt.executeQuery();
				rs.next();

				int remFunds = rs.getInt(1);
				if(remFunds < 0)
					System.out.println(":: WITHDRAW - ERROR - NOT ENOUGH FUNDS");
				else
				{
					query = "UPDATE P1.ACCOUNT SET BALANCE = BALANCE - ?" +
							" WHERE NUMBER = ?";
					prepStmt = con.prepareStatement(query);
					prepStmt.setInt(1, Integer.valueOf(amount));
					prepStmt.setInt(2, Integer.valueOf(accNum));
					prepStmt.executeUpdate();
					System.out.println(":: WITHDRAW - SUCCESS");
					withdrawSuccess = true;
				}

			}
			rs.close();
            prepStmt.close();
            con.close();


		}
		catch (Exception e)
		{
			System.out.println(":: WITHDRAW - FAILURE");
			e.printStackTrace();
		}

	}

	/**
	 * Transfer amount from source account to destination account.
	 * @param srcAccNum source account number
	 * @param destAccNum destination account number
	 * @param amount transfer amount
	 */
	public static void transfer(String srcAccNum, String destAccNum, String amount)
	{
		System.out.println(":: TRANSFER - RUNNING");
		try{
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			withdraw(srcAccNum, amount);
			if(withdrawSuccess)
			{
				deposit(destAccNum, amount);
				if(!depositSuccess)
					deposit(srcAccNum, amount);
				else
					System.out.println(":: TRANSFER - SUCCESS");
			}
			else
				System.out.println(":: TRANSFER - ERROR - NOT ENOUGH FUNDS");
		}
		catch (Exception e)
		{
			System.out.println(":: TRANSFER - FAILURE");
			e.printStackTrace();
		}

	}

	/**
	 * Display account summary.
	 * @param cusID customer ID
	 */
	public static void accountSummary(String cusID)
	{
		System.out.println(":: ACCOUNT SUMMARY - RUNNING");
		try{
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			String query = "SELECT COUNT(*) FROM P1.CUSTOMER WHERE ID = ?";
			PreparedStatement prepStmt = con.prepareStatement(query);
			prepStmt.setInt(1, Integer.valueOf(cusID));
			ResultSet rs = prepStmt.executeQuery();
			rs.next();
			int numCustomers = rs.getInt(1);
			if(numCustomers == 0)
				System.out.println(":: ACCOUNT SUMMARY - ERROR - INVALID CUSTOMER ID");
			else
			{
				query = "SELECT NUMBER, BALANCE FROM P1.ACCOUNT WHERE ID = ? AND STATUS = ?";
				prepStmt = con.prepareStatement(query);
				prepStmt.setInt(1, Integer.valueOf(cusID));
				prepStmt.setString(2, "A");
				rs = prepStmt.executeQuery();
				System.out.println("NUMBER          BALANCE");
				System.out.println("------          -------");
				while (rs.next())
				{
					int number = rs.getInt(1);
					int balance = rs.getInt(2);
					System.out.println(number + "                " + balance);


				}
				query = "SELECT SUM(BALANCE) FROM P1.ACCOUNT WHERE ID = ? AND STATUS = ?";
				prepStmt = con.prepareStatement(query);
				prepStmt.setInt(1, Integer.valueOf(cusID));
				prepStmt.setString(2, "A");

				rs = prepStmt.executeQuery();
				rs.next();
				int total = rs.getInt(1);

				System.out.println("-----------------------");
				System.out.println("TOTAL" + "            " + total);
				System.out.println(":: ACCOUNT SUMMARY - SUCCESS");
			}

			rs.close();
			prepStmt.close();
			con.close();

		}
		catch(Exception e)
		{
			System.out.println(":: ACCOUNT SUMMARY - FAILURE");
			e.printStackTrace();
		}

	}

	/**
	 * Display Report A - Customer Information with Total Balance in Decreasing Order.
	 */
	public static void reportA()
	{
		System.out.println(":: REPORT A - RUNNING");
		try{
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			String query = "SELECT T1.ID, T1.NAME, T1.GENDER, T1.AGE, T2.TOTAL FROM P1.CUSTOMER AS T1, " +
					"(SELECT C.ID, SUM(BALANCE) AS TOTAL FROM P1.CUSTOMER C, P1.ACCOUNT A WHERE C.ID = A.ID GROUP BY C.ID) AS T2 " +
					"WHERE T1.ID = T2.ID ORDER BY T2.TOTAL DESC";
			PreparedStatement prepStmt = con.prepareStatement(query);
			ResultSet rs = prepStmt.executeQuery();
			System.out.println("ID" + "          " + "NAME" + "            " + "GENDER" + " " + "AGE" + "         " + "TOTAL");
			System.out.println("----------- --------------- ------ ----------- -----------");
			while(rs.next())
			{
				int id = rs.getInt(1);
				String name = rs.getString(2);
				String gender = rs.getString(3);
				int age = rs.getInt(4);
				int total = rs.getInt(5);
				System.out.println(id + "            " + name + "          " + gender + "       " + age + "            " + total);

			}
			System.out.println(":: REPORT A - SUCCESS");

			rs.close();
			prepStmt.close();
			con.close();
		}
		catch(Exception e)
		{
			System.out.println(":: REPORT A - FAILURE");
			e.printStackTrace();
		}


	}

	/**
	 * Display Report B - Customer Information with Total Balance in Decreasing Order.
	 * @param min minimum age
	 * @param max maximum age
	 */
	public static void reportB(String min, String max) 
	{
		System.out.println(":: REPORT B - RUNNING");
		try{
			Class.forName(driver);
			con = DriverManager.getConnection(url, username, password);
			if(!(min.matches("[0-9]+")) || !(max.matches("[0-9]+")))
				System.out.println(":: REPORT B - ERROR - INVALID AGE(S)");
			else if((Integer.valueOf(min) <= 0) || (Integer.valueOf(max) <= 0))
				System.out.println(":: REPORT B - ERROR - INVALID AGE(S)");
			else if(Integer.valueOf(min) > Integer.valueOf(max))
				System.out.println(":: REPORT B - ERROR - INVALID AGE(S)");
			else
			{
				String query = "SELECT AVG(T3.TOTAL) FROM (SELECT T2.TOTAL FROM P1.CUSTOMER AS T1, " +
						"(SELECT C.ID, SUM(BALANCE) AS TOTAL FROM P1.CUSTOMER C, P1.ACCOUNT A WHERE C.ID = A.ID GROUP BY C.ID) AS T2 " +
						"WHERE T1.ID = T2.ID AND T1.AGE BETWEEN ? AND ? ORDER BY T2.TOTAL DESC) AS T3";
				PreparedStatement prepStmt = con.prepareStatement(query);
				prepStmt.setInt(1, Integer.valueOf(min));
				prepStmt.setInt(2, Integer.valueOf(max));
				ResultSet rs = prepStmt.executeQuery();
				rs.next();
				System.out.println("AVERAGE");
				System.out.println("---------");
				System.out.println(rs.getInt(1));
				System.out.println(":: REPORT B - SUCCESS");
				rs.close();
				prepStmt.close();
			}



			con.close();
		}
		catch (Exception e)
		{
			System.out.println(":: REPORT B - FAILURE");
			e.printStackTrace();
		}

	}
}
