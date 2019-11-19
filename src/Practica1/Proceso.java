package Practica1;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;
import TDA.Peticion.EstadoEjecucion;
import TDA.ResolucionProceso;
import TDA.Peticion;
import TDA.Marcos;
import TDA.Lista;

/**
 * @brief   Clase que representa un proceso
 * @file    Proceso.java
 * @author  Javier Serrano Casas
 * @date    18-03-2018
 */
public class Proceso implements Runnable{
    //Variables Compartidas
    private final ResolucionProceso resolucion; //Permite almacenar los marcos fisicos asignados
    private final Lista<Peticion> bufferPeticiones; //Almacena las peticiones que insertaremos
    
    //Semáforos
    private final Semaphore exmMemoria;     //Semáforo para la exclusión mutua de la memoria
    private final Semaphore exmPeticiones;  //Semáforo para la exclusión mutua de las peticiones
    private final Semaphore maxPeticiones;  //Semáforo para que no se inserten peticiones estando lleno
    private final Semaphore nuevaPeticion;  //Semáforo para que no se extraiga peticiones estando vacío
    
    //Variables locales
    private final Lista<Integer> marcosEnMemoria; //Marcos cargados del proceso
    private int totalMarcosLogicos; //Número total de marcos lógicos del proceso
    private int fallosPagina;       //Número de fallos de página
    private int marcoLogico;        //Marco Lógico necesario para ciclo
    private final int id;           //Identificador del proceso
    
    
    /*============= Constructor de la clase Proceso =================*/
    public Proceso(int id, ResolucionProceso resolucion,Lista buffer,  
                    Semaphore nuevaPeticion, Semaphore exmPeticiones, 
                    Semaphore exmMemoria, Semaphore maxPeticiones){
        this.id = id;
        marcosEnMemoria = new Lista();
        this.bufferPeticiones = buffer;
        this.resolucion = resolucion;
        this.maxPeticiones = maxPeticiones;
        this.nuevaPeticion = nuevaPeticion;
        this.exmPeticiones = exmPeticiones;
        this.exmMemoria = exmMemoria;    
    }
    
    /*==================== Métodos públicos =========================*/
    /**
     * @brief Hilo de ejecución del proceso
     */
    @Override
    public void run(){
        try {
            inicio();
            ejecucion();
            finalizacion();
        } catch (InterruptedException ex) {
            System.err.println("Proceso-" + id + " cancelado.   " + new Date());
        }
    }
    
    /*===============================================================*/
    
    /*==================== Métodos privados =========================*/
    /**
     * @brief Método que realiza el inicio de un proceso, realizando una solicitud
     *        de carga, esperando su resolución y actualiza los marcos cargados
     */
    private void inicio() throws InterruptedException{
        int numMarcos;
        fallosPagina = 0;
        totalMarcosLogicos = generarMarcos();
        
         //Solicitud de carga inicial
        peticionGestor(EstadoEjecucion.INICIO);            
        //Espera resolución
        numMarcos = esperaResolucion();          
        //Actualiza marcos cargados
        actualizaMarcos(numMarcos, EstadoEjecucion.INICIO); 
    }
    
    /**
     * @brief Método que realiza la ejecucion de un proceso, cargando los marcos
     *        adicionales necesarios y simulando la ejecución
     */
    private void ejecucion() throws InterruptedException{
        int numEjecuciones;
        int numMarcos;
        int ejecucion = 1;
        
        numEjecuciones = generaEjecuciones(); //Total de ciclos de ejecución
        while (ejecucion <= numEjecuciones){
            marcoLogico = generaMarcoLogico(); //Marco que se ejecuta
            System.out.println("El proceso-" + id + " ha ejecutado un ciclo");
            if(!marcosEnMemoria.find(marcoLogico)){ //Si el marco no esta cargado
                fallosPagina++;               
                //Solicitud carga de marco
                peticionGestor(EstadoEjecucion.EJECUCION);
                //Esperar resolución
                numMarcos = esperaResolucion();
                //Actualiza los marcos cargados
                actualizaMarcos(numMarcos, EstadoEjecucion.EJECUCION);
            }
            int tiempo = (int) (Math.random() * 3) + 3; 
            TimeUnit.SECONDS.sleep(tiempo);   //Espera entre 3 y 5 segundos
            
            ejecucion++; //Siguiente ejecución
        }
    }
    
