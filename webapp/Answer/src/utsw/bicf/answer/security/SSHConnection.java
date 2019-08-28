package utsw.bicf.answer.security;

import javax.annotation.PostConstruct;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class SSHConnection {
	
	boolean needed;
	String serverHostname;
	String dbHostname;
	String serverUsername;
	String localPrivateKeyPath;
	String localPublicKeyPath;
	int localPortForwarding;
	int dbPortForwarded;
	String knownHostsPath;
	String localPrivateKeyPassphrase;
	
	private static Session session= null;
	
	public SSHConnection() {
		super();
	}
	
	@PostConstruct
	public void init() {
		if (needed) {
			JSch jsch = new JSch();
	    	try {
				jsch.addIdentity(localPrivateKeyPath, localPublicKeyPath, localPrivateKeyPassphrase.getBytes());
				jsch.setKnownHosts(knownHostsPath);
				session=jsch.getSession(serverUsername, serverHostname, 22);
				session.connect();
				System.out.println("Connected");
				session.setPortForwardingL(localPortForwarding, serverHostname, dbPortForwarded);
			} catch (JSchException e) {
				e.printStackTrace();
				if(session !=null && session.isConnected()){
		    		System.out.println("Closing SSH Connection");
		    		session.disconnect();
		    	}
			}
		}
	}

	public boolean isNeeded() {
		return needed;
	}

	public void setNeeded(boolean needed) {
		this.needed = needed;
	}

	public String getServerHostname() {
		return serverHostname;
	}

	public void setServerHostname(String serverHostname) {
		this.serverHostname = serverHostname;
	}

	public String getDbHostname() {
		return dbHostname;
	}

	public void setDbHostname(String dbHostname) {
		this.dbHostname = dbHostname;
	}

	public String getServerUsername() {
		return serverUsername;
	}

	public void setServerUsername(String serverUsername) {
		this.serverUsername = serverUsername;
	}

	public String getLocalPrivateKeyPath() {
		return localPrivateKeyPath;
	}

	public void setLocalPrivateKeyPath(String localPrivateKeyPath) {
		this.localPrivateKeyPath = localPrivateKeyPath;
	}

	public String getLocalPublicKeyPath() {
		return localPublicKeyPath;
	}

	public void setLocalPublicKeyPath(String localPublicKeyPath) {
		this.localPublicKeyPath = localPublicKeyPath;
	}

	public String getKnownHostsPath() {
		return knownHostsPath;
	}

	public void setKnownHostsPath(String knownHostsPath) {
		this.knownHostsPath = knownHostsPath;
	}

	public String getLocalPrivateKeyPassphrase() {
		return localPrivateKeyPassphrase;
	}

	public void setLocalPrivateKeyPassphrase(String localPrivateKeyPassphrase) {
		this.localPrivateKeyPassphrase = localPrivateKeyPassphrase;
	}

	public static Session getSession() {
		return session;
	}

	public static void setSession(Session session1) {
		session = session1;
	}

	public int getDbPortForwarded() {
		return dbPortForwarded;
	}

	public void setDbPortForwarded(int dbPortForwarded) {
		this.dbPortForwarded = dbPortForwarded;
	}

	public int getLocalPortForwarding() {
		return localPortForwarding;
	}

	public void setLocalPortForwarding(int localPortForwarding) {
		this.localPortForwarding = localPortForwarding;
	}

	

}
