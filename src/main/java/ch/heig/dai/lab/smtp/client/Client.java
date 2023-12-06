package ch.heig.dai.lab.smtp.client;

import java.io.*;
import java.net.*;
import static java.nio.charset.StandardCharsets.UTF_8;
public class Client {

    private final static String END_CONNECTION = "221 Bye";
    private final static String EHLO = "EHLO ";
    private final static String CLIENT_IDENDITY = "heig-vd.ch";
    private final static String ADDR = "localhost";
    private final static String ACCEPTED_REQUEST ="250 ";


    private final static int PORT = 1025;
    public void connect() {
        try (Socket s = new Socket(ADDR, PORT);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), UTF_8));
        BufferedReader in = new BufferedReader((new InputStreamReader(s.getInputStream(), UTF_8)))) {
            System.out.println(sessionInitiation(out, in));
            System.out.println(clientInitiation(out, in));


        } catch (IOException e) {
            System.err.println("Client Error: " + e);
        }
    }

    public String sessionInitiation(BufferedWriter out, BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null && !line.contains("220")) {}

        //In case there is an error here, the client should quit the connection
        if (line.contains("Error")) {
            line = endConnection(out, in);
        }
        return line;
    }

    public String clientInitiation(BufferedWriter out, BufferedReader in) throws IOException {
        String line;
        out.write(EHLO + CLIENT_IDENDITY + "\r\n");
        out.flush();

        //TODO: Make it a function
        while((line = in.readLine()) != null && !line.contains(ACCEPTED_REQUEST)) {
            System.out.println(line);
            //To deal with error that could happens in client initiation phase
            if (line.contains("Error")) {
                line = endConnection(out, in);
                break;
            }
        }
        return line;
    }

    public String endConnection(BufferedWriter out, BufferedReader in) throws IOException {
        String line;
        out.write("QUIT\r\n");
        out.flush();
        //TODO: Make it a function
        while ((line = in.readLine()) != null && !line.equals(ACCEPTED_REQUEST)) {}
        return line;
    }

    public String mailAddressFormat(String mail) {
        return "<" + utf8Formatting(mail.toLowerCase()) + ">\r\n";
    }

    public boolean mailTransadtions(BufferedWriter out, BufferedReader in) throws IOException {
        String mail = "example@example.com";
        String line;
        //Setting hidden sender
        out.write("MAIL FROM:" + mailAddressFormat(mail));
        out.flush();
        //TODO: Make it a function
        while ((line = in.readLine()) != null && !line.equals(ACCEPTED_REQUEST)) {}

        //Setting hidden recipients - FOllow RFC
        String[] mailList = new String[5];
        mailList[0] = "thisIS@hotmalr.vf";
        mailList[1] = "日本人oliverzpua@h0tmal.com";
        mailList[2] = "tecno#roj@drmarcomendozacorbetto.com";
        mailList[3] = "lexoair#!$_&-@meslivresienetre.com";
        mailList[4] = "oui.sza@boranora.com";
        for (int i = 0; i < 5; i++) {
            out.write("RCPT TO:" + mailAddressFormat(mailList[i]));
            out.flush();
            //TODO: Make it a function
            while ((line = in.readLine()) != null && !line.equals(ACCEPTED_REQUEST)) {}
        }

        return false;
    }

    public String utf8Formatting(String text) {
        return "=?utf-8?Q?" + text + "?=";
    }
    public static void main(String[] args) {
        new Client().connect();
    }
}