    /**
     * @brief Método que realiza la finalización de un proceso, liberando los 
     *        marcos asignados necesarios e indicando al gestor que ha finalizado
     */
    private void finalizacion() throws InterruptedException{
        try{
            exmMemoria.acquire();
            Marcos.sumaMarcosFisicos(marcosEnMemoria.size());
        } catch (InterruptedException e) {
            throw new InterruptedException("Dep finalizacion");
        } finally {
            exmMemoria.release();			
        }

        //Muestra el número de fallos de página que ha tenido el proceso
        System.out.println("El proceso-" + id + ": Ha terminado con " + 
                      fallosPagina + " fallos de página.    " + new Date());
    }

    /**
     * @brief Comunica al gestor una petición insertandola en el buffer compartido
     * @param estado Tipo de peticion que se solicita
     * @post  Este método se debe sincronizar para la comunicación con el gestor
     */
    private void peticionGestor(EstadoEjecucion estado) throws InterruptedException{
        //Realiza una petición al Gestor de Memoria
        try{
            maxPeticiones.acquire();
            exmPeticiones.acquire();
            if(estado == EstadoEjecucion.INICIO){
                System.out.println("Petición de INICIO del proceso-" + id );
            } else {
                System.out.println("Petición de EJECUCIÓN del proceso-" + id);
            }
            bufferPeticiones.add(new Peticion(id, estado));
        } catch (InterruptedException e) {
            throw new InterruptedException("Dep peticionGestor");
        } finally {
            exmPeticiones.release();
            nuevaPeticion.release();			
        }
    }
    
    /**
     * @brief Espera a que el gestor resuelva 
     * @return Devuelve el número de marcos que el gestor le asigna
     */
    private int esperaResolucion() throws InterruptedException{
        resolucion.getSemaforoProceso().acquire(); //Espera a que el Gestor resuelva
        
        return resolucion.getRespuestaPeticion(); //Marcos que se cargan en memoria
    }
    
    /**
     * @brief Actualizo los marcos que tiene el proceso asignado
     * @param numMarcos int Número de marcos que el gestor le asigna
     * @param tipo Tipo de petición que se solicitó
     */
    private void actualizaMarcos(int numMarcos, EstadoEjecucion estado){
        if(estado == EstadoEjecucion.INICIO){
            //Al inicio están cargados los marcos lógicos 1 y 2
            marcosEnMemoria.add(1);
            marcosEnMemoria.add(2);
        } else {
            if( numMarcos == 0){ //Si el número de marcos asignados es 0
                //Se reemplaza por el marco más antiguo
                marcosEnMemoria.remove();
            }
            marcosEnMemoria.add(marcoLogico);
        }
    }
    
    /**
     * @brief Genera un entero aleatorio entre 4 y 8
     * @return Devuelve el entero aleatorio generado
     */
    private int generarMarcos(){
        return (int) (Math.random() * 5) + 4;
    }
    
    /**
     * @brief Genera un entero aleatorio entre 8 y 12
     * @return Devuelve el entero aleatorio generado
     */
    private int generaEjecuciones(){
        return (int) (Math.random() * 5) + 8;
    }
    
    /**
     * @brief Genera un entero aleatorio entre 1 y el total de marcos lógicos
     * @return Devuelve el entero aleatorio generado
     */
    private int generaMarcoLogico(){
        return (int) (Math.random() * totalMarcosLogicos) + 1;
    }
    
    /*===============================================================*/
}
