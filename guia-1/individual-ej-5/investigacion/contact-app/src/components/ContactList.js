import React, { useState } from 'react';
import ContactCard from './ContactCard';

const ContactList = (props) => {

    const { contacts } = props;
    const deleteContactHandler = (id) => {
        props.getContactId(id);
    }

    const renderContactList = contacts.map((contact) => {
        return (
            <ContactCard key={contact.id} contact={contact} onDelete={deleteContactHandler}/>
        );
    });


    return (
        <div className="ui celled list">
            <h3>Contact List</h3>
            {renderContactList}
        </div>
    );

}
export default ContactList;