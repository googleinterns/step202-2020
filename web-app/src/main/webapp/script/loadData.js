async function loadPoliceReports(map) {
  const FILE_NAMES = [
    "2019_12_london",
    "2020_01_london",
    "2020_02_london",
    "2020_03_london",
    "2020_04_london",
    "2020_05_london",
  ];
  for (const file_name of FILE_NAMES) {
    const data = await fetch("../data/" + file_name + ".json");
    const reports = await data.json();
    for (report of reports) {
      new google.maps.Marker({
        position: {
          lat: Number(report.latitude),
          lng: Number(report.longitude),
        },
        map: map,
      });
    }
  }
}

async function fetchMarkers(map) {
  const response = await fetch("/report");
  const markers = await response.json();
  let uiState = { activeInfoWindow: null };

  markers.forEach((marker) => {
    createMarkerForDisplay(map, marker, uiState);
  });

  map.addListener("click", () => {
    if (uiState.activeInfoWindow) {
      uiState.activeInfoWindow.close();
      uiState.activeInfoWindow = null;
    }
  });
}

function createMarkerForDisplay(map, data, uiState) {
  const marker = new google.maps.Marker({
    position: { lat: data.latitude, lng: data.longitude },
    map: map,
  });

  const infoParagraph = document.createElement("div");
  infoParagraph.setAttribute("id", "info-window");
  const timestamp = new Date(data.timestamp);
  infoParagraph.innerHTML = `
    <h1>${data.title}</h1>
    <p>${timestamp.toLocaleDateString()}, ${timestamp.toLocaleTimeString()}</p>
    <p>${data.description}</p>
  `;

  if (data.imageUrl) {
    infoParagraph.insertAdjacentHTML(
      "beforeend",
      `<img src="${window.location.href}serve?blob-key=${data.imageUrl}"
      id="info-image" alt="User-submitted image of incident">`
    );
  }

  const infoWindow = new google.maps.InfoWindow({ content: infoParagraph });
  marker.addListener("click", () => {
    if (uiState.activeInfoWindow) {
      uiState.activeInfoWindow.close();
    }
    infoWindow.open(map, marker);
    uiState.activeInfoWindow = infoWindow;
  });
}
