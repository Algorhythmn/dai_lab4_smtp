package ch.heig.dai.lab.smtp.client;

import java.io.*;
import java.net.*;
import static java.nio.charset.StandardCharsets.UTF_8;
public class Client {

    private final static String ADDR = "localhost";
    private final static int PORT = 1025;
    public void connect() {
        try (Socket s = new Socket(ADDR, PORT);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), UTF_8));
        BufferedReader in = new BufferedReader((new InputStreamReader(s.getInputStream(), UTF_8)))) {
            System.out.println(initialConnection(in));

        } catch (IOException e) {
            System.err.println("Client Error: " + e);
        }
    }

    public String initialConnection(BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null && !line.equals("220 e3be39763734 ESMTP")) {}
        return line;
    }

    
    public static void main(String[] args) {
        new Client().connect();
    }
}
