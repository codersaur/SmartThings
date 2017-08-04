/*****************************************************************************************************************
 *  Copyright: David Lomas (codersaur)
 *
 *  Name: Fibaro Dimmer 2
 *
 *  Author: David Lomas (codersaur)
 *
 *  Date: 2017-02-27
 *
 *  Version: 2.02
 *
 *  Source: https://github.com/codersaur/SmartThings/tree/master/devices/fibaro-dimmer-2
 *
 *  Author: David Lomas (codersaur)
 *
 *  Description: An advanced SmartThings device handler for the Fibaro Dimmer 2 (FGD-212) Z-Wave Dimmer.
 *
 *  For full information, including installation instructions, exmples, and version history, see:
 *   https://github.com/codersaur/SmartThings/tree/master/devices/fibaro-dimmer-2
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
 *****************************************************************************************************************/
metadata {
    definition (name: "Fibaro Dimmer 2", namespace: "codersaur", author: "David Lomas") {
        capability "Actuator"
        capability "Switch"
        capability "Switch Level"
        capability "Light"
        capability "Sensor"
        capability "Power Meter"
        capability "Energy Meter"
        capability "Polling"
        capability "Refresh"

        // Custom (Virtual) Capabilities:
        //capability "Fault"
        //capability "Logging"
        //capability "Scene Controller"

        // Standard (Capability) Attributes:
        attribute "switch", "string"
        attribute "level", "number"
        attribute "power", "number"
        attribute "energy", "number"

        // Custom Attributes:
        attribute "fault", "string"             // Indicates if the device has any faults. 'clear' if no active faults.
        attribute "logMessage", "string"        // Important log messages.
        attribute "energyLastReset", "string"   // Last time that Accumulated Engergy was reset.
        attribute "syncPending", "number"       // Number of config items that need to be synced with the physical device.
        attribute "nightmode", "string"         // 'Enabled' or 'Disabled'.
        attribute "scene", "number"             // ID of last-activated scene.

        // Display Attributes:
        // These are only required because the UI lacks number formatting and strips leading zeros.
        attribute "dispPower", "string"
        attribute "dispEnergy", "string"

        // Custom Commands:
        command "reset"
        command "resetEnergy"
        command "enableNightmode"
        command "disableNightmode"
        command "toggleNightmode"
        command "clearFault"
        command "sync"
        command "test"

        // Fingerprints (new format):
        fingerprint mfr: "010F", prod: "0102", model: "1000"
        fingerprint type: "1101", mfr: "010F", cc: "5E,86,72,59,73,22,31,32,71,56,98,7A"
        fingerprint type: "1101", mfr: "010F", cc: "5E,86,72,59,73,22,31,32,71,56,98,7A", sec: "20,5A,85,26,8E,60,70,75,27", secOut: "2B"
    }

    tiles(scale: 2) {

        // Multi Tile:
        multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#79b821", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
            }
            tileAttribute ("device.level", key: "SLIDER_CONTROL", range:"(0..100)") {
                attributeState "level", action:"setLevel"
            }
        }

        // Instantaneous Power:
        valueTile("instMode", "device.dispPower", decoration: "flat", width: 2, height: 1) {
            state "default", label:'Now:', action:"refresh", icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_refresh.png"
        }
        valueTile("power", "device.dispPower", decoration: "flat", width: 2, height: 1) {
            state "default", label:'${currentValue}', icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_top_bottom_2.png"
        }

        // Accumulated Energy:
        valueTile("energyLastReset", "device.energyLastReset", decoration: "flat", width: 2, height: 1) {
            state "default", label:'Since:  ${currentValue}', action:"resetEnergy", icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_stopwatch_reset.png"
        }
        valueTile("energy", "device.dispEnergy", width: 2, height: 1) {
            state "default", label:'${currentValue}', icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_top_bottom_2.png"
        }

        // Other Tiles:
        standardTile("nightmode", "device.nightmode", decoration: "flat", width: 2, height: 2) {
            state "default", label:'${currentValue}', action:"toggleNightmode", icon:"st.Weather.weather4"
        }
        valueTile("scene", "device.scene", decoration: "flat", width: 2, height: 2) {
            state "default", label:'Scene: ${currentValue}'
        }
        standardTile("refresh", "device.power", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:'', action:"refresh", icon:"st.secondary.refresh"
        }
        standardTile("syncPending", "device.syncPending", decoration: "flat", width: 2, height: 2) {
            state "default", label:'Sync Pending', action:"sync", backgroundColor:"#FF6600"
            state "0", label:'Synced', action:"", backgroundColor:"#79b821"
        }
        standardTile("fault", "device.fault", decoration: "flat", width: 2, height: 2) {
            state "default", label:'${currentValue} Fault', action:"clearFault", backgroundColor:"#FF6600", icon:"st.secondary.tools"
            state "clear", label:'${currentValue}', action:"", backgroundColor:"#79b821", icon:""
        }
        standardTile("test", "device.power", decoration: "flat", width: 2, height: 2) {
            state "default", label:'Test', action:"test"
        }

        // Tile Layouts:
        main(["switch"])
        details([
            "switch",
            "instMode","power",
            "nightmode",
            "energyLastReset","energy",
            "scene",
            //"refresh",
            //"test",
            "syncPending",
            "fault"
        ])
    }

    preferences {

        section { // GENERAL:
            input (
                type: "paragraph",
                element: "paragraph",
                title: "GENERAL:",
                description: "General device handler settings."
            )

            input (
                name: "configLoggingLevelIDE",
                title: "IDE Live Logging Level: Messages with this level and higher will be logged to the IDE.",
                type: "enum",
                options: [
                    "0" : "None",
                    "1" : "Error",
                    "2" : "Warning",
                    "3" : "Info",
                    "4" : "Debug",
                    "5" : "Trace"
                ],
//                defaultValue: "3", // iPhone users can uncomment these lines!
                required: true
            )

            input (
                name: "configLoggingLevelDevice",
                title: "Device Logging Level: Messages with this level and higher will be logged to the logMessage attribute.",
                type: "enum",
                options: [
                    "0" : "None",
                    "1" : "Error",
                    "2" : "Warning"
                ],
//                defaultValue: "2", // iPhone users can uncomment these lines!
                required: true
            )

            input (
                name: "configSyncAll",
                title: "Force Full Sync: All device parameters, association groups, and protection settings will " +
                "be re-sent to the device. This will take several minutes and you may need to press the 'sync' " +
                "tile a few times.",
                type: "boolean",
//                defaultValue: false, // iPhone users can uncomment these lines!
                required: true
            )

            input (
                name: "configProactiveReports",
                title: "Proactively Request Reports: Additonal requests for status reports will be made. " +
                "Use only if status reporting is unreliable.",
                type: "boolean",
//                defaultValue: false, // iPhone users can uncomment these lines!
                required: true
            )
        }

        section { // PROTECTION:
            input type: "paragraph",
                element: "paragraph",
                title: "PROTECTION:",
                description: "Prevent unintentional control (e.g. by a child) by disabling the physical switches and/or RF control."

            input (
                name: "configProtectLocal",
                title: "Local Protection: Applies to physical switches:",
                type: "enum",
                options: [
                    "0" : "Unprotected",
                    //"1" : "Protection by sequence", // Not supported by Fibaro Dimmer 2.
                    "2" : "No operation possible"
                ],
//                defaultValue: "0", // iPhone users can uncomment these lines!
                required: true
            )

            input (
                name: "configProtectRF",
                title: "RF Protection: Applies to Z-Wave commands sent from hub or other devices:",
                type: "enum",
                options: [
                    "0" : "Unprotected",
                    "1" : "No RF control"//,
                    //"2" : "No RF response" // Not supported by Fibaro Dimmer 2.
                ],
//                defaultValue: "0", // iPhone users can uncomment these lines!
                required: true
            )

        }

        section { // NIGHTMODE:
            input type: "paragraph",
                element: "paragraph",
                title: "NIGHTMODE:",
                description: "Nightmode forces the dimmer to switch on at a specific level (e.g. low-level during the night).\n" +
                    "Nightmode can be enabled/disabled manually using the new Nightmode tile, or scheduled below."

            input type: "number",
                name: "configNightmodeLevel",
                title: "Nightmode Level: The dimmer will always switch on at this level when nightmode is enabled.",
                range: "1..100",
//                defaultValue: "10", // iPhone users can uncomment these lines!
                required: true

            input type: "boolean",
                name: "configNightmodeForce",
                title: "Force Nightmode: If the dimmer is on when nightmode is enabled, the Nightmode Level is applied immediately " +
                    "(otherwise it's only applied next time the dimmer is switched on).",
//                defaultValue: true, // iPhone users can uncomment these lines!
                required: true

            input type: "time",
                name: "configNightmodeStartTime",
                title: "Nightmode Start Time: Nightmode will be enabled every day at this time.",
                required: false

            input type: "time",
                name: "configNightmodeStopTime",
                title: "Nightmode Stop Time: Nightmode will be disabled every day at this time.",
                required: false
        }

        generatePrefsParams()

        generatePrefsAssocGroups()

    }
}

/**
 *  parse()
 *
 *  Called when messages from the device are received by the hub. The parse method is responsible for interpreting
 *  those messages and returning event definitions (and command responses).
 *
 *  As this is a Z-wave device, zwave.parse() is used to convert the message into a command. The command is then
 *  passed to zwaveEvent(), which is overloaded for each type of command below.
 *
 *  Parameters:
 *   String      description        The raw message from the device.
 **/
def parse(description) {
    logger("parse(): Parsing raw message: ${description}","trace")

    def result = null

    if (description.startsWith("Err")) {
        logger("parse(): Unknown Error. Raw message: ${description}","error")
    }
    else if (description != "updated") {
        // The purpose of the replace statement here is to fix a bug, see:
        // https://community.smartthings.com/t/wireless-wall-switch-zme-wallc-s-to-control-smartthings-devices-and-routines/24810/28
        def cmd = zwave.parse(description.replace("98C1", "9881"), getCommandClassVersions())
        if (cmd) {
            result = zwaveEvent(cmd)
        } else {
            logger("parse(): Could not parse raw message: ${description}","error")
        }
    }

    return result
}

/*****************************************************************************************************************
 *  Z-wave Event Handlers.
 *****************************************************************************************************************/

/**
 *  zwaveEvent( COMMAND_CLASS_BASIC V1 (0x20) : BASIC_REPORT )
 *
 *  The Basic Report command is used to advertise the status of the primary functionality of the device.
 *
 *  Action: Pass command to dimmerEvent().
 *
 *  cmd attributes:
 *    Short    value
 *      0x00       = Off
 *      0x01..0x63 = 0..100%
 *      0xFE       = Unknown
 *      0xFF       = On
 **/
def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
    logger("zwaveEvent(): Basic Report received: ${cmd}","trace")
    return dimmerEvent(cmd)
}

/**
 *  zwaveEvent( COMMAND_CLASS_BASIC V1 (0x20) : BASIC_SET )
 *
 *  The Basic Set command is used to set a value in a supporting device.
 *  If this command is received by the hub, the hub must be a member of one or more association groups.
 *
 *  Action: No action required as state change will be triggered via BASIC_REPORT handler.
 *
 *  cmd attributes:
 *    Short    value
 *      0x00       = Off
 *      0x01..0x63 = 0..100%
 *      0xFE       = Unknown
 *      0xFF       = On
 *
 *  Example: BasicSet(value: 0)
 **/
def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
    logger("zwaveEvent(): Basic Set received: ${cmd}","trace")
}

/**
 *  zwaveEvent( COMMAND_CLASS_SWITCH_MULTILEVEL V3 (0x26) : SWITCH_MULTILEVEL_REPORT )
 *
 *  The Switch Multilevel Report is used to advertise the status of a multilevel device.
 *
 *  Action: Pass command to dimmerEvent().
 *
 *  cmd attributes:
 *    Short    value
 *      0x00       = Off
 *      0x01..0x63 = 0..100%
 *      0xFE       = Unknown
 *      0xFF       = On [Deprecated]
 *
 *  Example: SwitchMultilevelReport(value: 1)
 **/
def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv3.SwitchMultilevelReport cmd) {
    logger("zwaveEvent(): Switch Multilevel Report received: ${cmd}","trace")
    return dimmerEvent(cmd)
}

/**
 *  zwaveEvent( COMMAND_CLASS_SWITCH_MULTILEVEL V3 (0x26) : SWITCH_MULTILEVEL_SET )
 *
 *  The Switch Multilevel Set command is used to set a value in a supporting device.
 *  If this command is received by the hub, the hub must be a member of one or more association groups.
 *
 *  Action: No action required as state change will be triggered via SWITCH_MULTILEVEL_REPORT handler.
 *
 *  cmd attributes:
 *    Short    value
 *      0x00       = Off
 *      0x01..0x63 = 0..100%
 *      0xFE       = Unknown
 *      0xFF       = On [Deprecated]
 *    Short    dimmingDuration
 *
 *  Example: SwitchMultilevelSet(dimmingDuration: 1, value: 0)
 **/
