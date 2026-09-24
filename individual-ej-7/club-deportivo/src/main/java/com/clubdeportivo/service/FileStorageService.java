package com.clubdeportivo.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

/**
 * ========================================================================================
 * SERVICIO: FileStorageService
 * ========================================================================================
 * Gestiona el almacenamiento físico, sanitización y recuperación de las imágenes faciales.
 *
 * Requerimiento de consigna:
 * "El sistema guarda además de los datos principales una imagen con el rostro de cada persona."
 *
 * Seguridad y buenas prácticas aplicadas:
 * 1. Renombrado aleatorio con UUID para impedir ataques de Path Traversal y sobreescrituras.
 * 2. Validación de extensiones de imagen permitidas (JPG, JPEG, PNG, WEBP).
 * 3. Creación automática del directorio de subidas si no existe.
 * ========================================================================================
 */
@Service
public class FileStorageService {

    private static final Logger log = LoggerFactory.getLogger(FileStorageService.class);

    private final Path rootLocation;

    public FileStorageService(@Value("${app.upload.dir:uploads/rostros/}") String uploadDir) {
        this.rootLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.rootLocation);
            log.info("Directorio de imágenes faciales inicializado en: {}", this.rootLocation);
        } catch (IOException e) {
            log.error("No se pudo crear el directorio de almacenamiento de rostros", e);
            throw new RuntimeException("No se pudo inicializar el almacenamiento de archivos", e);
        }
    }

    /**
     * Almacena una imagen subida por el usuario y retorna el nombre de archivo asignado.
     */
    public String almacenarArchivo(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            return null;
        }

        String nombreOriginal = archivo.getOriginalFilename();
        String extension = "";
        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(nombreOriginal.lastIndexOf(".")).toLowerCase();
        } else {
            extension = ".jpg";
        }

        // Valida que sea un formato de imagen permitido
        if (!extension.equals(".jpg") && !extension.equals(".jpeg") &&
                !extension.equals(".png") && !extension.equals(".webp")) {
            throw new IllegalArgumentException("Formato de imagen no soportado. Formatos válidos: JPG, PNG, WEBP.");
        }

        // Genera un nombre de archivo único para evitar colisiones
        String nombreArchivoFinal = UUID.randomUUID().toString() + extension;

        try {
            Path destino = this.rootLocation.resolve(nombreArchivoFinal).normalize();
            // Previene ataques de escape de directorio (Path Traversal)
            if (!destino.getParent().equals(this.rootLocation)) {
                throw new SecurityException("Intento de almacenamiento fuera del directorio configurado");
            }

            try (InputStream inputStream = archivo.getInputStream()) {
                Files.copy(inputStream, destino, StandardCopyOption.REPLACE_EXISTING);
            }
            log.info("Imagen facial almacenada con éxito: {}", nombreArchivoFinal);
            return nombreArchivoFinal;
        } catch (IOException e) {
            log.error("Error al escribir el archivo de rostro en disco", e);
            throw new RuntimeException("Error al almacenar la imagen facial: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina una imagen existente cuando el socio actualiza su rostro o es dado de baja.
     */
    public void eliminarArchivo(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isBlank() || nombreArchivo.contains("default-avatar")) {
            return;
        }
        try {
            Path archivo = this.rootLocation.resolve(nombreArchivo).normalize();
            Files.deleteIfExists(archivo);
            log.info("Imagen previa eliminada: {}", nombreArchivo);
        } catch (IOException e) {
            log.warn("No se pudo eliminar el archivo físico: {}", nombreArchivo);
        }
    }

    public Path getRootLocation() {
        return rootLocation;
    }
}
