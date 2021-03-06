import { hideReportForm } from "/script/manipulateUI.js"

export async function postUserReport(geocoder) {
  const missingFields = findMissingFields();
  if (missingFields.length !== 0) {
    alertMissingFields(missingFields);
    return;
  }
  const address = document.getElementById("location-input").value;
  geocoder.geocode({ address: address }, async (results, status) => {
    if (status === "OK") {
      const coordinates = results[0].geometry.location;

      let data;
      try {
        data = reportFormToURLQuery(coordinates.lat(), coordinates.lng());
      } catch (error) {
        console.log(error);
        return;
      }

      const url = await fetchBlobstoreUrl();
      fetch(url, { method: "POST", body: data });
      document.getElementById("report-form").reset();
      hideReportForm();

    } else {
      console.error("Geocode was not successful: " + status);
    }
  });
}

function findMissingFields() {
  const compulsoryFields = document.getElementsByClassName("compulsory");
  let missingFields = Object.values(compulsoryFields).filter(field => !field.value.trim()).map(field => field.name);

  if (document.getElementById("category-input").value === "Category") {
    missingFields.push(document.getElementById("category-input").name);
  }

  return missingFields;
}

function alertMissingFields(missingFields) {
  let errorMessage = "Please fill in the ";
  errorMessage += missingFields.join(', ');
  if (missingFields.length === 1) {
    errorMessage += " field.";
  } else if (missingFields.length > 1) {
    errorMessage += " fields.";
  }
  alert(errorMessage);
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
