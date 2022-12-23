/* Writer class:
 * This class defines the writing on a file.
 * State:  COMPLETED FOR LEXIC ANALIZER
 */

import java.io.FileWriter;

public class Writer {
    private static FileWriter fToken;
    private static FileWriter fTS;
    private static FileWriter fError;
    private static FileWriter fParse;
    private static boolean desc;

    public Writer(String tokenFile, String tsFile, String errorFile, String parseFile){
        try {
            fToken = new FileWriter(tokenFile);
            fTS = new FileWriter(tsFile);
            fError = new FileWriter(errorFile);
            fParse = new FileWriter(parseFile);
            desc = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeToken(String token){
        try {
            fToken.write(token);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    public void writeTS(String ts){
        try {
            fTS.write(ts);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    public void writeParse(String trace){
        try {
            if (!desc){
                fParse.write("Descendente ");
                desc = true;
            }
            fParse.write(trace);
            fParse.write(" ");
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    public void writeError(String error){
        try {
            fError.write(error);
        } catch (Exception e) {
           e.printStackTrace();
        }
    }

    public void close(){
        try {
            fToken.close();
            fTS.close();
            fError.close();
            fParse.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
