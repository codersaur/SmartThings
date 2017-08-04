/**
 *  Copyright 2016 David Lomas (codersaur)
 *
 *  Name: Aeon Home Energy Meter (GEN2 - UK - 1 Clamp)
 *
 *  Author: David Lomas (codersaur)
 *
 *  Date: 2017-03-02
 *
 *  Version: 1.03
 *
 *  Description:
 *   - This device handler is written specifically for the Aeon Home Energy Meter Gen2 UK version, with a single clamp.
 *   - Supports live reporting of energy, power, current, and voltage. Press the 'Now' tile to refresh.
 *      (voltage tile is not shown by default, but you can enable it below).
 *   - Supports reporting of energy usage and cost over an ad hoc period, based on the 'energy' figure reported by 
 *     the device. Press the 'Since...' tile to reset.
 *   - Supports additional reporting of energy usage and cost over multiple pre-defined periods:
 *       'Today', 'Last 24 Hours', 'Last 7 Days', 'This Month', 'This Year', and 'Lifetime'
 *     These can be cycled through by pressing the 'statsMode' tile. 
 *   - There's a tile that will reset all Energy Stats periods, but it's hidden by default.
 *   - Key device parameters can be set from the device settings. Refer to the Aeon HEMv2 instruction 
 *     manual for full details.
 *   - If you are re-using this device, please use your own hosting for the icons.
 *
 *  Version History:
 *
 *   2017-03-02: v1.03:
 *    - Fixed tile formatting for Android.
 *    - Limited power attribute to one decimal place.
 *
 *   2016-02-27: v1.02
 *    - Added "Voltage Measurement" capability to metadata (although not currently suppoted by hub).
 *    
 *   2016-02-15: v1.01
 *    - Added reporting of energy usage and cost over multiple pre-defined periods.
 *    - Added ConfigurationReport event parser (useful for debuging).
 *    - Added input preferences for Parameter 2, 4, 8.
 *    - Improved input preference descriptions and ranges.
 *    - Added background colours for mainPower and multi1 tiles. 
 *    - Added Instantaneous £/day figure as a secondary info on multi1.
 *
 *   2016-02-05: v1.0 - Initial Version for HEMv2 UK 1 Clamp.
 *    - Added support for voltage (V) and current (A).
 *    - Added fingerprint for HEMv2.
 *    - Added Refresh and Polling capabilities.
 *    - Added input preferences for reporting intervals.
 *    - Added calculation of total cost, based on CostPerKWh setting.
 * 
 *  To Do:
 *   - Capture out-of-band energy reset.
 *   - Option to specify a '£/day' fixed charge, which is added to all energy cost figures.
 *   - If the use of 'enum' inputs with "multiple: true" is ever fixed by ST, then implement input
 *     preferences to specify Reporting Group Content Flags (Parameters 101-103).
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
	definition (
		name: "Aeon Home Energy Meter (GEN2 - UK - 1 Clamp)", 
		namespace: "codersaur", 
		author: "David Lomas"
	) 
	{
		capability "Power Meter"
		capability "Energy Meter"
		capability "Voltage Measurement"
		capability "Polling"
		capability "Refresh"
        capability "Configuration"
		capability "Sensor"
        
		command "reset"
        command "resetAllStats"
		command "poll"
        command "refresh"
        command "configure"
        command "updated"
        command "cycleStats"
		command "test"
  
		// Standard (Capability) Attributes:
		attribute "power", "number"
        attribute "energy", "number" // Energy (kWh) as reported by device (ad hoc period).

       // Custom Attributes:
        attribute "current", "number"
        attribute "voltage", "number"
        //attribute "powerFactor", "number" - Not supported.
		attribute "powerCost", "number"  // Instantaneous Cost of Power (£/day)
		attribute "lastReset", "string" // Time that ad hoc reporting was reset.
		attribute "statsMode", "string"
		attribute "costOfEnergy", "number" 
		attribute "energyToday", "number"
		attribute "costOfEnergyToday", "number"
		attribute "energy24Hours", "number"
		attribute "costOfEnergy24Hours", "number"
		attribute "energy7Days", "number"
		attribute "costOfEnergy7Days", "number"
		attribute "energyMonth", "number"
		attribute "costOfEnergyMonth", "number"
		attribute "energyYear", "number"
		attribute "costOfEnergyYear", "number"
		attribute "energyLifetime", "number"
		attribute "costOfEnergyLifetime", "number"

        // Display Attributes:
        // These are only required because the UI lacks number formatting and strips leading zeros.
        attribute "dispPower", "string"
        attribute "dispPowerCost", "string"
        attribute "dispCurrent", "string"
        attribute "dispVoltage", "string"
        //attribute "dispPowerFactor", "string" - Not supported.
        attribute "dispEnergy", "string"
        attribute "dispCostOfEnergy", "string"
        attribute "dispEnergyPeriod", "string"
        attribute "dispCostOfEnergyPeriod", "string"
		
		// Fingerprints:
		fingerprint deviceId: "0x3101", inClusters: "0x70 0x32 0x60 0x85 0x56 0x72 0x86"
	}

	// Tile definitions:
	tiles(scale: 2) {
	
		// Multi Tile:
		multiAttributeTile(name:"multi1", type: "generic", width: 6, height: 4) {
			tileAttribute ("device.power", key: "PRIMARY_CONTROL") {
				attributeState "default", label:'${currentValue} W', backgroundColors: [
					[value: 0, color: "#00cc33"],
					[value: 250, color: "#66cc33"],
					[value: 500, color: "#cccc33"],
					[value: 750, color: "#ffcc33"],
					[value: 1000, color: "#ff9933"], 
					[value: 1500, color: "#ff6633"], 
					[value: 2000, color: "#ff3333"]
				]		
			}
			tileAttribute ("device.dispPowerCost", key: "SECONDARY_CONTROL") {
				attributeState "default", label:'(${currentValue})'
			}
		}
		
		// Instantaneous Values:
		valueTile("instMode", "device.dispPower", decoration: "flat", width: 2, height: 1) {
			state "default", label:'Now:', action:"refresh.refresh", icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_refresh.png"
		}
		valueTile("power", "device.dispPower", decoration: "flat", width: 2, height: 1, canChangeIcon: true) {
			state "default", label:'${currentValue}', icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_top_bottom_2.png"
		}
		valueTile("current", "device.dispCurrent", decoration: "flat", width: 2, height: 1) {
			state "default", label:'${currentValue}', icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_top_bottom_2.png"
		}
		valueTile("voltage", "device.dispVoltage", decoration: "flat", width: 2, height: 1) {
			state "default", label:'${currentValue}', icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_top_bottom_2.png"
		}
		//valueTile("powerFactor", "device.dispPowerFactor", decoration: "flat", width: 2, height: 1) {
		//	state "default", label:'${currentValue}', icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_top_bottom_2.png"
		//}
		
        // Ad Hoc Energy Stats:
		valueTile("lastReset", "device.lastReset", decoration: "flat", width: 2, height: 1) {
			state "default", label:'Since:  ${currentValue}', action:"reset", icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_stopwatch_reset.png"
		}
		valueTile("energy", "device.dispEnergy", width: 2, height: 1) {
			state "default", label:'${currentValue}', icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_top_bottom_2.png"
		}
		valueTile("costOfEnergy", "device.dispCostOfEnergy", width: 2, height: 1) {
			state "default", label:'${currentValue}', icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_top_bottom_2.png"
		}
		
		// Energy Stats:
        // Needs to be a standardTile to be able to change icon for each state.
		valueTile("statsMode", "device.statsMode", decoration: "flat", canChangeIcon: true, canChangeBackground: true, width: 2, height: 1) {
			state "default", label:'${currentValue}:', action: "cycleStats", icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_cal_cycle.png"
			state "Today", label:'${currentValue}:', action: "cycleStats", icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_cal_cycle.png"
            state "Last 24 Hours", label:'${currentValue}:', action: "cycleStats", icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_cal_cycle.png"
            state "Last 7 Days", label:'${currentValue}:', action: "cycleStats", icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_cal_cycle.png"
            state "This Month", label:'${currentValue}:', action: "cycleStats", icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_cal_cycle.png"
			state "This Year", label:"${currentValue}:", action: "cycleStats", icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_cal_cycle.png"
			state "Lifetime", label:'${currentValue}:', action: "cycleStats", icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_cal_cycle.png"
		}
		valueTile("energyPeriod", "device.dispEnergyPeriod", width: 2, height: 1) {
			state "default", label:'${currentValue}', icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_top_bottom_2.png"
		}
		valueTile("costOfEnergyPeriod", "device.dispCostOfEnergyPeriod", width: 2, height: 1) {
			state "default", label:'${currentValue}', icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_top_bottom_2.png"
		}
		valueTile("costPerKWH", "device.costPerKWH", decoration: "flat", width: 2, height: 1) {
			state "default", label:'Unit Cost: ${currentValue}', icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_top_bottom_2.png"
		}
		
		// Action Buttons:
		standardTile("refresh", "device.power", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		standardTile("resetAllStats", "device.power", decoration: "flat", width: 2, height: 2) {
			state "default", label:'RESET ALL STATS!', action:"resetAllStats"
		}
		standardTile("configure", "device.power", decoration: "flat", width: 2, height: 2) {
			state "default", label:'', action:"configuration.configure", icon:"st.secondary.configure"
		}
		standardTile("test", "device.power", decoration: "flat", width: 2, height: 2) {
			state "default", label:'Test', action:"test"
		}
		
		// Tile layouts:
		main (["multi1"])
		details([
			// Multi Tile:
			"multi1",
			// Instantaneous Values:
			"instMode","power", "current", //"voltage" ,// "powerFactor",
			// Ad Hoc Stats:
			"lastReset", "energy", "costOfEnergy",
			// Energy Stats:
			"statsMode", "energyPeriod", "costOfEnergyPeriod"//, //"costPerKWH",
			// Action Buttons:
			// "refresh","resetAllStats","configure","test"
		])
	}

	// Preferences:
	preferences {
    	section {
			// Debug Mode:
			input "configDebugMode", "boolean", title: "Enable debug logging?", defaultValue: false, displayDuringSetup: false
			input "configCostPerKWH", "string", title: "Energy Cost (£/kWh)", defaultValue: "0.1316", required: true, displayDuringSetup: false
    	}
		
		section {
			// Native Device Parameters:
			
			input "configEnergyDetectionMode", "enum", title: "Energy Detection Mode:", options: ["Wattage, absolute kWh","+/-Wattage, algebraic sum kWh","+/-Wattage, +ive kWh (consuming electricity)","+/-Wattage, -iv kWh (generating electricity)"], defaultValue: "Wattage, absolute kWh", required: true, displayDuringSetup: false
			input "configSelectiveReporting", "boolean", title: "Enable Selective Reporting?", defaultValue: false, required: true, displayDuringSetup: false
			
			// Parameter 4: "Power Change Threshold for Auto-Report - Whole HEM (W)"
			input "configPowerThresholdAbs_HEM", "number", title: "Auto-report Power Threshold (W):", description: "Report power when value changes by... W", defaultValue: 50, range: "0..60000", displayDuringSetup: false
			
			// Parameters 5-7 are not needed for single-clamp version.
			
			// Parameter 8: "Power Percentage Change Threshold for Auto-Report - Whole HEM (%)"
			input "configPowerThresholdPercent_HEM", "number", title: "Auto-report Power Threshold (%):", description: "Report power when value changes by...%", defaultValue: 10, range: "0..100", displayDuringSetup: false
			
			// Parameters 9-11 are not needed for single-clamp version.
			
			// Parameters 101-103 are hard-coded. Will add input preferences if multi-select enum input behaviour is fixed by ST. Currently buggy.
			//  Reporting Group 1 = Power and Current.
			//  Reporting Group 2 = Energy
			//  Reporting Group 3 = Voltage
			
			// Parameter 111: Reporting Group 1 - Report Interval (s):
			input "configReportGroup1Interval", "number", title: "Power/Current Reporting Interval (s):", defaultValue: 60, range: "0..2147483647", displayDuringSetup: false
			
			// Parameter 112: Reporting Group 2 - Report Interval (s):
			input "configReportGroup2Interval", "number", title: "Energy Reporting Interval (s):", defaultValue: 600, range: "0..2147483647", displayDuringSetup: false
			
			// Parameter 113: Reporting Group 3 - Report Interval (s):
			input "configReportGroup3Interval", "number", title: "Voltage Reporting Interval (s):", defaultValue: 600, range: "0..2147483647", displayDuringSetup: false
		}
	}

	
	// simulator metadata
	simulator {
		for (int i = 0; i <= 10000; i += 1000) {
			status "power  ${i} W": new physicalgraph.zwave.Zwave().meterV1.meterReport(
				scaledMeterValue: i, precision: 3, meterType: 4, scale: 2, size: 4).incomingMessage()
		}
		for (int i = 0; i <= 100; i += 10) {
			status "energy  ${i} kWh": new physicalgraph.zwave.Zwave().meterV1.meterReport(
				scaledMeterValue: i, precision: 3, meterType: 0, scale: 0, size: 4).incomingMessage()
		}
	}

}

/**********************************************************************
 *  Z-wave Event Handlers.
 **********************************************************************/

