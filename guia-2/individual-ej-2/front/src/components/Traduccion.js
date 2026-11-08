export const traducirDescripcion = (descripcionEnIngles) => {
    // Mapeo de descripciones en inglés a sus equivalentes en español
    const traducciones = {
      'clear sky': 'cielo despejado',
      'few clouds': 'pocas nubes',
      'scattered clouds': 'nubes dispersas',
      'broken clouds': 'nubes rotas',
      'overcast clouds': 'nublado',
      'shower rain': 'lluvia',
      'rain': 'lluvia',
      'thunderstorm': 'tormenta eléctrica',
      'snow': 'nieve',
      'mist': 'niebla',
      'smoke': 'humo',
      'haze': 'neblina',
      'dust': 'polvo',
      'fog': 'niebla',
      'sand': 'arena',
      'ash': 'ceniza',
      'squall': 'ráfaga',
      'tornado': 'tornado'
    };
  
    // Retorna la traducción si está disponible, de lo contrario retorna la descripción original
    return traducciones[descripcionEnIngles] || descripcionEnIngles;
  };