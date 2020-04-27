package page.ndser.remotewakeup.util;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import page.ndser.remotewakeup.model.WakeupHistory;

/**
 * Static SendWakeup class to provide utility to take a WakeupHistory and send the request to wake up
 * the specified machine.
 *
 * @author Nathan Duckett
 */
public class SendWakeup {

    /**
     * Send a wakeup signal to the machine specified in the details.
     *
     * @param details WakeupHistory details containing all of the information to wake up a PC.
     * @throws SocketException Thrown when the socket can't be opened.
     * @throws UnknownHostException Thrown when the hostname is invalid/can't be found.
     */
    public static void to(WakeupHistory details) throws SocketException, UnknownHostException {
        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(details.getHostname());

        byte[] buf = constructMessage(details.getMac());

        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, details.getPort());

        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Constructs the Magic Packet which should be sent to the host to wake up.
     *
     * @param mac Mac Address of the machine to wake up in byte array format.
     * @return Byte array containing the content to send through the socket.
     */
    private static byte[] constructMessage(byte[] mac) {
        byte[] buf = new byte[102];

        // Repeat for six insertions of FF characters.
        for (int i = 0; i < 6; i++) {
            buf[i] = (byte) 0xFF;
        }

        // Repeat for 16 insertions of MAC address
        int pos = 6;
        for (int i = 0; i < 16; i++) {
            buf[pos++] = mac[0];
            buf[pos++] = mac[1];
            buf[pos++] = mac[2];
            buf[pos++] = mac[3];
            buf[pos++] = mac[4];
            buf[pos++] = mac[5];
        }

        return buf;
    }
}
