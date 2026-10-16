/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.jpaprueba;

import com.mycompany.jpaprueba.logica.Alumno;
import com.mycompany.jpaprueba.logica.Controladora;
import com.mycompany.jpaprueba.persistencia.ControladoraPersistencia;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Usuario
 */
public class JpaPrueba {

    public static void main(String[] args) {
        
        Controladora control = new Controladora();
        /*Alumno a = new Alumno(30, "Pepe", "Debai", new Date());
        control.crearAlumno(a);*/
        //control.eliminarAlumno(30);
        
        Alumno alu = control.traerAlumno(11);
        ArrayList<Alumno> listaAlumnos = control.traerListaAlumnos();
        System.out.println("--------------------------------");
        System.out.println("El alumno es: " + alu.toString());
        System.out.println("--------------------------------");
        for (Alumno a: listaAlumnos) {
            System.out.println("El alumno es: " + a.toString());
        }
    }
}
