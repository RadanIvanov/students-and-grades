import javax.swing.SwingUtilities;

public class GradeAppWithGUI {
    public static void main(String[] args) {
        StudentRepository realRepo = new RealStudentRepository();
        StudentRepository repo = new StudentRepositoryProxy(realRepo);

        // Примерни студенти
        repo.addStudent(new Student(1, "Иван Иванов", "A1"));
        repo.addStudent(new Student(2, "Петър Петров", "A1"));
        repo.addStudent(new Student(3, "Мария Георгиева", "B2"));
        repo.addStudent(new Student(4, "Георги Стоянов", "A1"));
        repo.addStudent(new Student(5, "Елена Димитрова", "B2"));

        GradeService gradeService = new GradeService(repo);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainWindow mw = new MainWindow(repo, gradeService);
                mw.setVisible(true);
            }
        });
    }
}

