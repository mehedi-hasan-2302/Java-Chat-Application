import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.*;

public class Client extends JFrame {

    Socket socket;

    BufferedReader br;
    PrintWriter out;

    private JLabel heading = new JLabel("Client Area");
    private JTextArea messageArea = new JTextArea();
    private JTextField messageInput = new JTextField();
    private Font font = new Font("Roboto", Font.PLAIN,20);

    public Client() {

        try{
            System.out.println("Sending request to server...");
            socket = new Socket("127.0.0.1", 9000);
            System.out.println("Connection done");

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

              createGUI();
              handleEvents();
              startReading();
//            startWriting();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createGUI(){

        this.setTitle("Client Messager[END]");
        this.setSize(600,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        heading.setFont(font);
        messageArea.setFont(font);
        messageInput.setFont(font);


        heading.setHorizontalAlignment(SwingConstants.CENTER);
        heading.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        messageInput.setHorizontalAlignment(SwingConstants.CENTER);


        this.setLayout(new BorderLayout());

        this.add(heading, BorderLayout.NORTH);
        this.add(messageArea, BorderLayout.CENTER);
        this.add(messageInput, BorderLayout.SOUTH);



       this.setVisible(true);

    }


    private void handleEvents(){
        messageInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

                if(e.getKeyCode() == 10) {

                  String contentToSend = messageInput.getText();
                  messageArea.append("Me: "+ contentToSend + "\n");
                  out.println(contentToSend);
                  out.flush();
                  messageInput.setText(" ");
                  messageInput.requestFocus();
                }

            }
        });
    }

    public void startReading()
    {
        Runnable r1 = ()->{

            System.out.println("Reader started...");

            try {

                while(true){

                    String msg = br.readLine();
                    if(msg.equals("exit")){
                        System.out.println("Server terminated the chat");
                        JOptionPane.showMessageDialog(this,"Server terminated the chat");
                        messageInput.setEnabled(true);
                        socket.close();
                        break;
                    }

                   // System.out.println("Server: " + msg);

                    messageArea.append("Server: " + msg + "\n");
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
//            try{
//                while(!socket.isClosed()) {
//                    BufferedReader  br1 = new BufferedReader(new InputStreamReader(System.in));
//                    String content = br1.readLine();
//
//                    out.println(content);
//                    out.flush();
//
//                    if(content.equals("exit")){
//                        socket.close();
//                        break;
//                    }
//
//                }
//
//            }catch (Exception e){
//               // e.printStackTrace();
//                System.out.println("Connection closed");
//            }
//
//        };
//
//        new Thread(r2).start();
//    }


    public static void main(String[] args) {

        System.out.println("this is client...");

        new Client();
    }
}
