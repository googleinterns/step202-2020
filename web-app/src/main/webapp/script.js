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

function initMap() {
  const map = new google.maps.Map(document.getElementById("map"), {
    center: { lat: -34.397, lng: 150.644 },
    zoom: 5,
  });
  const infoWindow = new google.maps.InfoWindow();
  displayUserLocation(map, infoWindow);
  fetchMarkers(map);
}

async function fetchMarkers(map) {
  const response = await fetch('/report');
  const markers = await response.json();
  markers.forEach((marker) => {
    console.log(marker);
    createMarkerForDisplay(map, marker);
  });
}

function createMarkerForDisplay(map, data) {
  const marker =
    new google.maps.Marker({ position: { lat: data.latitude, lng: data.longitude }, map: map });

  let infoParagraph = document.createElement("div");
  let readableTimestamp = new Date(data.timestamp);
  infoParagraph.innerHTML = `
    <h1>${data.title}</h1>
    <h2>${readableTimestamp}</h2>
    <p>${data.description}</p>
  `;
  if (data.imageUrl) {
    infoParagraph.insertAdjacentHTML('beforeend',
      `<img src="${data.imageUrl}" alt="User-submitted image of incident">`)
  }
  const infoWindow = new google.maps.InfoWindow({ content: infoParagraph });
  marker.addListener('click', () => {
    infoWindow.open(map, marker);
  });
}

function displayUserLocation(map, infoWindow) {
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

window.onload = () => {
  const geocoder = new google.maps.Geocoder();
  document.getElementById('report-button').addEventListener('click', showReportForm);
  document.getElementById('submit-button').addEventListener('click', (event) => postUserReport(geocoder));
};

function showReportForm() {
  document.getElementById("report-form").style.display = "block";
}

// This currently gets the address from the report form's location field (no autopopulate, no map picker)
async function postUserReport(geocoder) {
  const address = document.getElementById('location-input').value;
  geocoder.geocode({ 'address': address }, async function (results, status) {
    if (status == 'OK') {
      const coordinates = results[0].geometry.location;
      const data = reportFormToURLQuery(coordinates.lat(), coordinates.lng());
      const url = await fetchBlobstoreUrl();
      fetch(url, { method: 'POST', body: data });
    } else {
      alert('Geocode was not successful: ' + status);
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
