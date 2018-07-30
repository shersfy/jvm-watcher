package org.shersfy.jwatcher.connector;

import java.io.IOException;
import java.util.Map;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.remote.JMXConnector;
import javax.security.auth.Subject;

public class JMXLocalConnector implements JMXConnector{
	
	public static String getLocalUrl(long pid){
		return String.format("localhost/%s", pid);
	}

	@Override
	public void connect() throws IOException {
		
	}

	@Override
	public void connect(Map<String, ?> env) throws IOException {
		
	}

	@Override
	public MBeanServerConnection getMBeanServerConnection() throws IOException {
		return null;
	}

	@Override
	public MBeanServerConnection getMBeanServerConnection(Subject delegationSubject) throws IOException {
		return null;
	}

	@Override
	public void close() throws IOException {
		
	}

	@Override
	public void addConnectionNotificationListener(NotificationListener listener, NotificationFilter filter,
			Object handback) {
		
	}

	@Override
	public void removeConnectionNotificationListener(NotificationListener listener) throws ListenerNotFoundException {
		
	}

	@Override
	public void removeConnectionNotificationListener(NotificationListener l, NotificationFilter f, Object handback)
			throws ListenerNotFoundException {
		
	}

	@Override
	public String getConnectionId() throws IOException {
		return null;
	}

	
}
