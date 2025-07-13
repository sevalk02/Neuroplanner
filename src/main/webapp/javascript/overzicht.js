const kleurHexMapping = {
    BLAUW: "#2196F3",
    BRUIN: "#795548",
    GEEL: "#FFEB3B",
    GRIJS: "#9E9E9E",
    GROEN: "#4CAF50",
    LICHTBLAUW: "#03A9F4",
    ORANJE: "#FF9800",
    PAARS: "#9C27B0",
    ROOD: "#F44336",
    ROZE: "#E91E63",
    WIT: "#FFFFFF"
};
const dagen = ['Ma', 'Di', 'Wo', 'Do', 'Vr', 'Za', 'Zo'];
const maandNamen = ['januari','februari','maart','april','mei','juni','juli','augustus','september','oktober','november','december'];
let huidigeDatum = new Date();
let taken = [];
let instellingen = {};

function toonWelkom() {
    const user = JSON.parse(localStorage.getItem("user"));
    if (user && user.gebruikersnaam) {
        document.getElementById("welkom").textContent = `Welkom, ${user.gebruikersnaam}!`;
    }
}

async function haalTakenOp() {
    const user = JSON.parse(localStorage.getItem("user"));
    const response = await fetch("/NeuroplannerHU/restservices/taak", {
        headers: { "X-Gebruikersnaam": user.gebruikersnaam }
    });
    taken = await response.json();
    toonKalender();
    toonTakenLijst();
}

function toonKalender() {
    const weergave = instellingen.overzichtWeergave?.toUpperCase() || "MAAND";

    if (weergave === "DAG") {
        toonDagWeergave();
        return;
    } else if (weergave === "WEEK") {
        toonWeekWeergave();
        return;
    }

    const kalenderSectie = document.getElementById('calendar');
    const maandTitel = document.getElementById('maandTitel');
    kalenderSectie.innerHTML = '';

    const jaar = huidigeDatum.getFullYear();
    const maand = huidigeDatum.getMonth();
    maandTitel.textContent = `${maandNamen[maand]} ${jaar}`;

    dagen.forEach(day => {
        const dayName = document.createElement('div');
        dayName.className = 'day-name';
        dayName.textContent = day;
        kalenderSectie.appendChild(dayName);
    });

    const eersteDag = new Date(jaar, maand, 1).getDay();
    const offset = (eersteDag + 6) % 7;
    const dagenInMaand = new Date(jaar, maand + 1, 0).getDate();

    for (let i = 0; i < offset; i++) {
        const leeg = document.createElement('div');
        leeg.className = 'day';
        leeg.innerHTML = '&nbsp;';
        kalenderSectie.appendChild(leeg);
    }

    for (let i = 1; i <= dagenInMaand; i++) {
        const dag = document.createElement('div');
        dag.className = 'day';
        dag.textContent = i;

        const datumString = `${jaar}-${String(maand+1).padStart(2,'0')}-${String(i).padStart(2,'0')}`;

        taken.forEach(t => {
            const eind = t.eindTijd?.substring(0,10);
            const start = t.startTijd?.substring(0,10);
            if (datumString >= start && datumString <= eind) {
                const label = document.createElement('div');
                label.className = 'taak-label';
                label.style.backgroundColor = kleurHexMapping[t.kleur] || "#000";
                label.innerHTML = `<div style="background-color: ${kleurHexMapping[t.kleur] || '#000'}; color: white; padding: 2px 4px; border-radius: 3px; font-size: 11px;">${t.titel}</div>`;
                dag.appendChild(label);
            }
        });

        kalenderSectie.appendChild(dag);
    }
}
function toonDagWeergave() {
    const kalenderSectie = document.getElementById("calendar");
    const maandTitel = document.getElementById("maandTitel");
    kalenderSectie.innerHTML = "";

    maandTitel.textContent = huidigeDatum.toLocaleDateString();

    const dagDiv = document.createElement("div");
    dagDiv.className = "day";
    dagDiv.textContent = huidigeDatum.toDateString();

    const datumString = huidigeDatum.toISOString().substring(0, 10);
    taken.forEach(t => {
        const start = t.startTijd?.substring(0, 10);
        const eind = t.eindTijd?.substring(0, 10);
        if (datumString >= start && datumString <= eind) {
            const label = document.createElement("div");
            label.className = "taak-label";
            label.style.backgroundColor = kleurHexMapping[t.kleur] || "#000";
            label.innerHTML = `<div style="background-color: ${kleurHexMapping[t.kleur]}; color: white; padding: 2px 4px; border-radius: 3px; font-size: 11px;">${t.titel}</div>`;
            dagDiv.appendChild(label);
        }
    });

    kalenderSectie.appendChild(dagDiv);
}

