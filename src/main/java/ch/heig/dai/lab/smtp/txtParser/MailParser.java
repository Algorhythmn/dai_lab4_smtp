package ch.heig.dai.lab.smtp.txtParser;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.nio.charset.StandardCharsets.UTF_8;


public class MailParser {

    /**
     * Parses mail.txt config file for a list of all email to be used by the client
     *
     * @return a list of all the correct emails on config file.
     */
    public ArrayList<String> parseMails(){
        //Create the regex for valid email
        Pattern pattern = Pattern.compile("^[^@.]+\\.?[^@.]+@\\w+\\.\\w+$", Pattern.CASE_INSENSITIVE);
        Matcher matcher;
        ArrayList<String> mailList = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new FileReader("config/mail.txt", UTF_8))){
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                matcher = pattern.matcher(line.trim());
                if(!matcher.find()) {
                    throw new RuntimeException(line.trim() + " has an invalid syntax - Please only enter valid email");
                }
                mailList.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error parsing mail.txt file: " + e);
        }
        return mailList;
    }
}
