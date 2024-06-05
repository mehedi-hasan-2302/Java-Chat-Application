import javax.swing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;

public class Server extends JFrame {

    ServerSocket server;
    Socket socket;

    BufferedReader br;
    PrintWriter out;

    private JLabel heading = new JLabel("Server Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN,20);

    public Server() {

        try {
            server = new ServerSocket(9000);
            System.out.println("Server is ready to accept connection...");
            System.out.println("waiting...");
            socket = server.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());


            createGUI();
            handleEvents();
            startReading();
          //  startWriting();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void createGUI(){

        this.setTitle("Server Messager[END]");
        this.setSize(600,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        heading.setFont(font);
        messageArea.setFont(font);
        messageArea.setBackground(new Color(204, 229, 255)); // Light blue background color

        messageInput.setFont(font);
        messageInput.setBackground(new Color(255, 204, 229)); // Light pink background color




        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        messageInput.setHorizontalAlignment(SwingConstants.CENTER);
        messageArea.setEditable(false);

        this.setLayout(new BorderLayout());

        this.add(heading, BorderLayout.NORTH);
        JScrollPane jScrollPane = new JScrollPane(messageArea);
        this.add(jScrollPane, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);



        this.setVisible(true);

    }


//    private void handleEvents(){
//        messageInput.addKeyListener(new KeyListener() {
//            @Override
//            public void keyTyped(KeyEvent e) {
//
//            }
//
//            @Override
//            public void keyPressed(KeyEvent e) {
//
//            }
//
//            @Override
//            public void keyReleased(KeyEvent e) {
//
//                if(e.getKeyCode() == 10) {
//
//                    String contentToSend = messageInput.getText();
//                    messageArea.append("Me: "+ contentToSend + "\n");
//                    messageArea.setCaretPosition(messageArea.getDocument().getLength());
//                    out.println(contentToSend);
//                    out.flush();
//                    messageInput.setText(" ");
//                    messageInput.requestFocus();
//                }
//
//            }
//        });
//    }

    private void handleEvents() {
        messageInput.addActionListener(e -> {
            String contentToSend = messageInput.getText();
            messageArea.append("Me: " + contentToSend + "\n");
            messageArea.setCaretPosition(messageArea.getDocument().getLength()); // Autoscroll
            out.println(contentToSend);
            out.flush();
            messageInput.setText("");
        });
    }


    public void startReading()
    {
        Runnable r1 = ()->{

            System.out.println("Reader started...");

            try {

                while(true){

                    String msg = br.readLine();
                    if (msg.equals("exit")) {
                        System.out.println("Client terminated the chat");

                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, "Client terminated the chat");
                            messageInput.setEnabled(false); // Disable input after showing the dialog
                        });
                        socket.close();
                        break;
                    }


                    // System.out.println("Server: " + msg);

                    messageArea.append("Client: " + msg + "\n");
                }
            } catch (Exception e) {
                // throw new RuntimeException(e);
                System.out.println("Connection closed");
            }


        };

        new Thread(r1).start();
    }



//    public void startWriting(){
//
//        Runnable r2 = ()->{
//
//            System.out.println("Writer started...");
//
//          try{
//              while(!socket.isClosed()){
//                  BufferedReader  br1 = new BufferedReader(new InputStreamReader(System.in));
//                  String content = br1.readLine();
//
//                  out.println(content);
//                  out.flush();
//
//                  if(content.equals("exit")){
//                      socket.close();
//                      break;
//                  }
//
//              }
//
//          }catch (Exception e){
//              //e.printStackTrace();
//              System.out.println("Connection closed");
//          }
//
//        };
//
//        new Thread(r2).start();
//    }


    public static void main(String[] args) {

        System.out.println("Server is going to start...");
        new Server();

    }
}
