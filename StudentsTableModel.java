import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class StudentsTableModel extends AbstractTableModel {
    private final String[] columns = {"ID", "Име", "Група"};
    private List<Student> students = new ArrayList<Student>();

    public StudentsTableModel(List<Student> students) {
        if (students != null) this.students = new ArrayList<Student>(students);
    }

    public void setStudents(List<Student> students) {
        if (students == null) this.students = new ArrayList<Student>();
        else this.students = new ArrayList<Student>(students);
        fireTableDataChanged();
    }

    public Student getStudentAt(int row) {
        if (row < 0 || row >= students.size()) return null;
        return students.get(row);
    }

    @Override public int getRowCount() { return students.size(); }
    @Override public int getColumnCount() { return columns.length; }
    @Override public String getColumnName(int column) { return columns[column]; }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Student s = students.get(rowIndex);
        switch (columnIndex) {
            case 0: return s.getId();
            case 1: return s.getName();
            case 2: return s.getGroup();
            default: return "";
        }
    }
}
