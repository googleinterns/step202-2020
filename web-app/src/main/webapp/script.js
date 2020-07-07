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

window.onload = async () => {
  document.getElementById('report-button').addEventListener('click', showReportForm);
  document.getElementById('back-icon').addEventListener('click', hideReportForm);
  document.getElementById('submit-button').addEventListener('click', postUserReport);
  document.getElementById('menu-button').addEventListener('click',
    () => { document.getElementById('menu').style.display = 'block' });
  document.getElementById('close-menu').addEventListener('click',
    () => document.getElementById('menu').style.display = 'none');
    const map = initMap();
  loadPoliceReports(map);
  displayUserLocation(map);
  await setLoginStatus();
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

function reportFormToURLQuery() {
  const PARAMS_FORM_MAP = new Map([
    ['title-input', 'title'],
    ['time-input', 'timestamp'],
    ['category-input', 'incidentType'],
    ['description-input', 'description'],
  ]);

  const searchParams = new URLSearchParams();
  for (const [formID, paramName] of PARAMS_FORM_MAP.entries()) {
    const value = document.getElementById(formID).value;
    searchParams.append(paramName, value);
  }

  return searchParams;
}

function postUserReport() {
  const urlQuery = reportFormToURLQuery();
  fetch('/report', {method: 'POST', body: urlQuery}); 
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
