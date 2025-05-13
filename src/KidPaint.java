import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class KidPaint {

	public static void main(String[] args) {
		try{
		String s= JOptionPane.showInputDialog(null,"Username:","GUI",JOptionPane.PLAIN_MESSAGE);


		UI ui = UI.getInstance();

		ui.username=s;

		ui.setData(new int[50][50], 20);	// set the data array and block size. comment this statement to use the default data array and block size.
		ui.setVisible(true);				// set the ui
	}catch(Exception e){
			e.printStackTrace();
		}
}}