/**
 *  parse - Called when messages from a device are received by the hub.
 *
 *  The parse method is responsible for interpreting those messages and returning Event definitions.
 *
 *  String 		description 		- The message from the device.
 **/
def parse(String description) {
	//if (state.debug) log.debug "$device.displayName Parsing raw command: " + description
    
    def result = null
    
	// zwave.parse(): 
    // The second parameter specifies which command version to return for each command type:
    // Aeon Home Energy Meter Gen2 supports:
    //  COMMAND_CLASS_METER_V3 [0x32: 3]
    //  COMMAND_CLASS_CONFIGURATION [0x70: 1]
    //  COMMAND_CLASS_MANUFACTURER_SPECIFIC_V2 [0x72: 2]
    //  COMMAND_CLASS_MULTI_CHANNEL V3 [????] - Not needed for single clamp device.
	def cmd = zwave.parse(description, [0x32: 3, 0x70: 1, 0x72: 2])
	if (cmd) {
		if (state.debug) log.debug "$device.displayName zwave.parse() returned: $cmd"
		result = zwaveEvent(cmd)
		if (state.debug) log.debug "$device.displayName zwaveEvent() returned: ${result?.inspect()}"	
	}
	return result
}

/**
 *  COMMAND_CLASS_METER_V3 (0x32)
 *
 *  Integer			deltaTime		    		Time in seconds since last report
 *  Short			meterType		    		Unknown = 0, Electric = 1, Gas = 2, Water = 3
 *  List<Short>		meterValue		    		Meter value as an array of bytes
 *  Double			scaledMeterValue			Meter value as a double
 *  List<Short>		previousMeterValue			Previous meter value as an array of bytes
 *  Double			scaledPreviousMeterValue    Previous meter value as a double
 *  Short			size						The size of the array for the meterValue and previousMeterValue
 *  Short			scale						The scale of the values: "kWh"=0, "kVAh"=1, "Watts"=2, "pulses"=3, "Volts"=4, "Amps"=5, "Power Factor"=6, "Unknown"=7
 *  Short			precision					The decimal precision of the values
 *  Short			rateType					???
 *  Boolean			scale2						???
 **/
