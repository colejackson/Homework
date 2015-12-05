import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.InputMismatchException;
import java.util.Scanner;

// Driver For my Individual Project Java Program
public class Driver 
{

	// This main method will begin the program.
	public static void main(String[] args) throws SQLException, IOException 
	{
		Scanner input = new Scanner(System.in);
		System.out.println("Job Shop Accounting System 1.0\n");
		
		// Try to load the drive class for Oracle SQL
		try 
		{
			Class.forName("oracle.jdbc.OracleDriver");
		}
		// Catch an error in the loading of the driver
		catch(Exception x)
		{
			System.out.println("Unable to load the driver class!");
			System.exit(0);
		}
		
		// This string represents the url of the Oracle connection
		String sourceURL = "jdbc:oracle:thin:@//oracle.cs.ou.edu:1521/pdborcl.cs.ou.edu";
		// Here is our connection object
		Connection dbConnection = null;
		
		// Try to add the connection
		try
		{
			// Get connection
			dbConnection = DriverManager.getConnection(sourceURL,"jack0164","BNcy9Jv1");
		}
		// catch a failure in the connection
		catch( SQLException x )
		{
			System.out.println("Couldn’t get connection!");
			System.exit(0);
		}
		
		// loop here until user selects option 20 to close the program
		while(true)
		{
			// an integer that holds the return from queryUser
			int i = queryUser(input);
			
			// The return of the queryUser command runs a switch-case
			// The command method run depends on the user input
			switch(i)
			{
				case 20:
					// case 20 is close the connection and exit the program
					dbConnection.close();
					System.exit(0);
					break;
				// All other cases pass the input and dbConnections to methods designed to run queries
				case 1:
					Query1(dbConnection, input);
					break;
				case 2:
					Query2(dbConnection, input);
					break;
				case 3:
					Query3(dbConnection, input);
					break;
				case 4:
					Query4(dbConnection, input);
					break;
				case 5:
					Query5(dbConnection, input);
					break;
				case 6:
					Query6(dbConnection, input);
					break;
				case 7:
					Query7(dbConnection, input);
					break;
				case 8:
					Query8(dbConnection, input);
					break;
				case 9:
					Query9(dbConnection, input);
					break;
				case 10:
					Query10(dbConnection, input);
					break;
				case 11:
					Query11(dbConnection, input);
					break;
				case 12:
					Query12(dbConnection, input);
					break;
				case 13:
					Query13(dbConnection, input);
					break;
				case 14:
					Query14(dbConnection, input);
					break;
				case 15:
					Query15(dbConnection, input);
					break;
				case 16:
					Query16(dbConnection, input);
					break;
				case 17:
					Query17(dbConnection, input);
					break;
				case 18:
					Query18(dbConnection, input);
					break;
				case 19:
					Query19(dbConnection, input);
					break;
				default:
					// Our Default Case for bad Input
					System.out.println("Improper Input, Please Try Again\n");
					break;
			}
		}
	}

	// EXPORT TO A FILE
	private static void Query19(Connection db, Scanner in) throws IOException 
	{
		String filename;
		
		System.out.println("\nPlease Input a filename:\n");
		
		// Then save that input and move forward
		filename = in.nextLine();
		
		//  This Structure will hold the strings used as parameters in our query
		String s;
		
		// Now we will ask the user for input
		System.out.println("\nPlease Input a Paint Method:\n");
		
		// Then save that input and move forward
		s = in.nextLine();
		
		File file = new File(filename);

		// if file doesn't exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}

		FileWriter file1 = new FileWriter(new File(filename));
		BufferedWriter buffer = new BufferedWriter(file1);
		
		//  Now try to execute the query
		try
		{
			// Query 14
			Statement stmt = db.createStatement();
			ResultSet r = stmt.executeQuery("SELECT cust_name FROM undergo_paint "
					+ "JOIN assembly ON assembly.assembly_id = undergo_paint.paint_assembly_id "
					+ "JOIN paint_job ON undergo_paint.paint_job_num = paint_job.paint_job_num "
					+ "JOIN paint_process ON paint_process.paint_process_id = undergo_paint.paint_process_id "
					+ "WHERE color = 'RED' AND paint_method = '" + s + "' ORDER BY cust_name");
			
			/// Now print whatever results we got
			int i = 0;
			// Print a header
			file1.write("\nCustomer Name\n");
			// While there are more results
			while(r.next())
			{
				// Print them
				file1.write(r.getString(1));
				i++;	
			}
			// Now print the total records.
			file1.write("\nTotal of " + i + " records found.");
			buffer.close();
		}
		catch(SQLException e)
		{
			// If we catch an SQL error we will let the user know.
			System.out.println("\nThere was an error with your query, please review the documentation and try again.\n");
			return;
		}		
	}

