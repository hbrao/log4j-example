package com.makotogroup.log4j.db;

import java.util.Date;
import java.util.Set;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class HibernateAppender extends AppenderSkeleton {

	SessionFactory sessionFactory;
	private SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			Configuration configuration = new Configuration().configure();
			sessionFactory = configuration.buildSessionFactory();
		}
		return sessionFactory;
	}
	private Session openSession() {
		return getSessionFactory().openSession();
	}
	private void closeSession(Session session) {
		session.flush();
		session.close();
	}
	@SuppressWarnings("unchecked")// Log4j's old, but we love her...
	@Override
	protected void append(LoggingEvent event) {
		// Skip any events that are from Hibernate... makes it easier this way...
		if (!event.getLoggerName().startsWith("org.hibernate")) {
			Message m = new Message();
			m.setLevelName(event.getLevel().toString());
			m.setThreadName(event.getThreadName());
			m.setMessage(event.getRenderedMessage());
			m.setWhenLogged(new Date(event.getTimeStamp()));
			m.setLoggerName(event.getLoggerName());
			Set properties = event.getPropertyKeySet();
			for (Object property : properties) {
				if (property instanceof String) {
					String propertyName = (String)property;
					MappedDiagnosticContext mdc = new MappedDiagnosticContext();
					mdc.setMessage(m);
					mdc.setPropertyName(propertyName);
					mdc.setPropertyValue(event.getMDC(propertyName).toString());
					m.getMdc().add(mdc);
				}
			}
			Session session = openSession();
			Transaction tx = session.beginTransaction();
			session.save(m);
			tx.commit();
			closeSession(session);
		}
	}

	@Override
	public void close() {
		// Nothing to do...
	}

	@Override
	public boolean requiresLayout() {
		return false;
	}

}
