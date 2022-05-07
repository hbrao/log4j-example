package com.makotogroup.log4j;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.RuntimeMBeanException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.jdmk.comm.HtmlAdaptorServer;

/**
 * Controls and coordinates all activities in the sample application.
 * Implemented as a dynamic MBean.
 * 
 * @author J Steven Perry
 */
public class Controller extends Basic implements MBeanRegistration {
	private static final Logger log = Logger.getLogger(Controller.class);

	public static final String JMX_DOMAIN = "com.makotogroup.log4j";
	public static final String OBJECT_NAME = "Controller";

	private static final int QUEUE_DEPTH = 10;

	private MBeanServer server;

	private Queue<WorkUnit> queue;

	private int numberOfSuppliers;

	private int numberOfConsumers;

	private Document config;

	public static void main(String[] args) {
		log.trace("Application Starting...");
		log.trace("Checking arguments...");
		if (log.isDebugEnabled()) {
			log.debug("Arguments:");
			int aa = 0;
			for (String arg : args) {
				log.debug("\targ[" + aa++ + "] -> '" + arg + "'");
			}
		}
		if (args.length < 1) {
			log.info("Usage: " + Controller.class.getName() + " mbean_definition_file target_name");
		} else {
			log.trace("Inspecting argument[0] (supposed to be the MBean definition file)...");
			String appMbeanDef = args[0];
			log.debug("Application definition mbean file is '" + appMbeanDef + "'");
			log.trace("Inspecting argument[1] (supposed to be the target name)...");
			String targetName = args[1];
			log.debug("Target name is '" + targetName + "'");
			new Controller(appMbeanDef, targetName);
			log.info("APPLICATION STARTING AT ==> " + new DateTime().toString("MM/dd/yyyy HH:mm:ss.SSS") + " <==");
			try {
				log.trace("Waiting for current thread to die...");
				log.debug("Current thread is '" + Thread.currentThread().getName() + "'...");
				Thread.currentThread().join();
				log.trace("Thread dead...");
			} catch (InterruptedException e) {
				log.warn("Caught InterruptedException! What gives?", e);
			}
		}
		log.trace("Application Terminated.");
	}

	public Controller(String appMbeanDef, String targetName) {
		super();
		log.trace("Entering Controller() constructor (after super() call)...");
		log.debug("Parameters:");
		log.debug("\tappMbeanDef -> '" + appMbeanDef + "'");
		
		log.trace("Creating MBeanServer...");
		server = MBeanServerFactory.createMBeanServer();
		// Create the HTML adapter
		log.trace("Creating HTML Adapter...");
		this.createHtmlAdapter();
		log.debug("Controller: INFO: " + "supplierWorkFactor=" + Supplier.DEFAULT_WORK_FACTOR + ", consumerWorkFactor=" + Consumer.DEFAULT_WORK_FACTOR);
		// Register the controller as an MBean
		ObjectName objName = null;
		try {
			log.trace("Creating work Queue...");
			queue = new Queue<WorkUnit>(QUEUE_DEPTH);
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			docBuilderFactory.setValidating(true);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			// Set an ErrorHandler before parsing
			OutputStreamWriter errorWriter = new OutputStreamWriter(System.err, "UTF-8");
			docBuilder.setErrorHandler(new MyErrorHandler(new PrintWriter(errorWriter, true)));
			log.trace("Parsing MBean definition file...");
			config = docBuilder.parse(Thread.currentThread().getContextClassLoader().getResourceAsStream(appMbeanDef));

			log.trace("Creating Queue MBean from MBean definition file...");
			DynamicMBeanFacade mbean = createMBeanFromXMLDefinition(config.getDocumentElement(), "Queue", queue);
			if (mbean != null) {
				objName = new ObjectName(JMX_DOMAIN + ":name=" + Queue.OBJECT_NAME);
				log.trace("Registering Queue MBean...");
				server.registerMBean(mbean, objName);
				log.debug("\tRegistered MBean: OBJECT NAME = " + objName);
			}
			log.trace("Creating Controller MBean from MBean definition file...");
			mbean = createMBeanFromXMLDefinition(config.getDocumentElement(), "Controller", this);
			if (mbean != null) {
				objName = new ObjectName(JMX_DOMAIN + ":name=" + OBJECT_NAME);
				log.trace("Registering Controller MBean...");
				server.registerMBean(mbean, objName);
				log.debug("\tRegistered MBean: OBJECT NAME = " + objName);
			}
			//
			// Now create the worker threads.
			//
			log.trace("Creating Workers...");
			if (targetName.equalsIgnoreCase("EXAMPLE1-16")) {
				createWorker(Supplier.ROLE, Supplier.DEFAULT_WORK_FACTOR);
				createWorker(Supplier.ROLE, Supplier.DEFAULT_WORK_FACTOR);
				createWorker(Supplier.ROLE, Supplier.DEFAULT_WORK_FACTOR);
				createWorker(Consumer.ROLE, Consumer.DEFAULT_WORK_FACTOR);				
				createWorker(Consumer.ROLE, Consumer.DEFAULT_WORK_FACTOR);				
			} else {
				createWorker(Supplier.ROLE, Consumer.DEFAULT_WORK_FACTOR);
				createWorker(Consumer.ROLE, Consumer.DEFAULT_WORK_FACTOR);
			}
		} catch (Exception e) {
			log.error("Could not register the Controller MBean! Stack trace follows...", e);
		}
		log.trace("Leaving Controller() constructor...");
	}

