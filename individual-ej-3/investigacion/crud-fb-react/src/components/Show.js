import React, { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import {  collection, getDocs, deleteDoc, doc, getDoc } from 'firebase/firestore'
import { db } from '../firebaseConfig/firebase'

import Swal from 'sweetalert2'
import withReactContent from 'sweetalert2-react-content'

const MySwal = withReactContent(Swal)

const Show = () => {

    //configuracion de los hooks
    const [products, setProducts] = useState([])


    //referencia a la base de datos
    const productsCollection = collection(db, "products")


    //funcion para mostrar los datos de la base de datos

    const getProducts = async () => {
        const data = await getDocs(productsCollection)
        setProducts(
            data.docs.map((doc) => ({ ...doc.data(), id: doc.id }))
        )
        console.log(products)
    }


    //funcion para eliminar un registro de la base de datos
    const deleteProduct = async (id) => {
        const productDoc = doc(db, "products", id)
        await deleteDoc(productDoc)
        getProducts()
    }


    //funcion de confirmacion de eliminacion
    const confirmDelete = (id) => {
        MySwal.fire({
            title: 'Are you sure?',
            text: "You won't be able to revert this!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, delete it!'
        }).then((result) => {
            if (result.isConfirmed) {
                deleteProduct(id)
                MySwal.fire(
                    'Deleted!',
                    'Your file has been deleted.',
                    'success'
                )
            }
        })
    }

    //usamos useEffect para que se ejecute la funcion de mostrar los datos 

    useEffect(() => {
        getProducts()
    }, [])

    //retorno de la vista
    return (
        <>
            <div className='container'>
                <div className='row'>
                    <div className='col'>
                        <div className='d-grid gap-2'>
                            <Link to="/create" className='btn btn-secondary mt-2 mb-2'>Create</Link>
                        </div>
                        <table className='table table-dark table-hover'>
                            <thead>
                                <tr>
                                    <th>Description</th>
                                    <th>Stock</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                {products.map((product) => (
                                    <tr key={product.id}>
                                        <td>{product.description}</td>
                                        <td>{product.stock}</td>
                                        <td>
                                            <Link to={`/edit/${product.id}`} className='btn btn-light'>Edit</Link>
                                            <button onClick={() => confirmDelete(product.id)} className='btn btn-danger'>Delete</button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </>
    )
}

export default Show
