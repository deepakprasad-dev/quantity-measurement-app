// ==========================================
// 1. DOM ELEMENTS
// ==========================================
const authSection = document.getElementById('authSection');
const converterSection = document.getElementById('converterSection');
const logoutBtn = document.getElementById('logoutBtn');
const conversionForm = document.getElementById('conversionForm');
const resultDisplay = document.getElementById('resultDisplay');
const finalResult = document.getElementById('finalResult');

const operationSelect = document.getElementById('operationType');
const inputUnit1 = document.getElementById('inputUnit1');
const inputUnit2 = document.getElementById('inputUnit2');
const targetUnitSelect = document.getElementById('targetUnit');
const quantityTypeRadios = document.querySelectorAll('input[name="quantityType"]');

const groupValue2 = document.getElementById('groupValue2');
const groupUnit2 = document.getElementById('groupUnit2');
const groupTargetUnit = document.getElementById('groupTargetUnit');
const submitBtn = document.getElementById('submitBtn');
const btnText = document.getElementById('btnText');
const spinner = document.getElementById('spinner');

// Auth Form Elements
const tabLogin = document.getElementById('tabLogin');
const tabRegister = document.getElementById('tabRegister');
const loginForm = document.getElementById('loginForm');
const registerForm = document.getElementById('registerForm');

// ==========================================
// 2. DATA DICTIONARY
// ==========================================
const unitMap = {
    LENGTH: ['INCH', 'FEET', 'YARD', 'CENTIMETER'],
    VOLUME: ['GALLON', 'LITRE', 'MILLILITER'],
    WEIGHT: ['GRAM', 'KILOGRAM', 'TONNE'],
    TEMPERATURE: ['CELSIUS', 'FAHRENHEIT']
};

// ==========================================
// 3. TOAST NOTIFICATIONS (Bootstrap)
// ==========================================
const toastContainer = document.getElementById('toastContainer');
function showToast(message, type = 'success') {
    let bgClass = 'text-bg-success';
    if(type === 'error') bgClass = 'text-bg-danger';
    else if(type === 'warning') bgClass = 'text-bg-warning';

    const toastHTML = `
        <div class="toast align-items-center ${bgClass} border-0" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="d-flex">
                <div class="toast-body">
                    ${message}
                </div>
                <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
            </div>
        </div>
    `;
    
    toastContainer.insertAdjacentHTML('beforeend', toastHTML);
    const toastElement = toastContainer.lastElementChild;
    const bsToast = new bootstrap.Toast(toastElement, { delay: 3000 });
    bsToast.show();
    
    toastElement.addEventListener('hidden.bs.toast', () => {
        toastElement.remove();
    });
}

// ==========================================
// 4. INITIALIZATION & SECURITY
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
    checkAuthentication();
    updateDropdowns('LENGTH');
    updateFormLayout();
});

function checkAuthentication() {
    const urlParams = new URLSearchParams(window.location.search);
    const tokenFromUrl = urlParams.get('token');

    if (tokenFromUrl) {
        localStorage.setItem('jwt_token', tokenFromUrl);
        window.history.replaceState({}, document.title, window.location.pathname);
        showToast("Successfully logged in with Google!");
    }

    const token = localStorage.getItem('jwt_token');
    
    if (token) {
        authSection.classList.add('d-none');
        converterSection.classList.remove('d-none');
        logoutBtn.classList.remove('d-none');
    } else {
        authSection.classList.remove('d-none');
        converterSection.classList.add('d-none');
        logoutBtn.classList.add('d-none');
    }
}

logoutBtn.addEventListener('click', () => {
    localStorage.removeItem('jwt_token');
    checkAuthentication();
    resultDisplay.classList.add('d-none');
    showToast("Logged out successfully.");
});

// ==========================================
// 5. AUTHENTICATION LOGIC (LOGIN & REGISTER)
// ==========================================
const API_BASE_URL = "http://localhost:8080/api";

tabLogin.addEventListener('click', () => {
    tabLogin.classList.add('active');
    tabRegister.classList.remove('active');
    loginForm.classList.remove('d-none');
    registerForm.classList.add('d-none');
});

tabRegister.addEventListener('click', () => {
    tabRegister.classList.add('active');
    tabLogin.classList.remove('active');
    registerForm.classList.remove('d-none');
    loginForm.classList.add('d-none');
});

loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;
    
    const submitBtnLogin = loginForm.querySelector('button[type="submit"]');
    submitBtnLogin.disabled = true;
    submitBtnLogin.textContent = "Signing In...";

    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        
        if(!response.ok) throw new Error("Invalid email or password");
        
        const data = await response.json();
        localStorage.setItem('jwt_token', data.token);
        showToast("Logged in successfully!");
        checkAuthentication();
    } catch (error) {
        showToast(error.message, 'error');
    } finally {
        submitBtnLogin.disabled = false;
        submitBtnLogin.textContent = "Login";
    }
});

registerForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const name = document.getElementById('registerName').value;
    const email = document.getElementById('registerEmail').value;
    const password = document.getElementById('registerPassword').value;
    
    const submitBtnReg = registerForm.querySelector('button[type="submit"]');
    submitBtnReg.disabled = true;
    submitBtnReg.textContent = "Signing Up...";

    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });
        
        if(!response.ok) {
            const errText = await response.text();
            throw new Error(errText || "Registration failed");
        }
        
        showToast("Account created successfully! Please log in.");
        tabLogin.click(); // Switch to login tab
        document.getElementById('loginEmail').value = email;
    } catch (error) {
        showToast(error.message, 'error');
    } finally {
        submitBtnReg.disabled = false;
        submitBtnReg.textContent = "Sign Up";
    }
});


