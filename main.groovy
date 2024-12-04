definition(
    name: "Hub Details",
    namespace: "hubDetails",
    author: "Josh Lobe",
    description: "Hub details and statistics.",
    category: "Convenience",
    iconUrl: "",
    iconX2Url: ""
)

import java.text.DecimalFormat
import java.text.SimpleDateFormat

hubUrl = "http://${location.hub.localIP}"

preferences {
    page(name: "mainPage", title: "Hub Details", install: true, uninstall: true) {
        section {
            
            Integer DD = location.hub.uptime / (24 * 3600) 
            Integer HH = (location.hub.uptime % (24 * 3600)) / 3600
            Integer MM = (location.hub.uptime % 3600) / 60
            Integer SS = location.hub.uptime % 60
            
            // Scripts
            html = "<script type='text/javascript' src='https://cdn.jsdelivr.net/npm/chart.js'></script>"
            
            html += "<div class='mdl-grid'>"
            html += "<div class='mdl-cell mdl-cell--6-col graybox'>"
            
                // Hub Details
                html += "<table class='table'><tbody>"
                html += "<tr><th colspan='2'>Hub Details</th></tr>"
                html += "<tr><td>Name</td><td>${location.hub.name}</td></tr>"
                html += "<tr><td>Model</td><td>${getHubVersion()}</td></tr>"
                html += "<tr><td>Local IP</td><td>${location.hub.localIP}</td></tr>"
                html += "<tr><td>Firmware</td><td>${location.hub.firmwareVersionString}</td></tr>"
                html += "<tr><td>Zigbee ID</td><td>${location.hub.zigbeeId}</td></tr>"
                html += "<tr><td>Zigbee EUI</td><td>${location.hub.zigbeeEui}</td></tr>"
                html += "<tr><td>Zigbee Channel</td><td>${location.hub.properties.data.zigbeeChannel}</td></tr>"
                html += "<tr><td>Zigbee Pan ID</td><td>${location.hub.data.zigbeePanID}</td></tr>"
                html += "<tr><td>Uptime</td><td>${ DD + ' days, ' + HH + ' Hours, ' + MM + ' Minutes, and ' + SS + ' Seconds' }</td></tr>"
                html += "</tbody></table>"
            
                httpGet([ uri: "${hubUrl}:8080/hub2/hubData", contentType: "application/json" ]) { resp ->
                    if (resp.success) {  
                        
                        // Hub Data
                        html += "<table class='table'><tbody>"
                        html += "<tr><th colspan='2'>Hub Data</th></tr>"
                        html += "<tr><td>Hub ID</td><td>${resp.data.hubId}</td></tr>"
                        html += "<tr><td>Access Token</td><td>${resp.data.baseModel.dashboard.accessToken}</td></tr>"
                        html += "<tr><td>Hub Version</td><td>${resp.data.hubVersion}</td></tr>"
                        html += "</tbody></table>"
                        
                        // Hub Alerts
                        html += "<table class='table'><tbody>"
                        html += "<tr><th colspan='2'>Hub Alerts</th></tr>"
                        html += "<tr><td>Load High</td><td>${resp.data.alerts.hubHighLoad}</td></tr>"
                        html += "<tr><td>Load Elevated</td><td>${resp.data.alerts.hubLoadElevated}</td></tr>"
                        html += "<tr><td>Load Severe</td><td>${resp.data.alerts.hubLoadSevere}</td></tr>"
                        html += "<tr><td>Low Memory</td><td>${resp.data.alerts.hubLowMemory}</td></tr>"
                        html += "<tr><td>Large-ish Database</td><td>${resp.data.alerts.hubLargeishDatabase}</td></tr>"
                        html += "<tr><td>Large Database</td><td>${resp.data.alerts.hubLargeDatabase}</td></tr>"
                        html += "<tr><td>Huge Database</td><td>${resp.data.alerts.hubHugeDatabase}</td></tr>"
                        html += "<tr><td>Zigbee Offline</td><td>${resp.data.alerts.zigbeeOffline}</td></tr>"
                        html += "<tr><td>ZWave Offline</td><td>${resp.data.alerts.zwaveOffline}</td></tr>"
                        html += "<tr><td>Spammy Devices</td><td>${resp.data.alerts.spammyDevices}</td></tr>"
                        html += "</tbody></table>"
                    }
                }
            
            html += "</div>"
            html += "<div class='mdl-cell mdl-cell--6-col graybox'>"
            
                // Location Details
                html += "<table class='table'><tbody>"
                html += "<tr><th colspan='2'>Location Details</th></tr>"
                html += "<tr><td>Name</td><td>${location.name}</td></tr>"
                html += "<tr><td>ID</td><td>${location.id}</td></tr>"
                html += "<tr><td>Firmware</td><td>${location.hub.firmwareVersionString}</td></tr>"
                html += "<tr><td>Zip Code</td><td>${location.zipCode}</td></tr>"
                html += "<tr><td>Latitude</td><td>${location.latitude}</td></tr>"
                html += "<tr><td>Longitude</td><td>${location.longitude}</td></tr>"
                html += "<tr><td>Sunrise</td><td>${location.sunrise}</td></tr>"
                html += "<tr><td>Sunset</td><td>${location.sunset}</td></tr>"
                html += "<tr><td>HSM Status</td><td>${location.hsmStatus}</td></tr>"
                html += "<tr><td>HSM Mode</td><td>${location.properties.currentMode}</td></tr>"
                html += "<tr><td>Temp Scale</td><td>${location.temperatureScale}</td></tr>"
                html += "</tbody></table>"
            
                // Hub Endpoints
                html += "<table class='table'><tbody>"
                html += "<tr><th colspan='2'>Hub Endpoints</th></tr>"
                httpGet([ uri: "${hubUrl}:8080/hub/advanced/freeOSMemory" ]) { resp ->
                    if (resp.success) { 
                        DecimalFormat df = new DecimalFormat( "#,###" )
                        historyLink = "<a href='${hubUrl}:8080/hub/advanced/freeOSMemoryHistory' target='_blank'>(history)</a>"
                        html += "<tr><td>Hub Memory</td><td>${df.format( resp.data.toString() as Integer )} KB ${historyLink}</td></tr>"
                    }
                }
                httpGet([ uri: "${hubUrl}:8080/hub/advanced/internalTempCelsius" ]) { resp ->
                    if (resp.success) { html += "<tr><td>Hub CPU Temp</td><td>${celsiusToFahrenheit( new Double( resp.data.toString() ) )} °F</td></tr>" }
                }
                httpGet([ uri: "${hubUrl}:8080/hub/advanced/databaseSize" ]) { resp ->
                    if (resp.success) { html += "<tr><td>Database Size</td><td>${resp.data} MB</td></tr>" }
                }
                httpGet([ uri: "${hubUrl}:8080/hub/advanced/maxDeviceStateAgeDays" ]) { resp ->
                    if (resp.success) { html += "<tr><td>Max State Time</td><td>${resp.data} Days</td></tr>" }
                }
                httpGet([ uri: "${hubUrl}:8080/hub/advanced/maxEventAgeDays" ]) { resp ->
                    if (resp.success) { html += "<tr><td>Max Event Time</td><td>${resp.data} Days</td></tr>" }
                }
                html += "</tbody></table>"
            
                httpGet([ uri: "${hubUrl}:8080/hub/zwaveVersion" ]) { resp ->
                    if (resp.success) {  
                        
                        zString = resp.data.toString()
                        Integer start = zString.indexOf( '(' )
                        Integer end = zString.length()
                        String wrkStr = zString.substring( start, end ).replace( "(", "[" ).replace( ")", "]" )
                        
                        HashMap zMap = (HashMap)evaluate( wrkStr )
                        
                        // ZWave Data
                        html += "<table class='table'><tbody>"
                        html += "<tr><th colspan='2'>ZWave Data</th></tr>"
                        html += "<tr><td>Library Type</td><td>${zMap.zWaveLibraryType}</td></tr>"
                        html += "<tr><td>Protocol Version</td><td>${zMap.zWaveProtocolVersion}</td></tr>"
                        html += "<tr><td>Protocol Sub Version</td><td>${zMap.zWaveProtocolSubVersion}</td></tr>"
                        html += "<tr><td>Firmware Version</td><td>${zMap.firmware0Version}</td></tr>"
                        html += "<tr><td>Firmware Sub Version</td><td>${zMap.firmware0SubVersion}</td></tr>"
                        html += "<tr><td>Hardware Version</td><td>${zMap.hardwareVersion}</td></tr>"
                        html += "</tbody></table>"
                    }
                }
            
            html += "</div>"
            html += "</div>"
            
           
            
            httpGet([ uri: "${hubUrl}:8080/hub/advanced/freeOSMemoryHistory", contentType: "text/html" ]) { resp ->
                if (resp.success) { 

                    html += "<div class='mdl-grid'>"

                        // Memory Graph
                        html += "<div class='mdl-cell mdl-cell--6-col graybox'>"
                            html += "<div><canvas id='memChart'></canvas></div>"
                        html += "</div>"

                        // CPU Graph
                        html += "<div class='mdl-cell mdl-cell--6-col graybox'>"
                            html += "<div><canvas id='cpuChart'></canvas></div>"
                        html += "</div>"

                    html += "</div>"

                    def count = 0
                    def times = ''
                    def mems = ''
                    def cpus = ''
                    loadWork = resp.data.toString()
                    loadWork.eachLine{ 

                        if( it.contains( "Date" ) ) return
                        count++
                        if( count % 20 != 0 ) return
                        line = it.split( "," )
                            
                        Date date = Date.parse( 'MM-dd H:mm:ss', line[0] )
                        String newDate = date.format( 'MM-dd, h:mm a' )
                            
                        times += '"' + newDate + '",'
                        mems += line[1] + ','
                        cpus += line[2] + ','
                    }
                    times = times.replaceAll(',$', '');
                    mems = mems.replaceAll(',$', '');
                    cpus = cpus.replaceAll(',$', '');

                    html += "<script type='text/javascript'>"
                    html += '''
                        const memChart = document.getElementById( 'memChart' );
                        const cpuChart = document.getElementById( 'cpuChart' );
                        setTimeout( () => { 
                            new Chart( memChart, { 
                                type: 'line', 
                                data: { 
                                    labels: [ ''' + times + ''' ], 
                                    datasets: [{ label: 'Free Memory', data: [''' + mems + '''], borderWidth: 1 }] 
                                }, 
                                options: { 
                                    scales: { 
                                        y: { suggestedMax: 650000, suggestedMin: 400000 } 
                                    } 
                                } 
                            } ); 
                            new Chart( cpuChart, { 
                                type: 'line', 
                                data: { 
                                    labels: [ ''' + times + ''' ], 
                                    datasets: [{ label: 'CPU Load', data: [''' + cpus + '''], borderWidth: 1 }] 
                                }, 
                                options: { 
                                    scales: { 
                                        y: { suggestedMax: 0, suggestedMin: 10 } 
                                    } 
                                } 
                            } ); 
                        }, 1000 );
                    '''
                    html += "</script>"
                }
            }
            
            
            
            // Styles
            html += "<style type='text/css'>"
            html += "div.form div h3.pre { display: none; }"
            html += ".table { width: 100%; border-collapse: collapse; margin-bottom: 34px; }"
            html += ".table, .table tr, .table td { border: 1px solid #000; }"
            html += ".table th { text-align: center; background-color: #81BC00; color: white; }"
            html += ".table th, .table td { padding: 3px 10px; }"
            html += ".table tr:nth-child( even ){ background-color: #f2f2f2 }"
            html += "</style>"
            
            // Print the page
            paragraph html
        }
    }
}

def installed() {
    
    log.trace "Installed Hub Details Application"
    updated()
}

def updated() {
    
    log.trace "Updated Hub Details Application"
}

def uninstalled() {
    
    log.trace "Uninstalled Hub Details Application"
}
