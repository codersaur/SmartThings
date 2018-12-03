/*****************************************************************************************************************
 *  Copyright: David Lomas (codersaur)
 *
 *  Name: Fibaro Flood Sensor Advanced
 *
 *  Author: David Lomas (codersaur)
 *
 *  Date: 2017-03-02
 *
 *  Version: 1.00
 *
 *  Source: https://github.com/codersaur/SmartThings/tree/master/devices/fibaro-flood-sensor
 *
 *  Author: David Lomas (codersaur)
 *
 *  Description: An advanced SmartThings device handler for the Fibaro Flood Sensor (FGFS-101) (EU),
 *   with firmware: 2.6 or older.
 *
 *  For full information, including installation instructions, exmples, and version history, see:
 *   https://github.com/codersaur/SmartThings/tree/master/devices/fibaro-flood-sensor
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
    definition (name: "Fibaro Flood Sensor Advanced", namespace: "codersaur", author: "David Lomas") {
        capability "Sensor"
        capability "Water Sensor"
        capability "Tamper Alert"
        capability "Temperature Measurement"
        capability "Battery"
        capability "Power Source"

        // Standard (Capability) Attributes:
        attribute "battery", "number"
        attribute "powerSource", "enum", ["battery", "dc", "mains", "unknown"]
        attribute "tamper", "enum", ["detected", "clear"]
        attribute "temperature", "number"
        attribute "water", "enum", ["dry", "wet"]

        // Custom Attributes:
        attribute "batteryStatus", "string"     // Indicates DC-power or battery %.
        attribute "logMessage", "string"        // Important log messages.
        attribute "syncPending", "number"       // Number of config items that need to be synced with the physical device.

        // Custom Commands:
        command "resetTamper"
        command "sync"
        command "test"

        // Fingerprints:
        fingerprint mfr: "010F", prod: "0B00", model: "1001"
        fingerprint mfr: "010F", prod: "0B00", model: "2001"
        fingerprint deviceId: "0xA102", inClusters: "0x30,0x9C,0x60,0x85,0x8E,0x72,0x70,0x86,0x80,0x84"
    }

    tiles(scale: 2) {
        multiAttributeTile(name:"multiTile", type:"generic", width:6, height:4) {
            tileAttribute("device.water", key: "PRIMARY_CONTROL") {
                attributeState "dry", label:'', icon:"st.alarm.water.dry", backgroundColor:"#79b821"
                attributeState "wet", label:'', icon:"st.alarm.water.wet", backgroundColor:"#53a7c0"
            }
            tileAttribute("device.temperature", key: "SECONDARY_CONTROL") {
                attributeState "temperature", label:'Temperature: ${currentValue}°C'
            }
        }

        standardTile("water", "device.water", width: 2, height: 2, canChangeIcon: true) {
            state "dry", icon:"st.alarm.water.dry", backgroundColor:"#ffffff"
            state "wet", icon:"st.alarm.water.wet", backgroundColor:"#53a7c0"
        }
        valueTile("temperature", "device.temperature", width: 2, height: 2) {
            state "temperature", label:'${currentValue}°C'
        }
        standardTile("tamper", "device.tamper", decoration: "flat", width: 2, height: 2) {
            state("default", label:"tampered", icon:"st.security.alarm.alarm", backgroundColor:"#FF6600", action: "resetTamper")
            state("clear", label:"clear", icon:"st.security.alarm.clear", backgroundColor:"#ffffff")
        }
        valueTile("battery", "device.battery", width: 2, height: 2, decoration: "flat") {
            state "battery", label:'Battery: ${currentValue}%'
        }
        standardTile("powerSource", "device.powerSource", width: 2, height: 2, decoration: "flat") {
            state "powerSource", label:'${currentValue}-Powered'
        }
        valueTile("batteryStatus", "device.batteryStatus", width: 2, height: 2, decoration: "flat", inactiveLabel: false) {
            state "batteryStatus", label:'${currentValue}', unit:""
        }

        standardTile("syncPending", "device.syncPending", decoration: "flat", width: 2, height: 2) {
            state "default", label:'Sync Pending', backgroundColor:"#FF6600", action:"sync"
            state "0", label:'Synced', action:"", backgroundColor:"#79b821"
        }
        standardTile("test", "device.test", decoration: "flat", width: 2, height: 2) {
            state "default", label:'Test', action:"test"
        }

        main(["water","temperature"])
        details([
            "multiTile",
            //"water", // Also in multiTile.
            //"temperature", // Also in multiTile.
            //"battery",
            //"powerSource",
            "batteryStatus",
            "tamper",
            "syncPending"
            //,"test"
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
                title: "Force Full Sync: All device parameters and association groups will be re-sent to the device. " +
                "This will happen at next wake up or on receipt of an alarm/temperature report.",
                type: "boolean",
//                defaultValue: false, // iPhone users can uncomment these lines!
                required: true
            )

            input (
                name: "configAutoResetTamperDelay",
                title: "Auto-Reset Tamper Alarm:\n" +
                "Automatically reset tamper alarms after this time delay.\n" +
                "Values: 0 = Auto-reset Disabled\n" +
                "1-86400 = Delay (s)\n" +
                "Default Value: 30s",
                type: "number",
                ,
//                defaultValue: "30", // iPhone users can uncomment these lines!
                required: false
            )

        }

        section { // WAKE UP INTERVAL:
            input (
                name: "configWakeUpInterval",
                title: "WAKE UP INTERVAL:\n" +
                "The device will wake up after each defined time interval to sync configuration parameters, " +
                "associations and settings.\n" +
                "Values: 5-86399 = Interval (s)\n" +
                "Default Value: 4000 (every 66 minutes)",
                type: "number",
                ,
//                defaultValue: "4000", // iPhone users can uncomment these lines!
                required: false
            )
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

    def result = []

    if (description.startsWith("Err")) {
        logger("parse(): Unknown Error. Raw message: ${description}","error")
    }
    else {

        // Run testRun() if there is a test pending:
        if (state.testPending) {
            testRun()
        }

        def cmd = zwave.parse(description, getCommandClassVersions())
        if (cmd) {
            result += zwaveEvent(cmd)

            // Attempt sync(), but only if the received message is an unsolicited command:
            if (
                (cmd.commandClassId == 0x20 )  // Basic
                || (cmd.commandClassId == 0x30 )  // Sensor Binary
                || (cmd.commandClassId == 0x31 )  // Sensor Multilevel
                || (cmd.commandClassId == 0x60 )  // Multichannel (SensorMultilevelReport arrive in Multichannel)
                || (cmd.commandClassId == 0x71 )  // Alarm
                || (cmd.commandClassId == 0x84 & cmd.commandId == 0x07) // WakeUpNotification
                || (cmd.commandClassId == 0x9C )  // Sensor Alarm
            ) { sync() }

        } else {
            logger("parse(): Could not parse raw message: ${description}","error")
        }
    }

    // Send wakeUpNoMoreInformation command, but only if there is nothing more to sync:
    if ( (device.latestValue("powerSource") == "battery") & (device.latestValue("syncPending").toInteger() == 0) ) {
        result << response(zwave.wakeUpV1.wakeUpNoMoreInformation())
    }

    return result
}

/*****************************************************************************************************************
 *  Z-wave Event Handlers.
 *****************************************************************************************************************/

