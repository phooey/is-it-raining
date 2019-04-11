function showError(message) {
  console.log(message);
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
  if (isNaN(floatNumber) || floatNumber === -1) {
    return "unknown";
  }
  return Number(floatNumber * 100).toFixed(0).toString() + "%";
}

function formatIntensityString(inchesPerHour) {
  if (isNaN(inchesPerHour) || inchesPerHour === -1.0) {
    return "unknown";
  }
  // Convert from inches to millimeters
  return Number(inchesPerHour * 25.4).toFixed(2).toString() + " mm/hour";
}

function displayRainReport(rainReport) {
  document.getElementById("rainReport").style.visibility = "visible";
  document.getElementById("latitude").innerHTML = rainReport.latitude;
  document.getElementById("longitude").innerHTML = rainReport.longitude;
  document.getElementById("answer").innerHTML = formatAnswerString(rainReport.currentPrecipitation);
  document.getElementById("currentPrecipitation").innerHTML = formatPrecipitationString(rainReport.currentPrecipitation);
  document.getElementById("currentProbability").innerHTML = convertToPercentString(rainReport.currentProbability);
  document.getElementById("currentIntensity").innerHTML = formatIntensityString(rainReport.currentIntensity);
  document.getElementById("chanceOfPrecipitationToday").innerHTML = convertToPercentString(rainReport.chanceOfPrecipitationToday);
  document.getElementById("typeOfPrecipitationToday").innerHTML = formatPrecipitationString(rainReport.typeOfPrecipitationToday);
}

function retrieveRainReport(position) {
  var latitude = position.coords.latitude;
  var longitude = position.coords.longitude;
  var xhr = new XMLHttpRequest();
  xhr.open('GET', 'isitraining/?latitude=' + latitude + '&longitude=' + longitude);
  xhr.timeout = 5000;
  xhr.addEventListener("error", function (e) {
    showError("Could not reach service: Error");
  });
  xhr.addEventListener("timeout", function (e) {
    showError("Could not reach service: Timeout");
  });
  xhr.addEventListener("load",  function() {
    if (xhr.status === 200) {
      displayRainReport(JSON.parse(xhr.responseText));
    } else {
      showError("Could not retrieve a rain report, try again later.");
    }
  });
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