def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv3.SwitchMultilevelSet cmd) {
    logger("zwaveEvent(): Switch Multilevel Set received: ${cmd}","trace")
}

/**
 *  zwaveEvent( COMMAND_CLASS_SWITCH_MULTILEVEL V3 (0x26) : SWITCH_MULTILEVEL_START_LEVEL_CHANGE )
 *
 *  The Multilevel Switch Start Level Change command is used to initiate a transition to a new level.
 *  If this command is received by the hub, the hub must be a member of one or more association groups.
 *
 *  Action: No action required as state change will be triggered via a SWITCH_MULTILEVEL_REPORT on completion
 *  of the transition.
 *
 *  cmd attributes:
 *    Short    dimmingDuration
 *    Boolean  ignoreStartLevel
 *    Short    incDec
 *    Short    startLevel
 *    Short    stepSize
 *    Short    upDown
 *
 *  Example: SwitchMultilevelStartLevelChange(dimmingDuration: 3, ignoreStartLevel: false, incDec: 0,
 *            reserved00: 0, startLevel: 4, stepSize: 1, upDown: 0)
 **/
def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv3.SwitchMultilevelStartLevelChange cmd) {
    logger("zwaveEvent():  Switch Multilevel Start Level Change received: ${cmd}","trace")
}

/**
 *  zwaveEvent( COMMAND_CLASS_SWITCH_MULTILEVEL V3 (0x26) : SWITCH_MULTILEVEL_STOP_LEVEL_CHANGE )
 *
 *  The Multilevel Switch Stop Level Change command is used to stop an ongoing transition.
 *  If this command is received by the hub, the hub must be a member of one or more association groups.
 *
 *  Action: No action required as state change will be triggered via a SWITCH_MULTILEVEL_REPORT on completion
 *  of the transition.
 *
 *  cmd attributes: None
 *
 *  Example: SwitchMultilevelStopLevelChange()
 **/
def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv3.SwitchMultilevelStopLevelChange cmd) {
    logger("zwaveEvent():  Switch Multilevel Stop Level Change received: ${cmd}","trace")
}

/**
 *  dimmerEvent()
 *
 *  Common handler for BasicReport, SwitchBinaryReport, SwitchMultilevelReport.
 *
 *  Action: Raise 'switch' and 'level' events.
 *   Restore pending level if dimmer has been switched on after nightmode has been disabled.
 *   If Proactive Reporting is enabled, and the level has changed, request a meter report.
 **/
def dimmerEvent(physicalgraph.zwave.Command cmd) {

    def result = []

    // switch event:
    def switchValue = (cmd.value ? "on" : "off")
    def switchEvent = createEvent(name: "switch", value: switchValue)
    if (switchEvent.isStateChange) logger("Dimmer turned ${switchValue}.","info")
    result << switchEvent

    // level event:
    def levelValue = Math.round (cmd.value * 100 / 99)
    def levelEvent = createEvent(name: "level", value: levelValue, unit: "%")
    if (levelEvent.isStateChange) logger("Dimmer level is ${levelValue}%","info")
    result << levelEvent

    // Store last active level, which is needed for nightmode functionality:
    if (levelValue > 0) state.lastActiveLevel = levelValue

    // Restore pending level if dimmer has been switched on after nightmode has been disabled:
    if (!state.nightmodeActive & (state.nightmodePendingLevel > 0) & switchEvent.isStateChange & switchValue == "on") {
        logger("dimmerEvent(): Applying Pending Level: ${state.nightmodePendingLevel}","debug")
        result << response(secure(zwave.basicV1.basicSet(value: Math.round(state.nightmodePendingLevel.toInteger() * 99 / 100 ))))
        state.nightmodePendingLevel = 0
    }
    // Else if Proactive Reporting is enabled, and the level has changed, request a meter report:
    else if (state.proactiveReports & levelEvent.isStateChange) {
        result << response(["delay 5000", secure(zwave.meterV3.meterGet(scale: 2)),"delay 10000", secure(zwave.meterV3.meterGet(scale: 2))])
        // Meter request is delayed for 5s, although sometimes this isn't long enough, so make a second request after another 10 seconds.
    }

    return result
}

/**
 *  zwaveEvent( COMMAND_CLASS_SWITCH_ALL V1 (0x27) : SWITCH_ALL_REPORT )
 *
 *  The All Switch Report Command is used to report if the device is included or excluded from the all on/all off
 *  functionality.
 *
 *  Note: The Fibaro Dimmer 2 supports control of this functionality via Parameter #11, in addition to
 *  SWITCH_ALL_SET commands.
 *
 *  Action: Log an info message.
 *
 *  cmd attributes:
 *    Short    mode
 *      0   = MODE_EXCLUDED_FROM_THE_ALL_ON_ALL_OFF_FUNCTIONALITY
 *      1   = MODE_EXCLUDED_FROM_THE_ALL_ON_FUNCTIONALITY_BUT_NOT_ALL_OFF
 *      2   = MODE_EXCLUDED_FROM_THE_ALL_OFF_FUNCTIONALITY_BUT_NOT_ALL_ON
 *      255 = MODE_INCLUDED_IN_THE_ALL_ON_ALL_OFF_FUNCTIONALITY
 **/
def zwaveEvent(physicalgraph.zwave.commands.switchallv1.SwitchAllReport cmd) {
    logger("zwaveEvent(): Switch All Report received: ${cmd}","trace")

    def msg = ""
    switch (cmd.mode) {
            case 0:
                msg = "Device is excluded from the all on/all off functionality."
                break

            case 1:
                msg = "Device is excluded from the all on functionality but not all off."
                break

            case 2:
                msg = "Device is excluded from the all off functionality but not all on."
                break

            default:
                msg = "Device is included in the all on/all off functionality."
                break
    }

    logger("Switch All Mode: ${msg}","info")

    return msg
}

/**
 *  zwaveEvent( COMMAND_CLASS_SCENE_ACTIVATION (0x2B) : SCENE_ACTIVATION_SET )
 *
 *  The Scene Activation Set Command is used to activate the setting associated to the scene ID.
 *
 *  Action: Raise scene event and log an info message.
 *
 *  cmd attributes:
 *    Short    dimmingDuration
 *      0x00       = Instantly
 *      0x01..0x7F = 1 second (0x01) to 127 seconds (0x7F) in 1-second resolution.
 *      0x80..0xFE = 1 minute (0x80) to 127 minutes (0xFE) in 1-minute resolution.
 *      0xFF       = Dimming duration configured by the Scene Actuator Configuration Set and Scene
 *                   Controller Configuration Set Command depending on device used.
 *    Short    sceneId
 *      0x00..0xFF = Scene0..Scene255
 **/
def zwaveEvent(physicalgraph.zwave.commands.sceneactivationv1.SceneActivationSet cmd) {
    logger("zwaveEvent(): Scene Activation Set received: ${cmd}","trace")

    def result = []
    result << createEvent(name: "scene", value: "$cmd.sceneId", data: [switchType: "$settings.param20"], descriptionText: "Scene id ${cmd.sceneId} was activated", isStateChange: true)

    logger("Scene #${cmd.sceneId} was activated.","info")

    return result
}

/**
 *  zwaveEvent( COMMAND_CLASS_SENSOR_MULTILEVEL V4 (0x31) : SENSOR_MULTILEVEL_REPORT )
 *
 *  The Multilevel Sensor Report Command is used by a multilevel sensor to advertise a sensor reading.
 *
 *  Action: Raise appropriate type of event (and disp event) and log an info message.
 *
 *  Note: SmartThings does not yet have capabilities corresponding to all possible sensor types, therefore
 *  some of the event types raised below are non-standard.
 *
 *  Note: Fibaro Dimmer 2 appears to report power (sensorType 4) only.
 *
 *  cmd attributes:
 *    Short         precision           Indicates the number of decimals.
 *                                      E.g. The decimal value 1025 with precision 2 is therefore equal to 10.25.
 *    Short         scale               Indicates what unit the sensor uses.
 *    BigDecimal    scaledSensorValue   Sensor value as a double.
 *    Short         sensorType          Sensor Type (8 bits).
 *    List<Short>   sensorValue         Sensor value as an array of bytes.
 *    Short         size                Indicates the number of bytes used for the sensor value.
 **/
def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv4.SensorMultilevelReport cmd) {
    logger("zwaveEvent(): SensorMultilevelReport received: ${cmd}","trace")

    def result = []
    def map = [ displayed: true, value: cmd.scaledSensorValue.toString() ]
    def dispMap = [ displayed: false ]

    // Sensor Types up to V4 only, there are further sensor types up to V10 defined.
    switch (cmd.sensorType) {
        case 1:  // Air Temperature (V1)
            map.name = "temperature"
            map.unit = (cmd.scale == 1) ? "F" : "C"
            break

        case 2:  // General Purpose (V1)
            map.name = "value"
            map.unit = (cmd.scale == 1) ? "" : "%"
            break

        case 3:  // Luninance (V1)
            map.name = "illuminance"
            map.unit = (cmd.scale == 1) ? "lux" : "%"
            break

        case 4:  // Power (V2)
            map.name = "power"
            map.unit = (cmd.scale == 1) ? "Btu/h" : "W"
            dispMap.name = "dispPower"
            dispMap.value = String.format("%.1f",cmd.scaledSensorValue as BigDecimal) + " ${map.unit}"
            break

        case 5:  // Humidity (V2)
            map.name = "humidity"
            map.unit = (cmd.scale == 1) ? "g/m^3" : "%"
            break

        case 6:  // Velocity (V2)
            map.name = "velocity"
            map.unit = (cmd.scale == 1) ? "mph" : "m/s"
            break

        case 7:  // Direction (V2)
            map.name = "direction"
            map.unit = ""
            break

        case 8:  // Atmospheric Pressure (V2)
        case 9:  // Barometric Pressure (V2)
            map.name = "pressure"
            map.unit = (cmd.scale == 1) ? "inHg" : "kPa"
            break

        case 0xA:  // Solar Radiation (V2)
            map.name = "radiation"
            map.unit = "W/m^3"
            break

        case 0xB:  // Dew Point (V2)
            map.name = "dewPoint"
            map.unit = (cmd.scale == 1) ? "F" : "C"
            break

        case 0xC:  // Rain Rate (V2)
            map.name = "rainRate"
            map.unit = (cmd.scale == 1) ? "in/h" : "mm/h"
            break

        case 0xD:  // Tide Level (V2)
            map.name = "tideLevel"
            map.unit = (cmd.scale == 1) ? "ft" : "m"
            break

        case 0xE:  // Weight (V3)
            map.name = "weight"
            map.unit = (cmd.scale == 1) ? "lbs" : "kg"
            break

        case 0xF:  // Voltage (V3)
            map.name = "voltage"
            map.unit = (cmd.scale == 1) ? "mV" : "V"
            dispMap.name = "dispVoltage"
            dispMap.value = String.format("%.1f",cmd.scaledSensorValue as BigDecimal) + " ${map.unit}"
            break

        case 0x10:  // Current (V3)
            map.name = "current"
            map.unit = (cmd.scale == 1) ? "mA" : "A"
            dispMap.name = "dispCurrent"
            dispMap.value = String.format("%.1f",cmd.scaledSensorValue as BigDecimal) + " ${map.unit}"
            break

        case 0x11:  // Carbon Dioxide Level (V3)
            map.name = "carbonDioxide"
            map.unit = "ppm"
            break

        case 0x12:  // Air Flow (V3)
            map.name = "fluidFlow"
            map.unit = (cmd.scale == 1) ? "cfm" : "m^3/h"
            break

        case 0x13:  // Tank Capacity (V3)
            map.name = "fluidVolume"
            map.unit = (cmd.scale == 0) ? "ltr" : (cmd.scale == 1) ? "m^3" : "gal"
            break

        case 0x14:  // Distance (V3)
            map.name = "distance"
            map.unit = (cmd.scale == 0) ? "m" : (cmd.scale == 1) ? "cm" : "ft"
            break

        default:
            logger("zwaveEvent(): SensorMultilevelReport with unhandled sensorType: ${cmd}","warn")
            map.name = "unknown"
            map.unit = "unknown"
            break
    }

    logger("New sensor reading: Name: ${map.name}, Value: ${map.value}, Unit: ${map.unit}","info")

    result << createEvent(map)
    if (dispMap.name) { result << createEvent(dispMap) }

    return result
}