def zwaveEvent(physicalgraph.zwave.commands.meterv3.MeterReport cmd) {
	if (cmd.scale == 0) {
    	// Accumulated Energy (kWh) - Update stats and record energy.
    	state.energy = cmd.scaledMeterValue
		updateStats()
        sendEvent(name: "dispEnergy", value: String.format("%.2f",cmd.scaledMeterValue as BigDecimal) + " kWh", displayed: false)
		return createEvent(name: "energy", value: String.format("%.2f",cmd.scaledMeterValue as BigDecimal), unit: "kWh")
	}
	else if (cmd.scale == 1) {
    	// Accumulated Energy (kVAh) - Ignore.
		//createEvent(name: "energy", value: cmd.scaledMeterValue, unit: "kVAh")
	}
	else if (cmd.scale == 2) {
    	// Instantaneous Power (Watts) - Calculate powerCost and record power:
		state.powerCost = cmd.scaledMeterValue * state.costPerKWH * 0.024
		sendEvent(name: "powerCost", value: state.powerCost, unit: "£/day")
        sendEvent(name: "dispPowerCost", value: "£" + String.format("%.2f",state.powerCost as BigDecimal) + " per day", displayed: false)
        sendEvent(name: "dispPower", value: String.format("%.1f",cmd.scaledMeterValue as BigDecimal) + " W", displayed: false)
        return createEvent(name: "power", value: String.format("%.1f",cmd.scaledMeterValue as BigDecimal), unit: "W")
	}
	else if (cmd.scale == 4) {
    	// Instantaneous Voltage (Volts)
		sendEvent(name: "dispVoltage", value: String.format("%.1f",cmd.scaledMeterValue as BigDecimal) + " V", displayed: false)
        return createEvent(name: "voltage", value: cmd.scaledMeterValue, unit: "V")
	}
	else if (cmd.scale == 5) { 
    	// Instantaneous Current (Amps)
		sendEvent(name: "dispCurrent", value: String.format("%.1f",cmd.scaledMeterValue as BigDecimal) + " A", displayed: false)
        return createEvent(name: "current", value: cmd.scaledMeterValue, unit: "A")
	}
	//else if (cmd.scale == 6) {
    	// Instantaneous Power Factor - Not supported.
	//	sendEvent(name: "dispPowerFactor", value: "PF: " + String.format("%.2f",cmd.scaledMeterValue as BigDecimal), displayed: false)
    //    return createEvent(name: "powerFactor", value: cmd.scaledMeterValue, unit: "PF")
	//}
}


/**
 *  COMMAND_CLASS_CONFIGURATION (0x70)
 *
 **/
