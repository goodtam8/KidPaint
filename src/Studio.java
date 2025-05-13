import javax.swing.*;
import java.util.ArrayList;

public class Studio {
   public int width;
   public int height;

   public int studionum;
   public Studio(){

      String stu= JOptionPane.showInputDialog(null,"Studio number Please input a integer:","GUI",JOptionPane.PLAIN_MESSAGE);

      this.studionum=Integer.valueOf(stu);

   }

}
