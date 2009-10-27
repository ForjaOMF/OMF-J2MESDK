/*
 * SMS20.java
 *
 * Created on 14 de mayo de 2008, 15:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package SMS20;
import java.io.DataInputStream;
import javax.microedition.io.*;
import javax.microedition.pki.*;
import java.util.Vector;
import java.util.Enumeration;
import java.io.*;


/**
 *
 * @author Ivan Alvarez 
 */

public class SMS20 extends HttpObserver {
    
    //Allowed requests
    public static int PETICIONLOGIN = 1;
    public static int PETICIONCONNECT = 2;
    public static int PETICIONADDCONTACT=3;
    public static int PETICIONDELETECONTACT=4;
    public static int PETICIONSENDMESSAGE=5;
    public static int PETICIONAUTHORIZECONTACT=6;
    public static int PETICIONPOLLING=7;
    public static int PETICIONLOGOUT= 8;
    
    //XML files
    private static int XMLCLIENTCAPABILITY=1;
    private static int XMLSERVICE=2;
    private static int XMLUPDATEPRESENCE=3;
    private static int XMLGETLIST=4;
    private static int XMLGETPRESENCE=5;
    private static int XMLGETPRIVATELIST=6;
    private static int XMLSUSCRIPTIONLIST=7;
    private static int XMLPRESENCECONTACT=8;
    private static int XMLSENDNICK=9;
    private PeticionesHttp http;
    
    //True when the http connection return the response
    private boolean peticionLoginTerminada;
    private boolean peticionConnectTerminada;
    private boolean peticionAddContactTerminada;
    private boolean peticionDeleteContactTerminada;
    private boolean peticionSendMessageTerminada;
    private boolean peticionAuthorizeContactTerminada;
    private boolean peticionPollingTerminada;
    private boolean peticionLogoutTerminada;
     //Cookie returned by the petition of Login
    private String cookieLogin="";
    //True if the user is logged
    private boolean logged=false;
    //Keep the kind of request to make to the server
    private int tipoPeticion=0;
    
    //Contains the result of the http connection.
    private String resultPost="";
    
    //Transactions counter for the current session    
    private int m_nTransId;
    //Session id
    private String SessionID;
    //Own Nickname
    private String MyAlias;

    /** Creates a new instance of TestClass */
    public SMS20() {
         super();
    }
   
    public String Login(String csLogin, String csPassw) 
    {   
        m_nTransId=0;
        tipoPeticion=PETICIONLOGIN;
        peticionLoginTerminada=false;
        String postData="TM_ACTION=AUTHENTICATE&TM_LOGIN="+csLogin+"&TM_PASSWORD="+csPassw+"&SessionCookie=ColibriaIMPS_367918656&ClientID=WV:InstantMessenger-1.0.2309.16485@COLIBRIA.PC-CLIENT";
        http = new PeticionesHttp("http://impw.movistar.es/tmelogin/tmelogin.jsp?USERID=616685072", this, postData, PeticionesHttp.POST, PETICIONLOGIN);
        http.run();
        
         while (peticionLoginTerminada==false)
         {    
         }
        //Now in resultPost, if the loggin has been correct, there is the sessionID in this way:
        /*<Login-Response>
                    <ClientID>
                            <URL>WV:InstantMessenger-1.0.2309.16485@COLIBRIA.PC-CLIENT</URL>
                    </ClientID>
                    <Result>
                            <Code>200</Code>
                            <Description>Successfully completed</Description>
                    </Result>
                    <SessionID>2zrev0w230</SessionID>
                    <KeepAliveTime>1620</KeepAliveTime>
                    <CapabilityRequest>T</CapabilityRequest>
            </Login-Response>
         **/
        String returnResult;
        if (resultPost.indexOf("<SessionID>")!=-1)
        {
            SessionID=resultPost.substring(resultPost.indexOf("<SessionID>")+11,resultPost.indexOf("</SessionID>"));
            returnResult=SessionID;
        }
        else
            returnResult="";
        
        tipoPeticion=0;
        return returnResult;
    }
   
    public Vector Connect(String csLog, String csNickname)
    {
        tipoPeticion=PETICIONCONNECT;
        peticionConnectTerminada=false;
        //Call to Client Capability. Negociate capabilities.
        String postData=XMLConnectFile(XMLCLIENTCAPABILITY, "");
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONCONNECT);
        http.run();

