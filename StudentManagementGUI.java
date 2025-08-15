import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.List;

public class StudentManagementGUI extends JFrame {
    private StudentDAO dao;
    private JTextField idField, nameField, deptField, ageField, genderField, searchField;
    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> sortBox;

    public StudentManagementGUI() throws SQLException {
        Connection conn = DBConnection.getConnection();
        dao = new StudentDAO(conn);
        dao.createTableIfNotExists();

        setTitle("Student Management System");
        setSize(950, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // === Form Panel ===
        JPanel formPanel = new JPanel(new GridLayout(2, 6, 5, 5));
        idField = new JTextField();
        nameField = new JTextField();
        deptField = new JTextField();
        ageField = new JTextField();
        genderField = new JTextField();

        formPanel.add(new JLabel("ID:"));
        formPanel.add(new JLabel("Name:"));
        formPanel.add(new JLabel("Department:"));
        formPanel.add(new JLabel("Age:"));
        formPanel.add(new JLabel("Gender:"));
        formPanel.add(new JLabel("")); // Empty cell

        formPanel.add(idField);
        formPanel.add(nameField);
        formPanel.add(deptField);
        formPanel.add(ageField);
        formPanel.add(genderField);

        // === Button Panel ===
        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");
        JButton clearBtn = new JButton("Clear Form");
        JButton exportBtn = new JButton("Export CSV");

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(refreshBtn);
        btnPanel.add(clearBtn);
        btnPanel.add(exportBtn);

        // === Table ===
        model = new DefaultTableModel(new String[]{"ID", "Name", "Department", "Age", "Gender"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // === Search & Sort Panel ===
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(15);
        sortBox = new JComboBox<>(new String[]{"name", "age", "department"});
        JButton sortBtn = new JButton("Sort");

        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Sort by:"));
        searchPanel.add(sortBox);
        searchPanel.add(sortBtn);

        // === Adding Panels ===
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(btnPanel, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.SOUTH);

        // Load initial table data
        loadTable(null);

        // ===== Feature 1: Row Click → Auto Fill =====
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                idField.setText(model.getValueAt(row, 0).toString());
                nameField.setText(model.getValueAt(row, 1).toString());
                deptField.setText(model.getValueAt(row, 2).toString());
                ageField.setText(model.getValueAt(row, 3).toString());
                genderField.setText(model.getValueAt(row, 4).toString());
            }
        });

        // ===== Feature 2: Live Search =====
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                searchStudents();
            }
        });

        // Button Actions
        addBtn.addActionListener(e -> addStudent());
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        refreshBtn.addActionListener(e -> loadTable(null));
        clearBtn.addActionListener(e -> clearForm());
        exportBtn.addActionListener(e -> exportToCSV());
        sortBtn.addActionListener(e -> loadTable((String) sortBox.getSelectedItem()));
    }

    private void loadTable(String sortBy) {
        try {
            model.setRowCount(0);
            List<Student> list = dao.getAllStudents(sortBy);
            for (Student s : list) {
                model.addRow(new Object[]{s.getId(), s.getName(), s.getDepartment(), s.getAge(), s.getGender()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
        }
    }

    // ===== Feature 3: Input Validation =====
    private boolean validateInput() {
        if (idField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty() || deptField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID, Name, and Department are required.");
            return false;
        }
        try {
            Integer.parseInt(ageField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Age must be a valid number.");
            return false;
        }
        String gender = genderField.getText().trim().toLowerCase();
        if (!(gender.equals("male") || gender.equals("female") || gender.equals("other"))) {
            JOptionPane.showMessageDialog(this, "Gender must be Male, Female, or Other.");
            return false;
        }
        return true;
    }

    private void addStudent() {
        try {
            if (!validateInput()) return;
            Student s = new Student(
                idField.getText().trim(),
                nameField.getText().trim(),
                deptField.getText().trim(),
                Integer.parseInt(ageField.getText().trim()),
                genderField.getText().trim()
            );
            boolean ok = dao.addStudent(s);
            JOptionPane.showMessageDialog(this, ok ? "Added successfully" : "Duplicate ID - Not added");
            loadTable(null);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateStudent() {
        try {
            if (!validateInput()) return;
            String id = idField.getText().trim();
            Student s = dao.getStudentById(id);
            if (s != null) {
                s.setName(nameField.getText().trim());
                s.setDepartment(deptField.getText().trim());
                s.setAge(Integer.parseInt(ageField.getText().trim()));
                s.setGender(genderField.getText().trim());
                dao.updateStudent(s);
                JOptionPane.showMessageDialog(this, "Updated successfully");
                loadTable(null);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        try {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a row to delete");
                return;
            }
            String id = model.getValueAt(row, 0).toString();
            dao.deleteStudent(id);
            JOptionPane.showMessageDialog(this, "Deleted successfully");
            loadTable(null);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
        }
    }

    private void searchStudents() {
        try {
            String term = searchField.getText().trim();
            List<Student> list = dao.search(term);
            model.setRowCount(0);
            for (Student s : list) {
                model.addRow(new Object[]{s.getId(), s.getName(), s.getDepartment(), s.getAge(), s.getGender()});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "DB Error: " + e.getMessage());
        }
    }

    // ===== Feature 4: Clear Form =====
    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        deptField.setText("");
        ageField.setText("");
        genderField.setText("");
        idField.requestFocus();
    }

    // ===== Feature 5: Export to CSV =====
    private void exportToCSV() {
        try {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try (FileWriter writer = new FileWriter(chooser.getSelectedFile() + ".csv")) {
                    writer.write("ID,Name,Department,Age,Gender\n");
                    for (int i = 0; i < model.getRowCount(); i++) {
                        for (int j = 0; j < model.getColumnCount(); j++) {
                            writer.write(model.getValueAt(i, j).toString());
                            if (j < model.getColumnCount() - 1) writer.write(",");
                        }
                        writer.write("\n");
                    }
                    JOptionPane.showMessageDialog(this, "Exported Successfully");
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error exporting: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new StudentManagementGUI().setVisible(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
