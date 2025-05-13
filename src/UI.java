import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.util.Random;
import javax.swing.border.LineBorder;

enum PaintMode {Pixel, Area, MagicBrush, Refresh};
class info{
	int port;
	String address;
	String op;
	public info(String info,String address,int port){
		this.port=port;
		this.address=address;
		this.op=info;
	}

}
public class UI extends JFrame {
	Socket socket;
	DataInputStream in;
	DataOutputStream out;
	public String hostname;
	public int port=0;
	private JTextField msgField;
	private JTextArea chatArea;
	private JPanel pnlColorPicker;
	private JPanel paintPanel;
	private JToggleButton tglPen;
	private JToggleButton tglBucket;
	private JToggleButton tglExport;
	private JToggleButton tglImport;
	private JToggleButton tglRefresh;
	private JToggleButton tglMagicBrush;

	public String username;
	public String studioname;
	private static UI instance;
	public int number;
	private int selectedColor = -543230; 	//golden

	int[][] data=new int[50][50] ;	// pixel color data array
	int blockSize = 16;
	ArrayList<info> st=new ArrayList<>();
	PaintMode paintMode = PaintMode.Pixel;

	/**
	 * get the instance of UI. Singleton design pattern.
	 * @return
	 */
	public static UI getInstance() throws IOException, InterruptedException {
		try {
			if (instance == null)
				instance = new UI();
		}
		catch(IOException e){}
		return instance;
	}
	private void playSound(String filePath) {
		try {
			File soundFile = new File(filePath);
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
		} catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
			e.printStackTrace();
		}
	}
	private void receiveChatMessage(DataInputStream in)throws IOException{
		byte[]buffer=new byte[1024];
		int len=in.readInt();
		in.read(buffer,0,len);
		String msg=new String(buffer,0,len);
		System.out.print(msg);
		SwingUtilities.invokeLater(()-> {//everytime if you call this one it will not
			//run immediately until it is free
			chatArea.append(msg + "\n");

		});}
	private void receivePixelMessage(DataInputStream in )throws IOException{

		int color=in.readInt();
		int x=in.readInt();
		int y=in.readInt();

		paintPixel(color,x,y);
		//we do it later
	}
	private void receiveBucketMessage(DataInputStream in)throws IOException{
		int color=in.readInt();
		int x=in.readInt();
		int y=in.readInt();
		List<Point>op=paintArea(color,x,y);

		for (int i=0;i<op.size();i++){
			out.writeInt(1);
			out.writeInt(selectedColor);
			out.writeInt(op.get(i).x);
			out.writeInt(op.get(i).y);
		}

	}
	private void receiveStudio(DataInputStream in)throws IOException{
		int y=in.readInt();
		//st.add(y);
		System.out.println(y);
	}
	private void receive(DataInputStream in) {//in practice you should use try
		//and catch
		//we have to add the client name reference the lab 5
		while(true){
			try{
				int type=in.readInt();// if this is a chat message
				switch(type) {
					case 0:
						receiveChatMessage(in);
						break;
					case 1:
						receivePixelMessage(in);
						break;
					case 2:
						receiveBucketMessage(in);
						//receive other data
						break;

					case 4:
						receiveStudio(in);
						break;
					case 7:
						setsize(in);
						break;
					case 88:
						showalert();
						break;
					default:
						//do other things?
				}


			}catch(Exception e){

			}
		}


	}
	private void setsize(DataInputStream in) throws IOException {
		int color=in.readInt();
		int x=in.readInt();
		int y=in.readInt();

		paintPixel(color,x,y);

	}
	private void showalert() throws IOException {
		int n=JOptionPane.showConfirmDialog(null,"Do you want to repaint it?","Title",JOptionPane.YES_NO_OPTION);
		out.writeInt(98);
		out.writeInt(n);
		System.out.println("Yes or no:"+n);
	}

	public String[] changestring(ArrayList<info>a){
		String[]re=new String [a.size()];
		for (int i=0;i<a.size();i++){
			re[i]=a.get(i).op;
		}
		return re;
	}




	/**
	 * private constructor. To create an instance of UI, call UI.getInstance() instead.
	 */
	private UI() throws IOException, InterruptedException {
		String msg="studio";

		DatagramPacket receivedPacket = new DatagramPacket(new byte[1024], 1024);
		System.out.println("Listening...");

		int timeout = 5000; // Replace with your desired timeout value in milliseconds

		DatagramSocket yysocket = new DatagramSocket(5555);
		yysocket.setSoTimeout(timeout);
		st.add(new info("0","128",12345));

		byte[] buffer = new byte[1024]; // Replace with your desired buffer size
		DatagramPacket packet = new DatagramPacket(msg.getBytes(), msg.length(), InetAddress.getByName("255.255.255.255"), 45678);
		yysocket.send(packet);
		boolean receiving = true;
		while (receiving) {
			try {
				yysocket.receive(receivedPacket);
				// Process the received packet data
				String receivedData = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
				st.add(new info(receivedData,receivedPacket.getAddress().getHostAddress(),12345));

				System.out.println("Received packet: " + receivedData);
			} catch (java.net.SocketTimeoutException e) {
				System.out.println("No packet received for a while. Stopping receive operation.");
				receiving = false;
			}
		}


		yysocket.close();
		String[]re= this.changestring(st);
		String op=String.valueOf(JOptionPane.showInputDialog(null,"studio 0 means create","name",
				JOptionPane.PLAIN_MESSAGE,new ImageIcon("icon.png"),re,"0"));
		if(op.equals("0")){
			Studio nm=new Studio();

			studioname=String.valueOf(nm.studionum);
			data=new int[50][50];
			Thread y=new Thread(()->{
				try {
					Server like=new Server(nm.studionum);
				} catch (IOException e) {
					throw new RuntimeException(e);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}


			});
			y.start();
			Thread.sleep(2000);
			InetAddress localHost = InetAddress. getLocalHost();

			socket=new Socket(localHost,12345);// we should do modification dont try to make it just run in local environment


		}else{
			studioname=op;
			for(int i=0;i<st.size();i++){
				if(studioname.equals(st.get(i).op)){
					socket=new Socket(st.get(i).address,12345);// we should do modification dont try to make it just run in local environment


				}
			}


			//out.writeInt(4);
			//out.writeInt(Integer.valueOf(studioname));

		}




		out=new DataOutputStream(socket.getOutputStream());

		// we can do the refresh the board for the kidpainting
		in=new DataInputStream(socket.getInputStream());




		Thread t=new Thread(()->{
			try {

				receive(in);

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		t.start();
		Thread.sleep(2000);

		//this.stop();

		setTitle("KidPaint");
		out.writeInt(5);
		out.flush();
		JPanel basePanel = new JPanel();
		getContentPane().add(basePanel, BorderLayout.CENTER);
		basePanel.setLayout(new BorderLayout(0, 0));

		paintPanel = new JPanel() {

			// refresh the paint panel
			@Override
			public void paint(Graphics g) {
				super.paint(g);

				Graphics2D g2 = (Graphics2D) g; // Graphics2D provides the setRenderingHints method

				// enable anti-aliasing
				RenderingHints rh = new RenderingHints(
						RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHints(rh);

				// clear the paint panel using black
				g2.setColor(Color.black);
				g2.fillRect(0, 0, this.getWidth(), this.getHeight());

				// draw and fill circles with the specific colors stored in the data array
				for(int x=0; x<data.length; x++) {
					for (int y=0; y<data[0].length; y++) {
						g2.setColor(new Color(data[x][y]));
						g2.fillArc(blockSize * x, blockSize * y, blockSize, blockSize, 0, 360);
						g2.setColor(Color.darkGray);
						g2.drawArc(blockSize * x, blockSize * y, blockSize, blockSize, 0, 360);
					}
				}

			}
		};

		paintPanel.addMouseListener(new MouseListener() {
			@Override public void mouseClicked(MouseEvent e) {}
			@Override public void mouseEntered(MouseEvent e) {}
			@Override public void mouseExited(MouseEvent e) {}
			@Override public void mousePressed(MouseEvent e) {}

			// handle the mouse-up event of the paint panel
			@Override
			public void mouseReleased(MouseEvent e) {//this is for handle the button of the function
				if (paintMode == PaintMode.Area && e.getX() >= 0 && e.getY() >= 0)
					try{
						out.writeInt(2);
						out.writeInt(selectedColor);//send out the color of the pixel
						out.writeInt(e.getX()/blockSize);
						out.writeInt(e.getY()/blockSize);
						out.flush();

					}catch(IOException ex){

						ex.printStackTrace();//for debugging only .remove it after development

					}
			}
		});

		paintPanel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {// this is for handel pen 即是係度拖行pixel
				// try to delay the update now
				try {

					if (paintMode == PaintMode.Pixel && e.getX() >= 0 && e.getY() >= 0){
						//paintPixel(e.getX()/blockSize,e.getY()/blockSize);
						out.writeInt(1);//indicate this is a graphic data
						out.writeInt(selectedColor);//send out the color of the pixel
						out.writeInt(e.getX()/blockSize);
						out.writeInt(e.getY()/blockSize);}


					else if (paintMode == PaintMode.MagicBrush) {
						// Magic brush
						Random random = new Random();
						int sparkleSize = 5; // adjust the size of the sparkles as needed
						for (int i = 0; i < sparkleSize; i++) {
							int sparkleX = e.getX() + random.nextInt(sparkleSize) - sparkleSize / 2;
							int sparkleY = e.getY() + random.nextInt(sparkleSize) - sparkleSize / 2;
							int sparkleColor = getRandomSparkleColor(); // implement this method to get a random sparkle color

							out.writeInt(1); // indicate this is a graphic data
							out.writeInt(sparkleColor); // send out the color of the sparkle
							out.writeInt(sparkleX / blockSize);
							out.writeInt(sparkleY / blockSize);
						}
					}
					out.flush();

				}
				catch( IOException ex){
					ex.printStackTrace();//for debugging only .remove it after development

				}
			}

			@Override public void mouseMoved(MouseEvent e) {}

		});
		paintPanel.setPreferredSize(new Dimension(data.length * blockSize, data[0].length * blockSize));

		JScrollPane scrollPaneLeft = new JScrollPane(paintPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		basePanel.add(scrollPaneLeft, BorderLayout.CENTER);

		JPanel toolPanel = new JPanel();
		basePanel.add(toolPanel, BorderLayout.NORTH);
		toolPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));


		pnlColorPicker = new JPanel();
		pnlColorPicker.setPreferredSize(new Dimension(24, 24));
		pnlColorPicker.setBackground(new Color(selectedColor));
		pnlColorPicker.setBorder(new LineBorder(new Color(0, 0, 0)));

		// show the color picker
		pnlColorPicker.addMouseListener(new MouseListener() {
			@Override public void mouseClicked(MouseEvent e) {}
			@Override public void mouseEntered(MouseEvent e) {}
			@Override public void mouseExited(MouseEvent e) {}
			@Override public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {
				ColorPicker picker = ColorPicker.getInstance(UI.instance);
				Point location = pnlColorPicker.getLocationOnScreen();
				location.y += pnlColorPicker.getHeight();
				picker.setLocation(location);
				picker.setVisible(true);
			}

		});

		toolPanel.add(pnlColorPicker);

		tglPen = new JToggleButton("Pen");
		tglPen.setSelected(true);
		toolPanel.add(tglPen);
		tglMagicBrush = new JToggleButton("Magic Brush");
		tglMagicBrush.setSelected(true);
		toolPanel.add(tglMagicBrush);

		tglBucket = new JToggleButton("Bucket");
		tglExport=new JToggleButton("Export");
		tglImport=new JToggleButton("Import");
		tglRefresh=new JToggleButton("Refresh");

		toolPanel.add(tglBucket);
		toolPanel.add(tglExport);
		toolPanel.add(tglImport);
		toolPanel.add(tglRefresh);
		tglRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					out.writeInt(88);
					out.flush();
				} catch (IOException ex) {
					throw new RuntimeException(ex);
				}
			}
		});
		tglImport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					DataInputStream in = new DataInputStream(new FileInputStream("test.txt"));
					for(int i=0;i<data.length;i++){
						for(int j=0;j<data[i].length;j++){
							int color=in.readInt();
							int x=in.readInt();

							int y=in.readInt();
							out.writeInt(1);
							out.writeInt(color);
							out.writeInt(x);
							out.writeInt(y);
							out.flush();

						}
					}
					in.close();

					System.out.println("you trigger the export button");
				} catch (FileNotFoundException e) {

				} catch (IOException e) {

				}

			}
		});
		tglExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					DataOutputStream out = new DataOutputStream(new FileOutputStream("test.txt"));
					for(int i=0;i<data.length;i++){
						for(int j=0;j<data[i].length;j++){
							out.writeInt(data[i][j]);
							out.writeInt(i);
							out.writeInt(j);
							out.flush();

						}
					}
					out.close();

					System.out.println("you trigger the export button");
				} catch (FileNotFoundException e) {

				} catch (IOException e) {

				}

			}
		});

		// change the paint mode to PIXEL mode
		tglPen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tglMagicBrush.setSelected(false);

				tglPen.setSelected(true);
				tglBucket.setSelected(false);
				paintMode = PaintMode.Pixel;
			}
		});

		// change the paint mode to AREA mode
		tglBucket.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tglMagicBrush.setSelected(false);

				tglPen.setSelected(false);
				tglBucket.setSelected(true);
				paintMode = PaintMode.Area;
			}
		});
		tglMagicBrush.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				tglPen.setSelected(false);
				tglBucket.setSelected(false);
				tglMagicBrush.setSelected(true);
				paintMode = PaintMode.MagicBrush;
			}
		});
		tglBucket.addMouseListener(new MouseListener() {
			@Override public void mousePressed(MouseEvent e) {}
			@Override public void mouseReleased(MouseEvent e) {}
			@Override public void mouseEntered(MouseEvent e) {}
			@Override public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {
				playSound("sound.wav");  //sound file
			}
		});
		tglMagicBrush.addMouseListener(new MouseListener() {
			@Override public void mousePressed(MouseEvent e) {}
			@Override public void mouseReleased(MouseEvent e) {}
			@Override public void mouseEntered(MouseEvent e) {}
			@Override public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {
				playSound("sound.wav");  //sound file
			}
		});
		tglPen.addMouseListener(new MouseListener() {
			@Override public void mousePressed(MouseEvent e) {}
			@Override public void mouseReleased(MouseEvent e) {}
			@Override public void mouseEntered(MouseEvent e) {}
			@Override public void mouseExited(MouseEvent e) {}
			@Override
			public void mouseClicked(MouseEvent e) {
				playSound("sound.wav");  //sound file
			}
		});




		JPanel msgPanel = new JPanel();

		getContentPane().add(msgPanel, BorderLayout.EAST);

		msgPanel.setLayout(new BorderLayout(0, 0));

		msgField = new JTextField();	// text field for inputting message

		msgPanel.add(msgField, BorderLayout.SOUTH);

		// handle key-input event of the message field
		msgField.addKeyListener(new KeyListener() {
			@Override public void keyTyped(KeyEvent e) {}
			@Override public void keyPressed(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == 10) {		// if the user press ENTER
					onTextInputted(msgField.getText());
					msgField.setText("");
				}
			}

		});

		chatArea = new JTextArea();		// the read only text area for showing messages
		chatArea.setEditable(false);
		chatArea.setLineWrap(true);

		JScrollPane scrollPaneRight = new JScrollPane(chatArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPaneRight.setPreferredSize(new Dimension(300, this.getHeight()));
		msgPanel.add(scrollPaneRight, BorderLayout.CENTER);

		this.setSize(new Dimension(800, 600));// set 返個window
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * it will be invoked if the user selected the specific color through the color picker
	 * @param colorValue - the selected color
	 */
	public void selectColor(int colorValue) {
		SwingUtilities.invokeLater(()->{
			selectedColor = colorValue;
			pnlColorPicker.setBackground(new Color(colorValue));
		});
	}

	/**
	 * it will be invoked if the user inputted text in the message field
	 * @param text - user inputted text
	 */
	private void onTextInputted(String text) {
		//chatArea.setText(chatArea.getText() + text + "\n");
		//send out the message to the server
		try {
			text=this.username+":"+text;
			out.writeInt(0);//indicate the user write a text
			out.writeInt(text.length());
			out.write(text.getBytes());
			out.flush();
		}
		catch(IOException e ){
			//you need to do something to handle the error


		}
	}

	/**
	 * change the color of a specific pixel
	 * @param col, row - the position of the selected pixel
	 */
	public void paintPixel(int col, int row) {
		paintPixel(selectedColor,col,row);
	}
	public void paintPixel(int color,int col, int row) {
		if (col >= data.length || row >= data[0].length) return;
		data[col][row] = color;
		paintPanel.repaint(col * blockSize, row * blockSize, blockSize, blockSize);
		;}


	/**
	 * change the color of a specific area
	 * @param col, row - the position of the selected pixel
	 * @return a list of modified pixels
	 */
	public List paintArea(int col, int row) throws IOException {
		List<Point>op=paintArea(selectedColor,col,row);

		for (int i=0;i<op.size();i++){
			out.writeInt(1);
			out.writeInt(selectedColor);
			out.writeInt(op.get(i).x);
			out.writeInt(op.get(i).y);
		}
		return op;
	}

	private List paintArea(int color,int row,int col){

		LinkedList<Point> filledPixels = new LinkedList<Point>();

		if (col >= data.length || row >= data[0].length) return filledPixels;

		int oriColor = data[col][row];
		LinkedList<Point> buffer = new LinkedList<Point>();

		if (oriColor != color) {
			buffer.add(new Point(col, row));

			while(!buffer.isEmpty()) {
				Point p = buffer.removeFirst();
				int x = p.x;
				int y = p.y;

				if (data[x][y] != oriColor) continue;

				data[x][y] = color;
				filledPixels.add(p);

				if (x > 0 && data[x-1][y] == oriColor) buffer.add(new Point(x-1, y));
				if (x < data.length - 1 && data[x+1][y] == oriColor) buffer.add(new Point(x+1, y));
				if (y > 0 && data[x][y-1] == oriColor) buffer.add(new Point(x, y-1));
				if (y < data[0].length - 1 && data[x][y+1] == oriColor) buffer.add(new Point(x, y+1));
			}
			paintPanel.repaint();
		}
		return filledPixels;
	}
	private int getRandomSparkleColor() {
		Random random = new Random();
		int red = random.nextInt(256); // Generate a random value between 0 and 255
		int green = random.nextInt(256);
		int blue = random.nextInt(256);
		int sparkleColor = (red << 16) | (green << 8) | blue; // Combine the RGB components into a color
		return sparkleColor;
	}

	/**
	 * set pixel data and block size
	 * @param data
	 * @param blockSize
	 */
	public void setData(int[][] data, int blockSize) {
		this.data = data;
		this.blockSize = blockSize;
		paintPanel.setPreferredSize(new Dimension(data.length * blockSize, data[0].length * blockSize));
		paintPanel.repaint();
	}
}