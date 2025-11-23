import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.awt.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainWindow extends JFrame {

    private final StudentRepository repo;
    private final GradeService gradeService;

    private final StudentsTableModel studentsTableModel;
    private final JTable studentsTable;
    private final TableRowSorter<StudentsTableModel> sorter;

    private final JTextField filterNameField = new JTextField(15);
    private final JTextField filterGroupField = new JTextField(6);
    private final JComboBox<Object> studentComboBox = new JComboBox<Object>();
    private final JComboBox<Discipline> disciplineComboBox = new JComboBox<Discipline>();
    private final JTextField gradeField = new JTextField(5);
    private final JTextArea groupViewArea = new JTextArea(12, 40);

    public MainWindow(StudentRepository repo, GradeService gradeService) {
        super("GradeApp — GUI (Swing) — Observer + Proxy + Factory");
        this.repo = repo;
        this.gradeService = gradeService;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        studentsTableModel = new StudentsTableModel(repo.getAllStudents());
        studentsTable = new JTable(studentsTableModel);
        sorter = new TableRowSorter<StudentsTableModel>(studentsTableModel);
        studentsTable.setRowSorter(sorter);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Студенти"));
        topPanel.add(new JScrollPane(studentsTable), BorderLayout.CENTER);
        topPanel.add(buildFiltersPanel(), BorderLayout.NORTH);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Group view (автоматично)"));
        groupViewArea.setEditable(false);
        groupViewArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        rightPanel.add(new JScrollPane(groupViewArea), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Добавяне на оценка"));

        bottomPanel.add(new JLabel("Студент:"));
        bottomPanel.add(studentComboBox);

        bottomPanel.add(new JLabel("Дисциплина:"));
        bottomPanel.add(disciplineComboBox);

        bottomPanel.add(new JLabel("Оценка (2.00-6.00):"));
        bottomPanel.add(gradeField);

        JButton addGradeBtn = new JButton("Добави оценка");
        bottomPanel.add(addGradeBtn);

        bottomPanel.add(Box.createHorizontalStrut(20));

        JButton refreshBtn = new JButton("Обнови списъка");
        bottomPanel.add(refreshBtn);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, topPanel, rightPanel);
        split.setResizeWeight(0.65);

        add(split, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        reloadStudentCombo();

        disciplineComboBox.removeAllItems();
        for (Discipline d : DisciplineFactory.defaultDisciplines()) {
            disciplineComboBox.addItem(d);
        }

        // register GroupView observer for group "A1" by default
        GroupView gv = new GroupView("A1", repo, gradeService, new GroupViewCallback() {
            public void update(List<String> lines) {
                final String txt = joinLines(lines);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        groupViewArea.setText(txt);
                    }
                });
            }
        });
        gradeService.registerObserver(gv);

        addGradeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onAddGrade();
            }
        });

        refreshBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reloadData();
            }
        });

        studentsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = studentsTable.getSelectedRow();
                    if (row >= 0) {
                        row = studentsTable.convertRowIndexToModel(row);
                        Student s = studentsTableModel.getStudentAt(row);
                        selectStudentInCombo(s.getId());
                    }
                }
            }
        });

        DocumentListener filterListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        };

        filterNameField.getDocument().addDocumentListener(filterListener);
        filterGroupField.getDocument().addDocumentListener(filterListener);
    }

    private JPanel buildFiltersPanel() {
        JPanel p = new JPanel();
        p.add(new JLabel("Филтър по име:"));
        p.add(filterNameField);

        p.add(new JLabel("Филтър по група:"));
        p.add(filterGroupField);

        JButton clear = new JButton("Изчисти филтрите");
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                filterNameField.setText("");
                filterGroupField.setText("");
                applyFilters();
            }
        });

        p.add(clear);
        return p;
    }

    private void onAddGrade() {
        Object selectedStudentObj = studentComboBox.getSelectedItem();
        Discipline d = (Discipline) disciplineComboBox.getSelectedItem();
        String gradeText = gradeField.getText().trim();

        if (selectedStudentObj == null || d == null || gradeText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Попълнете всички полета", "Грешка",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int studentId;
        if (selectedStudentObj instanceof Student) {
            studentId = ((Student) selectedStudentObj).getId();
        } else {
            try {
                String s = selectedStudentObj.toString();
                if (s.contains("[")) {
                    int idx = s.lastIndexOf('[');
                    int end = s.lastIndexOf(']');
                    studentId = Integer.parseInt(s.substring(idx + 1, end));
                } else {
                    JOptionPane.showMessageDialog(this, "Невалиден студент", "Грешка",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Невалиден студент", "Грешка",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        double value;
        try {
            value = Double.parseDouble(gradeText.replace(',', '.'));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Невалиден формат на оценката",
                    "Грешка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            gradeService.addGrade(studentId, d, value);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Грешка", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Оценката е добавена", "OK",
                JOptionPane.INFORMATION_MESSAGE);

        gradeField.setText("");
        reloadData();
    }

    private void applyFilters() {
        RowFilter<StudentsTableModel, Object> rf = new RowFilter<StudentsTableModel, Object>() {
            public boolean include(RowFilter.Entry<? extends StudentsTableModel, ?> entry) {
                StudentsTableModel model = entry.getModel();
                int row = ((Integer) entry.getIdentifier()).intValue();
                Student s = model.getStudentAt(row);

                String nameFilter = filterNameField.getText().trim().toLowerCase();
                String groupFilter = filterGroupField.getText().trim().toLowerCase();

                boolean matchesName = nameFilter.isEmpty() ||
                        s.getName().toLowerCase().contains(nameFilter);

                boolean matchesGroup = groupFilter.isEmpty() ||
                        s.getGroup().toLowerCase().contains(groupFilter);

                return matchesName && matchesGroup;
            }
        };

        sorter.setRowFilter(rf);
    }

    private void reloadStudentCombo() {
        studentComboBox.removeAllItems();
        List<Student> all = repo.getAllStudents();

        Collections.sort(all, new Comparator<Student>() {
            public int compare(Student a, Student b) {
                return a.getName().compareTo(b.getName());
            }
        });

        for (Student s : all) studentComboBox.addItem(s);
    }

    private void selectStudentInCombo(int studentId) {
        ComboBoxModel<Object> model = studentComboBox.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            Object o = model.getElementAt(i);
            if (o instanceof Student && ((Student) o).getId() == studentId) {
                studentComboBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private void reloadData() {
        List<Student> all = repo.getAllStudents();
        studentsTableModel.setStudents(all);
        reloadStudentCombo();
    }

    private static String joinLines(java.util.List<String> lines) {
        StringBuilder sb = new StringBuilder();
        for (String x : lines) {
            sb.append(x).append("\n");
        }
        return sb.toString();
    }
}
