// 로그인 폼 처리 로직
document.addEventListener("DOMContentLoaded", () => {
    const loginForm = document.getElementById("loginForm");

    loginForm.addEventListener("submit", async (event) => {
        event.preventDefault(); // 기본 폼 제출 동작 방지

        const formData = new FormData(loginForm);
        const email = formData.get("email");
        const password = formData.get("password");

        try {
            const response = await fetch("/login", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: new URLSearchParams({ email, password }),
            });

            if (response.ok) {
                const result = await response.json();
                const token = result.token;

                // JWT를 localStorage에 저장
                localStorage.setItem("jwt", token);
                console.log("JWT saved:", token);

                // /company로 리다이렉트
                window.location.href = "/company";
            } else {
                alert("Login failed. Please check your credentials.");
            }
        } catch (error) {
            console.error("Error during login:", error);
            alert("An error occurred during login.");
        }
    });
});
