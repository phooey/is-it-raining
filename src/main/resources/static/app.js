function showError(message) {
  document.getElementById("error").style.visibility = "visible";
  document.getElementById("error").innerHTML = message;
}

function convertToPercentString(chanceOfRain) {
  if (chanceOfRain === -1) {
    return "unknown";
  }
  return Number(chanceOfRain * 100).toFixed(0).toString() + "%";
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
      document.getElementById("rainingCurrently").innerHTML = response.rainingCurrently;
      document.getElementById("chanceOfRain").innerHTML = convertToPercentString(response.chanceOfRainToday);
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
