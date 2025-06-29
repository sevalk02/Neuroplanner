document.getElementById("loginForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const baseUrl = window.location.pathname.split("/")[1] || "";

    const response = await fetch(`/${baseUrl}/restservices/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ gebruikersnaam: username, wachtwoord: password })
    });


    const result = await response.json();

    if (response.ok) {
        localStorage.setItem("user", JSON.stringify({
            gebruikersnaam: result.gebruikersnaam,
            rol: result.rol
        }));
        window.location.href = "overzicht.html";
    } else {
        document.getElementById("feedback").textContent = result.error || "Login mislukt.";
    }
});
