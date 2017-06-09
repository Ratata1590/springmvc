package com.ratata.dynamicCodeRest.controller;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

import javax.annotation.PostConstruct;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SocketRestEndpointController {

  public static Socket socket;

  @RequestMapping(value = "/socketHandlerUp", method = RequestMethod.POST)
  public void socketHandler(@RequestBody byte[] writeTo) throws Exception {
    if (writeTo.length != 0) {
      socket.getOutputStream().write(writeTo);
      socket.getOutputStream().flush();
    }
  }

  @RequestMapping(value = "/socketHandlerDown", method = RequestMethod.GET)
  public Object socketHandlerDown() throws Exception {
    byte[] resultBuff = new byte[socket.getReceiveBufferSize()];
    byte[] tmpbuff = new byte[socket.getInputStream().read(resultBuff, 0, resultBuff.length)];
    System.arraycopy(resultBuff, 0, tmpbuff, 0, tmpbuff.length);
    String data = new String(tmpbuff);
    System.out.println(data);
    return tmpbuff;
  }

  @PostConstruct
  public void connectSocket() throws Exception {
    socket = openSocket("localhost", 21);
  }

  private Socket openSocket(String server, int port) throws Exception {
    Socket socket;

    // create a socket with a timeout
    try {
      InetAddress inteAddress = InetAddress.getByName(server);
      SocketAddress socketAddress = new InetSocketAddress(inteAddress, port);

      // create a socket
      socket = new Socket();

      // this method will block no more than timeout ms.
      int timeoutInMs = 10 * 1000; // 10 seconds
      socket.connect(socketAddress, timeoutInMs);

      return socket;
    } catch (SocketTimeoutException ste) {
      System.err.println("Timed out waiting for the socket.");
      ste.printStackTrace();
      throw ste;
    }
  }
}
