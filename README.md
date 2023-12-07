# DAI Lab 4 - SMTP

## Description
This is small mail client that simulate sending prank mails to multiple group of recipients to a mock server mail.

Needed:
- Docker or Docker Desktop
- Maven

## TL;DR
**Requirement**: *Docker*, *Maven*
1. Download ![Maildev](https://github.com/maildev/maildev) and launch it with a docker container.
2. Fork and clone this repository
3. Modify the `messages.txt` and `mail.txt` in the **config** folder
4. Compile with the command `mvn build package`
5. Launch the program with the command `java -jar target/dai_lab4_smtp-1.0-SNAPSHOT.jar`
6. When prompted, put the number of groups you desire send your emails to.

## How to set up the mock mail server
We will be using ![Maildev](https://github.com/maildev/maildev) as our mail server which require the use of ![Docker](https://docker.com). 
Dowload the link of the Maildev docker image in their repository and launch it from Docker Desktop or from the terminal using the following command:
```
$ docker run -p 1080:1080 -p 1025:1025 maildev/maildev
```
If you change the port mapping for SMTP, you'll need to also change the attribute PORT of Socket in the Java class `Client.java`

You can then connect via browser to `localhost:1080` or the other port you decided to map to reach the mock mail server via HTTP.

## Starting a prank campaign
Before compiling and launching our client we will need to modify two files: the `messages.txt` and `mail.txt` in the **config** folder. Note that our client only accept those names and extensions. If they were any modification of those characteristic htne the client would not be running properly.

### Configuring the `mail.txt` file
For the client to accept emails, you will need to write one email address per line in the txt file. Email should follow the syntax as referenced ![here](https://en.wikipedia.org/wiki/Email_address#Syntax), Otherwise an error will be thrown and the client will not start. You can look inside the default `mail.txt` for an example of correct formatting.

### Configuring the `messages.txt` file
For your messages, start a line containing only the word `SUBJECT`followed by your subject on the next line. Directly after that, start a newline containing only the word `BODY` and put your email body on right after on the next line. If you wish to add multiple subject and body, right after the body of the previous message leave an empty line and repeat the process for as many email messages you desire to have.

### Launching the client
Compile the project with Maven:
```
mvn build package
```

Launch the program with:
```
java -jar target/dai_lab4_smtp-1.0-SNAPSHOT.jar
```

You will be prompted to enter the desired number of group you wish to send emails to. Note that the each groups needs at minimum 2 addresses and a maximum of 5 addresses. If the number of groups is not appropriated, you will be prompted again.
After entering a valid number of group. Your email will be sent and you can verify it on the mock mail server !

## Implementation's description
The Main class use 3 primordial classes to do the SMTP transaction. The Main class itself will only be in charge of fetching
a valid user input for the number of desired group and will process to initialized all the other classes below.

### `MailParser.java`
This is the class that fetch all email addresses from the `mail.txt` file.
Mail are only added when they are in the correct formatting and syntax explained above.
The syntax is verified via regex pattern matching.

### `MessageParser.java`
This is the class that fetch the subject and body message and store them in its attribute
in ArrayLists. 

### `Client.java`
This is the main class that communicate with the SMTP mail server. 
Port and address are hard-coded in attribute. Also all messages that the mock mail server respond withare also stored
as constant inside. If another mail server, address or port are used, we have to change it directly in the code. 

It is also the job of the Client to subdivise the groups into subgroups and send a random email text from the list provided.
The subdivision is not random: if a `N` email are provided and we desire `x` groups assuming that each groups will at the very least
have 2 or at most 5 members. The the first `N/x `(rounded to the top) members of the email list will be a subgroup, and the following, until the entire email list is exhausted.

The chosen encoding when conversing with the mail server is hard-coded to be UTF-8.