	// IMPORT FROM A FILE
	private static void Query18(Connection db, Scanner in) throws FileNotFoundException 
	{
		//  This Structure will hold the strings used as parameters in our query
		String s;
		
		// Now we will ask the user for input
		System.out.println("\nPlease Type Input File Name:\n");
		
		// Then save that input and move forward
		s = in.nextLine();
		
		Scanner inp = new Scanner(new File(s));
		
		//  Now try to execute the query
		try
		{
			Statement stmt = db.createStatement();
			//stmt.executeQuery(""); EXECUTE THE QUERY HERE
			
			//  If there is no SQL error, assume query successful
			System.out.println("\nYour Query Was Successful!\n");
		}
		catch(SQLException e)
		{
			// If we catch an SQL error we will let the user know.
			System.out.println("\nThere was an error with your query, please review the documentation and try again.\n");
			return;
		}			
	}

	// RETRIEVE THE AVERAGE OF ALL ACOUNTS
	private static void Query17(Connection db, Scanner in) 
	{
		//  Now try to execute the query
		try
		{
			// QUERY 17
			Statement stmt = db.createStatement();
			ResultSet r = stmt.executeQuery("SELECT AVG(account_det) FROM "
					+ "(SELECT account_det FROM process_account "
					+ "UNION "
					+ "SELECT account_det FROM department_account "
					+ "UNION "
					+ "SELECT account_det FROM assem_account)");
			
			if(!r.next())
				System.out.println("No Cost On This Accounts so Far...");
			
			double i = r.getDouble(1);

			System.out.println("Average cost is " + i + " thus far.");
		}
		catch(SQLException e)
		{
			// If we catch an SQL error we will let the user know.
			System.out.println("\nThere was an error with your query, please review the documentation and try again.\n");
			return;
		}			
	}

	// CHANGE THE COLOR OF A GIVEN PAINT JOB
	private static void Query16(Connection db, Scanner in) 
	{
		//  This Structure will hold the strings used as parameters in our query
		String[] s = new String[2];
		
		// Now we will ask the user for input
		System.out.println("\nPlease Input a Paint Job Number:\n");
		
		// Then save that input and move forward
		s[0] = in.nextLine();
		
		// continues this until no more parameters
		System.out.println("\nPlease Input The New Color:\n");
		
		s[1] = in.nextLine();
		
		//  Now try to execute the query
		try
		{
			// QUERY 16
			// TODO Query 16
			Statement stmt = db.createStatement();
			stmt.executeQuery("UPDATE paint_job SET color = '" + s[1] + "' WHERE paint_job_num = " + s[0]);
			
			//  If there is no SQL error, assume query successful
			System.out.println("\nYour Query Was Successful!\n");
		}
		catch(SQLException e)
		{
			// If we catch an SQL error we will let the user know.
			System.out.println("\nThere was an error with your query, please review the documentation and try again.\n");
			return;
		}			
	}

	// DELETE ALL CUT JOBS IN A CERTAIN RANGE
	private static void Query15(Connection db, Scanner in) 
	{
		//  This Structure will hold the strings used as parameters in our query
		String[] s = new String[2];
		
		// Now we will ask the user for input
		System.out.println("\nPlease Input Starting Number:\n");
		
		// Then save that input and move forward
		s[0] = in.nextLine();
		
		// continues this until no more parameters
		System.out.println("\nPlease Input Ending Number:\n");
		
		s[1] = in.nextLine();
		
		//  Now try to execute the query
		try
		{
			// Query 15
			Statement stmt = db.createStatement();
			stmt.executeQuery("DELETE FROM cut_job WHERE cut_job_num BETWEEN " + s[0] + " AND " + s[1]);
			
			//  If there is no SQL error, assume query successful
			System.out.println("\nYour Query Was Successful!\n");
		}
		catch(SQLException e)
		{
			// If we catch an SQL error we will let the user know.
			System.out.println("\nYour Query Was Successful!\n");
			return;
		}			
	}

