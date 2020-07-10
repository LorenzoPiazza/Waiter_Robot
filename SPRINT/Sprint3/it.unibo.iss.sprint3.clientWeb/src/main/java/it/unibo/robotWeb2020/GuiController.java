package it.unibo.robotWeb2020;
//https://www.baeldung.com/websockets-spring
//https://www.toptal.com/java/stomp-spring-boot-websocket
	
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; 
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;

import connQak.configurator;
import connQak.connQakCoap;
import it.unibo.kactor.ApplMessage;
import it.unibo.kactor.MsgUtil;


@Controller 
public class GuiController { 
    String appName     ="robotGui";
    String viewModelRep="startup";
     
    String htmlPage  = "tearoomGuiSocket";
    
    Set<String> robotMoves = new HashSet<String>(); 
    
    connQakCoap connQakSupport ;   
    
    public GuiController() {
        connQak.configurator.configure();
        htmlPage  = connQak.configurator.getPageTemplate();
        connQakSupport = new connQakCoap(  );  
        connQakSupport.createConnection();
          
     }

    /*
     * Update the page vie socket.io when the application-resource changes.
     * Thanks to Eugenio Cerulo
     */
    	@Autowired
    	SimpMessagingTemplate simpMessagingTemplate;

  @GetMapping("/") 		 
  public String entry(Model viewmodel) {
 	 viewmodel.addAttribute("arg", "Entry page loaded. Please use the buttons ");
 	 viewmodel.addAttribute("enableRing", "disabled");
 	 peparePageUpdating();
 	 return htmlPage;
  } 
	

	private void peparePageUpdating() {
    	connQakSupport.getWaiterlogic().observe(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				System.out.println("GuiController --> CoapWaiterLogic changed ->" + response.getResponseText());
				simpMessagingTemplate.convertAndSend(WebSocketConfig.topicForWaiterLogicResponse, 
						new ResourceRep("" + HtmlUtils.htmlEscape(response.getResponseText())  ));
			}
			@Override
			public void onError() {
				System.out.println("GuiController --> CoapWaiterLogic error!");
			}
		});
		
		connQakSupport.getSmartbell().observe(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				System.out.println("GuiController --> CoapSmartbell changed ->" + response.getResponseText());
				simpMessagingTemplate.convertAndSend(WebSocketConfig.topicForSmartbellResponse, 
						new ResourceRep("" + HtmlUtils.htmlEscape(response.getResponseText())  ));
			}
			@Override
			public void onError() {
				System.out.println("GuiController --> CoapSmartbell error!");
			}
		});
    	connQakSupport.getClient_sim_1().observe(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				System.out.println("GuiController --> CoapClient1 changed ->" + response.getResponseText());
				simpMessagingTemplate.convertAndSend(WebSocketConfig.topicForClient1Response, 
						new ResourceRep("" + HtmlUtils.htmlEscape(response.getResponseText())  ));
			}
			@Override
			public void onError() {
				System.out.println("GuiController --> CoapClient1 error!");
			}
		});
    	connQakSupport.getClient_sim_2().observe(new CoapHandler() {
			@Override
			public void onLoad(CoapResponse response) {
				System.out.println("GuiController --> CoapClient2 changed ->" + response.getResponseText());
				simpMessagingTemplate.convertAndSend(WebSocketConfig.topicForClient2Response, 
						new ResourceRep("" + HtmlUtils.htmlEscape(response.getResponseText())  ));
			}
			@Override
			public void onError() {
				System.out.println("GuiController --> CoapClient2 error!");
			}
		});
	}
	
	/*
	 * INTERACTION WITH THE BUSINESS LOGIC OF SMARTBELL		
	 */
	protected void doBusinessJobSmartbell( String temp, Model viewmodel) {
		try {
			ApplMessage msg = MsgUtil.buildDispatch("web", "ring", "ring("+temp+")", configurator.getQaksmartbell() );
			connQakSupport.forward( msg, connQakSupport.getSmartbell());	
			//WAIT for command completion ...
			Thread.sleep(400);  //QUITE A LONG TIME ...
			if( viewmodel != null ) {
				ResourceRep rep = getWebPageRep("smartbell");
				viewmodel.addAttribute("arg", "Smartbell Response:  "+rep.getContent());
			}
		} catch (Exception e) {
			System.out.println("------------------- Smartbell doBusinessJob ERROR=" + e.getMessage()  );
			//e.printStackTrace();
		}		
	}
	
	/*
	 * INTERACTION WITH THE BUSINESS LOGIC OF CLIENT_SIMULATOR		
	 */
	protected void doBusinessJobClientSimulator( String clientN, Model viewmodel) {
		try {
			if(clientN.equals("1")){
				ApplMessage msg = MsgUtil.buildDispatch("web", "proceed", "proceed("+clientN+")", configurator.getQakclient_sim_1() );
				connQakSupport.forward( msg, connQakSupport.getClient_sim_1());					
			}
			else {
				ApplMessage msg = MsgUtil.buildDispatch("web", "proceed", "proceed("+clientN+")", configurator.getQakclient_sim_2() );
				connQakSupport.forward( msg, connQakSupport.getClient_sim_2());	
			}
			//WAIT for command completion ...
			Thread.sleep(400);  //QUITE A LONG TIME ...
			if( viewmodel != null ) {
				ResourceRep rep = getWebPageRep("client"+clientN);
				viewmodel.addAttribute("arg", "ClientSimulator Response:  "+rep.getContent());
			}
		} catch (Exception e) {
			System.out.println("------------------- ClientSimulator doBusinessJob ERROR=" + e.getMessage()  );
			//e.printStackTrace();
		}		
	}

    @ExceptionHandler 
    public ResponseEntity<String> handle(Exception ex) {
    	HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<String>(
        		"GuiController ERROR " + ex.getMessage(), responseHeaders, HttpStatus.CREATED);
    }

