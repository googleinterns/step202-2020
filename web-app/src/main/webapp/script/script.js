// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
let mapMarkers = [];

window.onload = async () => {
  const geocoder = new google.maps.Geocoder();
  const directionsService = new google.maps.DirectionsService();
  const directionsRenderer = new google.maps.DirectionsRenderer();
  const map = initMap();
  // Search bar
  document.getElementById('search-location').addEventListener('keydown', (e) => {
    if (e.code === "Enter") {
      e.preventDefault();
      console.log(document.getElementById('search-location').value);
      getDirections(document.getElementById('search-location').value);
    }
  })
  document.getElementById('report-button').addEventListener('click', () => showReportForm(map, geocoder));
  document.getElementById('back-icon').addEventListener('click', () => {
    hideReportForm();
    document.getElementById('report-form').reset();
  }
  );
  document.getElementById('map-icon').addEventListener('click', () => hideReportForm)
  document.getElementById('submit-button').addEventListener('click', () => postUserReport(geocoder));
  document.getElementById('menu-button').addEventListener('click',
    () => { document.getElementById('menu').style.display = 'block' });
  document.getElementById('close-menu').addEventListener('click',
    () => document.getElementById('menu').style.display = 'none');
  fetchMarkers(map);
  const timeFrameOptions = document.getElementById("time-frame-options");
  timeFrameOptions.addEventListener('change', () => { loadPoliceReports(map) });
  const categoryOptions = document.getElementById("category-options");
  categoryOptions.addEventListener('change', () => { loadPoliceReports(map) });
  loadPoliceReports(map);
  displayUserLocation(map);
  await setLoginStatus();
};


function initMap() {
  const map = new google.maps.Map(document.getElementById("map"), {
    center: { lat: -34.397, lng: 150.644 },
    zoom: 5,
  });
  return map;
}

async function fetchMarkers(map) {
  const response = await fetch('/report');
  const markers = await response.json();
  let uiState = { activeInfoWindow: null };

  markers.forEach((marker) => {
    createMarkerForDisplay(map, marker, uiState);
  });

  map.addListener('click', () => {
    if (uiState.activeInfoWindow) {
      uiState.activeInfoWindow.close();
      uiState.activeInfoWindow = null;
    }
  })
}

function createMarkerForDisplay(map, data, uiState) {
  const marker =
    new google.maps.Marker({ position: { lat: data.latitude, lng: data.longitude }, map: map });

  const infoParagraph = document.createElement("div");
  infoParagraph.setAttribute('id', 'info-window');
  const timestamp = new Date(data.timestamp);
  infoParagraph.innerHTML = `
    <h1>${data.title}</h1>
    <p>${timestamp.toLocaleDateString()}, ${timestamp.toLocaleTimeString()}</p>
    <p>${data.description}</p>
  `;

  if (data.imageUrl) {
    infoParagraph.insertAdjacentHTML('beforeend',
      `<img src="${window.location.href}serve?blob-key=${data.imageUrl}"
      id="info-image" alt="User-submitted image of incident">`)
  }

  const infoWindow = new google.maps.InfoWindow({ content: infoParagraph });
  marker.addListener('click', () => {
    if (uiState.activeInfoWindow) {
      uiState.activeInfoWindow.close();
    }
    infoWindow.open(map, marker);
    uiState.activeInfoWindow = infoWindow;
  });

}

function getUserLocation() {
  navigator.geolocation.getCurrentPosition(
    (position) => {
      const userPosition = {
        lat: position.coords.latitude,
        lng: position.coords.longitude,
      };

      return userPosition;
    },
    () => {
      return null;
    }
  )
}

function displayUserLocation(map) {
  const infoWindow = new google.maps.InfoWindow();

  if (!navigator.geolocation) {
    showMessageOnInfoWindow(
      "Error: Your browser doesn't support geolocation.",
      map.getCenter(), map, infoWindow);
    return;
  }

  const userPosition = getUserLocation();
  if (userPosition !== null) {
    showMessageOnInfoWindow(
      "Please enable location services.",
      map.getCenter(), map, infoWindow);
    return;
  }

  const marker = new google.maps.Marker({
    position: userPosition,
    map: map,
  });
  map.setCenter(userPosition);
}

function showMessageOnInfoWindow(message, position, map, infoWindow) {
  infoWindow.setPosition(position);
  infoWindow.setContent(message);
  infoWindow.open(map);
}

