import React, { useState, useEffect } from 'react';

function CronometroSimple() {
  const [segundos, setSegundos] = useState(0);
  const [activo, setActivo] = useState(false);

  // Hook useEffect: Gestiona el efecto secundario del temporizador
  useEffect(() => {
    let intervalo = null;

    if (activo) {
      intervalo = setInterval(() => {
        setSegundos((segundosPrevios) => segundosPrevios + 1);
      }, 1000);
    } else {
      clearInterval(intervalo);
    }

    // Función de limpieza: Se ejecuta cuando el componente se desmonta o cambia 'activo'
    return () => clearInterval(intervalo);
  }, [activo]); // Array de dependencias: se reejecuta solo si 'activo' cambia

  return (
    <div className="cronometro-contenedor">
      <h2 className="text-2xl font-normal text-gray-600">Temporizador</h2>
      <p><strong className="text-gray-600 text-4xl">{segundos} s</strong></p>
      <div className="flex gap-4 mt-4">
        <button  className="bg-blue-200 hover:bg-blue-300 text-blue-800 font-semibold border-2 border-blue-800 py-2 px-4 rounded-lg" onClick={() => setActivo(!activo)}>
            {activo ? 'Pausar' : 'Iniciar'}
        </button>
        <button className="bg-red-200 hover:bg-red-300 text-red-800 font-semibold border-2 border-red-800 py-2 px-4 rounded-lg" onClick={() => { setSegundos(0); setActivo(false); }}>
            Restablecer
        </button>
      </div>
    </div>
  );
}

export default CronometroSimple;