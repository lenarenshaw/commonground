// Global reference to the canvas element.
let canvas;
// Global reference to the canvas' context.
let ctx;

// Global refrence to scale variables for map
let latScale;
let lonScale;
let latShift;
let lonShift;
const WIDTH = 800;
const HEIGHT = 650;
const ZOOMFACTOR = 1/400;
let lat1;
let lon1;
let lat2;
let lon2;
let zoomlevel = 2;
let paintingFlag = false ; //true when canvas is being painted

// for drawing nearest
let nearestFlag = false;
let nearestX;
let nearestY;

// for drawing route
let routePath;
let routeFlag = false;

// for drawing route with clicked points
let clickCount = 0;
let click1Lat;
let click1Lon;
let click2Lat;
let click2Lon;

// TODO: test speeding up with paint flag


$(document).ready(() => {
    // Setting up the canvas.
    canvas = $('#map')[0];

    canvas.width = WIDTH;
    canvas.height = HEIGHT;
    ctx = canvas.getContext("2d");

    // BROWN CAMPUS COORDINATES
    lat1 = 41.835;
    lon1 = -71.40946;
    lat2 = 41.82;
    lon2 = -71.391;

    paintMap();
    routeButton();
    clickNearest();
    draggableMap();
    zoomButtons();
    zoomMouse();
    cacheTiles();
});

// TODO: make cacheing work, currently not functional
const cacheTiles = () => {
  const tiles = [new Tile(1, 2)];
}

class Tile {
   constructor(topleft, ways) {
     this.topleft = topleft;
     this.ways = ways;
   }
}

const zoomMouse = () => {
  let flag = 1;
  canvas.addEventListener('mousewheel', function (e) {

    let delta = Math.max(-1, Math.min(1, (e.wheelDelta || -e.detail)));

      if (delta < 0 && !paintFlag && zoomlevel > 0) {
        lat1 = lat1 - ZOOMFACTOR;
        lat2 = lat2 + ZOOMFACTOR;
        lon1 = lon1 + ZOOMFACTOR * WIDTH/HEIGHT;
        lon2 = lon2 - ZOOMFACTOR * WIDTH/HEIGHT;
        zoomlevel = zoomlevel - 1;
        paintMap();

      } else if(delta > 0 && !paintFlag && zoomlevel < 12) {
        lat1 = lat1 + ZOOMFACTOR;
        lat2 = lat2 - ZOOMFACTOR;
        lon1 = lon1 - ZOOMFACTOR * WIDTH/HEIGHT;
        lon2 = lon2 + ZOOMFACTOR * WIDTH/HEIGHT;

        zoomlevel = zoomlevel + 1;

        paintMap();
      }
  });
}

const zoomButtons = () => {

  let zoomin = $('#in')[0];
  let zoomout = $('#out')[0];
  zoomin.addEventListener("click", function() {
    if (!paintFlag && zoomlevel > 0) {
      lat1 = lat1 - ZOOMFACTOR;
      lat2 = lat2 + ZOOMFACTOR;
      lon1 = lon1 + ZOOMFACTOR * WIDTH/HEIGHT;
      lon2 = lon2 - ZOOMFACTOR * WIDTH/HEIGHT;
      zoomlevel = zoomlevel - 1;
      paintMap();
    }
   });

  zoomout.addEventListener("click", function() {
    if (!paintFlag && zoomlevel < 12) {
      lat1 = lat1 + ZOOMFACTOR;
      lat2 = lat2 - ZOOMFACTOR;
      lon1 = lon1 - ZOOMFACTOR * WIDTH/HEIGHT;
      lon2 = lon2 + ZOOMFACTOR * WIDTH/HEIGHT;
      zoomlevel = zoomlevel + 1;
      paintMap();
    }
  });
}

const draggableMap = () => {
  let mouseDown = false;
  let mouseX;
  let mouseY;
  let t;

  canvas.addEventListener("mousedown", function(e){
    mouseDown = true;
    mouseX = e.clientX;
    mouseY = e.clientY;

  }, false);

  window.addEventListener("mouseup", function(e){
    mouseDown = false;
    clearTimeout(t);
  }, false);

  canvas.addEventListener("mousemove", function(e){
    if (mouseDown && !paintFlag){
      let diffX = e.clientX - mouseX;
      let diffY = e.clientY - mouseY;
      mouseX = e.clientX;
      mouseY = e.clientY;

      lat1 = lat1 - diffY / latScale;
      lat2 = lat2 - diffY / latScale;
      lon1 = lon1 - diffX / lonScale;
      lon2 = lon2 - diffX / lonScale;
      paintMap();

      // mouseDown = false;
      // t = setTimeout(function(){mouseDown = true;}, 0);
    }
  }, false);
}

