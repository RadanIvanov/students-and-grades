import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    void addStudent(Student s);
    Optional<Student> findById(int id);
    List<Student> findByGroup(String group);
    List<Student> getAllStudents();
}