function toonWeekWeergave() {
    const kalenderSectie = document.getElementById("calendar");
    const maandTitel = document.getElementById("maandTitel");
    kalenderSectie.innerHTML = "";

    const weekStart = new Date(huidigeDatum);
    weekStart.setDate(weekStart.getDate() - ((weekStart.getDay() + 6) % 7)); // maandag

    maandTitel.textContent = `Week van ${weekStart.toLocaleDateString()}`;

    for (let i = 0; i < 7; i++) {
        const dag = new Date(weekStart);
        dag.setDate(weekStart.getDate() + i);
        const datumString = dag.toISOString().substring(0, 10);

        const dagDiv = document.createElement("div");
        dagDiv.className = "day";
        dagDiv.textContent = `${dagen[i]} ${dag.getDate()}/${dag.getMonth() + 1}`;

        taken.forEach(t => {
            const start = t.startTijd?.substring(0, 10);
            const eind = t.eindTijd?.substring(0, 10);
            if (datumString >= start && datumString <= eind) {
                const label = document.createElement("div");
                label.className = "taak-label";
                label.style.backgroundColor = kleurHexMapping[t.kleur] || "#000";
                label.innerHTML = `<div style="background-color: ${kleurHexMapping[t.kleur]}; color: white; padding: 2px 4px; border-radius: 3px; font-size: 11px;">${t.titel}</div>`;
                dagDiv.appendChild(label);
            }
        });

        kalenderSectie.appendChild(dagDiv);
    }
}

function veranderDatum(delta) {
    const weergave = instellingen.overzichtWeergave?.toUpperCase();
    if (weergave === "DAG") {
        huidigeDatum.setDate(huidigeDatum.getDate() + delta);
    } else if (weergave === "WEEK") {
        huidigeDatum.setDate(huidigeDatum.getDate() + (7 * delta));
    } else {
        huidigeDatum.setMonth(huidigeDatum.getMonth() + delta);
    }
    toonKalender();
}

function toonTakenLijst() {
    const lijst = document.getElementById("takenOverzicht");
    lijst.innerHTML = '';
    taken.sort((a,b) => new Date(a.eindTijd) - new Date(b.eindTijd));

    const gekozenKleur = document.getElementById("kleurFilter").value;
    const gefilterdeTaken = gekozenKleur
        ? taken.filter(t => t.kleur === gekozenKleur)
        : taken;

    gefilterdeTaken.sort((a, b) => new Date(a.eindTijd) - new Date(b.eindTijd));


    gefilterdeTaken.forEach(t => {
        const div = document.createElement('div');
        div.className = 'taak-item';
        div.style.borderLeftColor = kleurHexMapping[t.kleur] || "#000";

        const titelStijl = t.voltooid ? 'text-decoration: line-through; color: gray;' : '';

        div.innerHTML = `
            <strong style="${titelStijl}">${t.titel}</strong><br>
            ${t.startTijd} - ${t.eindTijd}<br>
            <button class="btn" onclick="verwijderTaak('${t.id}')">Verwijder</button>
            <button class="btn" onclick="verwijderTaak('${t.id}')">Voltooid</button>
            <button class="btn" onclick="openBewerkenPopup('${t.id}')">Aanpassen</button>
            

        `;

        lijst.appendChild(div);
    });
}
function openBewerkenPopup(id) {
    const taak = taken.find(t => t.id == id);
    if (!taak) return;

    document.getElementById("bewerkenId").value = taak.id;
    document.getElementById("bewerkenType").value = taak.type;
    document.getElementById("bewerkenKleur").value = taak.kleur;
    document.getElementById("bewerkenTitel").value = taak.titel;
    document.getElementById("bewerkenOmschrijving").value = taak.omschrijving;
    document.getElementById("bewerkenStartTijd").value = taak.startTijd;
    document.getElementById("bewerkenEindTijd").value = taak.eindTijd;

    document.getElementById("bewerkenPopup").style.display = "block";
}

