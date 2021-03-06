<!DOCTYPE html>
<html lang="en">
    <head>
        <title></title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        
        <link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.5.1/leaflet.css" />

        <link rel="stylesheet" href="http://cdn.leafletjs.com/leaflet-0.5.1/leaflet.ie.css" />

        <style type="text/css">
            body {
                padding: 0;
                margin: 0;
            }
            html,body,#map {
                height: 100%;
                font-family: 'oswald';
            }
            .leaflet-container .leaflet-control-zoom {
                margin-left: 13px;
                margin-top: 70px;
            }
            #map {
                z-index: 1;
            }
            #title {
                z-index: 2;
                position: absolute;
                left: 10px;
            }   
        </style>

    </head>
    <body>
        <h1 id="title">MLB Stadiums</h1>
        <div id="map"></div>



        <script src="http://code.jquery.com/jquery-2.0.0.min.js"></script>

        <script src="http://cdn.leafletjs.com/leaflet-0.5.1/leaflet.js"></script>

        <script>
            Number.prototype.toCurrencyString = function() { 
                return "$" + Math.floor(this).toLocaleString() + (this % 1).toFixed(2).toLocaleString().replace(/^0/,''); 
            }
            
            center = new L.LatLng(39.82, -98.57);
            
            zoom = 5;
            
            var map = L.map('map').setView(center, zoom);
            
            var markerLayerGroup = L.layerGroup().addTo(map);
            
            L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                maxZoom: 18,
                attribution: 'Map data &copy; <a href="http://openstreetmap.org">OpenStreetMap</a> contributors, <a href="http://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>'
            }).addTo(map);

            function getPins(e){
                bounds = map.getBounds();
                url = "api/parks/within?"+
                	"lat1=" + bounds.getNorthEast().lat +
                    "&lon1="+ bounds.getNorthEast().lng +
                    "&lat2=" + bounds.getSouthWest().lat +
                    "&lon2=" + bounds.getSouthWest().lng;
                $.get(url, pinTheMap, "json")
            }

            function pinTheMap(data){
                map.removeLayer(markerLayerGroup);
                var markerArray = new Array(data.length)
                for (var i = 0; i < data.length; i++){
                    park = data[i];
                    var popupInformation = "<b>" + park.name + "</b></br>" + park.ballpark + "</br>";
                        popupInformation += "<b>Team Payroll: </b>" + park.payroll.toCurrencyString() + "</br>";
                        popupInformation += "<b>League: </b>" + park.league + "</br>";
                    markerArray[i] = L.marker([park.position[1], park.position[0]]).bindPopup(popupInformation);
                }
                markerLayerGroup = L.layerGroup(markerArray).addTo(map);
            }

            map.on('dragend', getPins);
            
            map.on('zoomend', getPins);
            
            map.whenReady(getPins);


        </script>

    </body>
</html>