import { useState } from 'react'
import heroImg from './assets/hero.png'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'
import './App.css'
import CronometroSimple from './components/CronometroSimple'

function App() {
  const [count, setCount] = useState(0)

  return (
    <main className="App h-screen w-full flex flex-col items-center justify-center p-24 text-center">
      <h1 className="text-4xl font-bold text-gray-500 spacing-y-0">Ejemplo uso de Hooks</h1>
      <p className="text-lg text-gray-400 mb-4">Este es un ejemplo de cómo usar los hooks en React</p>
      <CronometroSimple/>
      <div className="mt-8 text-left">
        <h3 className="text-lg font-bold text-gray-600">Explicación:</h3>
        <li className="text-gray-600 list-none">Se combinan dos hooks fundamentales: useState para el estado de los datos (segundos y activo) y useEffect para la tarea asíncrona del temporizador.</li>
        <li className="text-gray-600 list-none">useEffect inicia un temporizador setInterval únicamente cuando activo es verdadero.</li>
        <li className="text-gray-600 list-none">El arreglo de dependencias [activo] asegura que el efecto solo se configure nuevamente cuando el usuario presione el botón de Iniciar/Pausar.</li>
        <li className="text-gray-600 list-none">La función de retorno dentro de useEffect garantiza la limpieza del intervalo de memoria al desmontar el componente o al cambiar el estado, previniendo fugas de memoria (memory leaks).</li>
      </div>
    </main>
  )
}

export default App
