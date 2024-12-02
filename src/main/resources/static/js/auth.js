// auth.js

// 로그인 여부 및 토큰을 로컬 스토리지에서 확인
function getToken() {
    return localStorage.getItem('token');
}

// 토큰이 있다면 Authorization 헤더에 추가하여 API 호출
function addAuthHeader(headers = {}) {
    const token = getToken();
    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }
    return headers;
}

// API 호출 예시
function fetchWithAuth(url, options = {}) {
    const headers = addAuthHeader(options.headers || {});
    return fetch(url, { ...options, headers });
}

// 로그인 상태 확인 및 리다이렉트
function checkLogin() {
    if (!getToken()) {
        window.location.href = '/login.html'; // 로그인 페이지로 리다이렉트
    }
}

// 다른 페이지에서 사용할 수 있는 API 호출 함수 예시
function getProtectedData() {
    fetchWithAuth('/login', { method: 'POST' })
        .then(response => response.json())
        .then(data => console.log(data))
        .catch(error => console.error('Error:', error));
}
