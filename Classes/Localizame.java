/*
 * Localizame.java
 *
 * Created on 27 de Mayo de 2008, 15:59
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
//Class which implements a method to send a WashPush message.*/
package hello;

import java.io.DataInputStream;
import javax.microedition.io.*;
import javax.microedition.pki.*;
import java.io.*;


/**
 *
 * @author Ivan Alvarez
 */

public class Localizame extends HttpObserver {
        
    private PeticionesHttp http;
    
    //True when the http connection return the response
    boolean peticionLoginTerminada;
    boolean peticionNuevoUsuarioTerminada;
    boolean peticionLocateTerminada;
    boolean peticionAuthorizeTerminada;
    boolean peticionUnauthorizeTerminada;
    boolean peticionLogoutTerminada;
    
    //Contains the result of the http connection.
    public String resultPost="";
    
    //Cookie returned by the petition of Login
    private String cookieLogin="";
    
    //True if the user is logged
    private boolean logged=false;
    //Keep the kind of request to make to the server
    int tipoPeticion=0;
    //Allowed requests
    public static int PETICIONLOGIN = 1;
    public static int PETICIONNUEVOUSUARIO = 2;
    public static int PETICIONLOCATE = 3;
    public static int PETICIONAUTHORIZE = 4;
    public static int PETICIONUNAUTHORIZE = 5;
    public static int PETICIONLOGOUT = 6;
    
    private String numeroLocate="";
    /** Creates a new instance of TestClass */
    public Localizame() {
        super();
    }
   
    //Realised a login to the localizame functionality
    public void Login(String Login, String Pwd) 
    {   
        peticionLoginTerminada=false;
        //If other petition is being executed, wait until it has finished.
        if (tipoPeticion!=0) while (tipoPeticion!=0){}
        tipoPeticion=PETICIONLOGIN;
        resultPost="";
        String postData="usuario="+Login+"&clave="+Pwd+"&submit.x=36&submit.y=6";
        http = new PeticionesHttp("http://www.localizame.movistar.es/login.do", PeticionesHttp.POST, this, postData, PETICIONLOGIN);
        http.run();
         while (peticionLoginTerminada==false)
        {    
        }
        //It is re-inicialized the kind of request.
        tipoPeticion=0;
        if (http.getCookie()!="" && resultPost.indexOf("Restringido")==-1)
        {
            logged=true;
            cookieLogin=http.getCookie();
            NuevoUsuario();
        }
        else
            logged=false;
        //return " Cookie="+cookieLogin;
        //return resultPost+" Cookie="+http.getCookie();       
        
        
    }                   
    //Makes posible to use the service to be located.
    private void NuevoUsuario() 
    {   
        peticionNuevoUsuarioTerminada=false;
        //If other petition is being executed, wait until it has finished.
        if (tipoPeticion!=0) while (tipoPeticion!=0){}
        tipoPeticion=PETICIONNUEVOUSUARIO;
        resultPost="";
        //String getData="usuario="+Login+"&clave="+Pwd+"&submit.x=36&submit.y=6";
        http = new PeticionesHttp("http://www.localizame.movistar.es/nuevousuario.do", PeticionesHttp.GET, this, "", PETICIONNUEVOUSUARIO, cookieLogin);
        http.run();
         while (peticionNuevoUsuarioTerminada==false)
        {    
        }
        //It is re-inicialized the kind of request.
        tipoPeticion=0;
        //return resultPost;      
    }        
    //Realised the petition to locate a terminal. Returns text location or error
    public String Locate(String csNumber) 
    {   
        peticionLocateTerminada=false;
        //If other petition is being executed, wait until it has finished.
        if (tipoPeticion!=0) while (tipoPeticion!=0){}
        tipoPeticion=PETICIONLOCATE;
        resultPost="";
        numeroLocate=csNumber;
        String postData="telefono="+csNumber;
        http = new PeticionesHttp("http://www.localizame.movistar.es/buscar.do", PeticionesHttp.POST, this, postData, PETICIONLOCATE, cookieLogin);
        http.run();
         while (peticionLocateTerminada==false)
        {    
        }
        //It is re-inicialized the kind of request.
        tipoPeticion=0;
        return resultPost;      
    }
    
