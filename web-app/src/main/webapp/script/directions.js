export async function setDirections(directionsService, directionsRenderer, origin, destination) {
  const request = {
    // Currently hardcoded because I can't enable geolocation on CRD
    origin: { lat: 51.5196, lng: -0.1025 },
    destination: { lat: 51.5141, lng: -0.0876 },
    travelMode: "DRIVING",
  };
  directionsService.route(request, async (result, status) => {
    if (status == "OK") {
      directionsRenderer.setDirections(result);

      const response = await fetch("/analytics", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(result.routes[0].overview_path)
      });
      const analytics = await response.json();
      generateAnalyticsHtml(analytics);
    } else if (status == "NOT_FOUND") {
      console.log("location could not be geocoded.");
    }
  });
}

function generateAnalyticsHtml(analytics) {
  document.getElementById("analytics-button").style.display = "block";
  document.getElementById("num-reports").innerHTML =
    `<p>Number of reports near the route: ${analytics.numReports}</p>`;

  let frequentCrimes = "<ol>";
  for (const crimeType of analytics.frequentTypes) {
    frequentCrimes += `<li>${crimeType}</li>`
  }
  frequentCrimes += "</ol>";
  document.getElementById("report-types").innerHTML = frequentCrimes;
}