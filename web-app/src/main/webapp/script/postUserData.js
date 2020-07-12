// This currently gets the address from the report form's location field (no autopopulate, no map picker)
async function postUserReport(geocoder) {
  document.getElementById("report-form").reset();

  const address = document.getElementById("location-input").value;
  geocoder.geocode({ address: address }, async (results, status) => {
    if (status === "OK") {
      const coordinates = results[0].geometry.location;
      const data = reportFormToURLQuery(coordinates.lat(), coordinates.lng());
      const url = await fetchBlobstoreUrl();
      fetch(url, { method: "POST", body: data });
    } else {
      console.error("Geocode was not successful: " + status);
    }
  });
}

function reportFormToURLQuery(latitude, longitude) {
  const PARAMS_FORM_MAP = new Map([
    ["title-input", "title"],
    ["time-input", "timestamp"],
    ["category-input", "incidentType"],
    ["description-input", "description"],
  ]);

  const formData = new FormData();
  for (const [formID, paramName] of PARAMS_FORM_MAP.entries()) {
    const value = document.getElementById(formID).value;
    formData.append(paramName, value);
  }

  formData.append("latitude", latitude);
  formData.append("longitude", longitude);
  formData.append("image", document.getElementById("attach-image").files[0]);

  return formData;
}

async function fetchBlobstoreUrl() {
  const response = await fetch("/blobstore-upload-url");
  const imageURL = await response.text();
  return imageURL;
}
