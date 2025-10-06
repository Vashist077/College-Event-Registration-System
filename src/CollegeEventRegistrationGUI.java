package src;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CollegeEventRegistrationGUI extends JFrame {
    
    // Components
    private JTabbedPane tabbedPane;
    private EventManager eventManager;
    private StudentRegistrationManager registrationManager;
    
    // Add Event Tab Components
    private JTextField txtEventName, txtEventDate;
    
    // View Events Tab Components
    private JTable eventsTable;
    private DefaultTableModel eventsTableModel;
    
    // Register Student Tab Components
    private JTextField txtStudentName, txtStudentEmail;
    private JComboBox<Event> cmbEvents;
    
    // View Participants Tab Components
    private JComboBox<Event> cmbEventsForParticipants;
    private JTable participantsTable;
    private DefaultTableModel participantsTableModel;
    
    public CollegeEventRegistrationGUI() {
        eventManager = new EventManager();
        registrationManager = new StudentRegistrationManager();
        
        initializeComponents();
        setupGUI();
        loadInitialData();
    }
    
    private void initializeComponents() {
        // Main frame setup
        setTitle("College Event Registration System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Initialize components for each tab
        initializeAddEventTab();
        initializeViewEventsTab();
        initializeRegisterStudentTab();
        initializeViewParticipantsTab();
    }
    
    private void initializeAddEventTab() {
        JPanel addEventPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Event Name
        gbc.gridx = 0; gbc.gridy = 0;
        addEventPanel.add(new JLabel("Event Name:"), gbc);
        gbc.gridx = 1;
        txtEventName = new JTextField(20);
        addEventPanel.add(txtEventName, gbc);
        
        // Event Date
        gbc.gridx = 0; gbc.gridy = 1;
        addEventPanel.add(new JLabel("Event Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        txtEventDate = new JTextField(20);
        addEventPanel.add(txtEventDate, gbc);
        
        // Add Event Button
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton btnAddEvent = new JButton("Add Event");
        btnAddEvent.addActionListener(this::addEventAction);
        addEventPanel.add(btnAddEvent, gbc);
        
        tabbedPane.addTab("Add Event", addEventPanel);
    }
    
    private void initializeViewEventsTab() {
        JPanel viewEventsPanel = new JPanel(new BorderLayout());
        
        // Table for events
        String[] columnNames = {"Event ID", "Event Name", "Event Date"};
        eventsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        eventsTable = new JTable(eventsTableModel);
        JScrollPane scrollPane = new JScrollPane(eventsTable);
        
        // Refresh button
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(this::refreshEventsAction);
        
        viewEventsPanel.add(scrollPane, BorderLayout.CENTER);
        viewEventsPanel.add(btnRefresh, BorderLayout.SOUTH);
        
        tabbedPane.addTab("View Events", viewEventsPanel);
    }
    
    private void initializeRegisterStudentTab() {
        JPanel registerPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Student Name
        gbc.gridx = 0; gbc.gridy = 0;
        registerPanel.add(new JLabel("Student Name:"), gbc);
        gbc.gridx = 1;
        txtStudentName = new JTextField(20);
        registerPanel.add(txtStudentName, gbc);
        
        // Student Email
        gbc.gridx = 0; gbc.gridy = 1;
        registerPanel.add(new JLabel("Student Email:"), gbc);
        gbc.gridx = 1;
        txtStudentEmail = new JTextField(20);
        registerPanel.add(txtStudentEmail, gbc);
        
        // Event Selection
        gbc.gridx = 0; gbc.gridy = 2;
        registerPanel.add(new JLabel("Select Event:"), gbc);
        gbc.gridx = 1;
        cmbEvents = new JComboBox<>();
        registerPanel.add(cmbEvents, gbc);
        
        // Register Button
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton btnRegister = new JButton("Register Student");
        btnRegister.addActionListener(this::registerStudentAction);
        registerPanel.add(btnRegister, gbc);
        
        tabbedPane.addTab("Register Student", registerPanel);
    }
    
    private void initializeViewParticipantsTab() {
        JPanel participantsPanel = new JPanel(new BorderLayout());
        
        // Top panel for event selection
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Select Event:"));
        cmbEventsForParticipants = new JComboBox<>();
        cmbEventsForParticipants.addActionListener(this::viewParticipantsAction);
        topPanel.add(cmbEventsForParticipants);
        
        // Table for participants
        String[] columnNames = {"Student ID", "Student Name", "Student Email"};
        participantsTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        participantsTable = new JTable(participantsTableModel);
        JScrollPane scrollPane = new JScrollPane(participantsTable);
        
        participantsPanel.add(topPanel, BorderLayout.NORTH);
        participantsPanel.add(scrollPane, BorderLayout.CENTER);
        
        tabbedPane.addTab("View Participants", participantsPanel);
    }
    
    private void setupGUI() {
        add(tabbedPane);
        
        // Add menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }
    
    private void loadInitialData() {
        refreshEventsData();
        loadEventsComboBoxes();
    }
    
    // Event handlers
    private void addEventAction(ActionEvent e) {
        String eventName = txtEventName.getText().trim();
        String eventDate = txtEventDate.getText().trim();
        
        // Validation
        if (eventName.isEmpty() || eventDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields!", 
                                        "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Add event
        if (eventManager.addEvent(eventName, eventDate)) {
            JOptionPane.showMessageDialog(this, "Event added successfully!", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
            clearAddEventFields();
            refreshEventsData();
            loadEventsComboBoxes();
        }
    }
    
    private void refreshEventsAction(ActionEvent e) {
        refreshEventsData();
    }
    
    private void registerStudentAction(ActionEvent e) {
        String studentName = txtStudentName.getText().trim();
        String studentEmail = txtStudentEmail.getText().trim();
        Event selectedEvent = (Event) cmbEvents.getSelectedItem();
        
        // Validation
        if (studentName.isEmpty() || studentEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all student fields!", 
                                        "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (selectedEvent == null) {
            JOptionPane.showMessageDialog(this, "Please select an event!", 
                                        "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Register student
        if (registrationManager.registerStudent(studentName, studentEmail, selectedEvent.getEventId())) {
            JOptionPane.showMessageDialog(this, "Student registered successfully!", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
            clearRegisterStudentFields();
        }
    }
    
    private void viewParticipantsAction(ActionEvent e) {
        Event selectedEvent = (Event) cmbEventsForParticipants.getSelectedItem();
        if (selectedEvent != null) {
            loadParticipantsData(selectedEvent.getEventId());
        }
    }
    
    // Helper methods
    private void clearAddEventFields() {
        txtEventName.setText("");
        txtEventDate.setText("");
    }
    
    private void clearRegisterStudentFields() {
        txtStudentName.setText("");
        txtStudentEmail.setText("");
        cmbEvents.setSelectedIndex(-1);
    }
    
    private void refreshEventsData()
     {
        eventsTableModel.setRowCount(0);
        List<Event> events = eventManager.getAllEvents();
        
        for (Event event : events) {
            eventsTableModel.addRow(new Object[]{
                event.getEventId(),
                event.getEventName(),
                event.getEventDate()
            });
        }
    }
    
    private void loadEventsComboBoxes()
     {
        Event[] events = eventManager.getEventsArray();
        
        // Update register student combo box
        cmbEvents.removeAllItems();
        for (Event event : events) {
            cmbEvents.addItem(event);
        }
        
        // Update view participants combo box
        cmbEventsForParticipants.removeAllItems();
        for (Event event : events) {
            cmbEventsForParticipants.addItem(event);
        }
    }
    
    private void loadParticipantsData(int eventId) {
        participantsTableModel.setRowCount(0);
        List<Student> students = registrationManager.getRegisteredStudents(eventId);
        
        for (Student student : students) {
            participantsTableModel.addRow(new Object[]{
                student.getStudentId(),
                student.getStudentName(),
                student.getStudentEmail()
            });
        }
    }
    
    // Main method
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Create and show GUI
        SwingUtilities.invokeLater(() -> {
            new CollegeEventRegistrationGUI().setVisible(true);
        });
    }
}

/*
Database Setup SQL (Run these commands in phpMyAdmin):

CREATE DATABASE college_event_db;
USE college_event_db;

CREATE TABLE events (
    event_id INT PRIMARY KEY AUTO_INCREMENT,
    event_name VARCHAR(100) NOT NULL,
    event_date DATE NOT NULL,
    event_location VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE students (
    student_id INT PRIMARY KEY AUTO_INCREMENT,
    student_name VARCHAR(100) NOT NULL,
    student_email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE registrations (
    registration_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id INT,
    event_id INT,
    registration_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (event_id) REFERENCES events(event_id),
    UNIQUE KEY unique_registration (student_id, event_id)
);

-- Demo data for testing
INSERT INTO events (event_name, event_date, event_location) VALUES
('Tech Symposium 2024', '2024-03-15', 'Main Auditorium'),
('Cultural Fest', '2024-04-20', 'Campus Grounds'),
('Sports Meet', '2024-05-10', 'Sports Complex');

INSERT INTO students (student_name, student_email) VALUES
('John Doe', 'john.doe@college.edu'),
('Jane Smith', 'jane.smith@college.edu'),
('Mike Johnson', 'mike.johnson@college.edu');

INSERT INTO registrations (student_id, event_id, registration_date) VALUES
(1, 1, '2024-03-01'),
(2, 1, '2024-03-02'),
(1, 2, '2024-03-05');
*/