/**
 *  zwaveEvent( COMMAND_CLASS_METER V3 (0x32) : METER_REPORT )
 *
 *  The Meter Report Command is used to advertise a meter reading.
 *
 *  Action: Raise appropriate type of event (and disp... event) and log an info message.
 *
 *  Note: Fibaro Dimmer 2 supports energy and power only. It will not report current, voltage, or power factor.
 *
 *  cmd attributes:
 *    Integer        deltaTime                   Time in seconds since last report.
 *    Short          meterType                   Specifies the type of metering device.
 *      0x00 = Unknown
 *      0x01 = Electric meter
 *      0x02 = Gas meter
 *      0x03 = Water meter
 *    List<Short>    meterValue                  Meter value as an array of bytes.
 *    Double         scaledMeterValue            Meter value as a double.
 *    List<Short>    previousMeterValue          Previous meter value as an array of bytes.
 *    Double         scaledPreviousMeterValue    Previous meter value as a double.
 *    Short          size                        The size of the array for the meterValue and previousMeterValue.
 *    Short          scale                       Indicates what unit the sensor uses (dependent on meterType).
 *    Short          precision                   The decimal precision of the values.
 *    Short          rateType                    Specifies if it is import or export values to be read.
 *      0x01 = Import (consumed)
 *      0x02 = Export (produced)
 *    Boolean        scale2                      ???
 **/
def zwaveEvent(physicalgraph.zwave.commands.meterv3.MeterReport cmd) {
    logger("zwaveEvent(): Meter Report received: ${cmd}","trace")

    def result = []

    switch (cmd.meterType) {
        case 1:  // Electric meter:
            switch (cmd.scale) {
                case 0:  // Accumulated Energy (kWh):
                    result << createEvent(name: "energy", value: cmd.scaledMeterValue, unit: "kWh", displayed: true)
                    result << createEvent(name: "dispEnergy", value: String.format("%.2f",cmd.scaledMeterValue as BigDecimal) + " kWh", displayed: false)
                    logger("New meter reading: Accumulated Energy: ${cmd.scaledMeterValue} kWh","info")
                    break

                case 1:  // Accumulated Energy (kVAh):
                    result << createEvent(name: "energy", value: cmd.scaledMeterValue, unit: "kVAh", displayed: true)
                    result << createEvent(name: "dispEnergy", value: String.format("%.2f",cmd.scaledMeterValue as BigDecimal) + " kVAh", displayed: false)
                    logger("New meter reading: Accumulated Energy: ${cmd.scaledMeterValue} kVAh","info")
                    break

                case 2:  // Instantaneous Power (Watts):
                    result << createEvent(name: "power", value: cmd.scaledMeterValue, unit: "W", displayed: true)
                    result << createEvent(name: "dispPower", value: String.format("%.1f",cmd.scaledMeterValue as BigDecimal) + " W", displayed: false)
                    logger("New meter reading: Instantaneous Power: ${cmd.scaledMeterValue} W","info")
                    break

                case 3:  // Accumulated Pulse Count:
                    result << createEvent(name: "pulseCount", value: cmd.scaledMeterValue, unit: "", displayed: true)
                    logger("New meter reading: Accumulated Electricity Pulse Count: ${cmd.scaledMeterValue}","info")
                    break

                case 4:  // Instantaneous Voltage (Volts):
                    result << createEvent(name: "voltage", value: cmd.scaledMeterValue, unit: "V", displayed: true)
                    result << createEvent(name: "dispVoltage", value: String.format("%.1f",cmd.scaledMeterValue as BigDecimal) + " V", displayed: false)
                    logger("New meter reading: Instantaneous Voltage: ${cmd.scaledMeterValue} V","info")
                    break

                 case 5:  // Instantaneous Current (Amps):
                    result << createEvent(name: "current", value: cmd.scaledMeterValue, unit: "A", displayed: true)
                    result << createEvent(name: "dispCurrent", value: String.format("%.1f",cmd.scaledMeterValue as BigDecimal) + " V", displayed: false)
                    logger("New meter reading: Instantaneous Current: ${cmd.scaledMeterValue} A","info")
                    break

                 case 6:  // Instantaneous Power Factor:
                    result << createEvent(name: "powerFactor", value: cmd.scaledMeterValue, unit: "", displayed: true)
                    result << createEvent(name: "dispPowerFactor", value: String.format("%.1f",cmd.scaledMeterValue as BigDecimal), displayed: false)
                    logger("New meter reading: Instantaneous Power Factor: ${cmd.scaledMeterValue}","info")
                    break

                default:
                    logger("zwaveEvent(): Meter Report with unhandled scale: ${cmd}","warn")
                    break
            }
            break

        case 2:  // Gas meter:

            switch (cmd.scale) {
                case 0:  // Accumulated Gas Volume (m^3):
                    result << createEvent(name: "fluidVolume", value: cmd.scaledMeterValue, unit: "m^3", displayed: true)
                    result << createEvent(name: "dispFluidVolume", value: String.format("%.2f",cmd.scaledMeterValue as BigDecimal) + " m^3", displayed: false)
                    logger("New meter reading: Accumulated Gas Volume: ${cmd.scaledMeterValue} m^3","info")
                    break

                case 1:  // Accumulated Gas Volume (ft^3):
                    result << createEvent(name: "fluidVolume", value: cmd.scaledMeterValue, unit: "ft^3", displayed: true)
                    result << createEvent(name: "dispFluidVolume", value: String.format("%.2f",cmd.scaledMeterValue as BigDecimal) + " ft^3", displayed: false)
                    logger("New meter reading: Accumulated Gas Volume: ${cmd.scaledMeterValue} ft^3","info")
                    break

                case 3:  // Accumulated Pulse Count:
                    result << createEvent(name: "pulseCount", value: cmd.scaledMeterValue, unit: "", displayed: true)
                    logger("New meter reading: Accumulated Gas Pulse Count: ${cmd.scaledMeterValue}","info")
                    break

                default:
                    logger("zwaveEvent(): Meter Report with unhandled scale: ${cmd}","warn")
                    break
            }
            break

        case 3:  // Water meter:

            switch (cmd.scale) {
                case 0:  // Accumulated Water Volume (m^3):
                    result << createEvent(name: "fluidVolume", value: cmd.scaledMeterValue, unit: "m^3", displayed: true)
                    result << createEvent(name: "dispFluidVolume", value: String.format("%.2f",cmd.scaledMeterValue as BigDecimal) + " m^3", displayed: false)
                    logger("New meter reading: Accumulated Water Volume: ${cmd.scaledMeterValue} m^3","info")
                    break

                case 1:  // Accumulated Water Volume (ft^3):
                    result << createEvent(name: "fluidVolume", value: cmd.scaledMeterValue, unit: "ft^3", displayed: true)
                    result << createEvent(name: "dispFluidVolume", value: String.format("%.2f",cmd.scaledMeterValue as BigDecimal) + " ft^3", displayed: false)
                    logger("New meter reading: Accumulated Water Volume: ${cmd.scaledMeterValue} ft^3","info")
                    break

                case 2:  // Accumulated Water Volume (US gallons):
                    result << createEvent(name: "fluidVolume", value: cmd.scaledMeterValue, unit: "gal", displayed: true)
                    result << createEvent(name: "dispFluidVolume", value: String.format("%.2f",cmd.scaledMeterValue as BigDecimal) + " gal", displayed: false)
                    logger("New meter reading: Accumulated Water Volume: ${cmd.scaledMeterValue} gal","info")
                    break

                case 3:  // Accumulated Pulse Count:
                    result << createEvent(name: "pulseCount", value: cmd.scaledMeterValue, unit: "", displayed: true)
                    logger("New meter reading: Accumulated Water Pulse Count: ${cmd.scaledMeterValue}","info")
                    break

                default:
                    logger("zwaveEvent(): Meter Report with unhandled scale: ${cmd}","warn")
                    break
            }
            break

        default:
            logger("zwaveEvent(): Meter Report with unhandled meterType: ${cmd}","warn")
            break
    }

    return result
}

/**
 *  zwaveEvent( COMMAND_CLASS_CRC16_ENCAP V1 (0x56) : CRC_16_ENCAP )
 *
 *  The CRC-16 Encapsulation Command Class is used to encapsulate a command with an additional CRC-16 checksum
 *  to ensure integrity of the payload. The purpose for this command class is to ensure a higher integrity level
 *  of payloads carrying important data.
 *
 *  Action: Extract the encapsulated command and pass to zwaveEvent().
 *
 *  Note: Validation of the checksum is not necessary as this is performed by the hub.
 *
 *  cmd attributes:
 *    Integer      checksum      Checksum.
 *    Short        command       Command identifier of the embedded command.
 *    Short        commandClass  Command Class identifier of the embedded command.
 *    List<Short>  data          Embedded command data.
 *
 *  Example: Crc16Encap(checksum: 125, command: 2, commandClass: 50, data: [33, 68, 0, 0, 0, 194, 0, 0, 77])
 **/
def zwaveEvent(physicalgraph.zwave.commands.crc16encapv1.Crc16Encap cmd) {
    logger("zwaveEvent(): CRC-16 Encapsulation Command received: ${cmd}","trace")

    def versions = getCommandClassVersions()
    def version = versions[cmd.commandClass as Integer]
    def ccObj = version ? zwave.commandClass(cmd.commandClass, version) : zwave.commandClass(cmd.commandClass)
    def encapsulatedCommand = ccObj?.command(cmd.command)?.parse(cmd.data)
    // TO DO: It should be possible to replace the lines above with this line soon...
    //def encapsulatedCommand = cmd.encapsulatedCommand(getCommandClassVersions())
    if (!encapsulatedCommand) {
        logger("zwaveEvent(): Could not extract command from ${cmd}","error")
    } else {
        return zwaveEvent(encapsulatedCommand)
    }
}

/**
 *  zwaveEvent( COMMAND_CLASS_DEVICE_RESET_LOCALLY V1 (0x5A) : DEVICE_RESET_LOCALLY_NOTIFICATION )
 *
 *  The Device Reset Locally Notification Command is used to advertise that the device will be reset.
 *
 *  Action: Log a warn message.
 **/
def zwaveEvent(physicalgraph.zwave.commands.deviceresetlocallyv1.DeviceResetLocallyNotification cmd) {
    logger("zwaveEvent(): Device Reset Locally Notification: ${cmd}","trace")
    logger("zwaveEvent(): Device was reset!","warn")
}

/**
 *  zwaveEvent( COMMAND_CLASS_MULTICHANNEL V4 (0x60) : MULTI_CHANNEL_CMD_ENCAP )
 *
 *  The Multi Channel Command Encapsulation command is used to encapsulate commands. Any command supported by
 *  a Multi Channel End Point may be encapsulated using this command.
 *
 *  Action: Extract the encapsulated command and pass to the appropriate zwaveEvent() handler.
 *
 *  Note: We only receive these commands from a Dimmer 2 if the hub has been added to one or more association
 *  groups 2-5, which is not normally needed. The sourceEndPoint attribute will indicate if from S1 or S2, but we
 *  don't care here, because button presses are handled via SCENE_ACTIVATION_SET commands instead.
 *
 *  cmd attributes:
 *    Boolean      bitAddress           Set to true if multicast addressing is used.
 *    Short        command              Command identifier of the embedded command.
 *    Short        commandClass         Command Class identifier of the embedded command.
 *    Short        destinationEndPoint  Destination End Point.
 *    List<Short>  parameter            Carries the parameter(s) of the embedded command.
 *    Short        sourceEndPoint       Source End Point.
 *
 *  Example: MultiChannelCmdEncap(bitAddress: false, command: 1, commandClass: 32, destinationEndPoint: 0,
 *            parameter: [0], sourceEndPoint: 1)
 **/
def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd) {
    logger("zwaveEvent(): Multi Channel Command Encapsulation command received: ${cmd}","trace")

    def encapsulatedCommand = cmd.encapsulatedCommand(getCommandClassVersions())
    if (!encapsulatedCommand) {
        logger("zwaveEvent(): Could not extract command from ${cmd}","error")
    } else {
        return zwaveEvent(encapsulatedCommand)
    }
}

/**
 *  zwaveEvent( COMMAND_CLASS_CONFIGURATION V1 (0x70) : CONFIGURATION_REPORT )
 *
 *  The Configuration Report Command is used to advertise the actual value of the advertised parameter.
 *
 *  Action: Store the value in the parameter cache, update syncPending, and log an info message.
 *
 *  Note: Ideally, we want to update the corresponding preference value shown on the Settings GUI, however this
 *  is not possible due to security restrictions in the SmartThings platform.
 *
 *  cmd attributes:
 *    List<Short>  configurationValue  Value of parameter (byte array).
 *    Short        parameterNumber     Parameter ID.
 *    Short        size                Size of parameter's value (bytes).
 *
 *  Example: ConfigurationReport(configurationValue: [0], parameterNumber: 14, reserved11: 0,
 *            scaledConfigurationValue: 0, size: 1)
 **/
def zwaveEvent(physicalgraph.zwave.commands.configurationv1.ConfigurationReport cmd) {
    logger("zwaveEvent(): Configuration Report received: ${cmd}","trace")

    state."paramCache${cmd.parameterNumber}" = cmd.scaledConfigurationValue.toInteger()
    def paramName = getParamsMd().find( { it.id == cmd.parameterNumber }).name
    logger("Parameter #${cmd.parameterNumber} [${paramName}] has value: ${cmd.scaledConfigurationValue}","info")
    updateSyncPending()
}

