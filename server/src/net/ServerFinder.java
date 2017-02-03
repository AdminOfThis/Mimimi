package net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.log4j.Logger;

public class ServerFinder extends Thread {

	private static final Logger LOG = Logger.getLogger(ServerFinder.class);
	private static final int PORT = 1100;
	private static final String BROAD_REQUEST = "BROADCAST_REQUEST";
	private static final String BROAD_RESPONSE = "BROADCAST_RESPONSE";
	private static long lengthOfRun = 1000;
	static boolean searchAllowed = true;
	private DatagramSocket broadcastSocket;

	public ServerFinder() {
		start();
	}

	@Override
	public void run() {
		try {
			broadcastSocket = new DatagramSocket(PORT, InetAddress.getByName("0.0.0.0"));
			broadcastSocket.setBroadcast(true);
		}
		catch (IOException e) {
			LOG.warn("Unable to start Broadcast Socket on Port " + PORT);
			return;
		}
		LOG.info("Starting broadcastserver");
		while (true) {
			try {
				byte[] recvBuf = new byte[15000];
				DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
				broadcastSocket.receive(packet);
				// received
				String message = new String(packet.getData()).trim();
				if (message.equals(BROAD_REQUEST)) {
					byte[] sendData = BROAD_RESPONSE.getBytes();
					// Send a response
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
					broadcastSocket.send(sendPacket);
				}
			}
			catch (IOException e) {
				LOG.info(e);
			}
		}
		// broadcastSocket.close();
	}

	public static String findServer() {
		try {
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);
			LOG.trace("Sending Broadcast on Port " + PORT);
			byte[] sendData = BROAD_REQUEST.getBytes();
			// Try the 255.255.255.255 first
			try {
				DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), PORT);
				socket.send(sendPacket);
			}
			catch (Exception e) {
				LOG.warn("Unable to send Broadcast Request", e);
			}
			// Broadcast the message over all the network interfaces
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();
				if (networkInterface.isLoopback() || !networkInterface.isUp()) {
					continue; // Don't want to broadcast to the loopback interface
				}
				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					if (broadcast == null) {
						continue;
					}
					// Send the broadcast package!
					try {
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, PORT);
						socket.send(sendPacket);
					}
					catch (Exception e) {}
				}
			}
			return receiveAnswers(socket);
		}
		catch (IOException e) {
			LOG.warn("Some kind of error, whatever...", e);
		}
		return null;
	}

	private static String receiveAnswers(DatagramSocket socket) throws IOException {
		searchAllowed = true;
		LOG.trace("Receiving started");
		Thread searchCancelThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(lengthOfRun);
				}
				catch (Exception e) {}
				searchAllowed = false;
				socket.close();
			}
		});
		searchCancelThread.setName("Search Cancel Thread");
		searchCancelThread.start();
		while (searchAllowed) {
			// Wait for a response
			byte[] recvBuf = new byte[15000];
			DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
			try {
				socket.receive(receivePacket);
			}
			catch (IOException e) {}
			// Check if the message is correct
			String message = new String(receivePacket.getData()).trim();
			if (message.equals(BROAD_RESPONSE)) {
				String ip = receivePacket.getAddress().getHostAddress();
				LOG.info("Found server on " + ip);
				return ip;
			}
		}
		LOG.error("No server found");
		return null;
	}

	public static String getIp() {
		String currentHostIpAddress = null;
		try {
			Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
			while (netInterfaces.hasMoreElements()) {
				NetworkInterface ni = netInterfaces.nextElement();
				Enumeration<InetAddress> address = ni.getInetAddresses();
				while (address.hasMoreElements()) {
					InetAddress addr = address.nextElement();
					if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress() && !(addr.getHostAddress().indexOf(":") > -1)) {
						currentHostIpAddress = addr.getHostAddress();
						break;
					}
				}
			}
			if (currentHostIpAddress == null) {
				currentHostIpAddress = "127.0.0.1";
			}
		}
		catch (SocketException e) {
			currentHostIpAddress = "127.0.0.1";
		}
		return currentHostIpAddress;
	}
}
