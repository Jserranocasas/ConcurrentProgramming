package Practica1;

import TDA.Lista;
import TDA.Peticion;
import TDA.ResolucionProceso;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @brief   Clase que encapsula la tarea de crear procesos
 * @file    CreaProceso.java
 * @author  Javier Serrano Casas
 * @date    21-03-2018
 */
public class CrearProceso implements Runnable {
    //Variables Compartidas
    private final ArrayList<ResolucionProceso> resoluciones; //Permite almacenar los marcos fisicos asignados
    private final Lista<Peticion> bufferPeticiones;  //Almacena las peticiones que insertaremos
    private final ExecutorService ejecucion; //Marco de ejecución
    List<Future<?>> listaProcesos;
    
    //Semáforos
    private final Semaphore exmMemoria;    //Semáforo para la exclusión mutua de la memoria
    private final Semaphore exmPeticiones; //Semáforo para la exclusión mutua de las peticiones
    private final Semaphore maxPeticiones; //Semáforo para que no se inserten peticiones estando lleno
    private final Semaphore nuevaPeticion; //Semáforo para que no se extraiga peticiones estando vacio
    
    /*================= Constructor de la clase CrearProceso ====================*/
    public CrearProceso(ArrayList<ResolucionProceso> resolucionProceso, Lista buffer,
            ExecutorService ejecucion, Semaphore maxPeticiones, Semaphore nuevaPeticion,
            List<Future<?>> listaProcesos, Semaphore exmPeticiones, Semaphore exmMemoria){
        this.ejecucion = ejecucion;
        this.bufferPeticiones = buffer;
        this.resoluciones = resolucionProceso;
        this.listaProcesos = listaProcesos;
        this.maxPeticiones = maxPeticiones;
        this.nuevaPeticion = nuevaPeticion;
        this.exmPeticiones = exmPeticiones;
        this.exmMemoria = exmMemoria;
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void run() {
        int i=0;
        while (true){
            try {
                if(Thread.interrupted())
                    throw new InterruptedException("Procesos interrumpidos");
                
                ResolucionProceso resolucion = new ResolucionProceso();
                resoluciones.add(i, resolucion);
                Proceso proceso = new Proceso(i, resolucion, bufferPeticiones,
                        nuevaPeticion, exmPeticiones,  exmMemoria, maxPeticiones);

                listaProcesos.add(ejecucion.submit(proceso));
                System.err.println("Proceso-" + i + " creado.   " + new Date());
                
                int tiempo = (int) (Math.random() * 3) + 1; 
                TimeUnit.SECONDS.sleep(tiempo);    //Espera entre 1 y 3 segundos            
            } catch (InterruptedException ex) {
                System.err.println("Crear procesos se ha interrumpido.   " + new Date());
            }
            i++;
        }
    }
    
    
}
