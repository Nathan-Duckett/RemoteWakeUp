package page.ndser.remotewakeup.model;

import java.util.Arrays;
import java.util.Objects;

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
    private String friendlyName;

    /**
     * Create a new WakeupHistory instance with the following details. Accepts default strings
     * and converts to expected values.
     *
     * @param mac      The mac address in the form "aa:bb:cc:dd:ee"
     * @param hostname String hostname which the request is forwarded to.
     * @param port     Port to send the request through.
     */
    public WakeupHistory(String friendlyName, String mac, String hostname, String port) {
        this.friendlyName = friendlyName;
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
     * Get the friendlyName of this wakeup call.
     *
     * @return String containing the friendly name for this device.
     */
    public String getFriendlyName() {
        return friendlyName;
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
        return friendlyName + "\nHost: " + hostname + "\nMAC: " + macString + "\nPort: " + port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WakeupHistory that = (WakeupHistory) o;
        return port == that.port &&
                Arrays.equals(mac, that.mac) &&
                Objects.equals(macString, that.macString) &&
                hostname.equals(that.hostname);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(macString, hostname, port);
        result = 31 * result + Arrays.hashCode(mac);
        return result;
    }
}
