/**
 *  Copyright 2016 David Lomas (codersaur)
 *
 *  Name: Evohome Heating Zone
 *
 *  Author: David Lomas (codersaur)
 *
 *  Date: 2016-04-08
 *
 *  Version: 0.09
 *
 *  Description:
 *   - This device handler is a child device for the Evohome (Connect) SmartApp.
 *   - For latest documentation see: https://github.com/codersaur/SmartThings
 *
 *  Version History:
 *
 *   2016-04-08: v0.09
 *    - calculateOptimisations(): Fixed comparison of temperature values.
 * 
 *   2016-04-05: v0.08
 *    - New 'Update Refresh Time' setting from parent to control polling after making an update.
 *    - setThermostatMode(): Forces poll for all zones to ensure new thermostatMode is updated.
 * 
 *   2016-04-04: v0.07
 *    - generateEvent(): hides events if name or value are null.
 *    - generateEvent(): log.info message for new values.
 * 
 *   2016-04-03: v0.06
 *    - Initial Beta Release
 * 
 *  To Do:
 *   - Clean up device settings (preferences). Hide/Show prefSetpointDuration input dynamically depending on prefSetpointMode. - If supported for devices???
 *   - When thermostat mode is away or off, heatingSetpoint overrides should not allowed (although setting while away actually works). Should warn at least.
 *
 *  License:
 *   Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *   in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *   on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *   for the specific language governing permissions and limitations under the License.
 *
 */ 
