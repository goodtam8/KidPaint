import javax.sound.sampled.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;


class skt{
    int[][]data;
    int name;
    int width;
    int height;
    public skt(int[][]data,int name,int width,int height){
        this.data=data;
        this.name=name;
        this.width=width;
        this.height=height;
    }


        }
public class Server {

//if we open the new client it should show the latest version of the painting
    //create 3 object?_? one for storing the text
    //one for storing the bucket
    //one for storing the pen
    ServerSocket serverSocket;
    ArrayList<Socket>list=new ArrayList();
    ArrayList<Integer>studio=new ArrayList<>();
    ArrayList<Integer>bool=new ArrayList<>();
    ArrayList<skt>tt=new ArrayList<>();
     HashMap<String, ArrayList<Socket>> em=new HashMap<>();
     public int name;
    public int[][] da=new int[50][50];
    int port=12345;

    public Server(int www) throws IOException, InterruptedException {

        serverSocket=new ServerSocket(port);
        name=www;


        Thread yy=new Thread(()-> {
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket(45678);
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }

            while (true) {
                try {
                    String msg = "studio";
                    String na = String.valueOf(name);

                    DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
                    System.out.println(name);


                    socket.receive(packet);
                    System.out.println("收到");
                    DatagramPacket info = new DatagramPacket(na.getBytes(), na.length(), InetAddress.getByName("255.255.255.255"), packet.getPort());

                    String content = new String(packet.getData(), 0, packet.getLength());

                    if (content.equals(msg)) {
                        socket.send(info);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }});
            yy.start();
            Thread.sleep(2000);




         while (true){
            Socket clientSocket=serverSocket.accept();//main thread of the server





            Thread t=new Thread(()->{
                synchronized (list){
                    list.add(clientSocket);


                }
                System.out.printf("[KidPaintServer] Total %d clients are connected.\n", list.size());

                try {
                    serve(clientSocket);
                }catch(IOException e){}
                list.remove(clientSocket);
                System.out.printf("[KidPaintServer] Total %d clients are connected.\n", list.size());

            });
            t.start();

//        while(true){
//            DatagramSocket socket = new DatagramSocket(45678);
//
//            DatagramPacket packet = new DatagramPacket(new byte[1024], 1024);
//            socket.receive(packet);
//            String reply = String.valueOf(port);
//
//            DatagramPacket sendpacket = new DatagramPacket(reply.getBytes(), reply.length(), packet.getAddress(), packet.getPort());
//            socket.send(sendpacket);
//            socket.close();
//
//
//
//            Socket clientSocket=serverSocket.accept();//main thread of the server
//
//            DataInputStream in=new DataInputStream(clientSocket.getInputStream());
//            DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
//            int yy=in.readInt();
//
//
//            for(int i=0;i<tt.size();i++){
//
//
//            out.writeInt(4);
//            out.writeInt(tt.get(i).name);
//            out.flush();
//
//            }
//
//            Thread t=new Thread(()->{
//                synchronized (list){
//                    list.add(clientSocket);
//
//
//                }
//                System.out.printf("[KidPaintServer] Total %d clients are connected.\n", list.size());
//
//                try {
//                    serve(clientSocket);
//                }catch(IOException e){}
//                list.remove(clientSocket);
//                System.out.printf("[KidPaintServer] Total %d clients are connected.\n", list.size());
//
//            });
//            t.start();


    }}
private void handleChatMessage(DataInputStream in )throws IOException{
    byte[]buffer=new byte[1024];
    int len=in.readInt();
    int v=in.read(buffer,0,len);
    System.out.println(new String(buffer,0,len));
    synchronized (list){
        for (int i=0;i<list.size();i++){
            try{
                Socket s=list.get(i);
                DataOutputStream out=new DataOutputStream(s.getOutputStream());
                out.writeInt(0);
                out.writeInt(len);
                out.write(buffer, 0, len);
                out.flush();



            }

            catch(IOException E) {

            }
        }
    }}
    private void handlePixelMessage(DataInputStream in )throws IOException{
        int color=in.readInt();
        int x=in.readInt();
        int y=in.readInt();
        System.out.println(color);
        da[x][y]=color;
        //data[x][y]=color;
        //system.out.print f can do a debugging purpose

          synchronized (list){
              for(int i=0;i<list.size();i++){
                  Socket s=list.get(i);
                  DataOutputStream out=new DataOutputStream(s.getOutputStream());




                          out.writeInt(1);
                          out.writeInt(color);
                          out.writeInt(x);
                          out.writeInt(y);


                      }



                      }


          }


    private void handleBucketMessage(DataInputStream in )throws IOException{
        int color=in.readInt();
        int x=in.readInt();
        int y=in.readInt();

        //data[x][y]=color;
        //system.out.print f can do a debugging purpose

        synchronized (list){
            for(int i=0;i<list.size();i++){
                Socket s=list.get(i);
                DataOutputStream out=new DataOutputStream(s.getOutputStream());




                        out.writeInt(2);
                        out.writeInt(color);
                        out.writeInt(x);
                        out.writeInt(y);


                    }

            }
        }


        private void serve(Socket clientSocket) throws IOException {
        DataInputStream in=new DataInputStream(clientSocket.getInputStream());// one client collect the message and broadcast them to different client
        byte[]buffer=new byte[1024];
        while(true){
            int type=in.readInt();
            // seperate them for one method receive one type of message
            switch(type){
                case 0:
                    handleChatMessage(in);
                    break;
                case 1:
                    handlePixelMessage(in);
                    break;
                case 2:
                    handleBucketMessage(in);
                    break;
                    //other message



                case 4:
                    printStudio(in);
                    break;
                case 5:
                    System.out.println("print pixel");

                    printpixel(in);
                    break;
                case 88:
                    sendalert();
                    break;

                case 98:
                    refresh();


                    //the first time enter to the server
                default:

            }

            }}
    private void refresh(){

        synchronized (list){
            for (int i=0;i<list.size();i++){
                for(int j=0;j<bool.size();j++){
                    if(bool.get(j)==1)
                        System.out.println("It is yes");
                    return;
                    }



                }
            }}
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
    private void sendalert(){
        synchronized (list){
            for (int i=0;i<list.size();i++){
                try {
                    Socket s = list.get(i);
                    DataOutputStream out = new DataOutputStream(s.getOutputStream());
                    out.writeInt(88);

                    out.flush();
                }
                catch(IOException E){
                    System.out.println("The client is disconnected already");
                }
            }

    }}


    private void printStudio(DataInputStream  in) throws IOException {
        String type=String.valueOf(in.readInt());
        ArrayList<Socket> hm=em.get(type);
        for(int i=0;i<hm.size();i++){
            Socket s=hm.get(i);
            DataOutputStream out=new DataOutputStream(s.getOutputStream());

            out.writeInt(7);
            out.writeInt(da.length);
            out.writeInt(da[1].length);



    }}

    private void printpixel(DataInputStream in) throws IOException {

        synchronized (list){

           for(int i=0;i<list.size();i++){
               Socket s=list.get(i);
            DataOutputStream out=new DataOutputStream(s.getOutputStream());



                    for(int k=0;k<da.length;k++){
                        for(int l=0;l<da[k].length;l++){
                            out.writeInt(1);
                            out.writeInt(da[k][l]);
                            out.writeInt(k);
                            out.writeInt(l);


                        }
                    }}


                    }
            }







}
