/**
 * DIGITAL BANKING SYSTEM - Common JavaScript Utilities
 */

const BASE_URL = "http://localhost:8080";

// ============ Theme Management ============
function initTheme() {
    const savedTheme = localStorage.getItem("theme") || "dark";
    document.documentElement.setAttribute("data-theme", savedTheme);
}

function toggleTheme() {
    const currentTheme = document.documentElement.getAttribute("data-theme");
    const newTheme = currentTheme === "light" ? "dark" : "light";
    document.documentElement.setAttribute("data-theme", newTheme);
    localStorage.setItem("theme", newTheme);
}

// Initialize theme on page load
initTheme();

// ============ Toast Notifications ============
function createToastContainer() {
    if (!document.querySelector('.toast-container')) {
        const container = document.createElement('div');
        container.className = 'toast-container';
        document.body.appendChild(container);
    }
    return document.querySelector('.toast-container');
}

function showToast(message, type = 'info', duration = 4000) {
    const container = createToastContainer();
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    
    const icons = {
        success: 'fa-check-circle',
        error: 'fa-exclamation-circle',
        warning: 'fa-exclamation-triangle',
        info: 'fa-info-circle'
    };
    
    toast.innerHTML = `
        <i class="fas ${icons[type]}"></i>
        <span>${message}</span>
        <button class="toast-close" onclick="this.parentElement.remove()">
            <i class="fas fa-times"></i>
        </button>
    `;
    
    container.appendChild(toast);
    
    setTimeout(() => {
        toast.style.animation = 'slideIn 0.3s ease reverse';
        setTimeout(() => toast.remove(), 300);
    }, duration);
}

// ============ API Utilities ============
async function apiCall(endpoint, method = 'GET', body = null) {
    const options = {
        method,
        headers: {
            'Content-Type': 'application/json',
        }
    };
    
    if (body) {
        options.body = JSON.stringify(body);
    }
    
    try {
        const response = await fetch(BASE_URL + endpoint, options);
        const contentType = response.headers.get('content-type');
        
        if (!response.ok) {
            let errorMessage = 'Request failed';
            if (contentType && contentType.includes('application/json')) {
                const errorData = await response.json();
                errorMessage = errorData.message || errorData.error || errorMessage;
            } else {
                errorMessage = await response.text() || errorMessage;
            }
            throw new Error(errorMessage);
        }
        
        if (contentType && contentType.includes('application/json')) {
            return await response.json();
        }
        return await response.text();
        
    } catch (error) {
        if (error.message === 'Failed to fetch') {
            throw new Error('Unable to connect to server. Please check if the backend is running.');
        }
        throw error;
    }
}

// ============ Form Validation Utilities ============
const Validators = {
    required: (value, fieldName) => {
        if (!value || value.toString().trim() === '') {
            return `${fieldName} is required`;
        }
        return null;
    },
    
    minLength: (value, min, fieldName) => {
        if (value && value.length < min) {
            return `${fieldName} must be at least ${min} characters`;
        }
        return null;
    },
    
    maxLength: (value, max, fieldName) => {
        if (value && value.length > max) {
            return `${fieldName} must not exceed ${max} characters`;
        }
        return null;
    },
    
    exactLength: (value, length, fieldName) => {
        if (value && value.length !== length) {
            return `${fieldName} must be exactly ${length} characters`;
        }
        return null;
    },
    
    numeric: (value, fieldName) => {
        if (value && !/^\d+$/.test(value)) {
            return `${fieldName} must contain only numbers`;
        }
        return null;
    },
    
    alpha: (value, fieldName) => {
        if (value && !/^[a-zA-Z\s]+$/.test(value)) {
            return `${fieldName} must contain only letters and spaces`;
        }
        return null;
    },
    
    alphanumeric: (value, fieldName) => {
        if (value && !/^[a-zA-Z0-9]+$/.test(value)) {
            return `${fieldName} must contain only letters and numbers`;
        }
        return null;
    },
    
    email: (value, fieldName) => {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (value && !emailRegex.test(value)) {
            return `Please enter a valid ${fieldName}`;
        }
        return null;
    },
    
    phone: (value) => {
        if (value && !/^\d{10}$/.test(value)) {
            return 'Phone number must be exactly 10 digits';
        }
        return null;
    },
    
    aadhar: (value) => {
        if (value && !/^\d{12}$/.test(value)) {
            return 'Aadhar number must be exactly 12 digits';
        }
        return null;
    },
    
    positiveNumber: (value, fieldName) => {
        const num = parseFloat(value);
        if (isNaN(num) || num <= 0) {
            return `${fieldName} must be a positive number`;
        }
        return null;
    },
    
    accountNumber: (value) => {
        if (value && !/^\d{1,20}$/.test(value)) {
            return 'Please enter a valid account number';
        }
        return null;
    },
    
    customerId: (value) => {
        if (value && !/^CUST\d{3,}$/.test(value.toUpperCase())) {
            return 'Customer ID must be in format CUST followed by numbers (e.g., CUST001)';
        }
        return null;
    }
};

