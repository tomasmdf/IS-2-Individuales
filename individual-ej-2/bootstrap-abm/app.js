console.log('Hello World!');

var form = document.getElementById('userForm'),
    imgInput = document.getElementById('picture'),
    imgPreview = document.querySelector('.img-fluid'),
    nameInput = document.getElementById('name'),
    ageInput = document.getElementById('age'),
    cityInput = document.getElementById('city'),
    emailInput = document.getElementById('email'),
    phoneInput = document.getElementById('phone'),
    postInput = document.getElementById('post'),
    dateInput = document.getElementById('startDate'),
    submitButton = document.getElementById('submitButton'),
    userInfo = document.getElementById('data'),
    modalTitle = document.getElementsByClassName('modal-title')[0];

let getData = localStorage.getItem('userProfile') ? JSON.parse(localStorage.getItem('userProfile')) : [];

let isEdit = false, editId
showData();

imgInput.onChange = function() {
    if (imgInput.files[0].size < 1000000) {
        
        var fileReader = new FileReader();

        fileReader.onload = function(e) {
            imgUrl = e.target.result;
            imgInput.src = imgUrl;

        };

        fileReader.readAsDataURL(imgInput.files[0]);
    } else {
        alert('File size exceeds 1MB');
    }
}


function readData(picture, name, age, city, email, phone, post, startDate) {    
    document.querySelector('.showImg').src = picture;
    document.getElementById('showName').value = name;
    document.getElementById('showAge').value = age;
    document.getElementById('showCity').value = city;
    document.getElementById('showEmail').value = email;
    document.getElementById('showPhone').value = phone;
    document.getElementById('showPost').value = post;
    document.getElementById('showStartDate').value = startDate;
}

function editInfo(index, picture, name, age, city, email, phone, post, startDate) {
    isEdit = true;
    editId = index;
    imgInput.src = picture; 
    nameInput.value = name;
    ageInput.value = age;
    cityInput.value = city;
    emailInput.value = email;
    phoneInput.value = phone;
    postInput.value = post;
    dateInput.value = startDate;

    submitButton.innerText = 'Update';
    modalTitle.innerText = 'Edit User';

}

function showData() {
    document.querySelectorAll('.employeeDetails').forEach((element) => {
        element.remove();
    });

    getData.forEach((user, index) => {
        let createElement = 
        `
        <tr class="align-middle employeeDetails">
            <td>${index + 1}</td>
            <td>
                <img src="${user.picture}" alt="User Picture" class="img-fluid rounded-circle user-img" style="width: 50px; height: 50px;">
            </td>
            <td>${user.name}</td>
            <td>${user.age}</td>
            <td>${user.city}</td>
            <td>${user.email}</td>
            <td>${user.phone}</td>
            <td>${user.post}</td>
            <td>${user.startDate}</td>
            <td>
                <button class="btn btn-info" data-bs-toggle="modal" data-bs-target="#readData" onclick="readData('${user.picture}', '${user.name}', '${user.age}', '${user.city}', '${user.email}', '${user.phone}', '${user.post}', '${user.startDate}')"><i class="bi-eye"></i></button>

                <button class="btn btn-success" onclick="editInfo('${index}', '${user.picture}', '${user.name}', '${user.age}', '${user.city}', '${user.email}', '${user.phone}', '${user.post}', '${user.startDate}')" data-bs-toggle="modal" data-bs-target="#userModal"><i class="bi-pencil"></i></button>

                <button class="btn btn-danger" onclick="deleteInfo(${index})"><i class="bi-trash"></i></button>
            </td>
        </tr>
        `
        
        userInfo.innerHTML += createElement;
    });
}

function deleteInfo(index) {
    if (confirm('Are you sure you want to delete this user?')) {
        getData.splice(index, 1);
        localStorage.setItem('userProfile', JSON.stringify(getData));
        userInfo.innerHTML = '';
        showData();
    }
}   


form.addEventListener('submit', (e) => {
    e.preventDefault();

    const userData = {
        picture: imgInput.src == '' ? 'https://via.placeholder.com/150' : imgInput.src,
        name: nameInput.value,
        age: ageInput.value,
        city: cityInput.value,
        email: emailInput.value,
        phone: phoneInput.value,
        post: postInput.value,
        startDate: dateInput.value
    }

    if (!isEdit) {
        getData.push(userData);
    } else {
        isEdit = false;
        getData[editId] = userData;
    }

    localStorage.setItem('userProfile', JSON.stringify(getData));

    showData();

    form.reset();
})