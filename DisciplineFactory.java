import java.util.Arrays;
import java.util.List;

public class DisciplineFactory {
    public static Discipline create(String name) {
        return new Discipline(name);
    }

    public static List<Discipline> defaultDisciplines() {
        return Arrays.asList(
                create("Математика"),
                create("Програмиране"),
                create("Физика"),
                create("Бази Данни")
        );
    }
}
