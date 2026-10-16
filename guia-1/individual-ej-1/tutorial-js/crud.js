const crudForm = document.getElementById('crudForm');
const employeeList = document.getElementById('employeeList');
const messageElement = document.getElementById('message');
messageElement.style.display = 'none'; // Oculta el mensaje inicialmente

let editMode = false;

const objEmployee = {
    id: '',
    nombre: '',
    puesto: ''
};

let employeesList = [];

crudForm.addEventListener('submit', function(event) {
    event.preventDefault();

    const nombre = document.getElementById('nombre').value;
    const puesto = document.getElementById('puesto').value;

    if (nombre && puesto) {

        greenClass = 'text-green-800 bg-green-100/50 border-green-700/30'
        redClass = 'text-red-800 bg-red-100/50 border-red-700/30'

        messageElement.textContent = editMode ? 'Empleado actualizado correctamente.' : 'Empleado agregado correctamente.';

        if (messageElement.classList.contains('text-red-800')) {
            messageElement.classList.remove(...redClass.split(' '));
            messageElement.classList.add(...greenClass.split(' '));   
        }   

        messageElement.style.display = 'block'; // Muestra el mensaje

        if (editMode) {
            editEmployee();
            editMode = false;
        } else {
            objEmployee.id = Date.now().toString();
            objEmployee.nombre = nombre;
            objEmployee.puesto = puesto;
            addEmployee();
        }


        crudForm.reset(); // Limpia los campos del formulario
        return;
    }


    if (!nombre || !puesto) {
        
        redClass = 'text-red-800 bg-red-100/50 border-red-700/30 display-block'
        greenClass = 'text-green-800 bg-green-100/50 border-green-700/30 display-none'

        messageElement.textContent = 'Por favor, complete todos los campos.';
        messageElement.classList.remove(...greenClass.split(' '));
        messageElement.classList.add(...redClass.split(' '));   

        messageElement.style.display = 'block'; // Muestra el mensaje

        return;
    }

});


function displayEmployees() {
    
    cleanEmployeeList();
    
    employeeList.innerHTML = ''; // Limpia la lista antes de mostrar los empleados
    employeesList.forEach(employee => {
        
        const { id, nombre, puesto } = employee;
        
        console.log(`ID: ${id}, Nombre: ${nombre}, Puesto: ${puesto}`);
        
        const listItem = document.createElement('li');
        listItem.classList.add('mb-2', 'flex', 'items-center', 'justify-between', 'gap-2', 'bg-slate-300/50', 'rounded-lg', 'px-3', 'py-2');
        listItem.textContent = `${employee.nombre} - ${employee.puesto}`;
        listItem.setAttribute('data-id', employee.id);
        
        
        const buttonContainer = document.createElement('div');
        buttonContainer.classList.add('flex', 'gap-2');
        
        
        const editButton = document.createElement('button');
        editButton.textContent = 'Editar';
        const editButtonClasses = ['list-style-none', 'bg-blue-500/50', 'hover:bg-blue-600', 'text-white', 'rounded-lg', 'px-3', 'py-1', 'ml-3', 'cursor-pointer', 'transition', 'duration-150'];
        editButton.classList.add(...editButtonClasses);
        
        editButton.onclick = () => cargarEmpleado(employee);
        
        const deleteButton = document.createElement('button');
        deleteButton.textContent = 'Eliminar';
        const deleteButtonClasses = ['list-style-none', 'bg-red-500/50', 'hover:bg-red-600', 'text-white', 'rounded-lg', 'px-3', 'py-1', 'ml-2', 'cursor-pointer', 'transition', 'duration-150'];
        deleteButton.classList.add(...deleteButtonClasses);
        
        deleteButton.addEventListener('click', function() {
            employeesList = employeesList.filter(e => e.id !== employee.id);
            displayEmployees();
        });
        
        buttonContainer.appendChild(editButton);
        buttonContainer.appendChild(deleteButton);
        listItem.appendChild(buttonContainer);
        
        employeeList.appendChild(listItem);
    });
    
};

function addEmployee() {
    employeesList.push({ ...objEmployee });
    console.log('Empleado agregado:', objEmployee);
    displayEmployees();
    crudForm.reset();
    cleanObject();
    crudForm.querySelector('input[type="submit"]').value = 'Agregar';
}

function cleanEmployeeList() {
    while (employeeList.firstChild) {
        employeeList.removeChild(employeeList.firstChild);
    }
}

function cleanObject() {
    objEmployee.id = '';
    objEmployee.nombre = '';
    objEmployee.puesto = '';
}

function cargarEmpleado(employee) {
    objEmployee.id = employee.id;
    objEmployee.nombre = employee.nombre;
    objEmployee.puesto = employee.puesto;

    document.getElementById('nombre').value = employee.nombre;
    document.getElementById('puesto').value = employee.puesto;

    crudForm.querySelector('input[type="submit"]').value = 'Actualizar';

    editMode = true;
}

function editEmployee() {
    employeesList = employeesList.map(employee => {
        if (employee.id === objEmployee.id) {
            return {
                ...employee,
                nombre: document.getElementById('nombre').value.trim(),
                puesto: document.getElementById('puesto').value.trim()
            };
        }

        return employee;
    });

    displayEmployees();
    crudForm.querySelector('input[type="submit"]').value = 'Agregar';
    crudForm.reset(); // Limpia los campos del formulario
    cleanObject();
    editMode = false;

}