import java.io.*;
import java.util.*;


class StudentNotFoundException extends Exception {
    public StudentNotFoundException(String msg) {
        super(msg);
    }
}


abstract class Person {
    String name;
    String email;

    public Person(String name, String email) {
        this.name = name;
        this.email = email;
    }

    abstract void displayInfo();
}

// ======================= Student Class ==============================
class Student extends Person {
    int rollNo;
    String course;
    double marks;
    String grade;

    public Student(int rollNo, String name, String email, String course, double marks) {
        super(name, email);
        this.rollNo = rollNo;
        this.course = course;
        this.marks = marks;
        calculateGrade();
    }

    public void calculateGrade() {
        if (marks >= 90) grade = "A";
        else if (marks >= 75) grade = "B";
        else if (marks >= 60) grade = "C";
        else grade = "D";
    }

    @Override
    public void displayInfo() {
        System.out.println("Roll No: " + rollNo);
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Course: " + course);
        System.out.println("Marks: " + marks);
        System.out.println("Grade: " + grade + "\n");
    }
}


interface RecordActions {
    void addStudent() throws Exception;
    void deleteStudent() throws StudentNotFoundException;
    void updateStudent() throws StudentNotFoundException;
    void searchStudent() throws StudentNotFoundException;
    void viewAllStudents();
}


class Loader implements Runnable {
    String message;
    public Loader(String message) { this.message = message; }

    @Override
    public void run() {
        try {
            System.out.print(message);
            for (int i = 0; i < 3; i++) {
                Thread.sleep(500);
                System.out.print(".");
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("Loading interrupted.");
        }
    }
}


class StudentManager implements RecordActions {

    Map<Integer, Student> map = new HashMap<>();
    List<Student> list = new ArrayList<>();
    Scanner sc = new Scanner(System.in);
    final String FILE_NAME = "students.txt";

    public StudentManager() {
        loadRecords();
    }

    // ---------------- Load from File ---------------------
    void loadRecords() {
        try {
            File file = new File(FILE_NAME);
            if (!file.exists()) file.createNewFile();

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                String[] d = line.split(",");
                int r = Integer.parseInt(d[0]);
                String name = d[1];
                String email = d[2];
                String course = d[3];
                double marks = Double.parseDouble(d[4]);

                Student s = new Student(r, name, email, course, marks);
                map.put(r, s);
            }
            br.close();

            list.clear();
            list.addAll(map.values());

            System.out.println("Records Loaded Successfully!\n");

        } catch (Exception e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }

    // ---------------- Save to File ---------------------
    void saveRecords() {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME));

            for (Student s : list) {
                bw.write(s.rollNo + "," + s.name + "," + s.email + "," +
                        s.course + "," + s.marks);
                bw.newLine();
            }
            bw.close();

            Thread t = new Thread(new Loader("Saving"));
            t.start();
            t.join();

            System.out.println("Records Saved!");

        } catch (Exception e) {
            System.out.println("Error saving file.");
        }
    }

    // ---------------- Add Student -----------------------
    @Override
    public void addStudent() throws Exception {
        System.out.print("Enter Roll No: ");
        int roll = Integer.parseInt(sc.nextLine());

        if (map.containsKey(roll))
            throw new Exception("Roll No already exists!");

        System.out.print("Enter Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Email: ");
        String email = sc.nextLine();

        System.out.print("Enter Course: ");
        String course = sc.nextLine();

        System.out.print("Enter Marks: ");
        double marks = Double.parseDouble(sc.nextLine());

        if (marks < 0 || marks > 100)
            throw new Exception("Marks must be between 0–100.");

        Student s = new Student(roll, name, email, course, marks);
        map.put(roll, s);
        list.add(s);

        Thread t = new Thread(new Loader("Adding"));
        t.start();
        t.join();

        System.out.println("Student Added Successfully!\n");
    }

    // ---------------- Delete Student -----------------------
    @Override
    public void deleteStudent() throws StudentNotFoundException {
        System.out.print("Enter Roll No to delete: ");
        int roll = Integer.parseInt(sc.nextLine());

        if (!map.containsKey(roll))
            throw new StudentNotFoundException("Student not found!");

        map.remove(roll);
        list.removeIf(s -> s.rollNo == roll);

        System.out.println("Record Deleted!\n");
    }

    // ---------------- Update Student -----------------------
    @Override
    public void updateStudent() throws StudentNotFoundException {
        System.out.print("Enter Roll No to update: ");
        int roll = Integer.parseInt(sc.nextLine());

        if (!map.containsKey(roll))
            throw new StudentNotFoundException("Student not found!");

        Student s = map.get(roll);

        System.out.print("Enter New Email: ");
        s.email = sc.nextLine();

        System.out.print("Enter New Course: ");
        s.course = sc.nextLine();

        System.out.print("Enter New Marks: ");
        s.marks = Double.parseDouble(sc.nextLine());

        s.calculateGrade();

        System.out.println("Record Updated!\n");
    }

    // ---------------- Search by Name -----------------------
    @Override
    public void searchStudent() throws StudentNotFoundException {
        System.out.print("Enter Name to search: ");
        String name = sc.nextLine();
        boolean found = false;

        for (Student s : list) {
            if (s.name.equalsIgnoreCase(name)) {
                s.displayInfo();
                found = true;
            }
        }

        if (!found)
            throw new StudentNotFoundException("Student not found!");
    }

    // ---------------- View All -----------------------
    @Override
    public void viewAllStudents() {
        Iterator<Student> itr = list.iterator();
        while (itr.hasNext()) itr.next().displayInfo();
    }

    // ---------------- Sort -----------------------
    void sortByMarks() {
        list.sort(Comparator.comparingDouble((Student s) -> s.marks).reversed());
        System.out.println("Sorted By Marks:\n");
        viewAllStudents();
    }
}

// ======================= MAIN CLASS ===============================
public class Assignment5_Capstone {
    public static void main(String[] args) {

        StudentManager sm = new StudentManager();
        Scanner sc = new Scanner(System.in);

        while (true) {

            System.out.println("\n===== Capstone Student Menu =====");
            System.out.println("1. Add Student");
            System.out.println("2. View All Students");
            System.out.println("3. Search Student");
            System.out.println("4. Delete Student");
            System.out.println("5. Update Student");
            System.out.println("6. Sort by Marks");
            System.out.println("7. Save & Exit");
            System.out.print("Enter choice: ");

            int ch;
            try {
                ch = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid Input!");
                continue;
            }

            try {
                switch (ch) {
                    case 1 -> sm.addStudent();
                    case 2 -> sm.viewAllStudents();
                    case 3 -> sm.searchStudent();
                    case 4 -> sm.deleteStudent();
                    case 5 -> sm.updateStudent();
                    case 6 -> sm.sortByMarks();
                    case 7 -> {
                        sm.saveRecords();
                        System.out.println("Exiting...");
                        return;
                    }
                    default -> System.out.println("Invalid Option!");
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }
}
