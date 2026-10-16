/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.jpaprueba.logica;

import com.mycompany.jpaprueba.persistencia.ControladoraPersistencia;
import java.util.ArrayList;

/**
 *
 * @author Usuario
 */
public class Controladora {
    
    ControladoraPersistencia controlPersis = new ControladoraPersistencia();
    
    public void crearAlumno(Alumno a) {
        controlPersis.crearAlumno(a);
    }
    
    public void eliminarAlumno(int id) {
        controlPersis.eliminarAlumno(id);
    }
    
    public void editarAlumno(Alumno a) {
        controlPersis.editarAlumno(a);
    }
 
    public Alumno traerAlumno(int id) {
        
        return controlPersis.traerAlumno(id);
    }
    
    public ArrayList<Alumno> traerListaAlumnos() {
        return controlPersis.traerListaAlumnos();
    }
    
}
