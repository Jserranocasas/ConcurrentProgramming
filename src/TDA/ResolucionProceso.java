package TDA;

import java.util.concurrent.Semaphore;

/**
 * @brief   Clase que encapsula las resoluciones individuales de cada proceso
 * @file    ResolucionProceso.java
 * @author  Javier Serrano Casas
 * @date    21-03-2018
 */
public class ResolucionProceso {
    private int respuestaPeticion; 
    private final Semaphore semaforoProceso;
    
    /*============ Constructor de la clase ResolucionProceso ===============*/
    public ResolucionProceso(){
        respuestaPeticion = 0;
        semaforoProceso = new Semaphore(0);
    }
    
    /*==================== Métodos públicos =========================*/
    /**
     * @brief Método para obtener respuestaPeticion
     * @return 
     */
    public int getRespuestaPeticion(){
        return respuestaPeticion;
    }
    
    /**
     * @brief Método para modificar respuestaPeticion
     * @param numMarcos
     */
    public void setRespuestaPeticion(int numMarcos){
        respuestaPeticion = numMarcos;
    }
    
    /**
     * @brief Método para obtener el semáforo
     * @return 
     */
    public Semaphore getSemaforoProceso(){
        return semaforoProceso;
    }
}
