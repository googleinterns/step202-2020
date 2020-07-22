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

import { fetchAndParseJson, loadPoliceReports, fetchMarkers } from "/script/loadData.js";
import { postUserReport } from "/script/postUserData.js"
import { setDirections } from "/script/directions.js";

let mapMarkers = [];

window.onload = async () => {
  const geocoder = new google.maps.Geocoder();
  const directionsService = new google.maps.DirectionsService();
  const directionsRenderer = new google.maps.DirectionsRenderer();
  const map = initMap();
  // Search bar
  directionsRenderer.setMap(map);
  document.getElementById("search-location").addEventListener("keydown", (e) => {
    if (e.code === "Enter") {
      e.preventDefault();
      setDirections(
        directionsService,
        directionsRenderer,
        getUserLocation(),
        document.getElementById("search-location").value
      );
    }
  });
  
  document
    .getElementById("report-button")
    .addEventListener("click", () => showReportForm(map, geocoder));
  document.getElementById("back-icon").addEventListener("click", () => {
    hideReportForm();
    document.getElementById("report-form").reset();
  });
  document.getElementById("map-icon").addEventListener("click", () => hideReportForm);
  document
    .getElementById("submit-button")
    .addEventListener("click", () => postUserReport(geocoder));
  document.getElementById("menu-button").addEventListener("click", () => {
    document.getElementById("menu").style.display = "block";
  });
  document
    .getElementById("close-menu")
    .addEventListener("click", () => (document.getElementById("menu").style.display = "none"));

  const userReports = await fetchAndParseJson("/report");
  fetchMarkers(map, userReports);
  
  const timeFrameOptions = document.getElementById("time-frame-options");
  timeFrameOptions.addEventListener('change', () => { loadPoliceReports(map) });
  const categoryOptions = document.getElementById("category-options");
  categoryOptions.addEventListener('change', () => { loadPoliceReports(map) });
  loadPoliceReports(map);
  
  displayUserLocation(map);
  const loginStatus = await fetchAndParseJson("/login");
  setLoginStatus(loginStatus);
};

function initMap() {
  const map = new google.maps.Map(document.getElementById("map"), {
    center: { lat: 51.5074, lng: -0.1278 },
    zoom: 13,
    disableDefaultUI: true,
  });
  return map;
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
  );
}

function displayUserLocation(map) {
  const infoWindow = new google.maps.InfoWindow();

  if (!navigator.geolocation) {
    showMessageOnInfoWindow(
      "Error: Your browser doesn't support geolocation.",
      map.getCenter(),
      map,
      infoWindow
    );
    return;
  }

  const userPosition = getUserLocation();
  if (userPosition === null) {
    showMessageOnInfoWindow("Please enable location services.", map.getCenter(), map, infoWindow);
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
  infoWindow.setContent(`<h3>${message}</h3>`);
  infoWindow.open(map);
}

function showReportForm(map, geocoder) {
  document.getElementById("form-container").style.display = "block";
  const homeElements = document.getElementsByClassName("home");
  for (const element of homeElements) {
    element.style.display = "none";
  }

  geocoder.geocode({ location: map.getCenter() }, (results, status) => {
    if (status === "OK") {
      if (results[0]) {
        document.getElementById("location-input").value = results[0].formatted_address;
      } else {
        console.error("No results found");
      }
    } else {
      console.error("Geocoder failed due to: " + status);
    }
  });
}

function hideReportForm() {
  document.getElementById("form-container").style.display = "none";
  const homeElements = document.getElementsByClassName("home");
  for (const element of homeElements) {
    element.style.display = "block";
  }
}

function setLoginStatus(loginStatus) {
  const loginLogout = document.getElementById("login-logout");
  if (loginStatus.loggedIn) {
    loginLogout.innerText = "Logout";
  } else {
    loginLogout.innerText = "Login";
  }
  loginLogout.addEventListener("click", () => {
    location.replace(loginStatus.url);
  });
}
