package bonoRedes;

import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.*;

public class Suscriptor {

  public static void main (String args[]) {

    try {

      DatagramSocket socketUDP = new DatagramSocket(6789);
      byte[] bufer = new byte[1000];

      // Crear o abrir el archivo para registrar la información
      FileWriter fileWriter = new FileWriter("./data/registro.txt", true);
      PrintWriter printWriter = new PrintWriter(fileWriter);

      while (true) {
        // Construimos el DatagramPacket para recibir peticiones
        DatagramPacket peticion =
          new DatagramPacket(bufer, bufer.length);



        // Leemos una petición del DatagramSocket
        socketUDP.receive(peticion);

        // Obtener información
        String nombreHost = peticion.getAddress().getHostName();
        String direccionIP = peticion.getAddress().getHostAddress();
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        System.out.print("Datagrama recibido del host: " + nombreHost);
        System.out.print(" desde la ip: " + direccionIP);
        System.out.println(" desde el puerto remoto: " + peticion.getPort());

        // Registrar
        printWriter.println("Host: " + nombreHost + ", IP: " + direccionIP + ", Timestamp: " + timestamp);
        printWriter.flush(); 

        // Construimos el DatagramPacket para enviar la respuesta
        DatagramPacket respuesta =
          new DatagramPacket(peticion.getData(), peticion.getLength(),
                             peticion.getAddress(), peticion.getPort());

        // Enviamos la respuesta, que es un eco
        socketUDP.send(respuesta);
      }

    } catch (SocketException e) {
      System.out.println("Socket: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("IO: " + e.getMessage());
    }
  }

}