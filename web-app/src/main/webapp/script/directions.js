export async function setDirections(directionsService, directionsRenderer, origin, destination) {
  const request = {
    // Currently hardcoded because I can't enable geolocation on CRD
    origin: { lat: 51.5196, lng: -0.1025 },
    destination: { lat: 51.5141, lng: -0.0876 },
    travelMode: "DRIVING",
  };
  const analytics = directionsService.route(request, async (result, status) => {
    if (status == "OK") {
      directionsRenderer.setDirections(result);

      const response = await fetch("/analytics", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(result.routes[0].overview_path)
      }); 
      return await response.json();
    } else if (status == "NOT_FOUND") {
      console.log("location could not be geocoded.");
    }
    return null;
  });
  return analytics;
}
