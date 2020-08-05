export function showReportForm(map, geocoder) {
  document.getElementById("form-container").style.display = "block";
  hideHomeElements();

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
  // TODO(ltwAshley): tap on map should close menu, making this unecessary
  const menuElements = document.getElementsByClassName("menu");

  for (const element of menuElements) {
    element.style.display = "none";
  }
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
