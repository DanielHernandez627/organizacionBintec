
package Modelo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author Daniel Hernandez
 */
public class Utilidades {
    
    public String consumir(String Code) throws IOException //Metodo de consumo del WS
    {
        String str = "";
        String retorno = "";
        StringBuffer buf = new StringBuffer();    
        URL url= new URL("https://test.evalartapp.com/extapiquest/code_decrypt/"+Code);
        URLConnection urlConnection = url.openConnection();
        urlConnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
        InputStream is = urlConnection.getInputStream();
        BufferedReader objreader = new BufferedReader(new InputStreamReader(is));
         if (is != null) {                            
            while ((str = objreader.readLine()) != null) {    
                buf.append(str);
                retorno = str;
            }                
        }
        
        retorno = retorno.replace("\"", "");
         
        return retorno;
    }
    
    
}
