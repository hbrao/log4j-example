package com.makotogroup.log4j.db;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

public class Message implements Serializable {
	protected Integer getId() {
		return id;
	}
	protected void setId(Integer id) {
		this.id = id;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (levelName == null) {
			if (other.levelName != null)
				return false;
		} else if (!levelName.equals(other.levelName))
			return false;
		if (loggerName == null) {
			if (other.loggerName != null)
				return false;
		} else if (!loggerName.equals(other.loggerName))
			return false;
		if (mdc == null) {
			if (other.mdc != null)
				return false;
		} else if (!mdc.equals(other.mdc))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (threadName == null) {
			if (other.threadName != null)
				return false;
		} else if (!threadName.equals(other.threadName))
			return false;
		if (whenLogged == null) {
			if (other.whenLogged != null)
				return false;
		} else if (!whenLogged.equals(other.whenLogged))
			return false;
		return true;
	}
	protected String getThreadName() {
		return threadName;
	}
	protected void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	protected String getLoggerName() {
		return loggerName;
	}
	protected void setLoggerName(String loggerName) {
		this.loggerName = loggerName;
	}
	protected String getLevelName() {
		return levelName;
	}
	protected void setLevelName(String levelName) {
		this.levelName = levelName;
	}
	protected String getMessage() {
		return message;
	}
	protected void setMessage(String message) {
		this.message = message;
	}
	protected Date getWhenLogged() {
		return whenLogged;
	}
	protected void setWhenLogged(Date whenLogged) {
		this.whenLogged = whenLogged;
	}
	protected Set<MappedDiagnosticContext> getMdc() {
		return mdc;
	}
	protected void setMdc(Set<MappedDiagnosticContext> mdc) {
		this.mdc = mdc;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -311432225701687688L;
	private Integer id;
	private String threadName;
	private String loggerName;
	private String levelName;
	private String message;
	private Date whenLogged;
	private Set<MappedDiagnosticContext> mdc  = new LinkedHashSet<MappedDiagnosticContext>();
}