/**
 *  zwaveEvent( COMMAND_CLASS_BASIC V1 (0x20) : BASIC_SET )
 *
 *  The Basic Set command is used to set a value in a supporting device.
 *
 *  Note: If this command is received by the hub, the hub will be in Associatin Group 1, and parameter #5 set to 255.
 *   The hub should also receive a corresponding SensorAlarmReport anyway.
 *
 *  Action: Log water event.
 *
 *  cmd attributes:
 *    Short    value
 *      0x00       = Off       = Dry
 *      0x01..0x63 = 0..100%   = Wet
 *      0xFE       = Unknown
 *      0xFF       = On        = Wet
 *
 *  Example: BasicSet(value: 0)
 **/
def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
    logger("zwaveEvent(): Basic Set received: ${cmd}","trace")

    def map = [:]

    map.name = "water"
    map.value = cmd.value ? "wet" : "dry"
    map.descriptionText = "${device.displayName} is ${map.value}"

    return createEvent(map)
}

/**
 *  zwaveEvent( COMMAND_CLASS_SENSOR_BINARY V1 (0x30) : SENSOR_BINARY_REPORT (0x03) )
 *
 *  The Sensor Binary Report command is used to advertise a sensor value.
 *   THIS COMMAND CLASS IS DEPRECIATED!
 *
 *  Action: Do nothing, as we don't event know which sensor the value is from.
 *
 *  Note: The Fibaro Flood Sensor will not send these unless explicitly requested.
 *
 *  cmd attributes:
 *    Short  sensorValue  Sensor Value.
 *
 *  Example: SensorBinaryReport(sensorValue: 0)
 **/
def zwaveEvent(physicalgraph.zwave.commands.sensorbinaryv1.SensorBinaryReport cmd) {
    logger("zwaveEvent(): Sensor Binary Report received: ${cmd}","trace")
}

/**
 *  zwaveEvent( COMMAND_CLASS_SENSOR_MULTILEVEL V2 (0x31) : SENSOR_MULTILEVEL_REPORT (0x05) )
 *
 *  The Multilevel Sensor Report Command is used by a multilevel sensor to advertise a sensor reading.
 *
 *  Action: Raise appropriate type of event (and disp event) and log an info message.
 *
 *  Note: SmartThings does not yet have capabilities corresponding to all possible sensor types, therefore
 *  some of the event types raised below are non-standard.
 *
 *  cmd attributes:
 *    Short         precision           Indicates the number of decimals.
 *                                      E.g. The decimal value 1025 with precision 2 is therefore equal to 10.25.
 *    Short         scale               Indicates what unit the sensor uses.
 *    BigDecimal    scaledSensorValue   Sensor value as a double.
 *    Short         sensorType          Sensor Type (8 bits).
 *    List<Short>   sensorValue         Sensor value as an array of bytes.
 *    Short         size                Indicates the number of bytes used for the sensor value.
 *
 *  Example: SensorMultilevelReport(precision: 2, scale: 0, scaledSensorValue: 20.67, sensorType: 1, sensorValue: [0, 0, 8, 19], size: 4)
 **/