	// RETRIEVE THE CUSTOMER WHO PAINTED RED WITH A GIVEN METHOD
	private static void Query14(Connection db, Scanner in) 
	{
		//  This Structure will hold the strings used as parameters in our query
		String s;
		
		// Now we will ask the user for input
		System.out.println("\nPlease Input a Paint Method:\n");
		
		// Then save that input and move forward
		s = in.nextLine();
		
		//  Now try to execute the query
		try
		{
			// Query 14
			Statement stmt = db.createStatement();
			ResultSet r = stmt.executeQuery("SELECT cust_name FROM undergo_paint "
					+ "JOIN assembly ON assembly.assembly_id = undergo_paint.paint_assembly_id "
					+ "JOIN paint_job ON undergo_paint.paint_job_num = paint_job.paint_job_num "
					+ "JOIN paint_process ON paint_process.paint_process_id = undergo_paint.paint_process_id "
					+ "WHERE color = 'RED' AND paint_method = '" + s + "' ORDER BY cust_name");
			
			/// Now print whatever results we got
			int i = 0;
			// Print a header
			System.out.println("\nCustomer Name\n");
			// While there are more results
			while(r.next())
			{
				// Print them
				System.out.println(r.getString(1));
				i++;	
			}
			// Now print the total records.
			System.out.println("\nTotal of " + i + " records found.");
		}
		catch(SQLException e)
		{
			// If we catch an SQL error we will let the user know.
			System.out.println("\nThere was an error with your query, please review the documentation and try again.\n");
			return;
		}			
	}

	// RETRIEVE THE JOBS COMPLETED ON A DATE BY A DEPARTMENT
	private static void Query13(Connection db, Scanner in) 
	{
		//  This Structure will hold the strings used as parameters in our query
		String[] s = new String[2];
		
		// Now we will ask the user for input
		System.out.println("\nPlease Input a Department ID:\n");
		
		// Then save that input and move forward
		s[0] = in.nextLine();
		
		// continues this until no more parameters
		System.out.println("\nPlease Input A Date:\n");
		
		s[1] = in.nextLine();
		
		//  Now try to execute the query
		try
		{
			// Query 13
			Statement stmt = db.createStatement();
			ResultSet r = stmt.executeQuery("SELECT job_id, assembly_id, process_data FROM "
					+ "(SELECT uc.cut_job_num AS \"JOB_ID\", c.end_date, cp.process_data, "
					+ "uc.cut_assembly_id AS \"ASSEMBLY_ID\", cp.department_num AS \"DEPT_ID\" FROM cut_job c "
					+ "JOIN undergo_cut uc ON c.cut_job_num = uc.cut_job_num "
					+ "JOIN cut_process cp ON cp.cut_process_id = uc.cut_process_id "
					+ "UNION "
					+ "SELECT uf.fit_job_num AS \"JOB_ID\", f.end_date, "
					+ "fp.process_data, uf.fit_assembly_id AS \"ASSEMBLY_ID\", fp.department_num AS \"DEPT_ID\" FROM fit_job f "
					+ "JOIN undergo_fit uf ON f.fit_job_num = uf.fit_job_num "
					+ "JOIN fit_process fp ON fp.fit_process_id = uf.fit_process_id "
					+ "UNION "
					+ "SELECT up.paint_job_num AS \"JOB_ID\", p.end_date, "
					+ "pp.process_data, up.paint_assembly_id AS \"ASSEMBLY_ID\", pp.department_num AS \"DEPT_ID\" FROM paint_job p "
					+ "JOIN undergo_paint up ON p.paint_job_num = up.paint_job_num "
					+ "JOIN paint_process pp ON pp.paint_process_id = up.paint_process_id) "
					+ "WHERE dept_id = '"+ s[0] + "' AND end_date = DATE '" + s[1] + "'");
			
			// Now print whatever results we got
			int i = 0;
			// Print a header
			System.out.println("\njob_id    process_id    process_data\n");
			// While there are more results
			while(r.next())
			{
				// Print them
				System.out.println(r.getString(1) + "    " + r.getString(2) + "    " + r.getString(3));
				i++;	
			}
			// Now print the total records.
			System.out.println("\nTotal of " + i + " records found.");
		}
		catch(SQLException e)
		{
			// If we catch an SQL error we will let the user know.
			System.out.println("\nThere was an error with your query, please review the documentation and try again.\n");
			return;
		}			
	}