def zwaveEvent(physicalgraph.zwave.commands.configurationv1.ConfigurationReport cmd) {
	// Translate the cmd and log the parameter configuration.

	// Translate value (byte array) back to scaledConfigurationValue (decimal):
    // This should be done in zwave.parse() but isn't implemented yet.
    // See: https://community.smartthings.com/t/zwave-configurationv2-configurationreport-dev-question/9771/6
    // I can't make this work just yet...
	//int value = java.nio.ByteBuffer.wrap(cmd.configurationValue as byte[]).getInt()
    // Instead, a brute force way
    def scValue = 0
    if (cmd.size == 1) { scValue = cmd.configurationValue[0]}
    else if (cmd.size == 2) {  scValue = cmd.configurationValue[1] + (cmd.configurationValue[0] * 0x100) }
    else if (cmd.size == 3) {  scValue = cmd.configurationValue[2] + (cmd.configurationValue[1] * 0x100) + (cmd.configurationValue[0] * 0x10000) }
    else if (cmd.size == 4) {  scValue = cmd.configurationValue[3] + (cmd.configurationValue[2] * 0x100) + (cmd.configurationValue[1] * 0x10000) + (cmd.configurationValue[0] * 0x1000000) }
    
    // Translate parameterNumber to parameterDescription:
    def parameterDescription
    switch (cmd.parameterNumber) {
        case 2:
            parameterDescription = "Energy Detection Mode"
            break
        case 3:
            parameterDescription = "Enable Selective Reporting"
            break
        case 4:
            parameterDescription = "Power Change Threshold for Auto-Report - Whole HEM (W)"
            break
        case 5:
            parameterDescription = "Power Change Threshold for Auto-Report - Clamp 1 (W)"
            break
        case 6:
            parameterDescription = "Power Change Threshold for Auto-Report - Clamp 2 (W)"
            break
        case 7:
            parameterDescription = "Power Change Threshold for Auto-Report - Clamp 3 (W)"
            break
        case 8:
            parameterDescription = "Power Percentage Change Threshold for Auto-Report - Whole HEM (%)"
            break
        case 9:
            parameterDescription = "Power Percentage Change Threshold for Auto-Report - Clamp 1 (%)"
            break
        case 10:
            parameterDescription = "Power Percentage Change Threshold for Auto-Report - Clamp 2 (%)"
            break
        case 11:
            parameterDescription = "Power Percentage Change Threshold for Auto-Report - Clamp 3 (%)"
            break
        case 13:
            parameterDescription = "Enable Reporting CRC16 Encapsulation Command"
            break
        case 101:
            parameterDescription = "Reporting Group 1 - Content Flags"
            break
        case 102:
            parameterDescription = "Reporting Group 2 - Content Flags"
            break
        case 103:
            parameterDescription = "Reporting Group 3 - Content Flags"
            break
        case 111:
            parameterDescription = "Reporting Group 1 - Report Interval (s)"
            break
        case 112:
            parameterDescription = "Reporting Group 2 - Report Interval (s)"
            break
        case 113:
            parameterDescription = "Reporting Group 3 - Report Interval (s)"
            break
        case 200:
            parameterDescription = "Partner ID"
            break
        case 252:
            parameterDescription = "Configuration Locked"
            break
        default:
            parameterDescription = "Unknown Parameter"
	}
    
	//log.debug "$device.displayName: Configuration Report: parameterNumber: $cmd.parameterNumber, parameterDescription: $parameterDescription, size: $cmd.size, scaledConfigurationValue: $scValue"
	createEvent(descriptionText: "$device.displayName: Configuration Report: parameterNumber: $cmd.parameterNumber, parameterDescription: $parameterDescription, size: $cmd.size, scaledConfigurationValue: $scValue", displayed: false)
}

/**
 *  COMMAND_CLASS_MANUFACTURER_SPECIFIC_V2 (0x72)
 *
 *  
 **/
def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
	def msr = String.format("%04X-%04X-%04X", cmd.manufacturerId, cmd.productTypeId, cmd.productId)
	if (state.debug) log.debug "$device.displayName: MSR: $msr"
	updateDataValue("MSR", msr)

	// Apply Manufacturer- or Product-specific configuration here...
}

/**
 *  Default event handler.
 *
 *  Called for all events that aren't handled above.
 **/
def zwaveEvent(physicalgraph.zwave.Command cmd) {
	if (state.debug) log.warn "$device.displayName: Unhandled: $cmd"
	[:]
}


/**********************************************************************
 *  Capability-related Commands:
 **********************************************************************/


/**
 *  refresh() - Refreshes values from the device.
 *
 *  Required for the "Refresh" capability.
 **/
def refresh() {
	delayBetween([
		zwave.meterV3.meterGet(scale: 0).format(), // Energy
		zwave.meterV3.meterGet(scale: 2).format(), // Power
		zwave.meterV3.meterGet(scale: 4).format(), // Volts
		//zwave.meterV3.meterGet(scale: 5).format(), // Current - Not included, as a request will be triggered when Power report is received.
		//zwave.meterV3.meterGet(scale: 6).format() // Power Factor - Not Supported.
	])
}


/**
 *  poll() - Polls the device.
 *
 *  Required for the "Polling" capability
 **/
def poll() {
	refresh()
}


/**
 *  reset() - Reset the Accumulated Energy figure held in the device.
 *
 *  Custom energy reporting period stats are preserved.
 **/
def reset() {
	if (state.debug) log.debug "Reseting Accumulated Energy"
    state.lastReset = new Date().format("YYYY/MM/dd \n HH:mm:ss", location.timeZone)
    sendEvent(name: "lastReset", value: state.lastReset, unit: "")
	
	// Record energy<Period> in energy<Period>Prev:
	state.energyTodayPrev = state.energyToday
	state.energyTodayStart = 0.00
	state.energyMonthPrev = state.energyMonth
	state.energyMonthStart = 0.00
	state.energyYearPrev = state.energyYear
	state.energyYearStart = 0.00
    state.energyLifetimePrev = state.energyLifetime
	state.energy = 0.00
    
    return [
		zwave.meterV3.meterReset().format(),
		zwave.meterV3.meterGet(scale: 0).format()
	]
}


/**********************************************************************
 *  Other Commands:
 **********************************************************************/


