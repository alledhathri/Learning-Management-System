import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;

public class DesktopLearningManagementSystem extends JFrame {
    private ArrayList<Course> courses;
    private JPanel mainPanel;
    private JPanel contentPanel;
    private JList<String> courseList;
    private DefaultListModel<String> courseListModel;
    private Course selectedCourse;
    private CardLayout cardLayout;
    private JLabel titleLabel;
    private JProgressBar overallProgressBar;
    private int currentQuizIndex = 0;
    private int quizScore = 0;

    public DesktopLearningManagementSystem() {
        setTitle("Learning Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        courses = initializeCourses();
        buildUI();
        updateProgress();
        setVisible(true);
    }

    private void buildUI() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        JPanel headerPanel = buildHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(245, 245, 245));

        JPanel sidebarPanel = buildSidebarPanel();
        centerPanel.add(sidebarPanel, BorderLayout.WEST);

        cardLayout = new CardLayout() {
            @Override
            public void show(Container parent, String name) {
                if (name.equals("DASHBOARD")) {
                    JPanel dashboardWrapper = (JPanel) parent.getComponent(0);
                    JPanel dashboardPanel = (JPanel) dashboardWrapper.getClientProperty("dashboardPanel");
                    if (dashboardPanel != null) {
                        dashboardPanel.removeAll();
                        
                        JLabel dashboardTitle = new JLabel("Course Dashboard");
                        dashboardTitle.setFont(new Font("Arial", Font.BOLD, 20));
                        dashboardPanel.add(dashboardTitle);
                        dashboardPanel.add(Box.createVerticalStrut(20));

                        for (Course course : courses) {
                            JPanel courseCard = buildCourseCard(course);
                            dashboardPanel.add(courseCard);
                            dashboardPanel.add(Box.createVerticalStrut(15));
                        }

                        dashboardPanel.add(Box.createVerticalGlue());
                        dashboardPanel.revalidate();
                        dashboardPanel.repaint();
                    }
                }
                super.show(parent, name);
            }
        };
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(buildDashboardPanel(), "DASHBOARD");
        contentPanel.add(buildCourseDetailsPanel(), "DETAILS");
        contentPanel.add(buildQuizPanel(), "QUIZ");
        centerPanel.add(contentPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel buildHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
        headerPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        titleLabel = new JLabel("Learning Management System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel progressWrapper = new JPanel(new BorderLayout());
        progressWrapper.setOpaque(false);
        progressWrapper.setBorder(new EmptyBorder(0, 0, 0, 20));

        JLabel progressLabel = new JLabel("Overall Progress:");
        progressLabel.setForeground(Color.WHITE);
        progressLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        overallProgressBar = new JProgressBar(0, 100);
        overallProgressBar.setValue(0);
        overallProgressBar.setPreferredSize(new Dimension(200, 25));
        overallProgressBar.setStringPainted(true);

        progressWrapper.add(progressLabel, BorderLayout.WEST);
        progressWrapper.add(overallProgressBar, BorderLayout.CENTER);

        headerPanel.add(progressWrapper, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel buildSidebarPanel() {
        JPanel sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(new Color(230, 230, 230));
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setBorder(new MatteBorder(0, 0, 0, 1, new Color(180, 180, 180)));

        JLabel coursesLabel = new JLabel("Courses");
        coursesLabel.setFont(new Font("Arial", Font.BOLD, 16));
        coursesLabel.setBorder(new EmptyBorder(15, 15, 15, 15));
        sidebarPanel.add(coursesLabel, BorderLayout.NORTH);

        courseListModel = new DefaultListModel<>();
        for (Course course : courses) {
            courseListModel.addElement(course.getName());
        }

        courseList = new JList<>(courseListModel);
        courseList.setBackground(new Color(240, 240, 240));
        courseList.setFont(new Font("Arial", Font.PLAIN, 13));
        courseList.setBorder(new EmptyBorder(0, 10, 0, 10));
        courseList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        courseList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = courseList.getSelectedIndex();
                if (selectedIndex >= 0) {
                    selectedCourse = courses.get(selectedIndex);
                    JPanel detailsPanel = (JPanel) contentPanel.getComponent(1);
                    JLabel title = (JLabel) detailsPanel.getClientProperty("titleLabel");
                    JPanel lessons = (JPanel) detailsPanel.getClientProperty("lessonPanel");
                    
                    if (title != null && lessons != null) {
                        title.setText(selectedCourse.getName() + " - Lessons");
                        lessons.removeAll();

                        for (Lesson lesson : selectedCourse.getLessons()) {
                            JPanel lessonItem = buildLessonItem(lesson);
                            lessons.add(lessonItem);
                            lessons.add(Box.createVerticalStrut(10));
                        }

                        lessons.add(Box.createVerticalGlue());
                        lessons.revalidate();
                        lessons.repaint();
                    }
                    
                    cardLayout.show(contentPanel, "DETAILS");
                    contentPanel.repaint();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(courseList);
        scrollPane.setBackground(new Color(240, 240, 240));
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        sidebarPanel.add(scrollPane, BorderLayout.CENTER);

        JButton dashboardBtn = new JButton("Dashboard");
        dashboardBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        dashboardBtn.setBackground(new Color(52, 152, 219));
        dashboardBtn.setForeground(Color.WHITE);
        dashboardBtn.setBorder(new EmptyBorder(8, 15, 8, 15));
        dashboardBtn.setFocusPainted(false);
        dashboardBtn.addActionListener(e -> cardLayout.show(contentPanel, "DASHBOARD"));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(230, 230, 230));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottomPanel.add(dashboardBtn, BorderLayout.CENTER);

        sidebarPanel.add(bottomPanel, BorderLayout.SOUTH);

        return sidebarPanel;
    }

    private JPanel buildDashboardPanel() {
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new BoxLayout(dashboardPanel, BoxLayout.Y_AXIS));
        dashboardPanel.setBackground(Color.WHITE);
        dashboardPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel dashboardTitle = new JLabel("Course Dashboard");
        dashboardTitle.setFont(new Font("Arial", Font.BOLD, 20));
        dashboardPanel.add(dashboardTitle);
        dashboardPanel.add(Box.createVerticalStrut(20));

        for (Course course : courses) {
            JPanel courseCard = buildCourseCard(course);
            dashboardPanel.add(courseCard);
            dashboardPanel.add(Box.createVerticalStrut(15));
        }

        dashboardPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(dashboardPanel);
        scrollPane.setBorder(null);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);
        wrapper.add(scrollPane, BorderLayout.CENTER);

        wrapper.putClientProperty("dashboardPanel", dashboardPanel);
        wrapper.putClientProperty("scrollPane", scrollPane);

        return wrapper;
    }

    private JPanel buildCourseCard(Course course) {
        JPanel cardPanel = new JPanel(new BorderLayout());
        cardPanel.setBackground(new Color(240, 248, 255));
        cardPanel.setBorder(new LineBorder(new Color(200, 220, 240), 2));
        cardPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        cardPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 220, 240), 1),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel courseNameLabel = new JLabel(course.getName());
        courseNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        leftPanel.add(courseNameLabel);

