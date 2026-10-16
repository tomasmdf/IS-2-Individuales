document.getElementById('myForm').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent the default form submission behavior

    const userName = document.getElementById('nombre').value;
    const userEmail = document.getElementById('email').value;

    if (userName.length < 6) {
        console.log('Username must be at least 8 characters long.');
        return;
    }

    if (!userEmail.includes('@')) {
        console.log('Please enter a valid email address.');
        return;
    }



    if (userName && userEmail) {
        console.log('Form submitted successfully!');
        // You can add code here to actually submit the form data to a server if needed
        console.log(`Username: ${userName}, Email: ${userEmail}`);
        return;
    }

    
});