/**
 *  zwaveEvent( COMMAND_CLASS_NOTIFICATION V3 (0x71) : NOTIFICATION_REPORT )
 *
 *  The Notification Report Command is used to advertise notification information.
 *
 *  Action: Raise appropriate type of event (e.g. fault, tamper, water) and log an info or warn message.
 *
 *  Note: SmartThings does not yet have official capabilities definited for many types of notification. E.g. this
 *  handler raises 'fault' events, which is not part of any standard capability.
 *
 *  cmd attributes:
 *    Short        event                  Event Type (see code below).
 *    List<Short>  eventParameter         Event Parameter(s) (depends on Event type).
 *    Short        eventParametersLength  Length of eventParameter.
 *    Short        notificationStatus     The notification reporting status of the device (depends on push or pull model).
 *    Short        notificationType       Notification Type (see code below).
 *    Boolean      sequence
 *    Short        v1AlarmLevel           Legacy Alarm Level from Alarm CC V1.
 *    Short        v1AlarmType            Legacy Alarm Type from Alarm CC V1.
 *    Short        zensorNetSourceNodeId  Source node ID
 *
 *  Example: NotificationReport(event: 8, eventParameter: [], eventParametersLength: 0, notificationStatus: 255,
 *    notificationType: 8, reserved61: 0, sequence: false, v1AlarmLevel: 0, v1AlarmType: 0, zensorNetSourceNodeId: 0)
 **/
def zwaveEvent(physicalgraph.zwave.commands.notificationv3.NotificationReport cmd) {
    logger("zwaveEvent(): Notification Report received: ${cmd}","trace")

    def result = []

    switch (cmd.notificationType) {
        //case 1:  // Smoke Alarm: // Not Implemented yet. Should raise smoke/carbonMonoxide/consumableStatus events etc...
        //case 2:  // CO Alarm:
        //case 3:  // CO2 Alarm:

        case 4:  // Heat Alarm:
            switch (cmd.event) {
                case 0:  // Previous Events cleared:
                    // Do not send a fault clear event automatically.
                    logger("Heat Alarm Cleared","info")
                    break

                case 1:  // Overheat detected:
                case 2:  // Overheat detected, Unknown Location:
                    result << createEvent(name: "fault", value: "overheat", descriptionText: "Overheat detected!", displayed: true)
                    logger("Overheat detected!","warn")
                    break

                case 3:  // Rapid Temperature Rise:
                case 4:  // Rapid Temperature Rise, Unknown Location:
                    result << createEvent(name: "fault", value: "temperature", descriptionText: "Rapid temperature rise detected!", displayed: true)
                    logger("Rapid temperature rise detected!","warn")
                    break

                case 5:  // Underheat detected:
                case 6:  // Underheat detected, Unknown Location:
                    result << createEvent(name: "fault", value: "underheat", descriptionText: "Underheat detected!", displayed: true)
                    logger("Underheat detected!","warn")
                    break

                default:
                    logger("zwaveEvent(): Notification Report recieved with unhandled event: ${cmd}","warn")
                    break
            }
            break

        //case 5:  // Water Alarm: // Not Implemented yet. Should raise water/consumableStatus events etc...

        case 8:  // Power Management:
            switch (cmd.event) {
                case 0:  // Previous Events cleared:
                    // Do not send a fault clear event automatically.
                    logger("Previous Events cleared","info")
                    break

                //case 1:  // Mains Connected:
                //case 2:  // AC Mains Disconnected:
                //case 3:  // AC Mains Re-connected:

                case 4:  // Surge:
                    result << createEvent(name: "fault", value: "surge", descriptionText: "Power surge detected!", displayed: true)
                    logger("Power surge detected!","warn")
                    break

                case 5:  // Voltage Drop:
                    result << createEvent(name: "fault", value: "voltage", descriptionText: "Voltage drop detected!", displayed: true)
                    logger("Voltage drop detected!","warn")
                    break

                case 6:  // Over-current:
                    result << createEvent(name: "fault", value: "current", descriptionText: "Over-current detected!", displayed: true)
                    logger("Over-current detected!","warn")
                    break

                 case 7:  // Over-Voltage:
                    result << createEvent(name: "fault", value: "voltage", descriptionText: "Over-voltage detected!", displayed: true)
                    logger("Over-voltage detected!","warn")
                    break

                 case 8:  // Overload:
                    result << createEvent(name: "fault", value: "load", descriptionText: "Overload detected!", displayed: true)
                    logger("Overload detected!","warn")
                    break

                 case 9:  // Load Error:
                    result << createEvent(name: "fault", value: "load", descriptionText: "Load Error detected!", displayed: true)
                    logger("Load Error detected!","warn")
                    break

                default:
                    logger("zwaveEvent(): Notification Report recieved with unhandled event: ${cmd}","warn")
                    break
            }
            break

        case 9:  // system:
            switch (cmd.event) {
                case 0:  // Previous Events cleared:
                    // Do not send a fault clear event automatically.
                    logger("Previous Events cleared","info")
                    break

                case 1:  // Harware Failure:
                case 3:  // Harware Failure (with manufacturer proprietary failure code):
                    result << createEvent(name: "fault", value: "hardware", descriptionText: "Hardware failure detected!", displayed: true)
                    logger("Hardware failure detected!","warn")
                    break

                case 2:  // Software Failure:
                case 4:  // Software Failure (with manufacturer proprietary failure code):
                    result << createEvent(name: "fault", value: "firmware", descriptionText: "Firmware failure detected!", displayed: true)
                    logger("Firmware failure detected!","warn")
                    break

                case 6:  // Tampering:
                    result << createEvent(name: "tamper", value: "detected", descriptionText: "Tampering: Product covering removed!", displayed: true)
                    logger("Tampering: Product covering removed!","warn")
                    break

                default:
                    logger("zwaveEvent(): Notification Report recieved with unhandled event: ${cmd}","warn")
                    break
            }
            break

        default:
            logger("zwaveEvent(): Notification Report recieved with unhandled notificationType: ${cmd}","warn")
            break
    }

    return result
}

/**
 *  zwaveEvent( COMMAND_CLASS_MANUFACTURER_SPECIFIC V2 (0x72) : MANUFACTURER_SPECIFIC_REPORT )
 *
 *  Manufacturer-Specific Reports are used to advertise manufacturer-specific information, such as product number
 *  and serial number.
 *
 *  Action: Publish values as device 'data'. Log a warn message if manufacturerId and/or productId do not
 *  correspond to Fibaro Dimmer 2.
 *
 *  Example: ManufacturerSpecificReport(manufacturerId: 271, manufacturerName: Fibargroup, productId: 4096,
 *   productTypeId: 258)
 **/
def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
    logger("zwaveEvent(): Manufacturer-Specific Report received: ${cmd}","trace")

    // Display as hex strings:
    def manufacturerIdDisp = String.format("%04X",cmd.manufacturerId)
    def productIdDisp = String.format("%04X",cmd.productId)
    def productTypeIdDisp = String.format("%04X",cmd.productTypeId)

    logger("Manufacturer-Specific Report: Manufacturer ID: ${manufacturerIdDisp}, Manufacturer Name: ${cmd.manufacturerName}" +
    ", Product Type ID: ${productTypeIdDisp}, Product ID: ${productIdDisp}","info")

    if ( 271 != cmd.manufacturerId) logger("Device Manufacturer is not Fibaro. Using this device handler with a different device may damage your device!","warn")
    if ( 4096 != cmd.productId) logger("Product ID does not match Fibaro Dimmer 2. Using this device handler with a different device may damage you device!","warn")

    updateDataValue("manufacturerName",cmd.manufacturerName)
    updateDataValue("manufacturerId",manufacturerIdDisp)
    updateDataValue("productId",productIdDisp)
    updateDataValue("productTypeId",productTypeIdDisp)
}

/**
 *  zwaveEvent( COMMAND_CLASS_POWERLEVEL V1 (0x73) : POWERLEVEL_REPORT )
 *
 *  The Powerlevel Report is used to advertise the current RF transmit power of the device.
 *
 *  Action: Log an info message.
 *
 *  cmd attributes:
 *    Short  powerLevel  The current power level indicator value in effect on the node
 *    Short  timeout     The time in seconds the node has at Power level before resetting to normal Power level.
 *
 *  Example: PowerlevelReport(powerLevel: 0, timeout: 0)
 **/
def zwaveEvent(physicalgraph.zwave.commands.powerlevelv1.PowerlevelReport cmd) {
    logger("zwaveEvent(): Powerlevel Report received: ${cmd}","trace")
    def power = (cmd.powerLevel > 0) ? "minus${cmd.powerLevel}dBm" : "NormalPower"
    logger("Powerlevel Report: Power: ${power}, Timeout: ${cmd.timeout}","info")
}

/**
 *  zwaveEvent( COMMAND_CLASS_PROTECTION V2 (0x75) : PROTECTION_REPORT )
 *
 *  The Protection Report is used to report the protection state of a device.
 *  I.e. measures to prevent unintentional control (e.g. by a child).
 *
 *  Action: Cache values, update syncPending, and log an info message.
 *
 *  cmd attributes:
 *    Short  localProtectionState  Local protection state (i.e. physical switches/buttons)
 *    Short  rfProtectionState     RF protection state.
 *
 *  Example: ProtectionReport(localProtectionState: 0, reserved01: 0, reserved11: 0, rfProtectionState: 0)
 **/
def zwaveEvent(physicalgraph.zwave.commands.protectionv2.ProtectionReport cmd) {
    logger("zwaveEvent(): Protection Report received: ${cmd}","trace")

    state.protectLocalCache = cmd.localProtectionState
    state.protectRFCache = cmd.rfProtectionState

    def lp, rfp = ""

    switch(cmd.localProtectionState)  {
        case 0:
            lp = "Unprotected"
            break
        case 1:
            lp = "Protection by sequence"
            break
        case 2:
            lp = "No operation possible"
            break
        default:
            lp = "Unknwon"
            break

    }

    switch(cmd.rfProtectionState)  {
        case 0:
            rfp = "Unprotected"
            break
        case 1:
            rfp = "No RF Control"
            break
        case 2:
            rfp = "No RF Response"
            break
        default:
            rfp = "Unknwon"
            break
    }

    logger("Protection Report: Local Protection: ${lp}, RF Protection: ${rfp}","info")
    updateSyncPending()
}

/**
 *  zwaveEvent( COMMAND_CLASS_FIRMWARE_UPDATE_MD V2 (0x7A) : FirmwareMdReport )
 *
 *  The Firmware Meta Data Report Command is used to advertise the status of the current firmware in the device.
 *
 *  Action: Publish values as device 'data' and log an info message. No check is performed.
 *
 *  cmd attributes:
 *    Integer  checksum        Checksum of the firmware image.
 *    Integer  firmwareId      Firware ID (this is not the firmware version).
 *    Integer  manufacturerId  Manufacturer ID.
 *
 *  Example: FirmwareMdReport(checksum: 50874, firmwareId: 274, manufacturerId: 271)
 **/
def zwaveEvent(physicalgraph.zwave.commands.firmwareupdatemdv2.FirmwareMdReport cmd) {
    logger("zwaveEvent(): Firmware Metadata Report received: ${cmd}","trace")

    // Display as hex strings:
    def firmwareIdDisp = String.format("%04X",cmd.firmwareId)
    def checksumDisp = String.format("%04X",cmd.checksum)

    logger("Firmware Metadata Report: Firmware ID: ${firmwareIdDisp}, Checksum: ${checksumDisp}","info")

    updateDataValue("firmwareId","${firmwareIdDisp}")
    updateDataValue("firmwareChecksum","${checksumDisp}")
}

/**
 *  zwaveEvent( COMMAND_CLASS_ASSOCIATION V2 (0x85) : ASSOCIATION_REPORT )
 *
 *  The Association Report command is used to advertise the current destination nodes of a given association group.
 *
 *  Action: Log info message only. Do not cache values as the Fibaro Dimmer 2 uses COMMAND_CLASS_MULTI_CHANNEL_ASSOCIATION.
 *
 *  Note: Ideally, we want to update the corresponding preference value shown on the Settings GUI, however this
 *  is not possible due to security restrictions in the SmartThings platform.
 *
 *  Example: AssociationReport(groupingIdentifier: 4, maxNodesSupported: 5, nodeId: [1], reportsToFollow: 0)
 **/
def zwaveEvent(physicalgraph.zwave.commands.associationv2.AssociationReport cmd) {
    logger("zwaveEvent(): Association Report received: ${cmd}","trace")

    //state."assocGroupCache${cmd.groupingIdentifier}" = cmd.nodeId

    // Display to user in hex format (same as IDE):
    def hexArray  = []
    cmd.nodeId.sort().each { hexArray.add(String.format("%02X", it)) };
    logger("Association Group ${cmd.groupingIdentifier} contains nodes: ${hexArray} (hexadecimal format)","info")

    //updateSyncPending()
}