function showReportForm(map, geocoder) {
  document.getElementById('form-container').style.display = 'block';
  const homeElements = document.getElementsByClassName('home');
  for (const element of homeElements) {
    element.style.display = 'none';
  }

  geocoder.geocode({ 'location': map.getCenter() }, (results, status) => {
    if (status === 'OK') {
      if (results[0]) {
        document.getElementById('location-input').value = results[0].formatted_address;
      } else {
        console.error('No results found');
      }
    } else {
      console.error('Geocoder failed due to: ' + status);
    }
  })
}

function hideReportForm() {
  document.getElementById('form-container').style.display = 'none';

  const homeElements = document.getElementsByClassName('home');
  for (const element of homeElements) {
    element.style.display = 'block';
  }
}

// This currently gets the address from the report form's location field (no autopopulate, no map picker)
async function postUserReport(geocoder) {
  document.getElementById('report-form').reset();

  const address = document.getElementById('location-input').value;
  geocoder.geocode({ 'address': address }, async (results, status) => {
    if (status === 'OK') {
      const coordinates = results[0].geometry.location;
      const data = reportFormToURLQuery(coordinates.lat(), coordinates.lng());
      const url = await fetchBlobstoreUrl();
      fetch(url, { method: 'POST', body: data });
    } else {
      console.error('Geocode was not successful: ' + status);
    }
  })
}

function reportFormToURLQuery(latitude, longitude) {
  const PARAMS_FORM_MAP = new Map([
    ['title-input', 'title'],
    ['time-input', 'timestamp'],
    ['category-input', 'incidentType'],
    ['description-input', 'description'],
  ]);

  const formData = new FormData();
  for (const [formID, paramName] of PARAMS_FORM_MAP.entries()) {
    const value = document.getElementById(formID).value;
    formData.append(paramName, value);
  }

  formData.append('latitude', latitude);
  formData.append('longitude', longitude);
  formData.append('image', document.getElementById('attach-image').files[0]);

  return formData;
}

async function fetchBlobstoreUrl() {
  const response = await fetch('/blobstore-upload-url');
  const imageURL = await response.text();
  return imageURL;
}

async function loadPoliceReports(map) {
  // Clear all markers on the map
  for (const marker of mapMarkers) {
    marker.setMap(null);
  }
  mapMarkers = [];

  const FILE_NAMES = ['2019_12_london', '2020_01_london', '2020_02_london', '2020_03_london', '2020_04_london', '2020_05_london']
  const uncheckedCategoriesElement = Array.from(document.querySelectorAll("input.category:not(:checked)"));
  const uncheckedCategories = uncheckedCategoriesElement.map(element => element.value);
  const numberOfMonths = Number(document.querySelector("input.time-frame:checked").value);

  const markersArrayForEachReports = await Promise.all(FILE_NAMES.map((file_name) =>
    createPoliceReportMarkers(map, file_name, uncheckedCategories, numberOfMonths)));
  mapMarkers = markersArrayForEachReports.flat();
}

async function createPoliceReportMarkers(map, file_name, uncheckedCategories, numberOfMonths) {
  const data = await fetch('../data/' + file_name + '.json');
  const reports = await data.json();

  const reportsDate = new Date();
  // Only check first report because all reports have same date if in same file
  reportsDate.setMonth(Number(reports[0].yearMonth.substring(5, 7)));
  reportsDate.setYear(Number(reports[0].yearMonth.substring(0, 4)));

  if (reports.length !== 0 && !isReportwithinTimeFrame(reportsDate, numberOfMonths)) {
    return [];
  }

  const filteredReports = reports.filter((report) => {
    if (!report.latitude || !report.longitude) {
      return false;
    }
    return displayCrimeType(uncheckedCategories, report.crimeType);
  });

  const markers = filteredReports.map((report) => new google.maps.Marker({
    position: {
      lat: Number(report.latitude),
      lng: Number(report.longitude)
    }, map: map
  }));

  return markers;
}

function displayCrimeType(uncheckedCategories, crimeType) {
  // Don't display if category unchecked
  for (category of uncheckedCategories) {
    if (crimeType.toLowerCase().includes(category)) {
      return false;
    }
  }
  return true;
}

function isReportwithinTimeFrame(reportsDate, numberOfMonths) {
  const today = new Date();
  const monthDiff = (today.getFullYear() - reportsDate.getFullYear()) * 12 + today.getMonth() + 1 - reportsDate.getMonth();

  return monthDiff < numberOfMonths;
}

async function setLoginStatus() {
  const response = await fetch('/login');
  const loginStatus = await response.json();

  const loginLogout = document.getElementById('login-logout');
  if (loginStatus.loggedIn) {
    loginLogout.innerText = "Logout";
  } else {
    loginLogout.innerText = "Login";
  }
  loginLogout.addEventListener('click', () => { location.replace(loginStatus.url) });
}
