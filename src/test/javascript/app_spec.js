describe("The app.js function", function() {
  
  describe("showError", function() {
    it("should display an error message in the 'error' div and make it visible", function () {
      // Given
      var element = document.createElement('div');
      element.setAttribute("style", "visibility: hidden");
      element.setAttribute("id", "error");
      spyOn(document, "getElementById").and.returnValue(element);
      var message = "error message";
      
      // When
      showError(message);
      
      // Then
      expect(element.getAttribute("style")).toEqual('visibility: visible;');
      expect(element.innerHTML).toEqual(message);
    });
  });

  describe("formatAnswerString", function() {
    it("should return 'Yes' if currentPrecipitation is rain", function () {
      expect(formatAnswerString("rain")).toEqual("Yes");
    });

    it("should return 'No' if currentPrecipitation is snow", function () {
      expect(formatAnswerString("snow")).toEqual("No");
    });

    it("should return 'No' if currentPrecipitation is sleet", function () {
      expect(formatAnswerString("sleet")).toEqual("No");
    });

    it("should return 'No' if currentPrecipitation is none", function () {
      expect(formatAnswerString("none")).toEqual("No");
    });

    it("should return 'Unknown' if currentPrecipitation has any other value", function () {
      expect(formatAnswerString("test")).toEqual("Unknown");
    });
  });

  describe("formatPrecipitationString", function() {
    it("should return 'rain' if typeOfPrecipitation is rain", function () {
      expect(formatPrecipitationString("rain")).toEqual("rain");
    });

    it("should return 'snow' if typeOfPrecipitation is rain", function () {
      expect(formatPrecipitationString("snow")).toEqual("snow");
    });

    it("should return 'sleet' if typeOfPrecipitation is rain", function () {
      expect(formatPrecipitationString("sleet")).toEqual("sleet");
    });

    it("should return 'no precipitation' if typeOfPrecipitation is none", function () {
      expect(formatPrecipitationString("none")).toEqual("no precipitation");
    });

    it("should return 'precipitation' if typeOfPrecipitation is any other value", function () {
      expect(formatPrecipitationString("test")).toEqual("precipitation");
    });
  });

  describe("convertToPercentString", function() {
    it("should return 'unknown' if floatNumber is not a number", function () {
      expect(convertToPercentString("test")).toEqual("unknown");
    });

    it("should return 'unknown' if floatNumber is minus one", function () {
      expect(convertToPercentString(-1.0)).toEqual("unknown");
    });

    it("should return '33%' if floatNumber is 0.333", function () {
      expect(convertToPercentString(0.333)).toEqual("33%");
    });
  });

  describe("formatIntensityString", function() {
    it("should return 'unknown' if inchesPerHour is not a number", function () {
      expect(formatIntensityString("test")).toEqual("unknown");
    });

    it("should return 'unknown' if inchesPerHour is minus one", function () {
      expect(formatIntensityString(-1.0)).toEqual("unknown");
    });

    it("should return '12.7 mm/hour' if inchesPerHour is 0.5", function () {
      expect(formatIntensityString(0.5)).toEqual("12.70 mm/hour");
    });
  });

  describe("displayRainReport", function() {
    var dummyRainReport = {
      longitude: 13.37,
      latitude: 90.01,
      currentPrecipitation: "rain",
      currentProbability: 0.75,
      currentIntensity: 0.5,
      chanceOfPrecipitationToday: 1.0,
      typeOfPrecipitationToday: "rain"
    };

    it("should set the visibility of the rainReport table to visible", function () {
      // Given
      var element = document.createElement('table');
      element.setAttribute("style", "visibility: hidden");
      element.setAttribute("id", "rainReport");
      spyOn(document, "getElementById").and.returnValue(element);

      // When
      displayRainReport(dummyRainReport);

      // Then
      expect(element.getAttribute("style")).toEqual('visibility: visible;');
    });

    it("should set the coordinate values in the longitude and latitude divs", function () {
      // Given
      var longitudeDiv = document.createElement('div');
      longitudeDiv.setAttribute("id", "longitude");
      var latitudeDiv = document.createElement('div');
      latitudeDiv.setAttribute("id", "latitude");
      spyOn(document, "getElementById").and.callFake(function(element) {
        if (element === "latitude"){
            return latitudeDiv;
        } else if (element === "longitude") {
            return longitudeDiv;
        } else {
          return document.createElement('div');
        }
      });

      // When
      displayRainReport(dummyRainReport);

      // Then
      expect(longitudeDiv.innerHTML).toEqual('13.37');
      expect(latitudeDiv.innerHTML).toEqual('90.01');
    });
  });

  describe("retrieveRainReport", function() {

    var eventListener;
    var position;

    beforeEach(function() {
      eventListener = spyOn(XMLHttpRequest.prototype, "addEventListener").and.stub();
      spyOn(XMLHttpRequest.prototype, "open").and.stub();
      spyOn(XMLHttpRequest.prototype, "send").and.stub();

      position = { coords: { latitude: 13.37, longitude: 90.01 }};
    });

    it("should initialize an XMLHttpRequest of type 'GET' to the backend with the provided coordinates and send it", function () {
      // Given

      // When
      retrieveRainReport(position);

      // Then
      expect(XMLHttpRequest.prototype.open.calls.count()).toEqual(1);
      expect(XMLHttpRequest.prototype.open.calls.argsFor(0)).toEqual(['GET', 'isitraining/?latitude=13.37&longitude=90.01']);
      expect(XMLHttpRequest.prototype.send.calls.count()).toEqual(1);
    });

    it("should register a callback for the error event and show an error when it is called", function () {
      // Given
      eventListener.and.callFake(function(event) {
        if (event === "error") {
          arguments[1]("error");
        }
      });
      spyOn(window, "showError").and.stub();

      // When
      retrieveRainReport(position);

      // Then      
      expect(XMLHttpRequest.prototype.addEventListener).toHaveBeenCalledWith("error", jasmine.any(Function));
      expect(window.showError.calls.count()).toEqual(1);
      expect(window.showError.calls.argsFor(0)).toEqual(["Could not reach service: Error"]);
    });

    it("should register a callback for the timeout event and show an error when it is called", function () {
      // Given
      eventListener.and.callFake(function(event) {
        if (event === "timeout") {
          arguments[1]("error");
        }
      });
      spyOn(window, "showError").and.stub();

      // When
      retrieveRainReport(position);

      // Then      
      expect(XMLHttpRequest.prototype.addEventListener).toHaveBeenCalledWith("timeout", jasmine.any(Function));
      expect(window.showError.calls.count()).toEqual(1);
      expect(window.showError.calls.argsFor(0)).toEqual(["Could not reach service: Timeout"]);
    });

    it("should register a callback for the load event and show an error when it is called and the status of the XMLHttpRequest is not 200 OK", function () {
      // Given
      eventListener.and.callFake(function(event) {
        if (event === "load") {
          arguments[1]();
        }
      });
      spyOn(window, "showError").and.stub();

      // When
      retrieveRainReport(position);

      // Then      
      expect(XMLHttpRequest.prototype.addEventListener).toHaveBeenCalledWith("load", jasmine.any(Function));
      expect(window.showError.calls.count()).toEqual(1);
      expect(window.showError.calls.argsFor(0)).toEqual(["Could not retrieve a rain report, try again later."]);
    });

    // TODO:
    // A further test should be written to test that displayRainReport is called from the "load" callback when the
    // XMLHttpRequest has status 200. This is difficult with pure Jasmine, as it doesn't support mcocking properties
    // on Spy objects, and would require something like this: https://github.com/jasmine/jasmine-ajax
  });

  describe("getLocation", function() {
    it("should call showError if 'navigator.geolocation' is not defined", function () {
      // Given
      spyOnProperty(navigator, "geolocation", "get").and.returnValue(undefined);
      spyOn(window, "showError").and.stub();

      // When
      getLocation();

      // Then
      expect(window.showError.calls.count()).toEqual(1);
      expect(window.showError.calls.argsFor(0)).toEqual(["This page will not function without GeoLocation support."]);
    });

    it("should register retrieveRainReport as callback for getCurrentPosition", function () {
      // Given
      spyOn(navigator.geolocation,"getCurrentPosition").and.stub();
      
      // When
      getLocation();

      // Then     
      expect(navigator.geolocation.getCurrentPosition.calls.count()).toEqual(1);
      expect(navigator.geolocation.getCurrentPosition.calls.argsFor(0)[0]).toEqual(window.retrieveRainReport);
    });

    it("should call showError if the error callback registered for getCurrentPosition is called", function () {
      // Given
      spyOn(navigator.geolocation, "getCurrentPosition").and.callFake(function() {
        arguments[1]("error");
      });
      spyOn(window, "showError").and.stub();

      // When
      getLocation();

      // Then
      expect(window.showError.calls.count()).toEqual(1);
      expect(window.showError.calls.argsFor(0)).toEqual(["You need to share your location for this page to work."]);
    });
  });
});