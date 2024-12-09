import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.Objects;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sound.sampled.*;




public class Main extends JFrame implements ItemListener, ActionListener {

    private JPanel MainPanel;
    private JButton CloseButton;
    private JButton StartButton;
    private JButton RankingButton;

    private Clip clip;
    public Main() {
        createUIComponents();
        add(MainPanel);
    }


    private void createUIComponents() {
        MainPanel = new JPanel();
        MainPanel.setLayout(new BorderLayout());
        setResizable(false);

        // Agrega el icono de la aplicación al panel principal
        URL url2 = getClass().getResource("img/app-icon.png");
        JLabel appIconLabel;

            appIconLabel = new JLabel(new ImageIcon(url2));
            MainPanel.add(appIconLabel, BorderLayout.NORTH);


        // Agregar el fondo al panel principal
        ImageIcon bgIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("img/blurred_heaven_hell.png")));
        JLabel backgroundLabel = new JLabel(bgIcon);
        backgroundLabel.setSize(1000, 700);
        MainPanel.add(backgroundLabel, BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Use BoxLayout for button panel

        StartButton = new JButton("Start");
        StartButton.addActionListener(this);
        StartButton.setFont(StartButton.getFont().deriveFont(Font.BOLD, 18)); // Set font size to 16 and bold
        buttonPanel.add(StartButton);
        buttonPanel.add(Box.createVerticalStrut(10)); // Add vertical space between buttons

        RankingButton = new JButton("Ranking");
        RankingButton.addActionListener(this);
        RankingButton.setFont(RankingButton.getFont().deriveFont(Font.BOLD, 18)); // Set font size to 16 and bold
        buttonPanel.add(RankingButton);
        buttonPanel.add(Box.createVerticalStrut(10)); // Add vertical space between buttons

        CloseButton = new JButton("Close");
        CloseButton.addActionListener(this);
        CloseButton.setFont(CloseButton.getFont().deriveFont(Font.BOLD, 18)); // Set font size to 16 and bold
        buttonPanel.add(CloseButton);
        buttonPanel.add(Box.createVerticalStrut(10)); // Add vertical space between buttons

        JPanel buttonPanelContainer = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Use FlowLayout to center the button panel
        buttonPanelContainer.add(buttonPanel);

        // Add button panel container to the center of main panel, centered
        MainPanel.add(buttonPanelContainer, BorderLayout.CENTER);

        // Mostrar el frame después de que todos los componentes se hayan agregado
        setTitle("GAME: TYPE IT!");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setVisible(true);
    }


    public static void main(String[] args) { // Main method, entry point of app
        new Main(); // Create an instance of Main
    }



    public void playSound(String soundFileName) {
        URL url = this.getClass().getClassLoader().getResource(soundFileName);
        AudioInputStream audioIn;
        try {
            audioIn = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void itemStateChanged(ItemEvent e) {


    }

    String nickname = "";

    @Override
    public void actionPerformed(ActionEvent e) {

        String[] words = {"ciudad", "encrucijada", "fantasma", "laberinto", "misterio", "domótica",
                "desesperación", "clandestino", "espectral", "abismo", "microprocesador", "enigma", "sombrío",
                "biometría", "escalofrío", "intriga", "programación", "oscuridad", "terrorífico", "suspenso", "secreto"};

        if (e.getSource() == CloseButton) {
            System.exit(0);
        }
        if (e.getSource() == StartButton) {

            String playerName = "";
            final boolean[] exit = {false};
            do {
                JTextField playerNameField = new JTextField();
                Object[] message = {
                        "Enter your name:", playerNameField
                };
                JOptionPane optionPane = new JOptionPane(message, JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION);
                JDialog dialog = optionPane.createDialog(this, "Enter your name");
                dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
                dialog.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowOpened(WindowEvent e) {
                        super.windowOpened(e);
                        playerNameField.requestFocusInWindow();
                    }

                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        dialog.dispose();
                    }


                });
                dialog.setVisible(true);
                if (optionPane.getValue() != null) {
                    if ((int) optionPane.getValue() == JOptionPane.OK_OPTION) {
                        playerName = playerNameField.getText();
                        nickname = playerName;
                        if (playerName != null && !playerName.trim().isEmpty()) {
                            System.out.println("Player name: " + playerName);
                            DatabaseHandler.insertPlayer(playerName); // Insert player into the database
                        }

                    }//if player choose cancel option or close the windows then go home screen
                    else if ((int) optionPane.getValue() == JOptionPane.CANCEL_OPTION || (int) optionPane.getValue() == JOptionPane.CLOSED_OPTION) {
                        exit[0] = true;
                        break;
                    }
                } else {
                    playerName = null;
                }
            } while (playerName == null || playerName.trim().isEmpty());

            if (exit[0]) {
                new Main();
                dispose();
            }else{
                new Game(words);
                dispose();
            }


        }
        if (e.getSource() == RankingButton) {
            new Ranking();

            dispose();
        }

    }





    public class Game  implements  ActionListener {

        private JLabel label;
        private ImageIcon bgIcon;
        private JFrame frame;
        private JTextField textField;

        private JButton HomeButton;
        private String[] words;
        private int currentWordIndex;
        private JLabel TimeLabel;
        private AtomicInteger time;
        private Timer timer;




        public Game(String[] words) {
            this.words = words;
            currentWordIndex = 0;
            time = new AtomicInteger(31);
            initializeUI();
            playSound("called-to-win-30sec-195403.wav");
        }

        private void initializeUI() {
            // Crear el icono del fondo
            bgIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("img/heaven_hell.png")));
            label = new JLabel(bgIcon);
            label.setSize(1000, 700);
            setResizable(false);

            // Crear el JFrame
            frame = new JFrame("Type the words correctly! (DON'T FORGET THE ACCENTS)");
            frame.add(label);
            frame.setSize(1000, 700);
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());

            // Crear el JTextField
            textField = new JTextField();
            textField.setFont(new Font("Arial", Font.PLAIN, 24));
            frame.add(textField, BorderLayout.SOUTH);

            // Establecer el foco en el JTextField cuando el juego inicie
            frame.addWindowListener(new java.awt.event.WindowAdapter() {
                public void windowOpened(java.awt.event.WindowEvent windowEvent) {
                    textField.requestFocus();
                }
            });

            // Agregar un ActionListener al JTextField para verificar la palabra cuando se presione Enter
            textField.addActionListener(e -> checkWord());

            // Initialize HomeButton
            HomeButton = new JButton("🏠");
            HomeButton.addActionListener(this);
            HomeButton.setFont(HomeButton.getFont().deriveFont(Font.PLAIN, 18));

            // Initialize TimeLabel
            TimeLabel = new JLabel("Time left: " + time);
            TimeLabel.setFont(TimeLabel.getFont().deriveFont(Font.PLAIN, 18));


            // Create a panel for the HomeButton and TimeLabel
            JPanel buttonPanel = new JPanel(new BorderLayout());

            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            leftPanel.add(TimeLabel);

            JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            rightPanel.add(HomeButton);

            buttonPanel.add(leftPanel, BorderLayout.WEST);
            buttonPanel.add(rightPanel, BorderLayout.EAST);

            // Add the panel with HomeButton and TimeLabel to the NORTH of the frame
            frame.add(buttonPanel, BorderLayout.NORTH);

            // Create a Timer that triggers every second
            timer = new Timer(1000, e -> {
                time.getAndDecrement(); // Decrementar el tiempo

                if (time.get() >= 0) {
                    TimeLabel.setText("Time left: " + time + " seconds");
                } else {
                    ((Timer) e.getSource()).stop();
                    frame.dispose();
                    new GameOver();
                    JOptionPane.showMessageDialog(frame, "Time's up! ⏰ ", "Game over", JOptionPane.WARNING_MESSAGE);
                }
            });

            timer.setRepeats(true);
            timer.start();

            frame.setVisible(true);
        }



        private HashSet<String> typedWords = new HashSet<>();

        private void checkWord() {
            String typedWord = textField.getText().trim();

            // Check if the word has already been typed
            if (typedWords.contains(typedWord)) {
                JOptionPane.showMessageDialog(frame, "You already typed this word! 🙃", "Message", JOptionPane.WARNING_MESSAGE);
                textField.setText(""); // Clear the JTextField
                return; // Exit the method early
            }

            // Search for the word written by the user in the array of words
            boolean wordFound = false;
            for (String word : words) {
                if (typedWord.equals(word)) {
                    wordFound = true;
                    break;
                }
            }

            if (wordFound) {
                JOptionPane.showMessageDialog(frame, "Correct! ✅", "Message", JOptionPane.INFORMATION_MESSAGE);
                typedWords.add(typedWord); // Add the typed word to the set of typed words
                // Increment the player's score in the database
                try (Connection connection = DriverManager.getConnection(DatabaseHandler.JDBC_URL, DatabaseHandler.USERNAME, DatabaseHandler.PASSWORD)) {
                    String query = "UPDATE matches SET score = score + 10 WHERE player_id = (SELECT id FROM players WHERE nickname = ? LIMIT 1)";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, nickname);
                    statement.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                currentWordIndex++;
                if (currentWordIndex < words.length) {
                    textField.setText(""); // Clear the JTextField
                    textField.requestFocus(); // Request focus for the text field
                    textField.setCaretPosition(0);
                } else {
                    JOptionPane.showMessageDialog(frame, "You have typed all the words! 🎉", "Message", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                    new Main();
                }
            } else {
                JOptionPane.showMessageDialog(frame, "Incorrect. Try again.", "Message", JOptionPane.ERROR_MESSAGE);
            }
        }







        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == HomeButton) {
                timer.stop(); // Stop the timer
                time.set(31); // Reset the time
                if (clip != null && clip.isRunning()) {
                    clip.stop();
                }
                frame.dispose();
                new Main();

            }

        }
    }

    public class GameOver implements ActionListener {

        private JButton HomeButton;
        private JLabel label;
        private JFrame frame;


        public GameOver() {


            ImageIcon bgIcon;
            bgIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("img/hell.jpg")));
            label = new JLabel(bgIcon);
            label.setSize(1000, 700);


            frame = new JFrame("Game Over");
            frame.add(label);
            frame.setSize(1000, 700);
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());


            // Initialize HomeButton
            HomeButton = new JButton("Home");
            HomeButton.addActionListener(this);
            HomeButton.setFont(HomeButton.getFont().deriveFont(Font.BOLD, 18)); // Set font size to 18 and bold