function sluitPopup() {
    document.getElementById("bewerkenPopup").style.display = "none";
}

document.getElementById("bewerkenForm").addEventListener("submit", async function(e) {
    e.preventDefault();
    const user = JSON.parse(localStorage.getItem("user"));
    const id = document.getElementById("bewerkenId").value;
    const startTijd = document.getElementById("bewerkenStartTijd").value;
    const eindTijd = document.getElementById("bewerkenEindTijd").value;

    if (new Date(startTijd) > new Date(eindTijd)) {
        alert("Starttijd mag niet na de eindtijd liggen.");
        return;
    }

    const aangepasteTaak = {
        id,
        titel: document.getElementById("bewerkenTitel").value,
        omschrijving: document.getElementById("bewerkenOmschrijving").value,
        type: document.getElementById("bewerkenType").value,
        kleur: document.getElementById("bewerkenKleur").value,
        startTijd: document.getElementById("bewerkenStartTijd").value,
        eindTijd: document.getElementById("bewerkenEindTijd").value,
    };

    await fetch(`/NeuroplannerHU/restservices/taak/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "X-Gebruikersnaam": user.gebruikersnaam
        },
        body: JSON.stringify(aangepasteTaak)
    });

    sluitPopup();
    await haalTakenOp();
});
function vulKleurDropdowns() {
    const kleurSelect = document.getElementById("kleur");
    const filterSelect = document.getElementById("kleurFilter");

    const opties = Object.entries(kleurHexMapping).map(([kleur, hex]) => {
        const optie = document.createElement("option");
        optie.value = kleur;
        optie.textContent = kleur.charAt(0) + kleur.slice(1).toLowerCase(); // bv. Lichtblauw
        optie.style.backgroundColor = hex;
        optie.style.color = kleur === "GEEL" || kleur === "ORANJE" || kleur === "WIT" ? "black" : "white";
        return optie;
    });


    kleurSelect.innerHTML = "";
    opties.forEach(optie => kleurSelect.appendChild(optie));


    filterSelect.innerHTML = "";
    const allesOptie = document.createElement("option");
    allesOptie.value = "";
    allesOptie.textContent = "-- Alles --";
    filterSelect.appendChild(allesOptie);
    opties.forEach(optie => filterSelect.appendChild(optie.cloneNode(true)));
}

async function verwijderTaak(id) {
    const user = JSON.parse(localStorage.getItem("user"));
    await fetch(`/NeuroplannerHU/restservices/taak/${id}`, {
        method: "DELETE",
        headers: {
            "X-Gebruikersnaam": user.gebruikersnaam
        }
    });
    await haalTakenOp();
}


function veranderMaand(delta) {
    huidigeDatum.setMonth(huidigeDatum.getMonth() + delta);
    toonKalender();
}
async function haalInstellingenOp() {
    const user = JSON.parse(localStorage.getItem("user"));
    const response = await fetch(`/NeuroplannerHU/restservices/instelling`, {
        headers: { "X-Gebruikersnaam": user.gebruikersnaam }
    });
    if (response.ok) {
        instellingen = await response.json();
        pasInstellingenToe();
    }
}
function pasInstellingenToe() {
    document.body.classList.toggle("dark-mode", instellingen.donkereModus);

    const kalender = document.querySelector(".calendar-section");
    const takenlijst = document.querySelector(".takenlijst");
    if (kalender) kalender.style.display = instellingen.toonKalender ? "block" : "none";
    if (takenlijst) takenlijst.style.display = instellingen.toonTakenlijst ? "block" : "none";
    document.getElementById("donkereModus").checked = instellingen.donkereModus;
    document.getElementById("toonKalender").checked = instellingen.toonKalender;
    document.getElementById("toonTakenlijst").checked = instellingen.toonTakenlijst;
    document.getElementById("weergaveSelect").value = instellingen.overzichtWeergave?.toLowerCase() || "maand";

    toonKalender();


}
async function slaInstellingenOp() {
    const user = JSON.parse(localStorage.getItem("user"));
    instellingen.gebruikersnaam = user.gebruikersnaam;

    console.log("Verstuurde instellingen:", JSON.stringify(instellingen));

    try {
        const response = await fetch("/NeuroplannerHU/restservices/instelling", {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "X-Gebruikersnaam": user.gebruikersnaam
            },
            body: JSON.stringify(instellingen)
        });

        if (!response.ok) {
            throw new Error(`Server error: ${response.status}`);
        }

        console.log("Instellingen succesvol opgeslagen!");
    } catch (error) {
        console.error("Fout bij opslaan instellingen:", error);
        alert("Opslaan van instellingen is mislukt.");
    }
}



document.getElementById("taakForm").addEventListener("submit", async function(e) {
    e.preventDefault();
    const user = JSON.parse(localStorage.getItem("user"));
    const startTijd = document.getElementById("startTijd").value;
    const eindTijd = document.getElementById("eindTijd").value;

    if (new Date(startTijd) > new Date(eindTijd)) {
        alert("Starttijd mag niet na de eindtijd liggen.");
        return;
    }
    const data = {
        titel: document.getElementById("titel").value,
        omschrijving: document.getElementById("omschrijving").value,
        type: document.getElementById("type").value,
        kleur: document.getElementById("kleur").value,
        startTijd: document.getElementById("startTijd").value,
        eindTijd: document.getElementById("eindTijd").value,
    };
    console.log("Verstuurde data:", JSON.stringify(data));

    await fetch("/NeuroplannerHU/restservices/taak", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "X-Gebruikersnaam": user.gebruikersnaam
        },
        body: JSON.stringify(data)
    });

    await haalTakenOp();
    document.getElementById("taakForm").reset();
});

window.onload = async function () {
    document.getElementById("donkereModus").addEventListener("change", e => {
        instellingen.donkereModus = e.target.checked;
        pasInstellingenToe();
        slaInstellingenOp();
    });
    document.getElementById("toonKalender").addEventListener("change", e => {
        instellingen.toonKalender = e.target.checked;
        pasInstellingenToe();
        slaInstellingenOp();
    });
    document.getElementById("toonTakenlijst").addEventListener("change", e => {
        instellingen.toonTakenlijst = e.target.checked;
        pasInstellingenToe();
        slaInstellingenOp();
    });
    document.getElementById("weergaveSelect").addEventListener("change", e => {
        instellingen.overzichtWeergave = e.target.value.toUpperCase(); // DAG, WEEK, MAAND
        toonKalender();
        slaInstellingenOp();
    });

    vulKleurDropdowns();
    document.getElementById("kleurFilter").addEventListener("change", toonTakenLijst);
    toonWelkom();
    await haalInstellingenOp();
    await haalTakenOp();
    document.getElementById("logoutBtn").addEventListener("click", () => {
        localStorage.removeItem("user");
        window.location.href = "index.html";
    });
};
