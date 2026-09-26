import React, { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom';
import { getDocs, doc, updateDoc } from 'firebase/firestore';
import { db } from '../firebaseConfig/firebase';

const Edit = () => {

    const [descripcion, setDescripcion] = React.useState('');
    const [stock, setStock] = React.useState(0);
    const navigate = useNavigate() 
    const { id } = useParams();

    const update = async (e) => {
        e.preventDefault();
        const productDoc = doc(db, "products", id);
        await updateDoc(productDoc, {description: descripcion, stock: stock});
        navigate("/");
    }

    const getProductById = async (id) => {
        const productDoc = doc(db, "products", id);
        const product = await getDocs(productDoc);
        if (product.exists()) {
            setDescripcion(product.data().descripcion);
            setStock(product.data().stock);
        } else {
            console.log("No such document!");
        }
    }

    useEffect(() => {
        getProductById(id);
    }, [id]);

    return (
        <div className='container'>
            <div className='row'>
                <div className='col'>
                    <h1>Editar Producto</h1>
                    <form onSubmit={update}>
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
                        <button type='submit' className='btn btn-primary'>Update</button>
                    </form>
                </div>
            </div>
        </div>
    )
}

export default Edit