    //Realised the petition to authorize to other terminal to locate you.
    public void Authorize(String Number) 
    {   
        peticionAuthorizeTerminada=false;
        //If other petition is being executed, wait until it has finished.
        if (tipoPeticion!=0) while (tipoPeticion!=0){}
        tipoPeticion=PETICIONAUTHORIZE;
        resultPost="";
        //String postData="telefono="+csNumber;
        http = new PeticionesHttp("http://www.localizame.movistar.es/insertalocalizador.do?telefono="+Number+"&submit.x=40&submit.y=5", PeticionesHttp.GET, this, "", PETICIONAUTHORIZE, cookieLogin);
        http.run();
         while (peticionAuthorizeTerminada==false)
        {    
        }
        //It is re-inicialized the kind of request.
        tipoPeticion=0;
        //return resultPost;      
    }        
    //Realised the petition to unauthorize to other terminal to locate you.
    public void Unauthorize(String Number) 
    {   
        peticionUnauthorizeTerminada=false;
        //If other petition is being executed, wait until it has finished.
        if (tipoPeticion!=0) while (tipoPeticion!=0){}
        tipoPeticion=PETICIONUNAUTHORIZE;
        resultPost="";
        //String postData="telefono="+csNumber;
        http = new PeticionesHttp("http://www.localizame.movistar.es/borralocalizador.do?telefono="+Number+"&submit.x=44&submit.y=8", PeticionesHttp.GET, this, "", PETICIONAUTHORIZE, cookieLogin);
        http.run();
         while (peticionUnauthorizeTerminada==false)
        {    
        }
        //It is re-inicialized the kind of request.
        tipoPeticion=0;
        //return resultPost;      
    }    
    //Realised the petition to logout localizame.
    public void Logout() 
    {   
        peticionLogoutTerminada=false;
        //If other petition is being executed, wait until it has finished.
        if (tipoPeticion!=0) while (tipoPeticion!=0){}
        tipoPeticion=PETICIONLOGOUT;
        resultPost="";
        //String postData="telefono="+csNumber;
        http = new PeticionesHttp("http://www.localizame.movistar.es/logout.do", PeticionesHttp.GET, this, "", PETICIONAUTHORIZE, cookieLogin);
        http.run();
         while (peticionLogoutTerminada==false)
        {    
        }
        logged=false;
        //It is re-inicialized the kind of request.
        tipoPeticion=0;
       //return resultPost;      
    }    
    public boolean isLogged()
    {
        return logged;
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
                if (i==LOCALIZATIONERROR)
                    resultPost="-4. Error de localización";
                else
                    resultPost="Error Desconocido";        
            }
        }
       
       //The while of the request is finished.
       if (tipoPeticion==PETICIONLOGIN)
           peticionLoginTerminada=true;
        if (tipoPeticion==PETICIONNUEVOUSUARIO)
           peticionNuevoUsuarioTerminada=true;      
        if (tipoPeticion==PETICIONLOCATE)
           peticionLocateTerminada=true;
        if (tipoPeticion==PETICIONAUTHORIZE)
           peticionAuthorizeTerminada=true;
        if (tipoPeticion==PETICIONUNAUTHORIZE)
           peticionUnauthorizeTerminada=true;
        if (tipoPeticion==PETICIONLOGOUT)
           peticionLogoutTerminada=true;
       
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
            //resultPost=out.toString();
            //resultPost=" Correcto ";
            if (tipoPeticion==PETICIONLOGIN)
            {
                resultPost=out.toString();
                peticionLoginTerminada=true;
                
            }
            if (tipoPeticion==PETICIONLOCATE)
            {
                resultPost=out.toString();
                //It is checked that the number of telephone to be located is in the returned result and that the word "metros." is on it aswell. 
                //This is because the user could ask to locate himself and as the 
                if (resultPost.indexOf(numeroLocate)!=-1 && resultPost.indexOf("metros.")!=-1)
                    resultPost=resultPost.substring(resultPost.indexOf(numeroLocate),resultPost.indexOf("metros.")+7);
                else
                    resultPost="Acceso Restringuido. Debe introducir el teléfono y la clave. Para obtener la clave válida durante 30 minutos, envíe CLAVE al 242.";
                peticionLocateTerminada=true;
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
             //String inStr = out.toString();     
            //resultPost=" Correcto ";
            resultPost=out.toString();     
            if (tipoPeticion==PETICIONNUEVOUSUARIO)
                peticionNuevoUsuarioTerminada=true;
            if (tipoPeticion==PETICIONAUTHORIZE)
                peticionAuthorizeTerminada=true;
            if (tipoPeticion==PETICIONUNAUTHORIZE)
                peticionUnauthorizeTerminada=true;
            if (tipoPeticion==PETICIONLOGOUT)
                peticionLogoutTerminada=true;
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
    
    /*Error de localización del número solicitado*/
     public static int LOCALIZATIONERROR = -4;
     
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
       public static int PETICIONNUEVOUSUARIO = 2;
       public static int PETICIONLOCATE = 3;
       public static int PETICIONAUTHORIZE = 4;
       public static int PETICIONUNAUTHORIZE = 5;
       public static int PETICIONLOGOUT = 6;
    /**

       /*Constructor*/
    
        /**
     * Instancia la clase http
     * @param currentUrl Url a la que se va a realizar la peticion
     * @param currentRequestObserver Observador a traves del que se van a comunicar los eventos ocurridos durante la peticion
     * @param text Texto que va a ser incluido en la peticion http
     * @param int Peticion indica el tipo de peticion que se está haciendo (login, localizame) y se usa para incluir las cabeceras necesarias
     **/
       public PeticionesHttp(String currentUrl,  int isGet, HttpObserver currentRequestObserver, String text, int peticion) {           
           url = currentUrl;
           requestObserver = currentRequestObserver;
           tipoPeticion=peticion;
           if (isGet == POST )
               get = false;
           else
               get = true;
           requeststring = text;
           //cookieValue=cookie;
        }
       public PeticionesHttp(String currentUrl,  int isGet, HttpObserver currentRequestObserver, String text, int peticion, String cookie) {           
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
                if (tipoPeticion==PETICIONAUTHORIZE || tipoPeticion==PETICIONUNAUTHORIZE || tipoPeticion==PETICIONLOGOUT)
               {
                   if (hcon.getResponseCode()!=HttpConnection.HTTP_OK)                        
                     requestObserver.serverGeneralError(requestObserver.NOCONNERROR);   
               }
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
               String numTfno2=requeststring.substring(9,17);
               InputStream is =  hcon.openInputStream();                              
               dis = new DataInputStream(is);
               if (tipoPeticion==PETICIONLOGIN)
               {
                   //if (hcon.getResponseCode()==HttpConnection.HTTP_OK)                        
                       cookieValue = hcon.getHeaderField("Set-Cookie"); 
                       if (cookieValue!="")
                           cookieValue=cookieValue.substring(0,cookieValue.indexOf(";"));
                   //else
                   //  requestObserver.serverGeneralError(requestObserver.NOCONNERROR);   
               }
               if (tipoPeticion==PETICIONLOCATE)
               {
                   if (hcon.getResponseCode()!=HttpConnection.HTTP_OK)  
                       requestObserver.serverGeneralError(requestObserver.NOCONNERROR);   
               }
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
                           
                            if (tipoPeticion==PETICIONLOGIN)
                            {
                                hcon.setRequestProperty("Content-type","application/x-www-form-urlencoded");
                                hcon.setRequestProperty("Host","www.localizame.movistar.es");
                                hcon.setRequestProperty("Accept-Encoding","identity");
                                hcon.setRequestProperty("Accept-Language","es");
                                hcon.setRequestProperty("Accept","image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, application/x-shockwave-flash, */*");
                                hcon.setRequestProperty("Connection","Keep-Alive");
                            }
                            if (tipoPeticion==PETICIONNUEVOUSUARIO)
                            {
                                hcon.setRequestProperty("Accept-Language","es");
                                hcon.setRequestProperty("Host","www.localizame.movistar.es");
                                hcon.setRequestProperty("Accept-Encoding","identity");
                                hcon.setRequestProperty("Accept","image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, application/x-shockwave-flash, */*");
                                hcon.setRequestProperty("Connection","Keep-Alive");
                                hcon.setRequestProperty("Referer","http://www.localizame.movistar.es/login.do");
                                hcon.setRequestProperty("Cookie",cookieValue);
                            }    
                            if (tipoPeticion==PETICIONLOCATE)
                            {
                                hcon.setRequestProperty("Content-type","application/x-www-form-urlencoded");
                                hcon.setRequestProperty("Host","www.localizame.movistar.es");
                                hcon.setRequestProperty("Accept-Encoding","identity");
                                hcon.setRequestProperty("Accept-Language","es");                                
                                hcon.setRequestProperty("Accept","image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, application/x-shockwave-flash, */*");
                                hcon.setRequestProperty("Connection","Keep-Alive");
                                hcon.setRequestProperty("Cookie",cookieValue);
                            }
                            if (tipoPeticion==PETICIONAUTHORIZE)
                            {
                                hcon.setRequestProperty("Accept-Language","es");
                                hcon.setRequestProperty("Host","www.localizame.movistar.es");
                                hcon.setRequestProperty("Accept-Encoding","identity");       
                                hcon.setRequestProperty("Accept","image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, application/x-shockwave-flash, */*");
                                hcon.setRequestProperty("Connection","Keep-Alive");
                                hcon.setRequestProperty("Referer","://www.localizame.movistar.es/buscalocalizadorespermisos.do");
                                hcon.setRequestProperty("Cookie",cookieValue);
                            }
                            if (tipoPeticion==PETICIONUNAUTHORIZE)
                            {
                                hcon.setRequestProperty("Content-type","application/x-www-form-urlencoded");
                                hcon.setRequestProperty("Accept-Encoding","identity");
                                hcon.setRequestProperty("Accept-Language","es");
                                hcon.setRequestProperty("Host","www.localizame.movistar.es");
                                hcon.setRequestProperty("Accept","image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, application/x-shockwave-flash, */*");
                                hcon.setRequestProperty("Connection","Keep-Alive");
                                hcon.setRequestProperty("Cookie",cookieValue);
                            }
                            if (tipoPeticion==PETICIONLOGOUT)
                            {
                                hcon.setRequestProperty("Accept-Language","es\r\n");
                                hcon.setRequestProperty("Host","www.localizame.movistar.es\r\n");
                                hcon.setRequestProperty("Accept-Encoding","identity\r\n");
                                hcon.setRequestProperty("Accept","image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/vnd.ms-powerpoint, application/vnd.ms-excel, application/msword, application/x-shockwave-flash, */*");
                                hcon.setRequestProperty("Connection","Keep-Alive");
                                hcon.setRequestProperty("Cookie",cookieValue);
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


    