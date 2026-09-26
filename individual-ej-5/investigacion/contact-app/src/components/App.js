import './App.css';
import Header from './Header';
import AddContact from './AddContact';
import ContactList from './ContactList';
import React, { useState, useEffect } from 'react';
import { v4 as uuid } from 'uuid';

function App() {

  
  const addContactHandler = (contact) => {
    console.log(contact);
    setContacts([...contacts, { ...contact, id: uuid() }]);
  };
  
  const LOCAL_STORAGE_KEY = "contacts";
  const [contacts, setContacts] = useState(() => {
    const savedContacts = localStorage.getItem(LOCAL_STORAGE_KEY);
    return savedContacts ? JSON.parse(savedContacts) : [];
  });

  useEffect(() => {
    localStorage.setItem(LOCAL_STORAGE_KEY, JSON.stringify(contacts));
  }, [contacts]);
 

  const removeContactHandler = (id) => {
    const newContactList = contacts.filter((contact) => {
      return contact.id !== id;
    });
    setContacts(newContactList);
  };


  return (
    <main className="ui container">
      <Header/>
      <AddContact addContactHandler={addContactHandler}/>
      <ContactList contacts={contacts} getContactId={removeContactHandler}/>
    </main>
  );
}

export default App;