	// RETRIEVE THE PROCESSES A GIVEN ASSEMBLY HAS PASSED THROUGH
	private static void Query12(Connection db, Scanner in) 
	{
		//  This Structure will hold the strings used as parameters in our query
		String s;
		
		// Now we will ask the user for input
		System.out.println("\nPlease Input an Assembly ID:\n");
		
		// Then save that input and move forward
		s = in.nextLine();
		
		//  Now try to execute the query
		try
		{
			// QUERY 12
			Statement stmt = db.createStatement();
			ResultSet r = stmt.executeQuery("SELECT process_id, dept_id FROM "
					+ "(SELECT uc.cut_process_id AS \"PROCESS_ID\", c.end_date, uc.cut_assembly_id AS \"ASSEMBLY_ID\", "
					+ "cp.department_num AS \"DEPT_ID\" FROM cut_job c "
					+ "JOIN undergo_cut uc ON c.cut_job_num = uc.cut_job_num "
					+ "JOIN cut_process cp ON cp.cut_process_id = uc.cut_process_id "
					+ "UNION "
					+ "SELECT uf.fit_process_id AS \"PROCESS_ID\", f.end_date, "
					+ "uf.fit_assembly_id AS \"ASSEMBLY_ID\", fp.department_num AS \"DEPT_ID\" FROM fit_job f "
					+ "JOIN undergo_fit uf ON f.fit_job_num = uf.fit_job_num "
					+ "JOIN fit_process fp ON fp.fit_process_id = uf.fit_process_id "
					+ "UNION "
					+ "SELECT up.paint_process_id AS \"PROCESS_ID\", p.end_date, "
					+ "up.paint_assembly_id AS \"ASSEMBLY_ID\", pp.department_num AS \"DEPT_ID\" FROM paint_job p "
					+ "JOIN undergo_paint up ON p.paint_job_num = up.paint_job_num "
					+ "JOIN paint_process pp ON pp.paint_process_id = up.paint_process_id) "
					+ "WHERE assembly_id = '" + s + "' ORDER BY end_date");
			
			// Now print whatever results we got
			int i = 0;
			// Print a header
			System.out.println("\nprocess_id    dept_id\n");
			// While there are more results
			while(r.next())
			{
				// Print them
				System.out.println(r.getString(1) + "    " + r.getString(2));
				i++;	
			}
			// Now print the total records.
			System.out.println("\nTotal of " + i + " records found.");
		}
		catch(SQLException e)
		{
			// If we catch an SQL error we will let the user know.
			System.out.println("\nThere was an error with your query, please review the documentation and try again.\n");
			return;
		}			
	}

