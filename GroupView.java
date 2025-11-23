import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class GroupView implements GradeObserver {
    private final String group;
    private final StudentRepository repo;
    private final GradeService gradeService;
    private final GroupViewCallback guiUpdater;

    public GroupView(String group, StudentRepository repo, GradeService gradeService, GroupViewCallback guiUpdater) {
        this.group = group;
        this.repo = repo;
        this.gradeService = gradeService;
        this.guiUpdater = guiUpdater;
    }

    @Override
    public void onGradeAdded(Grade grade) {
        Optional<Student> os = repo.findById(grade.getStudentId());
        if (!os.isPresent()) return;
        Student s = os.get();
        if (!s.getGroup().equalsIgnoreCase(group)) return;

        List<Student> students = repo.findByGroup(group);
        // sort by name (Java 8 comparator)
        Collections.sort(students, new Comparator<Student>() {
            public int compare(Student a, Student b) {
                return a.getName().compareTo(b.getName());
            }
        });

        List<String> out = new ArrayList<String>();
        out.add("Група: " + group + " — автоматична визуализация при нова оценка (" + s.getName() + ")");
        for (Student st : students) {
            out.add(" - " + st.getName() + " (id=" + st.getId() + ")");
            List<Grade> grades = gradeService.getGradesForStudent(st.getId());
            if (grades.isEmpty()) {
                out.add("     (няма оценки)");
            } else {
                for (Grade g : grades) {
                    out.add(String.format("     %s: %.2f", g.getDiscipline().getName(), g.getValue()));
                }
            }
        }

        if (guiUpdater != null) guiUpdater.update(out);

        // debug console
        System.out.println("\n[GroupView " + group + "] (console dump)");
        for (String line : out) System.out.println(line);
        System.out.println();
    }
}
