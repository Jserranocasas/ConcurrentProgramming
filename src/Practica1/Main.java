package Practica1;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Semaphore;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import TDA.ResolucionProceso;
import TDA.Peticion;
import TDA.Marcos;
import TDA.Lista;

/**
 * @brief   Programa Principal
 * @file    Main.java
 * @author  Javier Serrano Casas
 * @date    18-03-2018
 */
public class Main {

    /**
     * @param args the command line arguments
     * @throws java.lang.InterruptedException
     */
    public static void main(String[] args) throws InterruptedException {
        
        System.out.println("El Hilo Principal ha iniciado su ejecución)");
        
        // Creamos los semáforos que compartiran los procesos
        Semaphore exmMemoria = new Semaphore(1);   
        Semaphore exmPeticiones = new Semaphore(1); 
        Semaphore maxPeticiones = new Semaphore(80); //Inicializado al tamaño de bufferPeticiones
        Semaphore nuevaPeticion = new Semaphore(0); 
        
        // Creamos el marco de ejecución para los procesos
        ExecutorService ejecucion = (ExecutorService) Executors.newFixedThreadPool(8);
        
        // Creamos las variables compartidas
        ArrayList<ResolucionProceso> resoluciones = new ArrayList<>(); 
        Lista<Peticion> bufferPeticiones = new Lista<>(80);  
        Marcos marcos = new Marcos(); //Inicializo variable compartida de marcos Fisicos
        
        List<Future<?>> listaProcesos = new ArrayList();
        GestorMemoria gestor = new GestorMemoria(resoluciones,bufferPeticiones,
                        exmPeticiones,nuevaPeticion,maxPeticiones,exmMemoria );
        
        // Envio el proceso gestor de memoria al marco de ejecución 
        listaProcesos.add(ejecucion.submit(gestor));

        // Creamos una lista para almacenar las tareas que añadiremos a la ejecución
        CrearProceso creaProceso = new CrearProceso(resoluciones, bufferPeticiones,
                                            ejecucion, maxPeticiones, nuevaPeticion, 
                                            listaProcesos,exmPeticiones, exmMemoria);
                        
        // Envio el proceso que me creará los procesos, al marco de ejecución 
        listaProcesos.add(ejecucion.submit(creaProceso));
        
        // La tarea de finalización se ejecutará pasados 4 minutos
        CountDownLatch esperaFinalizacion = new CountDownLatch(1);
        TimeUnit.MINUTES.sleep(4);   
        
        listaProcesos.forEach((tarea) -> { 
            tarea.cancel(true);
        });
        
        // El programa principal puede presentar los resultados de las tareas
        esperaFinalizacion.countDown();
        
        // Esperamos a la finalización de la tarea finalización
        System.out.println("El Hilo Principal espera a la finalización");
        esperaFinalizacion.await();
        System.out.println("El Hilo Principal inicia la finalización");

        // Finalizamos la ejecución de los procesos esperando a que terminen los procesos
        ejecucion.shutdown();
        ejecucion.awaitTermination(1, TimeUnit.DAYS);
        System.out.println("El Hilo Principal ha finalizado su ejecución)");
    }
    
}
