// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getFirestore } from "firebase/firestore";


const firebaseConfig = {
  apiKey: "AIzaSyAfTLav7OnyYsdDRTnS8sO1AweCDn8Ro-A",
  authDomain: "crud-firebase-react-9a610.firebaseapp.com",
  projectId: "crud-firebase-react-9a610",
  storageBucket: "crud-firebase-react-9a610.firebasestorage.app",
  messagingSenderId: "769882654150",
  appId: "1:769882654150:web:7b66bdecb53041c6c71ec7"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
export const db = getFirestore(app);