/**
 *  zwaveEvent( COMMAND_CLASS_VERSION V1 (0x86) : VERSION_REPORT )
 *
 *  The Version Report Command is used to advertise the library type, protocol version, and application version.

 *  Action: Publish values as device 'data' and log an info message. No check is performed.
 *
 *  Note: Device actually supports V2, but SmartThings only supports V1.
 *
 *  cmd attributes:
 *    Short  applicationSubVersion
 *    Short  applicationVersion
 *    Short  zWaveLibraryType
 *    Short  zWaveProtocolSubVersion
 *    Short  zWaveProtocolVersion
 *
 *  Example: VersionReport(applicationSubVersion: 4, applicationVersion: 3, zWaveLibraryType: 3,
 *   zWaveProtocolSubVersion: 5, zWaveProtocolVersion: 4)
 **/
def zwaveEvent(physicalgraph.zwave.commands.versionv1.VersionReport cmd) {
    logger("zwaveEvent(): Version Report received: ${cmd}","trace")

    def zWaveLibraryTypeDisp  = String.format("%02X",cmd.zWaveLibraryType)
    def zWaveLibraryTypeDesc  = ""
    switch(cmd.zWaveLibraryType) {
        case 1:
            zWaveLibraryTypeDesc = "Static Controller"
            break

        case 2:
            zWaveLibraryTypeDesc = "Controller"
            break

        case 3:
            zWaveLibraryTypeDesc = "Enhanced Slave"
            break

        case 4:
            zWaveLibraryTypeDesc = "Slave"
            break

        case 5:
            zWaveLibraryTypeDesc = "Installer"
            break

        case 6:
            zWaveLibraryTypeDesc = "Routing Slave"
            break

        case 7:
            zWaveLibraryTypeDesc = "Bridge Controller"
            break

        case 8:
            zWaveLibraryTypeDesc = "Device Under Test (DUT)"
            break

        case 0x0A:
            zWaveLibraryTypeDesc = "AV Remote"
            break

        case 0x0B:
            zWaveLibraryTypeDesc = "AV Device"
            break

        default:
            zWaveLibraryTypeDesc = "N/A"
    }

    def applicationVersionDisp = String.format("%d.%02d",cmd.applicationVersion,cmd.applicationSubVersion)
    def zWaveProtocolVersionDisp = String.format("%d.%02d",cmd.zWaveProtocolVersion,cmd.zWaveProtocolSubVersion)

    logger("Version Report: Application Version: ${applicationVersionDisp}, " +
           "Z-Wave Protocol Version: ${zWaveProtocolVersionDisp}, " +
           "Z-Wave Library Type: ${zWaveLibraryTypeDisp} (${zWaveLibraryTypeDesc})","info")

    updateDataValue("applicationVersion","${cmd.applicationVersion}")
    updateDataValue("applicationSubVersion","${cmd.applicationSubVersion}")
    updateDataValue("zWaveLibraryType","${zWaveLibraryTypeDisp}")
    updateDataValue("zWaveProtocolVersion","${cmd.zWaveProtocolVersion}")
    updateDataValue("zWaveProtocolSubVersion","${cmd.zWaveProtocolSubVersion}")
}

/**
 *  zwaveEvent( COMMAND_CLASS_MULTI_CHANNEL_ASSOCIATION V2 (0x8E) : ASSOCIATION_REPORT )
 *
 *  The Multi-channel Association Report command is used to advertise the current destinations of a given
 *  association group (nodes and endpoints).
 *
 *  Action: Store the destinations in the assocGroup cache, update syncPending, and log an info message.
 *
 *  Note: Ideally, we want to update the corresponding preference value shown on the Settings GUI, however this
 *  is not possible due to security restrictions in the SmartThings platform.
 *
 *  Example: MultiChannelAssociationReport(groupingIdentifier: 2, maxNodesSupported: 8, nodeId: [9,0,1,1,2,3],
 *            reportsToFollow: 0)
 **/
def zwaveEvent(physicalgraph.zwave.commands.multichannelassociationv2.MultiChannelAssociationReport cmd) {
    logger("zwaveEvent(): Multi-Channel Association Report received: ${cmd}","trace")

    state."assocGroupCache${cmd.groupingIdentifier}" = cmd.nodeId // Must not sort as order is important.

    def assocGroupName = getAssocGroupsMd().find( { it.id == cmd.groupingIdentifier} ).name
    // Display to user in hex format (same as IDE):
    def hexArray  = []
    cmd.nodeId.each { hexArray.add(String.format("%02X", it)) };
    logger("Association Group #${cmd.groupingIdentifier} [${assocGroupName}] contains destinations: ${hexArray}","info")

    updateSyncPending()
}

/**
 *  zwaveEvent( COMMAND_CLASS_SECURITY V1 (0x98) : SECURITY_COMMANDS_SUPPORTED_REPORT )
 *
 *  The Security Commands Supported Report command advertises which command classes are supported using security
 *  encapsulation.
 *
 *  Action: Store the list of supported command classes in state.secureCommandClasses. Log info message.
 *
 *  Example:  SecurityCommandsSupportedReport(commandClassControl: [43],
 *   commandClassSupport: [32, 90, 133, 38, 142, 96, 112, 117, 39], reportsToFollow: 0)
 **/
def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityCommandsSupportedReport cmd) {
    logger("zwaveEvent(): Security Commands Supported Report received: ${cmd}","trace")

    state.secureCommandClasses = cmd.commandClassSupport

    // Display to user in hex format (same as IDE):
    def hexArray  = []
    cmd.commandClassSupport.sort().each { hexArray.add(String.format("0x%02X", it)) };
    logger("Security Commands Supported: ${hexArray}","info")
}

/**
 *  zwaveEvent( COMMAND_CLASS_SECURITY V1 (0x98) : SECURITY_MESSAGE_ENCAPSULATION )
 *
 *  The Security Message Encapsulation command is used to encapsulate Z-Wave commands using AES-128.
 *
 *  Action: Extract the encapsulated command and pass to the appropriate zwaveEvent() handler.
 *
 *  cmd attributes:
 *    List<Short> commandByte         Parameters of the encapsulated command.
 *    Short   commandClassIdentifier  Command Class ID of the encapsulated command.
 *    Short   commandIdentifier       Command ID of the encapsulated command.
 *    Boolean secondFrame             Indicates if first or second frame.
 *    Short   sequenceCounter
 *    Boolean sequenced               True if the command is transmitted using multiple frames.
 **/
def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {
    logger("zwaveEvent(): Security Encapsulated Command received: ${cmd}","trace")

    def encapsulatedCommand = cmd.encapsulatedCommand(getCommandClassVersions())
    if (encapsulatedCommand) {
        return zwaveEvent(encapsulatedCommand)
    } else {
        logger("zwaveEvent(): Unable to extract security encapsulated command from: ${cmd}","error")
    }
}

/**
 *  zwaveEvent( DEFAULT CATCHALL )
 *
 *  Called for all commands that aren't handled above.
 **/
def zwaveEvent(physicalgraph.zwave.Command cmd) {
    logger("zwaveEvent(): No handler for command: ${cmd}","error")
}


/*****************************************************************************************************************
 *  Capability-related Commands:
 *****************************************************************************************************************/

/**
 *  on()                        [Capability: Switch]
 *
 *  Turn the dimmer on.
 **/
def on() {
    logger("on(): Turning dimmer on.","info")
    def cmds = []
    cmds << zwave.basicV1.basicSet(value: 0xFF)
    if (state.proactiveReports) cmds << zwave.switchMultilevelV1.switchMultilevelGet()
    sendSecureSequence(cmds,5000)
}

/**
 *  off()                       [Capability: Switch]
 *
 *  Turn the dimmer off.
 **/
def off() {
    logger("off(): Turning dimmer off.","info")
    def cmds = []
    cmds << zwave.basicV1.basicSet(value: 0x00)
    if (state.proactiveReports) cmds << zwave.switchMultilevelV1.switchMultilevelGet()
    sendSecureSequence(cmds,5000)
}

/**
 *  setLevel()                  [Capability: Switch Level]
 *
 *  Set the dimmer level.
 *
 *  Parameters:
 *   level    Target level (0-100%).
 **/
def setLevel(level) {
    logger("setLevel(${level})","trace")

    if (level < 0) level = 0
    if (level > 100) level = 100
    logger("Setting dimmer to ${level}%","info")

    // Clear nightmodePendingLevel as it's been overridden.
    state.nightmodePendingLevel = 0

    def cmds = []
    cmds << zwave.basicV1.basicSet(value: Math.round(level * 99 / 100 )) // Convert from 0-100 to 0-99.
    if (state.proactiveReports) cmds << zwave.switchMultilevelV1.switchMultilevelGet()
    sendSecureSequence(cmds,5000)
}

/**
 *  refresh()                   [Capability: Refresh]
 *
 *  Request switchMultilevel, energy, and power reports.
 *  Also, force a configuration sync.
 **/
def refresh() {
    logger("refresh()","trace")

    def cmds = []
    cmds << zwave.switchMultilevelV1.switchMultilevelGet()
    cmds << zwave.meterV3.meterGet(scale: 0)
    cmds << zwave.meterV3.meterGet(scale: 2)

    sendSecureSequence(cmds,200)
    sync()
}

/**
 *  poll()                      [Capability: Polling]
 *
 *  Calls refresh().
 **/
def poll() {
    logger("poll()","trace")
    refresh()
}

/*****************************************************************************************************************
 *  Custom Commands:
 *****************************************************************************************************************/

/**
 *  reset()
 *
 *  Calls resetEnergy().
 *
 *  Note: this used to be part of the official 'Energy Meter' capability, but isn't anymore.
 **/
def reset() {
    logger("reset()","trace")
    resetEnergy()
}

/**
 *  resetEnergy()
 *
 *  Reset the Accumulated Energy figure held in the device.
 *
 *  Does not return a list of commands, it sends them immediately using sendSecureSequence(). This is required if
 *  triggered by schedule().
 **/
def resetEnergy() {
    logger("resetEnergy(): Resetting Accumulated Energy","info")

    state.energyLastReset = new Date().format("YYYY/MM/dd \n HH:mm:ss", location.timeZone)
    sendEvent(name: "energyLastReset", value: state.energyLastReset, descriptionText: "Accumulated Energy Reset")

    sendSecureSequence([
        zwave.meterV3.meterReset(),
        zwave.meterV3.meterGet(scale: 0)
    ],400)
}

/**
 *  enableNightmode(level)
 *
 *  Force switch-on illuminance level.
 *
 *  Does not return a list of commands, it sends them immediately using sendSecureSequence(). This is required if
 *  triggered by schedule().
 **/
def enableNightmode(level=-1) {
    logger("enableNightmode(${level})","info")

    // Clean level value:
    if (level == -1) level = settings.configNightmodeLevel.toInteger()
    if (level > 100) level = 100
    if (level < 1) level = 1

    // If nightmode is not already active, save last active level and current value of param19, so they can be restored when nightmode is stopped:
    if (!state.nightmodeActive) {

        state.nightmodePriorLevel = state.lastActiveLevel
        logger("enableNightmode(): Saved previous active level: ${state.nightmodePriorLevel}","info")

        if (!state.paramCache19) state.paramCache19 = 0
        state.nightmodePriorParam19 = state.paramCache19.toInteger()
        logger("enableNightmode(): Saved previous param19: ${state.paramCache19}","info")
    }

    // If the dimmer is already on, and configNightmodeForce is enabled, then adjust the level immediately:
    if (("on" == device.latestValue("switch")) & ("true" == configNightmodeForce)) sendSecureSequence([zwave.basicV1.basicSet(value: Math.round(level * 99 / 100 ))])

    state.nightmodeActive = true
    sendEvent(name: "nightmode", value: "Enabled", descriptionText: "Nightmode Enabled", isStateChange: true)

    // Update parameter #19 for force next switch-on level:
    state.paramTarget19 = level.toInteger()
    sync()
}

/**
 *  disableNightmode()
 *
 *  Stop nightmode and restore previous values.
 *
 *  Does not return a list of commands, it sends them immediately using sendSecureSequence(). This is required if
 *  triggered by schedule().
 **/
def disableNightmode() {
    logger("disableNightmode()","info")

    // If nightmode is active, restore param19:
    if (state.nightmodeActive) {

        logger("disableNightmode(): Restoring previous value of param19 to: ${state.nightmodePriorParam19}","debug")
        state.paramTarget19 = state.nightmodePriorParam19
        sync()

        if (state.nightmodePriorLevel > 0) {
            if (("on" == device.latestValue("switch")) & ("true" == configNightmodeForce)) {
                // Dimmer is already on and configNightmodeForce is enabled, so adjust the level immediately:
                logger("disableNightmode(): Restoring level to: ${state.nightmodePriorLevel}","debug")
                sendSecureSequence([zwave.basicV1.basicSet(value: Math.round(state.nightmodePriorLevel.toInteger() * 99 / 100 ))])
            } else if (0 == state.nightmodePriorParam19) {
                // Dimmer is off (or configNightmodeForce is not enabled), so need to set a flag to restore the level after it's switched on again, but only if param19 is zero.
                logger("disableNightmode(): Setting flag to restore level at next switch-on: ${state.nightmodePriorLevel}","debug")
                state.nightmodePendingLevel = state.nightmodePriorLevel
            }
        }
    }

    state.nightmodeActive = false
    sendEvent(name: "nightmode", value: "Disabled", descriptionText: "Nightmode Disabled", isStateChange: true)
}