/**
 *  resetAllStats() - Reset all Accumulated Energy statistics (!)
 *
 *  Resets the Accumulated Energy figure held in the device AND resets all custom energy reporting period stats!
 **/
def resetAllStats() {
	if (state.debug) log.debug "Reseting All Accumulated Energy Stats!"
    state.lastReset = new Date().format("YYYY/MM/dd \n HH:mm:ss", location.timeZone)
    sendEvent(name: "lastReset", value: state.lastReset, unit: "")
	
	// Reset all energy<Period>Prev/Start values:
	state.energyTodayPrev = 0.00
	state.energyTodayStart = 0.00
	state.energyMonthPrev = 0.00
	state.energyMonthStart = 0.00
	state.energyYearPrev = 0.00
	state.energyYearStart = 0.00
    state.energyLifetimePrev = 0.00
	state.energy = 0.00
    
    return [
		zwave.meterV3.meterReset().format(),
		zwave.meterV3.meterGet(scale: 0).format()
	]
}


/**
 *  installed() - Runs when the device is first installed.
 **/
def installed() {
	log.debug "${device.displayName}: Installing."
	state.installedAt = now()
	state.energy = 0.00
	state.costPerKWH = 0.00
	state.costOfEnergy = 0.00
	state.lastReset = new Date().format("YYYY/MM/dd \n HH:mm:ss", location.timeZone)
    state.statsMode = 'Today'
}


/**
 *  updated() - Runs when you hit "Done" from "Edit Device".
 * 
 *  Weirdly, it seems to be called twice after hitting "Done"!
 * 
 *  Note, the updated() method is not a 'command', so it doesn't send commands by default.
 *  To execute commands from updated() you have to specifically return a HubAction object. 
 *  The response() helper wraps commands up in a HubAction so they can be sent from parse() or updated().
 *  See: https://community.smartthings.com/t/remotec-z-thermostat-configuration-with-z-wave-commands/31956/12
 **/
def updated() {

	log.debug "Updated() called"
	// Update internal state:
	state.debug = ("true" == configDebugMode)
	state.costPerKWH = configCostPerKWH as BigDecimal
    
    // Call configure() and refresh():
 	return response( [configure() , refresh() ])
}



/**
 *  updateStats() - Recalculates energy and cost for each reporting period.
 *
 *  All costs are calculated at the prevailing rate.
 *
 *   Attributes:
 *    energy                = Energy (kWh) as reported by device (ad hoc period). [Native Energy Meter attribute].
 *    costOfEnergy          = Cost of energy (ad hoc period).
 *    energyToday           = Accumulated energy (today only).
 *    costOfEnergyToday     = Cost of energy (today).
 *    energy24Hours         = Accumulated energy (last 24 hours).
 *    costOfEnergy24Hours   = Cost of energy (last 24 hours).
 *    energy7Days           = Accumulated energy (last 7 days).
 *    costOfEnergy7Days     = Cost of energy (last 7 days).
 *    energyMonth           = Accumulated energy (this month).
 *    costOfEnergyMonth     = Cost of energy (this month).
 *    energyYear            = Accumulated energy (this year).
 *    costOfEnergyYear      = Cost of energy (this year).
 *    energyLifetime        = Accumulated energy (lifetime).
 *    costOfEnergyLifetime  = Cost of energy (lifetime).
 *   
 *   Private State:
 *    costPerKWH            = Unit cost as specified by user in settings.
 *    reportingPeriod       = YYYY/MM/dd of current reporting period.
 *    energyTodayStart      = energy that was reported at the start of today. Will be zero if ad hoc period has been reset today.
 *    energyTodayPrev       = energy that was reported today, prior to lastReset. Will be zero if ad hoc period has not been reset today.
 *    energyMonthStart      = energy that was reported at the start of this month. Will be zero if ad hoc period has been reset this month.
 *    energyMonthPrev       = energy that was reported this month, prior to lastReset. Will be zero if ad hoc period has not been reset this month.
 *    energyYearStart       = energy that was reported at the start of this year. Will be zero if ad hoc period has been reset this year.
 *    energyYearPrev        = energy that was reported this year, prior to lastReset. Will be zero if ad hoc period has not been reset this year.
 *    energyLifetimePrev    = energy that was reported this lifetime, prior to lastReset. Will be zero if ad hoc period has never been reset.
 *   
 **/
