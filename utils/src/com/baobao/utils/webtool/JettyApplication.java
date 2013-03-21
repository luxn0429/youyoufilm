package com.baobao.utils.webtool;

import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppContext;



public class JettyApplication {
	
	static private final String CONTEXT_PATH = "/";
	static private final String PROJECT_HOME = System.getenv("MY_WORKSPACE_HOME") + "/WebServices";
	static public final int PORT = 8080;
	
    private final Server          httpServer        = new Server ();

    private static final JettyApplication instance = new JettyApplication();
    
    public static JettyApplication getInstance(){
    	return instance;
    }
    
    private JettyApplication () {
    }

    public void initServer () throws IllegalAccessException, InstantiationException, ClassNotFoundException, UnknownHostException {

    	SelectChannelConnector connector = new SelectChannelConnector();
    	connector.setPort(PORT);
    	//connector.setHost();
    	connector.setMaxIdleTime(30000);
        connector.setRequestHeaderSize(8192);
        
        httpServer.setConnectors(new Connector[] { connector });

        WebAppContext queryContext = new WebAppContext ();
        queryContext.setDescriptor("web/WEB-INF/web.xml");
        queryContext.setResourceBase("web");
        queryContext.setContextPath(CONTEXT_PATH);
        queryContext.setBaseResource(Resource.newClassPathResource("")); 
        queryContext.setClassLoader(Thread.currentThread().getContextClassLoader()); 
        queryContext.setConfigurationDiscovered(true);  
        queryContext.setParentLoaderPriority(true);  
        
        /*queryContext.setConfigurations(new Configuration[] {
                new AnnotationConfiguration(), new WebXmlConfiguration(),
                new WebInfConfiguration(), new TagLibConfiguration(),
                new PlusConfiguration(), new MetaInfConfiguration(),
                new FragmentConfiguration(), new EnvConfiguration() });*/
        httpServer.setHandler(queryContext);
        
        System.out.println(queryContext.getContextPath());  
        System.out.println(queryContext.getDescriptor());  
        System.out.println(queryContext.getResourceBase());  
        System.out.println(queryContext.getBaseResource());  
    }

      
    public boolean start () {

        try {
            initServer ();
        }
        catch (ClassNotFoundException e) {
            Logger.getLogger (this.getClass ()).error ("ClassNotFoundException when initializing webservice server", e);
            System.exit(1);
        }
        catch (IllegalAccessException e) {
            Logger.getLogger (this.getClass ()).error ("IllegalAccessException when initializing webservice server", e);
            System.exit(1);
        }
        catch (InstantiationException e) {
            Logger.getLogger (this.getClass ()).error ("InstantiationException when initializing webservice server", e);
            System.exit(1);
        } catch (UnknownHostException e) {
        	Logger.getLogger (this.getClass ()).error ("UnknownHostException when initializing webservice server", e);
			System.exit(1);
		}

        try {
            this.httpServer.start ();
            this.httpServer.join();
        }
        catch (Exception e) {
            Logger.getLogger (this.getClass ()).error ("Exception when starting webservice server", e);
            System.exit(1);
        }

        Logger.getLogger (this.getClass ()).info (
                "webservice server servlet started, listening on " + WebServerConfiguration.getInstance().getAddress() + ":"
                        + WebServerConfiguration.getInstance().getPort());

        return true;
    }
    
}
