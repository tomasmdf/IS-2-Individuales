import axios  from "axios";

export const ObtenerClima = async (ciudad, setClima) => {
  try {
    const respuesta = await axios.get(`http://localhost:8080/ciudad/clima/${ciudad}`);
    console.log("respuesta", respuesta.data);
    setClima(JSON.stringify(respuesta.data));

  } catch (error) {
    console.error("Error al obtener el clima de nuestro servidor", error);
  }
};
