import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentRepositoryProxy implements StudentRepository {
    private final StudentRepository real;
    private List<Student> cache = null;
    private boolean cacheValid = false;

    public StudentRepositoryProxy(StudentRepository real) {
        this.real = real;
    }

    @Override
    public synchronized void addStudent(Student s) {
        System.out.println("[Proxy] addStudent: " + s);
        real.addStudent(s);
        cacheValid = false;
    }

    @Override
    public Optional<Student> findById(int id) {
        System.out.println("[Proxy] findById: " + id);
        return real.findById(id);
    }

    @Override
    public List<Student> findByGroup(String group) {
        System.out.println("[Proxy] findByGroup: " + group);
        if (cacheValid && cache != null) {
            List<Student> res = new ArrayList<Student>();
            for (Student s : cache) {
                if (s.getGroup().equalsIgnoreCase(group)) res.add(s);
            }
            return res;
        }
        return real.findByGroup(group);
    }

    @Override
    public synchronized List<Student> getAllStudents() {
        System.out.println("[Proxy] getAllStudents called (cacheValid=" + cacheValid + ")");
        if (cacheValid && cache != null) {
            return new ArrayList<Student>(cache);
        }
        cache = real.getAllStudents();
        cacheValid = true;
        return new ArrayList<Student>(cache);
    }
}