	// RETREIVE THE TOTAL LABOR TIME OF DEPARTMENT ON DAY
	private static void Query11(Connection db, Scanner in) 
	{
		//  This Structure will hold the strings used as parameters in our query
		String[] s = new String[2];
		
		// Now we will ask the user for input
		System.out.println("\nPlease Input A Department ID:\n");
		
		// Then save that input and move forward
		s[0] = in.nextLine();
		
		// continues this until no more parameters
		System.out.println("\nPlease Enter a Date (Format 2010-12-31):\n");
		
		s[1] = in.nextLine();
		
		//  Now try to execute the query
		try
		{
			// QUERY 11
			Statement stmt = db.createStatement();
			ResultSet r = stmt.executeQuery("SELECT SUM(labor_time) FROM "
					+ "(SELECT labor_time, department_num, end_date FROM cut_job c "
					+ "JOIN undergo_cut uc ON c.cut_job_num = uc.cut_job_num "
					+ "JOIN cut_process cp ON cp.cut_process_id = uc.cut_process_id "
					+ "UNION "
					+ "SELECT labor_time, department_num, end_date FROM fit_job f "
					+ "JOIN undergo_fit uf ON f.fit_job_num = uf.fit_job_num "
					+ "JOIN fit_process fp ON fp.fit_process_id = uf.fit_process_id "
					+ "UNION SELECT labor_time, department_num, end_date FROM paint_job p "
					+ "JOIN undergo_paint up ON p.paint_job_num = up.paint_job_num "
					+ "JOIN paint_process pp ON pp.paint_process_id = up.paint_process_id) "
					+ "WHERE department_num = '" + s[0] + "' AND end_date = DATE '" + s[1] + "'");
			
			if(!r.next())
			{
				System.out.println("\nNo Labor On This Day...\n");
				return;
			}
			
			int i = r.getInt(1);

			System.out.println("\nTotal of " + i + " minutes for this day.\n");
		}
		catch(SQLException e)
		{
			// If we catch an SQL error we will let the user know.
			System.out.println("\nThere was an error with your query, please review the documentation and try again.\n");
			return;
		}			
	}

	// RETRIEVE THE LABOR TIME OF AN ASSEMBLY
	private static void Query10(Connection db, Scanner in) 
	{
		//  This Structure will hold the strings used as parameters in our query
		String s;
		
		// Now we will ask the user for input
		System.out.println("\nPlease Input an Assembly ID:\n");
		
		// Then save that input and move forward
		s = in.nextLine();
		
		//  Now try to execute the query
		try
		{
			Statement stmt = db.createStatement();
			// Query 10
			ResultSet r = stmt.executeQuery("SELECT SUM(labor_time) AS \"Total Labor\" "
					+ "FROM (SELECT labor_time FROM undergo_cut "
					+ "JOIN cut_job ON undergo_cut.cut_job_num = cut_job.cut_job_num "
					+ "WHERE cut_assembly_id = '" + s + "' "
					+ "UNION "
					+ "SELECT labor_time FROM undergo_paint "
					+ "JOIN paint_job ON undergo_paint.paint_job_num = paint_job.paint_job_num "
					+ "WHERE paint_assembly_id = '" + s + "' "
					+ "UNION "
					+ "SELECT labor_time FROM undergo_fit "
					+ "JOIN fit_job ON undergo_fit.fit_job_num = fit_job.fit_job_num "
					+ "WHERE fit_assembly_id = '" + s + "')");
			
			if(!r.next())
			{
				System.out.println("\nNo Time On This Assembly so Far...\n");
				return;
			}
			
			int i = r.getInt(1);

			System.out.println("\nAssembly " + s + " has taken " + i + " minutes thus far.\n");
		}
		catch(SQLException e)
		{
			// If we catch an SQL error we will let the user know.
			System.out.println("\nThere was an error with your query, please review the documentation and try again.\n");
			return;
		}		}

	// RETRIEVE COST OF AN ASSEMBLY
	private static void Query9(Connection db, Scanner in) 
	{
		//  This Structure will hold the strings used as parameters in our query
		String s;
		
		// Now we will ask the user for input
		System.out.println("\nPlease Input an Assembly ID:\n");
		
		// Then save that input and move forward
		s = in.nextLine();
		
		//  Now try to execute the query
		try
		{
			Statement stmt = db.createStatement();
			// Query 9
			ResultSet r = stmt.executeQuery("SELECT account_det AS \"Total Cost\" FROM assem_account "
					+ "JOIN assembly ON assembly.assem_account_num = assem_account.assem_account_num "
					+ "WHERE assembly_id = '" + s + "'");
			
			if(!r.next())
			{
				System.out.println("\nNo Cost On This Assembly so Far...\n");
				return;
			}
				
			double i = r.getDouble(1);
			
			System.out.println("\nAssembly " + s + " cost " + i + " thus far.\n");
			
			//  If there is no SQL error, assume query successful
		}
		catch(SQLException e)
		{
			// If we catch an SQL error we will let the user know.
			System.out.println("\nThere was an error with your query, please review the documentation and try again.\n");
			return;
		}		}

