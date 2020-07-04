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

let map;

function initMap() {
  map = new google.maps.Map(document.getElementById("map"), {
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
  document.getElementById("form-container").style.display = "none";
  document.getElementById('report-button').addEventListener('click', showReportForm);
  loadPoliceReports();
};

function showReportForm() {
  document.getElementById("form-container").style.display = "block";
}

async function loadPoliceReports() {
  const FILE_NAMES = ['2019_12_london', '2020_01_london', '2020_02_london', '2020_03_london', '2020_04_london', '2020_05_london']
  for (const file_name of FILE_NAMES) {
    const data = await fetch('../data/' + file_name + '.json');
    const reports = await data.json();
    for (report of reports) {
      new google.maps.Marker({
        position: {
          lat: Number(report.Latitude),
          lng: Number(report.Longitude)
        }, map: map
      });
    };
  }
}