// ============ Form Field Validation ============
function validateField(input, validations) {
    const value = input.value.trim();
    const fieldName = input.getAttribute('data-label') || input.placeholder || 'Field';
    
    for (const validation of validations) {
        const error = validation(value, fieldName);
        if (error) {
            showFieldError(input, error);
            return false;
        }
    }
    
    clearFieldError(input);
    return true;
}

function showFieldError(input, message) {
    input.classList.add('invalid');
    input.classList.remove('valid');
    
    let errorEl = input.parentElement.querySelector('.error-message');
    if (!errorEl) {
        errorEl = document.createElement('div');
        errorEl.className = 'error-message';
        input.parentElement.appendChild(errorEl);
    }
    
    errorEl.innerHTML = `<i class="fas fa-exclamation-circle"></i> ${message}`;
    errorEl.classList.add('show');
}

function clearFieldError(input) {
    input.classList.remove('invalid');
    input.classList.add('valid');
    
    const errorEl = input.parentElement.querySelector('.error-message');
    if (errorEl) {
        errorEl.classList.remove('show');
    }
}

function clearAllErrors(form) {
    form.querySelectorAll('.error-message').forEach(el => el.classList.remove('show'));
    form.querySelectorAll('input, select').forEach(el => {
        el.classList.remove('invalid', 'valid');
    });
}

// ============ Result Box ============
function showResult(elementId, message, type) {
    const el = document.getElementById(elementId);
    if (!el) return;
    
    el.innerHTML = `<i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'}"></i> ${message}`;
    el.className = `result ${type} show`;
}

function hideResult(elementId) {
    const el = document.getElementById(elementId);
    if (el) {
        el.className = 'result';
        el.innerHTML = '';
    }
}

// ============ Button Loading State ============
function setLoading(button, loading) {
    if (loading) {
        button.classList.add('loading');
        button.disabled = true;
        button.dataset.originalText = button.innerHTML;
        button.innerHTML = '<span style="visibility: hidden;">' + button.innerHTML + '</span>';
    } else {
        button.classList.remove('loading');
        button.disabled = false;
        button.innerHTML = button.dataset.originalText || button.innerHTML;
    }
}

// ============ Account Datalist ============
let accountsCache = [];

async function loadAccounts() {
    try {
        const accounts = await apiCall('/api/accounts');
        accountsCache = accounts;
        updateAccountDatalist();
        return accounts;
    } catch (error) {
        console.warn('Could not load accounts:', error.message);
        return [];
    }
}

function updateAccountDatalist() {
    const datalist = document.getElementById('accountsList');
    if (!datalist) return;
    
    datalist.innerHTML = accountsCache.map(acc => 
        `<option value="${acc.accountNo}">${acc.customerName} - ${acc.accountType} - ₹${acc.balance.toFixed(2)}</option>`
    ).join('');
}

function getAccountFromCache(accountNo) {
    return accountsCache.find(acc => acc.accountNo.toString() === accountNo.toString());
}

// ============ Date Formatting ============
function formatDate(dateValue) {
    if (!dateValue) return 'N/A';
    
    let date;
    
    // Handle array format from Jackson
    if (Array.isArray(dateValue)) {
        const [year, month, day, hour = 0, minute = 0, second = 0] = dateValue;
        date = new Date(year, month - 1, day, hour, minute, second);
    }
    // Handle object format
    else if (typeof dateValue === 'object' && dateValue.year) {
        date = new Date(
            dateValue.year,
            (dateValue.monthValue || dateValue.month || 1) - 1,
            dateValue.dayOfMonth || dateValue.day || 1,
            dateValue.hour || 0,
            dateValue.minute || 0,
            dateValue.second || 0
        );
    }
    // Handle string format
    else {
        date = new Date(dateValue);
    }
    
    if (isNaN(date.getTime())) return 'N/A';
    
    return date.toLocaleString('en-IN', {
        day: '2-digit',
        month: 'short',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: true
    });
}

// ============ Currency Formatting ============
function formatCurrency(amount) {
    return new Intl.NumberFormat('en-IN', {
        style: 'currency',
        currency: 'INR',
        maximumFractionDigits: 2
    }).format(amount);
}

// ============ Loading Overlay ============
function showLoadingOverlay() {
    let overlay = document.querySelector('.loading-overlay');
    if (!overlay) {
        overlay = document.createElement('div');
        overlay.className = 'loading-overlay';
        overlay.innerHTML = '<div class="loader"></div>';
        document.body.appendChild(overlay);
    }
    overlay.classList.add('show');
}

function hideLoadingOverlay() {
    const overlay = document.querySelector('.loading-overlay');
    if (overlay) {
        overlay.classList.remove('show');
    }
}

// ============ Input Sanitization ============
function sanitizeInput(value) {
    if (typeof value !== 'string') return value;
    return value.replace(/<[^>]*>/g, '').trim();
}

// ============ Initialize Common Features ============
document.addEventListener('DOMContentLoaded', () => {
    // Load accounts if datalist exists
    if (document.getElementById('accountsList')) {
        loadAccounts();
        setInterval(loadAccounts, 10000); // Refresh every 10 seconds
    }
    
    // Add real-time validation to inputs
    document.querySelectorAll('input[data-validate]').forEach(input => {
        input.addEventListener('blur', () => {
            const validations = input.dataset.validate.split(',');
            // Add validation based on data attributes
        });
    });
});