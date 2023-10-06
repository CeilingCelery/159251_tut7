package templatesTutorial;

import java.beans.XMLDecoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.xml.sax.InputSource;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;

public class PrintInvitations {
    public static final String INVITATIONS_TXT_FILENAME = "invitations.txt";
    public static final String INVITATIONS_HTML_FILENAME = "invitations.html";
    public static void main(String[] args) {
        // read in the existing xml to objects
        FileInputStream friendsFile = null;
        ArrayList<Person> decodedPersons = null;
        XMLDecoder decoder = null;
        try {
            friendsFile = new FileInputStream("friends.xml");
            InputSource inSource = new InputSource(friendsFile);
            decoder = new XMLDecoder(inSource);
            Object temp = decoder.readObject();
            if (temp instanceof ArrayList<?>) {
                decodedPersons = (ArrayList<Person>) temp;
            }
        } catch (FileNotFoundException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }  finally {
            if (decoder != null) {
                decoder.close();
            }
        }
        // place the read in values from the xml into Velocity so that they can be inserted into template
        Velocity.init();
        VelocityContext velContext = new VelocityContext();
        FileWriter writer = null;
        try {
            writer = new FileWriter(INVITATIONS_TXT_FILENAME, false);
            writer.write("<!DOCTYPE html>\n");
            for (Person p : decodedPersons) {
                velContext.put("person", p);
                Velocity.mergeTemplate("email_invite.vm", "UTF8", velContext, writer);
            }
            writer.close();
            // simple create a .html file too for easy reading in browser
            Files.copy(Paths.get(INVITATIONS_TXT_FILENAME)
                    , Paths.get(INVITATIONS_HTML_FILENAME)
                    , StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