        JLabel statusLabel = new JLabel("Status: " + course.getStatus());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(100, 100, 100));
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(statusLabel);

        cardPanel.add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setOpaque(false);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(course.getProgress());
        progressBar.setStringPainted(true);
        progressBar.setPreferredSize(new Dimension(150, 25));
        rightPanel.add(progressBar, BorderLayout.CENTER);

        JLabel progressLabel = new JLabel(course.getProgress() + "%");
        progressLabel.setFont(new Font("Arial", Font.BOLD, 12));
        progressLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        rightPanel.add(progressLabel, BorderLayout.EAST);

        cardPanel.add(rightPanel, BorderLayout.EAST);

        return cardPanel;
    }

    private JPanel buildCourseDetailsPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel();
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(new EmptyBorder(20, 20, 10, 20));

        JPanel lessonPanel = new JPanel();
        lessonPanel.setLayout(new BoxLayout(lessonPanel, BoxLayout.Y_AXIS));
        lessonPanel.setBackground(Color.WHITE);
        lessonPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        JScrollPane scrollPane = new JScrollPane(lessonPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton quizBtn = new JButton("Take Quiz");
        quizBtn.setFont(new Font("Arial", Font.BOLD, 12));
        quizBtn.setBackground(new Color(46, 204, 113));
        quizBtn.setForeground(Color.WHITE);
        quizBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        quizBtn.setFocusPainted(false);
        quizBtn.addActionListener(e -> {
            if (selectedCourse != null && selectedCourse.getAllLessonsCompleted()) {
                currentQuizIndex = 0;
                quizScore = 0;
                cardLayout.show(contentPanel, "QUIZ");
                JPanel quizPanel = (JPanel) contentPanel.getComponent(2);
                JPanel quizContent = (JPanel) quizPanel.getClientProperty("contentPanel");
                if (quizContent != null) {
                    displayQuizQuestion(quizContent);
                }
            } else {
                JOptionPane.showMessageDialog(DesktopLearningManagementSystem.this, "Complete all lessons before taking the quiz.", "Cannot Take Quiz", JOptionPane.WARNING_MESSAGE);
            }
        });

        JButton backBtn = new JButton("Back");
        backBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        backBtn.setBackground(new Color(149, 165, 166));
        backBtn.setForeground(Color.WHITE);
        backBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        backBtn.setFocusPainted(false);
        backBtn.addActionListener(e -> cardLayout.show(contentPanel, "DASHBOARD"));

        buttonPanel.add(quizBtn);
        buttonPanel.add(backBtn);

        wrapper.add(titleLabel, BorderLayout.NORTH);
        wrapper.add(scrollPane, BorderLayout.CENTER);
        wrapper.add(buttonPanel, BorderLayout.SOUTH);

        wrapper.putClientProperty("titleLabel", titleLabel);
        wrapper.putClientProperty("lessonPanel", lessonPanel);

        return wrapper;
    }

    private JPanel buildLessonItem(Lesson lesson) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBackground(new Color(250, 250, 250));
        itemPanel.setBorder(new CompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1),
            new EmptyBorder(12, 12, 12, 12)
        ));
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel lessonName = new JLabel(lesson.getTitle());
        lessonName.setFont(new Font("Arial", Font.BOLD, 13));
        leftPanel.add(lessonName);

        JLabel statusLabel = new JLabel(lesson.isCompleted() ? "✓ Completed" : "Not Completed");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusLabel.setForeground(lesson.isCompleted() ? new Color(46, 204, 113) : new Color(149, 165, 166));
        leftPanel.add(statusLabel);

        itemPanel.add(leftPanel, BorderLayout.WEST);

        JButton completeBtn = new JButton(lesson.isCompleted() ? "Completed" : "Mark Complete");
        completeBtn.setFont(new Font("Arial", Font.PLAIN, 11));
        completeBtn.setBackground(lesson.isCompleted() ? new Color(46, 204, 113) : new Color(52, 152, 219));
        completeBtn.setForeground(Color.WHITE);
        completeBtn.setBorder(new EmptyBorder(6, 12, 6, 12));
        completeBtn.setFocusPainted(false);
        completeBtn.setEnabled(!lesson.isCompleted());

        completeBtn.addActionListener(e -> {
            lesson.setCompleted(true);
            completeBtn.setText("Completed");
            completeBtn.setBackground(new Color(46, 204, 113));
            completeBtn.setEnabled(false);
            statusLabel.setText("✓ Completed");
            statusLabel.setForeground(new Color(46, 204, 113));
            itemPanel.repaint();
            updateProgress();
        });

        itemPanel.add(completeBtn, BorderLayout.EAST);

        return itemPanel;
    }

    private JPanel buildQuizPanel() {
        JPanel quizPanel = new JPanel(new BorderLayout());
        quizPanel.setBackground(Color.WHITE);
        quizPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        JLabel quizTitle = new JLabel("Quiz");
        quizTitle.setFont(new Font("Arial", Font.BOLD, 22));
        quizPanel.add(quizTitle, BorderLayout.NORTH);

        JPanel quizContent = new JPanel();
        quizContent.setLayout(new BoxLayout(quizContent, BoxLayout.Y_AXIS));
        quizContent.setBackground(Color.WHITE);
        quizContent.setBorder(new EmptyBorder(20, 0, 20, 0));

        JScrollPane scrollPane = new JScrollPane(quizContent);
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        quizPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        bottomPanel.setBackground(new Color(245, 245, 245));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton submitBtn = new JButton("Submit Answer");
        submitBtn.setFont(new Font("Arial", Font.BOLD, 12));
        submitBtn.setBackground(new Color(52, 152, 219));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        submitBtn.setFocusPainted(false);

        JButton exitQuizBtn = new JButton("Exit Quiz");
        exitQuizBtn.setFont(new Font("Arial", Font.PLAIN, 12));
        exitQuizBtn.setBackground(new Color(149, 165, 166));
        exitQuizBtn.setForeground(Color.WHITE);
        exitQuizBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        exitQuizBtn.setFocusPainted(false);
        exitQuizBtn.addActionListener(e -> cardLayout.show(contentPanel, "DETAILS"));

        submitBtn.addActionListener(e -> {
            if (selectedCourse != null) {
                Quiz[] quizzes = selectedCourse.getQuizzes();
                if (currentQuizIndex < quizzes.length) {
                    boolean answered = false;
                    for (Component comp : quizContent.getComponents()) {
                        if (comp instanceof JRadioButton && ((JRadioButton) comp).isSelected()) {
                            String selectedAnswer = ((JRadioButton) comp).getText();
                            if (selectedAnswer.equals(quizzes[currentQuizIndex].getCorrectAnswer())) {
                                quizScore++;
                            }
                            answered = true;
                            break;
                        }
                    }
                    
                    if (!answered) {
                        JOptionPane.showMessageDialog(DesktopLearningManagementSystem.this, "Please select an answer.", "No Answer Selected", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    currentQuizIndex++;
                    if (currentQuizIndex < quizzes.length) {
                        displayQuizQuestion(quizContent);
                        quizPanel.repaint();
                    } else {
                        showQuizResults(quizzes.length);
                        selectedCourse.completeQuiz();
                        updateProgress();
                        cardLayout.show(contentPanel, "DETAILS");
                    }
                }
            }
        });

        bottomPanel.add(submitBtn);
        bottomPanel.add(exitQuizBtn);

        quizPanel.add(bottomPanel, BorderLayout.SOUTH);
        quizPanel.putClientProperty("contentPanel", quizContent);

        return quizPanel;
    }

    private void displayQuizQuestion(JPanel contentPanel) {
        contentPanel.removeAll();

        Quiz[] quizzes = selectedCourse.getQuizzes();
        Quiz currentQuiz = quizzes[currentQuizIndex];

        JLabel questionLabel = new JLabel((currentQuizIndex + 1) + ". " + currentQuiz.getQuestion());
        questionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        questionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(questionLabel);
        contentPanel.add(Box.createVerticalStrut(15));

        ButtonGroup buttonGroup = new ButtonGroup();
        for (String option : currentQuiz.getOptions()) {
            JRadioButton radioButton = new JRadioButton(option);
            radioButton.setFont(new Font("Arial", Font.PLAIN, 12));
            radioButton.setBackground(Color.WHITE);
            radioButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            buttonGroup.add(radioButton);
            contentPanel.add(radioButton);
            contentPanel.add(Box.createVerticalStrut(10));
        }

        contentPanel.add(Box.createVerticalGlue());
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showQuizResults(int totalQuestions) {
        int percentage = (quizScore * 100) / totalQuestions;
        String message = "Quiz Completed!\n\nScore: " + quizScore + " / " + totalQuestions + "\nPercentage: " + percentage + "%";
        JOptionPane.showMessageDialog(this, message, "Quiz Results", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateProgress() {
        int totalLessons = 0;
        int completedLessons = 0;

        for (Course course : courses) {
            for (Lesson lesson : course.getLessons()) {
                totalLessons++;
                if (lesson.isCompleted()) {
                    completedLessons++;
                }
            }

            if (course.isQuizCompleted() && course.getAllLessonsCompleted()) {
                course.setStatus("Completed");
            } else if (course.getAllLessonsCompleted() || course.isQuizCompleted()) {
                course.setStatus("In Progress");
            }

            int lessonCount = course.getLessons().length;
            int completed = 0;
            for (Lesson lesson : course.getLessons()) {
                if (lesson.isCompleted()) completed++;
            }
            int progress = (completed * 100) / lessonCount;
            course.setProgress(progress);
        }

        int overallProgress = (totalLessons > 0) ? (completedLessons * 100) / totalLessons : 0;
        overallProgressBar.setValue(overallProgress);
        overallProgressBar.setString(overallProgress + "%");

        courseList.repaint();
    }

    private ArrayList<Course> initializeCourses() {
        ArrayList<Course> courseList = new ArrayList<>();

        Lesson[] javaLessons = {
            new Lesson("Introduction to Java"),
            new Lesson("Variables and Data Types"),
            new Lesson("Control Flow Statements"),
            new Lesson("Object-Oriented Programming")
        };
        Quiz[] javaQuizzes = {
            new Quiz("What is the correct syntax for a method?", new String[]{"public void method()", "void public method()", "void method public()", "method void public()"}, "public void method()"),
            new Quiz("Which keyword is used to create an object?", new String[]{"new", "create", "make", "init"}, "new"),
            new Quiz("What does OOP stand for?", new String[]{"Object-Oriented Programming", "Output-Oriented Programming", "Optimized-Oriented Programming", "Online-Oriented Programming"}, "Object-Oriented Programming"),
            new Quiz("Which of these is NOT a primitive type?", new String[]{"int", "String", "double", "boolean"}, "String"),
            new Quiz("What is the default value of an int variable?", new String[]{"0", "null", "undefined", "-1"}, "0")
        };
        courseList.add(new Course("Java", javaLessons, javaQuizzes));

        Lesson[] dsaLessons = {
            new Lesson("Arrays and Lists"),
            new Lesson("Stacks and Queues"),
            new Lesson("Trees and Graphs"),
            new Lesson("Sorting Algorithms")
        };
        Quiz[] dsaQuizzes = {
            new Quiz("What is the time complexity of binary search?", new String[]{"O(log n)", "O(n)", "O(n^2)", "O(1)"}, "O(log n)"),
            new Quiz("Which data structure follows LIFO?", new String[]{"Stack", "Queue", "Array", "Tree"}, "Stack"),
            new Quiz("What is the worst-case time complexity of quicksort?", new String[]{"O(n^2)", "O(n log n)", "O(n)", "O(log n)"}, "O(n^2)"),
            new Quiz("In a binary search tree, what property must hold?", new String[]{"Left < Root < Right", "All nodes are sorted", "Random order", "Depends on implementation"}, "Left < Root < Right"),
            new Quiz("What is the space complexity of merge sort?", new String[]{"O(n)", "O(log n)", "O(1)", "O(n^2)"}, "O(n)")
        };
        courseList.add(new Course("DSA", dsaLessons, dsaQuizzes));

        Lesson[] dbmsLessons = {
            new Lesson("Database Fundamentals"),
            new Lesson("SQL Basics"),
            new Lesson("Normalization"),
            new Lesson("Indexing and Query Optimization")
        };
        Quiz[] dbmsQuizzes = {
            new Quiz("What does ACID stand for?", new String[]{"Atomicity, Consistency, Isolation, Durability", "Accuracy, Control, Input, Data", "Algorithm, Code, Interface, Database", "Async, Cache, Index, Data"}, "Atomicity, Consistency, Isolation, Durability"),
            new Quiz("Which normal form removes all transitive dependencies?", new String[]{"3NF", "1NF", "2NF", "BCNF"}, "3NF"),
            new Quiz("What is a PRIMARY KEY used for?", new String[]{"Uniquely identify each record", "Sort records", "Filter data", "Create relationships"}, "Uniquely identify each record"),
            new Quiz("Which SQL keyword is used to retrieve data?", new String[]{"SELECT", "GET", "RETRIEVE", "FETCH"}, "SELECT"),
            new Quiz("What does an INDEX improve?", new String[]{"Query performance", "Storage space", "Data security", "Data integrity"}, "Query performance")
        };
        courseList.add(new Course("DBMS", dbmsLessons, dbmsQuizzes));

        Lesson[] osLessons = {
            new Lesson("Operating System Basics"),
            new Lesson("Process Management"),
            new Lesson("Memory Management"),
            new Lesson("File Systems")
        };
        Quiz[] osQuizzes = {
            new Quiz("What is a process?", new String[]{"Running instance of a program", "Part of RAM", "A file extension", "System setting"}, "Running instance of a program"),
            new Quiz("What does CPU scheduling do?", new String[]{"Allocates CPU time to processes", "Manages memory", "Controls devices", "Handles interrupts"}, "Allocates CPU time to processes"),
            new Quiz("Which is a memory management technique?", new String[]{"Paging", "Caching", "Threading", "Compilation"}, "Paging"),
            new Quiz("What is virtual memory?", new String[]{"Memory space using disk storage", "RAM backup", "GPU memory", "Cache memory"}, "Memory space using disk storage"),
            new Quiz("What is thrashing?", new String[]{"Excessive page swapping reducing performance", "System crash", "Memory leak", "Disk failure"}, "Excessive page swapping reducing performance")
        };
        courseList.add(new Course("OS", osLessons, osQuizzes));

        Lesson[] cnLessons = {
            new Lesson("Network Fundamentals"),
            new Lesson("OSI Model"),
            new Lesson("TCP/IP Protocol Suite"),
            new Lesson("Network Security")
        };
        Quiz[] cnQuizzes = {
            new Quiz("How many layers does the OSI model have?", new String[]{"7", "5", "4", "8"}, "7"),
            new Quiz("What is the primary function of the transport layer?", new String[]{"End-to-end communication", "Routing", "Physical transmission", "Encryption"}, "End-to-end communication"),
            new Quiz("Which protocol operates at the application layer?", new String[]{"HTTP", "IP", "Ethernet", "PPP"}, "HTTP"),
            new Quiz("What does TCP ensure that UDP does not?", new String[]{"Reliable delivery", "Speed", "Lower latency", "Broadcasting"}, "Reliable delivery"),
            new Quiz("Which is NOT a valid IPv4 address?", new String[]{"256.1.1.1", "192.168.1.1", "10.0.0.1", "172.16.0.1"}, "256.1.1.1")
        };
        courseList.add(new Course("CN", cnLessons, cnQuizzes));

        return courseList;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DesktopLearningManagementSystem());
    }

    static class Course {
        private String name;
        private Lesson[] lessons;
        private Quiz[] quizzes;
        private int progress;
        private String status;
        private boolean quizCompleted;

        public Course(String name, Lesson[] lessons, Quiz[] quizzes) {
            this.name = name;
            this.lessons = lessons;
            this.quizzes = quizzes;
            this.progress = 0;
            this.status = "Not Started";
            this.quizCompleted = false;
        }

        public String getName() { return name; }
        public Lesson[] getLessons() { return lessons; }
        public Quiz[] getQuizzes() { return quizzes; }
        public int getProgress() { return progress; }
        public void setProgress(int progress) { this.progress = progress; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public boolean isQuizCompleted() { return quizCompleted; }
        public void completeQuiz() { this.quizCompleted = true; }
        public boolean getAllLessonsCompleted() {
            for (Lesson lesson : lessons) {
                if (!lesson.isCompleted()) return false;
            }
            return true;
        }
    }

    static class Lesson {
        private String title;
        private boolean completed;

        public Lesson(String title) {
            this.title = title;
            this.completed = false;
        }

        public String getTitle() { return title; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }

    static class Quiz {
        private String question;
        private String[] options;
        private String correctAnswer;

        public Quiz(String question, String[] options, String correctAnswer) {
            this.question = question;
            this.options = options;
            this.correctAnswer = correctAnswer;
        }

        public String getQuestion() { return question; }
        public String[] getOptions() { return options; }
        public String getCorrectAnswer() { return correctAnswer; }
    }
}
