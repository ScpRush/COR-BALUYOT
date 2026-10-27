/*
 * Programmed by: Jhaztintroy Baluyot
 * Program      : Certificate of Registration (COR) - Java Swing
 * Description  : Enter student details and enrolled subjects, then generate
 *                a printable-looking Certificate of Registration.
 */

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class COR extends JFrame {

    private static final String[] COLUMNS =
            {"Code", "Description", "Units", "Section", "Schedule", "Room"};

    // ----- Input fields -----
    private final JTextField tfStudentNo = new JTextField();
    private final JTextField tfName = new JTextField();
    private final JComboBox<String> cbProgram = new JComboBox<>(new String[]{
            "BS Computer Science - Artificial Intelligence",
            "BS Computer Science - Machine Learning",
            "BS Information Technology",
            "BS Computer Engineering",
            "BS Data Science"});
    private final JComboBox<String> cbYear =
            new JComboBox<>(new String[]{"1st Year", "2nd Year", "3rd Year", "4th Year"});
    private final JComboBox<String> cbSem =
            new JComboBox<>(new String[]{"1st Semester", "2nd Semester", "Summer"});
    private final JTextField tfSchoolYear = new JTextField("2026-2027");

    // ----- Subject table -----
    private final DefaultTableModel model = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JLabel lblTotal = new JLabel("Total Units: 0");

    public COR() {
        setTitle("COR - Certificate of Registration");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildCenter(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);

        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        // A few sample subjects so the program can be tried right away
        addRow("CS101", "Introduction to Computing", 3, "A11", "Mon/Thu 9:00-10:30", "Rm 301");
        addRow("MATH101", "Discrete Mathematics", 3, "A11", "Tue/Fri 1:00-2:30", "Rm 204");
        addRow("PE101", "Physical Fitness", 2, "A11", "Wed 3:00-5:00", "Gym");

        setSize(980, 620);
        setMinimumSize(new Dimension(900, 560));
        setLocationRelativeTo(null);
    }

    // =====================================================================
    //  Main window layout
    // =====================================================================
    private JPanel buildHeader() {
        JPanel p = new JPanel(new GridLayout(3, 1));
        JLabel school = new JLabel("FAR EASTERN UNIVERSITY INSTITUTE OF TECHNOLOGY", SwingConstants.CENTER);
        school.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel addr = new JLabel("Nicanor Reyes Street, Sampaloc, Manila", SwingConstants.CENTER);
        JLabel title = new JLabel("CERTIFICATE OF REGISTRATION", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        p.add(school);
        p.add(addr);
        p.add(title);
        return p;
    }

    private JPanel buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 10));

        // Student information
        JPanel info = new JPanel(new GridBagLayout());
        info.setBorder(BorderFactory.createTitledBorder("Student Information"));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 6, 4, 6);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridy = 0; g.gridx = 0; g.weightx = 0; info.add(new JLabel("Student No.:"), g);
        g.gridx = 1; g.weightx = 1; info.add(tfStudentNo, g);
        g.gridx = 2; g.weightx = 0; info.add(new JLabel("Student Name:"), g);
        g.gridx = 3; g.weightx = 1; info.add(tfName, g);
        g.gridy = 1; g.gridx = 0; g.weightx = 0; info.add(new JLabel("Program:"), g);
        g.gridx = 1; g.gridwidth = 3; g.weightx = 1; info.add(cbProgram, g);
        g.gridy = 2; g.gridx = 0; g.gridwidth = 1; g.weightx = 0; info.add(new JLabel("Year Level:"), g);
        g.gridx = 1; g.weightx = 1; info.add(cbYear, g);
        g.gridx = 2; g.weightx = 0; info.add(new JLabel("Semester:"), g);
        g.gridx = 3; g.weightx = 1; info.add(cbSem, g);
        g.gridy = 3; g.gridx = 0; g.weightx = 0; info.add(new JLabel("School Year:"), g);
        g.gridx = 1; g.weightx = 1; info.add(tfSchoolYear, g);
        center.add(info, BorderLayout.NORTH);

        // Subjects table
        table.setRowHeight(24);
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(260);
        table.getColumnModel().getColumn(2).setPreferredWidth(50);
        table.getColumnModel().getColumn(4).setPreferredWidth(170);
        centerColumn(table, 2);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Enrolled Subjects"));
        center.add(sp, BorderLayout.CENTER);

        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 14));
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(lblTotal);
        center.add(totalPanel, BorderLayout.SOUTH);

        return center;
    }

    private JPanel buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton btnAdd = new JButton("Add Subject");
        JButton btnRemove = new JButton("Remove Selected");
        JButton btnGenerate = new JButton("Generate COR");
        JButton btnClear = new JButton("Clear All");

        btnAdd.addActionListener(e -> addSubject());
        btnRemove.addActionListener(e -> removeSelected());
        btnGenerate.addActionListener(e -> generateCor());
        btnClear.addActionListener(e -> clearAll());

        p.add(btnAdd);
        p.add(btnRemove);
        p.add(btnGenerate);
        p.add(btnClear);
        return p;
    }

    // =====================================================================
    //  Button actions
    // =====================================================================
    private void addSubject() {
        JTextField code = new JTextField();
        JTextField desc = new JTextField();
        JTextField units = new JTextField();
        JTextField section = new JTextField();
        JTextField schedule = new JTextField();
        JTextField room = new JTextField();

        JPanel form = new JPanel(new GridLayout(0, 2, 6, 6));
        form.add(new JLabel("Subject Code:"));
        form.add(code);
        form.add(new JLabel("Description:"));
        form.add(desc);
        form.add(new JLabel("Units:"));
        form.add(units);
        form.add(new JLabel("Section:"));
        form.add(section);
        form.add(new JLabel("Schedule:"));
        form.add(schedule);
        form.add(new JLabel("Room:"));
        form.add(room);

        int choice = JOptionPane.showConfirmDialog(this, form, "Add Subject",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        if (code.getText().isBlank() || desc.getText().isBlank()) {
            showError("Subject code and description are required.");
            return;
        }

        int u;
        try {
            u = Integer.parseInt(units.getText().trim());
            if (u < 0 || u > 12) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            showError("Units must be a whole number from 0 to 12.");
            return;
        }

        addRow(code.getText().trim().toUpperCase(), desc.getText().trim(), u,
                section.getText().trim(), schedule.getText().trim(), room.getText().trim());
    }

    private void removeSelected() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showError("Select a subject in the table first.");
            return;
        }
        model.removeRow(row);
        updateTotal();
    }

    private void clearAll() {
        int ok = JOptionPane.showConfirmDialog(this, "Clear all fields and subjects?",
                "Clear All", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) {
            return;
        }
        tfStudentNo.setText("");
        tfName.setText("");
        cbProgram.setSelectedIndex(0);
        cbYear.setSelectedIndex(0);
        cbSem.setSelectedIndex(0);
        tfSchoolYear.setText("2026-2027");
        model.setRowCount(0);
        updateTotal();
    }

    private void generateCor() {
        if (tfStudentNo.getText().isBlank() || tfName.getText().isBlank()
                || tfSchoolYear.getText().isBlank()) {
            showError("Please fill in the Student No., Student Name, and School Year.");
            return;
        }
        if (model.getRowCount() == 0) {
            showError("Add at least one subject before generating the COR.");
            return;
        }

        JPanel cor = buildCorPanel();

        JDialog dialog = new JDialog(this, "Certificate of Registration", true);
        dialog.setLayout(new BorderLayout());
        dialog.add(new JScrollPane(cor), BorderLayout.CENTER);

        JButton btnPrint = new JButton("Print");
        JButton btnClose = new JButton("Close");
        btnPrint.addActionListener(e -> printPanel(cor));
        btnClose.addActionListener(e -> dialog.dispose());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        south.add(btnPrint);
        south.add(btnClose);
        dialog.add(south, BorderLayout.SOUTH);

        dialog.pack();
        Rectangle screen = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        dialog.setSize(Math.min(dialog.getWidth() + 20, screen.width - 40),
                Math.min(dialog.getHeight() + 20, screen.height - 40));
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // =====================================================================
    //  COR document (what gets shown / printed)
    // =====================================================================
    private JPanel buildCorPanel() {
        JPanel doc = new JPanel(new BorderLayout(0, 14));
        doc.setBackground(Color.WHITE);
        doc.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(20, 24, 20, 24)));

        // Heading
        JPanel head = new JPanel(new GridLayout(5, 1));
        head.setOpaque(false);
        JLabel school = new JLabel("FAR EASTERN UNIVERSITY INSTITUTE OF TECHNOLOGY", SwingConstants.CENTER);
        school.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel addr = new JLabel("Nicanor Reyes Street, Sampaloc, Manila", SwingConstants.CENTER);
        JLabel title = new JLabel("CERTIFICATE OF REGISTRATION", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        JLabel term = new JLabel(cbSem.getSelectedItem() + ", S.Y. " + tfSchoolYear.getText().trim(),
                SwingConstants.CENTER);
        JLabel date = new JLabel("Date Issued: "
                + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                SwingConstants.CENTER);
        head.add(school);
        head.add(addr);
        head.add(title);
        head.add(term);
        head.add(date);

        // Student info block
        JPanel info = new JPanel(new GridBagLayout());
        info.setOpaque(false);
        info.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.BLACK));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 4, 3, 4);
        g.anchor = GridBagConstraints.WEST;
        g.gridy = 0; g.gridx = 0; info.add(boldLabel("Student No.:"), g);
        g.gridx = 1; g.weightx = 1; info.add(new JLabel(tfStudentNo.getText().trim()), g);
        g.gridx = 2; g.weightx = 0; info.add(boldLabel("Year Level:"), g);
        g.gridx = 3; g.weightx = 1; info.add(new JLabel((String) cbYear.getSelectedItem()), g);
        g.gridy = 1; g.gridx = 0; g.weightx = 0; info.add(boldLabel("Student Name:"), g);
        g.gridx = 1; g.gridwidth = 3; g.weightx = 1;
        info.add(new JLabel(tfName.getText().trim().toUpperCase()), g);
        g.gridy = 2; g.gridx = 0; g.gridwidth = 1; g.weightx = 0; info.add(boldLabel("Program:"), g);
        g.gridx = 1; g.gridwidth = 3; g.weightx = 1;
        info.add(new JLabel((String) cbProgram.getSelectedItem()), g);

        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);
        top.add(head, BorderLayout.NORTH);
        top.add(info, BorderLayout.CENTER);
        doc.add(top, BorderLayout.NORTH);

        // Subjects (read-only copy of the model)
        DefaultTableModel copy = new DefaultTableModel(COLUMNS, 0);
        for (int i = 0; i < model.getRowCount(); i++) {
            Object[] row = new Object[COLUMNS.length];
            for (int c = 0; c < COLUMNS.length; c++) {
                row[c] = model.getValueAt(i, c);
            }
            copy.addRow(row);
        }
        JTable corTable = new JTable(copy) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        corTable.setRowHeight(24);
        corTable.setGridColor(Color.GRAY);
        corTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        corTable.getColumnModel().getColumn(1).setPreferredWidth(240);
        corTable.getColumnModel().getColumn(2).setPreferredWidth(50);
        corTable.getColumnModel().getColumn(3).setPreferredWidth(70);
        corTable.getColumnModel().getColumn(4).setPreferredWidth(170);
        corTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        centerColumn(corTable, 2);
        corTable.setPreferredScrollableViewportSize(corTable.getPreferredSize());

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(corTable.getTableHeader(), BorderLayout.NORTH);
        tablePanel.add(corTable, BorderLayout.CENTER);
        doc.add(tablePanel, BorderLayout.CENTER);

        // Total + signatures
        JLabel total = new JLabel("TOTAL UNITS: " + computeTotal(), SwingConstants.RIGHT);
        total.setFont(new Font("SansSerif", Font.BOLD, 14));

        JPanel sign = new JPanel(new GridLayout(1, 3, 20, 0));
        sign.setOpaque(false);
        sign.add(signatureLine("Student's Signature"));
        sign.add(signatureLine("Registrar"));
        sign.add(signatureLine("Program Chair / Dean"));

        JPanel bottom = new JPanel(new BorderLayout(0, 30));
        bottom.setOpaque(false);
        bottom.add(total, BorderLayout.NORTH);
        bottom.add(sign, BorderLayout.CENTER);
        doc.add(bottom, BorderLayout.SOUTH);

        return doc;
    }

    private void printPanel(JPanel panel) {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Certificate of Registration");
        job.setPrintable((Graphics g, PageFormat pf, int pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }
            Graphics2D g2 = (Graphics2D) g;
            g2.translate(pf.getImageableX(), pf.getImageableY());
            double scale = Math.min(1.0, Math.min(
                    pf.getImageableWidth() / panel.getWidth(),
                    pf.getImageableHeight() / panel.getHeight()));
            g2.scale(scale, scale);
            panel.printAll(g2);
            return Printable.PAGE_EXISTS;
        });
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException ex) {
                showError("Printing failed: " + ex.getMessage());
            }
        }
    }

    // =====================================================================
    //  Helpers
    // =====================================================================
    private void addRow(String code, String desc, int units, String section, String schedule, String room) {
        model.addRow(new Object[]{code, desc, units, section, schedule, room});
        updateTotal();
    }

    private int computeTotal() {
        int total = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            total += (Integer) model.getValueAt(i, 2);
        }
        return total;
    }

    private void updateTotal() {
        lblTotal.setText("Total Units: " + computeTotal());
    }

    private static void centerColumn(JTable t, int col) {
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setHorizontalAlignment(SwingConstants.CENTER);
        t.getColumnModel().getColumn(col).setCellRenderer(r);
    }

    private static JLabel boldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.BOLD));
        return l;
    }

    private static JLabel signatureLine(String caption) {
        return new JLabel("<html><div style='text-align:center'>______________________<br>"
                + caption + "</div></html>", SwingConstants.CENTER);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Notice", JOptionPane.WARNING_MESSAGE);
    }

    // =====================================================================
    //  Entry point
    // =====================================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new COR().setVisible(true));
    }
}
