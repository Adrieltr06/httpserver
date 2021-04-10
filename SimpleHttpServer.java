package net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SimpleHttpServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8989)) {
            System.out.println("SimpleHttpServer listen " + serverSocket);
            while (true){
                try (Socket clientSocket = serverSocket.accept()) {
                    handleClient(clientSocket);
                }
            }
        } catch (IOException ex){
            System.err.println(ex.getMessage());
        }
    }

    private static void handleClient(Socket socket) throws IOException {
        System.out.println("Client connected from " + socket);
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Read request.
        StringBuilder sb = new StringBuilder();
        String line;
        while (!(line = reader.readLine()).isBlank()){
            sb.append(line + "\r\n");
        }

        // Parse request.
        String request = sb.toString();
//        System.out.println(request);
        String[] requestLines = request.split("\r\n");
        String firstLine = requestLines[0];
        String[] subs = firstLine.split(" ");
        String method = subs[0];
        String path = subs[1];
        String protocol = subs[2];

        /*String secondLine = requestLines[1];
        subs = secondLine.split(" ");
        String host = subs[1];
        System.out.println("Method: " + method);
        System.out.println("  Path: " + path);
        System.out.println(" Proto: " + protocol);
        System.out.println("  host: " + host);*/

        System.out.println("method=" + method + " path=" + path);

        // Send request.
        if("/".equals(path)){
            Path filePath = Paths.get("www", "index.html");
            BufferedOutputStream baos = new BufferedOutputStream(socket.getOutputStream());
            baos.write("HTTP/1.1 200 OK\r\n".getBytes(StandardCharsets.US_ASCII));
            baos.write("ContentType: text/html\r\n".getBytes(StandardCharsets.US_ASCII));
            baos.write("\r\n".getBytes(StandardCharsets.US_ASCII));
            baos.write(Files.readAllBytes(filePath));
            baos.write("\r\n".getBytes(StandardCharsets.US_ASCII));
            baos.write("\r\n".getBytes(StandardCharsets.US_ASCII));
            baos.flush();


        } else {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write("HTTP/1.1 200 OK\r\n");
            writer.write("ContentType: text/html\r\n");
            writer.write("\r\n");
            writer.write("<b>It works!</b>");
            writer.write("<h1>Header</h1>");
            writer.write("\r\n");
            writer.write("\r\n");
            writer.flush();
        }


    }
}
