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

let geocoder;

function initMap() {
  geocoder = new google.maps.Geocoder();
  const map = new google.maps.Map(document.getElementById("map"), {
    center: { lat: -34.397, lng: 150.644 },
    zoom: 5,
  });
  const infoWindow = new google.maps.InfoWindow();
  displayUserLocation(map, infoWindow);
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
  // document.getElementById("report-form").style.display = "none";
  document.getElementById('report-button').addEventListener('click', showReportForm);
  document.getElementById('submit-button').addEventListener('click', geocodeAddress);
};

function showReportForm() {
  document.getElementById("report-form").style.display = "block";
}

// This currently gets the address from the report form's location field (no autopopulate, no map picker)
function geocodeAddress() {
  const address = document.getElementById('location-input').value;
  geocoder.geocode({ 'address': address }, function (results, status) {
    if (status == 'OK') {
      const coordinates = results[0].geometry.location;
      const data = reportFormToURLQuery(coordinates.lat(), coordinates.lng());
      postUserReport(data);
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

async function postUserReport(data) {
  const url = await fetchBlobstoreUrl();
  fetch(url, { method: 'POST', body: data });
}


async function fetchBlobstoreUrl() {
  const response = await fetch('/blobstore-upload-url');
  const imageURL = await response.text();
  return imageURL;
}
