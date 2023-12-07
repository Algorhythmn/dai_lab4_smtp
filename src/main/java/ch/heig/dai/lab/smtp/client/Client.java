package ch.heig.dai.lab.smtp.client;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

import static java.nio.charset.StandardCharsets.UTF_8;
public class Client {

    private final static String END_CONNECTION = "221 Bye";
    private final static String EHLO = "EHLO ";
    private final static String CLIENT_IDENTITY = "heig-vd.ch";
    private final static String ADDR = "localhost";
    private final static String ACCEPTED_REQUEST = "250 ";
    private final static String START_DATA = "354";
    private final static String CRLF = "\r\n";
    private final static int PORT = 1025;

    /**
     * The full SMTP transaction to send email to all groups
     *
     * @param nbrOfGroup Desired number of group
     * @param mailList All mails that will be divided in n groups
     * @param subjectList All available subjects for email
     * @param bodyList All available body message for email
     */
    public void connect(int nbrOfGroup,
                        ArrayList<String> mailList,
                        ArrayList<String> subjectList,
                        ArrayList<String> bodyList) {
        try (Socket s = new Socket(ADDR, PORT);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), UTF_8));
        BufferedReader in = new BufferedReader((new InputStreamReader(s.getInputStream(), UTF_8)))) {
            sessionInitiation(in);
            clientInitiation(out);
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

    /**
     * After sending EHLO, for consuming all supported protocol by the mail server
     *
     * @param in Input from Server
     * @throws IOException
     */
    private void sessionInitiation(BufferedReader in) throws IOException {
        consumeServerText(in,"220");
    }

    /**
     * Consume all text sent by server depending of ending of messages
     *
     * @param in Input from mail server
     * @param endText text that announce that the server will not send anymore message to the client
     * @throws IOException
     */
    private void consumeServerText(BufferedReader in, String endText) throws IOException {
        String line;
        while((line = in.readLine()) != null && !line.contains(endText)) {}
    }

    /**
     * Send the EHLO mesage to the server
     *
     * @param out BufferedWriter to send message to the server
     * @throws IOException
     */
    private void clientInitiation(BufferedWriter out) throws IOException {
        out.write(EHLO + CLIENT_IDENTITY + CRLF);
        out.flush();
    }

    /**
     * Put an end to the conncetion to the server by sending the QUIT message
     *
     * @param out BufferedWriter to send message to the server
     * @param in Input from mail server
     * @throws IOException
     */
    private void endConnection(BufferedWriter out, BufferedReader in) throws IOException {
        out.write("QUIT" + CRLF);
        out.flush();
        consumeServerText(in, END_CONNECTION);
        System.out.println("Mail Sent");
    }

    /**
     * Correctly format mail address for MAIL FROM and RCPT TO message
     *
     * @param mail mail address to format
     * @return formatted mail address
     */
    private String mailAddressFormat(String mail) {
        return "<" + mail.toLowerCase() + ">" + CRLF;
    }


    /**
     * Send the first member of grouèp as the sender and the rest as recipients of the mail
     *
     * @param out BufferedWriter to send message to the server
     * @param in BufferedWriter to send message to the server
     * @param mailList Subgroup of original mail list
     * @throws IOException
     */
    private void setSenderAndRecipients(BufferedWriter out,
                                       BufferedReader in,
                                       ArrayList<String> mailList) throws IOException {
        //Setting hidden sender
        out.write("MAIL FROM:" + mailAddressFormat(mailList.getFirst()));
        out.flush();
        consumeServerText(in, ACCEPTED_REQUEST);

        //Setting hidden recipients
        for (int i = 1; i < mailList.size(); i++) {
            out.write("RCPT TO:" + mailAddressFormat(mailList.get(i)));
            out.flush();
            consumeServerText(in, ACCEPTED_REQUEST);
        }
    }

    /**
     * Send the body of the  email
     *
     * @param out BufferedWriter to send message to the server
     * @param in BufferedWriter to send message to the server
     * @param mailList Subgroup of original mail list
     * @param subject Subject of the email
     * @param body Body of the email
     * @throws IOException
     */
    private void setMsgBody(BufferedWriter out,
                           BufferedReader in,
                           ArrayList<String> mailList,
                           String subject,
                           String body) throws IOException {
        out.write("DATA" + CRLF);
        out.flush();
        consumeServerText(in, START_DATA);


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
        consumeServerText(in, ACCEPTED_REQUEST);
        }

    /**
     * Wrapper fo UTF-8 encoding on the subject for the mail server
     *
     * @param text text to be wrapped
     * @return wrapped text
     */
    private String utf8Formatting(String text) {
        return "=?utf-8?Q?" + text + "?=";
    }
}
