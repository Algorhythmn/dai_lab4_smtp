package ch.heig.dai.lab.smtp.txtParser;

import java.io.*;
import java.util.ArrayList;
import static java.nio.charset.StandardCharsets.UTF_8;
public class MessageParser {

    private ArrayList<String> subjectList = new ArrayList<>();
    private ArrayList<String> bodyList = new ArrayList<>();
    public void parseMessages() {
        try(BufferedReader in = new BufferedReader(new FileReader("config/messages.txt", UTF_8))){
            String line;
            while ((line = in.readLine()) != null && !line.isEmpty()) {
                StringBuilder body = new StringBuilder();
                if (line.equals("SUBJECT")) {
                    subjectList.add(in.readLine());
                } else if (line.equals("BODY")) {
                    while ((line = in.readLine()) != null && !line.isEmpty()) {
                        if (!line.equals("\n"))
                            body.append(line).append("\r\n");
                    }
                    bodyList.add(body.toString());
                }
            }
        } catch (IOException e) {
            System.err.println("Error parsing messages.txt file: " + e);
        }
    }

    public ArrayList<String> getBodyList() {
        return new ArrayList<>(bodyList);
    }

    public ArrayList<String> getSubjectList() {
        return new ArrayList<>(subjectList);
    }

    public static void main(String[] args) {
        MessageParser test = new MessageParser();
        test.parseMessages();
        System.out.println(test.getSubjectList());
        System.out.println(test.getBodyList());
    }
}
