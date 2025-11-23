import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class GradeService {
    private final Map<Integer, List<Grade>> gradesByStudent = new HashMap<Integer, List<Grade>>();
    private final List<GradeObserver> observers = new ArrayList<GradeObserver>();
    private final StudentRepository repo;

    public GradeService(StudentRepository repo) {
        this.repo = repo;
    }

    public synchronized void registerObserver(GradeObserver o) { observers.add(o); }
    public synchronized void removeObserver(GradeObserver o) { observers.remove(o); }

    public synchronized void addGrade(int studentId, Discipline d, double value) {
        if (value < 2.0 || value > 6.0) throw new IllegalArgumentException("Оценката трябва да е между 2.00 и 6.00");

        Optional<Student> os = repo.findById(studentId);
        if (!os.isPresent()) {
            System.out.println("Няма студент с id=" + studentId);
            return;
        }

        Grade g = new Grade(studentId, d, value);
        List<Grade> list = gradesByStudent.get(studentId);
        if (list == null) {
            list = new ArrayList<Grade>();
            gradesByStudent.put(studentId, list);
        }
        list.add(g);

        // Нотифицираме наблюдателите (копие)
        List<GradeObserver> copy = new ArrayList<GradeObserver>(observers);
        for (GradeObserver o : copy) {
            try { o.onGradeAdded(g); }
            catch (Exception ex) { System.err.println("Observer failed: " + ex.getMessage()); }
        }
    }

    public synchronized List<Grade> getGradesForStudent(int studentId) {
        List<Grade> list = gradesByStudent.get(studentId);
        if (list == null) return Collections.emptyList();
        return new ArrayList<Grade>(list);
    }

    public synchronized Map<Student, Double> calculateAveragePerStudent() {
        Map<Student, Double> res = new LinkedHashMap<Student, Double>();
        for (Map.Entry<Integer, List<Grade>> e : gradesByStudent.entrySet()) {
            Optional<Student> os = repo.findById(e.getKey());
            if (!os.isPresent()) continue;
            double sum = 0.0;
            for (Grade g : e.getValue()) sum += g.getValue();
            double avg = e.getValue().isEmpty() ? 0.0 : sum / e.getValue().size();
            res.put(os.get(), avg);
        }
        return res;
    }
}
