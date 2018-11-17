package network;

import de.lmu.ifi.sep.abalone.network.NetworkUtilities;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

@DisplayName("NetworkUtilities Class")
class NetworkUtilitiesTest {
    @Test
    @DisplayName("getLocalHost()::String")
    void getLocalHostTest() throws UnknownHostException {
        String host = NetworkUtilities.getLocalHost();

        Assertions.assertNotNull(host);
        Assertions.assertFalse(host.endsWith(".1"));
        Assertions.assertNotEquals(InetAddress.getLoopbackAddress(), host);
        Assertions.assertTrue(InetAddress.getByName(host) instanceof Inet4Address);
    }

    @Test
    @DisplayName("getLocalHostName()::String")
    void getLocalHostNameTest() throws UnknownHostException {
        Assertions.assertEquals(InetAddress.getLocalHost().getHostName(),
                NetworkUtilities.getLocalHostName());
    }

    @Test
    @DisplayName("getDefaultPort()::String")
    void getDefaultPortTest() {
        Assertions.assertEquals("48410", NetworkUtilities.getDefaultPort());
    }

    @Test
    @DisplayName("validatePort(String port)::boolean")
    void validatePortTest() {
        Assertions.assertTrue(NetworkUtilities.validatePort("12345"));
        Assertions.assertTrue(NetworkUtilities.validatePort(NetworkUtilities.getDefaultPort()));

        Assertions.assertFalse(NetworkUtilities.validatePort("-1"));
        Assertions.assertFalse(NetworkUtilities.validatePort("value"));
        Assertions.assertFalse(NetworkUtilities.validatePort("123s"));
        Assertions.assertFalse(NetworkUtilities.validatePort("9.9"));
        Assertions.assertFalse(NetworkUtilities.validatePort(""));
        Assertions.assertFalse(NetworkUtilities.validatePort("1000"));
        Assertions.assertFalse(NetworkUtilities.validatePort("10"));
        Assertions.assertFalse(NetworkUtilities.validatePort("123456"));
    }

    @Test
    @DisplayName("validateHost(String host)::boolean")
    void validateHostTest() throws UnknownHostException {
        Assertions.assertTrue(NetworkUtilities.validateHost("127.0.0.1"));
        Assertions.assertTrue(NetworkUtilities.validateHost(""));
        Assertions.assertTrue(NetworkUtilities.validateHost(NetworkUtilities.getLocalHost()));
        Assertions.assertTrue(NetworkUtilities.validateHost(NetworkUtilities.getLocalHostName()));

        Assertions.assertFalse(NetworkUtilities.validateHost("0.0.0.1"));
        Assertions.assertFalse(NetworkUtilities.validateHost("255.255.255.1"));
    }
}