private updateStats() {

	if (state.debug) log.debug "${device.displayName}: Updating Statistics"
	
	if (!state.energy) {state.energy = 0}
	if (!state.costPerKWH) {state.costPerKWH = 0}
	if (!state.reportingPeriod) {state.reportingPeriod = "Uninitialised"}
	if (!state.energyTodayStart) {state.energyTodayStart = 0}
	if (!state.energyTodayPrev) {state.energyTodayPrev = 0}
	if (!state.energyMonthStart) {state.energyMonthStart = 0}
	if (!state.energyMonthPrev) {state.energyMonthPrev = 0}
	if (!state.energyYearStart) {state.energyYearStart = 0}
	if (!state.energyYearPrev) {state.energyYearPrev = 0}
	if (!state.energyLifetimePrev) {state.energyLifetimePrev = 0}
	
	// Check if reportingPeriod has changed (i.e. it's a new day):
	def today = new Date().format("YYYY/MM/dd", location.timeZone)
	if ( today != state.reportingPeriod) {
		// It's a new Reporting Period:
		log.info "${device.displayName}: New Reporting Period: ${today}"
        
        // Check if new year:
		if ( today.substring(0,4) != state.reportingPeriod.substring(0,4)) {
        	state.energyYearStart = state.energy
			state.energyYearPrev = 0.00
        }

        // Check if new month:
		if ( today.substring(0,7) != state.reportingPeriod.substring(0,7)) {
        	state.energyMonthStart = state.energy
			state.energyMonthPrev = 0.00
        }

        // Daily rollover:
		state.energyTodayStart = state.energy
		state.energyTodayPrev = 0.00
        
        // Update reportingPeriod:
        state.reportingPeriod = today
	}
	
    // energy (ad hoc period):
    // Nothing to caclulate, just need to update dispEnergy:
    sendEvent(name: "dispEnergy", value: String.format("%.2f",state.energy as BigDecimal) + " kWh", displayed: false)
    
    // costOfEnergy (ad hoc period):
	try {
		state.costOfEnergy = state.energy * state.costPerKWH
		if (state.debug) log.debug "${device.displayName}: Cost of Energy: £${state.costOfEnergy}"
		sendEvent(name: "costOfEnergy", value: state.costOfEnergy, unit: "£")
        sendEvent(name: "dispCostOfEnergy", value: "£" + String.format("%.2f",state.costOfEnergy as BigDecimal), displayed: false)
	} catch (e) { log.debug e }

	// energyToday:
	try {
		state.energyToday = state.energy + state.energyTodayPrev - state.energyTodayStart
		if (state.debug) log.debug "${device.displayName}: Energy Today: ${state.energyToday} kWh"
		sendEvent(name: "energyToday", value: state.energyToday, unit: "kWh")
	} catch (e) { log.debug e }

	// costOfEnergyToday:
	try {
		state.costOfEnergyToday = (state.energyToday * state.costPerKWH) as BigDecimal
		if (state.debug) log.debug "${device.displayName}: Cost of Energy Today: £${state.costOfEnergyToday}"
		sendEvent(name: "costOfEnergyToday", value: state.costOfEnergyToday, unit: "£")
	} catch (e) { log.debug e }

	// energyMonth:
	try {
		state.energyMonth = state.energy + state.energyMonthPrev - state.energyMonthStart
		if (state.debug) log.debug "${device.displayName}: Energy This Month: ${state.energyMonth} kWh"
		sendEvent(name: "energyMonth", value: state.energyMonth, unit: "kWh")
	} catch (e) { log.debug e }

	// costOfEnergyMonth:
	try {
		state.costOfEnergyMonth = (state.energyMonth * state.costPerKWH) as BigDecimal
		if (state.debug) log.debug "${device.displayName}: Cost of Energy This Month: £${state.costOfEnergyMonth}"
		sendEvent(name: "costOfEnergyMonth", value: state.costOfEnergyMonth, unit: "£")
	} catch (e) { log.debug e }

	// energyYear:
	try {
		state.energyYear = state.energy + state.energyYearPrev - state.energyYearStart
		if (state.debug) log.debug "${device.displayName}: Energy This Year: ${state.energyYear} kWh"
		sendEvent(name: "energyYear", value: state.energyYear, unit: "kWh")
	} catch (e) { log.debug e }

	// costOfEnergyYear:
	try {
		state.costOfEnergyYear = (state.energyYear * state.costPerKWH) as BigDecimal
		if (state.debug) log.debug "${device.displayName}: Cost of Energy This Year: £${state.costOfEnergyYear}"
		sendEvent(name: "costOfEnergyYear", value: state.costOfEnergyYear, unit: "£")
	} catch (e) { log.debug e }

	// energyLifetime:
	try {
		state.energyLifetime = state.energy + state.energyLifetimePrev
		if (state.debug) log.debug "${device.displayName}: Energy This Lifetime: ${state.energyLifetime} kWh"
		sendEvent(name: "energyLifetime", value: state.energyLifetime, unit: "kWh")
	} catch (e) { log.debug e }

	// costOfEnergyLifetime:
	try {
		state.costOfEnergyLifetime = (state.energyLifetime * state.costPerKWH) as BigDecimal
		if (state.debug) log.debug "${device.displayName}: Cost of Energy This Lifetime: £${state.costOfEnergyLifetime}"
		sendEvent(name: "costOfEnergyLifetime", value: state.costOfEnergyLifetime, unit: "£")
	} catch (e) { log.debug e }
    
    // Moving Periods - Calculated by looking up previous values of energyLifetime:
    
    // energy24Hours:
	try {
    	// We need the last value of energyLifetime that is at least 24 hours old.
		//  We get previous values of energyLifetime between 1 and 7 days old, in case the device has been off for a while.
		//  So long as the device reported energy back at least once during this period, we should get a result.
        //  As results are returned in reverse chronological order, we just need the first 1 record.
		
        // Use a calendar object to create offset dates:
		Calendar cal = new GregorianCalendar()
		cal.add(Calendar.DATE, -1 )
		Date end = cal.getTime()
		cal.add(Calendar.DATE, -6 )
		Date start = cal.getTime()

		def previousELStates = device.statesBetween("energyLifetime", start, end,[max: 1])
		def previousEL
    	if (previousELStates) { 
        	previousEL = previousELStates[previousELStates.size -1].value as BigDecimal 
            if (state.debug) log.debug "${device.displayName}: energyLifetime 24 Hours Ago was: ${previousEL} kWh"
        }
    	else { 
        	previousEL = 0.0 
        	if (state.debug) log.debug "${device.displayName}: No value for energyLifetime 24 Hours Ago!"
        }
    	if (previousEL > state.energyLifetime) { previousEL = 0.0 } // If energyLifetime has been reset, discard previous value.
        
    	state.energy24Hours = state.energyLifetime - previousEL
        if (state.debug) log.debug "${device.displayName}: Energy Last 24 Hours: ${state.energy24Hours} kWh"
		sendEvent(name: "energy24Hours", value: state.energy24Hours, unit: "kWh")
	} catch (e) { log.debug e }    
    
	// costOfEnergy24Hours:
	try {
		state.costOfEnergy24Hours = (state.energy24Hours * state.costPerKWH) as BigDecimal
		if (state.debug) log.debug "${device.displayName}: Cost of Energy Last 24 Hours: £${state.costOfEnergy24Hours}"
		sendEvent(name: "costOfEnergy24Hours", value: state.costOfEnergy24Hours, unit: "£")
	} catch (e) { log.debug e }
    
    
    // energy7Days:
	try {
    	// We need the last value of energyLifetime, up to 7 old (previous states are only kept for 7 days).
		//  We get previous values of energyLifetime between 6 and 7 days old, in case the device has been off for a while.
		//  So long as the device reported energy back at least once during this period, we should get a result.
        //  As results are returned in reverse chronological order, we need the last record, so we request the max of 1000.
		//  If there were more than 1000 updates between start and end, we won't get the oldest one,
        //  however stats should normally only be generated every 10 mins at most.
		
    	// Use a calendar object to create offset dates:
		Calendar cal = new GregorianCalendar()
		cal.add(Calendar.DATE, -6 )
		Date end = cal.getTime()
		cal.add(Calendar.DATE, -1 )
		Date start = cal.getTime()

		// Get previous values of energyLifetime between 7 Days and 6 days 23 hours old: 
		def previousELStates = device.statesBetween("energyLifetime", start, end,[max: 1000])
		def previousEL
    	if (previousELStates) { 
        	previousEL = previousELStates[previousELStates.size -1].value as BigDecimal 
            if (state.debug) log.debug "${device.displayName}: energyLifetime 7 Days Ago was: ${previousEL} kWh"
        }
    	else { 
        	previousEL = 0.0 
        	if (state.debug) log.debug "${device.displayName}: No value for energyLifetime 7 Days Ago!"
        }
    	if (previousEL > state.energyLifetime) { previousEL = 0.0 } // If energyLifetime has been reset, discard previous value.
        
    	state.energy7Days = state.energyLifetime - previousEL
		if (state.debug) log.debug "${device.displayName}: Energy Last 7 Days: ${state.energy7Days} kWh"
		sendEvent(name: "energy7Days", value: state.energy7Days, unit: "kWh")
	} catch (e) { log.debug e }    
    
	// costOfEnergy7Days:
	try {
		state.costOfEnergy7Days = (state.energy7Days * state.costPerKWH) as BigDecimal
		if (state.debug) log.debug "${device.displayName}: Cost of Energy Last 7 Days: £${state.costOfEnergy7Days}"
		sendEvent(name: "costOfEnergy7Days", value: state.costOfEnergy7Days, unit: "£")
	} catch (e) { log.debug e }
    
    
    //disp<>Period:
    if ('Today' == state.statsMode) {
    	sendEvent(name: "dispEnergyPeriod", value: String.format("%.2f",state.energyToday as BigDecimal) + " kWh", displayed: false)
		sendEvent(name: "dispCostOfEnergyPeriod", value: "£" + String.format("%.2f",state.costOfEnergyToday as BigDecimal), displayed: false)
    }
    if ('Last 24 Hours' == state.statsMode) {
    	sendEvent(name: "dispEnergyPeriod", value: String.format("%.2f",state.energy24Hours as BigDecimal) + " kWh", displayed: false)
		sendEvent(name: "dispCostOfEnergyPeriod", value: "£" + String.format("%.2f",state.costOfEnergy24Hours as BigDecimal), displayed: false)
    }
    if ('Last 7 Days' == state.statsMode) {
    	sendEvent(name: "dispEnergyPeriod", value: String.format("%.2f",state.energy7Days as BigDecimal) + " kWh", displayed: false)
		sendEvent(name: "dispCostOfEnergyPeriod", value: "£" + String.format("%.2f",state.costOfEnergy7Days as BigDecimal), displayed: false)
    }
    if ('This Month' == state.statsMode) {
    	sendEvent(name: "dispEnergyPeriod", value: String.format("%.2f",state.energyMonth as BigDecimal) + " kWh", displayed: false)
		sendEvent(name: "dispCostOfEnergyPeriod", value: "£" + String.format("%.2f",state.costOfEnergyMonth as BigDecimal), displayed: false)
    }
    if ('This Year' == state.statsMode) {
    	sendEvent(name: "dispEnergyPeriod", value: String.format("%.2f",state.energyYear as BigDecimal) + " kWh", displayed: false)
		sendEvent(name: "dispCostOfEnergyPeriod", value: "£" + String.format("%.2f",state.costOfEnergyYear as BigDecimal), displayed: false)
    }
    if ('Lifetime' == state.statsMode) {
    	sendEvent(name: "dispEnergyPeriod", value: String.format("%.2f",state.energyLifetime as BigDecimal) + " kWh", displayed: false)
		sendEvent(name: "dispCostOfEnergyPeriod", value: "£" + String.format("%.2f",state.costOfEnergyLifetime as BigDecimal), displayed: false)
    }
    
}


