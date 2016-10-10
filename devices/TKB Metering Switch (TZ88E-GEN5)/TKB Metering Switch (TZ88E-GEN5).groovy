/**
 *  Copyright 2016 David Lomas (codersaur)
 *
 *  Name: TKB Metering Switch (TZ88E-GEN5)
 *
 *  Author: David Lomas (codersaur)
 *
 *  Date: 2016-10-10
 *
 *  Version: 1.11
 *
 *  Description:
 *   - This device handler is written specifically for the TKB Metering Switch (TZ88E-GEN5).
 *   - Supports live reporting of energy, power, current, voltage, and powerFactor. Press the 'Now' tile to refresh.
 *      (voltage and powerFactor tiles are not shown by default, but you can enable them below).
 *   - Supports reporting of energy usage and cost over an ad hoc period, based on the 'energy' figure reported by 
 *     the device. Press the 'Since...' tile to reset.
 *   - Supports additional reporting of energy usage and cost over multiple pre-defined periods:
 *       'Today', 'Last 24 Hours', 'Last 7 Days', 'This Month', 'This Year', and 'Lifetime'
 *     These can be cycled through by pressing the 'statsMode' tile. There's also a tile that will reset all Energy
 *     Stats periods, but it's hidden by default.
 *   - All configurable device parameters can be set from the device settings. Refer to the TZ88E-GEN5 instruction 
 *     manual for full details.
 *   - The Multi-tile will indicate if the physical switch is enabled/disabled, or if RF command behaviour is altered.
 *   - If you are re-using this device, please use your own hosting for the icons.
 *
 *  TZ88E-GEN5 device notes:
 *   - Auto-Meter-Reports for power and energy are sent to association group 1. The hub needs to be added to
 *     this group to receive these auto-reports (this is done for you if you enable 'Enable Auto-Reporting' in
 *     the device settings).
 *   - The device cannot be configured to send auto-reports for voltage, current, or powerFactor. 
 *     Therefore, meter reports for current and powerFactor are requested whenever a meter report for power is received.
 *     Additionally, a meter report for voltage is reqeusted whenever a meter report for energy is received.
 *
 *  Version History:
 *
 *   2016-10-10: v1.11
 *    - 'Voltage Measurement' capability is now accepted.
 *
 *   2016-03-02: v1.10
 *    - Meter reports for current and powerFactor are requested whenever a meter report for power is received.
 *    - Meter reports for voltage are reqeusted whenever a meter report for energy is received.
 *
 *   2016-03-01: v1.09
 *    - Cleaned up parse() method.
 *
 *   2016-02-28: v1.08
 *    - Fixed required properties on input parameters.
 *
 *   2016-02-14: v1.07
 *    - General tidy up.
 *    - poll() now just calls refresh().
 *    - standardised date format in installed().
 * 
 *   2016-02-12: v1.06
 *    - New Icons, hosted on GitHub.
 *    - A meter report for current is now requested whenever a meter report for power is received.
 *    - Fixed execution of commands in configure() when called from updated(), so a 'configure' tile is not needed.
 *    - resetAllStats() method to reset all Accumulated Energy statistics! Corresponding tile is hidden by default. 
 *
 *   2016-02-11: v1.05
 *    - Improved calculation of energy24Hours.
 *
 *   2016-02-10: v1.04
 *    - Added energy<> and costOfEnergy<> stats for 'Last 24 Hours' and 'Last 7 Days'.
 *
 *   2016-02-09: v1.03
 *    - Added energy<> and costOfEnergy<> stats for Month/Year/Lifetime.
 *    - statsMode tile now cycles through stats modes.
 *    - Fixed formatting of displayed values by using disp* attributes (yuk).
 *    - Secondary information on Multi-tile indicates if switch is enabled/disabled, or RF command behaviour is altered.
 *
 *   2016-02-08: v1.02
 *    - Added energyToday & costOfEnergyToday stats.
 *    - All stats calculation moved to updateStats().
 *
 *   2016-02-07: v1.01
 *    - Added ConfigurationReport event parser.
 *    - Added configurable settings for all device parameters.
 *    - Added multi-attribute tile.
 *    - Added support for Voltage, Current, and Power Factor.
 *    - Added Total Cost, based on CostPerKWh setting.
 *
 *   2016-02-06: v1.0 - Initial Version for TZ88E-GEN5.
 *    - Added fingerprint for TZ88E-GEN5.
 * 
 *  To Do:
 *   - Option to specify a '£/day' fixed charge, which is added to all energy cost calculations.
 *   - Process Alarm reports.
 *   - Add Min/Max/Ave stats (instMode tile to cycle through: Now/Min/Max/Ave).
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
 **/

