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
const typeSelect = document.getElementById('quantityType');
const inputUnit1 = document.getElementById('inputUnit1');
const inputUnit2 = document.getElementById('inputUnit2');
const targetUnitSelect = document.getElementById('targetUnit');

const groupValue2 = document.getElementById('groupValue2');
const groupUnit2 = document.getElementById('groupUnit2');
const groupTargetUnit = document.getElementById('groupTargetUnit');
const submitBtn = document.getElementById('submitBtn');

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
// 3. INITIALIZATION & SECURITY
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
        window.history.replaceState({}, document.title, "/");
    }

    const token = localStorage.getItem('jwt_token');
    
    if (token) {
        authSection.classList.add('hidden');
        converterSection.classList.remove('hidden');
        logoutBtn.classList.remove('hidden');
    } else {
        authSection.classList.remove('hidden');
        converterSection.classList.add('hidden');
        logoutBtn.classList.add('hidden');
    }
}

logoutBtn.addEventListener('click', () => {
    localStorage.removeItem('jwt_token');
    checkAuthentication();
    resultDisplay.classList.add('hidden');
});

// ==========================================
// 4. DYNAMIC UI RENDERING
// ==========================================
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
    
    // Reset requirements to prevent HTML form validation errors on hidden fields
    document.getElementById('inputValue2').required = false;
    targetUnitSelect.required = false;

    if (operation === 'CONVERT') {
        groupValue2.classList.add('hidden');
        groupUnit2.classList.add('hidden');
        groupTargetUnit.classList.remove('hidden');
        targetUnitSelect.required = true;
        submitBtn.textContent = "Convert";
        
    } else if (operation === 'COMPARE') {
        groupValue2.classList.remove('hidden');
        groupUnit2.classList.remove('hidden');
        groupTargetUnit.classList.add('hidden');
        document.getElementById('inputValue2').required = true;
        submitBtn.textContent = "Compare";
        
    } else if (operation === 'ADD') {
        groupValue2.classList.remove('hidden');
        groupUnit2.classList.remove('hidden');
        groupTargetUnit.classList.remove('hidden');
        document.getElementById('inputValue2').required = true;
        targetUnitSelect.required = true;
        submitBtn.textContent = "Add";
        
        // UX Guardrail: Prevent adding temperatures
        if (typeSelect.value === 'TEMPERATURE') {
            alert("Cannot perform Addition on Temperatures!");
            typeSelect.value = 'LENGTH';
            updateDropdowns('LENGTH');
        }
    }
}

// ==========================================
// 5. EVENT LISTENERS
// ==========================================
typeSelect.addEventListener('change', (e) => {
    if (operationSelect.value === 'ADD' && e.target.value === 'TEMPERATURE') {
        alert("Cannot add temperatures. Switching to Length.");
        e.target.value = 'LENGTH';
    }
    updateDropdowns(e.target.value);
});

operationSelect.addEventListener('change', updateFormLayout);

// ==========================================
// 6. AJAX API ROUTER
// ==========================================
conversionForm.addEventListener('submit', async (event) => {
    event.preventDefault();

    const operation = operationSelect.value;
    const token = localStorage.getItem('jwt_token');
    
    // Extract Form Values
    const qType = typeSelect.value;
    const val1 = parseFloat(document.getElementById('inputValue1').value);
    const u1 = inputUnit1.value;
    const val2 = parseFloat(document.getElementById('inputValue2').value || 0);
    const u2 = inputUnit2.value;
    const tUnit = targetUnitSelect.value;

    let url = "";
    let payload = {};

    // Map to the correct backend DTO
    if (operation === 'CONVERT') {
        url = `http://localhost:8080/api/quantity/convert?targetUnit=${tUnit}`;
        payload = { quantityType: qType, value: val1, unit: u1 };
    } 
    else if (operation === 'COMPARE') {
        url = `http://localhost:8080/api/quantity/compare`;
        payload = { quantityType: qType, firstValue: val1, firstUnit: u1, secondValue: val2, secondUnit: u2 };
    } 
    else if (operation === 'ADD') {
        url = `http://localhost:8080/api/quantity/add`;
        payload = { quantityType: qType, firstValue: val1, firstUnit: u1, secondValue: val2, secondUnit: u2, targetUnit: tUnit };
    }

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(payload)
        });

        // Handle Token Expiration
        if (response.status === 401 || response.status === 403) {
            throw new Error("Session expired. Please log in again.");
        }

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.message || "Request failed on the server.");
        }

        const data = await response.json();
        
        // Dynamic UI Rendering based on Operation Type
        if (operation === 'COMPARE') {
            finalResult.textContent = data === true ? "They are EQUAL!" : "They are NOT EQUAL!";
        } else {
            // Falls back safely depending on how your backend named the response field
            const outputUnit = data.targetUnitStr || data.targetUnit || data.unit || tUnit;
            finalResult.textContent = `${data.resultValue} ${outputUnit}`;
        }
        
        resultDisplay.classList.remove('hidden');

    } catch (error) {
        alert(`Error: ${error.message}`);
        if (error.message.includes("Session expired")) {
            localStorage.removeItem('jwt_token');
            checkAuthentication();
            resultDisplay.classList.add('hidden');
        }
    }
});