import React, { useState } from 'react';
import user from '../images/user.jpg';

const ContactCard = (props) => {
    const { id, name, email } = props.contact;
    return (
        <div className="item" key={id}>
            <img className="ui avatar image" src={user} alt="user"/>
            <div className="content">
                <div className="header">{name}</div>
                <div className="description">{email}</div>
            </div>
            <i className="trash alternate outline icon content-end " onClick={() => props.onDelete(id)}></i>
        </div>
    );
}

export default ContactCard;