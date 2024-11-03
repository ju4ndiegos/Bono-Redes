package bonoRedes;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class test {

    public static void main(String[] args) {
        try {   
            // Obtener la IP local
            InetAddress localHost = InetAddress.getLocalHost();
            String localIP = localHost.getHostAddress();
            System.out.println("IP local: " + localIP);

            // Determinar la subred a partir de la IP local
            String subnet = localIP.substring(0, localIP.lastIndexOf("."));
            System.out.println("Escaneando la subred: " + subnet);

            List<String> activeIPs = new ArrayList<>();

            // Escanear direcciones IP del 1 al 254
            for (int i = 1; i < 255; i++) {
                String host = subnet + "." + i;
                InetAddress address = InetAddress.getByName(host);

                // Intentar alcanzar la IP
                if (address.isReachable(100)) {  // Tiempo de espera en milisegundos
                    System.out.println("Dispositivo encontrado: " + host);
                    activeIPs.add(host);
                }
            }

            System.out.println("Dispositivos activos en la red:");
            for (String ip : activeIPs) {
                System.out.println(ip);
            }

        } catch (IOException e) {
            System.out.println("Ocurrió un error: " + e.getMessage());
        }
    }
}

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.InterfaceAddress;
import java.util.Enumeration;
import java.util.List;

public class NetworkInfo {

    public static void main(String[] args) {
        try {
            // Obtener la IP local
            InetAddress localHost = InetAddress.getLocalHost();
            System.out.println("Dirección IP Local: " + localHost.getHostAddress());

            // Buscar la interfaz de red correspondiente a la IP local
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
            if (networkInterface == null) {
                System.out.println("No se encontró la interfaz de red para la IP local.");
                return;
            }

            System.out.println("Interfaz: " + networkInterface.getDisplayName());

            // Obtener la lista de direcciones y prefijos de subred de la interfaz
            List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
            for (InterfaceAddress interfaceAddress : interfaceAddresses) {
                if (interfaceAddress.getAddress().equals(localHost)) {
                    // Longitud del prefijo (número de bits en la máscara de red)
                    int prefixLength = interfaceAddress.getNetworkPrefixLength();
                    System.out.println("Longitud del Prefijo: " + prefixLength);

                    // Calcular y mostrar la máscara de subred
                    String subnetMask = calculateSubnetMask(prefixLength);
                    System.out.println("Máscara de Subred: " + subnetMask);

                    // Calcular y mostrar la dirección de broadcast
                    String broadcastAddress = calculateBroadcastAddress(localHost, subnetMask);
                    System.out.println("Dirección de Broadcast: " + broadcastAddress);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Convertir la longitud del prefijo a una máscara de subred
    private static String calculateSubnetMask(int prefixLength) {
        int mask = 0xffffffff << (32 - prefixLength);
        int part1 = (mask >> 24) & 0xff;
        int part2 = (mask >> 16) & 0xff;
        int part3 = (mask >> 8) & 0xff;
        int part4 = mask & 0xff;
        return part1 + "." + part2 + "." + part3 + "." + part4;
    }

    // Calcular la dirección de broadcast usando la IP y la máscara de subred
    private static String calculateBroadcastAddress(InetAddress ip, String subnetMask) {
        String[] ipParts = ip.getHostAddress().split("\\.");
        String[] maskParts = subnetMask.split("\\.");
        StringBuilder broadcastAddress = new StringBuilder();

        for (int i = 0; i < 4; i++) {
            int ipPart = Integer.parseInt(ipParts[i]);
            int maskPart = Integer.parseInt(maskParts[i]);
            int broadcastPart = ipPart | (~maskPart & 0xff); // Aplicar máscara de broadcast
            broadcastAddress.append(broadcastPart);
            if (i < 3) broadcastAddress.append(".");
        }
        return broadcastAddress.toString();
    }
}
