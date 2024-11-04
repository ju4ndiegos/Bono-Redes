package bonoRedes;

import java.net.*;
import java.util.List;
import java.io.*;

public class Productor {

  // Los argumentos proporcionan el mensaje y el nombre del servidor
  public static void main(String args[]) {
    try {
      DatagramSocket socketUDP = new DatagramSocket();
      socketUDP.setSoTimeout(1000); // Configurar el timeout a 1 segundo
      byte[] mensaje = "hello".getBytes();
      InetAddress hostServidor = descubrirBroadcastAddress(); // Asumimos que esta función devuelve la IP correcta
      int puertoServidor = 6789;

      while (true) {
        try {
          // Construimos un datagrama para enviar el mensaje al servidor
          DatagramPacket peticion = new DatagramPacket(mensaje, mensaje.length, hostServidor, puertoServidor);

          // Enviamos el datagrama
          socketUDP.send(peticion);

          // Construimos el DatagramPacket que contendrá la respuesta
          byte[] bufer = new byte[1000];
          DatagramPacket respuesta = new DatagramPacket(bufer, bufer.length);

          // Intentamos recibir la respuesta
          socketUDP.receive(respuesta);

          // Enviamos la respuesta del servidor a la salida estándar
          System.out.println("Respuesta: " + new String(respuesta.getData(), 0, respuesta.getLength()));

        } catch (SocketTimeoutException e) {
          // Si se agota el tiempo de espera, volvemos a enviar el mensaje
          System.out.println("Tiempo de espera agotado. Volviendo a enviar el mensaje...");
        }

        // Espera entre intentos
        Thread.sleep(500);
      }
    } catch (SocketException e) {
      System.out.println("Socket: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("IO: " + e.getMessage());
    } catch (InterruptedException e) {
      System.out.println("Espera: " + e.getMessage());
    }
  }

  private static InetAddress descubrirBroadcastAddress() {
    InetAddress hostServidor = null;
    try {
      // Obtener la IP local
      InetAddress localHost = InetAddress.getLocalHost();

      // Buscar la interfaz de red correspondiente a la IP local
      NetworkInterface networkInterface = NetworkInterface.getByInetAddress(localHost);
      if (networkInterface == null) {
        System.out.println("No se encontró la interfaz de red para la IP local.");
        return null;
      }

      // Obtener la lista de direcciones y prefijos de subred de la interfaz
      List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
      for (InterfaceAddress interfaceAddress : interfaceAddresses) {
        if (interfaceAddress.getAddress().equals(localHost)) {
          // prefijo
          int prefixLength = interfaceAddress.getNetworkPrefixLength();

          // máscara de subred
          String subnetMask = calculateSubnetMask(prefixLength);

          // broadcast
          String broadcastAddress = calculateBroadcastAddress(localHost, subnetMask);
          hostServidor = InetAddress.getByName(broadcastAddress);
          break;
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return hostServidor;
  }

  private static String calculateSubnetMask(int prefixLength) {
    int mask = 0xffffffff << (32 - prefixLength);
    int part1 = (mask >> 24) & 0xff;
    int part2 = (mask >> 16) & 0xff;
    int part3 = (mask >> 8) & 0xff;
    int part4 = mask & 0xff;
    return part1 + "." + part2 + "." + part3 + "." + part4;
  }

  private static String calculateBroadcastAddress(InetAddress ip, String subnetMask) {
    String[] ipParts = ip.getHostAddress().split("\\.");
    String[] maskParts = subnetMask.split("\\.");
    StringBuilder broadcastAddress = new StringBuilder();

    for (int i = 0; i < 4; i++) {
      int ipPart = Integer.parseInt(ipParts[i]);
      int maskPart = Integer.parseInt(maskParts[i]);
      int broadcastPart = ipPart | (~maskPart & 0xff);
      broadcastAddress.append(broadcastPart);
      if (i < 3)
        broadcastAddress.append(".");
    }
    return broadcastAddress.toString();
  }
}