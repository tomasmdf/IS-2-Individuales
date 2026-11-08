import React from "react";
import { traducirDescripcion } from "./Traduccion";

export const DisplayClima = ({ clima }) => {
  try {
    //verificar si esta vacio el clima
    if (!clima) {
      return <div>No hay información del clima</div>;
    }

    const climaData = JSON.parse(clima);

    //convirtiendo temperatura a grados celsius
    const temperatuACelsius = climaData.main.temp - 273.15;

    return (
      <>
        <div className="clima">
          <table className="table table-striped table-bordered">
            <thead className="thead-dark">
              <tr>
                <th>Ciudad</th>
                <th>Temperatura (°C)</th>
                <th>Descripción</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>{climaData.name}</td>
                <td>{temperatuACelsius.toFixed(2)}</td>
                <td>{traducirDescripcion(climaData.weather[0].description)}</td>
              </tr>
            </tbody>
          </table>
        </div>
      </>
    );
  } catch (error) {
    console.log("Error en la carga de datos: ", error);
    return <div>Error al cargar los datos del clima</div>;
  }
};
