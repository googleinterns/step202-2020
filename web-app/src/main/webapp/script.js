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

function initMap() {
  const map = new google.maps.Map(document.getElementById("map"), {
    center: { lat: -34.397, lng: 150.644 },
    zoom: 5,
  });
  return map;
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

window.onload = () => {
  document.getElementById('form-container').style.display = 'none';
  document.getElementById('report-button').addEventListener('click', showReportForm);
  const map = initMap();
  const categories = Array.from(document.getElementsByClassName('category'));
  const timeFrames = Array.from(document.getElementsByClassName('time-frame'));
  categories.map(categoryElement => categoryElement.addEventListener('change', () => { loadPoliceReports(map) }));
  timeFrames.map(timeFrameElement => timeFrameElement.addEventListener('change', () => { loadPoliceReports(map) }));
  loadPoliceReports(map);
  displayUserLocation(map);
};

function showReportForm() {
  document.getElementById("form-container").style.display = "block";
}

async function loadPoliceReports(map) {
  // Clear all markers on the map
  mapMarkers.map(marker => marker.setMap(null));
  mapMarkers.length = 0;

  const FILE_NAMES = ['2019_12_london', '2020_01_london', '2020_02_london', '2020_03_london', '2020_04_london', '2020_05_london']
  const uncheckedCategoriesElement = Array.from(document.querySelectorAll("input.category:not(:checked)"));
  const uncheckedCategories = uncheckedCategoriesElement.map(element => element.value);
  const numberofMonths = Number(document.querySelector("input.time-frame:checked").value);

  for (const file_name of FILE_NAMES) {
    createPoliceReportMarkers(map, file_name, uncheckedCategories, numberofMonths);
  }
}

async function createPoliceReportMarkers(map, file_name, uncheckedCategories, numberofMonths) {
  const data = await fetch('../data/' + file_name + '.json');
  const reports = await data.json();
  
  if (reports.length != 0 && !isReportwithinTimeFrame(reports[0].month, numberofMonths)) {
    // Only check first report because all reports have same date if in same file
    console.log("reach here");
    return;
  }

  const filteredReports = reports.filter(report => {
    if (!report.latitude || !report.longitude) {
      return false;
    }
    return displayCrimeType(uncheckedCategories, report.crimeType);
  });

  const markers = filteredReports.map(report => new google.maps.Marker({
    position: {
      lat: Number(report.latitude),
      lng: Number(report.longitude)
    }, map: map
  }));

  mapMarkers = mapMarkers.concat(markers);
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

function isReportwithinTimeFrame(reportDate, numberofMonths) {
  const month = Number(reportDate.substring(5, 7));
  const year = Number(reportDate.substring(0, 4));

  const today = new Date();
  const monthDiff = (today.getFullYear() - year) * 12 + today.getMonth() + 1 - month;

  return (monthDiff < numberofMonths);
}
