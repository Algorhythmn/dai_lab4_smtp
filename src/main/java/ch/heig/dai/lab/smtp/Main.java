package ch.heig.dai.lab.smtp;

import java.util.ArrayList;
import java.util.Scanner;

import ch.heig.dai.lab.smtp.client.Client;
import ch.heig.dai.lab.smtp.txtParser.MailParser;
import ch.heig.dai.lab.smtp.txtParser.MessageParser;

public class Main {
    public static void main(String[] args) {
        ArrayList<String> mailList = new MailParser().parseMails();

        //Reception of desired number of groups
        Scanner sc = new Scanner(System.in);
        int nbrGroup;
        boolean correctNbrGroup = true;
        do {
            System.out.println("Please enter the desired number of group");
            while(!sc.hasNextInt()) {
                System.err.println("Please only enter an Integer");
                sc.next();
            }
            nbrGroup = sc.nextInt();
            if ((mailList.size() / nbrGroup) > 5) {
                System.err.println("The number of groups is too small for the set of email provided");
            } else if ((mailList.size() / nbrGroup) < 2) {
                System.err.println("The number of groups is too big for the set of email provided");
            } else {
                correctNbrGroup = false;
            }
        } while (correctNbrGroup);

        MessageParser messageParser = new MessageParser();
        messageParser.parseMessages();

        new Client().connect(nbrGroup,mailList,messageParser.getSubjectList(), messageParser.getBodyList());
    }
}