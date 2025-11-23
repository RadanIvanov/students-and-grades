public class Student {
    private final int id;
    private final String name;
    private final String group;

    public Student(int id, String name, String group) {
        this.id = id;
        this.name = name;
        this.group = group;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getGroup() { return group; }

    @Override
    public String toString() {
        return String.format("%s (%s) [%d]", name, group, id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Student)) return false;
        Student s = (Student) o;
        return id == s.id;
    }

    @Override
    public int hashCode() {
        return Integer.valueOf(id).hashCode();
    }
}