/**
 *  toggleNightmode()
 **/
def toggleNightmode() {
    logger("toggleNightmode()","trace")

    if (state.nightmodeActive) {
        disableNightmode()
    }
    else {
        enableNightmode(configNightmodeLevel)
    }
}

/**
 *  clearFault()
 *
 *  Clear all active faults.
 **/
def clearFault() {
    logger("clearFault(): Clearing active faults.","info")
    sendEvent(name: "fault", value: "clear", descriptionText: "Fault cleared", displayed: true)
}

/*****************************************************************************************************************
 *  SmartThings System Commands:
 *****************************************************************************************************************/

/**
 *  installed()
 *
 *  Runs when the device is first installed.
 *
 *  Action: Set initial values for internal state, and request a full configuration report from the device.
 **/
def installed() {
    log.trace "installed()"

    state.installedAt = now()
    state.energyLastReset = new Date().format("YYYY/MM/dd \n HH:mm:ss", location.timeZone)
    state.loggingLevelIDE     = 3
    state.loggingLevelDevice  = 2
    state.protectLocalTarget  = 0
    state.protectRFTarget     = 0

    sendEvent(name: "fault", value: "clear", descriptionText: "Fault cleared", displayed: false)

    refreshConfig()
}

/**
 *  updated()
 *
 *  Runs when the user hits "Done" from Settings page.
 *
 *  Action: Process new settings, sync parameters and association group members with the physical device. Request
 *  Firmware Metadata, Manufacturer-Specific, and Version reports.
 *
 *  Note: Weirdly, update() seems to be called twice. So execution is aborted if there was a previous execution
 *  within two seconds. See: https://community.smartthings.com/t/updated-being-called-twice/62912
 **/
def updated() {
    logger("updated()","trace")

    def cmds = []

    if (!state.updatedLastRanAt || now() >= state.updatedLastRanAt + 2000) {
        state.updatedLastRanAt = now()

        // Update internal state:
        state.loggingLevelIDE     = settings.configLoggingLevelIDE.toInteger()
        state.loggingLevelDevice  = settings.configLoggingLevelDevice.toInteger()
        state.syncAll             = ("true" == settings.configSyncAll)
        state.proactiveReports    = ("true" == settings.configProactiveReports)

        // Manage Schedules:
        manageSchedules()

        // Update Parameter target values:
        getParamsMd().findAll( {!it.readonly} ).each { // Exclude readonly parameters.
            state."paramTarget${it.id}" = settings."configParam${it.id}"?.toInteger()
        }

        // Check if auto-calibration is being forced. If so, must ignore target values for P1/2/30:
        if (state.paramTarget13 > 0) {
            state.paramCache13 = null // Remove cached value to force sync of P13:
            logger("Auto-calibration is being forced.","info")
            if (state.paramTarget1 != null) logger("Auto-calibration is being forced, but a value has been " +
            "provided for parameter #1. This will be ignored! Check Live Logging for the auto-calibrated " +
            "value shortly.","warn")
            if (state.paramTarget2 != null) logger("Auto-calibration is being forced, but a value has been " +
            "provided for parameter #2. This will be ignored! Check Live Logging for the auto-calibrated " +
            "value shortly.","warn")
            if (state.paramTarget30 != null) logger("Auto-calibration is being forced, but a value has been " +
            "provided for parameter #30. This will be ignored! Check Live Logging for the auto-calibrated " +
            "value shortly.","warn")
            state.paramTarget1 = null
            state.paramTarget2 = null
            state.paramTarget30 = null
        }

        // Update Assoc Group target values:
        state.assocGroupTarget1 = [ zwaveHubNodeId ] // Assoc Group #1 is Lifeline and will contain controller only.
        getAssocGroupsMd().findAll( { it.id != 1} ).each {
            state."assocGroupTarget${it.id}" = parseAssocGroupInput(settings."configAssocGroup${it.id}", it.maxNodes)
        }

        // Update Protection target values:
        state.protectLocalTarget = settings.configProtectLocal.toInteger()
        state.protectRFTarget    = settings.configProtectRF.toInteger()

        // Sync configuration with phyiscal device:
        sync(state.syncAll)

        // Set target for parameter #13 [Force Auto-calibration] back to 0 [Readout].
        // Sync will now only complete when auto-calibration has completed:
        state.paramTarget13 = 0

        // Request device medadata (this just seems the best place to do it):
        cmds << zwave.firmwareUpdateMdV2.firmwareMdGet()
        cmds << zwave.manufacturerSpecificV2.manufacturerSpecificGet()
        cmds << zwave.powerlevelV1.powerlevelGet()
        cmds << zwave.versionV1.versionGet()

        return response(secureSequence(cmds))
    }
    else {
        logger("updated(): Ran within last 2 seconds so aborting.","debug")
    }
}

/*****************************************************************************************************************
 *  Private Helper Functions:
 *****************************************************************************************************************/

/**
 *  logger()
 *
 *  Wrapper function for all logging:
 *    Logs messages to the IDE (Live Logging), and also keeps a historical log of critical error and warning
 *    messages by sending events for the device's logMessage attribute.
 *    Configured using configLoggingLevelIDE and configLoggingLevelDevice preferences.
 **/
private logger(msg, level = "debug") {

    switch(level) {
        case "error":
            if (state.loggingLevelIDE >= 1) log.error msg
            if (state.loggingLevelDevice >= 1) sendEvent(name: "logMessage", value: "ERROR: ${msg}", displayed: false, isStateChange: true)
            break

        case "warn":
            if (state.loggingLevelIDE >= 2) log.warn msg
            if (state.loggingLevelDevice >= 2) sendEvent(name: "logMessage", value: "WARNING: ${msg}", displayed: false, isStateChange: true)
            break

        case "info":
            if (state.loggingLevelIDE >= 3) log.info msg
            break

        case "debug":
            if (state.loggingLevelIDE >= 4) log.debug msg
            break

        case "trace":
            if (state.loggingLevelIDE >= 5) log.trace msg
            break

        default:
            log.debug msg
            break
    }
}

/**
 *  parseAssocGroupInput(string, maxNodes)
 *
 *  Converts a comma-delimited string of destinations (nodes and endpoints) into an array suitable for passing to
 *  multiChannelAssociationSet(). All numbers are interpreted as hexadecimal. Anything that's not a valid node or
 *  endpoint is discarded (warn). If the list has more than maxNodes, the extras are discarded (warn).
 *
 *  Example input strings:
 *    "9,A1"      = Nodes: 9 & 161 (no multi-channel endpoints)            => Output: [9, 161]
 *    "7,8:1,8:2" = Nodes: 7, Endpoints: Node8:endpoint1 & node8:endpoint2 => Output: [7, 0, 8, 1, 8, 2]
 */
private parseAssocGroupInput(string, maxNodes) {
    logger("parseAssocGroupInput(): Parsing Association Group Nodes: ${string}","trace")

    // First split into nodes and endpoints. Count valid entries as we go.
    if (string) {
        def nodeList = string.split(',')
        def nodes = []
        def endpoints = []
        def count = 0

        nodeList = nodeList.each { node ->
            node = node.trim()
            if ( count >= maxNodes) {
                logger("parseAssocGroupInput(): Number of nodes and endpoints is greater than ${maxNodes}! The following node was discarded: ${node}","warn")
            }
            else if (node.matches("\\p{XDigit}+")) { // There's only hexadecimal digits = nodeId
                def nodeId = Integer.parseInt(node,16)  // Parse as hex
                if ( (nodeId > 0) & (nodeId < 256) ) { // It's a valid nodeId
                    nodes << nodeId
                    count++
                }
                else {
                    logger("parseAssocGroupInput(): Invalid nodeId: ${node}","warn")
                }
            }
            else if (node.matches("\\p{XDigit}+:\\p{XDigit}+")) { // endpoint e.g. "0A:2"
                def endpoint = node.split(":")
                def nodeId = Integer.parseInt(endpoint[0],16) // Parse as hex
                def endpointId = Integer.parseInt(endpoint[1],16) // Parse as hex
                if ( (nodeId > 0) & (nodeId < 256) & (endpointId > 0) & (endpointId < 256) ) { // It's a valid endpoint
                    endpoints.addAll([nodeId,endpointId])
                    count++
                }
                else {
                    logger("parseAssocGroupInput(): Invalid endpoint: ${node}","warn")
                }
            }
            else {
                logger("parseAssocGroupInput(): Invalid nodeId: ${node}","warn")
            }
        }

        return (endpoints) ? nodes + [0] + endpoints : nodes
    }
    else {
        return []
    }
}

/**
 *  sync()
 *
 *  Manages synchronisation of parameters, association groups, and protection state with the physical device.
 *  The syncPending attribute advertises remaining number of sync operations.
 *
 *  Does not return a list of commands, it sends them immediately using sendSecureSequence(). This is required if
 *  triggered by schedule().
 *
 *  Parameters:
 *   forceAll    Force all items to be synced, otherwise only changed items will be synced.
 **/
private sync(forceAll = false) {
    logger("sync(): Syncing configuration with the physical device.","info")

    def cmds = []
    def syncPending = 0

    if (forceAll) { // Clear all cached values.
        getParamsMd().findAll( {!it.readonly} ).each { state."paramCache${it.id}" = null }
        getAssocGroupsMd().each { state."assocGroupCache${it.id}" = null }
        state.protectLocalCache = null
        state.protectRFCache = null
    }

    getParamsMd().findAll( {!it.readonly} ).each { // Exclude readonly parameters.
        if ( (state."paramTarget${it.id}" != null) & (state."paramCache${it.id}" != state."paramTarget${it.id}") ) {
            cmds << zwave.configurationV1.configurationSet(parameterNumber: it.id, size: it.size, scaledConfigurationValue: state."paramTarget${it.id}".toInteger())
            cmds << zwave.configurationV1.configurationGet(parameterNumber: it.id)
            logger("sync(): Syncing parameter #${it.id} [${it.name}]: New Value: " + state."paramTarget${it.id}","info")
            syncPending++
            }
    }

    getAssocGroupsMd().each {
        def cachedNodes = state."assocGroupCache${it.id}"
        def targetNodes = state."assocGroupTarget${it.id}"

        if ( cachedNodes != targetNodes ) {
            // Display to user in hex format (same as IDE):
            def targetNodesHex  = []
            targetNodes.each { targetNodesHex.add(String.format("%02X", it)) }
            logger("sync(): Syncing Association Group #${it.id}: Destinations: ${targetNodesHex}","info")

            cmds << zwave.multiChannelAssociationV2.multiChannelAssociationRemove(groupingIdentifier: it.id, nodeId: []) // Remove All
            cmds << zwave.multiChannelAssociationV2.multiChannelAssociationSet(groupingIdentifier: it.id, nodeId: targetNodes)
            cmds << zwave.multiChannelAssociationV2.multiChannelAssociationGet(groupingIdentifier: it.id)
            syncPending++
        }
    }

    if ( (state.protectLocalTarget != null) & (state.protectRFTarget != null)
      & ( (state.protectLocalCache != state.protectLocalTarget) || (state.protectRFCache != state.protectRFTarget) ) ) {

        logger("sync(): Syncing Protection State: Local Protection: ${state.protectLocalTarget}, RF Protection: ${state.protectRFTarget}","info")
        cmds << zwave.protectionV2.protectionSet(localProtectionState : state.protectLocalTarget, rfProtectionState: state.protectRFTarget)
        cmds << zwave.protectionV2.protectionGet()
        syncPending++
    }

    sendEvent(name: "syncPending", value: syncPending, displayed: false)
    sendSecureSequence(cmds,1000) // Need a delay of at least 1000ms.
}

/**
 *  updateSyncPending()
 *
 *  Updates syncPending attribute, which advertises remaining number of sync operations.
 **/
private updateSyncPending() {

    def syncPending = 0

    getParamsMd().findAll( {!it.readonly} ).each { // Exclude readonly parameters.
        if ( (state."paramTarget${it.id}" != null) & (state."paramCache${it.id}" != state."paramTarget${it.id}") ) {
            syncPending++
        }
    }

    getAssocGroupsMd().each {
        def cachedNodes = state."assocGroupCache${it.id}"
        def targetNodes = state."assocGroupTarget${it.id}"

        if ( cachedNodes != targetNodes ) {
            syncPending++
        }
    }

    if ( (state.protectLocalCache == null) || (state.protectRFCache == null) ||
         (state.protectLocalCache != state.protectLocalTarget) || (state.protectRFCache != state.protectRFTarget) ) {
        syncPending++
    }

    logger("updateSyncPending(): syncPending: ${syncPending}", "debug")
    if ((syncPending == 0) & (device.latestValue("syncPending") > 0)) logger("Sync Complete.", "info")
    sendEvent(name: "syncPending", value: syncPending, displayed: false)
}

