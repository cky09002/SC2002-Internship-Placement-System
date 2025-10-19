package Assignment;

public class Student extends User {
    private final int yearOfStudy;
    private final String major;

    public Student(String userID, String name, String password, int yearOfStudy, String major) {
        super(userID, name, password);
        this.yearOfStudy = yearOfStudy;
        this.major = major;
    }

    public int getYearOfStudy() { return yearOfStudy; }
    public String getMajor() { return major; }

    @Override
    public void displayProfile() {
        System.out.println("Student: " + getUserID() + " | " + getName() +
            " | Year: " + yearOfStudy + " | Major: " + major);
    }

    @Override
    public String getUserType() { return "Student"; }
}