	/**
	 * Creates the HTML adapter server and starts it running on its own thread of
	 * execution.
	 */
	private void createHtmlAdapter() {
		log.trace("Entering createHtmlAdapter...");
		int portNumber = 8090;
		try {
			HtmlAdaptorServer html = new HtmlAdaptorServer(portNumber);
			ObjectName html_name = null;
			html_name = new ObjectName("Adaptor:name=html,port=" + portNumber);
			log.debug("\tRegistered MBean: OBJECT NAME = " + html_name);
			server.registerMBean(html, html_name);
			html.start();
		} catch (Exception e) {
			log.error("Could not create the HTML adaptor!", e);
		}
		log.trace("Leaving createHtmlAdapter...");
	}

	/**
	 * Returns the number of workers of each type
	 */
	protected int getNumberOfWorkers(String role) {
		log.trace("Entering getNumberOfWorkers...");
		log.debug("Parameters:");
		log.debug("\trole -> '" + role + "'");
		int ret = 0;
		if (role.equalsIgnoreCase(Supplier.ROLE)) {
			ret = numberOfSuppliers;
		} else if (role.equalsIgnoreCase(Consumer.ROLE)) {
			ret = numberOfConsumers;
		} else {
			throw new RuntimeMBeanException(new IllegalArgumentException("Controller.getNumberOfWorkers(): ERROR: "
					+ "Unknown role name '" + role + "'."));
		}
		log.debug("Returning value -> " + ret);
		log.trace("Entering getNumberOfWorkers...");
		return ret;
	}

	/**
	 * Builds the worker key from the role name
	 */
	protected String buildWorkerKey(String role) {
		log.trace("Entering buildWorkerKey()...");
		
		StringBuffer buf = new StringBuffer();
		buf.append(JMX_DOMAIN + ":name=" + Worker.OBJECT_NAME);
		buf.append(",role=");
		buf.append(role);
		String ret = buf.toString();
		
		log.trace("Leaving buildWorkerKey()...");
		log.debug("Returning value -> '" + ret + "'");
		return ret;
	}

