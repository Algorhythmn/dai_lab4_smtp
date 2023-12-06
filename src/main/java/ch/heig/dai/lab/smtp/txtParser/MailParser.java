package ch.heig.dai.lab.smtp.txtParser;

import java.io.*;
import java.util.ArrayList;
import static java.nio.charset.StandardCharsets.UTF_8;

//TODO - Put the validation process here
public class MailParser {
    public ArrayList<String> parseMails(){
        ArrayList<String> mailList = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new FileReader("config/mail.txt", UTF_8))){
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                mailList.add(line.trim());
            }
        } catch (IOException e) {
            System.err.println("Error parsing mail.txt file: " + e);
        }
        return mailList;
    }

    public static void main(String[] args) {
        System.out.println(new MailParser().parseMails());
    }
}