// ==========================================
// 6. DYNAMIC UI RENDERING
// ==========================================
function getSelectedType() {
    const checked = document.querySelector('input[name="quantityType"]:checked');
    return checked ? checked.value : 'LENGTH';
}

function updateDropdowns(selectedType) {
    const units = unitMap[selectedType];
    
    inputUnit1.innerHTML = '';
    inputUnit2.innerHTML = '';
    targetUnitSelect.innerHTML = '';

    units.forEach(unit => {
        const optionText = unit.charAt(0) + unit.slice(1).toLowerCase();
        inputUnit1.add(new Option(optionText, unit));
        inputUnit2.add(new Option(optionText, unit));
        targetUnitSelect.add(new Option(optionText, unit));
    });
}

function updateFormLayout() {
    const operation = operationSelect.value;
    const currentType = getSelectedType();
    
    // Reset requirements
    document.getElementById('inputValue2').required = false;
    targetUnitSelect.required = false;

    if (operation === 'CONVERT') {
        groupValue2.classList.add('d-none');
        groupTargetUnit.classList.remove('d-none');
        targetUnitSelect.required = true;
        btnText.textContent = "Convert";
        
    } else if (operation === 'COMPARE') {
        groupValue2.classList.remove('d-none');
        groupTargetUnit.classList.add('d-none');
        document.getElementById('inputValue2').required = true;
        btnText.textContent = "Compare";
        
    } else if (operation === 'ADD') {
        groupValue2.classList.remove('d-none');
        groupTargetUnit.classList.remove('d-none');
        document.getElementById('inputValue2').required = true;
        targetUnitSelect.required = true;
        btnText.textContent = "Add";
        
        // UX Guardrail: Prevent adding temperatures
        if (currentType === 'TEMPERATURE') {
            showToast("Cannot perform Addition on Temperatures!", "warning");
            document.getElementById('typeLength').checked = true;
            updateDropdowns('LENGTH');
        }
    }
}

// ==========================================
// 7. EVENT LISTENERS
// ==========================================
quantityTypeRadios.forEach(radio => {
    radio.addEventListener('change', (e) => {
        if (operationSelect.value === 'ADD' && e.target.value === 'TEMPERATURE') {
            showToast("Cannot add temperatures. Switching to Length.", "warning");
            document.getElementById('typeLength').checked = true;
            updateDropdowns('LENGTH');
            return;
        }
        updateDropdowns(e.target.value);
        resultDisplay.classList.add('d-none');
    });
});

operationSelect.addEventListener('change', () => {
    updateFormLayout();
    resultDisplay.classList.add('d-none');
});

// ==========================================
// 8. CONVERTER API LOGIC
// ==========================================
conversionForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    const operation = operationSelect.value;
    const token = localStorage.getItem('jwt_token');
    
    const qType = getSelectedType();
    const val1 = parseFloat(document.getElementById('inputValue1').value);
    const u1 = inputUnit1.value;
    const val2 = parseFloat(document.getElementById('inputValue2').value || 0);
    const u2 = inputUnit2.value;
    const tUnit = targetUnitSelect.value;

    let url = "";
    let payload = {};

    if (operation === 'CONVERT') {
        url = `${API_BASE_URL}/quantity/convert?targetUnit=${tUnit}`;
        payload = { quantityType: qType, value: val1, unit: u1 };
    } 
    else if (operation === 'COMPARE') {
        url = `${API_BASE_URL}/quantity/compare`;
        payload = { quantityType: qType, firstValue: val1, firstUnit: u1, secondValue: val2, secondUnit: u2 };
    } 
    else if (operation === 'ADD') {
        url = `${API_BASE_URL}/quantity/add`;
        payload = { quantityType: qType, firstValue: val1, firstUnit: u1, secondValue: val2, secondUnit: u2, targetUnit: tUnit };
    }

    // UI Loading State
    spinner.classList.remove('d-none');
    submitBtn.disabled = true;
    resultDisplay.classList.add('d-none');

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(payload)
        });

        if (response.status === 401 || response.status === 403) {
            throw new Error("Session expired. Please log in again.");
        }

        if (!response.ok) {
            const errorData = await response.json().catch(() => ({}));
            throw new Error(errorData.message || "Request failed on the server.");
        }

        const data = await response.json();
        
        if (operation === 'COMPARE') {
            finalResult.textContent = data === true ? "EQUAL" : "NOT EQUAL";
            resultDisplay.className = data === true ? "alert alert-success mt-4 text-center" : "alert alert-danger mt-4 text-center";
        } else {
            const outputUnit = data.targetUnitStr || data.targetUnit || data.unit || tUnit;
            finalResult.textContent = `${data.resultValue.toFixed(2)} ${outputUnit.toLowerCase()}`;
            resultDisplay.className = "alert alert-success mt-4 text-center";
        }
        
        resultDisplay.classList.remove('d-none');
        showToast("Operation successful!");

    } catch (error) {
        showToast(error.message, 'error');
        if (error.message.includes("Session expired")) {
            localStorage.removeItem('jwt_token');
            checkAuthentication();
        }
    } finally {
        spinner.classList.add('d-none');
        submitBtn.disabled = false;
    }
});