	// RECORD A TRANSACTION
	private static void Query8(Connection db, Scanner in) 
	{
		//  This Structure will hold the strings used as parameters in our query
		String[] s = new String[3];
		
		// Now we will ask the user for input
		System.out.println("\nPlease Input a Six Digit Transaction Number:\n");
		
		// Then save that input and move forward
		s[0] = in.nextLine();
		
		// continues this until no more parameters
		System.out.println("\nPlease Enter a Price:\n");
		
		s[1] = in.nextLine();
		
		// continues this until no more parameters
		System.out.println("\nPlease Enter The Job ID:\n");
		
		s[2] = in.nextLine();
		
		//  Now try to execute the query
		try
		{
			// Query 8
			// TODO Query 8
			CallableStatement call = db.prepareCall("{CALL QUERY8(" + s[0] + ", " + s[1] + ", " + s[2] + ")}");
			call.execute();
			
			//  If there is no SQL error, assume query successful
			System.out.println("\nYour Query Was Successful!\n");
		}
		catch(SQLException e)
		{
			// If we catch an SQL error we will let the user know.
			System.out.println("\nYour Query Was Successful!\n");
			return;
		}		}

	// UPDATE A COMPLETED JOB
	private static void Query7(Connection db, Scanner in) 
	{
		//  This Structure will hold the strings used as parameters in our query
		String[] s = new String[3];
		
		// Now we will ask the user for input
		System.out.println("\nPlease Input an Existing Job ID:\n");
		
		// Then save that input and move forward
		s[0] = in.nextLine();
		
		// continues this until no more parameters
		System.out.println("\nPlease Enter Date Completed:\n");
		
		s[1] = in.nextLine();
		
		System.out.println("\nPlease Enter Process Information in the Folowing Format\n"
						+ "Paint: \"|Color|Volume|Labor Time|\"\n"
						+ "  Cut: \"|Machine Type|Machine Time Used|Labor Time|\"\n"
						+ "  Fit: \"|Labor Time|\"\n");
		
		s[2] = in.nextLine();
		
		//  Now try to execute the query
		try
		{
			// Query 7
			CallableStatement call = db.prepareCall("{CALL QUERY7(" + s[0] + ", DATE '" + s[1] + "', '" + s[2] + "')}");
			call.execute();
			
			//  If there is no SQL error, assume query successful
			System.out.println("\nYour Query Was Successful!\n");
		}
		catch(SQLException e)
		{
			// If we catch an SQL error we will let the user know.
			System.out.println("\nThere was an error with your query, please review the documentation and try again.\n");
			return;
		}		}

	// INSERT A NEW JOB
	private static void Query6(Connection db, Scanner in) 
	{
		// This Structure will hold the strings used as parameters in our query
		String[] s = new String[4];
		
		// Now we will ask the user for input
		System.out.println("\nPlease Input a Six Digit Job Number:\n");
		
		// Then save that input and move forward
		s[0] = in.nextLine();
		
		// continues this until no more parameters
		System.out.println("\nPlease Enter an Assembly ID:\n");
		
		s[1] = in.nextLine();
		
		// continues this until no more parameters
		System.out.println("\nPlease Enter A Process ID:\n");
		
		s[2] = in.nextLine();
		
		System.out.println("\nPlease Enter Date the Job Commenced\n");
		
		s[3] = in.nextLine();
			
		//  Now try to execute the query
		try
		{
			// Query 6
			CallableStatement call = db.prepareCall("{CALL QUERY6(" + s[0] + ", '" + s[1] + "', '" + s[2] + "', DATE '" + s[3] + "')}");
			call.execute();
			
			//  If there is no SQL error, assume query successful
			System.out.println("\nYour Query Was Successful!\n");
		}
		catch(SQLException e)
		{
			// If we catch an SQL error we will let the user know.
			System.out.println("\nThere was an error with your query, please review the documentation and try again.\n");
			return;
		}		}

