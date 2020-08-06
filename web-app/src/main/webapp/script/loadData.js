class MapComponents {
  constructor() {
    this.mapMarkers = [];
    this.activeInfoWindow = new google.maps.InfoWindow();
  }
}

let mapComponents = new MapComponents();

export async function fetchAndParseJson(url) {
  const response = await fetch(url);
  const json = await response.json();
  return json;
}

export async function loadPoliceReports(map) {
  // Clear all markers on the map
  for (const marker of mapComponents.mapMarkers) {
    marker.setMap(null);
  }
  mapComponents.mapMarkers = [];

  const FILE_NAMES = [
    "2019_12_london",
    "2020_01_london",
    "2020_02_london",
    "2020_03_london",
    "2020_04_london",
    "2020_05_london",
  ];
  const uncheckedCategoriesElement = Array.from(
    document.querySelectorAll("input.category:not(:checked)")
  );
  const uncheckedCategories = uncheckedCategoriesElement.map((element) => element.value);
  const numberOfMonths = Number(document.querySelector("input.time-frame:checked").value);

  const markersArrayForEachReports = await Promise.all(
    FILE_NAMES.map(async (file_name) => {
      const reports = await fetchAndParseJson("../data/" + file_name + ".json");
      const filteredReports = filterReports(reports, uncheckedCategories, numberOfMonths);
      const markers = createMarkers(map, filteredReports);
      for (const marker of markers) {
        google.maps.event.addListener(marker, "click", () => {
          const contentString =
            `<div id="info-window">
            <p>${marker.crimeType}</p>
            <p>${marker.date.getFullYear()}-${marker.date.getMonth()}</p>
            </div>`;
          const infoWindow = new google.maps.InfoWindow({ content: infoParagraph });
          closeActiveWindow();
          infoWindow.open(map, marker);
          MapComponents.activeInfoWindow = infoWindow;
        });
      }
      return markers;
    })
  );
  mapComponents.mapMarkers = markersArrayForEachReports.flat();
}

export function filterReports(reports, uncheckedCategories, numberOfMonths) {
  // Only check first report because all reports have same date if in same file
  const reportsDate = new Date(reports[0].timestamp * 1000);

  if (reports.length !== 0 && !isReportwithinTimeFrame(reportsDate, numberOfMonths)) {
    return [];
  }

  const filteredReports = reports.filter((report) => {
    if (!report.latitude || !report.longitude) {
      return false;
    }
    return displayCrimeType(uncheckedCategories, report.crimeType);
  });

  return filteredReports;
}

function createMarkers(map, reports) {
  return reports.map(
    (report) =>
      new google.maps.Marker({
        position: {
          lat: Number(report.latitude),
          lng: Number(report.longitude),
        },
        map: map,
        date: new Date(report.timestamp * 1000),
        crimeType: report.crimeType,
      })
  );
}

function displayCrimeType(uncheckedCategories, crimeType) {
  // Don't display if category unchecked
  for (const category of uncheckedCategories) {
    if (crimeType.toLowerCase().includes(category)) {
      return false;
    }
  }
  return true;
}

function isReportwithinTimeFrame(reportsDate, numberOfMonths) {
  const today = new Date();
  const monthDiff =
    (today.getFullYear() - reportsDate.getFullYear()) * 12 +
    today.getMonth() + 1 - reportsDate.getMonth();

  return monthDiff < numberOfMonths;
}

export async function fetchMarkers(map, userReports) {
  userReports.forEach((userReport) => {
    createMarkerForDisplay(map, userReport);
  });

  map.addListener("click", closeActiveWindow);
  document.getElementById("dock").addEventListener("click", closeActiveWindow);
}

function closeActiveWindow() {
  if (MapComponents.activeInfoWindow) {
    MapComponents.activeInfoWindow.close();
    MapComponents.activeInfoWindow = null;
  }
}

function createMarkerForDisplay(map, data) {
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
    closeActiveWindow();
    infoWindow.open(map, marker);
    MapComponents.activeInfoWindow = infoWindow;
  });
}
