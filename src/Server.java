import java.net.*;
import java.io.*;

public class Server {

    ServerSocket server;
    Socket socket;

    BufferedReader br;
    PrintWriter out;

    public Server() {

        try {
            server = new ServerSocket(9000);
            System.out.println("server is ready to accept connection...");
            System.out.println("waiting...");
            socket = server.accept();

            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void startReading()
    {
            Runnable r1 = ()->{

                System.out.println("Reader started...");
                while(true){

                    try {
                        String msg = br.readLine();
                        if(msg.equals("exit")){
                            System.out.println("Client terminated the chat");
                            break;
                        }

                        System.out.println("Client: " + msg);

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }


            };

            new Thread(r1).start();
    }

    public void startWriting(){

        Runnable r2 = ()->{

          try{
              BufferedReader  br1 = new BufferedReader(new InputStreamReader(System.in));
              String content = br1.readLine();

              out.println(content);
              out.flush();
          }catch (Exception e){
              e.printStackTrace();
          }

        };

        new Thread(r2).start();
    }


    public static void main(String[] args) {

        System.out.println("Server is going to start...");
        new Server();

    }
}
