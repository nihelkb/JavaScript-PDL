/* Reader class:
 * This class defines the reading from a file given character by character.
 * State:  COMPLETED
 */


import java.io.BufferedReader;
import java.io.FileReader;

public class Reader {
    private FileReader frd;
    private BufferedReader brd;


    public Reader(String file){
        try {
            //frd = new FileReader(ClassLoader.getSystemResource(file).getFile());
            frd = new FileReader(file);
            brd = new BufferedReader(frd);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Character read(){
        Character c = null;
        try {
            c = (char)brd.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public void close(){
        try {
            frd.close();
            brd.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}