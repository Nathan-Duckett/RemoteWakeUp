package page.ndser.remotewakeup.model;

/**
 * Represents a WakeupHistory object storing the details from previous wake up attempts.
 *
 * @author Nathan Duckett
 */
public class WakeupHistory {
    private byte[] mac;
    private String macString;
    private String hostname;
    private int port;

    /**
     * Create a new WakeupHistory instance with the following details. Accepts default strings
     * and converts to expected values.
     *
     * @param mac The mac address in the form "aa:bb:cc:dd:ee"
     * @param hostname String hostname which the request is forwarded to.
     * @param port Port to send the request through.
     */
    public WakeupHistory(String mac, String hostname, String port) {
        this.macString = mac;
        String[] splitMac = mac.split(":");
        this.mac = new byte[6];
        for (int i = 0; i < 6; i++) {
            // 16 for hex value parsing
            Integer hex = Integer.parseInt(splitMac[i], 16);
            this.mac[i] = hex.byteValue();
        }

        this.hostname = hostname;
        this.port = Integer.parseInt(port);
    }

    /**
     * Get the mac address of this wakeup call.
     *
     * @return Mac address in byte array form.
     */
    public byte[] getMac() {
        return mac;
    }

    /**
     * Get the hostname the wakeup call was sent to.
     *
     * @return String containing the hostname the wakeup call was made to.
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Get the port the wakeup call was routed through.
     *
     * @return integer representing the port the request was sent to.
     */
    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return "Host: " + hostname + " | MAC: " + macString + " | Port: " + port;
    }
}
