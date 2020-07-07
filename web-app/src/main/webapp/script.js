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
  document.getElementById('report-button').addEventListener('click', showReportForm);
  document.getElementById('back-icon').addEventListener('click', hideReportForm);
  document.getElementById('menu-button').addEventListener('click',
    () => { document.getElementById('menu').style.display = 'block' });
  document.getElementById('close-menu').addEventListener('click',
    () => document.getElementById('menu').style.display = 'none');
};

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
