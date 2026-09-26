import React from 'react'
import { useNavigate, useParams } from 'react-router-dom';
import { collection, addDoc } from 'firebase/firestore';
import { db } from '../firebaseConfig/firebase';    


const Create = () => {
    const [descripcion, setDescripcion] = React.useState('');
    const [stock, setStock] = React.useState(0);
    const navigate = useNavigate() 

    const productsCollection = collection(db, "products");

    const store = async (e) => {
        e.preventDefault();
        await addDoc(productsCollection, {description: descripcion, stock: stock});
        navigate("/");
    }


    return (
        <div className='container'>
            <div className='row'>
                <div className='col'>
                    <h1>Crear Producto</h1>
                    <form onSubmit={store}>
                        <div className='mb-3'>
                            <label className='form-label'>Descripcion</label>
                            <input
                                value={descripcion}
                                onChange={(e) => setDescripcion(e.target.value)}
                                type="text"
                                className='form-control'
                            />
                        </div>
                        <div className='mb-3'>
                            <label className='form-label'>Stock</label>
                            <input
                                value={stock}
                                onChange={(e) => setStock(e.target.value)}
                                type="number"
                                className='form-control'
                            />
                        </div>
                        <button type='submit' className='btn btn-primary'>Guardar</button>
                    </form>
                </div>
            </div>
        </div>
    )
}

export default Create
