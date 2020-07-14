function getDirections(directionsService, directionsRenderer, origin, destination) {
  // TODO(ltwashley): Parse destination through geocoder?
  const request = {
    origin: { lat: -33.865143, lng: 151.209900 },
    destination: destination,
    travelMode: 'DRIVING'
  };
  directionsService.route(request, (result, status) => {
    if (status == 'OK') {
      directionsRenderer.setDirections(result);
    }
  });
}
