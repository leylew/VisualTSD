package edu.uiuc.zenvisage.server;
import java.sql.SQLException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import edu.uiuc.zenvisage.data.remotedb.SQLQueryExecutor;

public class ZvServer {

	private Server server;
	private static int port = 8080;
	private static String metadatavalue="zenvisage_metadata";
	private static String userlist = "user_list";
	
	public void setPort(int port) {
		this.port = port;
	}

	public void start() throws Exception {	
		server = new Server(port);
		WebAppContext webAppContext = new WebAppContext();
		webAppContext.setContextPath("/");
		webAppContext.setWar("zenvisage.war");
		webAppContext.setParentLoaderPriority(true);
		webAppContext.setServer(server);
		webAppContext.setClassLoader(ClassLoader.getSystemClassLoader());
		webAppContext.getSessionHandler().getSessionManager()
				.setMaxInactiveInterval(10);
		server.setHandler(webAppContext);	
		server.start();
	}	
	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ZvServer zvServer = new ZvServer();
		zvServer.start();	
	}	
}
