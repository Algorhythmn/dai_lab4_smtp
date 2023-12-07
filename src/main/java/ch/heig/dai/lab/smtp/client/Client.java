package ch.heig.dai.lab.smtp.client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static java.nio.charset.StandardCharsets.UTF_8;
public class Client {

    private final static String END_CONNECTION = "221 Bye";
    private final static String EHLO = "EHLO ";
    private final static String CLIENT_IDENDITY = "heig-vd.ch";
    private final static String ADDR = "localhost";
    private final static String ACCEPTED_REQUEST = "250 ";
    private final static String START_DATA = "354";
    private final static String CRLF = "\r\n";
    private final static int PORT = 1025;

    public void connect(int nbrOfGroup,
                        ArrayList<String> mailList,
                        ArrayList<String> subjectList,
                        ArrayList<String> bodyList) {
        try (Socket s = new Socket(ADDR, PORT);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), UTF_8));
        BufferedReader in = new BufferedReader((new InputStreamReader(s.getInputStream(), UTF_8)))) {
            sessionInitiation(out, in);
            clientInitiation(out, in);
            int random;
            //Pass the correct group and all
            for(int i = 0; i < nbrOfGroup; i++) {
                ArrayList<String> subGroup = new ArrayList<>();
                for (int idx = 0; idx < (int) (idx + Math.ceil(mailList.size()/nbrOfGroup)) && idx < mailList.size(); idx++) {
                    subGroup.add(mailList.get(idx));
                }
                setSenderAndRecipients(out, in, subGroup);
                random = ThreadLocalRandom.current().nextInt(bodyList.size());
                setMsgBody(out,in,mailList, subjectList.get(random), bodyList.get(random));
            }
            endConnection(out,in);
        } catch (IOException e) {
            System.err.println("Client Error: " + e);
        }
    }

    private void sessionInitiation(BufferedWriter out, BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null && !line.contains("220")) {}

        //In case there is an error here, the client should quit the connection
        if (line.contains("Error")) {
            endConnection(out, in);
        }
    }

    private void clientInitiation(BufferedWriter out, BufferedReader in) throws IOException {
        String line;
        out.write(EHLO + CLIENT_IDENDITY + CRLF);
        out.flush();

        //TODO: Make it a function
        while((line = in.readLine()) != null && !line.contains(ACCEPTED_REQUEST)) {
            //To deal with error that could happens in client initiation phase
            if (line.contains("Error")) {
                endConnection(out, in);
                break;
            }
        }
    }

    private void endConnection(BufferedWriter out, BufferedReader in) throws IOException {
        String line;
        out.write("QUIT" + CRLF);
        out.flush();
        //TODO: Make it a function
        while ((line = in.readLine()) != null && !line.contains(END_CONNECTION)) {}
        System.out.println("Mail Sent");
    }

    private String mailAddressFormat(String mail) {
        return "<" + mail.toLowerCase() + ">" + CRLF;
    }

    /**
     * Send the sender and recipient to the mail server
     *
     * @param out
     * @param in
     * @param mailList
     * @throws IOException
     */
    private void setSenderAndRecipients(BufferedWriter out,
                                       BufferedReader in,
                                       ArrayList<String> mailList) throws IOException {
        String line;
        //Setting hidden sender
        out.write("MAIL FROM:" + mailAddressFormat(mailList.getFirst()));
        out.flush();
        //TODO: Make it a function
        while ((line = in.readLine()) != null && !line.contains(ACCEPTED_REQUEST)) {}

        //Setting hidden recipients
        for (int i = 1; i < mailList.size(); i++) {
            out.write("RCPT TO:" + mailAddressFormat(mailList.get(i)));
            out.flush();
            //TODO: Make it a function
            while ((line = in.readLine()) != null && !line.contains(ACCEPTED_REQUEST)) {}
        }
    }

    private void setMsgBody(BufferedWriter out,
                           BufferedReader in,
                           ArrayList<String> mailList,
                           String subject,
                           String body) throws IOException {
        String line;
        out.write("DATA" + CRLF);
        out.flush();
        //TODO - Make it a function consume text until ...
        while ((line = in.readLine()) != null && !line.contains(START_DATA)) {}

        //Setting Body encoding to UTF-8
        out.write("Content-Type: text/plain; charset=utf-8" + CRLF);
        out.flush();

        //Setting visible sender
        out.write("From: " + mailAddressFormat(mailList.getFirst()));
        out.flush();
        StringBuilder visibleRecipients = new StringBuilder();
        for (int i = 1; i < mailList.size(); i++) {
            visibleRecipients.append(mailList.get(i)).append(";");
        }

        //Set visible recipients
        out.write("To: " + visibleRecipients.substring(0,visibleRecipients.toString().length() -1).toLowerCase() + CRLF);
        out.flush();


        //Setting the Subject
        out.write("Subject: " + utf8Formatting(subject) + CRLF);
        out.flush();

        //Setting the Body
        out.write( CRLF);
        out.flush();
        out.write( body + CRLF);
        out.flush();

        //Ending message
        out.write("." + CRLF);
        out.flush();
        while ((line = in.readLine()) != null && !line.contains(ACCEPTED_REQUEST)) {}
        }

    private String utf8Formatting(String text) {
        return "=?utf-8?Q?" + text + "?=";
    }
}
