import './index.css'
import { TwitterFollowCard } from './TwitterFollowCard.jsx';




export function App() {

    const mockUsers = [
        {
            userName: 'midudev',
            name: 'Miguel Ángel Durán',
            isFollowing: true
        },
        {
            userName: 'elonmusk',
            name: 'Elon Musk',
            isFollowing: false
        },
        {
            userName: 'react',
            name: 'React',
            isFollowing: true
        },
        {
            userName: 'trump',
            name: 'Donald Trump',
            isFollowing: true
        }
    ];

    return (
        <section className='main-section flex flex-col gap-y-4 justify-center items-center'>
        
            {mockUsers.map(user => 
                <TwitterFollowCard userName={user.userName} initialIsFollowing={user.isFollowing}> 
                    {user.name}
                </TwitterFollowCard>
            )}

        </section>
        
    )
}