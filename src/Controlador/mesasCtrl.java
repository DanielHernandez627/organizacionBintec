
package Controlador;

import Modelo.Mesas;

/**
 *
 * @author Daniel Hernandez
 */
public class mesasCtrl {
    
    /*Inicio Objeto Metodos Modelo*/
    Mesas mesas = new Mesas();
    /*Fin Objeto Metodos Modelo*/
    
    
    public String adminmesas(String entrada){
        
        mesas.controlmesas(entrada);
        return mesas.getSalida();
    }
    
    
}