metadata {
	definition (name: "TKB Metering Switch (TZ88E-GEN5)", namespace: "codersaur", author: "David Lomas") {
		capability "Actuator"
		capability "Switch"
		capability "Power Meter"
		capability "Energy Meter"
		capability "Voltage Measurement"
		capability "Polling"
		capability "Refresh"
		capability "Configuration"
		capability "Sensor"

		command "reset"
        command "refresh"
        command "configure"
        command "updated"
        command "poll"
        command "cycleStats"
		command "resetAllStats"
		command "test"
        
		// Standard (Capability) Attributes:
		attribute "switch", "string"
        attribute "power", "number"
        attribute "energy", "number" // Energy (kWh) as reported by device (ad hoc period).
        
        // Custom Attributes:
        attribute "current", "number"
        attribute "voltage", "number"
        attribute "powerFactor", "number"
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
        attribute "secondaryInfo", "string"
        
        // Display Attributes:
        // These are only required because the UI lacks number formatting and strips leading zeros.
        attribute "dispPower", "string"
        attribute "dispCurrent", "string"
        attribute "dispVoltage", "string"
        attribute "dispPowerFactor", "string"
        attribute "dispEnergy", "string"
        attribute "dispCostOfEnergy", "string"
        attribute "dispEnergyPeriod", "string"
        attribute "dispCostOfEnergyPeriod", "string"
        
        // Fingerprints:
		fingerprint deviceId:"0x1001", inClusters:"0x5E 0x86 0x72 0x98 0x5A 0x85 0x59 0x73 0x25 0x20 0x27 0x32 0x70 0x71 0x75 0x7A"
	}

	// Tile definitions:
	tiles(scale: 2) {
    
		// Main Tiles:
        standardTile("switch", "device.switch", width: 2, height: 2, decoration: "flat", canChangeIcon: true) {
			state "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
			state "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
		}
        
        // Multi Tile:
		multiAttributeTile(name:"multi1", type: "generic", width: 4, height: 4, canChangeIcon: true) {
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#79b821"
				attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
			}
			tileAttribute ("device.secondaryInfo", key: "SECONDARY_CONTROL") {
				attributeState "default", label:'${currentValue}'
			}
		}
		
		// Instantaneous Values:
		valueTile("instMode", "device.dispPower", decoration: "flat", width: 2, height: 1) {
			state "default", label:'Now:', action:"refresh.refresh", icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_refresh.png"
		}
		valueTile("power", "device.dispPower", decoration: "flat", width: 2, height: 1) {
			state "default", label:'${currentValue}', icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_top_bottom_2.png"
		}
		valueTile("current", "device.dispCurrent", decoration: "flat", width: 2, height: 1) {
			state "default", label:'${currentValue}', icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_top_bottom_2.png"
		}
		valueTile("voltage", "device.dispVoltage", decoration: "flat", width: 2, height: 1) {
			state "default", label:'${currentValue}', icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_top_bottom_2.png"
		}
		valueTile("powerFactor", "device.dispPowerFactor", decoration: "flat", width: 2, height: 1) {
			state "default", label:'${currentValue}', icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_top_bottom_2.png"
		}
		
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
		main(["switch","power","energy"])
		details([
			// Multi Tile:
			"multi1"
			// Instantaneous Values:
			,"instMode","power", "current" //,"voltage", "powerFactor"
			// Ad Hoc Stats:
			,"lastReset", "energy", "costOfEnergy"	
			// Energy Stats:
			,"statsMode", "energyPeriod", "costOfEnergyPeriod" //,"costPerKWH"
			// Action Buttons:
			//, "refresh","resetAllStats","configure","test"
		])
	}
    
    preferences {
    	
        input "configCostPerKWH", "string", title: "Energy Cost (£/kWh)", defaultValue: "0.1253", required: true, displayDuringSetup: true
    	input "configAutoReport", "boolean", title: "Enable Auto-Reporting?", defaultValue: true, required: false, displayDuringSetup: true

		// Device Configuration Parameters:
    	input "configParameter1", "number", title: "Power Report Interval (x5sec):", defaultValue: 12, required: false, displayDuringSetup: true // 1 min.
    	input "configParameter2", "number", title: "Energy Report Interval (x10min):", defaultValue: 1, required: false, displayDuringSetup: true // 10 min.
        input "configParameter3", "number", title: "Current Threshold for Load Caution (x0.01A):", defaultValue: 1300, required: false, displayDuringSetup: true
        input "configParameter4", "number", title: "Energy Threshold for Load Caution (kWh):", defaultValue: 10000, required: false, displayDuringSetup: true
        input "configParameter5", "enum", title: "Restore Switch State Mode:", 
			options:["Last State", "Off", "On"], defaultValue: "Last State", required: false, displayDuringSetup: true
        input "configParameter6", "boolean", title: "Enable Switch?", defaultValue: true, required: false, displayDuringSetup: true
    	input "configParameter7", "enum", title: "LED Indication Mode:", 
			options:["Show Switch State", "Night Mode"], defaultValue: "Show Switch State", required: false, displayDuringSetup: true
        input "configParameter8", "number", title: "Auto-Off Timer (s):", defaultValue: 0, required: false, displayDuringSetup: true
        input "configParameter9", "enum", title: "RF Off Command Mode:", 
			options:["Switch Off", "Ignore", "Toggle State", "Switch On"], defaultValue: "Switch Off", required: false, displayDuringSetup: true		
        
		// Debug Mode:
		input "configDebugMode", "boolean", title: "Enable debug logging?", defaultValue: true, required: false, displayDuringSetup: true
    }
}


