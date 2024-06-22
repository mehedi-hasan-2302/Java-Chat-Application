import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Authentication extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public Authentication() {
        setTitle("Login or Register");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();

        setSize(350, 180);
        setLocationRelativeTo(null); // Center the frame on screen
        setResizable(false); // Prevent resizing for simplicity
        setVisible(true);
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(10, 10, 10, 10);
        constraints.fill = GridBagConstraints.HORIZONTAL;

        JLabel usernameLabel = new JLabel("Username:");
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(usernameLabel, constraints);

        usernameField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridwidth = 2;
        panel.add(usernameField, constraints);

        JLabel passwordLabel = new JLabel("Password:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        panel.add(passwordLabel, constraints);

        passwordField = new JPasswordField(20);
        constraints.gridx = 1;
        constraints.gridwidth = 2;
        panel.add(passwordField, constraints);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        constraints.gridx = 1;
        constraints.gridy = 2;
        constraints.gridwidth = 1;
        panel.add(loginButton, constraints);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                register();
            }
        });
        constraints.gridx = 2;
        panel.add(registerButton, constraints);

        getContentPane().add(panel, BorderLayout.CENTER);
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Check if username or password is empty
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username or password cannot be empty.");
            return;
        }

        Connection connection = Server.getConnection();
        if (connection != null) {
            try {
                // Query the database to check username and password
                String query = "SELECT * FROM users WHERE UserName = ? AND Password = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    // Successful login
                    JOptionPane.showMessageDialog(this, "Login successful!");

                    // Proceed to open chat application UI
                    Client chatUI = new Client(username);
                    chatUI.setVisible(true);

                    // Close login window
                    dispose();
                } else {
                    // Incorrect username or password
                    JOptionPane.showMessageDialog(this, "Incorrect username or password.");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error occurred while logging in. Please try again.");
            } finally {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void register() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        // Check if username or password is empty
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username or password cannot be empty.");
            return;
        }

        Connection connection = Server.getConnection();
        if (connection != null) {
            try {
                // Check if the username already exists
                String query = "SELECT * FROM users WHERE UserName = ?";
                PreparedStatement checkStatement = connection.prepareStatement(query);
                checkStatement.setString(1, username);
                ResultSet resultSet = checkStatement.executeQuery();

                if (resultSet.next()) {
                    JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different one.");
                    return;
                }


                String insertQuery = "INSERT INTO users (UserName, Password) VALUES (?, ?)";
                PreparedStatement insertStatement = connection.prepareStatement(insertQuery);
                insertStatement.setString(1, username);
                insertStatement.setString(2, password);
                insertStatement.executeUpdate();

                JOptionPane.showMessageDialog(this, "Registration successful!");


                usernameField.setText("");
                passwordField.setText("");

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error occurred while registering. Please try again.");
            } finally {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            new Authentication();
        });
    }
}