	/**
	 * Creates and registers a worker MBean of the specified type.
	 * @param workUnitFactory 
	 */
	protected void createNewWorker(String role, int workFactor, int instanceId) {
		log.trace("Entering createNewWorker()...");
		log.debug("Parameters:");
		log.debug("\trole      -> " + role);
		log.debug("\tworkFactor -> " + workFactor);
		log.debug("\tinstanceId -> " + instanceId);
		
		// Create the Worker and register it with the MBean server.
		log.info("Creating new worker in the role of '" + role	+ "' with a work factor of " + workFactor);
		Worker worker = null;
		ObjectName objName = null;
		StringBuffer buf = new StringBuffer();
		try {
			buf.append(buildWorkerKey(role));
			buf.append(",instanceId=");
			buf.append(instanceId);
			if (role.equalsIgnoreCase(Supplier.ROLE)) {
				worker = (Worker) new Supplier(queue, workFactor);
			} else if (role.equalsIgnoreCase(Consumer.ROLE)) {
				worker = (Worker) new Consumer(queue, workFactor);
			}
			DynamicMBeanFacade mbean = createMBeanFromXMLDefinition(config.getDocumentElement(), role, worker);
			if (mbean != null) {
				objName = new ObjectName(buf.toString());
				server.registerMBean(mbean, objName);
				log.debug("REGISTERED WORKER -> OBJECT NAME = " + objName);
			}
			Thread t = new Thread(worker);
			t.start();
			if (role.equalsIgnoreCase(Supplier.ROLE)) {
				numberOfSuppliers++;
			} else if (role.equalsIgnoreCase(Consumer.ROLE)) {
				numberOfConsumers++;
			}
		} catch (Exception e) {
			log.error("Could not register the Worker MBean!", e);
		}
	}

	/**
	 * Creates and starts a worker thread
	 * @param workUnitFactory 
	 */
	public void createWorker(String role, int workFactor) {
		log.trace("Entering createWorker(" + role + ", " + workFactor + ")");
		int index = getNumberOfWorkers(role);
		createNewWorker(role, workFactor, index + 1);
		log.trace("Leaving createWorker()...");
	}

	public void reset() {
		// nothing to do, have to implement, though...
	}