/**********************************************************************
 *  Z-wave Event Handlers.
 **********************************************************************/

/**
 *  parse() - Called when messages from a device are received by the hub.
 *
 *  The parse method is responsible for interpreting those messages and returning Event definitions.
 *
 *  String 		description 		- The message from the device.
 **/
def parse(String description) {
	if (state.debug) log.debug "$device.displayName Parsing raw command: " + description
    
    def result = null
    
	// zwave.parse: 
    // The second parameter specifies which command version to return for each command type:
    // TZ88E-GEN5 supports:
    //  COMMAND_CLASS_BASIC [0x20: 1]
    //  COMMAND_CLASS_SWITCH_BINARY [0x25: 1]
    //  COMMAND_CLASS_METER_V3 [0x32: 3]
    //  COMMAND_CLASS_CONFIGURATION [0x70: 1]
    //  COMMAND_CLASS_MANUFACTURER_SPECIFIC_V2 [0x72: 2]
    //  ...
	def cmd = zwave.parse(description, [0x20: 1, 0x25: 1, 0x32: 3, 0x70: 1, 0x72: 2])
	if (cmd) {
		if (state.debug) log.debug "$device.displayName zwave.parse() returned: $cmd"
		result = zwaveEvent(cmd)
		if (state.debug) log.debug "$device.displayName zwaveEvent() returned: ${result?.inspect()}"	
	}
	return result
}

/**
 *  COMMAND_CLASS_BASIC (0x20)
 *
 *  Short	value	0xFF for on, 0x00 for off
 **/
def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd)
{
	def evt = createEvent(name: "switch", value: cmd.value ? "on" : "off", type: "physical")
	if (evt.isStateChange) {
		[evt, response(["delay 1000", zwave.meterV2.meterGet(scale: 2).format()])]
	} else {
		evt
	}
}

/**
 *  COMMAND_CLASS_SWITCH_BINARY (0x25)
 *
 *  Short	value	0xFF for on, 0x00 for off
 **/
def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd)
{
	def evt = createEvent(name: "switch", value: cmd.value ? "on" : "off", type: "digital")
	if (evt.isStateChange) {
		[evt, response(["delay 1000", zwave.meterV3.meterGet(scale: 2).format()])]
	} else {
		evt
	}
}