/**
 *  cycleStats() - Cycle displayed statistics period.
 **/
def cycleStats() {
	if (state.debug) log.debug "$device.displayName: Cycling Stats"
	
    if ('Today' == state.statsMode) {
    	state.statsMode = 'Last 24 Hours'
    	sendEvent(name: "dispEnergyPeriod", value: String.format("%.2f",state.energy24Hours as BigDecimal) + " kWh", displayed: false)
		sendEvent(name: "dispCostOfEnergyPeriod", value: "£" + String.format("%.2f",state.costOfEnergy24Hours as BigDecimal), displayed: false)
    }
    else if ('Last 24 Hours' == state.statsMode) {
    	state.statsMode = 'Last 7 Days'
    	sendEvent(name: "dispEnergyPeriod", value: String.format("%.2f",state.energy7Days as BigDecimal) + " kWh", displayed: false)
		sendEvent(name: "dispCostOfEnergyPeriod", value: "£" + String.format("%.2f",state.costOfEnergy7Days as BigDecimal), displayed: false)
    }
    else if ('Last 7 Days' == state.statsMode) {
    	state.statsMode = 'This Month'
    	sendEvent(name: "dispEnergyPeriod", value: String.format("%.2f",state.energyMonth as BigDecimal) + " kWh", displayed: false)
		sendEvent(name: "dispCostOfEnergyPeriod", value: "£" + String.format("%.2f",state.costOfEnergyMonth as BigDecimal), displayed: false)
    }
    else if ('This Month' == state.statsMode) {
    	state.statsMode = 'This Year'
    	sendEvent(name: "dispEnergyPeriod", value: String.format("%.2f",state.energyYear as BigDecimal) + " kWh", displayed: false)
		sendEvent(name: "dispCostOfEnergyPeriod", value: "£" + String.format("%.2f",state.costOfEnergyYear as BigDecimal), displayed: false)
    }
    else if ('This Year' == state.statsMode) {
    	state.statsMode = 'Lifetime'
    	sendEvent(name: "dispEnergyPeriod", value: String.format("%.2f",state.energyLifetime as BigDecimal) + " kWh", displayed: false)
		sendEvent(name: "dispCostOfEnergyPeriod", value: "£" + String.format("%.2f",state.costOfEnergyLifetime as BigDecimal), displayed: false)
    }
    else  {
    	state.statsMode = 'Today'
    	sendEvent(name: "dispEnergyPeriod", value: String.format("%.2f",state.energyToday as BigDecimal) + " kWh", displayed: false)
		sendEvent(name: "dispCostOfEnergyPeriod", value: "£" + String.format("%.2f",state.costOfEnergyToday as BigDecimal), displayed: false)
    }
    
	sendEvent(name: "statsMode", value: state.statsMode, displayed: false)
	if (state.debug) log.debug "$device.displayName: StatsMode changed to: ${state.statsMode}"
	
}