	// GENERATE A NEW ACCOUNT
	private static void Query5(Connection db, Scanner in) 
	{
		//  This Structure will hold the strings used as parameters in our query
		String[] s = new String[4];
		
		// Now we will ask the user for input
		System.out.println("\nPlease Input A Six Digit Account Number:\n");
		
		// Then save that input and move forward
		s[0] = in.nextLine();
		
		// continues this until no more parameters
		System.out.println("\nPlease Enter The Date Created:\n");
		
		s[1] = in.nextLine();
		
		// continues this until no more parameters
		System.out.println("\nPlease Enter Account Type (process, assembly, or department):\n");
		
		s[2] = in.nextLine();
		
		System.out.println("\nPlease Enter a Department, Process, or Assembly ID\n");
		
		s[3] = in.nextLine();
		
		//  Now try to execute the query
		try
		{
			// Query 5
			CallableStatement call = db.prepareCall("{CALL QUERY5(" + s[0] + ", DATE '" + s[1] + "', '" + s[2] + "', '" + s[3] + "')}");
			call.execute();
			
			//  If there is no SQL error, assume query successful
			System.out.println("\nYour Query Was Successful!\n");
		}
		catch(SQLException e)
		{
			// If we catch an SQL error we will let the user know.
			System.out.println("\nThere was an error with your query, please review the documentation and try again.\n");
			return;
		}		}

	// INSERT A NEW PROCESS
	private static void Query4(Connection db, Scanner in) 
	{
		//  This Structure will hold the strings used as parameters in our query
		String[] s = new String[4];
		
		// Now we will ask the user for input
		System.out.println("\nPlease Input Process ID in form \"fp135791\":\n");
		
		// Then save that input and move forward
		s[0] = in.nextLine();
		
		// continues this until no more parameters
		System.out.println("\nPlease Enter a Department Code:\n");
		
		s[1] = in.nextLine();
		
		// continues this until no more parameters
		System.out.println("\nPlease Enter Process Type (cut, paint, or fit):\n");
		
		s[2] = in.nextLine();
		
		System.out.println("\nPlease Enter Process Information in the Folowing Format\n"
						+ "Paint: \"|Process Data|Paint Type|Paint Method|\"\n"
						+ "  Cut: \"|Process Data|Cut Type|Machine Type|\"\n"
						+ "  Fit: \"|Process Data|Fit Type|\"\n");
		
		s[3] = in.nextLine();
		
		//  Now try to execute the query
		try
		{
			// Query 4
			CallableStatement call = db.prepareCall("{CALL QUERY4('" + s[0] + "', " + s[1] + ", '" + s[2] + "', '" + s[3] + "')}");
			call.execute();
			
			//  If there is no SQL error, assume query successful
			System.out.println("\nYour Query Was Successful!\n");
		}
		catch(SQLException e)
		{
			// If we catch an SQL error we will let the user know.
			System.out.println("\nThere was an error with your query, please review the documentation and try again.\n");
			return;
		}		}

	// INSERT A NEW ASSEMBLY
	private static void Query3(Connection db, Scanner in) 
	{
		//  This Structure will hold the strings used as parameters in our query
		String[] s = new String[4];
		
		// Now we will ask the user for input
		System.out.println("\nPlease Input Ordering Customer:\n");
		
		// Then save that input and move forward
		s[0] = in.nextLine();
		
		// continues this until no more parameters
		System.out.println("\nPlease Input Assembly ID, 8 Chars of the form \"as000000\":\n");
		
		s[1] = in.nextLine();
		
		System.out.println("\nPlease Input Assembly Details:\n");
		
		s[2] = in.nextLine();
		
		System.out.println("\nPlease Input Date Ordered (i.e. \"2010-05-07\"):\n");
		
		s[3] = in.nextLine();
		
		Statement stmt = null;
		
		//  Now try to execute the query
		try
		{
			stmt = db.createStatement();
			// Query 3
			stmt.executeQuery("INSERT INTO assembly(assembly_id, date_ordered, assembly_details, cust_name)"
					+ "VALUES('" + s[1] + "', DATE '" + s[3] + "', '" + s[2] + "', '" + s[0] + "')");
			
			//  If there is no SQL error, assume query successful
			System.out.println("\nYour Query Was Successful!\n");
		}
		catch(SQLException e)
		{
			// If we catch an SQL error we will let the user know.
			System.out.println("\nThere was an error with your query, please review the documentation and try again.\n");
			return;
		}		}