/**
 *  COMMAND_CLASS_METER_V3 (0x32)
 * 
 *  Process Meter Report. 
 *  If an energy report is received, a voltage report is also requested.
 *  If a power report is received, current and powerFactor reports are reqeusted.
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
    	// Accumulated Energy (kWh) - Update stats and request voltage.
    	state.energy = cmd.scaledMeterValue
		updateStats()
        sendEvent(name: "dispEnergy", value: String.format("%.2f",cmd.scaledMeterValue as BigDecimal) + " kWh", displayed: false)
		def event = createEvent(name: "energy", value: cmd.scaledMeterValue, unit: "kWh")
        def cmds = []
        cmds << "delay 1000"
    	cmds << zwave.meterV3.meterGet(scale: 4).format() // Request voltage (Volts).
        return [event, response(cmds)] // return a list containing the event and the result of response(). 
	} else if (cmd.scale == 1) {
    	// Accumulated Energy (kVAh) - Ignore.
		//createEvent(name: "energy", value: cmd.scaledMeterValue, unit: "kVAh")
	} else if (cmd.scale == 2) {
    	// Instantaneous Power (Watts) - Record power, and requst current & powerFactor.
		sendEvent(name: "dispPower", value: String.format("%.1f",cmd.scaledMeterValue as BigDecimal) + " W", displayed: false)
        def event = createEvent(name: "power", value: cmd.scaledMeterValue, unit: "W")
        def cmds = []
        cmds << "delay 1000"
    	cmds << zwave.meterV3.meterGet(scale: 5).format() // Request current (Amps).
        cmds << "delay 1000"
    	cmds << zwave.meterV3.meterGet(scale: 6).format() // Request powerFactor.
        return [event, response(cmds)] // return a list containing the event and the result of response().
	} else if (cmd.scale == 4) {
    	// Instantaneous Voltage (Volts)
		sendEvent(name: "dispVoltage", value: String.format("%.1f",cmd.scaledMeterValue as BigDecimal) + " V", displayed: false)
        return createEvent(name: "voltage", value: cmd.scaledMeterValue, unit: "V")
	} else if (cmd.scale == 5) { 
    	// Instantaneous Current (Amps)
		sendEvent(name: "dispCurrent", value: String.format("%.1f",cmd.scaledMeterValue as BigDecimal) + " A", displayed: false)
        return createEvent(name: "current", value: cmd.scaledMeterValue, unit: "A")
	} else if (cmd.scale == 6) {
    	// Instantaneous Power Factor
		sendEvent(name: "dispPowerFactor", value: "PF: " + String.format("%.2f",cmd.scaledMeterValue as BigDecimal), displayed: false)
        return createEvent(name: "powerFactor", value: cmd.scaledMeterValue, unit: "PF")
	}
}

/**
 *  COMMAND_CLASS_CONFIGURATION (0x70)
 *
 *  Log received configuration values.
 **/