/**
 *  configure() - Configure physical device parameters.
 *
 *  Gets values from the Preferences section.
 **/
def configure() {
    
    if (state.debug) log.debug "$device.displayName: Configuring Device"
    
    // Build Commands based on input preferences:
    // Some basic validation is done, if any values are out of range they're set back to default.
    //  It doesn't seem possible to read the defaultValue of each input from $settings, so default values are duplicated here.
    def cmds = []
    
	// Parameter 2 - Energy Detection Mode:
	Short CP2 
    if (configEnergyDetectionMode == "Wattage, absolute kWh") {CP2 = 0}
	else if (configEnergyDetectionMode == "+/-Wattage, algebraic sum kWh") {CP2 = 1}
	else if (configEnergyDetectionMode == "+/-Wattage, +ive kWh (consuming electricity)") {CP2 = 2}
	else if (configEnergyDetectionMode == "+/-Wattage, -iv kWh (generating electricity)") {CP2 = 3}
	else {CP2 = 0}
	cmds << zwave.configurationV1.configurationSet(parameterNumber: 2, size: 1, scaledConfigurationValue: CP2).format() 
    cmds << zwave.configurationV1.configurationGet(parameterNumber: 2).format()
	
	// Parameter 3 - Selective Reporting:
	cmds << zwave.configurationV1.configurationSet(parameterNumber: 3, size: 1, scaledConfigurationValue: ("true" == configSelectiveReporting) ? 1 : 0).format()
    cmds << zwave.configurationV1.configurationGet(parameterNumber: 3).format()		
	
    // Parameter 4 - Power Change Threshold for Auto-Report - Whole HEM (W):
	Long CP4 = settings.configPowerThresholdAbs_HEM as Long  
    if ((CP4 == null) || (CP4 < 0) || (CP4 > 60000)) { CP4 = 50 }
	cmds << zwave.configurationV1.configurationSet(parameterNumber: 4, size: 2, scaledConfigurationValue: CP4).format()
    cmds << zwave.configurationV1.configurationGet(parameterNumber: 4).format()

    // Parameter 8 - Power Percentage Change Threshold for Auto-Report - Whole HEM (%):
    Long CP8 = settings.configPowerThresholdPercent_HEM as Long  
    if ((CP8 == null) || (CP8 < 0) || (CP8 > 100)) { CP8 = 10 }
	cmds << zwave.configurationV1.configurationSet(parameterNumber: 8, size: 1, scaledConfigurationValue: CP8).format()
    cmds << zwave.configurationV1.configurationGet(parameterNumber: 8).format()

	// Reporting Group Flags:
	//  energy = 1
	//  power = 2
	//  voltage = 4
	//  current = 8
        
    // Parameter 101 - Reporting Group 1 - Content Flags:
	// HARD-CODED to contain power (W) and current [2+8 = 10]:
	cmds << zwave.configurationV1.configurationSet(parameterNumber: 101, size: 4, scaledConfigurationValue: 10).format()
	cmds << zwave.configurationV1.configurationGet(parameterNumber: 101).format()
	
	// Parameter 102 - Reporting Group 2 - Content Flags:
	// HARD-CODED to contain energy [1]:
	cmds << zwave.configurationV1.configurationSet(parameterNumber: 102, size: 4, scaledConfigurationValue: 1).format()
	cmds << zwave.configurationV1.configurationGet(parameterNumber: 102).format()
	
	// Parameter 103 - Reporting Group 3 - Content Flags:
	// HARD-CODED to contain voltage [4]:
	cmds << zwave.configurationV1.configurationSet(parameterNumber: 103, size: 4, scaledConfigurationValue: 4).format()
	cmds << zwave.configurationV1.configurationGet(parameterNumber: 103).format()
	
	
	// Parameter 111 - Reporting Group 1 - Report Interval (s):
	Long CP111 = settings.configReportGroup1Interval as Long  
    if ((CP111 == null) || (CP111 < 1) || (CP111 > 2147483647)) { CP111 = 60 }
	cmds << zwave.configurationV1.configurationSet(parameterNumber: 111, size: 4, scaledConfigurationValue: CP111).format()
	cmds << zwave.configurationV1.configurationGet(parameterNumber: 111).format()
	
	// Parameter 112 - Reporting Group 2 - Report Interval (s):
	Long CP112 = settings.configReportGroup2Interval as Long  
    if ((CP112 == null) || (CP112 < 1) || (CP112 > 2147483647)) { CP112 = 600 }
	cmds << zwave.configurationV1.configurationSet(parameterNumber: 112, size: 4, scaledConfigurationValue: CP112).format()
	cmds << zwave.configurationV1.configurationGet(parameterNumber: 112).format()
	
	// Parameter 113 - Reporting Group 3 - Report Interval (s):
	Long CP113 = settings.configReportGroup3Interval as Long  
    if ((CP113 == null) || (CP113 < 1) || (CP113 > 2147483647)) { CP113 = 600 }
	cmds << zwave.configurationV1.configurationSet(parameterNumber: 113, size: 4, scaledConfigurationValue: CP113).format()
	cmds << zwave.configurationV1.configurationGet(parameterNumber: 113).format()
	
	
    // Return:
    if ( cmds != [] && cmds != null ) return delayBetween(cmds, 500) else return
}


/**
 *  test() - Temp testing method.
 **/
def test() {
	if (state.debug) log.debug "${device.displayName}: Testing"
}

