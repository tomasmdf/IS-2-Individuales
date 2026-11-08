import React from 'react'

export const BottonConsulta = ({obtenerClima}) => {
  return (
   <>
    <button className="btn btn-primary" onClick={obtenerClima}>Consultar</button>
   </>
  )
}
