
package Modelo;

import DAO.Conexion;
import java.io.IOException;
import java.sql.ResultSet;

/**
 *
 * @author Daniel Hernandez
 */
public class Mesas {
    
    private String entrada;
    private String salida = "";
    private int cantidadmesas;
    private final String Code[] = new String[8];
    private final String Male[] = new String[8];
    private final String Type[] = new String[8];
    private final String Location[] = new String[8];
    private final String Company[] = new String[8];
    private final String Encrypt[] = new String[8];
    private final String Tbalance[] = new String[8];
    
    /*Inicio Objeto Metodos Modelo*/
    Utilidades util = new Utilidades();
    /*Fin Objeto Metodos Modelo*/
    
    public Mesas() {
        
    }

    public Mesas(String entrada, String salida) {
        this.entrada = entrada;
        this.salida = salida;
    }

    public String getEntrada() {
        return entrada;
    }

    public String getSalida() {
        return salida;
    }

    public void setEntrada(String entrada) {
        this.entrada = entrada;
    }

    public void setSalida(String salida) {
        this.salida = salida;
    }
    
    public String controlmesas(String entrada){
        
        separarentrada(entrada);
        
        return "";
    }
    
    
    public void separarentrada(String entrada){
        
        String separar[] = entrada.split("<");
        String configuraciones[] = null;  
        String configGeneral[] = null;
        
        for (String separar1 : separar) { //Se separan las configuraciones de cada mesa y la general adicional a obtener el numero de mesas
            
            if (separar1.contains("General")) {
                this.cantidadmesas = this.cantidadmesas + 1;
                configGeneral = separar1.split("General");
                
                for (String configGeneral1 : configGeneral) {  
                    if (!"".equals(configGeneral1)) {
                        obtenerFiltros(configGeneral1,true);   
                    }
                }
            }
            
            if (separar1.contains("Mesa")) {
                this.cantidadmesas = this.cantidadmesas + 1;
                configuraciones = separar1.split("Mesa");
                for (String configuraciones1 : configuraciones) {  
                    if (!"".equals(configuraciones1)) {
                        obtenerFiltros(configuraciones1,false);   
                    }
                }
            }
            
        }
        
    }
    
    public void obtenerFiltros(String cadenaconfig,boolean indmesaGeneral){ //Se obtienen los datos para los filtros de los clientes para cada  mesa
        
        String separarparametros[] = null;
        
        int tc = 0,ug = 0,ri = 0,rf = 0;
        
        separarparametros = cadenaconfig.split(";");

        for (String separarparametro : separarparametros) {
            String separartc[] = separarparametro.split(":");

            for (int i = 0; i < separartc.length; i++) {
                switch (separartc[i]) {
                    case "TC":
                        tc = Integer.parseInt(separartc[i+1]);
                        break;
                    case "UG":
                        ug = Integer.parseInt(separartc[i+1]);
                        break;
                    case "RI":
                        ri = Integer.parseInt(separartc[i+1]);
                        break;
                    case "RF":
                        rf = Integer.parseInt(separartc[i+1]);
                        break;
                }
            }
        }
          
//        System.out.println("TC " + tc);
//        System.out.println("UG " + ug);
//        System.out.println("RI " + ri);
//        System.out.println("RF " + rf);
        
        configurarMesas(tc, ug, ri, rf,indmesaGeneral);
    }
    
