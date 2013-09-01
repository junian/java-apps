package org.junian.jtrmailclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.ssl.SSLSocketFactory;

/**
 *
 * @author bakajunichi
 */
public class Pop3Client {

    private String ipAddress;
    private int port;

    private String username;
    private String password;
    private String messagePath;
    private Socket pop3Socket;
    private PrintStream prStream;
    private DataInputStream diStream;
    private boolean connected = false;
    private boolean useSSL;

    public Pop3Client(String ipAddress, int port) throws UnknownHostException, IOException{
        this.ipAddress = ipAddress;
        this.port = port;
        this.useSSL = false;
        this.messagePath = System.getProperty("user.dir");
        connect();
    }

    public Pop3Client(String ipAddress, int port, boolean useSSL) throws UnknownHostException, IOException{
        this.ipAddress = ipAddress;
        this.port = port;
        this.useSSL = useSSL;
        this.messagePath = System.getProperty("user.dir");
        connect();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getMessagePath() {
        return messagePath;
    }

    public boolean isConnected(){
        return connected;
    }

    /**
     * 
     * @return
     */
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * get server port
     * @return port number
     */
    public int getPort() {
        return port;
    }

    /**
     * connect to server
     * @return true if connected
     * @throws UnknownHostException
     * @throws IOException
     */
    public boolean connect() throws UnknownHostException, IOException{
        System.out.println("Connecting to server [" + getIpAddress() + ":" + getPort() +"]... ");
        if(!useSSL){
            pop3Socket = new Socket(getIpAddress(), getPort());
        }
        else{
            SSLSocketFactory sfact = (SSLSocketFactory) SSLSocketFactory.getDefault();
            pop3Socket = sfact.createSocket(ipAddress, port);
        }
        diStream = new DataInputStream(new BufferedInputStream(pop3Socket.getInputStream()));
        prStream = new PrintStream(new BufferedOutputStream(pop3Socket.getOutputStream()),true);
        System.out.println("Server connected...");
        connected = true;
        getServerResponse();
        return pop3Socket!=null && pop3Socket.isConnected();
    }

    /**
     * close the connection
     * @return true if closed
     * @throws IOException
     */
    public boolean close() throws IOException{
        System.out.println("Closing connection...");
        sendCommand("QUIT");
        pop3Socket.close();
        System.out.println("Connection with server closed...");
        connected = false;
        return pop3Socket!=null && !pop3Socket.isConnected();
    }

    /**
     * Get Mail statistics
     * @return String[0] mail total, String[1] octet total
     * @throws IOException
     */
    public String[] getStatistics() throws IOException{
        String s = sendCommand("STAT");
        return s.substring(4, s.length()).split(" ");
    }

    public Header getHeader(int messageNumber) throws IOException {
        String s = sendCommand("TOP " + messageNumber + " 0");
        Header h = null;
        if(s.startsWith("+OK")) {
            h = new Header(messageNumber);
            readHeaders(h);
            while(!getServerResponse().equalsIgnoreCase("."));
        }
        return h;
    }
    
    /**
     * login into server
     * @param username
     * @param password
     * @return true if logged in
     * @throws IOException
     */
    public boolean login(String username, String password) throws IOException {
        String s;
        this.username = username;
        this.password = password;
        s = sendCommand("USER " + username);
        if(s.startsWith("-ERR"))
            return false;
        s = sendCommand("PASS " + password);
        if(s.startsWith("-ERR"))
            return false;
        return true;
    }

    /**
     * Just send noop
     * @return +OK
     * @throws IOException
     */
    public String noop() throws IOException{
        return sendCommand("NOOP");
    }

    /**
     * cancel delete action
     * @throws IOException
     */
    public void undoDelete() throws IOException {
        sendCommand("RSET");
    }

    /**
     * get octet of one message
     * @param messageNumber
     * @return octet of message, -1 if no such message
     * @throws IOException
     */
    public int getList(int messageNumber) throws IOException{
        String s = sendCommand("LIST " + messageNumber);
        if(s.startsWith("+OK")) {
            String[] ss = s.split(" ");
            return Integer.parseInt(ss[2]);
        }
        return -1;
    }

    /**
     * Get Message list and its octet
     * @return Message list with octet
     * @throws IOException
     */
    public String[] getList() throws IOException{
        int tot = Integer.parseInt(getStatistics()[0]);
        String res = sendCommand("LIST");
        String[] list = new String[tot];
        if(res.startsWith("+OK")){
            int i=0;
            while(!(res=getServerResponse()).equalsIgnoreCase("."))
                list[i++] = res;
        }
        return list;
    }

    /**
     * retrieve email message from server
     * @param messageNumber
     * @return Message contains header and body, null if there is no such message
     * @throws IOException
     */
    public Message retrieveMessage(int messageNumber) throws IOException{
        String res = sendCommand("RETR " + messageNumber);
        Message msg = null;
        if(res.startsWith("+OK")){
            msg = new Message(messageNumber);
            //get header
            readHeaders(msg.getHeader());
            System.out.println("Saving message to " + messagePath + "\\" + msg.getMessageFile());
            BufferedWriter bw = new BufferedWriter(new FileWriter(messagePath + "\\" + msg.getMessageFile()));
            PrintWriter pw = new PrintWriter(bw);

            //get body
            while(!(res=getServerResponse()).equalsIgnoreCase(".")){
                pw.println(res + "<br />");
            }
            
            pw.close();
        }
        return msg;
    }

    public void readHeaders(Header header) throws IOException {
        String headerName = "", res = "";
        while(!(res=getServerResponse()).equalsIgnoreCase("")){
            int ind = res.indexOf(":");
            if(ind > -1) {
                headerName = res.substring(0, ind).toLowerCase().trim();
                header.setHeader(headerName,
                        res.substring(ind+1, res.length()).trim());
            }
            else {
                header.setHeader(headerName, header.getHeader(headerName) + "\n" + res.trim());
            }
        }
    }

    /**
     * Mark message as delete
     * @param messageNumber
     * @return true if message is marked
     * @throws IOException
     */
    public boolean deleteMessage(int messageNumber) throws IOException{
        String s = sendCommand("DELE " + messageNumber);
        if(s.startsWith("-ERR"))
            return false;
        return true;
    }

    /**
     * Send command to server
     * @param command
     * @return String server response
     * @throws IOException
     */
    private String sendCommand(String command) throws IOException{
        if(command.startsWith("PASS"))
            System.out.println("Sending command: PASS *oops, you can't see it*");
        else
            System.out.println("Sending command: " + command);
        prStream.println(command);
        return getServerResponse();
    }

    /**
     * Get responses from server after sending command
     * @return String that represented server response
     * @throws IOException
     */
    private String getServerResponse() throws IOException{
        String recvReply = diStream.readLine();
        System.out.println("Server response: " + recvReply);
        return recvReply;
    }
}
