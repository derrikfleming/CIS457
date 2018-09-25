
import java.net.*; // for InetAddress
public class IPFinder2 {
public static void main(String[] args) {
// Get name and IP address of the local host
try {
InetAddress address = InetAddress.?;
// Gets the local host's IP address in object format
System.out.println("Local Host:");

System.out.println("\t" + address.?);// Gets the host name for this IP address.
} catch (UnknownHostException e) {
System.out.println("Unable to determine this host's address");
}
for (int i = 0; i < args.length; i++) {
// Get name(s)/address(es) of hosts given on command-line
try {
// Create an array of InetAddress instances for the specified host
InetAddress[] addressList = InetAddress.?;
System.out.println(args[i] + ":");
// Print the first name and all associated IP addresses. Assume array contains at least one entry.
System.out.println("\t" + addressList[0].?);
for (int j = 0; j < addressList.length; j++)
System.out.println("\t" + addressList[j].?);
} catch (UnknownHostException e) {
System.out.println("Unable to find address for " + args[i]);
}
}
}
}
