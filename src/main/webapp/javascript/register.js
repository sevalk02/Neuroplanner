document.getElementById("registerForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const gebruikersnaam = document.getElementById("username").value;
    const wachtwoord = document.getElementById("password").value;
    const voornaam = document.getElementById("firstname").value;
    const achternaam = document.getElementById("lastname").value;
    const rol = document.getElementById("rol").value;

    const response = await fetch("/NeuroplannerHU/restservices/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            gebruikersnaam: gebruikersnaam,
            wachtwoord: wachtwoord,
            rol: rol,
            voornaam: voornaam,
            achternaam: achternaam
        })
    });

    const result = await response.json();

    if (response.ok) {
        alert("Registratie geslaagd. Je kunt nu inloggen.");
        window.location.href = "loginscreen.html";
    } else {
        document.getElementById("feedback").textContent = result.error || "Registratie mislukt.";
    }
});
