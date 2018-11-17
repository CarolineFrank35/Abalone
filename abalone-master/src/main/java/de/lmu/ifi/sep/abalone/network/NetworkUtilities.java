package de.lmu.ifi.sep.abalone.network;

import java.io.Closeable;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Class provides some relevant functions
 * for validation and network information retrieving
 */
public class NetworkUtilities {
    private static final Logger logger = Logger.getLogger(NetworkUtilities.class.getName());
    private static final int DEFAULT_PORT = 48410;

    /**
     * @return ip address of the current device
     */
    public static String getLocalHost() {
        final Enumeration<NetworkInterface> networkInterfaceEnumeration;
        try {
            networkInterfaceEnumeration = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            e.printStackTrace();
            return null;
        }
        while (networkInterfaceEnumeration.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaceEnumeration.nextElement();
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();

                if (address instanceof Inet4Address && !address.isLoopbackAddress()
                        && !address.getHostAddress().endsWith(".1")) {

                    return address.getHostAddress();
                }
            }
        }
        return null;
    }

    /**
     * @return network name of the current device or 'not available'
     * @throws UnknownHostException if host is not reachable
     */
    public static String getLocalHostName() throws UnknownHostException {
        String hostname = InetAddress.getLocalHost().getHostName();
        return hostname.equals(getLocalHost()) ? "not available" : hostname;
    }

    /**
     * @return default port for the network communication of the application
     */
    public static String getDefaultPort() {
        return String.valueOf(DEFAULT_PORT);
    }

    /**
     * Validates the syntax and the availability of the given port
     *
     * @param port port which is validated
     * @return true if validation was successful, false otherwise
     */
    public static boolean validatePort(String port) {
        assert port != null;

        try {
            return isPortAvailable(Integer.valueOf(port));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates the syntax and the availability of the given host
     *
     * @param host host which is validated
     * @return true if validation was successful, false otherwise
     */
    public static boolean validateHost(String host) {
        assert host != null;

        try {
            return InetAddress.getByName(host).isReachable(1000);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Helper method to close the given closeable and catch unpreventable exceptions
     *
     * @param closeable Closeable which will be closed
     */
    public static void close(Closeable closeable) {
        if (!Objects.isNull(closeable)) {
            try {
                closeable.close();
            } catch (IOException e) {
                logger.info("Intercepted expected but not preventable IOException.");
            }
        }
    }

    /**
     * Validates the availability of the given port
     *
     * @param port port of which the availability is checked
     * @return true if validation was successful, false otherwise
     */
    private static boolean isPortAvailable(int port) {
        if (port < 1200 || port > 60000) {
            return false;
        }

        ServerSocket serverSocket = null;
        DatagramSocket datagramSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            datagramSocket = new DatagramSocket(port);
            datagramSocket.setReuseAddress(true);

            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (datagramSocket != null) {
                datagramSocket.close();
            }

            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }
    }
}