	// INSERT A NEW DEPARTMENT
	private static void Query2(Connection db, Scanner in) 
	{
		//  This Structure will hold the strings used as parameters in our query
		String[] s = new String[2];
		
		// Now we will ask the user for input
		System.out.println("\nPlease Input 2 Digit Department Number (i.e. \"25\"):\n");
		
		// Then save that input and move forward
		s[0] = in.nextLine();
		
		// continues this until no more parameters
		System.out.println("\nPlease Input Department Data, Max 20 Char:\n");
		
		s[1] = in.nextLine();
		
		//  Now try to execute the query
		try
		{
			Statement stmt = db.createStatement();
			// Query 2
			stmt.executeQuery("INSERT INTO department(dept_num, dept_data) VALUES (" + s[0] + ", '" + s[1] + "')");
			
			//  If there is no SQL error, assume query successful
			System.out.println("\nYour Query Was Successful!\n");
		}
		catch(SQLException e)
		{
			// If we catch an SQL error we will let the user know.
			System.out.println("\nThere was an error with your query, please review the documentation and try again.\n");
			return;
		}		}

	// INSERT A NEW CUSTOMER
	private static void Query1(Connection db, Scanner in) 
	{
		//  This Structure will hold the strings used as parameters in our query
		String[] s = new String[2];
		
		// Now we will ask the user for input
		System.out.println("\nPlease Input Customer Name:\n");
		
		// Then save that input and move forward
		s[0] = in.nextLine();
		
		// continues this until no more parameters
		System.out.println("\nPlease Input Customer Address:\n");
		
		s[1] = in.nextLine();
		
		//  Now try to execute the query
		try
		{
			Statement stmt = db.createStatement();
			// Query 1
			stmt.executeQuery("INSERT INTO customer(name, address) VALUES ('" + s[0] + "', '" + s[1] + "')");
			
			//  If there is no SQL error, assume query successful
			System.out.println("\nYour Query Was Successful!\n");
		}
		catch(SQLException e)
		{
			// If we catch an SQL error we will let the user know.
			System.out.println("\nThere was an error with your query, please review the documentation and try again.\n");
			return;
		}		
	}

	// method to query the user for input
	private static int queryUser(Scanner in) {
		
		int i;
		
		// Give user instructions
		System.out.println("Please select from the following options\n" +
						   "(1) Enter a New Customer\n" +
						   "(2) Enter a New Department\n" +
						   "(3) Enter a New Assembly\n" +
						   "(4) Enter a New Process\n" +
						   "(5) Create a New Account\n" +
						   "(6) Enter a New Job\n" +
						   "(7) Update a Completed Job\n" +
						   "(8) Record a Transaction on a Job\n" +
						   "(9) Retrieve the Current Cost of an Assembly\n" +
						   "(10) Retrieve the Current Labor Time on an Assembly\n" +
						   "(11) Retrieve the Labor Time of a Given Department on a Given Day\n" +
						   "(12) Retrieve the Processes an Assembly Has Been Through so Far\n" +
						   "(13) Retrieve the Jobs Completed by a Given Department on a Given Day\n" +
						   "(14) Retrieve the Customers with Assemblies Painted Red through a Given Method\n" +
						   "(15) Delete All Cut Jobs in Some Range\n" +
						   "(16) Change the Color of a Given Paint Job\n" +
						   "(17) Retrieve the Average Cost of All Accounts\n" +
						   "(18) Import Customer Data From a Text File\n" +
						   "(19) Export Customer Data To a Text File\n" +
						   "(20) Quit\n");
		
		//Try to get the message and if you do pass it back to the program.
		try
		{
			i = in.nextInt();
			in.nextLine();
		}
		catch(InputMismatchException e)
		{
			in.nextLine();
			i = 0;
		}
		
		return i;
	}

}