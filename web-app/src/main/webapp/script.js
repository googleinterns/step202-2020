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

window.onload = async () => {
  const geocoder = new google.maps.Geocoder();
  document.getElementById('report-button').addEventListener('click', showReportForm);
  document.getElementById('back-icon').addEventListener('click', hideReportForm);
  document.getElementById('submit-button').addEventListener('click', () => postUserReport(geocoder));
  document.getElementById('menu-button').addEventListener('click',
    () => { document.getElementById('menu').style.display = 'block' });
  document.getElementById('close-menu').addEventListener('click',
    () => document.getElementById('menu').style.display = 'none');
  const map = initMap();
  fetchMarkers(map);
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

function displayUserLocation(map) {
  const infoWindow = new google.maps.InfoWindow();

  if (!navigator.geolocation) {
    showMessageOnInfoWindow(
      "Error: Your browser doesn't support geolocation.",
      map.getCenter(), map, infoWindow);
    return;
  }

  navigator.geolocation.getCurrentPosition(
    (position) => {
      const userPosition = {
        lat: position.coords.latitude,
        lng: position.coords.longitude,
      };

      const marker = new google.maps.Marker({
        position: userPosition,
        map: map,
      });
      map.setCenter(userPosition);
    },
    () => {
      showMessageOnInfoWindow(
        "Error: The Geolocation service failed.",
        map.getCenter(), map, infoWindow);
    }
  );
}

function showMessageOnInfoWindow(message, position, map, infoWindow) {
  infoWindow.setPosition(position);
  infoWindow.setContent(message);
  infoWindow.open(map);
}

function showReportForm() {
  document.getElementById('form-container').style.display = 'block';
  const homeElements = document.getElementsByClassName('home');
  for (const element of homeElements) {
    element.style.display = 'none';
  }
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
  const FILE_NAMES = ['2019_12_london', '2020_01_london', '2020_02_london', '2020_03_london', '2020_04_london', '2020_05_london']
  for (const file_name of FILE_NAMES) {
    const data = await fetch('../data/' + file_name + '.json');
    const reports = await data.json();
    for (report of reports) {
      new google.maps.Marker({
        position: {
          lat: Number(report.latitude),
          lng: Number(report.longitude)
        }, map: map
      });
    };
  }
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