        while (peticionConnectTerminada==false)
        {    
        }
        peticionConnectTerminada=false;
        //Call to service request. Ask for access into the service.
        postData=XMLConnectFile(XMLSERVICE, "");
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONCONNECT);
        http.run();
        while (peticionConnectTerminada==false)
        {    
        }
        
        peticionConnectTerminada=false;
         //Call to Update Presence. Actualize our state.
        postData=XMLConnectFile(XMLUPDATEPRESENCE, "");
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONCONNECT);
        http.run();
        while (peticionConnectTerminada==false)
        {    
        }
        
        peticionConnectTerminada=false;
        //Call to Get List of Contacts. Take our contact list.
        postData=XMLConnectFile(XMLGETLIST, "");
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONCONNECT);
        http.run();
        while (peticionConnectTerminada==false)
        {    
        }
        //Now in resultPost there is a XML structure with the list of contacts in this way.
        /*
         *<GetList-Response>
         *  <ContactList>wv:6xxxxxxxx/~pep1.0_blocklist@movistar.es</ContactList>
	 *  <ContactList>wv:6xxxxxxxx/~pep1.0_subscriptions@movistar.es</ContactList>
	 *  <DefaultContactList>wv:6xxxxxxxx/~pep1.0_privatelist@movistar.es</DefaultContactList>
	 *</GetList-Response>
         */
        peticionConnectTerminada=false;
   
         //Call to get Presence.
        postData=XMLConnectFile(XMLGETPRESENCE,csLog);
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONCONNECT);
        http.run();
        while (peticionConnectTerminada==false)
        {    
        }
        
        peticionConnectTerminada=false;
        //Now in resultPust there is the own ALIAS if everything is correct.
        String alias;
        MyAlias=getAlias(resultPost);
        peticionConnectTerminada=false;
        //Call to Get PRIVATE List of Contacts. Take our contact list.
        postData=XMLConnectFile(XMLGETPRIVATELIST, csLog);
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONCONNECT);
        http.run();
        while (peticionConnectTerminada==false)
        {    
        }
        //Now in resultPost there is the list of our private contacts with the following format that we have to parser:
        /*<NickList>
	*	<NickName>
	*		<Name>Luis</Name>
	*		<UserID>wv:6yyyyyyyy@movistar.es</UserID>
	*	</NickName>
	*	<NickName>
	*		<Name>Carlos</Name>
	*		<UserID>wv:6zzzzzzzz@movistar.es</UserID>
	*	</NickName>
	*</NickList>
         */
        
        //Create suscription list.
        //Compose the list of contacts
        Vector contactList=GetContactList(resultPost);
        String nickList="";
        nickList=getNickList(resultPost);
        peticionConnectTerminada=false;
        postData=XMLConnectFile(XMLSUSCRIPTIONLIST, csLog, nickList);
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONCONNECT);
        http.run();
        while (peticionConnectTerminada==false)
        {    
        }
        
        //GET PRESENCE OF CONTACTS Send <SubscribePresence-Request>
        peticionConnectTerminada=false;
        postData=XMLConnectFile(XMLPRESENCECONTACT, csLog);
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONCONNECT);
        http.run();
        while (peticionConnectTerminada==false)
        {    
        }
        
        //Send Alias
        peticionConnectTerminada=false;
        postData=XMLConnectFile(XMLSENDNICK,  csNickname);
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONCONNECT);
        http.run();
        while (peticionConnectTerminada==false)
        {    
        }
        tipoPeticion=0;
        peticionConnectTerminada=false;
        
        return contactList;
        
    }
    
    //Function which add a new contact to sms20 session.
    
    public String AddContact(String csLog, String csContact)
    {
        tipoPeticion=PETICIONADDCONTACT;
        peticionAddContactTerminada=false;
        // Send <Search-Request>. Search identificator of the contact.
        String postData="";
        postData="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><Search-Request><SearchPairList><SearchElement>USER_MOBILE_NUMBER</SearchElement><SearchString>"+csContact+"</SearchString></SearchPairList><SearchLimit>50</SearchLimit></Search-Request></TransactionContent></Transaction></Session></WV-CSP-Message>";
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONCONNECT);
        http.run();
        while (peticionAddContactTerminada==false)
        {    
        }
        peticionAddContactTerminada=false;
        
        //If the search is sucess, the response will have the identificator of the new contact.
        if (resultPost.indexOf(csContact)==-1)
            return "Error. Identificador de contacto de encontrado.";
        
        // Send <GetPresence-Request> to get contact's presence status
        postData="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><GetPresence-Request><User><UserID>wv:"+csContact+"@movistar.es</UserID></User><PresenceSubList xmlns=\"http://www.openmobilealliance.org/DTD/WV-PA1.2\"><OnlineStatus /><ClientInfo /><GeoLocation /><FreeTextLocation /><CommCap /><UserAvailability /><StatusText /><StatusMood /><Alias /><StatusContent /><ContactInfo /></PresenceSubList></GetPresence-Request></TransactionContent></Transaction></Session></WV-CSP-Message>";
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONCONNECT);
        http.run();
        while (peticionAddContactTerminada==false)
        {    
        }
        peticionAddContactTerminada=false;
        
        
        //Now in resultPost, should appear the alias of the new contact
        String csNickname=getAlias(resultPost);
	// Send <ListManage-Request> for Subscriptions
	postData="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+m_nTransId+++"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><ListManage-Request><ContactList>wv:"+csLog+"/~PEP1.0_subscriptions@movistar.es</ContactList><AddNickList><NickName><Name>"+csNickname+"</Name><UserID>wv:"+csContact+"@movistar.es</UserID></NickName></AddNickList><ReceiveList>T</ReceiveList></ListManage-Request></TransactionContent></Transaction></Session></WV-CSP-Message>";
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONADDCONTACT);
        http.run();
        while (peticionAddContactTerminada==false)
        {    
        }
        peticionAddContactTerminada=false;

        // Send <ListManage-Request> for PrivateList
        postData="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><ListManage-Request><ContactList>wv:"+csLog+"/~PEP1.0_privatelist@movistar.es</ContactList><AddNickList><NickName><Name>"+csNickname+"</Name><UserID>wv:"+csContact+"@movistar.es</UserID></NickName></AddNickList><ReceiveList>T</ReceiveList></ListManage-Request></TransactionContent></Transaction></Session></WV-CSP-Message>";
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONADDCONTACT);
        http.run();
        while (peticionAddContactTerminada==false)
        {    
        }
        peticionAddContactTerminada=false;
        tipoPeticion=0;
        return csNickname;
    }
    
    // Deletes a contact
    // Input:	csLog=string with user's telephone number
    //			csContact=user id for the contact to delete (wv:6xxxxxxxx@movistar.es)
    // Returns: none
    public void DeleteContact(String csLog, String csContact)
    {
        String postData="";
        tipoPeticion=PETICIONDELETECONTACT;
        peticionDeleteContactTerminada=false;
        // Send <ListManage-Request> to delete contact from Subscriptions
        postData="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><ListManage-Request><ContactList>wv:"+csLog+"/~PEP1.0_subscriptions@movistar.es</ContactList><RemoveNickList><UserID>"+csContact+"</UserID></RemoveNickList><ReceiveList>T</ReceiveList></ListManage-Request></TransactionContent></Transaction></Session></WV-CSP-Message>";
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONDELETECONTACT);
        http.run();
        while (peticionDeleteContactTerminada==false)
        {    
        }
        
        peticionDeleteContactTerminada=false;
        // Send <ListManage-Request> to delete contact from PrivateList
        postData="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><ListManage-Request><ContactList>wv:"+csLog+"/~PEP1.0_privatelist@movistar.es</ContactList><RemoveNickList><UserID>"+csContact+"</UserID></RemoveNickList><ReceiveList>T</ReceiveList></ListManage-Request></TransactionContent></Transaction></Session></WV-CSP-Message>";
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONDELETECONTACT);
        http.run();
        while (peticionDeleteContactTerminada==false)
        {    
        }
        
        peticionDeleteContactTerminada=false;
        // Send <UnsubscribePresence-Request>
        postData="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><UnsubscribePresence-Request><User><UserID>"+csContact+"</UserID></User></UnsubscribePresence-Request></TransactionContent></Transaction></Session></WV-CSP-Message>";
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONDELETECONTACT);
        http.run();
        while (peticionDeleteContactTerminada==false)
        {    
        }
        
         peticionDeleteContactTerminada=false;
        // Send <DeleteAttributeList-Request>
        postData="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><DeleteAttributeList-Request><UserID>"+csContact+"</UserID><DefaultList>F</DefaultList></DeleteAttributeList-Request></TransactionContent></Transaction></Session></WV-CSP-Message>";
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONDELETECONTACT);
        http.run();
        while (peticionDeleteContactTerminada==false)
        {    
        }
        
         peticionDeleteContactTerminada=false;
        // Send <CancelAuth-Request>
        postData="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><CancelAuth-Request><UserID>"+csContact+"</UserID></CancelAuth-Request></TransactionContent></Transaction></Session></WV-CSP-Message>";
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONDELETECONTACT);
        http.run();
        while (peticionDeleteContactTerminada==false)
        {    
        }
        tipoPeticion=0;
        peticionDeleteContactTerminada=false;
    }
    
    // Sends a message to the destination contact
    // Input:	csLog=string with user's telephone number
    //			csDestination=string with the destination user id (wv:6xxxxxxxx@movistar.es)
    //			csMessage=text of the message
    // Returns: none
    public void SendMessage(String csLog, String csDestination, String csMessage)
    {
        // Send <SendMessage-Request>
        String postData="";
        tipoPeticion=PETICIONSENDMESSAGE;
        peticionSendMessageTerminada=false;
         postData="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><SendMessage-Request><DeliveryReport>F</DeliveryReport><MessageInfo><ContentType>text/html</ContentType><ContentSize>148</ContentSize><Recipient><User><UserID>"+csDestination+"</UserID></User></Recipient><Sender><User><UserID>"+csLog+"@movistar.es</UserID></User></Sender></MessageInfo><ContentData>&lt;span style=\"color:#000000;font-family:\"Microsoft Sans Serif\";font-style:normal;font-weight:normal;font-size:12px;\"&gt;"+csMessage+"&lt;/span&gt;</ContentData></SendMessage-Request></TransactionContent></Transaction></Session></WV-CSP-Message>";
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONSENDMESSAGE);
        http.run();
        while (peticionSendMessageTerminada==false)
        {    
        }
        
        tipoPeticion=0;
        peticionSendMessageTerminada=false;
        
    }
    
    // Authorizes a contact to be informed about our presence status
    // Input:	csUser=user id for the authorized contact (wv:6xxxxxxxx@movistar.es)
    // csTransaction=transaction id received in authorization request
    // Returns: none
    public void AuthorizeContact(String csUser, String csTransaction)
    {
         String postData="";      
	// Send <GetPresence-Request>
        tipoPeticion=PETICIONAUTHORIZECONTACT;
        peticionAuthorizeContactTerminada=false;
        postData="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+csTransaction+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><GetPresence-Request><User><UserID>"+csUser+"</UserID></User><PresenceSubList xmlns=\"http://www.openmobilealliance.org/DTD/WV-PA1.2\"><OnlineStatus /><ClientInfo /><GeoLocation /><FreeTextLocation /><CommCap /><UserAvailability /><StatusText /><StatusMood /><Alias /><StatusContent /><ContactInfo /></PresenceSubList></GetPresence-Request></TransactionContent></Transaction></Session></WV-CSP-Message>";
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONSENDMESSAGE);
        http.run();
        while (peticionAuthorizeContactTerminada==false)
        {    
        }
        
        // Send <Status> ack of the request
        peticionAuthorizeContactTerminada=false;
        postData="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Response</TransactionMode><TransactionID>"+csTransaction+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><Status><Result><Code>200</Code></Result></Status></TransactionContent></Transaction></Session></WV-CSP-Message>";
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONSENDMESSAGE);
        http.run();
        while (peticionAuthorizeContactTerminada==false)
        {    
        }
        
        // Send <PresenceAuth-User>
        peticionAuthorizeContactTerminada=false;
        postData="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><PresenceAuth-User><UserID>"+csUser+"</UserID><Acceptance>T</Acceptance></PresenceAuth-User></TransactionContent></Transaction></Session></WV-CSP-Message>";
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONSENDMESSAGE);
        http.run();
        while (peticionAuthorizeContactTerminada==false)
        {    
        }
        peticionAuthorizeContactTerminada=false;
        tipoPeticion=0;
        
    }
    // Performs polling to search for new message notifications, contacts online, etc...
    // Input:	none
    // Returns: Full text of the response to search for different types of notification
    public String Polling()
    {
        String postData="";
        tipoPeticion=PETICIONPOLLING;
        // Send <Polling-Request>
        peticionPollingTerminada=false;
        postData="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID/></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><Polling-Request/></TransactionContent></Transaction></Session></WV-CSP-Message>";
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONPOLLING);
        http.run();
        while (peticionPollingTerminada==false)
        {    
        }
        peticionPollingTerminada=false;
        tipoPeticion=0;
        
        return resultPost;
    }
    
    
    //Function which logout from SMS2.0 session
    public void Logout()
    {
        String postData="";
        tipoPeticion=PETICIONLOGOUT;
        peticionLogoutTerminada=false;
        postData="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><Logout-Request /></TransactionContent></Transaction></Session></WV-CSP-Message>";
        http = new PeticionesHttp("http://sms20.movistar.es", this, postData, PeticionesHttp.POST, PETICIONLOGOUT);
        http.run();
        while (peticionLogoutTerminada==false)
        {    
        }
        
        peticionLogoutTerminada=false;
        tipoPeticion=0;
        
        //return resultPost;
        
    }
    public String GetAlias()
    {
        return MyAlias;
    }
    public int GetTramsID()
    {
        return m_nTransId;
    }
    public String GetSessionID()
    {
        return SessionID;
    }
    //Used after polling to know if the polling has returned a Message Received.
    //Input: XML returned by the Pollling
    //Returns: True if the XML has a message else False.
    public boolean IsNewMessage(String XMLPolling)
    {
        return (XMLPolling.indexOf("<NewMessage>")!=-1);
    }
    //Used after polling to know if the polling has returned a Presence of contacts.
    //Input: XML returned by the Pollling
    //Returns: True if the XML has presence of contacts else False.
    public boolean IsContactPresence(String XMLPolling)
    {
        return XMLPolling.indexOf("<PresenceNotification-Request>")!=-1;
    }
    //Used after polling to know if the polling has a request of new contact.
    //Input: XML returned by the Pollling
    //Returns: True if the XML has a request of new contact else False.
    public boolean IsNewContact(String XMLPolling)
    {
        return XMLPolling.indexOf("<PresenceAuth-Request>")!=-1;
    }
    //Used after polling to get the Message Received.
    //Input: XML returned by the Pollling
    //Returns: Message Received.
    public String GetMessage(String XMLPolling)
    {
        if (XMLPolling.indexOf("<NewMessage>")!=-1)
        {
            String message;
            message=XMLPolling.substring(XMLPolling.indexOf("<NewMessage>"),XMLPolling.indexOf("</NewMessage>"));
            message=message.substring(message.indexOf("<ContentData>"),message.indexOf("</ContentData>"));
            message=message.substring(message.indexOf("12px;")+7, message.indexOf("span>")-5);
            return message;
        }
        else
            return "";
    }
    //Used after polling to get the MSISDN of the movil which has sent the message.
    //Input: XML returned by the Pollling
    //Returns: MSISDN.
    public String GetUserIDMessage(String XMLPolling)
    {
       if (XMLPolling.indexOf("<NewMessage>")!=-1)
        {
            String userID;
            userID=XMLPolling.substring(XMLPolling.indexOf("<NewMessage>"),XMLPolling.indexOf("</NewMessage>"));
            userID=userID.substring(userID.indexOf("<Sender>"),userID.indexOf("</Sender>"));
            userID=userID.substring(userID.indexOf("<UserID>")+11,userID.indexOf("@movistar.es"));
            return userID;
        }
        else
            return "";
    }
    
    //Used after polling to get the MSISDN of the movil which wants to be authorized.
    //Input: XML returned by the Polling
    //Returns: MSISDN.
    public String GetUserIDNewContact(String XMLPolling)
    {
       if (XMLPolling.indexOf("<PresenceAuth-Request>")!=-1)
        {
            String userID;
            userID=XMLPolling.substring(XMLPolling.indexOf("<PresenceAuth-Request>"),XMLPolling.indexOf("</PresenceAuth-Request>"));
            userID=userID.substring(userID.indexOf("<UserID>")+11,userID.indexOf("<UserID")+20);
            return userID;
        }
        else
            return "";
    }
     //Used after polling to get the Transaction ID of the contact which wants to be authorized.
    //Input: XML returned by the Polling
    //Returns: TransactionID.
    public String GetTransactionIDNewContact(String XMLPolling)
    {
       if (XMLPolling.indexOf("<PresenceAuth-Request>")!=-1)
        {
            String transactionID;
            transactionID=XMLPolling.substring(XMLPolling.indexOf("<Transaction>"),XMLPolling.indexOf("</Transaction>"));
            transactionID=transactionID.substring(transactionID.indexOf("<TransactionID>")+13,transactionID.indexOf("</TransactionID>"));
            return transactionID;
        }
        else
            return "";
    }
   
    //Function which gets an XML with the list of contacts
    private Vector GetContactList(String XMLData)
    {   
	Vector pContactList = new Vector();
        int nEndPos=0;
        if (XMLData.indexOf("<NickList>")!=-1)
        {
            String nickList = "";
            nickList=XMLData.substring(XMLData.indexOf("<NickList>")+10,XMLData.indexOf("</NickList>"));
             if(nickList!="")
            {
                    
                    String auxContact;
                    while (nickList.length()!=0)
                    {                   
                         auxContact= nickList.substring(nickList.indexOf("<NickName>")+10, nickList.indexOf("</NickName>"));
                         
                            if(auxContact!="")
                            {
                                    String nickName;
                                    String userID;
                                    nickName = auxContact.substring(auxContact.indexOf("<Name>")+6, auxContact.indexOf("</Name>"));
                                    userID = auxContact.substring(auxContact.indexOf("<UserID>")+11, auxContact.indexOf("@movistar.es"));
                                    CSMS20Contact pContact=new CSMS20Contact(userID, nickName, false);
                                    pContactList.addElement(pContact);
                      
                            }
                         nickList=nickList.substring(nickList.indexOf("</NickName>")+11,nickList.length());
                    }
                    
            }
           
	}

	return pContactList;
    }
    //Function which gets an XML with the list of presence contacts. After Polling.
    public Vector GetPresenceList(String XMLData)
    {   
	Vector pContactList = new Vector();
        int nEndPos=0;
        if (XMLData.indexOf("<Presence>")!=-1)
        {
            String nickList = "";
            nickList=XMLData.substring(XMLData.indexOf("<PresenceNotification-Request>")+30,XMLData.indexOf("</PresenceNotification-Request>"));
             if(nickList!="")
            {
                    
                    String auxContact;
                    while (nickList.length()!=0)
                    {                   
                         auxContact= nickList.substring(nickList.indexOf("<Presence>")+10, nickList.indexOf("</Presence>"));
                         
                            if(auxContact!="")
                            {
                                    String alias;
                                    String userID;
                                    CSMS20Contact pContact;
                                    userID = auxContact.substring(auxContact.indexOf("<UserID>")+11, auxContact.indexOf("@movistar.es"));
                                    alias = getAlias(auxContact);
                                    if (auxContact.indexOf("AVAILABLE")!=-1)
                                        pContact=new CSMS20Contact(userID, alias, true);
                                    else
                                        pContact=new CSMS20Contact(userID, alias, false);
                                    
                                   pContactList.addElement(pContact);
                      
                            }
                         nickList=nickList.substring(nickList.indexOf("</Presence>")+11,nickList.length());
                    }
                    
            }
           
	}

	return pContactList;
    }
     public void serverGeneralError(int i) {
          if (i==NOCONNERROR)
         {
            resultPost="-2. Error de conexion";
         }
        else
        {
            if (i==NOSECURITYERROR)
            {
                resultPost="-3. Error";
            }
            else
            {
                resultPost="Error Desconocido";        
            }
        }
       
       //The while of the request is finished.
       if (tipoPeticion==PETICIONLOGIN)
           peticionLoginTerminada=true;
       if (tipoPeticion==PETICIONCONNECT)
           peticionConnectTerminada=true;
       if (tipoPeticion==PETICIONADDCONTACT)
           peticionAddContactTerminada=true;
       if (tipoPeticion==PETICIONDELETECONTACT)
           peticionDeleteContactTerminada=true;
       if (tipoPeticion==PETICIONSENDMESSAGE)
           peticionSendMessageTerminada=true;
       if (tipoPeticion==PETICIONAUTHORIZECONTACT)
           peticionAuthorizeContactTerminada=true;
       if (tipoPeticion==PETICIONPOLLING)
           peticionPollingTerminada=true;
       if (tipoPeticion==PETICIONLOGOUT)
           peticionLogoutTerminada=true;
        
    }

    public void serverResultPost(DataInputStream dataInputStream) {
        StringBuffer responseMessage = new StringBuffer();
        try
        {
             int length = dataInputStream.available();   
             StringBuffer out = new StringBuffer();
             if (length!=0)
             {
                 byte[] b = new byte[length];
             
                 for (int n; (n = dataInputStream.read(b)) != -1;) 
                {
                        out.append(new String(b, 0, n));
                }
             }
            if (tipoPeticion==PETICIONLOGIN)
            {
                resultPost=out.toString();
                peticionLoginTerminada=true;
                
            }
            if (tipoPeticion==PETICIONCONNECT)
            {
                resultPost=out.toString();
                peticionConnectTerminada=true;
            }
            if (tipoPeticion==PETICIONADDCONTACT)
            {
                resultPost=out.toString();
                peticionAddContactTerminada=true;
            }
            if (tipoPeticion==PETICIONDELETECONTACT)
            {
                resultPost=out.toString();
                peticionDeleteContactTerminada=true;
            }
            if (tipoPeticion==PETICIONSENDMESSAGE)
            {
                resultPost=out.toString();
                peticionSendMessageTerminada=true;
            }
            if (tipoPeticion==PETICIONAUTHORIZECONTACT)
            {
                resultPost=out.toString();
                peticionAuthorizeContactTerminada=true;
            }
             if (tipoPeticion==PETICIONPOLLING)
            {
                resultPost=out.toString();
                peticionPollingTerminada=true;
            }
            if (tipoPeticion==PETICIONLOGOUT)
            {
                resultPost=out.toString();
                peticionLogoutTerminada=true;
            }
            
        }
        catch (IOException exe)
        {
            exe.printStackTrace();
            responseMessage.append( "ERROR" );
            serverGeneralError(NOSECURITYERROR);
        }
    }


    public void serverResultGet(DataInputStream dataInputStream) {
        StringBuffer responseMessage = new StringBuffer();
        try
        {
             int length = dataInputStream.available();   
             StringBuffer out = new StringBuffer();
             byte[] b = new byte[length];
             for (int n; (n = dataInputStream.read(b)) != -1;) 
            {
                    out.append(new String(b, 0, n));
            }
            resultPost=out.toString();     
        }
        catch (IOException exe)
        {
            exe.printStackTrace();
            responseMessage.append( "ERROR" );
            serverGeneralError(NOSECURITYERROR);
               
        }
    }
    
    private String XMLConnectFile(int xmltype, String LogOrNick)
    {
        String xmlComposition="";
        if (xmltype==XMLCLIENTCAPABILITY)
            xmlComposition="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><ClientCapability-Request><ClientID><URL>WV:InstantMessenger-1.0.2309.16485@COLIBRIA.PC-CLIENT</URL></ClientID><CapabilityList><ClientType>COMPUTER</ClientType><InitialDeliveryMethod>P</InitialDeliveryMethod><AcceptedContentType>text/plain</AcceptedContentType><AcceptedContentType>text/html</AcceptedContentType><AcceptedContentType>image/png</AcceptedContentType><AcceptedContentType>image/jpeg</AcceptedContentType><AcceptedContentType>image/gif</AcceptedContentType><AcceptedContentType>audio/x-wav</AcceptedContentType><AcceptedContentType>image/jpg</AcceptedContentType><AcceptedTransferEncoding>BASE64</AcceptedTransferEncoding><AcceptedContentLength>256000</AcceptedContentLength><MultiTrans>1</MultiTrans><ParserSize>300000</ParserSize><SupportedCIRMethod>STCP</SupportedCIRMethod><ColibriaExtensions>T</ColibriaExtensions></CapabilityList></ClientCapability-Request></TransactionContent></Transaction></Session></WV-CSP-Message>";
        if (xmltype==XMLSERVICE)
            xmlComposition="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><Service-Request><ClientID><URL>WV:InstantMessenger-1.0.2309.16485@COLIBRIA.PC-CLIENT</URL></ClientID><Functions><WVCSPFeat><FundamentalFeat /><PresenceFeat /><IMFeat /><GroupFeat /></WVCSPFeat></Functions><AllFunctionsRequest>T</AllFunctionsRequest></Service-Request></TransactionContent></Transaction></Session></WV-CSP-Message>";
        if (xmltype==XMLUPDATEPRESENCE)
            xmlComposition="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><UpdatePresence-Request><PresenceSubList xmlns=\"http://www.openmobilealliance.org/DTD/WV-PA1.2\"><OnlineStatus><Qualifier>T</Qualifier></OnlineStatus><ClientInfo><Qualifier>T</Qualifier><ClientType>COMPUTER</ClientType><ClientTypeDetail xmlns=\"http://imps.colibria.com/PA-ext-1.2\">PC</ClientTypeDetail><ClientProducer>Colibria As</ClientProducer><Model>TELEFONICA Messenger</Model><ClientVersion>1.0.2309.16485</ClientVersion></ClientInfo><CommCap><Qualifier>T</Qualifier><CommC><Cap>IM</Cap><Status>OPEN</Status></CommC></CommCap><UserAvailability><Qualifier>T</Qualifier><PresenceValue>AVAILABLE</PresenceValue></UserAvailability></PresenceSubList></UpdatePresence-Request></TransactionContent></Transaction></Session></WV-CSP-Message>";
        if (xmltype==XMLGETLIST)
            xmlComposition="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><GetList-Request /></TransactionContent></Transaction></Session></WV-CSP-Message>";
        if (xmltype==XMLGETPRESENCE)
            xmlComposition="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><GetPresence-Request><User><UserID>wv:"+LogOrNick+"@movistar.es</UserID></User></GetPresence-Request></TransactionContent></Transaction></Session></WV-CSP-Message>";
         if (xmltype==XMLGETPRIVATELIST)
            xmlComposition="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><ListManage-Request><ContactList>wv:"+LogOrNick+"/~pep1.0_privatelist@movistar.es</ContactList><ReceiveList>T</ReceiveList></ListManage-Request></TransactionContent></Transaction></Session></WV-CSP-Message>";
        if (xmltype==XMLPRESENCECONTACT)
            xmlComposition="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><SubscribePresence-Request><ContactList>wv:"+LogOrNick+"/~PEP1.0_subscriptions@movistar.es</ContactList><PresenceSubList xmlns=\"http://www.openmobilealliance.org/DTD/WV-PA1.2\"><OnlineStatus /><ClientInfo /><FreeTextLocation /><CommCap /><UserAvailability /><StatusText /><StatusMood /><Alias /><StatusContent /><ContactInfo /></PresenceSubList><AutoSubscribe>T</AutoSubscribe></SubscribePresence-Request></TransactionContent></Transaction></Session></WV-CSP-Message>";
        if (xmltype==XMLSENDNICK)
            xmlComposition="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><UpdatePresence-Request><PresenceSubList xmlns=\"http://www.openmobilealliance.org/DTD/WV-PA1.2\"><Alias><Qualifier>T</Qualifier><PresenceValue>"+LogOrNick+"</PresenceValue></Alias></PresenceSubList></UpdatePresence-Request></TransactionContent></Transaction></Session></WV-CSP-Message>";
       return xmlComposition;
    }
    
     private String XMLConnectFile(int xmltype, String Log, String nickList)
    {
        String xmlComposition="";
        if (xmltype==XMLSUSCRIPTIONLIST)
            xmlComposition="<?xml version=\"1.0\" encoding=\"utf-8\"?><WV-CSP-Message xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns=\"http://www.openmobilealliance.org/DTD/WV-CSP1.2\"><Session><SessionDescriptor><SessionType>Inband</SessionType><SessionID>"+SessionID+"</SessionID></SessionDescriptor><Transaction><TransactionDescriptor><TransactionMode>Request</TransactionMode><TransactionID>"+(m_nTransId++)+"</TransactionID></TransactionDescriptor><TransactionContent xmlns=\"http://www.openmobilealliance.org/DTD/WV-TRC1.2\"><CreateList-Request><ContactList>wv:"+Log+"/~PEP1.0_subscriptions@movistar.es</ContactList><NickList>"+nickList+"</NickList></CreateList-Request></TransactionContent></Transaction></Session></WV-CSP-Message>";
        return xmlComposition;
    }
    
    private String getAlias(String xmlAlias)
    {
        String alias;
        alias="";
        if (xmlAlias.indexOf("<Alias>")!=-1)
        {
            String auxAlias;
            auxAlias=resultPost.substring(resultPost.indexOf("<Alias>")+6,resultPost.indexOf("</Alias>"));
            if (auxAlias.indexOf("<PresenceValue>")!=-1)
                 alias=auxAlias.substring(auxAlias.indexOf("<PresenceValue>")+15,auxAlias.indexOf("</PresenceValue>"));
        }
        return alias;
    }
    
    //Take the nicklist from xmlNickList String File. This file has a secction in this way:
    /*<NickList>
	*	<NickName>
	*		<Name>Luis</Name>
	*		<UserID>wv:6yyyyyyyy@movistar.es</UserID>
	*	</NickName>
	*	<NickName>
	*		<Name>Carlos</Name>
	*		<UserID>wv:6zzzzzzzz@movistar.es</UserID>
	*	</NickName>
	*</NickList>
     */
    private String getNickList(String xmlNickList)
    {
        if (xmlNickList.indexOf("<NickList>")!=-1)
        {
             return resultPost.substring(resultPost.indexOf("<NickList>")+6,resultPost.indexOf("</NickList>"));
        }
        else
            return "";
    }

}
abstract class HttpObserver {
    
