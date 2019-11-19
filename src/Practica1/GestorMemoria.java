package Practica1;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import TDA.Peticion.EstadoEjecucion;
import TDA.ResolucionProceso;
import TDA.Peticion;
import TDA.Marcos;
import TDA.Lista;

/**
 * @brief   Clase que representa al gestor de memoria
 * @file    GestorMemoria.java
 * @author  Javier Serrano Casas
 * @date    18-03-2018
 */
public class GestorMemoria implements Runnable {
    //Constantes
    private static final int MINIMO_MARCOS = 2;
    private static final int MAXIMO_MARCOS = 4;

    //Variables Compartidas
    private final ArrayList<ResolucionProceso> resoluciones; //Permite almacenar los marcos fisicos asignados
    private final Lista<Peticion> bufferPeticiones;  //Almacena las peticiones que insertaremos
    
    //Semáforos
    private final Semaphore exmMemoria;    //Semáforo para la exclusión mutua de la memoria
    private final Semaphore exmPeticiones; //Semáforo para la exclusión mutua de las peticiones
    private final Semaphore maxPeticiones; //Semáforo para que no se inserten peticiones estando lleno
    private final Semaphore nuevaPeticion; //Semáforo para que no se extraiga peticiones estando vacio
    
    //Variables locales
    private final ArrayList<Integer> marcosProceso; //Array de enteros
    private int fallosMemoria;  //Fallos en asignación de marco físico
    
    /*============ Constructor de la clase Proceso ===============*/
    public GestorMemoria(ArrayList<ResolucionProceso> resoluciones, Lista buffer, 
                                Semaphore exmPeticiones, Semaphore nuevaPeticion, 
                                Semaphore maxPeticiones, Semaphore exmMemoria){
        fallosMemoria = 0;
        marcosProceso = new ArrayList<>();
        this.bufferPeticiones = buffer;
        this.resoluciones = resoluciones;
        this.maxPeticiones = maxPeticiones;
        this.nuevaPeticion = nuevaPeticion;
        this.exmPeticiones = exmPeticiones;
        this.exmMemoria = exmMemoria;
    }
    
    /*==================== Métodos públicos =========================*/
    /**
     * @brief Hilo de ejecución del proceso gestor de memoria
     */
    @Override
    public void run() { 
        Peticion peticion;
        
        System.err.println("El Gestor de Memoria ha comenzado su ejecución: ");
        
        while(true){
            try {
                if(Thread.interrupted())
                    throw new InterruptedException("Gestor de memoria interrumpido");
                peticion = obtenerPeticion();
                resolverPeticion(peticion);
            } catch (InterruptedException ex) {
                System.err.println("El Gestor de Memoria ha tenido: " + 
                                   fallosMemoria + " fallos de memoria");
            }
        }
        
    }
    
    /*===============================================================*/
    
    /*==================== Métodos privados =========================*/
    /**
     * @brief  Obtiene una petición de un proceso
     * @return Devuelve la peticion obtenida del buffer de peticiones
     * @post  Este método se debe sincronizar para la comunicación con los procesos
     */
    private Peticion obtenerPeticion() throws InterruptedException {
        Peticion peticion;
        
        try{
            nuevaPeticion.acquire();
            exmPeticiones.acquire();
            peticion = bufferPeticiones.remove();
        } catch (InterruptedException e) {
            throw new InterruptedException("Dep obtenerPeticion");
        } finally {
            exmPeticiones.release();
            maxPeticiones.release();		
        }
        
        return peticion;
    }

    /**
     * @brief Resuelve la decisión de una petición pasada por parámetro
     * @param peticion Petición que resolver
     */
    private void resolverPeticion(Peticion peticion) throws InterruptedException {
        int numMarcos;
        
        if(peticion.getTipo() == EstadoEjecucion.INICIO){
            numMarcos = obtenerMarcosFisicos(peticion);
            if(numMarcos != MINIMO_MARCOS){
                posponer(peticion);
            } else {
                asignarMarcos(peticion, numMarcos);
            }
        } else {
            numMarcos = obtenerMarcosFisicos(peticion);
            asignarMarcos(peticion, numMarcos);
        }
    }

    /**
     * @brief  Método encargado de gestionar los marcos físicos que se asignarán
     * @param  peticion Peticion del proceso que pretende obtener marcos físicos
     * @return Devuelve el numero de marcos que obtiene el proceso de la petición
     * @throws InterruptedException 
     */
    private int obtenerMarcosFisicos(Peticion peticion) throws InterruptedException {
        int numMarcos;
        int marcosAsignados;
        
        if( peticion.getTipo() == EstadoEjecucion.INICIO){
            try{    
                exmMemoria.acquire();
                if(Marcos.getMarcosFisicos() >= 2){
                    numMarcos = 2;
                    Marcos.restaMarcosFisicos(2);
                    marcosProceso.add(peticion.getID(), 2);
                } else {
                    numMarcos = 0;
                    fallosMemoria++;
                }
            } catch (InterruptedException e) {
                throw new InterruptedException("Dep obtenerMarcosFisicos");
            } finally {
                exmMemoria.release();			
            }
        } else {
            marcosAsignados = marcosProceso.get(peticion.getID());
            try{
                exmMemoria.acquire();
                if(Marcos.getMarcosFisicos() > 0 && marcosAsignados < MAXIMO_MARCOS){
                    Marcos.restaMarcosFisicos(1);
                    numMarcos = 1;
                    marcosProceso.set(peticion.getID(), marcosAsignados + 1);
                } else {
                    numMarcos = 0;
                    fallosMemoria++;
                }
            } catch (InterruptedException e) {
                throw new InterruptedException("Dep obtenerMarcosFisicos");
            } finally {
                exmMemoria.release();			
            }
        }
        return numMarcos;
    }

    /**
     * @brief Método que pospone una petición incluyendola de nuevo en el buffer
     * @param peticion Peticion a posponer
     * @throws InterruptedException 
     */
    private void posponer(Peticion peticion) throws InterruptedException {
        try{
            exmPeticiones.acquire();
            bufferPeticiones.add(peticion);
        } catch (InterruptedException e) {
            throw new InterruptedException("Dep posponer");
        } finally {
            exmPeticiones.release();			
            nuevaPeticion.release();
        }
    }

    /**
     * @brief Asigna marcos al proceso que realiza la petición
     * @param peticion Peticion de marcos
     * @param numMarcos int Número de marcos que asignarle al proceso
     * @throws InterruptedException 
     */
    private void asignarMarcos(Peticion peticion, int numMarcos) throws InterruptedException {
        int idProceso = peticion.getID();
        resoluciones.get(idProceso).setRespuestaPeticion(numMarcos);
        resoluciones.get(idProceso).getSemaforoProceso().release();
        System.err.println("El Gestor de Memoria asigna: " + numMarcos + 
                           " marcos al proceso-" + peticion.getID());
    }
    
    /*===============================================================*/
}