const paintMap = () => {

  paintFlag = true;
  // Scale numbers for map
  latScale = WIDTH/(lon1-lon2);
  lonScale = HEIGHT/(lat1-lat2);
  // latShift = -1 * lat2;
  latShift = -1 * lat1;
  lonShift = -1 * lon1;

  const postParameters = {lat1: lat1, lon1: lon1, lat2: lat2, lon2: lon2, zoomlevel: zoomlevel};
  $.post("/maps/drawmap", postParameters, responseJSON => {

    const responseObject = JSON.parse(responseJSON);
    const ways = responseObject.ways;

    // Streets with names drawn on the map
    let toDraw;
    let namedStreets = [];


    ctx.fillStyle = "grey";
    ctx.fillRect(0, 0, WIDTH, HEIGHT)
    ctx.stroke();

    // Loop through ways and draw all on map
    for (let i = 0; i < ways.length; i++) {
      toDraw = false;

      if (ways[i][1] == "primary"){
        ctx.beginPath();
        ctx.strokeStyle = "yellow";
        ctx.lineWidth = 6/zoomlevel;
        toDraw = true;

      } else if (ways[i][1] === "secondary" || ways[i][1] === "tertiary" || ways[i][1] === "primary"){

        // Draw name of street
        if (namedStreets.indexOf(ways[i][0]) < 0){
          ctx.font = "12px Arial";
          ctx.textAlign = 'center';
          ctx.textBaseline = 'middle'; 
          ctx.fillStyle = "black";
          namedStreets.push(ways[i][0]);
          // TODO: comment this in to draw name
          // ctx.fillText(ways[i][0], (parseFloat(ways[i][2]) + latShift) * latScale, (parseFloat(ways[i][3]) + lonShift) * lonScale);
        }

        ctx.beginPath();
        ctx.strokeStyle = "yellow";
        ctx.lineWidth = 3/zoomlevel;
        toDraw = true;
      } else if (ways[i][1] === "residential"){
        ctx.beginPath();
        ctx.strokeStyle = "white";
        ctx.lineWidth = 2/zoomlevel;
        toDraw = true;
      } else if (zoomlevel < 5) {
        ctx.beginPath();
        ctx.strokeStyle = "white";
        ctx.lineWidth = 2/zoomlevel;
        toDraw = true;
      }

      //TODO: DECIDE IF ALWAYS WANT TO DRAW
      if (toDraw){
        ctx.moveTo(lonToX(parseFloat(ways[i][3])), latToY(parseFloat(ways[i][2])));
        ctx.lineTo(lonToX(parseFloat(ways[i][5])), latToY(parseFloat(ways[i][4])));
        ctx.stroke();
      }
    }

    if (nearestFlag) {
      drawNearest();
    }

    if (routeFlag){
      drawRoute();
    }
    paintFlag = false;
  });
}

const routeButton = () => {
  const routeButton = $('#routeButton')[0];
  routeButton.addEventListener("click", function() {
    const $routeStart1 = $("#routeStart1")[0].value;
    const $routeStart2 = $("#routeStart2")[0].value;
    const $routeEnd1 = $("#routeEnd1")[0].value;
    const $routeEnd2 = $("#routeEnd2")[0].value;

    if (($routeStart1 === "" || $routeStart2 === "" || $routeEnd1 === "" || $routeEnd2 === "") && clickCount === 2) {
      const postParameters = {
        lat1: click1Lat,
        lon1: click1Lon,
        lat2: click2Lat,
        lon2: click2Lon
      };
      $.post("/maps/route/coords", postParameters, responseJSON => {
        const responseObject = JSON.parse(responseJSON);
        const message = responseObject.message;
        if (message !== "") {
          alert(message);
        } else {
          const path = responseObject.path;
          routePath = path;
          routeFlag = true;
          paintMap();
        }
      });
    } else {
      const postParameters = {
        routeStart1: $routeStart1,
        routeStart2: $routeStart2,
        routeEnd1: $routeEnd1,
        routeEnd2: $routeEnd2
      };
      $.post("/maps/route/names", postParameters, responseJSON => {
        const responseObject = JSON.parse(responseJSON);
        const message = responseObject.message;
        if (message !== "") {
          alert(message);
        } else {
          const path = responseObject.path;
          routePath = path;
          routeFlag = true;
          paintMap();
        }
      });
    }
  });
}

