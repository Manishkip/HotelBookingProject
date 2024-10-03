package com.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final String URL="jdbc:mysql://localhost:3306/hoteldb";
	private static final String USERNAME="root";
	private static final String PASSWORD="admin";
	
    public static void main( String[] args )
    {
        System.out.println( "Welcome to Hotel...." );
        
//        //Load Drivers
//        try {
//			Class.forName("com.mysql.jdbc.Driver");
//			
//			System.out.println("Drivers Loaded...");
//		} catch (ClassNotFoundException e) {
//			System.out.println("Failed to load the drivers : " + e.getMessage());
//		}
        
        //Connection to DB
        try {
			Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD);
			System.out.println("Connected to DB...");
			System.out.println();
			/*
			 MENU
			 1.Book a room 				- insert data of customer - save it to db
			 2.View the room 			- show details of that customer ->room
			 3.Show all Bookings 		- show all customer data
			 4. Update Booking Details 	- update data
			 5. Delete data 			- delete customer data
			 6. Exit
			 */
			boolean flag = true;
			while(flag) {
				System.out.println();
				//lets show these options to user
				System.out.println("Please select from below options : ");
				System.out.println("1. Book a room");
				System.out.println("2. View the room");
				System.out.println("3. Show all bookings");
				System.out.println("4. Update booking details");
				System.out.println("5. Delete booking");
				System.out.println("6. Exit");
				
				Scanner scanner = new Scanner(System.in);
				System.out.println("ENTER OPTION : ");
				int option = scanner.nextInt();
				System.out.println("Option selected by user : " + option);
				
				switch (option) {
					case 1:
						bookARoom(con,scanner);
						break;
					case 2:
						ViewBookedRoom(con,scanner);
						break;
					case 3:
						ShowAllBookings(con);
						break;
					case 4:
						updateDetails(con,scanner);
						break;
					case 5:
						deleteBooking(con,scanner);
						break;
					case 6:
						System.out.println("Thank You... program is terminated.");
						flag = false;
						break;
					default:
						System.out.println("Please select an option from 1 to 6.");
						break;
				}
				
//				if(option == 1) {
//					bookARoom(con,scanner);
//					System.out.println();
//				}
//				else if(option == 2) {
//					ViewBookedRoom(con,scanner);
//					System.out.println();
//				}
//				else if(option == 3) {
//					ShowAllBookings(con);
//					System.out.println();
//				}
//				else if(option == 4) {
//					updateDetails(con,scanner);
//					System.out.println();
//				}
//				else if(option == 5) {
//					deleteBooking(con,scanner);
//					System.out.println();
//				}
//				else if(option == 6) {
//					System.out.println("Thank You... program is terminated.");
//					break;
//				}
//				else {
//					System.out.println("Please select an option from 1 to 6.");
//				}
				
			}
			//close the connection
			con.close();
			System.out.println("Connection has been closed.");
			
		} catch (Exception e) {
			System.out.println("Failed to connect with DB : " + e.getMessage());
		}
    }
    
    //Method to book a room
    private static void bookARoom(Connection con,Scanner scanner){
    	//columns : id, room_no, name, phone, booking_time
    	String sql = "INSERT INTO booking_table(name, room_no, phone) VALUES(?,?,?)";
    	
    	try {
			PreparedStatement preparedStatement = con.prepareStatement(sql);
			
			System.out.println("ENTER NAME : ");
			String name = scanner.next();
			System.out.println("ENTER ROOM NO : ");
			int roomNo = scanner.nextInt();
			System.out.println("ENTER PHONE NO : ");
			String phoneNo = scanner.next();
			
			if(name != null || name != "" || !name.equals("") || phoneNo != null || phoneNo != "" || phoneNo != "0" || roomNo != 0) {
				preparedStatement.setString(1,name);
				preparedStatement.setInt(2, roomNo);
				preparedStatement.setString(3, phoneNo);
				
				int rowsAffected = preparedStatement.executeUpdate();
				
				if(rowsAffected > 0) {
					System.out.println("Booked Successfully...");
				}
				else {
					System.out.println("Failed to book...please try again.");
				}
			}
			else {
				System.out.println("Please Enter Details : ");
			}
			
			preparedStatement.close();
		} catch (SQLException e) {
			System.out.println("Failed to book a room : " + e.getMessage());
		}
    	
    }
    
    //Method to View only one Booked Room
    private static void ViewBookedRoom(Connection con,Scanner scanner) {
    	String sql = "SELECT * FROM booking_table where id = ? AND name = ?";
    	
    	try {
    		PreparedStatement preparedStatement = con.prepareStatement(sql);
    		//Input from the user
    		System.out.println("ENTER ID : ");
    		int userInputID = scanner.nextInt();
    		
    		if(!bookingExists(con, userInputID)) {
				System.out.println("Booking Not Available with ID: " + userInputID);
				return;
			}
    		
    		System.out.println("ENTER NAME : ");
    		String userInputName = scanner.next();
    		
    		if(userInputName != null || userInputName !="" || !userInputName.equals("") && userInputID != 0) {
    			preparedStatement.setInt(1,userInputID);
        		preparedStatement.setString(2,userInputName);
        		
        		ResultSet resultSet = preparedStatement.executeQuery();
        		System.out.println("+.......+..................+...............+....................+..........................+");
        		System.out.println("|  ID   |       NAME       |    ROOM NO    |        PHONE       |            TIME          |");
        		System.out.println("+.......+..................+...............+....................+..........................+");
        		
        		while(resultSet.next()) {
        			int id = resultSet.getInt("id");
        			String name = resultSet.getString("name");
        			int roomNo = resultSet.getInt("room_no");
        			String phoneNo = resultSet.getString("phone");
        			Timestamp timestamp = resultSet.getTimestamp("booking_time");
        			
        			System.out.println("|   "+id+"   |        "+name+"       |       "+roomNo+"       |     "+phoneNo+"     |   "+timestamp+"  |");
        		}
        		System.out.println("+.......+..................+...............+....................+..........................+");
        		System.out.println();
    		}
    		else {
    			System.out.println("Please enter the id and name...");
    		}
    		
			
		} catch (SQLException e) {
			System.out.println("Failed to get the Single Data from the table : " + e.getMessage());
		}
    }
    
    //Method to show all the Bookings
    private static void ShowAllBookings(Connection con){
    	String sql = "SELECT * FROM booking_table";
    	
    	try {
			Statement stmnt = con.createStatement();
			ResultSet resultSet = stmnt.executeQuery(sql);
			
			System.out.println("+.......+..................+...............+....................+..........................+");
    		System.out.println("|  ID   |       NAME       |    ROOM NO    |        PHONE       |            TIME          |");
    		System.out.println("+.......+..................+...............+....................+..........................+");
    		
    		while(resultSet.next()) {
    			int id = resultSet.getInt("id");
    			String name = resultSet.getString("name");
    			int roomNo = resultSet.getInt("room_no");
    			String phoneNo = resultSet.getString("phone");
    			Timestamp timestamp = resultSet.getTimestamp("booking_time");
    			
    			System.out.println("|   "+id+"   |        "+name+"       |       "+roomNo+"       |     "+phoneNo+"     |   "+timestamp+"  |");
    		}
    		System.out.println("+.......+..................+...............+....................+..........................+");
    		System.out.println();
    		
    		resultSet.close();
    		stmnt.close();
		} catch (SQLException e) {
			System.out.println("Failed to get All Data from Table : " + e.getMessage());
		}
    	 	
    }
    
    //Method to Update Booking Details
    private static void updateDetails(Connection con,Scanner scanner) {
    	String sql = "UPDATE booking_table SET phone = ?, name = ? WHERE id = ?";
    	
    	try {
			PreparedStatement preparedStatement = con.prepareStatement(sql);
			
			System.out.println("ENTER ID : ");
			int inputID = scanner.nextInt();
			
			if(!bookingExists(con, inputID)) {
				System.out.println("Booking Not Available with ID: " + inputID);
				return;
			}
			
			System.out.println("ENTER NAME : ");
			String inputName = scanner.next();
			System.out.println("ENTER PHONE : ");
			String inputPhone = scanner.next();
			
			if(inputID != 0 && inputPhone != null) {
				preparedStatement.setString(1, inputPhone);
				preparedStatement.setString(2,inputName);
				preparedStatement.setInt(3,inputID);
				int rowsAffected = preparedStatement.executeUpdate();
				
				if(rowsAffected > 0) {
					System.out.println("Successfully updated the details.");
				}
				else {
					System.out.println("Details not updated.");
				}
			}
			else {
				System.out.println("Please Enter Details.");
			}
			
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println("Failed to update the details : " + e.getMessage());
			e.printStackTrace();
		}
    }
    
    //Method to Delete Booking
    private static void deleteBooking(Connection con,Scanner scanner) {
    	String sql = "DELETE FROM booking_table where id = ?";
    	
    	try {
			PreparedStatement prepareStatement = con.prepareStatement(sql);
			System.out.println("ENTER ID TO DELETE DETAILS : ");
			int inputID = scanner.nextInt();
			if(inputID != 0) {
				
				if(!bookingExists(con, inputID)) {
					System.out.println("Booking Not Available with ID: " + inputID);
					return;
				}
				
				prepareStatement.setInt(1, inputID);
				int rowsAffected = prepareStatement.executeUpdate();
				if(rowsAffected > 0) {
					System.out.println("Booking Deleted Successfully.");
				}
				else {
					System.out.println("Booking not Deleted.");
				}
			}
		} catch (Exception e) {
			System.out.println("Failed to Delete : " + e.getMessage());
		}
    }
    
    //Method to check if Booking Exists
    private static boolean bookingExists(Connection con, int id) {
    	String sql = "SELECT * FROM booking_table WHERE id = ?";
    	try {
			PreparedStatement prepareStatement = con.prepareStatement(sql);
			prepareStatement.setInt(1, id);
			
			ResultSet resultSet = prepareStatement.executeQuery();
			
			if(resultSet.next()) {
				System.out.println("Booking Exists on ID : " + id);
				return true;
			}
			
			resultSet.close();
			prepareStatement.close();
			
		} catch (SQLException e) {
			System.out.println("Message : " + e.getMessage());
		}
    	return false;
    }
}