/**
 *  refreshConfig()
 *
 *  Request configuration reports from the physical device: [ Configuration, Association, Protection,
 *   SecuritySupportedCommands, Powerlevel, Manufacturer-Specific, Firmware Metadata, Version, etc. ]
 *
 *  Really only needed at installation or when debugging, as sync will request the necessary reports when the
 *  configuration is changed.
 */
private refreshConfig() {
    logger("refreshConfig()","trace")

    def cmds = []

    getParamsMd().each { cmds << zwave.configurationV1.configurationGet(parameterNumber: it.id) }
    getAssocGroupsMd().each { cmds << zwave.multiChannelAssociationV2.multiChannelAssociationGet(groupingIdentifier: it.id) }

    cmds << zwave.protectionV2.protectionGet()
    cmds << zwave.securityV1.securityCommandsSupportedGet()
    cmds << zwave.manufacturerSpecificV2.manufacturerSpecificGet()
    cmds << zwave.firmwareUpdateMdV2.firmwareMdGet()
    cmds << zwave.versionV1.versionGet()
    cmds << zwave.powerlevelV1.powerlevelGet()

    sendSecureSequence(cmds, 1000) // Delay must be at least 1000 to reliabilty get all results processed.
}

/**
 *  secure(cmd)
 *
 *  Secures and formats a command using securityMessageEncapsulation.
 *
 *  Note: All commands are secured, there is little benefit to not securing commands that are not in
 *  state.secureCommandClasses.
 **/
private secure(physicalgraph.zwave.Command cmd) {
    //if ( state.secureCommandClasses.contains(cmd.commandClassId.toInteger()) ) {...
    return zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
}

/**
 *  secureSequence()
 *
 *  Secure an array of commands. Returns a list of formatted commands.
 **/
private secureSequence(commands, delay = 200) {
    return delayBetween(commands.collect{ secure(it) }, delay)
}

/**
 *  sendSecureSequence()
 *
 *  Secure an array of commands and send them using sendHubCommand.
 **/
private sendSecureSequence(commands, delay = 200) {
    sendHubCommand(commands.collect{ response(secure(it)) }, delay)
}

/**
 *  generatePrefsParams()
 *
 *  Generates preferences (settings) for device parameters.
 **/
private generatePrefsParams() {
        section {
            input (
                type: "paragraph",
                element: "paragraph",
                title: "DEVICE PARAMETERS:",
                description: "Device parameters are used to customise the physical device. " +
                             "Refer to the product documentation for a full description of each parameter."
            )

    getParamsMd().findAll( {!it.readonly} ).each { // Exclude readonly parameters.

        def lb = (it.description.length() > 0) ? "\n" : ""

        switch(it.type) {
            case "number":
            input (
                name: "configParam${it.id}",
                title: "#${it.id}: ${it.name}: \n" + it.description + lb +"Default Value: ${it.defaultValue}",
                type: it.type,
                range: it.range,
//                defaultValue: it.defaultValue, // iPhone users can uncomment these lines!
                required: it.required
            )
            break

            case "enum":
            input (
                name: "configParam${it.id}",
                title: "#${it.id}: ${it.name}: \n" + it.description + lb + "Default Value: ${it.defaultValue}",
                type: it.type,
                options: it.options,
//                defaultValue: it.defaultValue, // iPhone users can uncomment these lines!
                required: it.required
            )
            break
        }
    }
        } // section
}

/**
 *  generatePrefsAssocGroups()
 *
 *  Generates preferences (settings) for Association Groups.
 **/
private generatePrefsAssocGroups() {
        section {
            input (
                type: "paragraph",
                element: "paragraph",
                title: "ASSOCIATION GROUPS:",
                description: "Association groups enable the dimmer to control other Z-Wave devices directly, " +
                             "without participation of the main controller.\n" +
                             "Enter a comma-delimited list of destinations (node IDs and/or endpoint IDs) for " +
                             "each association group. All IDs must be in hexadecimal format. E.g.:\n" +
                             "Node destinations: '11, 0F'\n" +
                             "Endpoint destinations: '1C:1, 1C:2'"
            )

    getAssocGroupsMd().findAll( { it.id != 1} ).each { // Don't show AssocGroup1 (Lifeline).
            input (
                name: "configAssocGroup${it.id}",
                title: "Association Group #${it.id}: ${it.name}: \n" + it.description + " \n[MAX NODES: ${it.maxNodes}]",
                type: "text",
//                defaultValue: "", // iPhone users can uncomment these lines!
                required: false
            )
        }
    }
}

/**
 *  manageSchedules()
 *
 *  Schedules/unschedules Nightmode.
 **/
private manageSchedules() {
    logger("manageSchedules()","trace")

    if (configNightmodeStartTime) {
        schedule(configNightmodeStartTime, enableNightmode)
        logger("manageSchedules(): Nightmode scheduled to start at ${configNightmodeStartTime}","debug")
    } else {
        try {
            unschedule("enableNightmode")
        }
        catch(e) {
            // Unschedule failed
        }
    }

    if (configNightmodeStopTime) {
        schedule(configNightmodeStopTime, disableNightmode)
        logger("manageSchedules(): Nightmode scheduled to stop at ${configNightmodeStopTime}","debug")
    } else {
        try {
            unschedule("disableNightmode")
        }
        catch(e) {
            // Unschedule failed
        }
    }

}

/**
 *  test()
 *
 *  Temp testing method. Called from 'test' tile.
 **/
private test() {
    logger("test()","trace")

    def cmds = []

    if (cmds) return secureSequence(cmds,200)
}

/*****************************************************************************************************************
 *  Static Matadata Functions:
 *
 *  These functions encapsulate metadata about the device. Mostly obtained from:
 *   Z-wave Alliance Reference for Fibaro Dimmer 2: http://products.z-wavealliance.org/products/1729
 *****************************************************************************************************************/

/**
 *  getCommandClassVersions()
 *
 *  Returns a map of the command class versions supported by the device. Used by parse() and zwaveEvent() to
 *  extract encapsulated commands from MultiChannelCmdEncap, MultiInstanceCmdEncap, SecurityMessageEncapsulation,
 *  and Crc16Encap messages.
 *
 *  Reference: http://products.z-wavealliance.org/products/1729/classes
 **/
private getCommandClassVersions() {
    return [0x20: 1, // Basic V1
            0x22: 1, // Application Status V1
            0x26: 3, // Switch Multilevel V3
            0x27: 1, // Switch All V1
            0x2B: 1, // Scene Activation V1
            0x31: 4, // Sensor Multilevel V4
            0x32: 3, // Meter V3
            0x56: 1, // CRC16 Encapsulation V1
            0x59: 1, // Association Group Information V1 (Not handled, as no need)
            0x5A: 1, // Device Reset Locally V1
            //0x5E: 2, // Z-Wave Plus Info V2 (Not supported by SmartThings)
            0x60: 3, // Multi Channel V4 (Device supports V4, but SmartThings only supports V3)
            0x70: 1, // Configuration V1
            0x71: 3, // Notification V5 ((Device supports V5, but SmartThings only supports V3)
            0x72: 2, // Manufacturer Specific V2
            0x73: 1, // Powerlevel V1
            0x75: 2, // Protection V2
            0x7A: 2, // Firmware Update MD V3 (Device supports V3, but SmartThings only supports V2)
            0x85: 2, // Association V2
            0x86: 1, // Version V2 (Device supports V2, but SmartThings only supports V1)
            0x8E: 2, // Multi Channel Association V3 (Device supports V3, but SmartThings only supports V2)
            0x98: 1  // Security V1
           ]
}

/**
 *  getParamsMd()
 *
 *  Returns device parameters metadata. Used by sync(), updateSyncPending(),  and generatePrefsParams().
 *
 *  Reference: http://products.z-wavealliance.org/products/1729/configs
 **/
