function showError(message) {
  document.getElementById("error").style.visibility = "visible";
  document.getElementById("error").innerHTML = message;
}

function formatAnswerString(currentPrecipitation) {
  var answer;
  switch (currentPrecipitation) {
    case "rain":
      answer = "Yes";
      break;
    case "sleet":
    case "snow":
    case "none":
      answer = "No";
      break;
    default:
      answer = "Unknown";
      break;
  }
  return answer;
}

function formatPrecipitationString(typeOfPrecipitation) {
  var precipitation;
  switch (typeOfPrecipitation) {
    case "rain":
    case "sleet":
    case "snow":
      precipitation = typeOfPrecipitation;
      break;
    case "none":
      precipitation = "no precipitation";
      break;
    default:
      precipitation = "precipitation";
      break;
  }
  return precipitation;
}

function convertToPercentString(floatNumber) {
  if (floatNumber === -1) {
    return "unknown";
  }
  return Number(floatNumber * 100).toFixed(0).toString() + "%";
}

function formatIntensityString(floatNumber) {
  if (floatNumber === -1) {
    return "unknown";
  }
  // Convert from inches to millimeters
  return Number(floatNumber * 25.4).toFixed(2).toString() + " mm/hour";
}

function retrieveRainReport(position) {
  var latitude = position.coords.latitude;
  var longitude = position.coords.longitude;
  var xhr = new XMLHttpRequest();
  xhr.open('GET', 'isitraining/?latitude=' + latitude + '&longitude=' + longitude);
  xhr.timeout = 5000;
  xhr.onerror = function (e) {
    showError("Could not reach service: Error");
  }
  xhr.ontimeout = function (e) {
    showError("Could not reach service: Timeout");
  };
  xhr.onload = function() {
    if (xhr.status === 200) {
      var response = JSON.parse(xhr.responseText);
      document.getElementById("rainReport").style.visibility = "visible";
      document.getElementById("latitude").innerHTML = response.latitude;
      document.getElementById("longitude").innerHTML = response.longitude;
      document.getElementById("answer").innerHTML = formatAnswerString(response.currentPrecipitation);
      document.getElementById("currentPrecipitation").innerHTML = formatPrecipitationString(response.currentPrecipitation);
      document.getElementById("currentProbability").innerHTML = convertToPercentString(response.currentProbability);
      document.getElementById("currentIntensity").innerHTML = formatIntensityString(response.currentIntensity);
      document.getElementById("chanceOfPrecipitationToday").innerHTML = convertToPercentString(response.chanceOfPrecipitationToday);
      document.getElementById("typeOfPrecipitationToday").innerHTML = formatPrecipitationString(response.typeOfPrecipitationToday);
    } else {
      showError("Could not retrieve a rain report, try again later.");
    }
  };
  xhr.send();
}

function getLocation() {
  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(retrieveRainReport, function (error) {
      showError("You need to share your location for this page to work.");
    });
  } else {
    showError("This page will not function without GeoLocation support.");
  }
}

window.onload = getLocation();