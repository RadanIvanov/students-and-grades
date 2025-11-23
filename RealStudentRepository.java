import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RealStudentRepository implements StudentRepository {
    private final Map<Integer, Student> storage = new HashMap<Integer, Student>();

    @Override
    public void addStudent(Student s) {
        storage.put(s.getId(), s);
    }

    @Override
    public Optional<Student> findById(int id) {
        simulateDelay();
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Student> findByGroup(String group) {
        simulateDelay();
        List<Student> res = new ArrayList<Student>();
        for (Student s : storage.values()) {
            if (s.getGroup().equalsIgnoreCase(group)) res.add(s);
        }
        return res;
    }

    @Override
    public List<Student> getAllStudents() {
        simulateDelay();
        return new ArrayList<Student>(storage.values());
    }

    private void simulateDelay() {
        try { Thread.sleep(30); } catch (InterruptedException ignored) {}
    }
}
