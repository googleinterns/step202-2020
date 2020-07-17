export function setDirections(directionsService, directionsRenderer, origin, destination) {
  const request = {
    // Currently hardcoded because I can't enable geolocation on CRD
    origin: { lat: 51.5196, lng: -0.1025 },
    destination: { lat: 51.5141, lng: -0.0876 },
    travelMode: "WALKING",
  };

  directionsService.route(request, (result, status) => {
    if (status == "OK") {
      directionsRenderer.setDirections(result);
      for (let i = 1; i < 20; i++) {
        console.log(result.routes[0].overview_path[i].toJSON().lat);
      }
    } else if (status == "NOT_FOUND") {
      console.log("location could not be geocoded.");
    }
  });
}
