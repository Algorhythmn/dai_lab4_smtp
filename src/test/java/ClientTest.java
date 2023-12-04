import ch.heig.dai.lab.smtp.client.Client;
import org.junit.Test;
import java.io.*;
import java.net.Socket;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static java.nio.charset.StandardCharsets.UTF_8;

public class ClientTest {

    @Test
    public void initialConnection() {
        //Create connection to mail server
        String result = null;
        try (Socket s = new Socket("localhost", 1025);
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream(), UTF_8));
             BufferedReader in = new BufferedReader((new InputStreamReader(s.getInputStream(), UTF_8)))) {
            result = new Client().initialConnection(out,in);
        } catch (IOException e) {
            System.err.println("Client Error: " + e);
        }

        assertEquals("220 e3be39763734 ESMTP", result);

    }
}
