export function setDirections(directionsService, directionsRenderer, origin, destination) {
  const request = {
    // Currently hardcoded because I can't enable geolocation on CRD
    origin: { lat: -33.865143, lng: 151.2099 },
    destination: destination,
    travelMode: "DRIVING",
  };
  directionsService.route(request, (result, status) => {
    if (status == "OK") {
      directionsRenderer.setDirections(result);
      fetch("/analytics", {
        method: "POST",
        route: JSON.stringify(result.)
      })
    } else if (status == "NOT_FOUND") {
      console.log("location could not be geocoded.");
    }
  });
}
