import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {
    public static void main(String[] args) {
        new File("converted_notes").mkdirs();
        File dir = new File(".");
        File[] directoryListing = dir.listFiles();
        if(directoryListing != null){
            for (File file : directoryListing){
                String fileName = file.getName(); //MM-DD-YYYY.HH.MM.SS
                if(fileName.toLowerCase().endsWith(".vnt")) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        int content;
                        boolean prevColon = false;
                        boolean isText = false;
                        String charHex = "";
                        String text = "";
                        while ((content = fis.read()) != -1) {
                            if((char)content == ':')
                                prevColon = true;
                            else if(prevColon) {
                                if((char)content == '=')
                                    isText = true;
                                prevColon = false;
                            }
                            else if(isText){
                                if(charHex.length() == 2){
                                    byte[] bytes = DatatypeConverter.parseHexBinary(charHex);
                                    text += new String(bytes, "UTF-8");
                                    charHex = "";
                                    if((char)content != '=')
                                        isText = false;
                                }
                                else if((char)content != '=' && content > 31)
                                    charHex += (char)content;
                            }
                        }
                        PrintWriter writer = new PrintWriter("converted_notes\\"+fileName+".txt", "UTF-8");
                        writer.print(text);
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