def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv2.SensorMultilevelReport cmd) {
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
 *  zwaveEvent( COMMAND_CLASS_MULTICHANNEL V4 (0x60) : MULTI_CHANNEL_CMD_ENCAP (0x0D))
 *
 *  The Multi Channel Command Encapsulation command is used to encapsulate commands. Any command supported by
 *  a Multi Channel End Point may be encapsulated using this command.
 *
 *  Action: Extract the encapsulated command and pass to the appropriate zwaveEvent() handler.
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
 *  zwaveEvent( COMMAND_CLASS_CONFIGURATION V1 (0x70) : CONFIGURATION_REPORT (0x06) )
 *
 *  The Configuration Report Command is used to advertise the actual value of the advertised parameter.
 *
 *  Action: Store the value in the parameter cache, update syncPending, and log an info message.
 *
 *  Note: The Fibaro Flood Sensor documentation treats some parameter values as SIGNED and others as UNSIGNED!
 *   configurationValues are converted accordingly, using the isSigned attribute from getParamMd().
 *
 *  Note: Ideally, we want to update the corresponding preference value shown on the Settings GUI, however this
 *  is not possible due to security restrictions in the SmartThings platform.
 *
 *  cmd attributes:
 *    List<Short>  configurationValue        Value of parameter (byte array).
 *    Short        parameterNumber           Parameter ID.
 *    Integer      scaledConfigurationValue  Value of parameter (as signed int).
 *    Short        size                      Size of parameter's value (bytes).
 *
 *  Example: ConfigurationReport(configurationValue: [0], parameterNumber: 14, reserved11: 0,
 *            scaledConfigurationValue: 0, size: 1)
 **/
def zwaveEvent(physicalgraph.zwave.commands.configurationv1.ConfigurationReport cmd) {
    logger("zwaveEvent(): Configuration Report received: ${cmd}","trace")

    def paramMd = getParamsMd().find( { it.id == cmd.parameterNumber })
    // Some values are treated as unsigned and some as signed, so we convert accordingly:
    def paramValue = (paramMd?.isSigned) ? cmd.scaledConfigurationValue : byteArrayToUInt(cmd.configurationValue)
    def signInfo = (paramMd?.isSigned) ? "SIGNED" : "UNSIGNED"

    state."paramCache${cmd.parameterNumber}" = paramValue
    logger("Parameter #${cmd.parameterNumber} [${paramMd?.name}] has value: ${paramValue} [${signInfo}]","info")
    updateSyncPending()
}

/**
 *  zwaveEvent( COMMAND_CLASS_NOTIFICATION V3 (0x71) : NOTIFICATION_REPORT (0x05) )
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
                    if (state.autoResetTamperDelay > 0) runIn(state.autoResetTamperDelay, "resetTamper")
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
 *  zwaveEvent( COMMAND_CLASS_MANUFACTURER_SPECIFIC V2 (0x72) : MANUFACTURER_SPECIFIC_REPORT (0x05) )
 *
 *  Manufacturer-Specific Reports are used to advertise manufacturer-specific information, such as product number
 *  and serial number.
 *
 *  Action: Publish values as device 'data'. Log a warn message if manufacturerId and/or productId do not
 *  correspond to Fibaro Flood Sensor V1.
 *
 *  Example: ManufacturerSpecificReport(manufacturerId: 271, manufacturerName: Fibargroup, productId: 4097,
 *   productTypeId: 2816)
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
    if ( 4097 != cmd.productId) logger("Product ID does not match Fibaro Flood Sensor. Using this device handler with a different device may damage you device!","warn")

    updateDataValue("manufacturerName",cmd.manufacturerName)
    updateDataValue("manufacturerId",manufacturerIdDisp)
    updateDataValue("productId",productIdDisp)
    updateDataValue("productTypeId",productTypeIdDisp)
}

/**
 *  zwaveEvent( COMMAND_CLASS_FIRMWARE_UPDATE_MD V2 (0x7A) : FIRMWARE_MD_REPORT (0x02) )
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
 *  zwaveEvent( COMMAND_CLASS_BATTERY V1 (0x80) : BATTERY_REPORT (0x03) )
 *
 *  The Battery Report command is used to report the battery level of a battery operated device.
 *
 *  Action: Raise battery event and log an info message.
 *
 *  cmd attributes:
 *    Integer  batteryLevel  Battery level (%).
 *
 *  Example: BatteryReport(batteryLevel: 52)
 **/
def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {
    logger("zwaveEvent(): Battery Report received: ${cmd}","trace")
    logger("Battery Level: ${cmd.batteryLevel}%","info")

    def result = []
    result << createEvent(name: "powerSource", value: "battery", descriptionText: "Device is using battery.")
    result << createEvent(name: "battery", value: cmd.batteryLevel, unit: "%", displayed: true)
    result << createEvent(name: "batteryStatus", value: "Battery: ${cmd.batteryLevel}%", displayed: false)

    return result
}

/**
 *  zwaveEvent( COMMAND_CLASS_WAKE_UP V1 (0x84) : WAKE_UP_INTERVAL_REPORT (0x06) )
 *
 *  The Wake Up Interval Report command is used to report the wake up interval of a device and the NodeID of the
 *  device receiving the Wake Up Notification Command.
 *
 *  Action: cache value, update syncPending, and log info message.
 *
 *  cmd attributes:
 *    nodeid
 *    seconds
 *
 *  Example: WakeUpIntervalReport(nodeid: 1, seconds: 300)
 **/
def zwaveEvent(physicalgraph.zwave.commands.wakeupv1.WakeUpIntervalReport cmd) {
    logger("zwaveEvent(): Wakeup Interval Report received: ${cmd}","trace")

    state.wakeUpIntervalCache = cmd.seconds.toInteger()
    logger("Wake Up Interval is ${cmd.seconds} seconds.","info")
    updateSyncPending()
}

/**
 *  zwaveEvent( COMMAND_CLASS_WAKE_UP V1 (0x84) : WAKE_UP_NOTIFICATION (0x07) )
 *
 *  The Wake Up Notificaiton command allows a battery-powered device to notify another device that it is awake and
 *  ready to receive any queued commands.
 *
 *  Action: Request BatteryReport, FirmwareMdReport, ManufacturerSpecificReport, and VersionReport.
 *
 *  cmd attributes:
 *    None
 *
 *  Example: WakeUpNotification()
 **/
def zwaveEvent(physicalgraph.zwave.commands.wakeupv1.WakeUpNotification cmd) {
    logger("zwaveEvent(): Wakeup Notification received: ${cmd}","trace")

    logger("Device Woke Up","info")

    def result = []

    result << response(zwave.batteryV1.batteryGet())
    result << response(zwave.firmwareUpdateMdV2.firmwareMdGet())
    result << response(zwave.manufacturerSpecificV2.manufacturerSpecificGet())
    result << response(zwave.versionV1.versionGet())

    // Send wakeUpNoMoreInformation command, but only if there is nothing more to sync:
    if (device.latestValue("syncPending").toInteger() == 0) result << response(zwave.wakeUpV1.wakeUpNoMoreInformation())

    return result
}

/**
 *  zwaveEvent( COMMAND_CLASS_ASSOCIATION V2 (0x85) : ASSOCIATION_REPORT (0x03) )
 *
 *  The Association Report command is used to advertise the current destination nodes of a given association group.
 *
 *  Action: Cache value and log info message only.
 *
 *  Note: Ideally, we want to update the corresponding preference value shown on the Settings GUI, however this
 *  is not possible due to security restrictions in the SmartThings platform.
 *
 *  Example: AssociationReport(groupingIdentifier: 4, maxNodesSupported: 5, nodeId: [1], reportsToFollow: 0)
 **/
def zwaveEvent(physicalgraph.zwave.commands.associationv2.AssociationReport cmd) {
    logger("zwaveEvent(): Association Report received: ${cmd}","trace")

    state."assocGroupCache${cmd.groupingIdentifier}" = cmd.nodeId

    // Display to user in hex format (same as IDE):
    def hexArray  = []
    cmd.nodeId.sort().each { hexArray.add(String.format("%02X", it)) };
    logger("Association Group ${cmd.groupingIdentifier} contains nodes: ${hexArray} (hexadecimal format)","info")

    updateSyncPending()
}

/**
 *  zwaveEvent( COMMAND_CLASS_VERSION V1 (0x86) : VERSION_REPORT (0x12) )
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
 *  zwaveEvent( COMMAND_CLASS_MULTI_CHANNEL_ASSOCIATION V2 (0x8E) : ASSOCIATION_REPORT (0x03) )
 *
 *  The Multi-channel Association Report command is used to advertise the current destinations of a given
 *  association group (nodes and endpoints).
 *
 *  Action: Store the destinations in the assocGroup cache, update syncPending, and log an info message.
 *   Also, if maxNodesSupported is reported as zero for Assoc Group #3, then disable future sync of this group.
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
 *  zwaveEvent( COMMAND_CLASS_SENSOR_ALARM V1 (0x9C) : SENSOR_ALARM_REPORT (0x02) )
 *
 *  The Sensor Alarm Report command is used to advertise the alarm state.
 *   THIS COMMAND CLASS IS DEPRECIATED! But still used by the device.
 *
 *  Action: Raies water or tamper event. Log info message.
 *
 *  cmd attributes:
 *    Integer  seconds       Time the alarm has been active.
 *    Short    sensorState   Sensor state.
 *      0x00      = No Alarm
 *      0x01-0x64 = Alarm Severity
 *      0xFF      = Alarm.
 *    Short    sensorType    Sensor Type.
 *    Short    sourceNodeId  Z-Wave node ID of sending device.
 *
 *  Example: SensorAlarmReport(seconds: 0, sensorState: 255, sensorType: 0, sourceNodeId: 7)
 **/
def zwaveEvent(physicalgraph.zwave.commands.sensoralarmv1.SensorAlarmReport cmd) {
    logger("zwaveEvent(): Sensor Alarm Report received: ${cmd}","trace")

    def map = [:]

    switch (cmd.sensorType) {
        case 0:  // General Purpose Alarm
        case 1:  // Smoke Alarm (but used here as tamper)
            map.name = "tamper"
            map.isStateChange = true
            map.value = cmd.sensorState ? "detected" : "clear"
            map.descriptionText = "${device.displayName} has been tampered with."
            logger("Device has been tampered with!","info")
            if (state.autoResetTamperDelay > 0) runIn(state.autoResetTamperDelay, "resetTamper")
            break

        case 5:  // Water Leak Alarm
            map.name = "water"
            map.isStateChange = true
            map.value = cmd.sensorState ? "wet" : "dry"
            map.descriptionText = "${device.displayName} is ${map.value}."
            logger("Device is ${map.value}!","info")
        break

        default:
            logger("zwaveEvent(): SensorAlarmReport with unhandled sensorType: ${cmd}","warn")
            map.name = "unknown"
            map.value = cmd.sensorState
            break
    }

    return createEvent(map)
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
 *  Capability-related Commands: [None]
 *****************************************************************************************************************/


/*****************************************************************************************************************
 *  Custom Commands:
 *****************************************************************************************************************/

/**
 *  resetTamper()
 *
 *  Clear tamper status.
 **/
def resetTamper() {
    logger("resetTamper(): Resetting tamper alarm.","info")
    sendEvent(name: "tamper", value: "clear", descriptionText: "Tamper alarm cleared", displayed: true)
}

/*****************************************************************************************************************
 *  SmartThings System Commands:
 *****************************************************************************************************************/

/**
 *  installed()
 *
 *  Runs when the device is first installed.
 *
 *  Action: Set initial values for internal state.
 **/
def installed() {
    log.trace "installed()"

    state.installedAt = now()
    state.loggingLevelIDE     = 5
    state.loggingLevelDevice  = 2

    // Initial settings:
    logger("Performing initial setup","info")
    sendEvent(name: "tamper", value: "clear", descriptionText: "Tamper cleared", displayed: false)
    sendEvent(name: "water", value: "dry", displayed: false)

    if (getZwaveInfo()?.zw?.startsWith("L")) {
        logger("Device is in listening mode (powered).","info")
        sendEvent(name: "powerSource", value: "dc", descriptionText: "Device is connected to DC power supply.")
        sendEvent(name: "batteryStatus", value: "DC-power", displayed: false)
    }
    else {
        logger("Device is in sleepy mode (battery).","info")
        sendEvent(name: "powerSource", value: "battery", descriptionText: "Device is using battery.")
        state.wakeUpIntervalTarget = 300
    }

    state.paramTarget74 = 3 // enable movement and tmp alerts at start to help sync.
    state.assocGroupTarget3 = [ zwaveHubNodeId ]
    sync()

    // Request extra info (same as wakeup):
    def cmds = []
    cmds << zwave.batteryV1.batteryGet()
    cmds << zwave.firmwareUpdateMdV2.firmwareMdGet()
    cmds << zwave.manufacturerSpecificV2.manufacturerSpecificGet()
    cmds << zwave.versionV1.versionGet()
    sendSequence(cmds, 400)

}

/**
 *  updated()
 *
 *  Runs when the user hits "Done" from Settings page.
 *
 *  Action: Process new settings, set targets for wakeup interval, parameters, and association groups (ready for next sync).
 *
 *  Note: Weirdly, update() seems to be called twice. So execution is aborted if there was a previous execution
 *  within two seconds. See: https://community.smartthings.com/t/updated-being-called-twice/62912
 **/
def updated() {
    logger("updated()","trace")

    if (!state.updatedLastRanAt || now() >= state.updatedLastRanAt + 2000) {
        state.updatedLastRanAt = now()

        // Update internal state:
        state.loggingLevelIDE       = (settings.configLoggingLevelIDE) ? settings.configLoggingLevelIDE.toInteger() : 3
        state.loggingLevelDevice    = (settings.configLoggingLevelDevice) ? settings.configLoggingLevelDevice.toInteger(): 2
        state.syncAll               = ("true" == settings.configSyncAll)
        state.autoResetTamperDelay  = (settings.configAutoResetTamperDelay) ? settings.configAutoResetTamperDelay.toInteger() : 0

        // Update Wake Up Interval target:
        state.wakeUpIntervalTarget = (settings.configWakeUpInterval) ? settings.configWakeUpInterval.toInteger() : 3600

        // Update Parameter target values:
        getParamsMd().findAll( {!it.readonly} ).each { // Exclude readonly parameters.
            state."paramTarget${it.id}" = settings."configParam${it.id}"?.toInteger()
        }

        // Update Assoc Group target values:
        getAssocGroupsMd().findAll( { it.id != 3} ).each {
            state."assocGroupTarget${it.id}" = parseAssocGroupInput(settings."configAssocGroup${it.id}", it.maxNodes)
        }
        // Assoc Group #3 will contain controller only:
        state.assocGroupTarget3 = [ zwaveHubNodeId ]

        (device.latestValue("powerSource") == "dc") ? sync() : updateSyncPending()

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
 *  Manages synchronisation of parameters, association groups, and wake up interval with the physical device.
 *  The syncPending attribute advertises remaining number of sync operations.
 *
 *  Does not return a list of commands, it sends them immediately using sendSequence().
 *
 *  Parameters:
 *   forceAll    Force all items to be synced, otherwise only changed items will be synced.
 **/
private sync(forceAll = false) {
    logger("sync(): Syncing configuration with the physical device.","info")

    def cmds = []
    def syncPending = 0

    if (forceAll || state.syncAll) { // Clear all cached values.
        state.wakeUpIntervalCache = null
        getParamsMd().findAll( {!it.readonly} ).each { state."paramCache${it.id}" = null }
        getAssocGroupsMd().each { state."assocGroupCache${it.id}" = null }
        state.syncAll = false
    }

    if ( (device.latestValue("powerSource") != "dc") & (state.wakeUpIntervalTarget != null) & (state.wakeUpIntervalTarget != state.wakeUpIntervalCache)) {
        cmds << zwave.wakeUpV1.wakeUpIntervalSet(seconds: state.wakeUpIntervalTarget, nodeid: zwaveHubNodeId)
        cmds << zwave.wakeUpV1.wakeUpIntervalGet().format()
        logger("sync(): Syncing Wake Up Interval: New Value: ${state.wakeUpIntervalTarget}","info")
        syncPending++
    }

    getParamsMd().findAll( {!it.readonly} ).each { // Exclude readonly parameters.
        if ( (state."paramTarget${it.id}" != null) & (state."paramCache${it.id}" != state."paramTarget${it.id}") ) {
            // configurationSet will detect if scaledConfigurationValue is SIGNEd or UNSIGNED and convert accordingly:
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
            if (it.id  == 3) { // Assoc Group #3 does not support multi-channel, must use regular associationV2.
                cmds << zwave.associationV2.associationSet(groupingIdentifier: it.id, nodeId: []) // Remove All
                cmds << zwave.associationV2.associationSet(groupingIdentifier: it.id, nodeId:[zwaveHubNodeId])
                cmds << zwave.associationV2.associationGet(groupingIdentifier: it.id)
            }
            else {
                cmds << zwave.multiChannelAssociationV2.multiChannelAssociationRemove(groupingIdentifier: it.id, nodeId: []) // Remove All
                cmds << zwave.multiChannelAssociationV2.multiChannelAssociationSet(groupingIdentifier: it.id, nodeId: targetNodes)
                cmds << zwave.multiChannelAssociationV2.multiChannelAssociationGet(groupingIdentifier: it.id)
            }

            syncPending++
        }
    }

    sendEvent(name: "syncPending", value: syncPending, displayed: false)
    sendSequence(cmds,800) // 800ms seems a reasonable balance.
}

/**
 *  updateSyncPending()
 *
 *  Updates syncPending attribute, which advertises remaining number of sync operations.
 **/
private updateSyncPending() {

    def syncPending = 0

    if ( (device.latestValue("powerSource") != "dc") & (state.wakeUpIntervalTarget != null) & (state.wakeUpIntervalTarget != state.wakeUpIntervalCache)) {
        syncPending++
    }

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

    logger("updateSyncPending(): syncPending: ${syncPending}", "debug")
    if ((syncPending == 0) & (device.latestValue("syncPending") > 0)) logger("Sync Complete.", "info")
    sendEvent(name: "syncPending", value: syncPending, displayed: false)
}

/**
 *  refreshConfig()
 *
 *  Request configuration reports from the physical device: [ Configuration, Association,
 *  Manufacturer-Specific, Firmware Metadata, Version, etc. ]
 *
 *  Really only needed at installation or when debugging, as sync will request the necessary reports when the
 *  configuration is changed.
 */
private refreshConfig() {
    logger("refreshConfig()","trace")

    if (getZwaveInfo()?.zw?.startsWith("L")) {
        logger("Device is in listening mode (powered).","info")
        sendEvent(name: "powerSource", value: "dc", descriptionText: "Device is connected to DC power supply.")
    }
    else {
        logger("Device is in sleepy mode (battery).","info")
        sendEvent(name: "powerSource", value: "battery", descriptionText: "Device is using battery.")
    }

    def cmds = []

    cmds << zwave.wakeUpV1.wakeUpIntervalGet()
    cmds << zwave.firmwareUpdateMdV2.firmwareMdGet()
    cmds << zwave.manufacturerSpecificV2.manufacturerSpecificGet()
    cmds << zwave.versionV1.versionGet()

    getParamsMd().each { cmds << zwave.configurationV1.configurationGet(parameterNumber: it.id) }
    getAssocGroupsMd().findAll( { it.id != 3 } ).each { cmds << zwave.multiChannelAssociationV2.multiChannelAssociationGet(groupingIdentifier: it.id) }
    cmds << zwave.associationV2.associationGet(groupingIdentifier:3)

    sendSequence(cmds, 500) // Delay must be at least 1000 to reliabilty get all results processed.
}

/**
 *  sendSequence()
 *
 *  Send an array of commands using sendHubCommand.
 **/
private sendSequence(commands, delay = 200) {
    sendHubCommand(commands.collect{ response(it) }, delay)
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
                description: "Association groups enable this device to control other Z-Wave devices directly, " +
                             "without participation of the main controller.\n" +
                             "Enter a comma-delimited list of destinations (node IDs and/or endpoint IDs) for " +
                             "each association group. All IDs must be in hexadecimal format. E.g.:\n" +
                             "Node destinations: '11, 0F'\n" +
                             "Endpoint destinations: '1C:1, 1C:2'"
            )

    getAssocGroupsMd().findAll( { it.id != 3} ).each { // Don't show AssocGroup3 (Lifeline).
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
 *  byteArrayToUInt(byteArray)
 *
 *  Converts a byte array to an UNSIGNED int.
 **/
private byteArrayToUInt(byteArray) {
    // return java.nio.ByteBuffer.wrap(byteArray as byte[]).getInt()
    def i = 0
    byteArray.reverse().eachWithIndex { b, ix -> i += b * (0x100 ** ix) }
    return i
}

/**
 *  test()
 *
 *  Called from 'test' tile.
 **/
private test() {
    logger("test()","trace")
    state.testPending = true

    // immediate test actions:
    def cmds = []
    //cmds << ...
    if (cmds) sendSequence(cmds,200)
}

/**
 *  testRun()
 *
 *  Async Testing method. Called when device wakes up and state.testPending = true.
 **/
private testRun() {
    logger("testRun()","trace")

    def cmds = []
    //cmds << zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType:1) //sensorType:1
    //cmds << zwave.wakeUpV2.wakeUpIntervalCapabilitiesGet()
    //cmds << zwave.batteryV1.batteryGet()

    if (cmds) sendSequence(cmds,500)

    state.testPending = false
}

/*****************************************************************************************************************
 *  Static Matadata Functions:
 *
 *  These functions encapsulate metadata about the device. Mostly obtained from:
 *   Z-wave Alliance Reference: http://products.z-wavealliance.org/products/1036
 *****************************************************************************************************************/

/**
 *  getCommandClassVersions()
 *
 *  Returns a map of the command class versions supported by the device. Used by parse() and zwaveEvent() to
 *  extract encapsulated commands from MultiChannelCmdEncap, MultiInstanceCmdEncap, SecurityMessageEncapsulation,
 *  and Crc16Encap messages.
 *
 *  Reference: http://products.z-wavealliance.org/products/1036/classes
 **/
private getCommandClassVersions() {
    return [0x20: 1, // Basic V1
            0x30: 1, // Sensor Binary V1 (not even v2).
            0x31: 2, // Sensor Multilevel V?
            0x60: 3, // Multi Channel V?
            0x70: 1, // Configuration V1
            0x71: 1, // Alarm (Notification) V1
            0x72: 2, // Manufacturer Specific V2
            0x7A: 2, // Firmware Update MD V2
            0x80: 1, // Battery V1
            0x84: 1, // Wake Up V1
            0x85: 2, // Association V2
            0x86: 1, // Version V1
            0x8E: 2, // Multi Channel Association V2
            0x9C: 1 // Sensor Alarm V1
           ]
}

/**
 *  getParamsMd()
 *
 *  Returns device parameters metadata. Used by sync(), updateSyncPending(), and generatePrefsParams().
 *
 *  Note: The Fibaro documentation treats *some* parameter values as SIGNED and others as UNSIGNED,
 *   e.g.: 1-bit parameters with values 0-255 = UNSIGNED.
 *   The treatment of each parameter is identified in getParamMd() by attribute isSigned.
 *   Unsigned parameter values are converted from signed to unsigned when receiving config reports.
 *
 *  Reference: http://manuals.fibaro.com/flood-sensor/
 **/
private getParamsMd() {
    return [
        [id:  1, size: 2, type: "number", range: "0..3600", defaultValue: 0, required: false, readonly: false,
         isSigned: true,
         name: "Alarm Cancellation Delay",
         description: "The time for which the device will retain the flood state after flooding has ceased.\n" +
         "Values: 0-3600 = Time Delay (s)"],
        [id: 2, size: 1, type: "enum", defaultValue: "3", required: false, readonly: false,
         isSigned: true,
         name: "Acoustic and Visual Alarms",
         description : "Disable/enable LED indicator and acoustic alarm for flooding detection.",
         options: ["0" : "0: Acoustic alarm INACTIVE. Visual alarm INACVTIVE",
                   "1" : "1: Acoustic alarm INACTIVE. Visual alarm ACTIVE",
                   "2" : "2: Acoustic alarm ACTIVE. Visual alarm INACTIVE",
                   "3" : "3: Acoustic alarm ACTIVE. Visual alarm ACTIVE"] ],
        [id: 5, size: 1, type: "enum", defaultValue: "255", required: false, readonly: false,
         isSigned: false,
         name: "Type of Alarm sent to Association Group 1",
         description : "",
         options: ["0" : "0: ALARM WATER command",
                   "255" : "255: BASIC_SET command"] ],
        [id: 7, size: 1, type: "number", range: "1..255", defaultValue : 255, required: false, readonly: false,
         isSigned: false,
         name: "Level sent to Association Group 1",
         description : "Determines the level sent (BASIC_SET) to Association Group 1 on alarm.\n" +
         "Values: 1-99 = Level\n255 = Last memorised state"],
        [id: 9, size: 1, type: "enum", defaultValue: "1", required: false, readonly: false,
         isSigned: true,
         name: "Alarm Cancelling",
         description : "",
         options: ["0" : "0: Alarm cancellation INACTIVE",
                   "1" : "1: Alarm cancellation ACTIVE"] ],
        [id: 10, size: 2, type: "number", range: "1..65535", defaultValue : 300, required: false, readonly: false,
         isSigned: false,
         name: "Temperature Measurement Interval",
         description : "Time between consecutive temperature measurements. New temperature value is reported to " +
         "the main controller only if it differs from the previously measured by hysteresis (parameter #12).\n" +
         "Values: 1-65535 = Time (s)"],
        [id: 12, size: 2, type: "number", range: "1..1000", defaultValue : 50, required: false, readonly: false,
         isSigned: true,
         name: "Temperature Measurement Hysteresis",
         description : "Determines the minimum temperature change resulting in a temperature report being " +
         "sent to the main controller.\n" +
         "Values: 1-1000 = Temp change (in 0.01C steps)"],
        [id: 13, size: 1, type: "enum", defaultValue: "0", required: false, readonly: false,
         isSigned: true,
         name: "Alarm Broadcasts",
         description : "Determines if flood and tamper alarms are broadcast to all devices.",
         options: ["0" : "0: Flood alarm broadcast INACTIVE. Tamper alarm broadcast INACTIVE",
                   "1" : "1: Flood alarm broadcast ACTIVE. Tamper alarm broadcast INACTIVE",
                   "2" : "2: Flood alarm broadcast INACTIVE. Tamper alarm broadcast ACTIVE",
                   "3" : "3: Flood alarm broadcast ACTIVE. Tamper alarm broadcast ACTIVE"] ],
        [id: 50, size: 2, type: "number", range: "-10000..10000", defaultValue : 1500, required: false, readonly: false,
         isSigned: true,
         name: "Low Temperature Alarm Threshold",
         description : "Temperature below which LED indicator blinks (with a colour determined by Parameter #61).\n" +
         "Values: -10000-10000 = Temp (-100C to +100C in 0.01C steps)"],
        [id: 51, size: 2, type: "number", range: "-10000..10000", defaultValue : 3500, required: false, readonly: false,
         isSigned: true,
         name: "High Temperature Alarm Threshold",
         description : "Temperature above which LED indicator blinks (with a colour determined by Parameter #62).\n" +
         "Values: -10000-10000 = Temp (-100C to +100C in 0.01C steps)"],
        [id: 61, size: 4, type: "number", range: "0..16777215", defaultValue : 255, required: false, readonly: false,
         isSigned: false,
         name: "Low Temperature Alarm indicator Colour",
         description : "Indicated colour = 65536 * RED value + 256 * GREEN value + BLUE value.\n" +
         "Values: 0-16777215"],
        [id: 62, size: 4, type: "number", range: "0..16777215", defaultValue : 16711680, required: false, readonly: false,
         isSigned: false,
         name: "High Temperature Alarm indicator Colour",
         description : "Indicated colour = 65536 * RED value + 256 * GREEN value + BLUE value.\n" +
         "Values: 0-16777215"],
        [id: 63, size: 1, type: "enum", defaultValue: "2", required: false, readonly: false,
         isSigned: true,
         name: "LED Indicator Operation",
         description : "LED Indicator can be turned off to save battery.",
         options: ["0" : "0: OFF",
                   "1" : "1: BLINK (every temperature measurement)",
                   "2" : "2: CONTINUOUS (constant power only)"] ],
        [id: 73, size: 2, type: "number", range: "-10000..10000", defaultValue : 0, required: false, readonly: false,
         isSigned: true,
         name: "Temperature Measurement Compensation",
         description : "Temperature value to be added to or deducted to compensate for the difference between air " +
         "temperature and temperature at the floor level.\n" +
         "Values: -10000-10000 = Temp (-100C to +100C in 0.01C steps)"],
        [id: 74, size: 1, type: "enum", defaultValue: "2", required: false, readonly: false,
         isSigned: true,
         name: "Alarm Frame Sent to Association Group #2",
         description : "Turn on alarms resulting from movement and/or the TMP button released.",
         options: ["0" : "0: TMP Button INACTIVE. Movement INACTIVE",
                   "1" : "1: TMP Button ACTIVE. Movement INACTIVE",
                   "2" : "2: TMP Button INACTIVE. Movement ACTIVE",
                   "3" : "3: TMP Button ACTIVE. Movement ACTIVE"] ],
        [id: 75, size: 2, type: "number", range: "0..65535", defaultValue : 0, required: false, readonly: false,
         isSigned: false,
         name: "Visual and Audible Alarms Duration",
         description : "Time period after which the LED and audible alarm the will become quiet. ignored when parameter #2 is 0.\n" +
         "Values: 0 = Active indefinitely\n" +
         "1-65535 = Time (s)"],
        [id: 76, size: 2, type: "number", range: "0..65535", defaultValue : 0, required: false, readonly: false,
         isSigned: false,
         name: "Alarm Retransmission Time",
         description : "Time period after which an alarm frame will be retransmitted.\n" +
         "Values: 0 = No retransmission\n" +
         "1-65535 = Time (s)"],
        [id: 77, size: 1, type: "enum", defaultValue: "0", required: false, readonly: false,
         isSigned: true,
         name: "Flood Sensor Functionality",
         description : "Allows for turning off the internal flood sensor. Tamper and temperature sensor will remain active.",
         options: ["0" : "0: Flood sensor ACTIVE",
                   "1" : "1: Flood sensor INACTIVE"] ]
    ]
}

/**
 *  getAssocGroupsMd()
 *
 *  Returns association groups metadata. Used by sync(), updateSyncPending(), and generatePrefsAssocGroups().
 *
 *  Reference: http://manuals.fibaro.com/flood-sensor/
 **/
private getAssocGroupsMd() {
    return [
        [id:  1, maxNodes: 5, name: "Device Status", // Water state?
         description : "Reports device state, sending BASIC SET or ALARM commands."],
        [id:  2, maxNodes: 5, name: "TMP Button and Tilt Sensor",
         description : "Sends ALARM commands to associated devices when TMP button is released or a tilt is triggered (depending on parameter 74)."],
        [id:  3, maxNodes: 0, name: "Device Status",
         description : "Reports device state. Main Z-Wave controller should be added to this group."]
    ]
}
