package modules;

import net.Server;

public abstract class Module extends Thread {

	private boolean finish = false;
	private Server server;

	public Module(Server server) {
		this.server = server;
	}

	public void finish() {
		finish = true;
	}

	public boolean isFinish() {
		return finish;
	}

	public Server getServer() {
		return server;
	}
}
