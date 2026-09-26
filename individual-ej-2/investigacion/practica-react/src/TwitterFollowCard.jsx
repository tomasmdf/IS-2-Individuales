import './App.css'
import './index.css'
import { useState } from 'react' // importo los estados

export function TwitterFollowCard({ userName = 'unknown', children, initialIsFollowing}) {

    const [isFollowing, setIsFollowing] = useState(initialIsFollowing)

    const text = isFollowing ? 'Siguiendo' : 'Seguir' // if  isFollowing then 'Siguiendo' else 'Seguir'







    const buttonClassName = isFollowing ? ' bg-blue-500/40 text-white' : 'bg-gray-200 text-gray-800' // si esta siguiendo cambia la clase del button

    const handleClick = () => {
        setIsFollowing(!isFollowing) // funcion que cambia el estado del button
    }

    return (
        <article className='flex flex-row gap-x-5 items-center justify-between w-90'>
            <header className='flex flex-row gap-x-3'>
                <img
                className='h-12 w-12 rounded-full'
                alt='El avatar de midudev'
                src={`https://unavatar.io/github/${userName}`}
                />
                <div className='flex flex-col'>
                    <strong>{children}</strong>
                    <span className='tw-followCard-infoUserName'>@{userName}</span>
                </div>
            </header>

            <aside>
                <button className={`${buttonClassName} cursor-pointer px-3 py-1.5 rounded-full` } onClick={handleClick}> 
                {/* Un comentario */}
                    <span className='tw-followCard-text'>{text}</span>  
                </button>
            </aside>
        </article>
    )
}