/**
 * @author nlo
 * @version 2.0
 * Java v14
 * 
 * PortScanner is a simple Java application for determining if network ports are
 * open for a list of hosts. It takes command-line arguments allowing users to
 * specify both individual and a range of hosts/ports.
 * 
 * Hosts and ports can be specified individually or with a hyphen to denote a range.
 * Example: `javac ... PortScanner --hosts=127.0.0.1-127.0.0.4,127.0.0.7 --ports=80,443,8000-8005,9999`.
 * Enter `--help` to see a list of commands.
 */

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

class PortScanner extends Thread
{
    private static ArrayList<String> hostList;
    private static ArrayList<Integer> portList;
    private static Queue<Integer> resultQueue;

    /**
     * Constructor.
     */
    public PortScanner() {
        hostList = new ArrayList<String>();
        portList = new ArrayList<Integer>();
        resultQueue = new ConcurrentLinkedQueue<Integer>();
    }
	
    /**
     * Main entry point.
     * @param  args  String[]  command line arguments.
     */
    public static void main(String[] args) {
        PortScanner.start(args);
    }
	
    /**
     * Parses the arguments and returns errors if any. Starts the scan.
     * @param  args  String[]  command line arguments.
     */
    public static void start(String[] args) {
        ArgParser ap = new ArgParser();
        PortScanner ps = new PortScanner();
        String error = ap.parseArgs(args);
        if(error != null) {
            System.out.println(error);
            System.exit(1);
        }
        ps.startScan(ap);
    }
	
    /**
     * Scans all hosts and ports provided.
     * @throws  IOException             indicates no port found.
     * @param   ap           ArgParser  argparser object.
     */
    private void startScan(ArgParser ap) {
        int openCount = 0;
        ArrayList<Integer> portList = ap.getPortList();
        ArrayList<String> hostList = ap.getHostList();
        for(String host : hostList) {
            for(int port : portList) {
                ScanThread scanThread = new ScanThread();
                scanThread.run(host, port, resultQueue);
            }
            
            ArrayList<Integer> openPorts = new ArrayList<Integer>();
            while(!resultQueue.isEmpty()) {
                Integer result = resultQueue.poll();
                openPorts.add(result);
            }
            
            System.out.println("Scan on host " + host + " found " + openPorts.size() + " open port(s).");
            for(int openPort : openPorts) {
              System.out.println("Port " + openPort + " is OPEN");
            }
            System.out.println();
        }
    }
}

/**
 * Threaded scan for each port.
 */
class ScanThread extends Thread {
    
    /**
     * Threaded function runner.
     * @param  ip           ip              address as a string.
     * @param  port         int             the TCP port number.
     * @param  resultQueue  Queue<Integer>  finished results queue.
     */
    public void run(String ip, int port, Queue<Integer> resultQueue) {
        Socket sock = null;
        try {
            InetSocketAddress target = new InetSocketAddress(InetAddress.getByName(ip), port);
            sock = new Socket();
            sock.connect(target, 200);
            resultQueue.add(port);
        }
        catch(UnknownHostException e) {
            System.out.println(e);
        }
        catch(IOException e) {
            ;  // Port is closed.
        }
        finally {
            try {
                sock.close();
            }
            catch(Exception e) {
                ;
            }
        }
    }
}
