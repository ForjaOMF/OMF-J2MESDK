/*
 * AutoWP.java
 *
 * Created on 27 de Mayo de 2008, 15:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
//Class which implements a method to send a WashPush message.*/
package AutoWP;

import java.io.DataInputStream;
import javax.microedition.io.*;
import javax.microedition.pki.*;
import java.io.*;


/**
 *
 * @author Ivan Alvarez
 */

public class AutoWP extends HttpObserver {
    
     
    private PeticionesHttp http;
    
    //True when the http connection return the response
    boolean peticionTerminada;
    
    //Contains the result of the http connection.
    String resultPost="";
    
    /** Creates a new instance of TestClass */
    public AutoWP() {
        super();
    }
   
    public String SendAutoWP(String Login, String Pwd, String URL, String Text) 
    {   
        peticionTerminada=false;
        resultPost="";
        String postData="TME_USER="+Login+"&TME_PASS="+Pwd+"&WAP_Push_URL="+URL+"&WAP_Push_Text="+Text;
        http = new PeticionesHttp("http://open.movilforum.com/apis/autowap", this, postData);
        http.run();
        //return(http.getResult());
        //It is waiting for the respon of the post request.
        while (peticionTerminada==false)
        {    
        }
        return resultPost;      
    }                   

    public void serverGeneralError(int i) {
        if (i==NOCONNERROR)
            resultPost="-2. Error de conexion";
        else  if (i==NOSECURITYERROR)
            resultPost="-3. Error";
        else
            resultPost="Error Desconocido";        
        peticionTerminada=true;
        //((Midlet)iMidlet).set_string("General Error:"+i);
    }

    public void serverResultPost(DataInputStream dataInputStream) {
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
             //String inStr = out.toString();     
            resultPost=out.toString();
            peticionTerminada=true;
        }
        catch (IOException exe)
        {
            exe.printStackTrace();
            responseMessage.append( "ERROR" );
            serverGeneralError(NOSECURITYERROR);
               
        }
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
     * Observador a traves del que se comunican los eventos aparecidos durante la peticion
     */
       private HttpObserver requestObserver;  
    /**

       /*Constructor*/
    
        /**
     * Instancia la clase http
     * @param currentUrl Url a la que se va a realizar la peticion
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
     * Metodo para el lanzamiento de un hilo de ejecucion en el que se va a realizar la peticion POST
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
           StringBuffer messagebuffer = new StringBuffer(); 
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
               //result=url;
               hcon.setRequestMethod( HttpConnection.POST );
               addHeaders();
               dos = hcon.openDataOutputStream();
               //dos.writeChars(requeststring);
               
               byte[] request_body = requeststring.getBytes();
               
               for( int i = 0; i < request_body.length; i++ ) {
                   dos.writeByte( request_body[i] );
               }
               
               dos.flush();
               //dos.close();
               InputStream is =  hcon.openInputStream();                              
               dis = new DataInputStream(is);
               requestObserver.serverResultPost(dis);
              
                }
       
            
               
            catch( SecurityException e ) {
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
            
       
}


    