import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class Client extends JFrame {
    private JTextPane chatArea;
    private JTextField messageField;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public Client(String username) {
        setupGUI();

        try {
            socket = new Socket("localhost", 8080); // Connect to server
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Send username to server for identification
            writer.println(username);

            // Start a thread to receive messages from server
            Thread readerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String message;
                        while ((message = reader.readLine()) != null) {
                            handleIncomingMessage(message);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            readerThread.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Chat Application");
        setSize(600, 400);
        setLayout(new BorderLayout());

        chatArea = new JTextPane();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);
        add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new BorderLayout());
        messageField = new JTextField();
        messageField.addActionListener(e -> {
            sendMessage();
        });
        inputPanel.add(messageField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> {
            sendMessage();
        });
        inputPanel.add(sendButton, BorderLayout.EAST);

        add(inputPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void sendMessage() {
        String message = messageField.getText().trim();
        if (!message.isEmpty()) {
            writer.println(message);
            messageField.setText("");
        }
    }

    private void handleIncomingMessage(String message) {
        SwingUtilities.invokeLater(() -> appendMessage(message));
    }

    private void appendMessage(String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                StyledDocument doc = chatArea.getStyledDocument();

                // Create styles
                Style defaultStyle = doc.addStyle("Default", null);
                Style boldStyle = doc.addStyle("Bold", null);
                StyleConstants.setBold(boldStyle, true);

                // Split message into sender and content
                int colonIndex = message.indexOf(":");
                if (colonIndex != -1 && colonIndex < message.length() - 1) {
                    String sender = message.substring(0, colonIndex);
                    String content = message.substring(colonIndex + 1);

                    try {
                        doc.insertString(doc.getLength(), sender + ": ", boldStyle);
                        doc.insertString(doc.getLength(), content + "\n", defaultStyle);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                } else {
                    // If no sender detected, just append the message as is
                    try {
                        doc.insertString(doc.getLength(), message + "\n", defaultStyle);
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }

                chatArea.setCaretPosition(doc.getLength());
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String username = JOptionPane.showInputDialog("Enter your username:");
            if (username != null && !username.isEmpty()) {
                new Client(username);
            } else {
                System.out.println("Invalid username.");
            }
        });
    }
}
