import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.ArrayList;

class UnitTests {
    @BeforeAll
    public static void setUp() {
        
    }
    
    @AfterAll
    public static void tearDown() {
        
    }
    
    // Begin PortScanner Tests.
    
    @Test
    public void test_port_scanner_should_pass() {
        ArrayList<Thread> threads = new ArrayList<Thread>();
        List<Integer> openPorts = Arrays.asList(80, 9999, 8002, 8003, 8004);
        
        // Re-direct System.out to buffer.
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        
        // Start test server threads.
        for(Integer port : openPorts) {
          Thread thread = new Thread() {
              public void run() {
                  try {
                      ServerSocket server = new ServerSocket(port);
                      while(true) {
                          Socket client = server.accept();
                      }
                  } catch(Exception e) { System.out.println(e); }
              }
          };
          threads.add(thread);
        }
        
        for(Thread thread : threads) {
            thread.start();
        }
        
        String[] args = {"PortScanner", "--hosts", "127.0.0.1-127.0.0.2,127.0.0.3", "--ports", "80,8000-8005,9999"};
        PortScanner ps = new PortScanner();
        ps.start(args);
        
        // Stop all test server threads.
        for(Thread thread : threads) {
            thread.interrupt();
        }
        
        // 1 second delay to wait for server threads.
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch(Exception e) { ; }
        
        // Reconnect System.out to stdout.
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));
        
        String output = buffer.toString();
        //System.out.print(output);
        buffer.reset();
        
        // Assert ports open.
        assert(output.indexOf("Port 80 is OPEN") > 0);
        assert(output.indexOf("Port 9999 is OPEN") > 0);
        assert(output.indexOf("Port 8002 is OPEN") > 0);
        assert(output.indexOf("Port 8003 is OPEN") > 0);
        assert(output.indexOf("Port 8004 is OPEN") > 0);
        
        // Assert ports closed.
        assert(output.indexOf("Port 8000 is OPEN") < 0);
        assert(output.indexOf("Port 8001 is OPEN") < 0);
        assert(output.indexOf("Port 8005 is OPEN") < 0);
    }
    
    // End PortScanner Tests.
    

    // Begin IPv4Address Tests.
    
    @Test
    public void test_ipv4address_constructor_should_pass() {
        String ipString = "1.1.1.1";
        IPv4Address ip = new IPv4Address(ipString);
        assertNotNull(ip);
        assertEquals(ipString, ip.toString());
    }
    
    @Test
    public void test_ipv4address_constructor_should_fail() {
        String ipString = "999.999.999.999";
        IPv4Address ip = new IPv4Address(ipString);
        assertEquals("", ip.toString());
    }
    
    @Test
    public void test_ipv4address_get_next_address_should_pass() {
        String ipString = "1.1.1.1";
        IPv4Address ip = new IPv4Address(ipString);
        try {
          assertEquals("1.1.1.2", ip.getNextAddress().toString());
        } catch(Exception e) { ; }
    }
    
    @Test
    public void test_ipv4address_get_next_address_reset_should_pass() {
        String ipString = "1.1.1.255";
        IPv4Address ip = new IPv4Address(ipString);
        try {
        assertEquals("1.1.2.0", ip.getNextAddress().toString());
        } catch(Exception e) { ; }
    }
    
    // End IPv4Address Tests.
    
    
    // Begin ArgParser Tests.
    
    @Test
    public void test_parse_args_valid_host_and_port_should_pass() {
        ArgParser ap = new ArgParser();
        String[] args = {"PortScanner", "--hosts", "127.0.0.1", "--ports", "22"};
        String error = ap.parseArgs(args);
        assertNull(error);
    }
    
    @Test
    public void test_parse_args_missing_ports_parameter_should_pass() {
        ArgParser ap = new ArgParser();
        String[] args = {"PortScanner", "--hosts", "127.0.0.1"};
        String error = ap.parseArgs(args);
        assertNull(error);
    }
    
    @Test
    public void test_parse_args_invalid_host_should_fail() {
        ArgParser ap = new ArgParser();
        String[] args = {"PortScanner", "--hosts", "300.0.0.1", "--ports", "22"};
        String error = ap.parseArgs(args);
        assertNotNull(error);
    }
    
    @Test
    public void test_parse_args_invalid_port_should_fail() {
        ArgParser ap = new ArgParser();
        String[] args = {"PortScanner", "--hosts", "127.0.0.1", "--ports", "65536"};
        String error = ap.parseArgs(args);
        assertNotNull(error);
    }
    
    @Test
    public void test_parse_args_valid_range_should_pass() {
        ArgParser ap = new ArgParser();
        String[] args = {"PortScanner", "--hosts", "127.0.0.1-127.0.0.2", "--ports", "18-22"};
        String error = ap.parseArgs(args);
        assertNull(error);
    }
    
    @Test
    public void test_parse_args_invalid_host_range_outside_should_fail() {
        ArgParser ap = new ArgParser();
        String[] args = {"PortScanner", "--hosts", "128.0.0.3-300.0.0.2", "--ports", "18-22"};
        String error = ap.parseArgs(args);
        assertNotNull(error);
    }
    
    @Test
    public void test_parse_args_invalid_host_range_reversed_should_fail() {
        ArgParser ap = new ArgParser();
        String[] args = {"PortScanner", "--hosts", "128.0.0.3-127.0.0.2", "--ports", "18-22"};
        String error = ap.parseArgs(args);
        assertNotNull(error);
    }
    
    @Test
    public void test_parse_args_invalid_port_range_outside_should_fail() {
        ArgParser ap = new ArgParser();
        String[] args = {"PortScanner", "--hosts", "127.0.0.1-127.0.0.2", "--ports", "18-65536"};
        String error = ap.parseArgs(args);
        assertNotNull(error);
    }
    
    @Test
    public void test_parse_args_invalid_port_range_reversed_should_fail() {
        ArgParser ap = new ArgParser();
        String[] args = {"PortScanner", "--hosts", "127.0.0.1-127.0.0.2", "--ports", "22-18"};
        String error = ap.parseArgs(args);
        assertNotNull(error);
    }
    
    @Test
    public void test_parse_args_missing_hosts_parameter_should_fail() {
        ArgParser ap = new ArgParser();
        String[] args = {"PortScanner", "--ports", "22"};
        String error = ap.parseArgs(args);
        assertNotNull(error);
    }
    
    // End ArgParser Tests.
    
}