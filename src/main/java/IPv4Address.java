/**
* Class used for validating and manipulating IPv4 addresses.
*/
class IPv4Address {
    
    int[] numArray;
    String[] strArray;
    String ipString;
    
    /**
     * Constructor.
     * @param  ip  String  ipv4 address as a string.
     */
    public IPv4Address(String ip) {
        ip = IPv4Address.validateIPAddress(ip);
        if(ip != null) {
            ipString = ip;
            strArray = ip.split("\\.");
            numArray = new int[strArray.length];
            for(int i = 0; i < strArray.length; i++)
                numArray[i] = Integer.parseInt(strArray[i]);
        } else {
            ipString = "";
            strArray = null;
            numArray = null;
        }
    }
    
    /**
     * Validates an IPv4 address.
     * @param   ip  String  ipv4 address as a string.
     * @return      String  ipv4 address ass a string if valid, null if invalid.
     */
    public static String validateIPAddress(String ip) {
        if(ip == null)
            return null;
        
        String[] octetArray = ip.split("\\.");
        if(octetArray.length != 4)
            return null;
        
        for(String octet : octetArray) {
            int parsedOctet;
            try {
                parsedOctet = Integer.parseInt(octet);
            }
            catch(Exception e) {
                return null;
            }
                
            if((parsedOctet < 0) || (parsedOctet > 255))
                return null;
        }
        return ip;
    }
    
    /**
     * Comparison method for IPv4 addresses.
     * @param   ip  IPv4Address  string representation of the address to get the next value for.
     * @return      int          positive if this > ip, negative if this < ip, 0 if this == ip.
     */
    public int compareTo(IPv4Address ip) {
        for(int i = 0; i < numArray.length; i++) {
            if(numArray[i] != ip.numArray[i])
                return (numArray[i] - ip.numArray[i]);
        }
        return 0;
    }
    
    /**
     * Public toString() method.
     * @return  String  IPv4 address as a string.
     */
    public String toString() {
        return ipString;
    }

    /**
     * This method returns the next numerically incremented IPv4 address in the range of (0.0.0.0-255.255.255.255.255)
     * Example: 1.1.1.1 will return 1.1.1.2
     *          192.168.0.2 will return 192.168.0.3
     *          217.0.255.255 will return 217.1.0.0
     * @throws  InvalidIPv4Address  if the IPv4Address format is invalid.
     * @return  String              string representation of the next address after the one provided in the arguments.
     */
    public String getNextAddress() throws InvalidIPv4Address
    {
        String[] temp = new String[numArray.length];
        for (int i = 0; i < numArray.length; i++)
            temp[i] = Integer.toString(numArray[i]);

        for(int i = numArray.length - 1; i >= 0; i--) {
            if((numArray[i] >= 0) && (numArray[i] < 255)) {
                temp[i] = Integer.toString(numArray[i] + 1);
                return String.join(".", temp);
            }
            else if(numArray[i] == 255) {
                if(i == 0)
                    throw new InvalidIPv4Address("Invalid IPv4 Address.");
                else
                    temp[i] = "0";
            }
            else
                throw new InvalidIPv4Address("Invalid IPv4 Address.");
        }
        throw new InvalidIPv4Address("Invalid IPv4 Address.");
    }
}