// Create a panel for the HomeButton
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Use FlowLayout to center the button
            buttonPanel.add(HomeButton);

// Add buttonPanel to the SOUTH of the frame
            frame.add(buttonPanel, BorderLayout.SOUTH);

            frame.setVisible(true);


        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == HomeButton) {
                frame.dispose();
                new Main();
            }
        }
    }


    public class Ranking implements ActionListener {

        private JButton HomeButton;
        private JLabel label;
        private JFrame frame;
        private JTable table;

        public Ranking() {
            ImageIcon bgIcon;
            bgIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("img/heaven.jpg")));
            label = new JLabel(bgIcon);
            label.setSize(1000, 700);

            frame = new JFrame("Ranking");
            frame.add(label);
            frame.setSize(1000, 700);
            frame.setResizable(false);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());

            // Initialize HomeButton
            HomeButton = new JButton("Home");
            HomeButton.addActionListener(this);
            HomeButton.setFont(HomeButton.getFont().deriveFont(Font.BOLD, 18)); // Set font size to 18 and bold

            // Create a panel for the HomeButton
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Use FlowLayout to center the button
            buttonPanel.add(HomeButton);
            //transparent
            buttonPanel.setOpaque(false);
            // Add buttonPanel to the NORTH of the frame
            frame.add(buttonPanel, BorderLayout.NORTH);

            // Create table and add to JScrollPane
            table = new JTable();
            JScrollPane scrollPane = new JScrollPane(table);

            // Set the preferred size of the viewport, not the JScrollPane itself
            scrollPane.getViewport().setPreferredSize(new Dimension(700, 400)); // Adjust the width and height as needed

            // Load data from database
            loadDataFromDatabase();

            // Create a panel for the button and table
            JPanel buttonAndTablePanel = new JPanel();
            buttonAndTablePanel.setLayout(new BorderLayout());

            // Add the button panel to the top of the BorderLayout panel
            buttonAndTablePanel.add(buttonPanel, BorderLayout.SOUTH);

            // Add the JScrollPane to the center of the button and table panel
            buttonAndTablePanel.add(scrollPane, BorderLayout.CENTER);

            // Add the panel containing the button and table to the background label using GridBagLayout for centering
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.CENTER;
            label.setLayout(new GridBagLayout());
            label.add(buttonAndTablePanel, gbc);

            frame.setVisible(true);
        }


        private void loadDataFromDatabase() {
            try (Connection connection = DriverManager.getConnection(DatabaseHandler.JDBC_URL, DatabaseHandler.USERNAME, DatabaseHandler.PASSWORD)) {
                String query = "SELECT p.nickname, m.score, m.date FROM matches m JOIN players p ON m.player_id = p.id ORDER BY m.score DESC";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();

                DefaultTableModel model = new DefaultTableModel();
                ResultSetMetaData metaData = resultSet.getMetaData();

                model.addColumn("place"); // Add the new column

                int columnCount = metaData.getColumnCount();
                for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                    model.addColumn(metaData.getColumnName(columnIndex));
                }

                int place = 1; // Initialize place counter
                while (resultSet.next()) {
                    Object[] rowData = new Object[columnCount + 1]; // +1 for the new column
                    rowData[0] = place++; // Set place and increment it for the next row
                    for (int i = 0; i < columnCount; i++) {
                        rowData[i + 1] = resultSet.getObject(i + 1); // +1 to leave space for the place column
                    }
                    model.addRow(rowData);
                }

                table.setModel(model);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == HomeButton) {
                frame.dispose();
                new Main();
            }
        }
    }



    static class DatabaseHandler {
        static final String JDBC_URL = "jdbc:sqlite:game-db.db";
        static final String USERNAME = ""; //USER
        static final String PASSWORD = ""; //PASSWORD

        public static void insertPlayer(String playerName) {
            try (Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD)) {

                // Check if player already exists
                String checkQuery = "SELECT COUNT(*) FROM players WHERE nickname = ?";
                PreparedStatement checkStatement = connection.prepareStatement(checkQuery);
                checkStatement.setString(1, playerName);
                ResultSet checkResult = checkStatement.executeQuery();
                if (checkResult.next() && checkResult.getInt(1) == 0) {

                    // Player does not exist, insert new player
                    String query = "INSERT INTO players (nickname) VALUES (?)";
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, playerName);
                    statement.executeUpdate();
                }

                // Insert into matches

                String query2 = "INSERT INTO matches (player_id, score, date) VALUES ((SELECT id FROM players WHERE nickname = ? LIMIT 1), 0, datetime('now') )";
                PreparedStatement statement2 = connection.prepareStatement(query2);
                statement2.setString(1, playerName);
                statement2.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}