    public void configurarMesas(int tc,int ug,int ri, int rf,boolean indmesaGeneral){
        
        Conexion conexion = new Conexion();
        
        int i=0;
        
        ResultSet rsM = null;
        ResultSet rsF = null;
        
        String queryM = ""; //Cadena para consulta filtrada por Masculino
        String queryF = ""; //Cadena para consulta filtrada por Femenino 
        
        //Consulta para cada caso de filtros
        
        queryM = "SELECT cl.id,cl.code,cl.male,cl.type,cl.location,cl.company,cl.encrypt,SUM(ac.balance) AS Tbalance FROM evalart_reto.client cl INNER JOIN evalart_reto.account ac ON cl.id = ac.client_id  WHERE";
        queryF = "SELECT cl.id,cl.code,cl.male,cl.type,cl.location,cl.company,cl.encrypt,SUM(ac.balance) AS Tbalance FROM evalart_reto.client cl INNER JOIN evalart_reto.account ac ON cl.id = ac.client_id  WHERE";
        
        if ((tc > 0) && (ug == 0) && (ri == 0) && (rf == 0)) { 
            queryM = queryM + " type = '"+tc+"'  AND male = '1' GROUP BY code ORDER BY Tbalance DESC LIMIT 4;";
            queryF = queryF + " type = '"+tc+"' AND male = '0' GROUP BY code ORDER BY Tbalance DESC LIMIT 4;";
        }else if((tc == 0) && (ug > 0) && (ri > 0) && (rf == 0)){
            queryM = queryM + "  location = '"+ug+"' AND ac.balance >= '"+ri+"' AND male = '1' GROUP BY code ORDER BY Tbalance DESC LIMIT 4;";
            queryF = queryF + "  location = '"+ug+"' AND ac.balance >= '"+ri+"' AND male = '0' GROUP BY code ORDER BY Tbalance DESC LIMIT 4;";
        }else if((tc == 0) && (ug > 0) && (ri == 0) && (rf > 0)){
            queryM = queryM + "  location = '"+ug+"' AND ac.balance >= '"+rf+"' AND male = '1' GROUP BY code ORDER BY Tbalance DESC LIMIT 4;";
            queryF = queryF + "  location = '"+ug+"' AND ac.balance >= '"+rf+"' AND male = '0' GROUP BY code ORDER BY Tbalance DESC LIMIT 4;";
        }else if((tc > 0) && (ug > 0) && (ri == 0) && (rf > 0)){
            queryM = queryM + "  type = '"+tc+"' AND location = '"+ug+"' AND ac.balance >= '"+rf+"' AND male = '1' GROUP BY code ORDER BY Tbalance DESC LIMIT 4;";
            queryF = queryF + "  type = '"+tc+"' AND location = '"+ug+"' AND ac.balance >= '"+rf+"' AND male = '0' GROUP BY code ORDER BY Tbalance DESC LIMIT 4;";
        }else if((tc == 0) && (ug > 0) && (ri == 0) && (rf == 0)){
            queryM = queryM + "  location = '"+ug+"' AND male = '1' GROUP BY code ORDER BY Tbalance DESC LIMIT 4;";
            queryF = queryF + "  location = '"+ug+"' AND male = '0' GROUP BY code ORDER BY Tbalance DESC LIMIT 4;";
        }else if((tc > 0) && (ug == 0) && (ri > 0) && (rf == 0)){
            queryM = queryM + "  type = '"+tc+"' AND ac.balance >= '"+ri+"' AND male = '1' GROUP BY code ORDER BY Tbalance DESC LIMIT 4;";
            queryF = queryF + "  type = '"+tc+"' AND ac.balance >= '"+ri+"' AND male = '0' GROUP BY code ORDER BY Tbalance DESC LIMIT 4;";
        }
        
        rsM = conexion.ExeConsulta(queryM);
        rsF = conexion.ExeConsulta(queryF);
        
        try{
            
            /*Inicio tratamiento de datos*/

            while(rsM.next()){
                Code[i] = rsM.getString("cl.code");
                Male[i] = rsM.getString("cl.male");
                Type[i] = rsM.getString("cl.type");
                Location[i] = rsM.getString("cl.location");
                Company[i] = rsM.getString("cl.company");
                Encrypt[i] = rsM.getString("cl.encrypt");
                Tbalance[i] = rsM.getString("Tbalance");
                i++;
            }
            
            while (rsF.next()) {
                Code[i] = rsF.getString("cl.code");
                Male[i] = rsF.getString("cl.male");
                Type[i] = rsF.getString("cl.type");
                Location[i] = rsF.getString("cl.location");
                Company[i] = rsF.getString("cl.company");
                Encrypt[i] = rsF.getString("cl.encrypt");
                Tbalance[i] = rsF.getString("Tbalance");
                i++;
            }
            
            String companiaduplicada = "";
            int posicionduplicada = 0;
            
            for (int j = 0; j < Company.length; j++) { //Revision Duplicidad de Compania
               
                if (j != 0) {
                    if (Company[j-1].equals(Company[j])) {
                        companiaduplicada = Company[j];
                        posicionduplicada = j;
                        correccionCompania(companiaduplicada,Code[posicionduplicada],Male[posicionduplicada],Tbalance[posicionduplicada], j, tc, ug, ri, rf);
                    }
                }
                
//                System.out.println(Code[j] + "-" + Male[j]+ "-" + Type[j]+ "-" + Location[j]+ "-" + Company[j]+ "-" + Encrypt[j]+ "-" + Tbalance[j]);
            }
            
            int posicionencryptada;
            
            for (int j = 0; j < Company.length; j++) { //Revision Code Encriptado
               
                if (Encrypt[j].equals("1")) {
                    posicionencryptada = j;
                    desencriptar(Code[j], posicionencryptada);
                }
            }
            
            
            /*Fin tratamiento de datos*/
            
            /*Inicio de Escritura de Salida*/
            int cantidadcode = 0;
            if (indmesaGeneral == true) {
                String etiquetaP = "<General>" + "\n";
                String codeInvitadosG= "";
                for (String Code1 : Code) {
                    if (!"N".equals(Code1)) {
                        cantidadcode ++;
                    }
                }
                
                if (cantidadcode > 4) {
                    for (String Code1 : Code) {
                        if (!"N".equals(Code1)) {
                            if ("".equals(codeInvitadosG)) {
                                codeInvitadosG = codeInvitadosG + Code1;
                            }else{
                                codeInvitadosG = codeInvitadosG + "," + Code1;   
                            }
                        }
                    }
                    
                    this.salida = this.salida + etiquetaP + codeInvitadosG + "\n";
                }else{
                    this.salida = this.salida + etiquetaP + "CANCELADA" + "\n";
                }
            }else if(indmesaGeneral == false){
                String etiquetaMesa = "<MESA "+(this.cantidadmesas - 1)+">" + "\n";
                
                String codeInvitadosM= "";
                for (String Code1 : Code) {
                    if (!"N".equals(Code1)) {
                        cantidadcode ++;
                    }
                }
                
                if (cantidadcode > 4) {
                    for (String Code1 : Code) {
                        if (!"N".equals(Code1)) {
                            if ("".equals(codeInvitadosM)) {
                                codeInvitadosM = codeInvitadosM + Code1;
                            }else{
                                codeInvitadosM = codeInvitadosM + "," + Code1;   
                            }
                        }
                    }
                    
                    this.salida = this.salida + etiquetaMesa + codeInvitadosM + "\n";
                }else{
                    this.salida = this.salida + etiquetaMesa + "CANCELADA" + "\n";
                }
                
            }
            /*Fin de Escritura de Salida*/
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public void correccionCompania(String company,String code,String male,String balance,int posicion,int tc,int ug,int ri, int rf){ //Metodo para la busqueda de nuevo cliente por duplicidad de compania
        
        Conexion conexion = new Conexion();
        
        ResultSet rs = null;
        
        String queryDuplicidad = "SELECT cl.id,cl.code,cl.male,cl.type,cl.location,cl.company,cl.encrypt,SUM(ac.balance) AS Tbalance FROM evalart_reto.client cl INNER JOIN evalart_reto.account ac ON cl.id = ac.client_id  WHERE ";
        
        if ((tc > 0) && (ug == 0) && (ri == 0) && (rf == 0)) { 
            queryDuplicidad = queryDuplicidad + " code <> '"+code+"' AND company <> '"+company+"' AND type = '"+tc+"'  AND male = '"+male+"' GROUP BY code ORDER BY Tbalance DESC LIMIT 10;";
        }else if((tc == 0) && (ug > 0) && (ri > 0) && (rf == 0)){
            queryDuplicidad = queryDuplicidad + "  code <> '"+code+"' AND company <> '"+company+"' AND location = '"+ug+"' AND ac.balance >= '"+ri+"' AND male = '"+male+"' GROUP BY code ORDER BY Tbalance DESC LIMIT 10;";
        }else if((tc == 0) && (ug > 0) && (ri == 0) && (rf > 0)){
            queryDuplicidad = queryDuplicidad + "  code <> '"+code+"' AND company <> '"+company+"' AND location = '"+ug+"' AND ac.balance >= '"+rf+"' AND male = '"+male+"' GROUP BY code ORDER BY Tbalance DESC LIMIT 10;";
        }else if((tc > 0) && (ug > 0) && (ri == 0) && (rf > 0)){
            queryDuplicidad = queryDuplicidad + "  code <> '"+code+"' AND company <> '"+company+"' AND type = '"+tc+"' AND location = '"+ug+"' AND ac.balance >= '"+rf+"' male = '"+male+"' GROUP BY Tbalance ORDER BY Tbalance DESC LIMIT 10;";
        }else if((tc == 0) && (ug > 0) && (ri == 0) && (rf == 0)){
            queryDuplicidad = queryDuplicidad + "  code <> '"+code+"' AND company <> '"+company+"' AND location = '"+ug+"' AND male = '"+male+"' GROUP BY code ORDER BY Tbalance DESC LIMIT 10;";
        }else if((tc > 0) && (ug == 0) && (ri > 0) && (rf == 0)){
            queryDuplicidad = queryDuplicidad + "  code <> '"+code+"' AND company <> '"+company+"' AND type = '"+tc+"' AND ac.balance >= '"+ri+"' AND male = '"+male+"' GROUP BY code ORDER BY Tbalance DESC LIMIT 10;";
        }
        
        rs = conexion.ExeConsulta(queryDuplicidad);
        
        try{
            
            while(rs.next()){
                
                this.Code[posicion] = rs.getString("cl.code");
                this.Male[posicion] = rs.getString("cl.male");
                this.Type[posicion] = rs.getString("cl.type");
                this.Location[posicion] = rs.getString("cl.location");
                this.Company[posicion] = rs.getString("cl.company");
                this.Encrypt[posicion] = rs.getString("cl.encrypt");
                this.Tbalance[posicion] = rs.getString("Tbalance");
                
                String codenuevo = rs.getString("cl.code");
                String companianueva = rs.getString("cl.company");
                
                for (int j = posicion; j < Code.length; j++) { //Revision Duplicidad de Compania
                        
                    if (codenuevo.equals(Code[posicion - 1])) {
                        if (companianueva.equals(Company[posicion - 1])) {
                            this.Code[posicion] = "N";
                            this.Male[posicion] = "N";
                            this.Type[posicion] = "N";
                            this.Location[posicion] = "N";
                            this.Company[posicion] = "N";
                            this.Encrypt[posicion] = "N";
                            this.Tbalance[posicion] = "N";
                        }
                    }
                    
                    if (j != posicion) {
                        if (codenuevo.equals(Code[j])) {
                            if (companianueva.equals(Company[j])) {
                                this.Code[posicion] = "N";
                                this.Male[posicion] = "N";
                                this.Type[posicion] = "N";
                                this.Location[posicion] = "N";
                                this.Company[posicion] = "N";
                                this.Encrypt[posicion] = "N";
                                this.Tbalance[posicion] = "N";
                            }
                        }
                    }

               }
                

            }      
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public void desencriptar(String code,int posicion) throws IOException{ //Metodo para desencriptar Code desde el WS
       String decencriptado = util.consumir(code);
       Code[posicion] = decencriptado;
    }
    
}