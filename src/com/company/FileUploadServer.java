package com.company;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileUploadServer extends JFrame {
    private static final int SERVER_PORT = 443;

    private JTextArea textArea;

    public FileUploadServer() {
        setTitle("File Upload Server");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea();
        textArea.setEditable(false);
        add(new JScrollPane(textArea), BorderLayout.CENTER);
    }

    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            textArea.append("Server started\n");

            while (true) {
                Socket socket = serverSocket.accept();
                textArea.append("Client connected"+socket.getInetAddress()+"\n");

                DataInputStream dis = new DataInputStream(socket.getInputStream());
                String fileName = dis.readUTF();
                long fileLength = dis.readLong();

                File file = new File(fileName);
                FileOutputStream fos = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int bytesRead;
                long totalBytesRead = 0;

                while ((bytesRead = dis.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                }


                fos.close();
                dis.close();
                socket.close();

                textArea.append("File " + fileName + " received, size: " + fileLength + "\n");
                Socket clientSocket = serverSocket.accept();
                textArea.append("New client connected: " + clientSocket.getInetAddress()+ "\n");



            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FileUploadServer server = new FileUploadServer();
            server.setVisible(true);

            // Start the server in a new thread
            new Thread(server::startServer).start();
        });
    }

}
