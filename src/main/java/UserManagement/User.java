package UserManagement;

public class User {
    private int userID;
    private String name;
    private int age;
    private String gender;
    private String address;
    private String email;
    private String password;

    public User(int userID, String name, int age, String gender, String address, String email, String password) {
        this.userID = userID;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.email = email;
        this.password = password;
    }

    // Getters
    public int getUserID()
    { return userID; }
    public String getName()
    { return name; }
    public int getAge()
    { return age; }
    public String getGender()
    { return gender; }
    public String getAddress()
    { return address; }
    public String getEmail()
    { return email; }
    public String getPassword()
    { return password; }

    public static void login(int userID, String password, String role) {
        User user = UserDAO.getUser(userID, password, role); // checks DB

        if (user == null) {
            System.out.println("Invalid credentials or user not found.");
            return;
        }

        switch (role.toLowerCase()) {
            case "doctor" -> new Doctor(userID, user.name, user.age, user.gender, user.address, user.email, user.password).loginDoctor();
            case "patient" -> new Patient(userID, user.name, user.age, user.gender, user.address, user.email, user.password).loginPatient();
            case "administrator" -> new Administrator(userID, user.name, user.age, user.gender, user.address, user.email, user.password).loginAdministrator();
            default -> System.out.println("Invalid role.");
        }
    }
}