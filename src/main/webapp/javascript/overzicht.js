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
    const calendar = document.getElementById('calendar');
    const maandTitel = document.getElementById('maandTitel');
    calendar.innerHTML = '';

    const jaar = huidigeDatum.getFullYear();
    const maand = huidigeDatum.getMonth();
    maandTitel.textContent = `${maandNamen[maand]} ${jaar}`;

    dagen.forEach(day => {
        const dayName = document.createElement('div');
        dayName.className = 'day-name';
        dayName.textContent = day;
        calendar.appendChild(dayName);
    });

    const eersteDag = new Date(jaar, maand, 1).getDay();
    const offset = (eersteDag + 6) % 7;
    const dagenInMaand = new Date(jaar, maand + 1, 0).getDate();
    const vandaag = new Date();

    for (let i = 0; i < offset; i++) {
        const leeg = document.createElement('div');
        leeg.className = 'day';
        leeg.innerHTML = '&nbsp;';
        calendar.appendChild(leeg);
    }

    for (let i = 1; i <= dagenInMaand; i++) {
        const dag = document.createElement('div');
        dag.className = 'day';
        dag.textContent = i;

        if (
            vandaag.getDate() === i &&
            vandaag.getMonth() === maand &&
            vandaag.getFullYear() === jaar
        ) {
            dag.classList.add('today');
        }

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

        calendar.appendChild(dag);
    }
}

function toonTakenLijst() {
    const lijst = document.getElementById("takenOverzicht");
    lijst.innerHTML = '';
    taken.sort((a,b) => new Date(a.eindTijd) - new Date(b.eindTijd));

    taken.forEach(t => {
        const div = document.createElement('div');
        div.className = 'taak-item';
        div.style.borderLeftColor = kleurHexMapping[t.kleur] || "#000";

        const titelStijl = t.voltooid ? 'text-decoration: line-through; color: gray;' : '';

        div.innerHTML = `
            <strong style="${titelStijl}">${t.titel}</strong><br>
            ${t.startTijd} - ${t.eindTijd}<br>
            <button class="btn" onclick="verwijderTaak('${t.id}')">Verwijder</button>
            <button class="btn" onclick="verwijderTaak('${t.id}')">Voltooid</button>
        `;

        lijst.appendChild(div);
    });
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

document.getElementById("taakForm").addEventListener("submit", async function(e) {
    e.preventDefault();
    const user = JSON.parse(localStorage.getItem("user"));
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
    toonWelkom();
    await haalTakenOp();
};