const drawRoute = () => {
  ctx.strokeStyle = "cyan";
  ctx.lineWidth = (6 + (zoomlevel/2))/zoomlevel;

  for (let i = 0; i < routePath.length; i++) {
    ctx.beginPath();
    ctx.moveTo(lonToX(routePath[i][1]), latToY(routePath[i][0]));
    ctx.lineTo(lonToX(routePath[i][3]), latToY(routePath[i][2]));
    ctx.stroke();
  }
  // Adding this in prevents the last thing drawn from lagging
  ctx.beginPath();
  ctx.stroke();
}

const clickNearest = () => {
  let mouseDown = false;
  let mouseMoved = false;
  let clickX;
  let clickY;
  let moveCounter = 0;


  canvas.addEventListener("mousedown", function(e){
    mouseDown = true;
    mouseMoved = false;
    moveCounter = 0;
  }, false);

  canvas.addEventListener("mousemove", function(e){
    if (mouseDown) {
      if (moveCounter > 0){
        mouseMoved = true;
      }
      moveCounter++;
    }
  }, false);

  canvas.addEventListener("mouseup", function(e){
    if (!mouseMoved) {
      routeFlag = false;
      const clickLat = yToLat(e.clientY - canvas.getBoundingClientRect().top);
      const clickLon = xToLon(e.clientX  - canvas.getBoundingClientRect().left);
      const postParameters = {
        clickLat: clickLat,
        clickLon: clickLon
      };
      $.post("maps/nearest", postParameters, responseJSON => {
        const responseObject = JSON.parse(responseJSON);
        const message = responseObject.message;
        if (message !== "") {
          alert(message);
        } else {
          const lat = parseFloat(responseObject.lat);
          const lon = parseFloat(responseObject.lon);
          if (clickCount === 0) {
            nearestFlag = false;
            click1Lat = lat;
            click1Lon = lon;
            clickCount++;
          } else if (!(click1Lat === lat && click1Lon === lon) && clickCount === 1) {
            click2Lat = lat;
            click2Lon = lon;
            clickCount++;
          } else if (!(click1Lat === lat && click1Lon === lon) && !(click2Lat === lat && click2Lon === lon) && clickCount === 2) {
            clickCount = 1;
            click1Lat = lat;
            click1Lon = lon;
            click2Lat = null;
            click2Lon = null;
          }
          nearestFlag = true;
          paintMap();
        }
      });
    }
    mouseDown = false;
  }, false);
}

const drawNearest = () => {

  if (clickCount === 1) {
    ctx.beginPath();
    ctx.strokeStyle = "green";
    ctx.fillStyle = "green";
    ctx.arc(lonToX(click1Lon), latToY(click1Lat), 5, 0, 2 * Math.PI, false);
    ctx.fill();
  } else if (clickCount === 2) {
    ctx.beginPath();
    ctx.strokeStyle = "green";
    ctx.fillStyle = "green";
    ctx.arc(lonToX(click1Lon), latToY(click1Lat), 5, 0, 2 * Math.PI, false);
    ctx.fill();
    ctx.beginPath();
    ctx.strokeStyle = "red";
    ctx.fillStyle = "red";
    ctx.arc(lonToX(click2Lon), latToY(click2Lat), 5, 0, 2 * Math.PI, false);
    ctx.fill();
  }

  // Adding this in prevents the last thing drawn from lagging
  ctx.beginPath();
  ctx.stroke();
}

const lonToX = (lon) => ((lon + lonShift) * lonScale)

const latToY = (lat) => ((lat + latShift) * latScale)

const xToLon = (x) => ((x / lonScale) - lonShift)

const yToLat = (y) => ((y / latScale) - latShift)