def zwaveEvent(physicalgraph.zwave.commands.configurationv1.ConfigurationReport cmd) {

	// Translate value (byte array) back to scaledConfigurationValue (decimal):
    // This should be done in zwave.parse() but isn't implemented yet.
    // See: https://community.smartthings.com/t/zwave-configurationv2-configurationreport-dev-question/9771/6
    // I can't make this work just yet...
	//  int value = java.nio.ByteBuffer.wrap(cmd.configurationValue as byte[]).getInt()
    // Instead, a brute force way
    def scValue = 0
    if (cmd.size == 1) { scValue = cmd.configurationValue[0]}
    else if (cmd.size == 2) {  scValue = cmd.configurationValue[1] + (cmd.configurationValue[0] * 0x100) }
    else if (cmd.size == 3) {  scValue = cmd.configurationValue[2] + (cmd.configurationValue[1] * 0x100) + (cmd.configurationValue[0] * 0x10000) }
    else if (cmd.size == 4) {  scValue = cmd.configurationValue[3] + (cmd.configurationValue[2] * 0x100) + (cmd.configurationValue[1] * 0x10000) + (cmd.configurationValue[0] * 0x1000000) }

    // Translate parameterNumber to parameterDescription:
    def parameterDescription
    switch (cmd.parameterNumber) {
        case 1:
            parameterDescription = "Power Report Interval (x5sec)"
            break
        case 2:
            parameterDescription = "Energy Report Interval (x10min)"
            break
        case 3:
            parameterDescription = "Current Threshold for Load Caution (x0.01A)"
            break
        case 4:
            parameterDescription = "Energy Threshold for Load Caution (kWh)"
            break
        case 5:
            parameterDescription = "Restore Switch State Mode"
            break
        case 6:
            parameterDescription = "Enable Switch"
            break
        case 7:
            parameterDescription = "LED Indication Mode"
            break
        case 8:
            parameterDescription = "Auto-Off Timer (s)"
            break
        case 9:
            parameterDescription = "RF Off Command Mode"
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
	if (state.debug) log.debug "$device.displayName: Unhandled: $cmd"
	[:]
}


/**********************************************************************
 *  Capability-related Commands:
 **********************************************************************/

/**
 *  on() - Turns the switch on.
 *
 *  Required for the "Switch" capability.
 **/
def on() {
	[
		zwave.basicV1.basicSet(value: 0xFF).format(),
		zwave.switchBinaryV1.switchBinaryGet().format(),
		"delay 1000",
		zwave.meterV3.meterGet(scale: 2).format()
	]
}


/**
 *  off() - Turns the switch off.
 *
 *  Required for the "Switch" capability.
 **/
def off() {
	[
		zwave.basicV1.basicSet(value: 0x00).format(),
		zwave.switchBinaryV1.switchBinaryGet().format(),
		"delay 1000",
		zwave.meterV3.meterGet(scale: 2).format()
	]
}


/**
 *  refresh() - Refreshes values from the device. Same as poll()?
 *
 *  Required for the "Refresh" capability.
 **/
def refresh() {
	delayBetween([
		zwave.switchBinaryV1.switchBinaryGet().format(),
		zwave.meterV3.meterGet(scale: 0).format(), // Energy
		zwave.meterV3.meterGet(scale: 2).format() // Power
		//zwave.meterV3.meterGet(scale: 4).format(), // Volts - Not included, as a request will be triggered when energy report is received.
		//zwave.meterV3.meterGet(scale: 5).format(), // Current - Not included, as a request will be triggered when power report is received.
		//zwave.meterV3.meterGet(scale: 6).format() // Power Factor - Not included, as a request will be triggered when power report is received.
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
		"delay 1000",
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
		"delay 1000",
		zwave.meterV3.meterGet(scale: 0).format()
	]
}


/**
 *  installed() - Runs when the device is first installed.
 **/
def installed() {
	log.debug "${device.displayName}: Installing."
	state.installedAt = now()
	state.energy = 0
	state.costPerKWH = 0
	state.costOfEnergy = 0
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

	log.debug "${device.displayName}: Updated()"
	// Update internal state:
	state.debug = ("true" == configDebugMode)
	state.costPerKWH = configCostPerKWH as BigDecimal
    
    // Update secondaryInfo:
    if (configParameter6 == "false") { state.secondaryInfo = "Switch is Disabled (Meter Only)" }
	else if (configParameter9 == "Ignore") { state.secondaryInfo = "RF Commands Disabled!" }
	else if (configParameter9 == "Toggle State") { state.secondaryInfo = "RF Commands Toggle Switch!" }
	else if (configParameter9 == "Switch On") { state.secondaryInfo = "RF Commands Reversed!" }
    else { state.secondaryInfo = "\n" }
	sendEvent(name: "secondaryInfo", value: state.secondaryInfo, displayed: false)
    
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
    
	// Auto-Reporting:
	if ("true" == configAutoReport) {
		// Add this hub's ID to Group 1 so that Power and Energy auto reports are sent to the hub:
		cmds << zwave.associationV1.associationSet(groupingIdentifier:1, nodeId:zwaveHubNodeId).format()
		if (state.debug) log.debug "$device.displayName: Enabling Auto-Reporting"
	}
	else {
		// Remove Hub's ID from Group 1 (auto-reports will not be received by the hub):
        cmds << zwave.associationV1.associationRemove(groupingIdentifier:1, nodeId:zwaveHubNodeId).format()
		if (state.debug) log.debug "$device.displayName: Disabling Auto-Reporting"
	}
    //cmds << zwave.associationV1.associationGet(groupingIdentifier:1).format()
    
    // Parameter 1 - Power Report Interval (x5sec):
	Long CP1 = configParameter1 as Long  
    if ((CP1 == null) || (CP1 < 1) || (CP1 > 32767)) { CP1 = 12 }
    cmds << zwave.configurationV1.configurationSet(parameterNumber: 1, size: 2, scaledConfigurationValue: CP1).format()
    cmds << zwave.configurationV1.configurationGet(parameterNumber: 1).format()
    
    // Parameter 2 - Energy Report Interval (x10min):
    Long CP2 = configParameter2 as Long
    if ((CP2 == null) || (CP2 < 1) || (CP2 > 32767)) { CP2 = 1 }
    cmds << zwave.configurationV1.configurationSet(parameterNumber: 2, size: 2, scaledConfigurationValue: CP2).format()
    cmds << zwave.configurationV1.configurationGet(parameterNumber: 2).format()
    
    // Parameter 3 - Current Threshold for Load Caution (x0.01A):
    Long CP3 = configParameter3 as Long
    if ((CP3 == null) || (CP3 < 10) || (CP3 > 1300)) { CP3 = 1300 }
    cmds << zwave.configurationV1.configurationSet(parameterNumber: 3, size: 2, scaledConfigurationValue: CP3).format()
    cmds << zwave.configurationV1.configurationGet(parameterNumber: 3).format()
    
    // Parameter 4 - Energy Threshold for Load Caution (kWh):
    Long CP4 = configParameter4 as Long
    if ((CP4 == null) || (CP4 < 1) || (CP4 > 10000)) { CP4 = 10000 }
    cmds << zwave.configurationV1.configurationSet(parameterNumber: 4, size: 2, scaledConfigurationValue: CP4).format()
    cmds << zwave.configurationV1.configurationGet(parameterNumber: 4).format()
    
    // Parameter 5 - Restore Switch State Mode:
	//  What state will the switch be set to when power is restored?
    Long CP5 = 1                                  // Last State (Default)
    if (configParameter5 == "Off") {CP5 = 0}      // On
	else if (configParameter5 == "On") {CP5 = 2}  // Off
	
    cmds << zwave.configurationV1.configurationSet(parameterNumber: 5, size: 1, scaledConfigurationValue: CP5).format()
    cmds << zwave.configurationV1.configurationGet(parameterNumber: 5).format()
    
    // Set Parameter 6 - Enable Switch?:
	// When the switch is disabled, the physical button will not work and z-wave switch on/off commands are also ignored.
    cmds << zwave.configurationV1.configurationSet(parameterNumber: 6, size: 1, scaledConfigurationValue: ("true" == configParameter6) ? 1 : 0).format()
    cmds << zwave.configurationV1.configurationGet(parameterNumber: 6).format()

	// Set Parameter 7 - LED Indication Mode:
	Long CP7
    if (configParameter7 == "Night Mode") {CP7 = 2} else {CP7 = 1}
    cmds << zwave.configurationV1.configurationSet(parameterNumber: 7, size: 1, scaledConfigurationValue: CP7).format()
    cmds << zwave.configurationV1.configurationGet(parameterNumber: 7).format()

	// Parameter 8 - Auto-Off Timer (s):
	Long CP8 = configParameter8 as Long  
    if ((CP8 == null) || (CP8 < 0) || (CP8 > 32767)) { CP8 = 0 }
    cmds << zwave.configurationV1.configurationSet(parameterNumber: 8, size: 2, scaledConfigurationValue: CP8).format()
    cmds << zwave.configurationV1.configurationGet(parameterNumber: 8).format()
    
    // Parameter 9 - RF Off Command Mode:
    Long CP9 
    if (configParameter9 == "Switch Off") {CP9 = 0}
	else if (configParameter9 == "Ignore") {CP9 = 1}
	else if (configParameter9 == "Toggle State") {CP9 = 2}
	else if (configParameter9 == "Switch On") {CP9 = 3}
	else {CP9 = 0}
	cmds << zwave.configurationV1.configurationSet(parameterNumber: 9, size: 1, scaledConfigurationValue: CP9).format()
    cmds << zwave.configurationV1.configurationGet(parameterNumber: 9).format()
    
    // Return:
    if ( cmds != [] && cmds != null ) return delayBetween(cmds, 500) else return
}

/**
 *  test() - Temp testing method.
 **/
def test() {
	if (state.debug) log.debug "$device.displayName: Testing"

}
