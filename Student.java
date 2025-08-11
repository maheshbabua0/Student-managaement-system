public class Student {
    private String id;
    private String name;
    private String department;
    private int age;

    public Student(String id, String name, String department, int age) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.age = age;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public int getAge() { return age; }

    public String toString() {
        return id + " | " + name + " | " + department + " | " + age;
    }
}
