/*
 Copyright (c) 2011, Lucre capital
 All rights reserved.
 *  This software is a property of Lucre Capital LLC.
 *  Any usage of this code requires a direct permision from the owner.
 *  contact legal@lucrecapital.com for questions on usage.
 */
package com.lucrecapital.mfeed.udp;

import java.net.InetAddress;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.Logger;

final class UDPReader extends Thread
{

  private Logger log;
  private final static int MAX_PACKET_SIZE = 64 * 1024;
  private final static int RECV_BUF_SIZE = 1024 * 1024;
  private InetAddress address;
  private MulticastSocket socket;
  private PacketListener packetListener;
  private AtomicBoolean running = new AtomicBoolean(true);

  public UDPReader (String host, int port, PacketListener packetListener) throws IOException
  {
    address = InetAddress.getByName(host);
    socket = new MulticastSocket(port);
    socket.setReceiveBufferSize(MAX_PACKET_SIZE);
    socket.setReceiveBufferSize(RECV_BUF_SIZE);
    this.packetListener = packetListener;
    log = Logger.getLogger(String.format("UDPReader[%s:%d]", host, port));
    log.info("Created reader");
  }

  @Override
  public void start ()
  {
    try
    {
      super.start();
      socket.joinGroup(address);
    }
    catch (IOException ioe)
    {
      log.error("can't join multicast group(host is not a multicast address) ", ioe);
    }
    catch (SecurityException se)
    {
      log.error("can't join multicast group(not allowed) ", se);
    }
    catch (IllegalThreadStateException itse)
    {
      log.error("Thread already started ", itse);
    }

    log.debug("Starting");
  }

  public void finish ()
  {
    try
    {
      System.out.println(this.address.getHostAddress() + " is leaved");
      running.set(false);
      socket.leaveGroup(address);

      // It makes socket.receive() to throw exception and stop blocking the thread loop.
      socket.close();
      log.info(this.address.getHostAddress() + " stopped");
    }
    catch (IOException ioe)
    {
      log.error("can't leave multicast group(host is not a multicast address)", ioe);
    }
    catch (SecurityException se)
    {
      log.error("can't leave multicast group(not allowed) " + se);
    }
  }

  @Override
  public final void run ()
  {
    byte[] response = new byte[MAX_PACKET_SIZE];
    DatagramPacket packet = new DatagramPacket(response, response.length);

    while (running.get())
    {
      try
      {
        socket.receive(packet);
        packetListener.onPacket(packet.getData(), packet.getLength());
      }
      catch (Exception e)
      {
        if (running.get())
        {
          log.error("Problem in message receiving", e);
        }
      }
    }
  }
}
