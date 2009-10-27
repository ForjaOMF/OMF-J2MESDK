/*
 * SMSSender.java
 *
 * Created on 14 de mayo de 2008, 15:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package SMSSender;

import java.io.DataInputStream;
import javax.microedition.io.*;
import javax.microedition.pki.*;
import java.io.*;


/**
 *
 * @author Javier 
 */

public class SMSSender extends HttpObserver {
    
    public static int SMS_HEADERS = 1;
    
    private PeticionesHttp http;
    
    /** Creates a new instance of TestClass */
    public SMSSender() {
        super();
    }
   
    public String SendMessage(String Login, String Pwd, String Dest, String Msg) 
    {   
    String postData="TM_ACTION=AUTHENTICATE&TM_LOGIN="+Login+"&TM_PASSWORD="+Pwd+"&to="+Dest+"&message="+Msg;
    http = new PeticionesHttp("https://opensms.movistar.es/aplicacionpost/loginEnvio.jsp", this, postData);
    http.run();
    return(http.getResult());
    }

    public void serverGeneralError(int i) {
        //((Midlet)iMidlet).set_string("General Error:"+i);
    }

    public void serverResultGet(DataInputStream dataInputStream) {
        //((Midlet)iMidlet).set_string("GET Received");
    }

    public void serverResultPost(DataInputStream dataInputStream) {
        //((Midlet)iMidlet).set_string("POST Received=");
        //if (http!=null)
        //      ((Midlet)iMidlet).set_string("resultado="+http.getResult());
    }
    
}

abstract class HttpObserver {
    
    /**
     * Constante de error que se pasa como parametro e indica un erro de conexion
     */
     public static int NOCONNERROR = 0;
    /**
     * Similar al caso anterior, implica error de seguridad
     */
     public static int NOSECURITYERROR = 1;
    
    /**
     * Manejador de un evento de error
     * @param error Tipo de error. Veanse las constantes
     */
     public abstract void serverGeneralError(int error);
    /**
     * Manejador de un evento de consecucion del POST
     * @param di DataInputStream resultado de la peticion
     */
     public abstract void serverResultGet(DataInputStream di);
    /**
     * Manejador de un evento de consecucion del GET
     * @param di DataInputStream resultado de la peticion
     */
     public abstract void serverResultPost(DataInputStream di);
}

/**
 * Clase para el lanzamiento de peticiones http
 */
 class PeticionesHttp  extends Thread {
   

    /**
     * Conexion para el lanzamiento de la conexion
     */
       public HttpsConnection hcon = null; 
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
   
    /**Resultado devuelto por la url*/
       String result="";
    /**
     * Observador a traves del que se comunican los eventos aparecidos durante la peticion
     */
       private HttpObserver requestObserver;  
    /**

       /*Constructor*/
    
        /**
     * Instancia la clase http
     * @param currentUrl Url a la que se va a realizar la peticion
     * @param isGet Indicador de que la peticion es un get o un post
     * @param currentRequestObserver Observador a traves del que se van a comunicar los eventos ocurridos durante la peticion
     * @param text Texto que va a ser incluido en la peticion http
     */
       public PeticionesHttp(String currentUrl,  HttpObserver currentRequestObserver, String text) {           
           url = currentUrl;
           requestObserver = currentRequestObserver;
           requeststring = text;
        }
       
     /*Inicia hilo de ejecución*/
    /**
     * Metodo para el lanzamiento de un hilo de ejecucion en el que se va a realizar la peticion GET o POST
     */
       public void run() {
           IssueHTTPPost();
           
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
               hcon = ( HttpsConnection )Connector.open( url);
               hcon.setRequestMethod( HttpsConnection.POST );
            
               addHeaders();
               dos = hcon.openDataOutputStream();
               
               //byte[] request_body = requeststring.getBytes();
               
               //for( int i = 0; i < request_body.length; i++ ) {
               //    dos.writeByte( request_body[i] );
               //}
               dos.write( requeststring.getBytes());
               dos.flush();
               SecurityInfo si = hcon.getSecurityInfo();
               Certificate c = si.getServerCertificate();
               int responseCode = hcon.getResponseCode();
               
               
               //if (responseCode==HttpsConnection.HTTP_OK )
               //{
               is =  hcon.openInputStream();
               dis = new DataInputStream(is);
               requestObserver.serverResultPost(dis);
               String protocol=hcon.getProtocol();
               result = hcon.getResponseMessage();
               //}
               
               
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
                            hcon.setRequestProperty("Content-type","application/x-www-form-urlencoded");
                            hcon.setRequestProperty("Accept-Encoding","gzip, deflate");
                            hcon.setRequestProperty("Host","opensms.movistar.es");
                            hcon.setRequestProperty("Accept","image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
                            hcon.setRequestProperty("Connection","Keep-Alive");
                        }
                }
           catch ( IOException ioe ) {
                  ioe.printStackTrace();
           }
       }
            
        public String getResult()
        {
            return result;
        }
}


    