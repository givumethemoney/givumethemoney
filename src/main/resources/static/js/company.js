document.addEventListener("DOMContentLoaded", async () => {
    const token = localStorage.getItem("jwt");

    if (!token) {
        alert("You are not logged in!");
        window.location.href = "/login";
        return;
    }

    try {
        const response = await fetch("/company", {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`,
            },
        });

        if (response.ok) {
            const data = await response.json();
            console.log("Company data:", data);
        } else {
            console.error("Failed to fetch company data");
            alert("Unauthorized. Please log in again.");
            window.location.href = "/login";
        }
    } catch (error) {
        console.error("Error fetching company data:", error);
    }
});
