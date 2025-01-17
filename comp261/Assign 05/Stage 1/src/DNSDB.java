import javax.swing.*;
import java.io.*;
import java.util.Map;
import java.util.Scanner;


public class DNSDB {

    private BPlusTree<Integer, String> hostNames;
    private BPlusTree<String, Integer> ipAddresses;

    public DNSDB() {
        hostNames = new BPlusTree<>();
        ipAddresses = new BPlusTree<>();
    }

    public static Integer stringToIP(String text) {
        String[] bytes = text.trim().split("\\.");

        if (bytes.length != 4)
            return null;

        try {
            int ip = 0;
            for (int i = 0; i < 4; i++) {
                int b = Integer.parseInt(bytes[i].trim());
                ip |= b << (24 - (8 * i));
            }

            return ip;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static String IPToString(int ip) {
        StringBuilder sb = new StringBuilder();
        for (int i = 3; i >= 0; i--) {
            sb.append(((ip >> (i * 8)) & 0xFF));
            if (i > 0)
                sb.append('.');
        }
        return sb.toString();
    }

    /**
     * Loads all the host-IP pairs into the B+ trees.
     *
     * @param fileName
     */
    public void load(File file) {
        if (!file.exists()) {
            System.out.println(file + " not found");
            return;
        }
        BufferedReader data;
        System.out.println("Loading....");
        try {
            data = new BufferedReader(new FileReader(file));
            while (true) {
                String line = data.readLine();
                if (line == null) {
                    break;
                }
                String[] pair = line.split("\t");
                String host = pair[0];
                int IP = stringToIP(pair[1]);
                hostNames.put(IP, host);
                ipAddresses.put(host, IP);
            }
        } catch (IOException e) {
            System.out.println("Fail: " + e);
        }
        System.out.println("Loading Done");



    }

    /**
     * Finds an IP address given the host name.
     *
     * @param hostName
     * @return integer representation of an IP address, null if not found.
     */
    public Integer findIP(String hostName) {
        return ipAddresses.find(hostName);
    }

    /**
     * Finds the host name given the IP address.
     *
     * @param ip
     * @return null if not found
     */
    public String findHostName(int ip) {
        return hostNames.find(ip);
    }

    /**
     * Tests whether the given IP-name pair is valid.
     *
     * @param ip       integer representation of an IP address
     * @param hostName
     * @return true if valid, false otherwise
     */
    public boolean testPair(int ip, String hostName) {
        String host = findHostName(ip);
        if (host == null) {
            Integer foundIP = findIP(hostName);
            if (foundIP == null)
                return false;
            return ip == foundIP;
        } else {
            return host.equals(hostName);
        }
    }

    /**
     * Tests whether the given name-IP pair is valid.
     *
     * @param hostName
     * @param ip       integer representation of an IP address
     * @return true if valid, false otherwise
     */
    public boolean testPair(String hostName, int ip) {
        if (hostName == null) {
            throw new IllegalArgumentException();
        }
        Integer foundIP = findIP(hostName);
        if (foundIP == null) {
            String foundName = findHostName(ip);
            if (foundName == null)
                return false;
            return hostName.equals(foundName);
        } else {
            return ip == foundIP;
        }
    }


    // FOR MARKING! PLEASE DO NOT MODIFY.

    /**
     * Adds an host-IP pair to the database (ie, to both B+ trees)
     *
     * @param hostName
     * @param ip
     * @return whether successfully added to the database
     */
    public boolean add(String hostName, int ip) {
        return ipAddresses.put(hostName, ip) && hostNames.put(ip, hostName);
    }


    // Utilities

    /**
     * Prints (to System.out) all the pairs in the HostNames index.
     */
    public void iterateAll() {
        //YOUR CODE HERE
        //You will need to add methods to the BPlusTree... classes.
        System.out.println("Host names");
        int count = 0;
        for (Map.Entry val : hostNames) {
            // System.out.println(val.getKey() + " -> " + val.getValue());
            count++;
        }
        System.out.println("Hostname count = " + count);
    }

    /**
     * Look up all the values in the file. If the value is not present, then
     * it will print that value to System.out.
     */
    public void testAllPairs(File file) {
        System.out.println("Starting Test");
        try {
            Scanner scan = new Scanner(file);
            while (scan.hasNextLine()) {
                String[] line = scan.nextLine().split("\t");
                if (line.length != 2)
                    continue;
                int ip = 0;
                String host = null;
                // test for IP address
                if (line[0].matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
                    ip = stringToIP(line[0].trim()); // make sure you test IP \t Domain format
                    host = line[1].trim();
                } else if (line[1].matches("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}")) {
                    ip = stringToIP(line[1].trim());
                    host = line[0].trim();
                } else {
                    System.out.println("Failed to parse: " + line[0]  + " " + line[1]);
                    continue;
                }
                if (host == null) {
                    System.out.println("Failed to parse: " + line[0]  + " " + line[1]);
                    continue;
                }

                if (!testPair(ip, host)) {
                    System.out.println("Missing: " + IPToString(ip) + " -> " + host);
                    testPair(ip, host);
                }
                if (!testPair(host, ip)) {
                    System.out.println("Missing: " + host + " -> " + IPToString(ip));
                    testPair(host, ip);
                }
            }
            scan.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Completed Test");
    }
}
