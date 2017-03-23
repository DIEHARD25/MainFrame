/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lucrecapital.mfeed.udp;

/**
 *
 * @author kham
 */
interface PacketListener
{

  public void onPacket (byte[] data, int length);
}