metadata {
	definition (name: "Evohome Heating Zone", namespace: "codersaur", author: "David Lomas") {
		capability "Actuator"
		capability "Sensor"
		capability "Refresh"
		capability "Temperature Measurement"
		capability "Thermostat"
		
		//command "poll" // Polling
		command "refresh" // Refresh
		command "setHeatingSetpoint" // Thermostat
		command "raiseSetpoint" // Custom
		command "lowerSetpoint" // Custom
		command "setThermostatMode" // Thermostat
		command "cycleThermostatMode" // Custom
		command "off" // Thermostat
		command "heat" // Thermostat
		command "auto" // Custom
		command "away" // Custom
		command "economy" // Custom
		command "dayOff" // Custom
		command "custom" // Custom
		command "resume" // Custom
		command "boost" // Custom
		command "suppress" // Custom
		command "generateEvent" // Custom
		command "test" // Custom

		attribute "temperature","number" // Temperature Measurement
		attribute "heatingSetpoint","number" // Thermostat
		attribute "thermostatSetpoint","number" // Thermostat
		attribute "thermostatSetpointMode", "string" // Custom
		attribute "thermostatSetpointUntil", "string" // Custom
		attribute "thermostatSetpointStatus", "string" // Custom
		attribute "thermostatMode", "string" // Thermostat
		attribute "thermostatOperatingState", "string" // Thermostat
		attribute "thermostatStatus", "string" // Custom
		attribute "scheduledSetpoint", "number" // Custom
		attribute "nextScheduledSetpoint", "number" // Custom
		attribute "nextScheduledTime", "string" // Custom
		attribute "optimisation", "string" // Custom
		attribute "windowFunction", "string" // Custom
		
	}

	tiles(scale: 2) {

		// Main multi
		multiAttributeTile(name:"multi", type:"thermostat", width:6, height:4) {
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
				attributeState("default", label:'${currentValue}°', unit:"C")
			}
			// Up and Down buttons:
			//tileAttribute("device.temperature", key: "VALUE_CONTROL") {
			//	attributeState("VALUE_UP", action: "raiseSetpoint")
			//	attributeState("VALUE_DOWN", action: "lowerSetpoint")
			//}
			// Operating State - used to get background colour when type is 'thermostat'.
			tileAttribute("device.thermostatStatus", key: "OPERATING_STATE") {
				attributeState("Heating", backgroundColor:"#ffa81e", defaultState: true)
				attributeState("Idle (Auto)", backgroundColor:"#44b621")
				attributeState("Idle (Custom)", backgroundColor:"#44b621")
				attributeState("Idle (Day Off)", backgroundColor:"#44b621")
				attributeState("Idle (Economy)", backgroundColor:"#44b621")
				attributeState("Idle (Away)", backgroundColor:"#44b621")
				attributeState("Off", backgroundColor:"#269bd2")
			}
			//tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
			//	attributeState("off", label:'${name}')
			//	attributeState("away", label:'${name}')
			//	attributeState("auto", label:'${name}')
			//	attributeState("economy", label:'${name}')
			//	attributeState("dayOff", label:'${name}')
			//	attributeState("custom", label:'${name}')
			//}
			//tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
			//	attributeState("default", label:'${currentValue}', unit:"C")
			//}
			//tileAttribute("device.coolingSetpoint", key: "COOLING_SETPOINT") {
			//	attributeState("default", label:'${currentValue}', unit:"C")
			//}
		}
	
		// temperature tile:
		valueTile("temperature", "device.temperature", width: 2, height: 2, canChangeIcon: true) {
			state("temperature", label:'${currentValue}°', unit:"C", icon:"st.Weather.weather2",
					backgroundColors:[
							// Celsius
							[value: 0, color: "#153591"],
							[value: 7, color: "#1e9cbb"],
							[value: 15, color: "#90d2a7"],
							[value: 23, color: "#44b621"],
							[value: 28, color: "#f1d801"],
							[value: 35, color: "#d04e00"],
							[value: 37, color: "#bc2323"]
					]
			)
		}
		
		// thermostatSetpoint tiles:
		valueTile("thermostatSetpoint", "device.thermostatSetpoint", width: 3, height: 1) {
			state "thermostatSetpoint", label:'Setpoint: ${currentValue}°', unit:"C"
		}
		valueTile("thermostatSetpointStatus", "device.thermostatSetpointStatus", width: 3, height: 1, decoration: "flat") {
			state "thermostatSetpointStatus", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		standardTile("raiseSetpoint", "device.thermostatSetpoint", width: 1, height: 1, decoration: "flat") {
			state "setpoint", action:"raiseSetpoint", icon:"st.thermostat.thermostat-up"
		}
		standardTile("lowerSetpoint", "device.thermostatSetpoint", width: 1, height: 1, decoration: "flat") {
			state "setpoint", action:"lowerSetpoint", icon:"st.thermostat.thermostat-down"
		}
		standardTile("resume", "device.resume", width: 1, height: 1, decoration: "flat") {
			state "default", action:"resume", label:'Resume', icon:"st.samsung.da.oven_ic_send"
		}
		standardTile("boost", "device.boost", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
			state "default", action:"boost", label:'Boost' // icon TBC
		}
		standardTile("suppress", "device.suppress", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
			state "default", action:"suppress", label:'Suppress' // icon TBC
		}
		
		
		// thermostatMode/Status Tiles:
		
		// thermostatStatus (also incorporated into the multi tile).
		valueTile("thermostatStatus", "device.thermostatStatus", height: 1, width: 6, decoration: "flat") {
			state "thermostatStatus", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		// Single thermostatMode tile that cycles between all modes (too slow).
		// To Do: Update with Evohome-specific modes:
		standardTile("thermostatMode", "device.thermostatMode", inactiveLabel: false, decoration: "flat") {
			state "off", action:"cycleMode", nextState: "updating", icon: "st.thermostat.heating-cooling-off"
			state "heat", action:"cycleMode",  nextState: "updating", icon: "st.thermostat.heat"
			state "cool", action:"cycleMode",  nextState: "updating", icon: "st.thermostat.cool"
			state "auto", action:"cycleMode",  nextState: "updating", icon: "st.thermostat.auto"
			state "auxHeatOnly", action:"cycleMode", icon: "st.thermostat.emergency-heat"
			state "updating", label:"Working", icon: "st.secondary.secondary"
		}
		// Individual Mode tiles:
		standardTile("auto", "device.auto", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", action:"auto", icon: "st.thermostat.auto"
		}
		standardTile("away", "device.away", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", action:"away", label:'Away' // icon TBC
		}
		standardTile("custom", "device.custom", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", action:"custom", label:'Custom' // icon TBC
		}
		standardTile("dayOff", "device.dayOff", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", action:"dayOff", label:'Day Off' // icon TBC
		}
		standardTile("economy", "device.economy", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", action:"economy", label:'Economy' // icon TBC
		}
		standardTile("off", "device.off", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", action:"off", icon:"st.thermostat.heating-cooling-off"
		}
		// Other tiles:
		standardTile("refresh", "device.thermostatMode", inactiveLabel: false, decoration: "flat") {
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		standardTile("test", "device.test", width: 1, height: 1, decoration: "flat") {
			state "default", label:'Test', action:"test"
		}
		
		main "temperature"
		details(
				[
				"multi",
				"thermostatSetpoint","raiseSetpoint","boost","resume",
				"thermostatSetpointStatus","lowerSetpoint","suppress","refresh",
				"auto","away","custom","dayOff","economy","off"
				]
		)
	}

	preferences {
		section { // Setpoint Adjustments:
			input title: "Setpoint Duration", description: "Configure how long setpoint adjustments are applied for.", displayDuringSetup: true, type: "paragraph", element: "paragraph"
			input 'prefSetpointMode', 'enum', title: 'Until', description: '', options: ["Next Switchpoint", "Midday", "Midnight", "Duration", "Permanent"], defaultValue: "Next Switchpoint", required: true, displayDuringSetup: true
			input 'prefSetpointDuration', 'number', title: 'Duration (minutes)', description: 'Apply setpoint for this many minutes', range: "1..1440", defaultValue: 60, required: true, displayDuringSetup: true
			//input 'prefSetpointTime', 'time', title: 'Time', description: 'Apply setpoint until this time', required: true, displayDuringSetup: true
			input title: "Setpoint Temperatures", description: "Configure preset temperatures for the 'Boost' and 'Suppress' buttons.", displayDuringSetup: true, type: "paragraph", element: "paragraph"
			input "prefBoostTemperature", "string", title: "'Boost' Temperature", defaultValue: "21.5", required: true, displayDuringSetup: true // use of 'decimal' input type in devices is currently broken.
			input "prefSuppressTemperature", "string", title: "'Suppress' Temperature", defaultValue: "15.0", required: true, displayDuringSetup: true // use of 'decimal' input type in devices is currently broken.
		}
				
	}

}

/**********************************************************************
 *  Test Commands:
 **********************************************************************/


/**
 *  test()
 *
 *  Test method, called from test tile.
 **/
def test() {

	//log.debug "$device.displayName: test(): Properties: ${properties}"
	//log.debug "$device.displayName: test(): Settings: ${settings}"
	//log.debug "$device.displayName: test(): State: ${state}"    

}


/**********************************************************************
 *  Setup and Configuration Commands:
 **********************************************************************/

/**
 *  installed()
 *
 *  Runs when the app is first installed.
 *  
 *  When a device is created by a SmartApp, settings are not populated
 *  with the defaultValues configured for each input. Therefore, we
 *  populate the corresponding state.* variables with the input defaultValues.
 * 
 **/
def installed() {

	log.debug "${app.label}: Installed with settings: ${settings}"

	state.installedAt = now()
	
	// These default values will be overwritten by the Evohome SmartApp almost immediately:
	state.debug = false
    state.updateRefreshTime = 5 // Wait this many seconds after an update before polling.
	state.zoneType = 'RadiatorZone'
	state.minHeatingSetpoint = formatTemperature(5.0)
	state.maxHeatingSetpoint = formatTemperature(35.0)
	state.temperatureResolution = formatTemperature(0.5)
	state.windowFunctionTemperature = formatTemperature(5.0)
	state.targetSetpoint = state.minHeatingSetpoint
	
	// Populate state.* with default values for each preference/input:
	state.setpointMode = getInputDefaultValue('prefSetpointMode')
	state.setpointDuration = getInputDefaultValue('prefSetpointDuration')
	state.boostTemperature = getInputDefaultValue('prefBoostTemperature')
	state.suppressTemperature = getInputDefaultValue('prefSuppressTemperature')
	
}


/**
 *  updated()
 * 
 *  Runs when device settings are changed.
 **/
def updated() {

	if (state.debug) log.debug "${device.label}: Updating with settings: ${settings}"

	// Copy input values to state:
	state.setpointMode = settings.prefSetpointMode
	state.setpointDuration = settings.prefSetpointDuration
	state.boostTemperature = formatTemperature(settings.prefBoostTemperature)
	state.suppressTemperature = formatTemperature(settings.prefSuppressTemperature)

}


/**********************************************************************
 *  SmartApp-Child Interface Commands:
 **********************************************************************/

/**
 *  generateEvent(values)
 *
 *  Called by parent to update the state of this child device.
 *
 **/
void generateEvent(values) {

	log.info "${device.label}: generateEvent(): New values: ${values}"
	
	if(values) {
		values.each { name, value ->
			if ( name == 'minHeatingSetpoint' 
				|| name == 'maxHeatingSetpoint' 
				|| name == 'temperatureResolution' 
				|| name == 'windowFunctionTemperature'
				|| name == 'zoneType'
				|| name == 'locationId'
				|| name == 'gatewayId'
				|| name == 'systemId'
				|| name == 'zoneId'
				|| name == 'schedule'
				|| name == 'debug'
                || name == 'updateRefreshTime'
				) {
				// Internal state only.
				state."${name}" = value
			}
			else { // Attribute value, so generate an event:
				if (name != null && value != null) {
					sendEvent(name: name, value: value, displayed: true)
				}
				else { // If name or value is null, set displayed to false,
					   // otherwise the 'Recently' view on smartphone app clogs 
					   // up with empty events.
					sendEvent(name: name, value: value, displayed: false)
				}
				
				// Reset targetSetpoint (used by raiseSetpoint/lowerSetpoint) if heatingSetpoint has changed:
				if (name == 'heatingSetpoint') {
					state.targetSetpoint = value
				}
			}
		}
	}
	
	// Calculate derived attributes (order is important here):
	calculateThermostatOperatingState()
	calculateOptimisations()
	calculateThermostatStatus()
	calculateThermostatSetpointStatus()
	
}


/**********************************************************************
 *  Capability-related Commands:
 **********************************************************************/


/**
 *  poll()
 *
 *  Polls the device. Required for the "Polling" capability
 **/
void poll() {

	if (state.debug) log.debug "${device.label}: poll()"
	parent.poll(state.zoneId)
}


/**
 *  refresh()
 *
 *  Refreshes values from the device. Required for the "Refresh" capability.
 **/
void refresh() {

	if (state.debug) log.debug "${device.label}: refresh()"
	sendEvent(name: 'thermostatSetpointStatus', value: 'Updating', displayed: false)
	parent.poll(state.zoneId)
}


/**
 *  setThermostatMode(mode, until=-1)
 * 
 *  Set thermostat mode until specified time.
 *
 *   mode:    Possible values: 'auto','off','away','dayOff','custom', or 'economy'.
 *
 *   until:   (Optional) Time to apply mode until, can be either:
 *             - Date: Date object representing when override should end.
 *             - ISO-8601 date string, in format "yyyy-MM-dd'T'HH:mm:ssXX", e.g.: "2016-04-01T00:00:00Z".
 *             - String: 'permanent'.
 *             - Number: Duration in hours if mode is 'economy', or days if mode is 'away'/'dayOff'/'custom'.
 *                       Duration will be rounded down to align with Midnight i nthe local timezone
 *                       (e.g. a duration of 1 day will end at midnight tonight). If 0, mode is permanent.
 *                       If duration is not specified, a default value is used from the Evohome SmartApp settings.
 *
 *   Notes:   'Auto' and 'Off' modes are always permanent.
 *            Thermostat mode is a property of the temperatureControlSystem (i.e. Evohome controller).
 *            Therefore changing the thermostatMode will affect all zones associated with the same controller.
 * 
 *  Example usage:
 *   setThermostatMode('off', 0)         // Set off mode permanently.
 *   setThermostatMode('away', 1)        // Set away mode for one day (i.e. until midnight tonight).
 *   setThermostatMode('dayOff', 2)      // Set dayOff mode for two days (ends tomorrow night).
 *   setThermostatMode('economy', 2)     // Set economy mode for two hours.
 *
 **/
def setThermostatMode(String mode, until=-1) {

	log.info "${device.label}: setThermostatMode(Mode: ${mode}, Until: ${until})"
	
	// Send update via parent:
	if (!parent.setThermostatMode(state.systemId, mode, until)) {
		sendEvent(name: 'thermostatSetpointStatus', value: 'Updating', displayed: false)
		// Wait a few seconds as it takes a while for Evohome to update setpoints in response to a mode change.
		pseudoSleep(state.updateRefreshTime * 1000)
		parent.poll(0) // Force poll for all zones as thermostatMode is a property of the temperatureControlSystem.
		return null
	}
	else {
		log.error "${device.label}: setThermostatMode(): Error: Unable to set thermostat mode."
		return 'error'
	}
}


/**
 *  setHeatingSetpoint(setpoint, until=-1)
 * 
 *  Set heatingSetpoint until specified time.
 *
 *   setpoint:   Setpoint temperature, e.g.: "21.5". Can be a number or string.
 *               If setpoint is outside allowed range (i.e. minHeatingSetpoint to 
 *               maxHeatingSetpoint) it will be re-written to the appropriate limit.
 *
 *   until:      (Optional) Time to apply setpoint until, can be either:
 *                - Date: date object representing when override should end.
 *                - ISO-8601 date string, in format "yyyy-MM-dd'T'HH:mm:ssXX", e.g.: "2016-04-01T00:00:00Z".
 *                - String: 'nextSwitchpoint', 'midnight', 'midday', or 'permanent'.
 *                - Number: duration in minutes (from now). 0 = permanent.
 *               If not specified, setpoint duration will default to the
 *               behaviour defined in the device settings.
 *
 *  Example usage:
 *   setHeatingSetpoint(21.0)                           // Set until <device default>.
 *   setHeatingSetpoint(21.0, 'nextSwitchpoint')        // Set until next scheduled switchpoint.
 *   setHeatingSetpoint(21.0, 'midnight')               // Set until midnight.
 *   setHeatingSetpoint(21.0, 'permanent')              // Set permanently.
 *   setHeatingSetpoint(21.0, 0)                        // Set permanently.
 *   setHeatingSetpoint(21.0, 6)                        // Set for 6 hours.
 *   setHeatingSetpoint(21.0, '2016-04-01T00:00:00Z')   // Set until specific time.
 *
 **/
def setHeatingSetpoint(setpoint, until=-1) {

	if (state.debug) log.debug "${device.label}: setHeatingSetpoint(Setpoint: ${setpoint}, Until: ${until})"
	
	// Clean setpoint:
	setpoint = formatTemperature(setpoint)
	if (Float.parseFloat(setpoint) < Float.parseFloat(state.minHeatingSetpoint)) {
		log.warn "${device.label}: setHeatingSetpoint(): Specified setpoint (${setpoint}) is less than zone's minimum setpoint (${state.minHeatingSetpoint})."
		setpoint = state.minHeatingSetpoint
	}
	else if (Float.parseFloat(setpoint) > Float.parseFloat(state.maxHeatingSetpoint)) {
		log.warn "${device.label}: setHeatingSetpoint(): Specified setpoint (${setpoint}) is greater than zone's maximum setpoint (${state.maxHeatingSetpoint})."
		setpoint = state.maxHeatingSetpoint
	}
	
	// Clean and parse until value:
	def untilRes
	Calendar c = new GregorianCalendar()
	def tzOffset = location.timeZone.getOffset(new Date().getTime()) // Timezone offset to UTC in milliseconds.
	
	// If until has not been specified, determine behaviour from device state.setpointMode:
	if (-1 == until) {
		switch (state.setpointMode) {
	    	case 'Next Switchpoint':
	        	until = 'nextSwitchpoint'
	            break
	    	case 'Midday':
	        	until = 'midday'
	            break
	    	case 'Midnight':
	        	until = 'midnight'
	            break
	    	case 'Duration':
	        	until = state.setpointDuration ?: 0
	            break
	    	case 'Time':
				// TO DO : construct time, like we do for midnight.
				// settings.prefSetpointTime appears to return an ISO dateformat string.
				// However using an input of type "time" causes HTTP 500 errors in the IDE, so disabled for now.
				// If time has passed, then need to make it the next day.
				if (state.debug) log.debug "${device.label}: setHeatingSetpoint(): Time: ${state.SetpointTime}"
	        	until = 'nextSwitchpoint'
	            break
	    	case 'Permanent':
	        	until = 'permanent'
	            break
	    	default:
	        	until = 'nextSwitchpoint'
	            break
		}
	}
	
	if ('permanent' == until || 0 == until) {
		untilRes = 0
	}
	else if (until instanceof Date) {
		untilRes = until
	}
	else if ('nextSwitchpoint' == until) {
		untilRes = new Date().parse("yyyy-MM-dd'T'HH:mm:ssXX", device.currentValue('nextScheduledTime'))
	}
	else if ('midday' == until) {
		untilRes = new Date().parse("yyyy-MM-dd'T'HH:mm:ssXX", new Date().format("yyyy-MM-dd'T'12:00:00XX", location.timeZone)) 
	}
	else if ('midnight' == until) {
		c.add(Calendar.DATE, 1 ) // Add one day to calendar and use to get midnight in local time:
		untilRes =  new Date().parse("yyyy-MM-dd'T'HH:mm:ssXX", c.getTime().format("yyyy-MM-dd'T'00:00:00XX", location.timeZone))
	}
	else if (until ==~ /\d+.*T.*/) { // until is a ISO-8601 date string, so parse:
		untilRes = new Date().parse("yyyy-MM-dd'T'HH:mm:ssXX", until)
	}
	else if (until.isNumber()) { // until is a duration in minutes, so construct date from now():
		// Evohome supposedly only accepts setpoints for up to 24 hours, so we should limit minutes to 1440.
		// For now, just pass any duration and see if Evohome accepts it...
		untilRes = new Date( now() + (Math.round(until) * 60000) )
	}
	else {
		log.warn "${device.label}: setHeatingSetpoint(): until value could not be parsed. Setpoint will be applied permanently."
		untilRes = 0
	}
	
	log.info "${device.label}: setHeatingSetpoint(): Setting setpoint to: ${setpoint} until: ${untilRes}"
	
	// Send update via parent:
	if (!parent.setHeatingSetpoint(state.zoneId, setpoint, untilRes)) {
		// Command was successful, but it takes a few seconds for the Evohome cloud service to update with new values.
		// Meanwhile, we know the new setpoint and thermostatSetpointMode anyway:
		sendEvent(name: 'heatingSetpoint', value: setpoint)
		sendEvent(name: 'thermostatSetpoint', value: setpoint)
		sendEvent(name: 'thermostatSetpointMode', value: (0 == untilRes) ? 'permanentOverride' : 'temporaryOverride' )
		sendEvent(name: 'thermostatSetpointUntil', value: (0 == untilRes) ? null : untilRes.format("yyyy-MM-dd'T'HH:mm:00XX", TimeZone.getTimeZone('UTC')))
		calculateThermostatOperatingState()
		calculateOptimisations()
		calculateThermostatStatus()
		sendEvent(name: 'thermostatSetpointStatus', value: 'Updating', displayed: false)
		pseudoSleep(state.updateRefreshTime * 1000)
		parent.poll(state.zoneId)
		return null
	}
	else {
		log.error "${device.label}: setHeatingSetpoint(): Error: Unable to set heating setpoint."
		return 'error'
	}
}



/**
 *  clearHeatingSetpoint()
 * 
 *  Clear the heatingSetpoint. Will return heatingSetpoint to scheduled value.
 *  thermostatSetpointMode should return to "followSchedule".
 * 
 **/
def clearHeatingSetpoint() {

	log.info "${device.label}: clearHeatingSetpoint()"

	// Send update via parent:
	if (!parent.clearHeatingSetpoint(state.zoneId)) {
		// Command was successful, but it takes a few seconds for the Evohome cloud service
		// to update the zone status with the new heatingSetpoint.
		// Meanwhile, we know the new thermostatSetpointMode is "followSchedule".
		sendEvent(name: 'thermostatSetpointMode', value: 'followSchedule')
		sendEvent(name: 'thermostatSetpointStatus', value: 'Updating', displayed: false)
		// sleep command is not allowed in SmartThings, so we use psuedoSleep().
		pseudoSleep(state.updateRefreshTime * 1000)
		parent.poll(state.zoneId)
		return null
	}
	else {
		log.error "${device.label}: clearHeatingSetpoint(): Error: Unable to clear heating setpoint."
		return 'error'
	}
}


/**
 *  raiseSetpoint()
 * 
 *  Raise heatingSetpoint and thermostatSetpoint.
 *  Increments by state.temperatureResolution (usually 0.5).
 *
 *  Called by raiseSetpoint tile.
 * 
 **/
void raiseSetpoint() {

	if (state.debug) log.debug "${device.label}: raiseSetpoint()"
	
	def mode = device.currentValue("thermostatMode")
	def targetSp = new BigDecimal(state.targetSetpoint)
	def tempRes = new BigDecimal(state.temperatureResolution) // (normally 0.5)
	def maxSp = new BigDecimal(state.maxHeatingSetpoint)
	
	if ('off' == mode || 'away' == mode) {
		log.warn "${device.label}: raiseSetpoint(): thermostat mode (${mode}) does not allow altering the temperature setpoint."
	}
	else {
		targetSp += tempRes

		if (targetSp > maxSp) {
			targetSp = maxSp
		}
		
		state.targetSetpoint = targetSp
		log.info "${device.label}: raiseSetpoint(): Target setpoint raised to: ${targetSp}"
		sendEvent(name: 'thermostatSetpointStatus', value: 'Updating', displayed: false)
		runIn(3, "alterSetpoint", [overwrite: true]) // Wait three seconds in case targetSetpoint is changed again.
	}
	
}


/**
 *  lowerSetpoint()
 * 
 *  Lower heatingSetpoint and thermostatSetpoint.
 *  Increments by state.temperatureResolution (usually 0.5).
 *
 *  Called by lowerSetpoint tile.
 * 
 **/
void lowerSetpoint() {

	if (state.debug) log.debug "${device.label}: lowerSetpoint()"
	
	def mode = device.currentValue("thermostatMode")
	def targetSp = new BigDecimal(state.targetSetpoint)
	def tempRes = new BigDecimal(state.temperatureResolution) // (normally 0.5)
	def minSp = new BigDecimal(state.minHeatingSetpoint)
	
	if ('off' == mode || 'away' == mode) {
		log.warn "${device.label}: lowerSetpoint(): thermostat mode (${mode}) does not allow altering the temperature setpoint."
	}
	else {
		targetSp -= tempRes 

		if (targetSp < minSp) {
			targetSp = minSp
		}
		
		state.targetSetpoint = targetSp
		log.info "${device.label}: lowerSetpoint(): Target setpoint lowered to: ${targetSp}"
		sendEvent(name: 'thermostatSetpointStatus', value: 'Updating', displayed: false)
		runIn(3, "alterSetpoint", [overwrite: true]) // Wait three seconds in case targetSetpoint is changed again.
	}
	
}


/**
 *  alterSetpoint()
 * 
 *  Proxy command called by raiseSetpoint and lowerSetpoint, as runIn 
 *  cannot pass targetSetpoint diretly to setHeatingSetpoint.
 *
 **/
private alterSetpoint() {

	if (state.debug) log.debug "${device.label}: alterSetpoint()"
	
	setHeatingSetpoint(state.targetSetpoint)
}


/**********************************************************************
 *  Convenience Commands:
 *   These commands alias other commands with preset parameters.
 **********************************************************************/

void resume() {
	if (state.debug) log.debug "${device.label}: resume()"
	clearHeatingSetpoint()
}

void auto() {
	if (state.debug) log.debug "${device.label}: auto()"
	setThermostatMode('auto')
}

void heat() {
	if (state.debug) log.debug "${device.label}: heat()"
	setThermostatMode('auto')
}

void off() {
	if (state.debug) log.debug "${device.label}: off()"
	setThermostatMode('off')
}

void away(until=-1) {
	if (state.debug) log.debug "${device.label}: away()"
	setThermostatMode('away', until)
}

void custom(until=-1) {
	if (state.debug) log.debug "${device.label}: custom()"
	setThermostatMode('custom', until)
}

void dayOff(until=-1) {
	if (state.debug) log.debug "${device.label}: dayOff()"
	setThermostatMode('dayOff', until)
}

void economy(until=-1) {
	if (state.debug) log.debug "${device.label}: economy()"
	setThermostatMode('economy', until)
}

void boost() {
	if (state.debug) log.debug "${device.label}: boost()"
	setHeatingSetpoint(state.boostTemperature)
}

void suppress() {
	if (state.debug) log.debug "${device.label}: suppress()"
	setHeatingSetpoint(state.suppressTemperature)
}

/**********************************************************************
 *  Helper Commands:
 **********************************************************************/

/**
 *  pseudoSleep(ms)
 * 
 *  Substitute for sleep() command.
 *
 **/
private pseudoSleep(ms) {
	def start = now()
	while (now() < start + ms) {
		// Do nothing, just wait.
	}
}


/**
 *  getInputDefaultValue(inputName)
 * 
 *  Get the default value for the specified input.
 *
 **/
private getInputDefaultValue(inputName) {

	if (state.debug) log.debug "${device.label}: getInputDefaultValue()"
	
	def returnValue
	properties.preferences?.sections.each { section ->
		section.input.each { input ->
			if (input.name == inputName) {
				returnValue = input.defaultValue
			}
		}
	}
	
	return returnValue
}



/**
 *  formatTemperature(t)
 * 
 *  Format temperature value to one decimal place.
 *  t:   can be string, float, bigdecimal...
 *  Returns as string.
 **/
private formatTemperature(t) {
	//return Float.parseFloat("${t}").round(1)
	//return String.format("%.1f", Float.parseFloat("${t}").round(1))
	return Float.parseFloat("${t}").round(1).toString()
}


/**
 *  formatThermostatModeForDisp(mode)
 * 
 *  Translate SmartThings values to display values.
 *   
 **/
private formatThermostatModeForDisp(mode) {

	if (state.debug) log.debug "${device.label}: formatThermostatModeForDisp()"

	switch (mode) {
		case 'auto':
			mode = 'Auto'
			break
		case 'economy':
			mode = 'Economy'
			break
		case 'away':
			mode = 'Away'
			break
		case 'custom':
			mode = 'Custom'
			break
		case 'dayOff':
			mode = 'Day Off'
			break
		case 'off':
			mode = 'Off'
			break
		default:
			mode = 'Unknown'
			break
	}

	return mode
 }
  

/**
 *  calculateThermostatOperatingState()
 * 
 *  Calculates thermostatOperatingState and generates event accordingly.
 *
 **/
private calculateThermostatOperatingState() {

	if (state.debug) log.debug "${device.label}: calculateThermostatOperatingState()"

	def tOS
	if ('off' == device.currentValue('thermostatMode')) {
		tOS = 'off'
	}
	else if (device.currentValue("temperature") < device.currentValue("thermostatSetpoint")) {
		tOS = 'heating'
	}
	else {
		tOS = 'idle'
	}
	
	sendEvent(name: 'thermostatOperatingState', value: tOS)
}


/**
 *  calculateOptimisations()
 * 
 *  Calculates if optimisation and windowFunction are active 
 *  and generates events accordingly.
 *
 *  This isn't going to be 100% perfect, but is reasonably accurate.
 *
 **/
private calculateOptimisations() {

	if (state.debug) log.debug "${device.label}: calculateOptimisations()"

	def newOptValue = 'inactive'
	def newWdfValue = 'inactive'
	
    // Convert temp values to BigDecimals for comparison:
	def heatingSp = new BigDecimal(device.currentValue('heatingSetpoint'))
	def scheduledSp = new BigDecimal(device.currentValue('scheduledSetpoint'))
	def nextScheduledSp = new BigDecimal(device.currentValue('nextScheduledSetpoint'))
	def windowTemp = new BigDecimal(state.windowFunctionTemperature)
    
	if ('auto' != device.currentValue('thermostatMode')) {
		// Optimisations cannot be active if thermostatMode is not 'auto'.
	}
	else if ('followSchedule' != device.currentValue('thermostatSetpointMode')) {
		// Optimisations cannot be active if thermostatSetpointMode is not 'followSchedule'.
		// There must be a manual override.
	}
	else if (heatingSp == scheduledSp) {
		// heatingSetpoint is what it should be, so no reason to suspect that optimisations are active.
	}
	else if (heatingSp == nextScheduledSp) {
		// heatingSetpoint is the nextScheduledSetpoint, so optimisation is likely active:
		newOptValue = 'active'
	}
	else if (heatingSp == windowTemp) {
		// heatingSetpoint is the windowFunctionTemp, so windowFunction is likely active:
		newWdfValue = 'active'
	}
   
	sendEvent(name: 'optimisation', value: newOptValue)
	sendEvent(name: 'windowFunction', value: newWdfValue)

}


/**
 *  calculateThermostatStatus()
 * 
 *  Calculates thermostatStatus and generates event accordingly.
 *
 *  thermostatStatus is a text summary of thermostatMode and thermostatOperatingState.
 *
 **/
private calculateThermostatStatus() {

	if (state.debug) log.debug "${device.label}: calculateThermostatStatus()"

	def newThermostatStatus = ''
	def thermostatModeDisp = formatThermostatModeForDisp(device.currentValue('thermostatMode'))
	def setpoint = device.currentValue('thermostatSetpoint')
	
	if ('Off' == thermostatModeDisp) {
		newThermostatStatus = 'Off'
	}
	else if('heating' == device.currentValue('thermostatOperatingState')) {
		newThermostatStatus = "Heating to ${setpoint}° (${thermostatModeDisp})"
	}
	else {
		newThermostatStatus = "Idle (${thermostatModeDisp})"
	}
	
	sendEvent(name: 'thermostatStatus', value: newThermostatStatus)
}



/**
 *  calculateThermostatSetpointStatus()
 * 
 *  Calculates thermostatSetpointStatus and generates event accordingly.
 *
 *  thermostatSetpointStatus is a text summary of thermostatSetpointMode and thermostatSetpointUntil. 
 *  It also indicates if 'optimisation' or 'windowFunction' is active.
 *
 **/
private calculateThermostatSetpointStatus() {

	if (state.debug) log.debug "${device.label}: calculateThermostatSetpointStatus()"

	def newThermostatSetpointStatus = ''
	def setpointMode = device.currentValue('thermostatSetpointMode')
	
	if ('off' == device.currentValue('thermostatMode')) {
		newThermostatSetpointStatus = 'Off'
	}
	else if ('away' == device.currentValue('thermostatMode')) {
		newThermostatSetpointStatus = 'Away'
	}
	else if ('active' == device.currentValue('optimisation')) {
		newThermostatSetpointStatus = 'Optimisation Active'
	}
	else if ('active' == device.currentValue('windowFunction')) {
		newThermostatSetpointStatus = 'Window Function Active'
	}
	else if ('followSchedule' == setpointMode) {
		newThermostatSetpointStatus = 'Following Schedule'
	}
	else if ('permanentOverride' == setpointMode) {
		newThermostatSetpointStatus = 'Permanent'
	}
	else {
		def untilStr = device.currentValue('thermostatSetpointUntil')
		if (untilStr) {
		
			//def nowDate = new Date()
			
			// thermostatSetpointUntil is an ISO-8601 date format in UTC, and parse() seems to assume date is in UTC.
			def untilDate = new Date().parse("yyyy-MM-dd'T'HH:mm:ssXX", untilStr) 
			def untilDisp = ''
			
			if (untilDate.format("u") == new Date().format("u")) { // Compare day of week to current day of week (today).
				untilDisp = untilDate.format("HH:mm", location.timeZone) // Same day, so just show time.
			}
			else {
				untilDisp = untilDate.format("HH:mm 'on' EEEE", location.timeZone) // Different day, so include name of day.
			}
			newThermostatSetpointStatus = "Temporary Until ${untilDisp}"
		}
		else {
			newThermostatSetpointStatus = "Temporary"
		}
	}
	
	sendEvent(name: 'thermostatSetpointStatus', value: newThermostatSetpointStatus)
}