private getParamsMd() {
    return [
        [id:  1, size: 1, type: "number", range: "1..98", defaultValue: 1, required: false, readonly: false,
         name: "Minimum Brightness Level",
         description: "Set automatically during the calibration process, but can be changed afterwards.\n" +
         "Values: 1-98 = Brightness level (%)"],
        [id:  2, size: 1, type: "number", range: "2..99", defaultValue: 99, required: false, readonly: false,
         name: "Maximum Brightness Level",
         description: "Set automatically during the calibration process, but can be changed afterwards.\n" +
         "Values: 2-99 = Brightness level (%)"],
        [id:  3, size: 1, type: "number", range: "1..99", defaultValue: 1, required: false, readonly: false,
         name: "Incandescence Level of CFLs",
         description : "The Dimmer 2 will set to this value after first switch on. It is required for warming up " +
         "and switching dimmable compact fluorescent lamps and certain types of light sources.\n" +
         "Values: 1-99 = Brightness level (%)"],
        [id:  4, size: 2, type: "number", range: "0..255", defaultValue: 0, required: false, readonly: false,
         name: "Incandescence Time of CFLs",
         description : "The time required for switching compact fluorescent lamps and certain types of light sources.\n" +
         "Values:\n0 = Function Disabled\n1-255 = 0.1-25.5s in 0.1s steps"],
        [id:  5, size: 1, type: "number", range: "1..99", defaultValue : 1, required: false, readonly: false,
         name: "Dimming Step Size (Auto)",
         description : "The percentage value of a dimming step during automatic control.\n" +
         "Values: 1-99 = Dimming step (%)"],
        [id:  6, size: 2, type: "number", range: "0..255", defaultValue: 1, required: false, readonly: false,
         name: "Dimming Step Time (Auto)",
         description : "The time of a single dimming step during automatic control.\n" +
         "Values: 0-255 = 0-2.55s, in 10ms steps"],
        [id:  7, size: 1, type: "number", range: "1..99", defaultValue: 1, required: false, readonly: false,
         name: "Dimming Step Size (Manual)",
         description : "The percentage value of a dimming step during manual control.\n" +
         "Values: 1-99 = Dimming step (%)"],
        [id:  8, size: 2, type: "number", range: "0..255", defaultValue: 5, required: false, readonly: false,
         name: "Dimming Step Time (Manual)",
         description : "The time of a single dimming step during manual control.\n" +
         "Values: 0-255 = 0-2.55s, in 10ms steps"],
        [id:  9, size: 1, type: "enum", defaultValue: "1", required: false, readonly: false,
         name: "State After Power Failure",
         description : "Dimmer state to restore after a power failure.",
         options: ["0" : "0: Off", "1" : "1: Restore Previous State"] ],
        [id: 10, size: 2, type: "number", range: "0..32767", defaultValue: 0, required: false, readonly: false,
         name: "Timer Functionality (Auto-off)",
         description : "Automatically switch off the device after a specified time.\n" +
         "Values:\n0 = Function Disabled\n1-32767 = time in seconds"],
        [id: 11, size: 2, type: "enum", defaultValue: "255", required: false, readonly: false,
         name: "ALL ON/ALL OFF Function",
         description : "Response to SWITCH_ALL_SET commands.",
         options: ["0" : "0: All ON not active, All OFF not active",
                   "1" : "1: All ON not active, All OFF active",
                   "2" : "2: All ON active, All OFF not active",
                   "255" : "255: All ON active, All OFF active"] ],
        [id: 13, size: 1, type: "enum", defaultValue: "0", required: false, readonly: false,
         name: "Force Auto-calibration",
         description : "During calibration this parameter is set to 1 or 2 and switched to 0 upon completion.",
         options: ["0" : "0: Readout",
                   "1" : "1: Force auto-calibration WITHOUT Fibaro Bypass 2",
                   "2" : "2: Force auto-calibration WITH Fibaro Bypass 2"] ],
        [id: 14, size: 1, type: "readonly", readonly: true,
         name: "Auto-calibration Status",
         description : "Read-Only: Indicates if dimmer is using auto-calibration (1) or manual (0) settings."],
        [id: 15, size: 1, type: "number", range: "0..99", defaultValue: 30, required: false, readonly: false,
         name: "Burnt Out Bulb Detection",
         description : "Power variation, compared to standard power consumption (measured during calibration), " +
         "to be interpreted as load error/burnt out bulb.\n" +
         "Values:\n0 = Function Disabled\n1-99 = Power variation (%)"],
        [id: 16, size: 2, type: "number", range: "0..255", defaultValue: 5, required: false, readonly: false,
         name: "Time Delay for Burnt Out Bulb/Overload Detection",
         description : "Time delay (in seconds) for LOAD ERROR or OVERLOAD detection.\n" +
         "Values:\n0 = Detection Disabled\n1-255 = Time delay (s)"],
        [id: 19, size: 1, type: "number", range: "0..99", defaultValue: 0, required: false, readonly: false,
         name: "Forced Switch-on Brightness Level",
         description : "Switching on the dimmer will always set this brightness level.\n" +
         "Note, the Nightmode feature can be used to change this parameter on a schedule.\n" +
         "Values:\n0 = Function Disabled\n1-99 = Brightness level (%)"],
        [id: 20, size: 1, type: "enum", defaultValue: "0", required: false, readonly: false,
         name: "Switch Type",
         description : "Physical switch type: momentary, toggle, or roller blind (S1 to brighten, S2 to dim).",
         options: ["0" : "0: Momentary Switch",
                   "1" : "1: Toggle Switch",
                   "2" : "2: Roller Blind Switch"] ],
        [id: 21, size: 1, type: "enum", defaultValue: "0", required: false, readonly: false,
         name: "Value Sent to Associated Devices on Single Click",
         description : "0xFF will set associated devices to their last-saved state. Current Level will " +
         "synchronise the state of all devices with this dimmer.",
         options: ["0" : "0: 0xFF",
                   "1" : "1: Current Level"] ],
        [id: 22, size: 1, type: "enum", defaultValue: "0", required: false, readonly: false,
         name: "Assign Toggle Switch Status to Device Status",
         description : "By default, each change of toggle switch position results in an on/off action " +
         "regardless the physical connection of contacts.",
         options: ["0" : "0: Change on Every Switch State Change",
                   "1" : "1: Synchronise with Switch State"] ],
        [id: 23, size: 1, type: "enum", defaultValue: "1", required: false, readonly: false,
         name: "Double-click sets Max Brightness",
         description : "Double-clicking will set brightness level to maximum.",
         options: ["0" : "0: Double-click DISABLED",
                   "1" : "1: Double-click ENABLED"] ],
        [id: 24, size: 1, type: "number", range: "0..31", defaultValue: 0, required: false, readonly: false,
         name: "Command Frames Sent to 2nd and 3rd Association Groups (S1 Associations)",
         description : "Determines which actions will not result in sending frames to association groups.\n" +
         "Values (add together):\n" +
         "0 = All actions sent to association groups\n" +
         "1 = Do not send when switching ON (single click)\n" +
         "2 = Do not send when switching OFF (single click)\n" +
         "4 = Do not send when changing dimming level (holding and releasing)\n" +
         "8 = Do not send on double click\n" +
         "16 = Send 0xFF value on double click"],
        [id: 25, size: 1, type: "number", range: "0..31", defaultValue: 0, required: false, readonly: false,
         name: "Command Frames Sent to 4th and 5th Association Groups (S2 Associations)",
         description : "Determines which actions will not result in sending frames to association groups.\n" +
         "Values (add together):\n" +
         "0 = All actions sent to association groups\n" +
         "1 = Do not send when switching ON (single click)\n" +
         "2 = Do not send when switching OFF (single click)\n" +
         "4 = Do not send when changing dimming level (holding and releasing)\n" +
         "8 = Do not send on double click\n" +
         "16 = Send 0xFF value on double click"],
        [id: 26, size: 1, type: "enum", defaultValue: "0", required: false, readonly: false,
         name: "3-way Switch Function",
         description : "Switch S2 also controls the dimmer when in 3-way switch mode. " +
         "Function is disabled if parameter #20 is set to 2 (roller blind switch).",
         options: ["0" : "0: 3-way switch function for S2 DISABLED",
                   "1" : "1: 3-way switch function for S2 ENABLED"] ],
        [id: 27, size: 1, type: "number", range: "0..15", defaultValue: 15, required: false, readonly: false,
         name: "Association Group Security Mode",
         description : "Defines if commands sent to association groups are secure or non-secure.\n" +
         "Values (add together):\n" +
         "0 = all groups (2-5) sent as non-secure\n" +
         "1 = 2nd group sent as secure\n" +
         "2 = 3rd group sent as secure\n" +
         "4 = 4th group sent as secure\n" +
         "8 = 5th group sent as secure\n" +
         "E.g. 15 = all groups (2-5) sent as secure."],
        [id: 28, size: 1, type: "enum", defaultValue: "0", required: false, readonly: false,
         name: "Scene Activation",
         description : "Defines if SCENE_ACTIVATION_SET commands are sent.",
         options: ["0" : "0: Function DISABLED",
                   "1" : "1: Function ENABLED"] ],
        [id: 29, size: 1, type: "enum", defaultValue: "0", required: false, readonly: false,
         name: "Swap S1 and S2",
         description : "Swap the roles of S1 and S2 without changes to physical wiring.",
         options: ["0" : "0: Standard Mode",
                   "1" : "1: S1 operates as S2, S2 operates as S1"] ],
        [id: 30, size: 1, type: "enum", defaultValue: "2", required: false, readonly: false,
         name: "Load Control Mode",
         description : "Override the dimmer mode (i.e. leading or trailing edge).",
         options: ["0" : "0: Force leading edge mode",
                   "1" : "1: Force trailing edge mode",
                   "2" : "2: Automatic (based on auto-calibration)"] ],
        [id: 31, size: 1, type: "readonly", readonly: true,
         name: "Load Control Mode Recognised During Auto-calibration",
         description : "Read-Only: Indicates the load control mode recognised during auto-calibration. Leading Edge (0) / trailing Edge (1)."],
        [id: 32, size: 1, type: "enum", defaultValue: "2", required: false, readonly: false,
         name: "On/Off Mode",
         description : "This mode is necessary when connecting non-dimmable light sources.",
         options: ["0" : "0: On/Off mode DISABLED (dimming is possible)",
                   "1" : "1: On/Off mode ENABLED (dimming not possible)",
                   "2" : "2: Automatic (based on auto-calibration)"] ],
        [id: 33, size: 1, type: "readonly", readonly: true,
         name: "Dimmability of the Load",
         description : "Read-Only: Indicates the dimmability of the load recognised during auto-calibration. Dimmable (0) / Non-dimmable (1)."],
        [id: 34, size: 1, type: "enum", defaultValue: "1", required: false, readonly: false,
         name: "Soft-Start",
         description : "Time required to warm up the filament of halogen bulbs.",
         options: ["0" : "0: No soft-start",
                   "1" : "1: Short soft-start (0.1s)",
                   "2" : "2: Long soft-start (0.5s)"] ],
        [id: 35, size: 1, type: "enum", defaultValue: "1", required: false, readonly: false,
         name: "Auto-calibration",
         description : "Determines when auto-calibration is triggered.",
         options: ["0" : "0: No auto-calibration",
                   "1" : "1: Auto-calibration after first power on only",
                   "2" : "2: Auto-calibration after each power on",
                   "3" : "3: Auto-calibration after first power on and after each LOAD ERROR",
                   "4" : "4: Auto-calibration after each power on and after each LOAD ERROR"] ],
        [id: 37, size: 1, type: "enum", defaultValue: "1", required: false, readonly: false,
         name: "Behaviour After OVERCURRENT or SURGE",
         description : "The dimmer will turn off when a surge or overcurrent is detected. " +
         "By default, the device performs three attempts to turn on the load.",
         options: ["0" : "0: Device disabled until command or external switch",
                   "1" : "1: Three attempts to turn on the load"] ],
        [id: 39, size: 2, type: "number", range: "0..350", defaultValue : 250, required: false, readonly: false,
         name: "Power Limit - OVERLOAD",
         description : "Reaching the defined value will result in turning off the load. " +
         "Additional apparent power limit of 350VA is active by default.\n" +
         "Values:\n0 = Function Disabled\n1-350 = Power limit (W)"],
        [id: 40, size: 1, type: "enum", defaultValue: "3", required: false, readonly: false,
         name: "Response to General Purpose Alarm",
         description : "",
         options: ["0" : "0: No reaction",
                   "1" : "1: Turn on the load",
                   "2" : "2: Turn off the load",
                   "3" : "3: Load blinking"] ],
        [id: 41, size: 1, type: "enum", defaultValue: "2", required: false, readonly: false,
         name: "Response to Water Flooding Alarm",
         description : "",
         options: ["0" : "0: No reaction",
                   "1" : "1: Turn on the load",
                   "2" : "2: Turn off the load",
                   "3" : "3: Load blinking"] ],
        [id: 42, size: 1, type: "enum", defaultValue: "3", required: false, readonly: false,
         name: "Response to Smoke, CO, or CO2 Alarm",
         description : "",
         options: ["0" : "0: No reaction",
                   "1" : "1: Turn on the load",
                   "2" : "2: Turn off the load",
                   "3" : "3: Load blinking"] ],
        [id: 43, size: 1, type: "enum", defaultValue: "1", required: false, readonly: false,
         name: " Response to Temperature Alarm",
         description : "",
         options: ["0" : "0: No reaction",
                   "1" : "1: Turn on the load",
                   "2" : "2: Turn off the load",
                   "3" : "3: Load blinking"] ],
        [id: 44, size: 2, type: "number", range: "1..32767", defaultValue : 600, required: false, readonly: false,
         name: "Time of Alarm State",
         description : "Values: 1-32767 = Time (s)"],
        [id: 45, size: 1, type: "enum", defaultValue: "1", required: false, readonly: false,
         name: "OVERLOAD Alarm Report",
         description : "Power consumption above Power Limit.",
         options: ["0" : "0: No reaction",
                   "1" : "1: Send an alarm frame"] ],
        [id: 46, size: 1, type: "enum", defaultValue: "1", required: false, readonly: false,
         name: "LOAD ERROR Alarm Report",
         description : "No load, load failure, or burnt out bulb.",
         options: ["0" : "0: No reaction",
                   "1" : "1: Send an alarm frame"] ],
        [id: 47, size: 1, type: "enum", defaultValue: "1", required: false, readonly: false,
         name: "OVERCURRENT Alarm Report",
         description : "Short circuit, or burnt out bulb causing overcurrent",
         options: ["0" : "0: No reaction",
                   "1" : "1: Send an alarm frame"] ],
        [id: 48, size: 1, type: "enum", defaultValue: "1", required: false, readonly: false,
         name: "SURGE Alarm Report",
         description : "",
         options: ["0" : "0: No reaction",
                   "1" : "1: Send an alarm frame"] ],
        [id: 49, size: 1, type: "enum", defaultValue: "1", required: false, readonly: false,
         name: "OVERHEAT and VOLTAGE DROP Alarm Report",
         description : "Critical temperature, or low voltage.",
         options: ["0" : "0: No reaction",
                   "1" : "1: Send an alarm frame"] ],
        [id: 50, size: 1, type: "number", range: "0..100", defaultValue : 10, required: false, readonly: false,
         name: "Power Reports Threshold",
         description : "Power level change that will result in a new power report being sent.\n" +
         "Values:\n0 = Reports disabled\n1-100 = % change from previous report"],
        [id: 52, size: 2, type: "number", range: "0..32767", defaultValue : 3600, required: false, readonly: false,
         name: "Reporting Period",
         description : "The time period between consecutive power and energy reports.\n" +
         "Values:\n0 = Reports disabled\n1-32767 = Time period (s)"],
        [id: 53, size: 2, type: "number", range: "0..255", defaultValue : 10, required: false, readonly: false,
         name: "Energy Reports Threshold",
         description : "Energy level change that will result in a new energy report being sent.\n" +
         "Values:\n0 = Reports disabled,\n1-255 = 0.01-2.55 kWh"],
        [id: 54, size: 1, type: "enum", defaultValue: "0", required: false, readonly: false,
         name: "Self-measurement",
         description : "Include power and energy consumed by the device itself in reports.",
         options: ["0" : "0: Self-measurement DISABLED",
                   "1" : "1: Self-measurement ENABLED"] ],
        [id: 58, size: 1, type: "enum", defaultValue: "0", required: false, readonly: false,
         name: "Method of Calculating Active Power",
         description : "Useful in 2-wire configurations with non-resistive loads.",
         options: ["0" : "0: Standard algorithm",
                   "1" : "1: Based on calibration data",
                   "2" : "2: Based on control angle"] ],
        [id: 59, size: 2, type: "number", range: "0..500", defaultValue : 0, required: false, readonly: false,
         name: "Approximated Power at Max Brightness",
         description : "Determines the approximate value of the power that will be reported by the device at " +
         "it's maximum brightness level.\n" +
         "Values: 0-500 = Power (W)"],
    ]
}

/**
 *  getAssocGroupsMd()
 *
 *  Returns association groups metadata. Used by sync(), updateSyncPending(), and generatePrefsAssocGroups().
 *
 *  Reference: http://products.z-wavealliance.org/products/1729/assoc
 **/
private getAssocGroupsMd() {
    return [
        [id:  1, maxNodes: 1, name: "Lifeline",
         description : "Reports device state. Main Z-Wave controller should be added to this group."],
        [id:  2, maxNodes: 8, name: "On/Off (S1)",
         description : "Sends on/off commands to associated devices when S1 is pressed (BASIC_SET)."],
        [id:  3, maxNodes: 8, name: "Dimmer (S1)",
         description : "Sends dim/brighten commands to associated devices when S1 is pressed (SWITCH_MULTILEVEL_SET)."],
        [id:  4, maxNodes: 8, name: "On/Off (S2)",
         description : "Sends on/off commands to associated devices when S2 is pressed (BASIC_SET)."],
        [id:  5, maxNodes: 8, name: "Dimmer (S2)",
         description : "Sends dim/brighten commands to associated devices when S2 is pressed (SWITCH_MULTILEVEL_SET)."]
    ]
}