    /**
     * Constante de error que se pasa como parametro e indica un erro de conexion
     */
     public static int NOCONNERROR = -2;
    /**
     * Similar al caso anterior, implica error de seguridad
     */
     public static int NOSECURITYERROR = -3;
    
    /**
     * Manejador de un evento de error
     * @param error Tipo de error. Veanse las constantes
     */
     public abstract void serverGeneralError(int error);
    /**
     * Manejador de un evento de consecucion del POST
     * @param di DataInputStream resultado de la peticion
     */
     public abstract void serverResultPost(DataInputStream di);
      /**
     * Manejador de un evento de consecucion del GET
     * @param di DataInputStream resultado de la peticion
     */
     public abstract void serverResultGet(DataInputStream di);
}

/**
 * Clase para el lanzamiento de peticiones http
 */
 class PeticionesHttp  extends Thread {
   

    /**
     * Conexion para el lanzamiento de la conexion
     */
       public HttpConnection hcon = null; 
    /**
     * Stream de entrada de datos
     */
       public DataInputStream dis = null;
    /**
     * Stream de salida de datos
     */
       public DataOutputStream dos = null;
    /**
     * Flag indicativo de que se ha llevado a cabo una cancelacion
     */
       private boolean mCancel = false;
    /**
     * Url a la que se va a realizar la peticion
     */
       private String url = "";
    /**
     * Cadena de respuesta de la peticion
     */
       private String  requeststring = "";
   /**
     * Flag indicativo de que la peticion a realizar se trata de un GET o de un POST
     */
       private boolean get = false;
     /**
     * Observador a traves del que se comunican los eventos aparecidos durante la peticion
     */
       private HttpObserver requestObserver;  
       
       private int tipoPeticion=0;
       
       /*To retrieve cookie value*/
       private String cookieValue="";
       /**
     * Constante del POST
     */
       public static int POST = 0;
    /**
     * Constante del GET
     */
       public static int GET = 1;
        //Allowed requests
       public static int PETICIONLOGIN = 1;
        public static int PETICIONCONNECT = 2;
        public static int PETICIONADDCONTACT=3;
        public static int PETICIONDELETECONTACT=4;
        public static int PETICIONSENDMESSAGE=5;
        public static int PETICIONAUTHORIZECONTACT=6;
        public static int PETICIONPOLLING=7;
        public static int PETICIONLOGOUT= 8;
       
    /**

       /*Constructor*/
    
        /**
     * Instancia la clase http
     * @param currentUrl Url a la que se va a realizar la peticion
     * @param currentRequestObserver Observador a traves del que se van a comunicar los eventos ocurridos durante la peticion
     * @param text Texto que va a ser incluido en la peticion http
     * @param Peticion tipo de peticion HTTP.
     * @param isGet determines if the http is get or post
     */
     
        public PeticionesHttp(String currentUrl,  HttpObserver currentRequestObserver, String text,  int isGet, int peticion) {           
           url = currentUrl;
           requestObserver = currentRequestObserver;
           tipoPeticion=peticion;
           if (isGet == POST )
               get = false;
           else
               get = true;
           requeststring = text;
         
        }
        public PeticionesHttp(String currentUrl,  HttpObserver currentRequestObserver, String text,  int isGet, int peticion, String cookie) {           
           url = currentUrl;
           requestObserver = currentRequestObserver;
           tipoPeticion=peticion;
           if (isGet == POST )
               get = false;
           else
               get = true;
           requeststring = text;
           cookieValue=cookie;
        }

       
     /*Inicia hilo de ejecución*/
    /**
     * Metodo para el lanzamiento de un hilo de ejecucion en el que se va a realizar la peticion POST
     */
       public void run() {
           if (get)
               IssueHTTPGet();
           else
               IssueHTTPPost();
           
       }
        /*Manejo de GET*/
    /**
     * Metodo para el lanzamiento del GET
     */
       public void IssueHTTPGet() {
           try {
                  if( hcon != null ) 
                   {
                       hcon.close();
                       hcon =  null;
                   }
                   if( dis != null ) 
                   {
                       dis.close();
                       dis = null;
                   }
                   if( dos != null )
                   {
                       dos.close();
                       dos = null;
                   }
               } catch ( IOException ioe ) {
                   ioe.printStackTrace();
               }
           
           StringBuffer  responseMessage = new StringBuffer();
           try {
                hcon = ( HttpConnection )Connector.open( url );
                hcon.setRequestMethod( HttpConnection.GET);
                addHeaders();
                InputStream is =  hcon.openInputStream();               
                dis = new DataInputStream(is);
                requestObserver.serverResultGet(dis);
                
               
           } catch( Exception e ) {
               e.printStackTrace();               
               requestObserver.serverGeneralError(requestObserver.NOCONNERROR);
           } finally {
               try {
                  if( hcon != null ) 
                   {
                       hcon.close();
                       hcon =  null;
                   }
                   if( dis != null ) 
                   {
                       dis.close();
                       dis = null;
                   }
                   if( dos != null )
                   {
                       dos.close();
                       dos = null;
                   }
               } catch ( IOException ioe ) {
                   ioe.printStackTrace();
               }
           }        
       }
       
       /*Manejo del POST*/
    /**
     * Metodo para el lanzamiento del POST
     */
       public void IssueHTTPPost(){
           
           StringBuffer responseMessage = new StringBuffer();
           InputStream is;
           try {
               try{
               mCancel = true;
               if( hcon != null ) {
                   hcon.close();
                   hcon =  null;
               }
               if( dis != null ) {
                   dis.close();
                   dis = null;
               }
               if( dos != null ) {
                   dos.close();
                   dos = null;
               }
           }catch (Exception e){}
               
               
               mCancel = false;
               hcon = ( HttpConnection )Connector.open( url, Connector.READ_WRITE );
               hcon.setRequestMethod( HttpConnection.POST );
               addHeaders();
               dos = hcon.openDataOutputStream();
               
               byte[] request_body = requeststring.getBytes();
               
               for( int i = 0; i < request_body.length; i++ ) {
                   dos.writeByte( request_body[i] );
               }
               
               dos.flush();
               is =  hcon.openInputStream();                              
               dis = new DataInputStream(is);
               requestObserver.serverResultPost(dis);
               
                  
            } catch( CertificateException excer ) {
               excer.printStackTrace();
               
               if (!(mCancel)) {
                   responseMessage.append( "ERROR" );
                   requestObserver.serverGeneralError(requestObserver.NOSECURITYERROR);
               }
               
           } catch( SecurityException e ) {
               e.printStackTrace();
               if (!(mCancel)) {
                   responseMessage.append( "ERROR" );
                   requestObserver.serverGeneralError(requestObserver.NOSECURITYERROR);
               }
               
           } catch (ConnectionNotFoundException ex) {
               ex.printStackTrace();
               if (!(mCancel)) {
                   responseMessage.append( "ERROR" );
                   requestObserver.serverGeneralError(requestObserver.NOCONNERROR);
               }
             } catch (IOException exe) {
               exe.printStackTrace();
               if (!(mCancel)){
                   responseMessage.append( "ERROR" );
                   requestObserver.serverGeneralError(requestObserver.NOSECURITYERROR);
               }
               
           }
           catch (Exception exc){
               exc.printStackTrace();
               if (!(mCancel)){
                   responseMessage.append( "ERROR" );
                   requestObserver.serverGeneralError(requestObserver.NOSECURITYERROR);
               }
           } 
           
           
           finally {
               try {
                   if( dos != null )
                   {                          
                       dos.close();                
                       dos = null;
                   }
                   if( hcon != null ) 
                   {
                       hcon.close();
                       hcon =  null;
                   }
                   if( dis != null ) 
                   {
                       dis.close();
                       dis = null;
                   }
                   
                   
               } catch ( IOException ioe ) {
                   ioe.printStackTrace();
               } catch ( Exception e ) {
                   e.printStackTrace();
               }
            
           }
       }
       
       /*Cancelación de http*/
    /**
     * Metodo para la cancelacion de la peticion
     */
       public void cancel() {
            mCancel = true;
       }
       
       public void addHeaders(){
            try {
                        if (hcon!=null)
                        {
                             if (tipoPeticion==PETICIONLOGIN)
                            {
                                 hcon.setRequestProperty("Host","impw.movistar.es");
                                 hcon.setRequestProperty("Accept-Encoding","identity");
                                 hcon.setRequestProperty("Content-Length","164");
                                 hcon.setRequestProperty("Content-type","application/x-www-form-urlencoded");
                                 hcon.setRequestProperty("Connection","Keep-Alive");
                             }
                            if (tipoPeticion==PETICIONCONNECT || tipoPeticion==PETICIONADDCONTACT || tipoPeticion==PETICIONDELETECONTACT || tipoPeticion==PETICIONLOGOUT || tipoPeticion==PETICIONAUTHORIZECONTACT || tipoPeticion==PETICIONPOLLING || tipoPeticion==PETICIONSENDMESSAGE)
                            {
                                 hcon.setRequestProperty("Host","sms20.movistar.es");
                                 hcon.setRequestProperty("Accept-Encoding","identity");
                                 hcon.setRequestProperty("Content-Length","1471");
                                 hcon.setRequestProperty("Content-type","application/vnd.wv.csp.xml");
                                 hcon.setRequestProperty("Expect","100-continue");
                             }
                        }
                }
           catch ( IOException ioe ) {
                  ioe.printStackTrace();
           }
       }
        public String getCookie()
       {
            return cookieValue;
       }
 }
 
//Class to contain contacts
 class CSMS20Contact{
     private String m_csUserID;
     private String m_csAlias;
     private boolean m_csPresent;
     
     public CSMS20Contact(String csUserID, String csAlias, boolean csPresent)
     {
	m_csUserID=csUserID;
	m_csAlias=csAlias;
        m_csPresent=csPresent;
      }
     public String GetUserID()
     {
         return m_csUserID;
     }
     public String GetAlias()
     {
         return m_csAlias;
     }
     public boolean GetPresent()
     {
         return m_csPresent;
     }
     
 }
   