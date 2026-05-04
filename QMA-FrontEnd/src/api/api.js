// =========================================
// Centralized API client for QMA backend
// =========================================

const API_BASE_URL = "http://localhost:8080/api";

/**
 * Generic request helper that attaches the JWT token
 * and handles common error responses.
 */
export async function apiRequest(path, { method = "POST", body, params, auth = true } = {}) {
  let url = `${API_BASE_URL}${path}`;

  // Append query params if provided
  if (params) {
    const qs = new URLSearchParams(params).toString();
    url += `?${qs}`;
  }

  const headers = { "Content-Type": "application/json" };

  if (auth) {
    const token = localStorage.getItem("jwt_token");
    if (token) {
      headers["Authorization"] = `Bearer ${token}`;
    }
  }

  const response = await fetch(url, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });

  // Session expired
  if (response.status === 401 || response.status === 403) {
    localStorage.removeItem("jwt_token");
    throw new Error("Session expired. Please log in again.");
  }

  if (!response.ok) {
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || (await response.text()) || "Request failed");
  }

  // Some endpoints may return plain boolean
  const contentType = response.headers.get("content-type") || "";
  if (contentType.includes("application/json")) {
    return response.json();
  }
  return response.text();
}

// ---------- Auth endpoints ----------

export function login(email, password) {
  return apiRequest("/auth/login", { body: { email, password }, auth: false });
}

export function register(name, email, password) {
  return apiRequest("/auth/register", { body: { name, email, password }, auth: false });
}

// ---------- Quantity endpoints ----------

export function convert(quantityType, value, unit, targetUnit) {
  return apiRequest("/quantity/convert", {
    body: { quantityType, value, unit },
    params: { targetUnit },
  });
}

export function compare(quantityType, firstValue, firstUnit, secondValue, secondUnit) {
  return apiRequest("/quantity/compare", {
    body: { quantityType, firstValue, firstUnit, secondValue, secondUnit },
  });
}

export function add(quantityType, firstValue, firstUnit, secondValue, secondUnit, targetUnit) {
  return apiRequest("/quantity/add", {
    body: { quantityType, firstValue, firstUnit, secondValue, secondUnit, targetUnit },
  });
}

export function subtract(quantityType, firstValue, firstUnit, secondValue, secondUnit, targetUnit) {
  return apiRequest("/quantity/subtract", {
    body: { quantityType, firstValue, firstUnit, secondValue, secondUnit, targetUnit },
  });
}

export function divide(quantityType, firstValue, firstUnit, secondValue, secondUnit, targetUnit) {
  return apiRequest("/quantity/divide", {
    body: { quantityType, firstValue, firstUnit, secondValue, secondUnit, targetUnit },
  });
}
