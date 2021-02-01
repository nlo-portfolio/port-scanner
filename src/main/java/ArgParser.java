import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.cli.*;

/**
 * Class for parsing IPv4 addresses and ranges.
 */
class ArgParser {
    
    private ArrayList<String> hostList;
    private ArrayList<Integer> portList;
    private ArrayList<String> errors;
    
    /**
     * Constructor.
     */
    public ArgParser() {
        hostList = new ArrayList<String>();
        portList = new ArrayList<Integer>();
        errors = new ArrayList<String>();
    }
    
    /**
     * Public access to the list of ports.
     * @return  ArrayList<Integer>  the list of ports.
     */
    public ArrayList<Integer> getPortList() {
        return portList;
    }
    
    /**
     * Public access to the list of hosts.
     * @return  ArrayList<String>  the list of hosts.
     */
    public ArrayList<String> getHostList() {
        return hostList;
    }
    
    /**
     * Parses all arguments supplied to the program.
     * @param   args  String[]  commandline arguments.
     * @return        String    errors if invalid arguments.
     */
    public String parseArgs(String[] args) {
        Options options = new Options();
        Option optionHosts = new Option("h", "hosts", true, "Host IP range to scan. Ex. --range=2.2.2.2,3.3.3.3,4.4.4.4-5.5.5.5 (REQUIRED)");
        optionHosts.setValueSeparator(',');
        optionHosts.setArgs(Option.UNLIMITED_VALUES);
        optionHosts.setRequired(true);
        options.addOption(optionHosts);
        Option optionPorts = new Option("p", "ports", true, "Ports to scan (comma separated). Ex. --ports 22,80,443. Default is 1-1024.");
        optionPorts.setArgs(Option.UNLIMITED_VALUES);
        optionPorts.setValueSeparator(',');
        options.addOption(optionPorts);
        Option optionStealth = new Option("s", "stealth", true, "Scan in stealth mode.");
        optionStealth.setArgs(0);
        options.addOption(optionStealth);
        Option optionVerbose = new Option("v", "verbose", true, "Verbose logging to console.");
        optionVerbose.setArgs(0);
        options.addOption(optionVerbose);
        
        
        CommandLine cmd = null;
        CommandLineParser parser = new DefaultParser();
        try {
            cmd = parser.parse(options, args);
        }
        catch(Exception e) {
            HelpFormatter help = new HelpFormatter();
            StringWriter out = new StringWriter();
            PrintWriter pw = new PrintWriter(out);

            help.printHelp(
                pw, 120, "java PortScanner <options>", "Host parameter is required to run scans.", options, 2, 4,
                "\nExample: PortScanner --hosts=127.0.0.1,127.0.0.2-127.0.0.5,127.0.0.10 --ports=80,443,8000-8005,9999"
            );
            pw.flush();
            return out.toString();
        }
        
        String hostError = parseHosts(cmd.getOptionValues("hosts"));
        String portError = parsePorts(cmd.getOptionValues("ports"));
        if((portError.length() > 0) || (hostError.length() > 0))
            return portError + "\n" + hostError;
        return null;
    }
    
    /**
     * Parses the port arguments.
     * @param   args    String[]  the list of ports and ranges.
     * @return  String            errors if invalid arguments.
     */
    private String parsePorts(String[] args) {
        if(args == null) {
            for (int i = 0; i < 1024; i++)
                portList.add(i);
            return "";
        }
            
        for(String arg : args) {
            String[] ranges = arg.split("-");
            
            /* Check that arguments are valid integers and in the TCP port range. */
            for(String i : ranges) {
                Integer n = null;
                try {
                    n = Integer.parseInt(i);
                }
                catch(Exception e) {
                    return "ERROR: Invalid integer supplied for port range.";
                }
                
                if((n < 0) || (n > 65535))
                    return "ERROR: Invalid integer supplied for port range (ports must be in range from 0-65535).";
            }
            
            /* Single port. */
            if(ranges.length == 1) {
                portList.add(Integer.parseInt(ranges[0]));
                continue;
            }
            
            if(ranges.length != 2)
                return "ERROR: Invalid range format (must be '<integer>-<integer>' or <integer>-<integer>,<integer>-<integer>,... with no quotes).";
            
            /* Port range. */
            int rangeStart = Integer.parseInt(ranges[0]);
            int rangeFinish = Integer.parseInt(ranges[1]);
            if(rangeStart > rangeFinish)
                return "ERROR: Invalid character supplied for port range (finish must be less than start range).";
            
            for(int i = rangeStart; i <= rangeFinish; i++) {
                if(!portList.contains(i))
                    portList.add(i);
            }
        }
        return "";
    }
    
    /**
     * Parses the host arguments.
     * @param   args  String[]  list of hosts and ranges.
     * @return        String    errors if invalid arguments.
     */
    private String parseHosts(String[] args) {
        for(String arg : args) {
            String ip = null;
            String[] ranges = arg.split("-");

            /* Check that arguments are valid TCP IP addresses. */
            for(String i : ranges) {
                if(IPv4Address.validateIPAddress(i) == null)
                    return "ERROR: Invalid host IP address format.";
            }
            
            /* Single machine. */
            if(ranges.length == 1) {
                if(!hostList.contains(ranges[0].toString()))
                  hostList.add(ranges[0]);
                continue;
            }
            
            if(ranges.length != 2)
                return "ERROR: Invalid host IP address range.";
            
            /* Machine range. */
            IPv4Address rangeStart = new IPv4Address(ranges[0]);
            IPv4Address rangeFinish = new IPv4Address(ranges[1]);
            if(rangeStart.compareTo(rangeFinish) > 0)
                return "ERROR: Invalid IP address range specified. Ending IP address must be greater than starting IP address.";
            
            IPv4Address tIP = rangeStart;
            do {
                if(!hostList.contains(tIP.toString()))
                    hostList.add(tIP.toString());
                
                try {
                    tIP = new IPv4Address(tIP.getNextAddress());
                }
                catch(InvalidIPv4Address e) {
                    break;
                }
            } while(tIP.compareTo(rangeFinish) <= 0);
        }
        return "";
    }
}
