import { fetchAndParseJson } from "/script/loadData.js";

export async function showReportForm(map, geocoder) {
  const loginStatus = await fetchAndParseJson("/login");
  if (!loginStatus.loggedIn) {
    alert("Please log in to post the report!");
    location.replace(loginStatus.url);
  }

  document.getElementById("form-container").style.display = "block";
  hideHomeElements();
  hideOptionMenu();

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

export function hideReportForm() {
  document.getElementById("form-container").style.display = "none";
  showHomeElements();
}

export function showAnalytics() {
  document.getElementById("analytics-container").style.display = "block";
  hideOptionMenu();
  hideHomeElements();
}

export function hideAnalytics() {
  document.getElementById("analytics-container").style.display = "none";
  showHomeElements();
}

function showHomeElements() {
  const homeElements = document.getElementsByClassName("home");
  for (const element of homeElements) {
    element.style.display = "block";
  }
}

function hideHomeElements() {
  const homeElements = document.getElementsByClassName("home");
  for (const element of homeElements) {
    element.style.display = "none";
  }
}

export function hideOptionMenu() {
  document.getElementById("menu").style.display = "none";
}
