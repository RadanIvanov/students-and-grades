import java.util.Date;

public class Grade {
    private final int studentId;
    private final Discipline discipline;
    private final double value;
    private final Date timestamp;

    public Grade(int studentId, Discipline discipline, double value) {
        this.studentId = studentId;
        this.discipline = discipline;
        this.value = value;
        this.timestamp = new Date();
    }

    public int getStudentId() { return studentId; }
    public Discipline getDiscipline() { return discipline; }
    public double getValue() { return value; }
    public Date getTimestamp() { return timestamp; }
}