	private DynamicMBeanFacade createMBeanFromXMLDefinition(Node n, String className, Object resource) {
		log.trace("Entering createMBeanFromXMLDefinition()...");
		log.debug("Parameters:");
		log.debug("\tn         -> '"+ n.toString() + "'");
		log.debug("\tclassName -> '" + className + "'");
		log.debug("\tresource  -> '" + resource.toString() + "'");
		DynamicMBeanFacade mbean = null;
		try {
			log.info("Looking for mbean '" + className + "'");
			//
			// First attempt to find all mbean nodes so we can find the
			// / one we'return looking for.
			//
			NodeList nodes = n.getChildNodes();
			for (int aa = 0; aa < nodes.getLength(); aa++) {
				Node node = nodes.item(aa);
				log.debug("\tLooking at node: type=" + node.getNodeType() + "', name='" + node.getNodeName() + "'");
				if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("mbean")) {
					NamedNodeMap attributes = node.getAttributes();
					Node att = attributes.getNamedItem("className");
					log.debug("\tFound an mbean node attribute named '" + att.getNodeName() + "' with value '"
							+ att.getNodeValue() + "'");
					if (att.getNodeValue().equals(className)) {
						String mbeanDesc = this.getElementText(node);
						mbean = new DynamicMBeanFacade(resource, mbeanDesc);
						//
						// Now read the management interface definition
						//
						log.debug("\tFound the mbean class: '" + className + "'");
						processMbeanDefinitions(mbean, node);
					} // att.getNodeValue().equals(className)
				}
				if (mbean != null)
					break;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		if (mbean == null) {
			log.warn("MBean '" + className + "' not found in definition file.");
		}
		log.trace("Leaving createMBeanFromXMLDefinition()...");
		return mbean;
	}

	private void processMbeanDefinitions(DynamicMBeanFacade mbean, Node node) throws DynamicMBeanFacadeException {
		log.trace("Entering processMbeanDefinitions()...");
		NodeList features = node.getChildNodes();
		for (int aa = 0; aa < features.getLength(); aa++) {
			Node feature = features.item(aa);
			if (feature.getNodeType() == Node.ELEMENT_NODE) {
				NamedNodeMap attributes = feature.getAttributes();
				if (feature.getNodeName().equals("attribute")) {
					Node nameNode = attributes.getNamedItem("name");
					String attName = nameNode.getNodeValue();
					String attDesc = this.getElementText(feature);
					log.debug("\tProcessing attribute: name='" + attName + "', description='" + attDesc + "'");
					mbean.addAttribute(attName, attDesc);
				} else if (feature.getNodeName().equals("operation")) {
					Node nameNode = attributes.getNamedItem("name");
					String opName = nameNode.getNodeValue();
					String opDesc = "";
					log.debug("\tProcessing operation: name='" + opName + "'");
					NodeList operationFeatures = feature.getChildNodes();
					for (int bb = 0; bb < operationFeatures.getLength(); bb++) {
						Node operationFeature = operationFeatures.item(bb);
						if (operationFeature.getNodeType() == Node.ELEMENT_NODE) {
							if (operationFeature.getNodeName().equals("description")) {
								opDesc = this.getElementText(operationFeature);							
							}
						}
					}
					mbean.addOperation(opName, opDesc);
				} else if (feature.getNodeName().equals("notification")) {
					Node nameNode = attributes.getNamedItem("type");
					String notifName = nameNode.getNodeValue();
					String notifDesc = this.getElementText(feature);
					log.trace("\tProcessing notification: name='" + notifName + "', description='" + notifDesc + "'");
					mbean.addNotification(notifName, notifDesc);
				}
			}
		} // for (int cc ...
		log.trace("Leaving processMbeanDefinitions()...");
	}

	private String getElementText(Node node) {
		log.trace("Entering getElementText()...");
		log.debug("Parameters:");
		log.debug("\tnode -> '" + node.toString() + "'");
		String ret = null;
		try {
			for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
				if (child.getNodeType() == Node.TEXT_NODE) {
					ret = child.getNodeValue();
					break;
				}
			}
		} catch (Exception e) {
			log.error(e);
		}
		log.debug("Returning value -> '" + ret + "'");
		log.trace("Leaving getElementText()...");
		return ret;
	}

	public void postDeregister() {
	}

	public void postRegister(Boolean booleanParam) {
	}

	public void preDeregister() throws java.lang.Exception {
	}

	public ObjectName preRegister(MBeanServer mBeanServer, ObjectName objectName) throws Exception {
		server = mBeanServer;
		ObjectName objName = objectName;
		log.info("REGISTERING OBJECT -> " + objectName.toString());
		return objName;
	}

	// Error handler to report errors and warnings
	private static class MyErrorHandler implements ErrorHandler {
		/** Error handler output goes here */
		private PrintWriter out;

		MyErrorHandler(PrintWriter out) {
			this.out = out;
		}

		/**
		 * Returns a string describing parse exception details
		 */
		private String getParseExceptionInfo(SAXParseException spe) {
			String systemId = spe.getSystemId();
			if (systemId == null) {
				systemId = "null";
			}
			String info = "URI=" + systemId + " Line=" + spe.getLineNumber() + ": " + spe.getMessage();
			return info;
		}

		// The following methods are standard SAX ErrorHandler methods.
		// See SAX documentation for more info.

		public void warning(SAXParseException spe) throws SAXException {
			out.println("Warning: " + getParseExceptionInfo(spe));
		}

		public void error(SAXParseException spe) throws SAXException {
			String message = "Error: " + getParseExceptionInfo(spe);
			throw new SAXException(message);
		}

		public void fatalError(SAXParseException spe) throws SAXException {
			String message = "Fatal Error: " + getParseExceptionInfo(spe);
			throw new SAXException(message);
		}
	}

	@Override
	public Logger getLogger() {
		return log;
	}
	
	@Override
	public void logError(String message) {
		getLogger().error(message);
	}
}
