package dom;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
public class LiveBoxReboot {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Logger logger = Logger.getLogger(LiveBoxReboot.class.getName());
		logger.log(Level.INFO,"Initializing Application Context");
		ApplicationContext appConntext = new ClassPathXmlApplicationContext("Reboot.xml");
		((AbstractApplicationContext)appConntext).registerShutdownHook();
		
	}

}
