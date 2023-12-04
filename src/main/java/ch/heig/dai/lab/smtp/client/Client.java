package ch.heig.dai.lab.smtp.client;

import java.io.*;
import java.net.*;
import static java.nio.charset.StandardCharsets.UTF_8;
public class Client {

    private final static String END_CONNECTION = "221 Bye";
    private final static String ADDR = "localhost";
    private final static int PORT = 1025;
    public void connect() {
        try (Socket s = new Socket(ADDR, PORT);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), UTF_8));
        BufferedReader in = new BufferedReader((new InputStreamReader(s.getInputStream(), UTF_8)))) {
            System.out.println(sessionInitiation(in, ));

        } catch (IOException e) {
            System.err.println("Client Error: " + e);
        }
    }

    public String sessionInitiation(BufferedReader in, BufferedWriter out) throws IOException {
        String line;
        while ((line = in.readLine()) != null && !line.equals("220 e3be39763734 ESMTP")) {}

        //In case there is an error here, the client should quit the connection
        if (line.contains("554")) {
            out.write("QUIT");
            out.flush();
            while ((line = in.readLine()) != null && !line.equals(END_CONNECTION)) {}
            line = "Session Ended";
        }
        return line;
    }

public String clientInitiation() {
        String line = null;

        return line;
}
    public static void main(String[] args) {
        new Client().connect();
    }
}
