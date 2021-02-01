/**
 * Custom exception class for invalid IPv4 addresses.
 * @param  s  String  exception message.
 */
class InvalidIPv4Address extends Exception {
    InvalidIPv4Address(String s) {
        super(s);  
    }
}