/* ----------------------------------------------------------
   Message-handling Controller
  ----------------------------------------------------------
 */
//	@MessageMapping("/hello")
//	@SendTo("/topic/display")
//	public ResourceRep greeting(RequestMessageOnSock message) throws Exception {
//		Thread.sleep(1000); // simulated delay
//		return new ResourceRep("Hello by AN, " + HtmlUtils.htmlEscape(message.getName()) + "!");
//	}
	
	@MessageMapping("/ring")
 	@SendTo("/topic/displaySmartbell")
 	public ResourceRep smartbellbacktoclient(RequestMessageOnSock message) throws Exception {
		doBusinessJobSmartbell(message.getName(), null);
		return getWebPageRep("smartbell");
 	}
	
	@MessageMapping("/proceed1")
 	@SendTo("/topic/displayClient1")
 	public ResourceRep client1backtoclient(RequestMessageOnSock message) throws Exception {
		doBusinessJobClientSimulator(message.getName(), null);
		return getWebPageRep("client1");
 	}
	
	@MessageMapping("/proceed2")
 	@SendTo("/topic/displayClient2")
 	public ResourceRep client2backtoclient(RequestMessageOnSock message) throws Exception {
		doBusinessJobClientSimulator(message.getName(), null);
		return getWebPageRep("client2");
 	}

/*	@MessageMapping("/update")
	@SendTo("/topic/display")
	public ResourceRep updateTheMap(@Payload String message) {
		ResourceRep rep = getWebPageRep();
		return rep;
	}*/

	public ResourceRep getWebPageRep(String caller)   {
		String resourceRep="somethingWrong";
		switch(caller) {
			case "smartbell":
				resourceRep = connQakSupport.readRep(connQakSupport.getSmartbell());
			case "client1":
				resourceRep = connQakSupport.readRep(connQakSupport.getClient_sim_1());
			case "client2":
				resourceRep = connQakSupport.readRep(connQakSupport.getClient_sim_2());
		}
		
		System.out.println("------------------- "+caller+" resourceRep=" + resourceRep  );
		return new ResourceRep("" + HtmlUtils.htmlEscape(resourceRep)  );
	}
	
  
 
	
 
/*
 * The @MessageMapping annotation ensures that, 
 * if a message is sent to the /hello destination, the greeting() method is called.    
 * The payload of the message is bound to a HelloMessage object, which is passed into greeting().
 * 
 * Internally, the implementation of the method simulates a processing delay by causing 
 * the thread to sleep for one second. 
 * This is to demonstrate that, after the client sends a message, 
 * the server can take as long as it needs to asynchronously process the message. 
 * The client can continue with whatever work it needs to do without waiting for the response.
 * 
 * After the one-second delay, the greeting() method creates a Greeting object and returns it. 
 * The return value is broadcast to all subscribers of /topic/display, 
 * as specified in the @SendTo annotation. 
 * Note that the name from the input message is sanitized, since, in this case, 
 * it will be echoed back and re-rendered in the browser DOM on the client side.
 */
    
 
/*
 * curl --location --request POST 'http://localhost:8080/move' --header 'Content-Type: text/plain' --form 'move=l'	
 * curl -d move=r localhost:8080/move
 */
}

