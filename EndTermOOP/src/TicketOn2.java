import java.sql.*;
import java.util.Scanner;

class MovieReservation {
    private static Connection connection;
    private static String MOVIE_TABLE = "movies4";
    private static String USER_TABLE = "users";
    private static String RESERVATION_TABLE = "reservations";
    private static String currentUser;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        initializeDatabase();

        while (true) {
            System.out.println("\nMovie Reservation System:");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");

            System.out.print("Enter your choice (1-3): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume the newline character

            if (choice == 1) {
                loginUser();
            } else if (choice == 2) {
                registerUser();
            } else if (choice == 3) {
                closeDatabaseConnection();
                scanner.close();
                System.out.println("Exiting Movie Reservation System. Goodbye!");
                System.exit(0);
            } else {
                System.out.println("Invalid choice. Please enter 1, 2, or 3.");
            }

            if (currentUser != null) {
                showMenu();
            }
        }
    }

    private static void initializeDatabase() {
        try {
            String url = "jdbc:postgresql://localhost:5432/OOPPROJ1";
            String username = "postgres";
            String password = "Zappivs123!";

            connection = DriverManager.getConnection(url, username, password);

            // Create the movies table if it doesn't exist
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + MOVIE_TABLE + " (" +
                        "id SERIAL PRIMARY KEY," +
                        "movie_name VARCHAR(255) NOT NULL," +
                        "show_time1 VARCHAR(10) NOT NULL," +
                        "show_time2 VARCHAR(10) NOT NULL," +
                        "show_time3 VARCHAR(10) NOT NULL," +
                        "show_time4 VARCHAR(10) NOT NULL," +
                        "show_time5 VARCHAR(10) NOT NULL," +
                        "price1 DECIMAL(5, 2) NOT NULL," +
                        "price2 DECIMAL(5, 2) NOT NULL," +
                        "price3 DECIMAL(5, 2) NOT NULL," +
                        "price4 DECIMAL(5, 2) NOT NULL," +
                        "price5 DECIMAL(5, 2) NOT NULL" +
                        ")");
            }

            // Create the users table if it doesn't exist
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + USER_TABLE + " (" +
                        "id SERIAL PRIMARY KEY," +
                        "username VARCHAR(255) NOT NULL," +
                        "password VARCHAR(255) NOT NULL" +
                        ")");
            }

            // Create the reservations table if it doesn't exist
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS " + RESERVATION_TABLE + " (" +
                        "id SERIAL PRIMARY KEY," +
                        "username VARCHAR(255) NOT NULL," +
                        "movie_name VARCHAR(255) NOT NULL," +
                        "show_time VARCHAR(10) NOT NULL" +
                        ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loginUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEnter Login Details:");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (checkUserExists(username, password)) {
            currentUser = username;
            System.out.println("Login successful. Welcome, " + currentUser + "!");
        } else {
            System.out.println("Invalid username or password. Please try again.");
        }
    }

    private static void registerUser() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEnter Registration Details:");

        System.out.print("Username: ");
        String username = scanner.nextLine();

        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (insertUser(username, password)) {
            System.out.println("Registration successful. Please log in.");
        } else {
            System.out.println("Registration failed. Please try again with a different username.");
        }
    }

    private static boolean insertUser(String username, String password) {
        String insertUserSQL = "INSERT INTO " + USER_TABLE + " (username, password) VALUES (?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertUserSQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            preparedStatement.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean checkUserExists(String username, String password) {
        String selectUserSQL = "SELECT * FROM " + USER_TABLE + " WHERE username = ? AND password = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectUserSQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void showMenu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nOptions:");
            System.out.println("1. Show Movies");
            System.out.println("2. Make Reservation");
            System.out.println("3. View Reservations");
            System.out.println("4. Logout");

            System.out.print("Enter your choice (1-4): ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume the newline character

            if (choice == 1) {
                showMovies();
            } else if (choice == 2) {
                makeReservation();
            } else if (choice == 3) {
                viewReservations();
            } else if (choice == 4) {
                currentUser = null;
                System.out.println("Logout successful. Returning to login screen.");
                break;
            } else {
                System.out.println("Invalid choice. Please enter a number between 1 and 4.");
            }
        }
    }

    private static void showMovies() {
        System.out.println("\nAvailable Movies:");

        String selectMoviesSQL = "SELECT * FROM " + MOVIE_TABLE;

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectMoviesSQL)) {

            if (!resultSet.isBeforeFirst()) {
                System.out.println("No movies available.");
            } else {
                while (resultSet.next()) {
                    String movieName = resultSet.getString("movie_name");
                    String showTime = resultSet.getString("show_time1") + ", " +
                            resultSet.getString("show_time2") + ", " +
                            resultSet.getString("show_time3") + ", " +
                            resultSet.getString("show_time4") + ", " +
                            resultSet.getString("show_time5");

                    System.out.println("Movie Name: " + movieName);
                    System.out.println("Show Times: " + showTime);
                    System.out.println();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void makeReservation() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nEnter Reservation Details:");

        System.out.print("Movie Name: ");
        String movieName = scanner.nextLine();

        System.out.println("Choose Show Time (1-5): ");
        int showTimeChoice = scanner.nextInt();
        scanner.nextLine(); // consume the newline character

        String showTimeColumn = "show_time" + showTimeChoice;

        insertReservation(currentUser, movieName, showTimeColumn);

        System.out.println("Reservation made successfully!");
    }

    private static void insertReservation(String username, String movieName, String showTimeColumn) {
        String insertReservationSQL = "INSERT INTO " + RESERVATION_TABLE + " (username, movie_name, show_time) " +
                "VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertReservationSQL)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, movieName);
            preparedStatement.setString(3, showTimeColumn);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void viewReservations() {
        System.out.println("\nYour Reservations:");

        String selectReservationsSQL = "SELECT * FROM " + RESERVATION_TABLE + " WHERE username = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectReservationsSQL)) {
            preparedStatement.setString(1, currentUser);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String movieName = resultSet.getString("movie_name");
                    String showTimeColumn = resultSet.getString("show_time");

                    String showTime = convertShowTime(showTimeColumn);

                    System.out.println("Movie Name: " + movieName);
                    System.out.println("Show Time: " + showTime);
                    System.out.println();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static String convertShowTime(String showTimeColumn) {
        // Add your logic here to convert show_timeColumn to the desired format (e.g., 19:20)
        // For simplicity, let's assume show_timeColumn is in the format "show_timeX" where X is the show time index
        String[] showTimeParts = showTimeColumn.split("show_time");
        if (showTimeParts.length == 2) {
            int showTimeIndex = Integer.parseInt(showTimeParts[1].trim());
            int hour = 17 + showTimeIndex; // Assuming shows start at 5:00 PM
            int minute = 0; // Assuming shows start on the hour
            return String.format("%02d:%02d", hour, minute);
        } else {
            return showTimeColumn; // Return as is if not in expected format
        }
    }

    private static void closeDatabaseConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}