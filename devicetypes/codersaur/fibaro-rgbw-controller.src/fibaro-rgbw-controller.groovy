/**
 *  Copyright David Lomas (codersaur)
 *
 *  SmartThings Device Handler for: Fibaro RGBW Controller EU v2.x (FGRGBWM-441)
 *
 *  Version: 0.04 (2017-04-17)
 *
 *  Source: https://github.com/codersaur/SmartThings/tree/master/devices/fibaro-rgbw-controller
 *
 *  Author: David Lomas (codersaur)
 *
 *  Description: This SmartThings device handler is written for the Fibaro RGBW Controller (FGRGBWM-441). It extends
 *  the native SmartThings device handler to support editing the device's parameters from the SmartThings GUI, and to
 *  support the use of one or more of the controller's channels in IN/OUT mode.
 *
 *  For full information, including installation instructions, exmples, and version history, see:
 *   https://github.com/codersaur/SmartThings/tree/master/devices/fibaro-rgbw-controller
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
    definition (name: "Fibaro RGBW Controller", namespace: "codersaur", author: "David Lomas") {
        capability "Actuator"
        capability "Switch"
        capability "Switch Level"
        capability "Color Control"
        capability "Sensor"
        capability "Energy Meter"
        capability "Power Meter"
        capability "Refresh"
        capability "Polling"

        // Standard Attributes (for the capabilities above):
        attribute "switch", "enum", ["on", "off"]
        attribute "level", "number"
        attribute "hue", "number"
        attribute "saturation", "number"
        attribute "color", "string"
        attribute "energy", "number"
        attribute "power", "number"

        // Custom Attributes:
        attribute "activeProgram", "number" // Required for Program Tiles.
        attribute "colorName", "string" // Required for Color Shortcut Tiles.
        attribute "lastReset", "string" // Last Time that energy reporting period was reset.

        // Custom Commands:
        command "test"
        command "getConfigReport"
        command "reset"

        // Raw Channel attributes and commands:
        (1..4).each { n ->
            attribute "switchCh${n}", "enum", ["on", "off"]
            attribute "levelCh${n}", "number"
            command "onCh$n"
            command "offCh$n"
            command "setLevelCh$n"
        }

        // Color Channel attributes and commands:
        ["Red", "Green", "Blue", "White"].each { c ->
            attribute "switch${c}", "enum", ["on", "off"]
            attribute "level${c}", "number"
            command "on${c}"
            command "off${c}"
            command "setLevel${c}"
        }

        // Color shortcut commands:
        command "black"
        command "white"
        command "red"
        command "green"
        command "blue"
        command "cyan"
        command "magenta"
        command "orange"
        command "purple"
        command "yellow"
        command "pink"
        command "coldWhite"
        command "warmWhite"

        // Program commands:
        command "startProgram"
        command "stopProgram"
        command "startFireplace"
        command "startStorm"
        command "startDeepFade"
        command "startLiteFade"
        command "startPolice"

        fingerprint deviceId: "0x1101", inClusters: "0x27,0x72,0x86,0x26,0x60,0x70,0x32,0x31,0x85,0x33"
    }

    tiles (scale: 2){
        // MultiTile:
        multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
            tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
                attributeState "on", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#79B821", nextState:"turningOff"
                attributeState "off", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
                attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#79B821", nextState:"turningOff"
                attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
            }
            tileAttribute ("device.level", key: "SLIDER_CONTROL", range:"(0..500)") {
                attributeState "level", action:"setLevel"
            }
            tileAttribute ("device.color", key: "COLOR_CONTROL") {
                attributeState "color", action:"setColor"
            }
            tileAttribute ("device.power", key: "SECONDARY_CONTROL") {
                attributeState "power", label:'${currentValue} W'
            }
        }

        // Colour Channels:
        standardTile("switchRed", "device.switchRed", height: 1, width: 1, inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"R", action:"onRed", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "on", label:"R", action:"offRed", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FF0000"
        }
        controlTile("levelRedSlider", "device.levelRed", "slider", range:"(0..100)", height: 1, width: 4, inactiveLabel: false) {
            state "levelRed", action:"setLevelRed"
        }
        valueTile("levelRedTile", "device.levelRed", decoration: "flat", height: 1, width: 1) {
            state "levelRed", label:'${currentValue}%'
        }

        standardTile("switchGreen", "device.switchGreen", height: 1, width: 1, inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"G", action:"onGreen", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "on", label:"G", action:"offGreen", icon:"st.illuminance.illuminance.bright", backgroundColor:"#00FF00"
        }
        controlTile("levelGreenSlider", "device.levelGreen", "slider", range:"(0..100)", height: 1, width: 4, inactiveLabel: false) {
            state "levelGreen", action:"setLevelGreen"
        }
        valueTile("levelGreenTile", "device.levelGreen", decoration: "flat", height: 1, width: 1) {
            state "levelGreen", label:'${currentValue}%'
        }

        standardTile("switchBlue", "device.switchBlue", height: 1, width: 1, inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"B", action:"onBlue", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "on", label:"B", action:"offBlue", icon:"st.illuminance.illuminance.bright", backgroundColor:"#0000FF"
        }
        controlTile("levelBlueSlider", "device.levelBlue", "slider", range:"(0..100)", height: 1, width: 4, inactiveLabel: false) {
            state "levelBlue", action:"setLevelBlue"
        }
        valueTile("levelBlueTile", "device.levelBlue", decoration: "flat", height: 1, width: 1) {
            state "levelBlue", label:'${currentValue}%'
        }

        standardTile("switchWhite", "device.switchWhite", height: 1, width: 1, inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"W", action:"onWhite", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "on", label:"W", action:"offWhite", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FFFFFF"
        }
        controlTile("levelWhiteSlider", "device.levelWhite", "slider", range:"(0..100)", height: 1, width: 4, inactiveLabel: false) {
            state "levelWhite", action:"setLevelWhite"
        }
        valueTile("levelWhiteTile", "device.levelWhite", decoration: "flat", height: 1, width: 1) {
            state "levelWhite", label:'${currentValue}%'
        }

        // OUT Channels:
        standardTile("switchCh1", "device.switchCh1", height: 1, width: 1, inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"1", action:"onCh1", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "on", label:"1", action:"offCh1", icon:"st.illuminance.illuminance.bright", backgroundColor:"#79B821"
        }
        controlTile("levelCh1Slider", "device.levelCh1", "slider", range:"(0..100)", height: 1, width: 4, inactiveLabel: false) {
            state "levelCh1", action:"setLevelCh1"
        }
        valueTile("levelCh1Tile", "device.levelCh1", decoration: "flat", height: 1, width: 1) {
            state "levelCh1", label:'${currentValue}%'
        }

        standardTile("switchCh2", "device.switchCh2", height: 1, width: 1, inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"2", action:"onCh2", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "on", label:"2", action:"offCh2", icon:"st.illuminance.illuminance.bright", backgroundColor:"#79B821"
        }
        controlTile("levelCh2Slider", "device.levelCh2", "slider", range:"(0..100)", height: 1, width: 4, inactiveLabel: false) {
            state "levelCh2", action:"setLevelCh2"
        }
        valueTile("levelCh2Tile", "device.levelCh2", decoration: "flat", height: 1, width: 1) {
            state "levelCh2", label:'${currentValue}%'
        }

        standardTile("switchCh3", "device.switchCh3", height: 1, width: 1, inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"3", action:"onCh3", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "on", label:"3", action:"offCh3", icon:"st.illuminance.illuminance.bright", backgroundColor:"#79B821"
        }
        controlTile("levelCh3Slider", "device.levelCh3", "slider", range:"(0..100)", height: 1, width: 4, inactiveLabel: false) {
            state "levelCh3", action:"setLevelCh3"
        }
        valueTile("levelCh3Tile", "device.levelCh3", decoration: "flat", height: 1, width: 1) {
            state "levelCh3", label:'${currentValue}%'
        }

        standardTile("switchCh4", "device.switchCh4", height: 1, width: 1, inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"4", action:"onCh4", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8"
            state "on", label:"4", action:"offCh4", icon:"st.illuminance.illuminance.bright", backgroundColor:"#79B821"
        }
        controlTile("levelCh4Slider", "device.levelCh4", "slider", range:"(0..100)", height: 1, width: 4, inactiveLabel: false) {
            state "levelCh4", action:"setLevelCh4"
        }
        valueTile("levelCh4Tile", "device.levelCh4", decoration: "flat", height: 1, width: 1) {
            state "levelCh4", label:'${currentValue}%'
        }

        // IN Channels (READ-ONLY) Labels:
        valueTile("switchCh1ReadOnly", "device.switchCh1", decoration: "flat", height: 1, width: 1) {
            state "default", label:'${currentValue}'
        }
        valueTile("ch1Label", "device.switchCh1", decoration: "flat", height: 1, width: 4) {
            state "default", label:'Channel #1 (Input):'
        }

        valueTile("switchCh2ReadOnly", "device.switchCh2", decoration: "flat", height: 1, width: 1) {
            state "default", label:'${currentValue}'
        }
        valueTile("ch2Label", "device.switchCh1", decoration: "flat", height: 1, width: 4) {
            state "default", label:'Channel #2 (Input):'
        }

        valueTile("switchCh3ReadOnly", "device.switchCh3", decoration: "flat", height: 1, width: 1) {
            state "default", label:'${currentValue}'
        }
        valueTile("ch3Label", "device.switchCh1", decoration: "flat", height: 1, width: 4) {
            state "default", label:'Channel #3 (Input):'
        }

        valueTile("switchCh4ReadOnly", "device.switchCh4", decoration: "flat", height: 1, width: 1) {
            state "default", label:'${currentValue}'
        }
        valueTile("ch4Label", "device.switchCh1", decoration: "flat", height: 1, width: 4) {
            state "default", label:'Channel #4 (Input):'
        }

        // Power
        valueTile("powerLabel", "device.power", decoration: "flat", height: 1, width: 2) {
            state "default", label:'Power:', action:"refresh.refresh", icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_refresh.png"
        }
        valueTile("power", "device.power", decoration: "flat", height: 1, width: 2) {
            state "power", label:'${currentValue} W'
        }

        // Energy:
        valueTile("lastReset", "device.lastReset", decoration: "flat", height: 1, width: 2) {
            state "default", label:'Since:  ${currentValue}', action:"reset", icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_stopwatch_reset.png"
        }
        valueTile("energy", "device.energy", height: 1, width: 2) {
            state "default", label:'${currentValue} kWh', icon: "https://raw.githubusercontent.com/codersaur/SmartThings/master/icons/tile_2x1_top_bottom_2.png"
        }

        // Programs:
        standardTile("fireplace", "device.activeProgram", height: 2, width: 2, decoration: "flat", inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"Fireplace", action:"startFireplace", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8", defaultState: true
            state "6", label:"Fireplace", action:"stopProgram", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FF0000"
        }
        standardTile("storm", "device.activeProgram", height: 2, width: 2, decoration: "flat", inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"storm", action:"startStorm", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8", defaultState: true
            state "7", label:"storm", action:"stopProgram", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FF0000"
        }
        standardTile("deepFade", "device.activeProgram", height: 2, width: 2, decoration: "flat", inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"deep fade", action:"startDeepFade", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8", defaultState: true
            state "8", label:"deep fade", action:"stopProgram", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FF0000"
        }
        standardTile("liteFade", "device.activeProgram", height: 2, width: 2, decoration: "flat", inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"lite fade", action:"startLiteFade", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8", defaultState: true
            state "9", label:"lite fade", action:"stopProgram", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FF0000"
        }
        standardTile("police", "device.activeProgram", height: 2, width: 2, decoration: "flat", inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"police", action:"startPolice", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8", defaultState: true
            state "10", label:"police", action:"stopProgram", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FF0000"
        }

        // Colour Shortcuts:
        standardTile("red", "device.colorName", height: 2, width: 2, decoration: "flat", inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"red", action:"red", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8", defaultState: true
            state "red", label:"red", action:"off", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FF0000"
        }
        standardTile("green", "device.colorName", height: 2, width: 2, decoration: "flat", inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"green", action:"green", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8", defaultState: true
            state "green", label:"green", action:"off", icon:"st.illuminance.illuminance.bright", backgroundColor:"#00FF00"
        }
        standardTile("blue", "device.colorName", height: 2, width: 2, decoration: "flat", inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"blue", action:"blue", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8", defaultState: true
            state "blue", label:"blue", action:"off", icon:"st.illuminance.illuminance.bright", backgroundColor:"#0000FF"
        }
        standardTile("cyan", "device.colorName", height: 2, width: 2, decoration: "flat", inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"cyan", action:"cyan", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8", defaultState: true
            state "cyan", label:"cyan", action:"off", icon:"st.illuminance.illuminance.bright", backgroundColor:"#00FFFF"
        }
        standardTile("magenta", "device.colorName", height: 2, width: 2, decoration: "flat", inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"magenta", action:"magenta", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8", defaultState: true
            state "magenta", label:"magenta", action:"off", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FF0040"
        }
        standardTile("orange", "device.colorName", height: 2, width: 2, decoration: "flat", inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"orange", action:"orange", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8", defaultState: true
            state "orange", label:"orange", action:"off", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FF6600"
        }
        standardTile("purple", "device.colorName", height: 2, width: 2, decoration: "flat", inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"purple", action:"purple", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8", defaultState: true
            state "purple", label:"purple", action:"off", icon:"st.illuminance.illuminance.bright", backgroundColor:"#BF00FF"
        }
        standardTile("yellow", "device.colorName", height: 2, width: 2, decoration: "flat", inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"yellow", action:"yellow", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8", defaultState: true
            state "yellow", label:"yellow", action:"off", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FFFF00"
        }
        standardTile("pink", "device.colorName", height: 2, width: 2, decoration: "flat", inactiveLabel: false, canChangeIcon: false) {
            state "off", label:"pink", action:"pink", icon:"st.illuminance.illuminance.dark", backgroundColor:"#D8D8D8", defaultState: true
            state "pink", label:"pink", action:"off", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FF33CB"
        }

        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
            state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
        }
        standardTile("test", "device.switch", decoration: "flat", width: 2, height: 2) {
            state "default", label:'Test', action:"test"
        }

        // Tile layouts:
        // ******** EDIT THIS SECTION to show the Tiles you want ********
        main(["switch"])
        details([
            // The main multitile:
            "switch",

            // RGBW Channels:
            "switchRed","levelRedSlider", "levelRedTile",
            "switchGreen","levelGreenSlider", "levelGreenTile",
            "switchBlue","levelBlueSlider", "levelBlueTile",
            "switchWhite","levelWhiteSlider", "levelWhiteTile",

            // OUT Channels:
            //"switchCh1","levelCh1Slider", "levelCh1Tile",
            //"switchCh2","levelCh2Slider", "levelCh2Tile",
            //"switchCh3","levelCh3Slider", "levelCh3Tile",
            //"switchCh4","levelCh4Slider", "levelCh4Tile",

            // INPUT Channels (read-only, label replaced slider control):
            //"switchCh1ReadOnly", "ch1Label", "levelCh1Tile",
            //"switchCh2ReadOnly", "ch2Label", "levelCh2Tile",
            //"switchCh3ReadOnly", "ch3Label", "levelCh3Tile",
            //"switchCh4ReadOnly", "ch4Label", "levelCh4Tile",

            // Energy and Power:
            "powerLabel", "power", "refresh", "lastReset", "energy",

            // Built-in Program Shortcuts (these only work if the channels are RGBW):
            "fireplace", "storm", "deepFade","liteFade", "police",

            // Color Shortcut Tiles (these only work if channels are mapped to red/green/blue/white):
            "red","green","blue",
            "orange","yellow","cyan",
            "magenta","pink","purple",

            // The Test Tile:
            //"test"
            ])
    }

    preferences {
        section { // GENERAL:
            input type: "paragraph", element: "paragraph",
                title: "GENERAL:", description: "General settings."

            input name: "configDebugMode", type: "boolean", defaultValue: false, displayDuringSetup: false,
                title: "Enable debug logging?"

        }

        section { // AGGREGATE SWITCH/LEVEL:
            input type: "paragraph", element: "paragraph",
                title: "AGGREGATE SWITCH/LEVEL:", description: "These settings control how the device's 'switch' and 'level' attributes are calculated."

            input name: "configAggregateSwitchMode", type: "enum", defaultValue: "OUT", required: true, displayDuringSetup: false,
                title: "Calaculate Aggregate 'switch' value from:\n[Default: RBGW/OUT Channels Only]",
                options: ["OUT" : "RBGW/OUT Channels Only",
                          "IN" : "IN Channels Only",
                          "ALL" : "All Channels"]

            input name: "configAggregateLevelMode", type: "enum", defaultValue: "OUT", required: true, displayDuringSetup: false,
                title: "Calaculate Aggregate 'level' value from:\n[Default: RBGW/OUT Channels Only]",
                options: ["OUT" : "RBGW/OUT Channels Only",
                          "IN" : "IN Channels Only",
                          "ALL" : "All Channels"]

            input name: "configLevelSetMode", type: "enum", defaultValue: "SCALE", required: true, displayDuringSetup: false,
                title: "LEVEL SET Mode:\n[Default: SCALE]",
                options: ["SCALE" : "SCALE individual channel levels",
                          "SIMPLE" : "SIMPLE: Set all channels to new level"]

        }

        section { // CHANNEL MAPPING & THRESHOLDS:
            input type: "paragraph", element: "paragraph",
                title: "CHANNEL MAPPING & THRESHOLDS:", description: "Define how the physical channels map to colours.\n" +
                       "Thresholds define the level at which a channel is considered ON, which can be used to translate an analog input to a binary value."

            input name: "configCh1Mapping", type: "enum", defaultValue: "Red", required: true, displayDuringSetup: false,
                title: "Channel #1: Maps to:",
                options: ["Red" : "Red",
                          "Green" : "Green",
                          "Blue" : "Blue",
                          "White" : "White",
                          "Other" : "Other",
                          "Input" : "Input"]

            input name: "configCh1Threshold", type: "number", range: "0..100", defaultValue: "1", required: true, displayDuringSetup: false,
                title: "Channel #1: Threshold for ON (%):"

            input name: "configCh2Mapping", type: "enum", defaultValue: "Green", required: true, displayDuringSetup: false,
                title: "Channel #2: Maps to:",
                options: ["Red" : "Red",
                          "Green" : "Green",
                          "Blue" : "Blue",
                          "White" : "White",
                          "Other" : "Other",
                          "Input" : "Input"]

            input name: "configCh2Threshold", type: "number", range: "0..100", defaultValue: "1", required: true, displayDuringSetup: false,
                title: "Channel #2: Threshold for ON (%):"

            input name: "configCh3Mapping", type: "enum", defaultValue: "Blue", required: true, displayDuringSetup: false,
                title: "Channel #3: Maps to:",
                options: ["Red" : "Red",
                          "Green" : "Green",
                          "Blue" : "Blue",
                          "White" : "White",
                          "Other" : "Other",
                          "Input" : "Input"]

            input name: "configCh3Threshold", type: "number", range: "0..100", defaultValue: "1", required: true, displayDuringSetup: false,
                title: "Channel #3: Threshold for ON (%):"

            input name: "configCh4Mapping", type: "enum", defaultValue: "White", required: true, displayDuringSetup: false,
                title: "Channel #4: Maps to:",
                options: ["Red" : "Red",
                          "Green" : "Green",
                          "Blue" : "Blue",
                          "White" : "White",
                          "Other" : "Other",
                          "Input" : "Input"]

            input name: "configCh4Threshold", type: "number", range: "0..100", defaultValue: "1", required: true, displayDuringSetup: false,
                title: "Channel #4: Threshold for ON (%):"

        }

        section { // PHYSICAL DEVICE PARAMETERS:
            input type: "paragraph", element: "paragraph",
                title: "PHYSICAL DEVICE PARAMETERS:", description: "Refer to the Fibaro manual for a full description of the device parameters."

            input name: "configParam01", type: "enum", defaultValue: "255", required: true, displayDuringSetup: false,
                title: "#1: ALL ON/ALL OFF function:\n[Default: 255]",
                options: ["0" : "0: ALL ON inactive, ALL OFF inactive",
                          "1" : "1: ALL ON inactive, ALL OFF active",
                          "2" : "2: ALL ON active, ALL OFF inactive",
                          "255" : "255: ALL ON active, ALL OFF active"]

            input name: "configParam06", type: "enum", defaultValue: "0", required: true, displayDuringSetup: false,
                title: "#6: Associations command class:\n[Default: 0]",
                options: ["0" : "0: NORMAL (DIMMER) - BASIC SET/SWITCH_MULTILEVEL_START/STOP",
                          "1" : "1: NORMAL (RGBW) - COLOR_CONTROL_SET/START/STOP_STATE_CHANGE",
                          "2" : "2: NORMAL (RGBW) - COLOR_CONTROL_SET",
                          "3" : "3: BRIGHTNESS - BASIC SET/SWITCH_MULTILEVEL_START/STOP",
                          "4" : "4: RAINBOW (RGBW) - COLOR_CONTROL_SET"]

            input name: "configParam08", type: "enum", defaultValue: "0", required: true, displayDuringSetup: false,
                title: "#8: IN/OUT: Outputs state change mode:\n[Default: 0: MODE1]",
                options: ["0" : "0: MODE1",
                          "1" : "1: MODE2"]

            input name: "configParam09", type: "number", range: "1..255", defaultValue: "1", required: true, displayDuringSetup: false,
                title: "#9: MODE1: Step value:\n[Default: 1]"

            input name: "configParam10", type: "number", range: "0..60000", defaultValue: "10", required: true, displayDuringSetup: false,
                title: "#10: MODE1: Time between steps:\n[Default: 10ms]\n" +
                       " - 0: immediate change"

            input name: "configParam11", type: "number", range: "0..255", defaultValue: "67", required: true, displayDuringSetup: false,
                title: "#11: MODE2: Time for changing from start to end value:\n" +
                       "[Default: 67 = 3s]\n" +
                       " - 0: immediate change\n" +
                       " - 1-63: 20-126- [ms] value*20ms\n" +
                       " - 65-127: 1-63 [s] [value-64]*1s\n" +
                       " - 129-191: 10-630[s] [value-128]*10s\n" +
                       " - 193-255: 1-63[min] [value-192]*1min"

            input name: "configParam12", type: "number", range: "3..255", defaultValue: "255", required: true, displayDuringSetup: false,
                title: "#12: Maximum brightening level:\n[Default: 255]"

            input name: "configParam13", type: "number", range: "0..254", defaultValue: "2", required: true, displayDuringSetup: false,
                title: "#13: Minimum dim level:\n[Default: 2]"

            input type: "paragraph", element: "paragraph",
                title: "#14: IN/OUT Channel settings: ", description: "If RGBW mode is chosen, settings for all 4 channels must be identical."

            input name: "configParam14_1", type: "enum", defaultValue: "1", required: true, displayDuringSetup: false,
                title: "CHANNEL 1:\n[Default: 1: RGBW - MOMENTARY (NORMAL MODE)]",
                options: ["1" : "1: RGBW - MOMENTARY (NORMAL MODE)",
                          "2" : "2: RGBW - MOMENTARY (BRIGHTNESS MODE)",
                          "3" : "3: RGBW - MOMENTARY (RAINBOW MODE)",
                          "4" : "4: RGBW - TOGGLE (NORMAL MODE)",
                          "5" : "5: RGBW - TOGGLE (BRIGHTNESS MODE)",
                          "6" : "6: RGBW - TOGGLE W. MEMORY (NORMAL MODE)",
                          "7" : "7: RGBW - TOGGLE W. MEMORY (BRIGHTNESS MODE)",
                          "8" : "8: IN - ANALOG 0-10V (SENSOR)",
                          "9" : "9: OUT - MOMENTARY (NORMAL MODE)",
                          "12" : "12: OUT - TOGGLE (NORMAL MODE)",
                          "14" : "14: OUT - TOGGLE W. MEMORY (NORMAL MODE)"]

            input name: "configParam14_2", type: "enum", defaultValue: "1", required: true, displayDuringSetup: false,
                title: "CHANNEL 2:\n[Default: 1: RGBW - MOMENTARY (NORMAL MODE)]",
                options: ["1" : "1: RGBW - MOMENTARY (NORMAL MODE)",
                          "2" : "2: RGBW - MOMENTARY (BRIGHTNESS MODE)",
                          "3" : "3: RGBW - MOMENTARY (RAINBOW MODE)",
                          "4" : "4: RGBW - TOGGLE (NORMAL MODE)",
                          "5" : "5: RGBW - TOGGLE (BRIGHTNESS MODE)",
                          "6" : "6: RGBW - TOGGLE W. MEMORY (NORMAL MODE)",
                          "7" : "7: RGBW - TOGGLE W. MEMORY (BRIGHTNESS MODE)",
                          "8" : "8: IN - ANALOG 0-10V (SENSOR)",
                          "9" : "9: OUT - MOMENTARY (NORMAL MODE)",
                          "12" : "12: OUT - TOGGLE (NORMAL MODE)",
                          "14" : "14: OUT - TOGGLE W. MEMORY (NORMAL MODE)"]

            input name: "configParam14_3", type: "enum", defaultValue: "1", required: true, displayDuringSetup: false,
                title: "CHANNEL 3:\n[Default: 1: RGBW - MOMENTARY (NORMAL MODE)]",
                options: ["1" : "1: RGBW - MOMENTARY (NORMAL MODE)",
                          "2" : "2: RGBW - MOMENTARY (BRIGHTNESS MODE)",
                          "3" : "3: RGBW - MOMENTARY (RAINBOW MODE)",
                          "4" : "4: RGBW - TOGGLE (NORMAL MODE)",
                          "5" : "5: RGBW - TOGGLE (BRIGHTNESS MODE)",
                          "6" : "6: RGBW - TOGGLE W. MEMORY (NORMAL MODE)",
                          "7" : "7: RGBW - TOGGLE W. MEMORY (BRIGHTNESS MODE)",
                          "8" : "8: IN - ANALOG 0-10V (SENSOR)",
                          "9" : "9: OUT - MOMENTARY (NORMAL MODE)",
                          "12" : "12: OUT - TOGGLE (NORMAL MODE)",
                          "14" : "14: OUT - TOGGLE W. MEMORY (NORMAL MODE)"]

            input name: "configParam14_4", type: "enum", defaultValue: "1", required: true, displayDuringSetup: false,
                title: "CHANNEL 4:\n[Default: 1: RGBW - MOMENTARY (NORMAL MODE)]",
                options: ["1" : "1: RGBW - MOMENTARY (NORMAL MODE)",
                          "2" : "2: RGBW - MOMENTARY (BRIGHTNESS MODE)",
                          "3" : "3: RGBW - MOMENTARY (RAINBOW MODE)",
                          "4" : "4: RGBW - TOGGLE (NORMAL MODE)",
                          "5" : "5: RGBW - TOGGLE (BRIGHTNESS MODE)",
                          "6" : "6: RGBW - TOGGLE W. MEMORY (NORMAL MODE)",
                          "7" : "7: RGBW - TOGGLE W. MEMORY (BRIGHTNESS MODE)",
                          "8" : "8: IN - ANALOG 0-10V (SENSOR)",
                          "9" : "9: OUT - MOMENTARY (NORMAL MODE)",
                          "12" : "12: OUT - TOGGLE (NORMAL MODE)",
                          "14" : "14: OUT - TOGGLE W. MEMORY (NORMAL MODE)"]

            input name: "configParam16", type: "enum", defaultValue: "1", required: true, displayDuringSetup: false,
                title: "#16: Memorise device status at power cut:\n[Default: 1: MEMORISE STATUS]",
                options: ["0" : "0: DO NOT MEMORISE STATUS",
                          "1" : "1: MEMORISE STATUS"]

            input name: "configParam30", type: "enum", defaultValue: "0", required: true, displayDuringSetup: false,
                title: "#30: Response to ALARM of any type:\n[Default: 0: INACTIVE]",
                options: ["0" : "0: INACTIVE - Device doesn't respond",
                          "1" : "1: ALARM ON - Device turns on when alarm is detected",
                          "2" : "2: ALARM OFF - Device turns off when alarm is detected",
                          "3" : "3: ALARM PROGRAM - Alarm sequence turns on (Parameter #38)"]

            input name: "configParam38", type: "number", range: "1..10", defaultValue: "10", required: true, displayDuringSetup: false,
                title: "#38: Alarm sequence program:\n[Default: 10]"

            input name: "configParam39", type: "number", range: "1..65534", defaultValue: "600", required: true, displayDuringSetup: false,
                title: "#39: Active PROGRAM alarm time:\n[Default: 600s]"

            input name: "configParam42", type: "enum", defaultValue: "0", required: true, displayDuringSetup: false,
                title: "#42: Command class reporting outputs status change:\n[Default: 0]",
                options: ["0" : "0: Reporting as a result of inputs and controllers actions (SWITCHMULTILEVEL)",
                          "1" : "1: Reporting as a result of input actions (SWITCH MULTILEVEL)",
                          "2" : "2: Reporting as a result of input actions (COLOR CONTROL)"]

            input name: "configParam43", type: "number", range: "1..100", defaultValue: "5", required: true, displayDuringSetup: false,
                title: "#43: Reporting 0-10v analog inputs change threshold:\n[Default: 5 = 0.5V]"

            input name: "configParam44", type: "number", range: "0..65534", defaultValue: "30", required: true, displayDuringSetup: false,
                title: "#44: Power load reporting frequency:\n[Default: 30s]\n" +
                       " - 0: reports are not sent\n" +
                       " - 1-65534: time between reports (s)"

            input name: "configParam45", type: "number", range: "0..254", defaultValue: "10", required: true, displayDuringSetup: false,
                title: "#45: Reporting changes in energy:\n[Default: 10 = 0.1kWh]\n" +
                       " - 0: reports are not sent\n" +
                       " - 1-254: 0.01kWh - 2.54kWh"

            input name: "configParam71", type: "enum", defaultValue: "1", required: true, displayDuringSetup: false,
                title: "#71: Response to BRIGHTNESS set to 0%:\n[Default: 1]",
                options: ["0" : "0: Illumination colour set to white",
                          "1" : "1: Last set colour is memorised"]

            input name: "configParam72", type: "number", range: "1..10", defaultValue: "1", required: true, displayDuringSetup: false,
                title: "#72: Start predefined (RGBW) program:\n[Default: 1]\n" +
                       " - 1-10: animation program number"

            input name: "configParam73", type: "enum", defaultValue: "0", required: true, displayDuringSetup: false,
                title: "#73: Triple click action:\n[Default: 0]",
                options: ["0" : "0: NODE INFO control frame is sent",
                          "1" : "1: Start favourite program"]

        }

        section { // ASSOCIATION GROUPS:
            input type: "paragraph", element: "paragraph",
                title: "ASSOCIATION GROUPS:", description: "Enter a comma-delimited list of node IDs for each association group.\n" +
                    "Node IDs must be in decimal format (E.g.: 27,155, ... ).\n" +
                    "Each group allows a maximum of five devices.\n"

            input name: "configAssocGroup01", type: "text", defaultValue: "", displayDuringSetup: false,
                title: "Association Group #1:"

            input name: "configAssocGroup02", type: "text", defaultValue: "", displayDuringSetup: false,
                title: "Association Group #2:"

            input name: "configAssocGroup03", type: "text", defaultValue: "", displayDuringSetup: false,
                title: "Association Group #3:"

            input name: "configAssocGroup04", type: "text", defaultValue: "", displayDuringSetup: false,
                title: "Association Group #4:"

        }

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
 *  String      description         - The message from the device.
 **/
def parse(description) {
    if (state.debug) log.trace "${device.displayName}: parse(): Parsing raw message: ${description}"

    def result = null
    if (description != "updated") {
        def cmd = zwave.parse(description, getSupportedCommands())
        if (cmd) {
            result = zwaveEvent(cmd)
        } else {
            log.error "${device.displayName}: parse(): Could not parse raw message: ${description}"
        }
    }
    return result
}

/**
 *  COMMAND_CLASS_BASIC (0x20) : BasicReport [IGNORED]
 *
 *  Short   value   0xFF for on, 0x00 for off
 **/
def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd) {
    if (state.debug) log.trace "${device.displayName}: zwaveEvent(): BasicReport received: ${cmd}"
    // BasicReports are ignored as the aggregate switch and level attributes are calculated seperately.
}

/**
 *  COMMAND_CLASS_SWITCH_MULTILEVEL (0x26) : SwitchMultilevelReport
 *
 *  SwitchMultilevelReports tell us the current level of a channel.
 *
 *  These reports will arrive via a MultiChannelCmdEncap command, the zwaveEvent(...MultiChannelCmdEncap) handler
 *  will add the correct sourceEndPoint, before passing to this event handler.
 *
 *  Fibaro RGBW SwitchMultilevelReports have value in range [0..99], so this is scaled to 255 and passed to
 *  zwaveEndPointEvent().
 *
 *  Short       value       0x00 for off, other values are level (on).
 **/
def zwaveEvent(physicalgraph.zwave.commands.switchmultilevelv2.SwitchMultilevelReport cmd, sourceEndPoint = 0) {
    if (state.debug) log.trace "${device.displayName}: zwaveEvent(): SwitchMultilevelReport received from endPoint ${sourceEndPoint}: ${cmd}"
    return zwaveEndPointEvent(sourceEndPoint, Math.round(cmd.value * 255/99))
}

/**
 *  COMMAND_CLASS_SWITCH_ALL (0x27) : * [IGNORED]
 *
 *  SwitchAll functionality is controlled and reported via device Parameter #1 instead.
 **/

/**
 *  COMMAND_CLASS_SENSOR_MULTILEVEL (0x31) : SensorMultilevelReport
 *
 *  Appears to be used to report power. Not sure if anything else...?
 **/
def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv2.SensorMultilevelReport cmd) {
    if (state.debug) log.trace "${device.displayName}: zwaveEvent(): SensorMultilevelReport received: ${cmd}"

    if ( cmd.sensorType == 4 ) { // Instantaneous Power (Watts):
        log.info "${device.displayName}: Power is ${cmd.scaledSensorValue} W"
        return createEvent(name: "power", value: cmd.scaledSensorValue, unit: "W")
    }
    else {
        log.warn "${device.displayName}: zwaveEvent(): SensorMultilevelReport with unhandled sensorType: ${cmd}"
    }
}

/**
 *  COMMAND_CLASS_METER_V3 (0x32) : MeterReport
 *
 *  The Fibaro RGBW Controller supports scale 0 (energy), and 2 (power) only.
 *
 *  Integer         deltaTime                   Time in seconds since last report
 *  Short           meterType                   Unknown = 0, Electric = 1, Gas = 2, Water = 3
 *  List<Short>     meterValue                  Meter value as an array of bytes
 *  Double          scaledMeterValue            Meter value as a double
 *  List<Short>     previousMeterValue          Previous meter value as an array of bytes
 *  Double          scaledPreviousMeterValue    Previous meter value as a double
 *  Short           size                        The size of the array for the meterValue and previousMeterValue
 *  Short           scale                       The scale of the values: "kWh"=0, "kVAh"=1, "Watts"=2, "pulses"=3,
 *                                              "Volts"=4, "Amps"=5, "Power Factor"=6, "Unknown"=7
 *  Short           precision                   The decimal precision of the values
 *  Short           rateType                    ???
 *  Boolean         scale2                      ???
 **/
def zwaveEvent(physicalgraph.zwave.commands.meterv3.MeterReport cmd) {
    if (state.debug) log.trace "${device.displayName}: zwaveEvent(): MeterReport received: ${cmd}"

    if (cmd.scale == 0) { // Accumulated Energy (kWh):
        state.energy = cmd.scaledMeterValue
        //sendEvent(name: "dispEnergy", value: String.format("%.2f",cmd.scaledMeterValue as BigDecimal) + " kWh", displayed: false)
        log.info "${device.displayName}: Accumulated energy is ${cmd.scaledMeterValue} kWh"
        return createEvent(name: "energy", value: cmd.scaledMeterValue, unit: "kWh")
    }
    else if (cmd.scale == 1) { // Accumulated Energy (kVAh): Ignore.
        //createEvent(name: "energy", value: cmd.scaledMeterValue, unit: "kVAh")
    }
    else if (cmd.scale == 2) { // Instantaneous Power (Watts):
        //sendEvent(name: "dispPower", value: String.format("%.1f",cmd.scaledMeterValue as BigDecimal) + " W", displayed: false)
        log.info "${device.displayName}: Power is ${cmd.scaledMeterValue} W"
        return createEvent(name: "power", value: cmd.scaledMeterValue, unit: "W")
    }
    else if (cmd.scale == 4) { // Instantaneous Voltage (Volts):
        //sendEvent(name: "dispVoltage", value: String.format("%.1f",cmd.scaledMeterValue as BigDecimal) + " V", displayed: false)
        log.info "${device.displayName}: Voltage is ${cmd.scaledMeterValue} V"
        return createEvent(name: "voltage", value: cmd.scaledMeterValue, unit: "V")
    }
    else if (cmd.scale == 5) {  // Instantaneous Current (Amps):
        //sendEvent(name: "dispCurrent", value: String.format("%.1f",cmd.scaledMeterValue as BigDecimal) + " A", displayed: false)
        log.info "${device.displayName}: Current is ${cmd.scaledMeterValue} A"
        return createEvent(name: "current", value: cmd.scaledMeterValue, unit: "A")
    }
    else if (cmd.scale == 6) { // Instantaneous Power Factor:
        //sendEvent(name: "dispPowerFactor", value: "PF: " + String.format("%.2f",cmd.scaledMeterValue as BigDecimal), displayed: false)
        log.info "${device.displayName}: PowerFactor is ${cmd.scaledMeterValue}"
        return createEvent(name: "powerFactor", value: cmd.scaledMeterValue, unit: "PF")
    }
}

/**
 *  COMMAND_CLASS_SWITCH_COLOR (0x33) : SwitchColorReport
 *
 *  SwitchColorReports tell us the current level of a color channel.
 *  The value will be in the range 0..255, which is passed to zwaveEndPointEvent().
 *
 *  String      colorComponent                  Color name, e.g. "red", "green", "blue".
 *  Short       colorComponentId                0 = warmWhite, 2 = red, 3 = green, 4 = blue, 5 = coldWhite.
 *  Short       value                           0x00 to 0xFF
 **/
def zwaveEvent(physicalgraph.zwave.commands.switchcolorv3.SwitchColorReport cmd) {
    if (state.debug) log.trace "${device.displayName}: zwaveEvent(): SwitchColorReport received: ${cmd}"
    if (cmd.colorComponentId == 0) { cmd.colorComponentId = 5 } // Remap warmWhite colorComponentId
    return zwaveEndPointEvent(cmd.colorComponentId, cmd.value)
}

/**
 *  COMMAND_CLASS_MULTICHANNEL (0x60) : MultiChannelCmdEncap
 *
 *  The MultiChannel Command Class is used to address one or more endpoints in a multi-channel device.
 *  The sourceEndPoint attribute will identify the sub-device/channel the command relates to.
 *  The encpsulated command is extracted and passed to the appropriate zwaveEvent handler.
 **/
def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd) {
    if (state.debug) log.trace "${device.displayName}: zwaveEvent(): MultiChannelCmdEncap received: ${cmd}"

    def encapsulatedCommand = cmd.encapsulatedCommand(getSupportedCommands())
    if (!encapsulatedCommand) {
        log.warn "${device.displayName}: zwaveEvent(): MultiChannelCmdEncap from endPoint ${cmd.sourceEndPoint} could not be translated: ${cmd}"
    } else {
        return zwaveEvent(encapsulatedCommand, cmd.sourceEndPoint)
    }
}

/**
 *  COMMAND_CLASS_CONFIGURATION (0x70) : ConfigurationReport
 *
 *  Configuration reports tell us the current parameter values stored in the physical device.
 *
 *  Due to platform security restrictions, the relevent preference value cannot be updated with the actual
 *  value from the device, instead all we can do is output to the SmartThings IDE Log for verification.
 **/
def zwaveEvent(physicalgraph.zwave.commands.configurationv2.ConfigurationReport cmd) {
    if (state.debug) log.trace "${device.displayName}: zwaveEvent(): ConfigurationReport received: ${cmd}"
    // Translate cmd.configurationValue to an int. This should be returned from zwave.parse() as
    // cmd.scaledConfigurationValue, but it hasn't been implemented by SmartThings yet! :/
    //  See: https://community.smartthings.com/t/zwave-configurationv2-configurationreport-dev-question/9771
    def scaledConfigurationValue = byteArrayToInt(cmd.configurationValue)
    log.info "${device.displayName}: Parameter #${cmd.parameterNumber} has value: ${cmd.configurationValue} (${scaledConfigurationValue})"
}

/**
 *  COMMAND_CLASS_MANUFACTURER_SPECIFIC (0x72) : ManufacturerSpecificReport
 *
 *  ManufacturerSpecific reports tell us the device's manufacturer ID and product ID.
 **/
def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {
    if (state.debug) log.trace "${device.displayName}: zwaveEvent(): ManufacturerSpecificReport received: ${cmd}"
    updateDataValue("manufacturerName","${cmd.manufacturerName}")
    updateDataValue("manufacturerId","${cmd.manufacturerId}")
    updateDataValue("productId","${cmd.productId}")
    updateDataValue("productTypeId","${cmd.productTypeId}")
}


/**
 *  COMMAND_CLASS_ASSOCIATION (0x85) : AssociationReport
 *
 *  AssociationReports tell the nodes in an association group.
 *  Due to platform security restrictions, the relevent preference value cannot be updated with the actual
 *  value from the device, instead all we can do is output to the SmartThings IDE Log for verification.
 *
 *  Example: AssociationReport(groupingIdentifier: 4, maxNodesSupported: 5, nodeId: [1], reportsToFollow: 0)
 **/
def zwaveEvent(physicalgraph.zwave.commands.associationv2.AssociationReport cmd) {
    if (state.debug) log.trace "${device.displayName}: zwaveEvent(): AssociationReport received: ${cmd}"
    log.info "${device.displayName}: Association Group ${cmd.groupingIdentifier} contains nodes: ${cmd.nodeId}"
}

/**
 *  COMMAND_CLASS_VERSION (0x86) : VersionReport
 *
 *  Version reports tell us the device's Z-Wave framework and firmware versions.
 **/
def zwaveEvent(physicalgraph.zwave.commands.versionv1.VersionReport cmd) {
    if (state.debug) log.trace "${device.displayName}: zwaveEvent(): VersionReport received: ${cmd}"
    updateDataValue("applicationVersion","${cmd.applicationVersion}")
    updateDataValue("applicationSubVersion","${cmd.applicationSubVersion}")
    updateDataValue("zWaveLibraryType","${cmd.zWaveLibraryType}")
    updateDataValue("zWaveProtocolVersion","${cmd.zWaveProtocolVersion}")
    updateDataValue("zWaveProtocolSubVersion","${cmd.zWaveProtocolSubVersion}")
}

/**
 *  COMMAND_CLASS_FIRMWARE_UPDATE_MD (0x7A) : FirmwareMdReport
 *
 *  Firmware Meta Data reports tell us the device's firmware version and manufacturer ID.
 **/
def zwaveEvent(physicalgraph.zwave.commands.firmwareupdatemdv2.FirmwareMdReport cmd) {
    if (state.debug) log.trace "${device.displayName}: zwaveEvent(): FirmwareMdReport received: ${cmd}"
    updateDataValue("firmwareChecksum","${cmd.checksum}")
    updateDataValue("firmwareId","${cmd.firmwareId}")
    updateDataValue("manufacturerId","${cmd.manufacturerId}")
}

/**
 *  Default zwaveEvent handler.
 *
 *  Called for all Z-Wave events that aren't handled above.
 **/
def zwaveEvent(physicalgraph.zwave.Command cmd) {
    log.error "${device.displayName}: zwaveEvent(): No handler for command: ${cmd}"
    log.error "${device.displayName}: zwaveEvent(): Class is: ${cmd.getClass()}" // This causes an error, but still gives us the class in the error message. LOL!
}


/**********************************************************************
 *  SmartThings Platform Commands:
 **********************************************************************/

/**
 *  installed() - Runs when the device is first installed.
 **/
def installed() {
    log.trace "installed()"

    state.debug = true
    state.installedAt = now()
    state.lastReset = new Date().format("YYYY/MM/dd \n HH:mm:ss", location.timeZone)
    state.channelMapping = [null, "Red", "Green", "Blue", "White"]
    state.channelThresholds = [null,1,1,1,1]
    state.channelModes = [null,1,1,1,1]

    // Initialise attributes:
    sendEvent(name: "switch", value: "off", displayed: false)
    sendEvent(name: "level", value: 0, unit: "%", displayed: false)
    sendEvent(name: "hue", value: 0, unit: "%", displayed: false)
    sendEvent(name: "saturation", value: 0, unit: "%", displayed: false)
    sendEvent(name: "colorName", value: "custom", displayed: false)
    sendEvent(name: "color", value: "[]", displayed: false)
    sendEvent(name: "activeProgram", value: 0, displayed: false)
    sendEvent(name: "energy", value: 0, unit: "kWh", displayed: false)
    sendEvent(name: "power", value: 0, unit: "W", displayed: false)
    sendEvent(name: "lastReset", value: state.lastReset, displayed: false)

    (1..4).each { channel ->
        sendEvent(name: "switchCh${channel}", value: "off", displayed: false)
        sendEvent(name: "levelCh${channel}", value: 0, unit: "%", displayed: false)
    }

    ["Red", "Green", "Blue", "White"].each { mapping ->
        sendEvent(name: "switchCh${mapping}", value: "off", displayed: false)
        sendEvent(name: "levelCh${mapping}", value: 0, unit: "%", displayed: false)
    }

    state.isInstalled = true
}

/**
 *  updated() - Runs after device settings have been changed in the SmartThings GUI (and/or IDE?).
 **/
def updated() {
    if ("true" == configDebugMode) log.trace "${device.displayName}: updated()"

    if (!state.updatedLastRanAt || now() >= state.updatedLastRanAt + 2000) {
        state.updatedLastRanAt = now()

        // Make sure installation has completed:
        if (!state.isInstalled) { installed() }

        state.debug = ("true" == configDebugMode)

        // Convert channel mappings to a map:
        def cMapping = []
        cMapping[1] = configCh1Mapping
        cMapping[2] = configCh2Mapping
        cMapping[3] = configCh3Mapping
        cMapping[4] = configCh4Mapping
        state.channelMapping = cMapping

        // Convert channel thresholds to a map:
        def cThresholds = []
        cThresholds[1] = configCh1Threshold.toInteger()
        cThresholds[2] = configCh2Threshold.toInteger()
        cThresholds[3] = configCh3Threshold.toInteger()
        cThresholds[4] = configCh4Threshold.toInteger()
        state.channelThresholds = cThresholds

        // Convert channel modes to a map:
        def cModes = []
        cModes[1] = configParam14_1.toInteger()
        cModes[2] = configParam14_2.toInteger()
        cModes[3] = configParam14_3.toInteger()
        cModes[4] = configParam14_4.toInteger()
        state.channelModes = cModes

        // Validate Paramter #14 settings:
        state.isRGBW = ( state.channelModes[1] < 8 ) || ( state.channelModes[2] < 8 ) || ( state.channelModes[3] < 8 ) || ( state.channelModes[4] < 8 )
        state.isIN   = ( state.channelModes[1] == 8 ) || ( state.channelModes[2] == 8 ) || ( state.channelModes[3] == 8 ) || ( state.channelModes[4] == 8 )
        state.isOUT  = ( state.channelModes[1] > 8 ) || ( state.channelModes[2] > 8 ) || ( state.channelModes[3] > 8 ) || ( state.channelModes[4] > 8 )
        if ( state.isRGBW & ( (state.channelModes[1] != state.channelModes[2]) || (state.channelModes[1] != state.channelModes[3]) || (state.channelModes[1] != state.channelModes[4]) ) ) {
            log.warn "${device.displayName}: updated(): Invalid combination of RGBW channels detected. All RGBW channels should be identical. You may get weird behaviour!"
        }
        if ( state.isRGBW & ( state.isIN || state.isOUT ) ) log.warn "${device.displayName}: updated(): Invalid combination of RGBW and IN/OUT channels detected. You may get weird behaviour!"

        // Call configure() and refresh():
        return response( [ configure() + refresh() ])
    }
    else {
        log.debug "updated(): Ran within last 2 seconds so aborting."
    }
}

/**
 *  configure() - Configure physical device parameters.
 *
 *  Uses values from device preferences.
 **/
def configure() {
    if (state.debug) log.trace "${device.displayName}: configure()"

    def cmds = []

    // Note: Parameters #10,#14,#39,#44 have size: 2!
    // can't use scaledConfigurationValue to set parameters with size < 1 as there is a bug in the configurationV1.configurationSet class.
    //  See: https://community.smartthings.com/t/zwave-configurationv2-configurationreport-dev-question/9771
    // Instead, must use intToUnsignedByteArray(number,size) to convert to an unsigned byteArray manually.
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 1, size: 1, configurationValue: [configParam01.toInteger()]).format()
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 6, size: 1, configurationValue: [configParam06.toInteger()]).format()
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 8, size: 1, configurationValue: [configParam08.toInteger()]).format()
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 9, size: 1, configurationValue: [configParam09.toInteger()]).format()
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 10, size: 2, configurationValue: intToUnsignedByteArray(configParam10.toInteger(),2)).format()
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 11, size: 1, configurationValue: [configParam11.toInteger()]).format()
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 12, size: 1, configurationValue: [configParam12.toInteger()]).format()
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 13, size: 1, configurationValue: [configParam13.toInteger()]).format()
    //  Parameter #14 needs to be reconstituted from each 4-bit channel value.
    def p14A = (configParam14_1.toInteger() * 0x10) + configParam14_2.toInteger()
    def p14B = (configParam14_3.toInteger() * 0x10) + configParam14_4.toInteger()
    if (state.debug) log.debug "${device.displayName}: configure(): Setting Parameter #14 to: [${p14A},${p14B}]"
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 14, size: 2, configurationValue: [p14A.toInteger(), p14B.toInteger()]).format()
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 16, size: 1, configurationValue: [configParam16.toInteger()]).format()
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 30, size: 1, configurationValue: [configParam30.toInteger()]).format()
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 38, size: 1, configurationValue: [configParam38.toInteger()]).format()
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 39, size: 2, configurationValue: intToUnsignedByteArray(configParam39.toInteger(),2)).format()
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 42, size: 1, configurationValue: [configParam42.toInteger()]).format()
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 43, size: 1, configurationValue: [configParam43.toInteger()]).format()
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 44, size: 2, configurationValue: intToUnsignedByteArray(configParam44.toInteger(),2)).format()
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 45, size: 1, configurationValue: [configParam45.toInteger()]).format()
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 71, size: 1, configurationValue: [configParam71.toInteger()]).format()
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 72, size: 1, configurationValue: [configParam72.toInteger()]).format()
    cmds << zwave.configurationV2.configurationSet(parameterNumber: 73, size: 1, configurationValue: [configParam73.toInteger()]).format()

    // Association Groups:
    cmds << zwave.associationV2.associationRemove(groupingIdentifier: 1, nodeId: []).format()
    cmds << zwave.associationV2.associationSet(groupingIdentifier: 1, nodeId: parseAssocGroup(configAssocGroup01,5)).format()
    cmds << zwave.associationV2.associationRemove(groupingIdentifier: 2, nodeId: []).format()
    cmds << zwave.associationV2.associationSet(groupingIdentifier: 2, nodeId: parseAssocGroup(configAssocGroup02,5)).format()
    cmds << zwave.associationV2.associationRemove(groupingIdentifier: 3, nodeId: []).format()
    cmds << zwave.associationV2.associationSet(groupingIdentifier: 3, nodeId: parseAssocGroup(configAssocGroup03,5)).format()
    cmds << zwave.associationV2.associationRemove(groupingIdentifier: 4, nodeId: []).format()
    cmds << zwave.associationV2.associationSet(groupingIdentifier: 4, nodeId: parseAssocGroup(configAssocGroup04,5)).format()
    cmds << zwave.associationV2.associationRemove(groupingIdentifier: 5, nodeId: []).format()
    cmds << zwave.associationV2.associationSet(groupingIdentifier: 5, nodeId: [zwaveHubNodeId]).format() // Add the SmartThings hub (controller) to Association Group #5.

    log.warn "${device.displayName}: configure(): Device Parameters are being updated. It is recommended to power-cycle the Fibaro device once completed."

    return delayBetween(cmds, 500) + getConfigReport()
}


/**********************************************************************
 *  Capability-related Commands:
 **********************************************************************/

 /**
  *  on() - Turn the switch on. [Switch Capability]
  *
  *  Only sends commands to RGBW/OUT channels to avoid altering the levels of INPUT channels.
  **/
 def on() {
     log.info "${device.displayName}: on()"

     def cmds = []
     def newLevel = 0
     def isAnyOn = false

     (1..4).each { channel ->
         // If there is a saved level which is not zero, then apply the saved level:
         newLevel = device.latestValue("savedLevelCh${channel}") ?: -1
         if (newLevel.toInteger() > 0) {
             cmds << setLevelChX(newLevel.toInteger(), channel)
             isAnyOn = true
         }
     }

     if (!isAnyOn) { // However, if none of the channels were turned on, turn them all on.
         (1..4).each { channel ->
             if ( 8 != state.channelModes[channel] ) { cmds << onChX(channel)}
         }
     }

     return cmds
 }

/**
 *  off() - Turn the switch off. [Switch Capability]
 *
 *  Only sends commands to RGBW/OUT channels to avoid altering the levels of INPUT channels.
 **/
def off() {
    log.info "${device.displayName}: off()"

    def cmds = []
    (1..4).each { i ->
        if ( 8 != state.channelModes[i] ) { cmds << offChX(i)}
    }
    return cmds
}

/**
 *  setLevel(level, rate) - Set the (aggregate) level. [Switch Level Capability]
 *
 *  Note: rate is ignored as it is not supported.
 *
 *  Calculation of new channel levels is controlled by configLevelSetMode (see preferences).
 *  Only sends commands to RGBW/OUT channels to avoid altering the levels of INPUT channels.
 **/
def setLevel(level, rate = 1) {
    if (state.debug) log.trace "${device.displayName}: setLevel(): Level: ${level}"
    if (level > 100) level = 100
    if (level < 0) level = 0

    def cmds = []

    if ( "SCALE" == configLevelSetMode ) { // SCALE Mode:
        float currentMaxOutLevel = 0.0
        (1..4).each { i ->
            if ( 8 != state.channelModes[i] ) { currentMaxOutLevel = Math.max(currentMaxOutLevel,device.latestValue("levelCh${i}").toInteger()) }
        }

        if (0.0 == currentMaxOutLevel) { // All OUT levels are currently zero, so just set all to the new level:
            (1..4).each { i ->
                if ( 8 != state.channelModes[i] ) { cmds << setLevelChX(level.toInteger(),i) }
            }
        }
        else { // Scale the individual channel levels:
            float s = level / currentMaxOutLevel
            (1..4).each { i ->
                if ( 8 != state.channelModes[i] ) { cmds << setLevelChX( (device.latestValue("levelCh${i}") * s).toInteger(),i) }
            }
        }
    }
    else { // SIMPLE Mode:
        (1..4).each { i ->
            if ( 8 != state.channelModes[i] ) { cmds << setLevelChX(level.toInteger(),i) }
        }
    }

    return cmds
}

/**
 *  setColor() - Set the color. [Color Control Capability]
 *
 *  Accepts a colorMap with the following key combinations (in order of precedence):
 *   red, green, blue, white
 *   red, green, blue
 *   hex
 *   name
 *   hue, saturation, level
 *   red|green|blue|white      [Will only set values that are specified]
 *   hue|saturation|level      [Will use the device's current value for any missing values]
 *
 *  Obeys the channel color mappings defined in the device's preferences.
 *  If a color channel does not exist it is simply ignored.
 **/
def setColor(Map colorMap) {
    if (state.debug) log.trace "${device.displayName}: setColor(): colorMap: ${colorMap}"

    def cmds = []
    def rgbw = []

    if (colorMap.containsKey("red") & colorMap.containsKey("green") & colorMap.containsKey("blue") & colorMap.containsKey("white")) {
        if (state.debug) log.debug "${device.displayName}: setColor(): Setting color using RGBW values."
        rgbw = colorMap
    }
    if (colorMap.containsKey("red") & colorMap.containsKey("green") & colorMap.containsKey("blue")) {
        if (state.debug) log.debug "${device.displayName}: setColor(): Setting color using RGB values."
        rgbw = rgbToRGBW(colorMap)
    }
    else if (colorMap.containsKey("hex")) {
        if (state.debug) log.debug "${device.displayName}: setColor(): Setting color using hex value."
        rgbw = hexToRGBW(colorMap)
    }
    else if (colorMap.containsKey("name")) {
        if (state.debug) log.debug "${device.displayName}: setColor(): Setting color using name."
        rgbw = nameToRGBW(colorMap)
    }
    else if (colorMap.containsKey("hue") & colorMap.containsKey("saturation") & colorMap.containsKey("level")) {
        if (state.debug) log.debug "${device.displayName}: setColor(): Setting color using HSV values."
        rgbw = hsvToRGBW(colorMap)
    }
    else if (colorMap.containsKey("red") || colorMap.containsKey("green") || colorMap.containsKey("blue") || colorMap.containsKey("white")) {
        if (state.debug) log.debug "${device.displayName}: setColor(): Setting color using partial RGBW values."
        rgbw = colorMap // Don't add any key/values, only those that exist will be set below.
    }
    else if (colorMap.containsKey("hue") || colorMap.containsKey("saturation") || colorMap.containsKey("level")) {
        if (state.debug) log.debug "${device.displayName}: setColor(): Setting color using partial HSV values."
        def h = (colorMap.containsKey("hue")) ? colorMap.hue : device.latestValue("hue").toInteger()
        def s = (colorMap.containsKey("saturation")) ? colorMap.saturation : device.latestValue("saturation").toInteger()
        def l = (colorMap.containsKey("level")) ? colorMap.level : device.latestValue("level").toInteger()
        rgbw = hsvToRGBW([hue: h, saturation: s, level: l])
    }
    else {
        log.error "${device.displayName}: setColor(): Cannot obtain color information from colorMap: ${colorMap}"
    }

    if (rgbw) {
        // Apply channel mappings before sending switchColorSet command:
        def chIndex = [ null, red, green, blue, warmWhite] // These are names of the channels used in switchColorSet.
        def rgbwMapped = [:]
        (1..4).each { i ->
            if ( "Red" == state.channelMapping[i] & rgbw.containsKey("red") ) { rgbwMapped << [(chIndex[i]) : rgbw.red] }
            else if ( "Green" == state.channelMapping[i] & rgbw.containsKey("green") ) { rgbwMapped << [(chIndex[i]) : rgbw.green] }
            else if ( "Blue" == state.channelMapping[i] & rgbw.containsKey("blue") ) { rgbwMapped << [(chIndex[i]) : rgbw.blue] }
            else if ( "White" == state.channelMapping[i] & rgbw.containsKey("white") ) { rgbwMapped << [(chIndex[i]) : rgbw.white] }

            sendEvent(name: "savedLevelCh${i}", value: null) // Wipe savedLevel.
        }
        cmds << zwave.switchColorV3.switchColorSet(rgbwMapped).format()

        // Alternatively, could use switchMultilevelSet commands via setLevel* (but switchColorSet is more efficient):
        //cmds << setLevelRed(Math.round(rgbw.red * 99/255)) // setLevel* uses 99 as max.
        //cmds << setLevelGreen(Math.round(rgbw.green * 99/255))
        //cmds << setLevelBlue(Math.round(rgbw.blue * 99/255))
        //cmds << setLevelWhite(Math.round(rgbw.white * 99/255))

        sendEvent(name: "activeProgram", value: 0) // Wipe activeProgram.

        delayBetween(cmds,200)
    }
}

/**
 *  setHue(percent) - Set the color hue. [Color Control Capability]
 **/
def setHue(percent) {
    if (state.debug) log.trace "${device.displayName}: setHue(): Hue: ${percent}"
    setColor([hue: percent])
}

/**
 *  setSaturation(percent) - Set the color saturation. [Color Control Capability]
 **/
def setSaturation(percent) {
    if (state.debug) log.trace "${device.displayName}: setSaturation(): Saturation: ${percent}"
    setColor([saturation: percent])
}

/**
 *  poll() - Polls the device. [Polling Capability]
 *
 *  The SmartThings platform seems to poll devices randomly every 6-8mins.
 **/
def poll() {
    if (state.debug) log.trace "${device.displayName}: poll()"
    refresh()
}

/**
 *  refresh() - Refreshes values from the physical device. [Refresh Capability]
 **/
def refresh() {
    if (state.debug) log.trace "${device.displayName}: refresh()"
    def cmds = []

    if (state.isIN) { // There are INPUT channels, so we must get channel levels using switchMultilevelGet:
        (2..5).each { cmds << response(zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint: it).encapsulate(zwave.switchMultilevelV2.switchMultilevelGet()).format()) }
    }
    else { // There are no INPUT channels, so we can use switchColorGet for greater accuracy:
        (0..4).each { cmds << response(zwave.switchColorV3.switchColorGet(colorComponentId: it).format()) }
    }

    cmds << response(zwave.meterV3.meterGet(scale: 0).format()) // Get energy MeterReport
    cmds << response(zwave.meterV3.meterGet(scale: 2).format()) // Get power MeterReport
    delayBetween(cmds,200)
}


/**********************************************************************
 *  Custom Commands:
 **********************************************************************/

/**
 *  reset() - Reset Accumulated Energy.
 **/
def reset() {
    if (state.debug) log.trace "${device.displayName}: reset()"

    state.lastReset = new Date().format("YYYY/MM/dd \n HH:mm:ss", location.timeZone)
    sendEvent(name: "lastReset", value: state.lastReset)

    return [
        zwave.meterV3.meterReset().format(),
        zwave.meterV3.meterGet(scale: 0).format()
    ]
}

/**
 *  on*() - Set switch for an individual channel to "on".
 *
 *  These commands all map to onChX().
 **/
def onCh1() { onChX(1) }
def onCh2() { onChX(2) }
def onCh3() { onChX(3) }
def onCh4() { onChX(4) }
def onRed() {
    def cmds = []
    (1..4).each { i -> if ( "Red" == state.channelMapping[i] ) { cmds << onChX(i) } }
    if (cmds.empty) log.warn "${device.displayName}: onRed(): There are no channels mapped to Red!"
    return cmds
}
def onGreen() {
    def cmds = []
    (1..4).each { i -> if ( "Green" == state.channelMapping[i] ) { cmds << onChX(i) } }
    if (cmds.empty) log.warn "${device.displayName}: onGreen(): There are no channels mapped to Green!"
    return cmds
}
def onBlue() {
    def cmds = []
    (1..4).each { i -> if ( "Blue" == state.channelMapping[i] ) { cmds << onChX(i) } }
    if (cmds.empty) log.warn "${device.displayName}: onBlue(): There are no channels mapped to Blue!"
    return cmds
}
def onWhite() {
    def cmds = []
    (1..4).each { i -> if ( "White" == state.channelMapping[i] ) { cmds << onChX(i) } }
    if (cmds.empty) log.warn "${device.displayName}: onWhite(): There are no channels mapped to White!"
    return cmds
}

/**
 *  off*() - Set switch for an individual channel to "off".
 *
 *  These commands all map to offChX().
 **/
def offCh1() { offChX(1) }
def offCh2() { offChX(2) }
def offCh3() { offChX(3) }
def offCh4() { offChX(4) }
def offRed() {
    def cmds = []
    (1..4).each { i -> if ( "Red" == state.channelMapping[i] ) { cmds << offChX(i) } }
    if (cmds.empty) log.warn "${device.displayName}: offRed(): There are no channels mapped to Red!"
    return cmds
}
def offGreen() {
    def cmds = []
    (1..4).each { i -> if ( "Green" == state.channelMapping[i] ) { cmds << offChX(i) } }
    if (cmds.empty) log.warn "${device.displayName}: offGreen(): There are no channels mapped to Green!"
    return cmds
}
def offBlue() {
    def cmds = []
    (1..4).each { i -> if ( "Blue" == state.channelMapping[i] ) { cmds << offChX(i) } }
    if (cmds.empty) log.warn "${device.displayName}: offBlue(): There are no channels mapped to Blue!"
    return cmds
}
def offWhite() {
    def cmds = []
    (1..4).each { i -> if ( "White" == state.channelMapping[i] ) { cmds << offChX(i) } }
    if (cmds.empty) log.warn "${device.displayName}: offWhite(): There are no channels mapped to White!"
    return cmds
}

/**
 *  setLevel*() - Set level of an individual channel.
 *
 *  These commands all map to setLevelChX().
 **/
def setLevelCh1(level) { setLevelChX(level, 1) }
def setLevelCh2(level) { setLevelChX(level, 2) }
def setLevelCh3(level) { setLevelChX(level, 3) }
def setLevelCh4(level) { setLevelChX(level, 4) }
def setLevelRed(level) {
    def cmds = []
    (1..4).each { i -> if ( "Red" == state.channelMapping[i] ) { cmds << setLevelChX(level,i) } }
    if (cmds.empty) log.warn "${device.displayName}: setLevelRed(): There are no channels mapped to Red!"
    return cmds
}
def setLevelGreen(level) {
    def cmds = []
    (1..4).each { i -> if ( "Green" == state.channelMapping[i] ) { cmds << setLevelChX(level,i) } }
    if (cmds.empty) log.warn "${device.displayName}: setLevelGreen(): There are no channels mapped to Green!"
    return cmds
}
def setLevelBlue(level) {
    def cmds = []
    (1..4).each { i -> if ( "Blue" == state.channelMapping[i] ) { cmds << setLevelChX(level,i) } }
    if (cmds.empty) log.warn "${device.displayName}: setLevelBlue(): There are no channels mapped to Blue!"
    return cmds
}
def setLevelWhite(level) {
    def cmds = []
    (1..4).each { i -> if ( "White" == state.channelMapping[i] ) { cmds << setLevelChX(level,i) } }
    if (cmds.empty) log.warn "${device.displayName}: setLevelWhite(): There are no channels mapped to White!"
    return cmds
}

/**
 *  *color*() - Set a colour by name.
 *
 *  These commands all map to setColor().
 **/
def black()     { setColor(name: "black") }
def white()     { setColor(name: "white") }
def red()       { setColor(name: "red") }
def green()     { setColor(name: "green") }
def blue()      { setColor(name: "blue") }
def cyan()      { setColor(name: "cyan") }
def magenta()   { setColor(name: "magenta") }
def orange()    { setColor(name: "orange") }
def purple()    { setColor(name: "purple") }
def yellow()    { setColor(name: "yellow") }
def pink()      { setColor(name: "pink") }
def coldWhite() { setColor(name: "coldWhite") }
def warmWhite() { setColor(name: "warmWhite") }

/**
 *  startProgram(programNumber) - Start a built-in animation program.
 **/
def startProgram(programNumber) {
    if (state.debug) log.trace "${device.displayName}: startProgram(): programNumber: ${programNumber}"

    if (state.isIN | state.isOUT) {
        log.warn "${device.displayName}: Built-in programs work with RGBW channels only, they will not function when using IN/OUT channels!"
    }
    else if (programNumber > 0 & programNumber <= 10) {
        (1..4).each { sendEvent(name: "savedLevelCh${it}", value: device.latestValue("levelCh${it}").toInteger(), displayed: false) } // Save levels for all channels.
        sendEvent(name: "activeProgram", value: programNumber, displayed: false)
        sendEvent(name: "colorName", value: "program")
        return zwave.configurationV1.configurationSet(configurationValue: [programNumber], parameterNumber: 72, size: 1).format()
    }
    else {
        log.warn "${device.displayName}: startProgram(): Invalid programNumber: ${programNumber}"
    }
}

/**
 *  start*() - Start built-in animation program by name.
 **/
def startFireplace() { startProgram(6) }
def startStorm()     { startProgram(7) }
def startDeepFade()  { startProgram(8) }
def startLiteFade()  { startProgram(9) }
def startPolice()    { startProgram(10) }

/**
 *  stopProgram() - Stop animation program (if running).
 **/
def stopProgram() {
    if (state.debug) log.trace "${device.displayName}: startProgram()"

    sendEvent(name: "activeProgram", value: 0, displayed: false)
    return on() // on() will automatically restore levels.
}


/**********************************************************************
 *  Private Helper Methods:
 **********************************************************************/

/**
 *  getSupportedCommands() - Returns a map of the command versions supported by the device.
 *
 *  Used by parse(), and to extract encapsulated commands from MultiChannelCmdEncap,
 *   MultiInstanceCmdEncap, SecurityMessageEncapsulation, and Crc16Encap messages.
 *
 *  The Fibaro RGBW Controller supports the following commmand classes:
 *   All Switch (0x27) : V1
 *   Association (0x85) : V2
 *   Basic (0x20) : V1
 *   Color Control (0x33) : V3
 *   Configuration (0x70) : V2
 *   Firmware Update Meta Data (0x7A) : V2
 *   Manufacturer Specific (0x72) : V2
 *   Meter (0x32) : V3
 *   Multi Channel (0x60) : V3
 *   Multilevel Sensor (0x31) : V2
 *   Switch Multilevel (0x26) : V2
 *   Version (0x86) : V1
 *
 **/
private getSupportedCommands() {
    return [0x20: 1, 0x26: 2, 0x27: 1, 0x31:2, 0x32: 3, 0x33: 3, 0x60: 3, 0x70: 2, 0x72: 2, 0x85: 2, 0x86: 1, 0x7A: 2]
}

/**
 *  byteArrayToInt(byteArray)
 *
 *  Converts an unsigned byte array to a int.
 *  Should use ByteBuffer, but it's not available in SmartThings.
 **/
private byteArrayToInt(byteArray) {
    // return java.nio.ByteBuffer.wrap(byteArray as byte[]).getInt()
    def i = 0
    byteArray.reverse().eachWithIndex { b, ix -> i += b * (0x100 ** ix) }
    return i
}

/**
 *  intToUnsignedByteArray(number, size)
 *
 *  Converts an unsigned int to an unsigned byte array of set size.
 **/
private intToUnsignedByteArray(number, size)  {
    if (number < 0) {
        log.error "${device.displayName}: intToUnsignedByteArray(): Doesn't work with negative number: ${number}"
    }
    else {
        def uBA = new BigInteger(number).toByteArray() // This returns a SIGNED byte array.
        uBA = uBA.collect { (it < 0) ? it & 0xFF : it } // Convert from signed to unsigned.
        while (uBA.size() > size) { uBA = uBA.drop(1) } // Trim leading bytes if too long. (takeRight() is not available)
        while (uBA.size() < size) { uBA = [0] + uBA } // Pad with leading zeros if too short.
        return uBA
    }
}

/**
 * parseAssocGroup(string, maxNodes)
 *
 *  Converts a comma-delimited string into a list of integers.
 *  Checks that all elements are integer numbers, and removes any that are not.
 *  Checks that the final list contains no more than maxNodes.
 */
private parseAssocGroup(string, maxNodes) {
    if (state.debug) log.trace "${device.displayName}: parseAssocGroup(): Translating string: ${string}"

    if (string) {
        def nodeList = string.split(',')
        nodeList = nodeList.collect { node ->
            if (node.isInteger()) { node.toInteger() }
            else { log.warn "${device.displayName}: parseAssocGroup(): Cannot parse: ${node}"}
        }
        nodeList = nodeList.findAll() // findAll() removes the nulls.
        if (nodeList.size() > maxNodes) { log.warn "${device.displayName}: parseAssocGroup(): Number of nodes is greater than ${maxNodes}!" }
        return nodeList.take(maxNodes)
    }
    else {
        return []
    }
}

/**
 *  zwaveEndPointEvent(sourceEndPoint, value)
 *
 *   Int        sourceEndPoint      ID of endPoint. 1 = Aggregate, 2 = Ch1, 3 = Ch2...
 *   Short      value               Expected range [0..255].
 *
 *  This method handles level reports received via several different command classes (BasicReport,
 *  SwitchMultilevelReport, SwitchColorReport).
 *
 *  switch and level attributes for the physical channel are updated (e.g. switchCh1, levelCh1).
 *
 *  If the channel is mapped to a colour, the colour's switch and level attributes are also updated
 *  (e.g. switchBlue, levelBlue).
 *
 *  Aggregate device atributes (switch, level, hue, saturation, color, colorName) are also updated.
 **/
private zwaveEndPointEvent(sourceEndPoint, value) {
    if (state.debug) log.trace "${device.displayName}: zwaveEndPointEvent(): EndPoint ${sourceEndPoint} has value: ${value}"

    def channel = sourceEndPoint - 1
    def mapping = state.channelMapping[channel]
    def isColor = ( mapping in ["Red", "Green", "Blue", "White"] )
    def percent = Math.round (value * 100 / 255)

    if ( 1 == sourceEndPoint ) { // EndPoint 1 is the aggregate channel, which is calculated later. IGNORE.
        if (state.debug) log.debug "${device.displayName}: zwaveEndPointEvent(): MultiChannelCmdEncap from endpoint 1 ignored."
    }
    else if ( (sourceEndPoint > 1) & (sourceEndPoint < 6) ) { // Physical channel #1..4

        // Update level:
        log.info "${device.displayName}: Channel ${channel} level is ${percent}%."
        sendEvent(name: "levelCh${channel}", value: percent, unit: "%")
        if (isColor) sendEvent(name: "level${mapping}", value: percent, unit: "%")

        // Update switch:
        if ( percent >= state.channelThresholds[channel].toInteger() ) {
            log.info "${device.displayName}: Channel ${channel} is on."
            sendEvent(name: "switchCh${channel}", value: "on")
            if (isColor) sendEvent(name: "switch${mapping}", value: "on")
        } else {
            log.info "${device.displayName}: Channel ${channel} is off."
            sendEvent(name: "switchCh${channel}", value: "off")
            if (isColor) sendEvent(name: "switch${mapping}", value: "off")
        }

        // If channel maps to a color, update hue, saturation, and color attributes:
        if (isColor) {
            def colorMap
            switch (mapping) {
                case "Red":
                    colorMap = [ red: value,
                               green: Math.round(device.latestValue("levelGreen").toInteger() * 255/100),
                                blue: Math.round(device.latestValue("levelBlue").toInteger() * 255/100),
                               white: Math.round(device.latestValue("levelWhite").toInteger() * 255/100)]
                    break
                case "Green":
                    colorMap = [ red: Math.round(device.latestValue("levelRed").toInteger() * 255/100),
                               green: value,
                                blue: Math.round(device.latestValue("levelBlue").toInteger() * 255/100),
                               white: Math.round(device.latestValue("levelWhite").toInteger() * 255/100)]
                    break
                case "Blue":
                    colorMap = [ red: Math.round(device.latestValue("levelRed").toInteger() * 255/100),
                               green: Math.round(device.latestValue("levelGreen").toInteger() * 255/100),
                                blue: value,
                               white: Math.round(device.latestValue("levelWhite").toInteger() * 255/100)]
                    break
                case "White":
                    colorMap = [ red: Math.round(device.latestValue("levelRed").toInteger() * 255/100),
                               green: Math.round(device.latestValue("levelGreen").toInteger() * 255/100),
                                blue: Math.round(device.latestValue("levelBlue").toInteger() * 255/100),
                               white: value]
                    break
                default:
                    colorMap = [ red: Math.round(device.latestValue("levelRed").toInteger() * 255/100),
                               green: Math.round(device.latestValue("levelGreen").toInteger() * 255/100),
                                blue: Math.round(device.latestValue("levelBlue").toInteger() * 255/100),
                               white: Math.round(device.latestValue("levelWhite").toInteger() * 255/100)]
                    break
            }
            colorMap << rgbwToHSV(colorMap) // Add HSV values into the colorMap.
            colorMap << rgbwToHex(colorMap) // Add hex into the colorMap.
            colorMap << rgbwToName(colorMap) // Add name into the colorMap.

            sendEvent(name: "hue", value: colorMap.hue, unit: "%")
            sendEvent(name: "saturation", value: colorMap.saturation, unit: "%")
            sendEvent(name: "colorName", value: "${colorMap.name}")
            sendEvent(name: "color", value: "${colorMap}", displayed: false)

            log.info "${device.displayName}: Color updated: ${colorMap}"
        }
    }
    else {
        log.warn "${device.displayName}: SwitchMultilevelReport recieved from unknown endpoint: ${sourceEndPoint}"
    }

    // Calculate aggregate switch attribute:
    // TODO: Add shortcuts here to check if the channel we are processing is IN or OUT.
    def newSwitch = "off"
    if ( "IN" == configAggregateSwitchMode) { // Build aggregate only from INput channels.
        (1..4).each { i ->
            if (( 8 == state.channelModes[i] ) & ( "on" == device.latestValue("switchCh${i}"))) { newSwitch = "on" }
        }
    } else if ("OUT" == configAggregateSwitchMode) { // Build aggregate only from RGBW/OUT channels.
        (1..4).each { i ->
            if (( 8 != state.channelModes[i] ) & ( "on" == device.latestValue("switchCh${i}"))) { newSwitch = "on" }
        }
    } else { // Build aggregate from ALL channels.
        (1..4).each { i ->
            if ( "on" == device.latestValue("switchCh${i}")) { newSwitch = "on" }
        }
    }
    log.info "${device.displayName}: Switch is ${newSwitch}."
    sendEvent(name: "switch", value: newSwitch)

    // Calculate aggregate level attribute:
    def newLevel = 0
    if ( "IN" == configAggregateSwitchMode) { // Build aggregate only from INput channels.
        (1..4).each { i ->
            if ( 8 == state.channelModes[i] ) { newLevel = Math.max(newLevel,device.latestValue("levelCh${i}").toInteger()) }
        }
    } else if ("OUT" == configAggregateSwitchMode) { // Build aggregate only from RGBW/OUT channels.
        (1..4).each { i ->
            if ( 8 != state.channelModes[i] ) { newLevel = Math.max(newLevel,device.latestValue("levelCh${i}").toInteger()) }
        }
    } else { // Build aggregate from ALL channels.
        (1..4).each { i ->
            newLevel = Math.max(newLevel,device.latestValue("levelCh${i}").toInteger())
        }
    }
    log.info "${device.displayName}: Level is ${newLevel}."
    sendEvent(name: "level", value: newLevel, unit: "%")

    // Should send the result of a CreateEvent...
    return "Processed channel level"
}

/**
 *  onChX() - Set switch for an individual channel to "on".
 *
 *  If channel is RGBW/OUT, restore the saved level (if there is one, else 100%).
 *  If channel is an INPUT channel, don't issue command. Log warning instead.
 **/
private onChX(channel) {
    log.info "${device.displayName}: onX(): Setting channel ${channel} switch to on."

    def cmds = []
    if (channel < 1 || channel > 4 ) {
        log.warn "${device.displayName}: onX(): Channel ${channel} does not exist!"
    }
    else if ( 8 == state.channelModes[channel] ) {
        log.warn "${device.displayName}: onX(): Channel ${channel} is an INPUT channel. Command not sent."
        cmds << zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint: (channel + 1) ).encapsulate(zwave.switchMultilevelV2.switchMultilevelGet()).format() // Endpoint = channel + 1
    }
    else {
        def newLevel =  device.latestValue("savedLevelCh${channel}") ?: 100
        newLevel =  ( 0 == newLevel.toInteger() ) ? 99 : Math.round(newLevel.toInteger() * 99 / 100 ) // scale level for switchMultilevelSet.
        cmds << zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint: (channel + 1) ).encapsulate(zwave.switchMultilevelV2.switchMultilevelSet(value: newLevel.toInteger())).format() // Endpoint = channel + 1
        sendEvent(name: "savedLevelCh${channel}", value: null) // Wipe savedLevel.
        sendEvent(name: "activeProgram", value: 0) // Wipe activeProgram.
    }

    return cmds
}

/**
 *  offChX() - Set switch for an individual channel to "off".
 *
 *  If channel is RGBW/OUT, save the level and turn off.
 *  If channel is an INPUT channel, don't issue command. Log warning instead.
 **/
private offChX(channel) {
    log.info "${device.displayName}: offX(): Setting channel ${channel} switch to off."

    def cmds = []
    if (channel > 4 || channel < 1 ) {
        log.warn "${device.displayName}: offX(): Channel ${channel} does not exist!"
    }
    else if ( 8 == state.channelModes[channel] ) {
        log.warn "${device.displayName}: offX(): Channel ${channel} is an INPUT channel. Command not sent."
        cmds << zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint: (channel + 1) ).encapsulate(zwave.switchMultilevelV2.switchMultilevelGet()).format() // endPoint = channel + 1
    }
    else {
        sendEvent(name: "savedLevelCh${channel}", value: device.latestValue("levelCh${channel}").toInteger()) // Save level to 'hidden' attribute.
        sendEvent(name: "activeProgram", value: 0) // Wipe activeProgram.
        cmds << zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint: (channel + 1) ).encapsulate(zwave.switchMultilevelV2.switchMultilevelSet(value: 0)).format() // endPoint = channel + 1
    }

    return cmds
}

/**
 *  setLevelChX() - Set level of an individual channel.
 *
 *  If channel is an INPUT channel, don't issue command. Log warning instead.
 *
 *  The Fibaro RGBW Controller does not support dimmingDuration. Instead,
 *  dimming durations are configured using device parameters (8/9/10/11).
 *
 **/
private setLevelChX(level, channel) {
    log.info "${device.displayName}: setLevelChX(): Setting channel ${channel} to level: ${level}."

    def cmds = []
    if (channel > 4 || channel < 1 ) {
        log.warn "${device.displayName}: setLevelChX(): Channel ${channel} does not exist!"
    }
    else if ( 8 == state.channelModes[channel] ) {
        log.warn "${device.displayName}: setLevelChX(): Channel ${channel} is an INPUT channel. Command not sent."
    }
    else {
        if (level < 0) level = 0
        if (level > 100) level = 100
        level = Math.round(level * 99 / 100 ) // scale level for switchMultilevelSet.
        cmds << zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint: (channel + 1) ).encapsulate(zwave.switchMultilevelV2.switchMultilevelSet(value: level.toInteger())).format() // Endpoint = channel + 1
        sendEvent(name: "savedLevelCh${channel}", value: null) // Wipe savedLevel.
        sendEvent(name: "activeProgram", value: 0) // Wipe activeProgram.
    }

    return cmds
}

/**
 *  rgbToRGBW(colorMap)
 *
 *  Adds white key to a colorMap containing red, green, and blue keys.
 *  For now, the white value is calculated as min(red,green,blue).
 *
 *  A more-complicated translation is discussed here:
 *   http://stackoverflow.com/questions/21117842/converting-an-rgbw-color-to-a-standard-rgb-hsb-rappresentation
 *  But for now we're keeping it simple.
 **/
private rgbToRGBW(Map colorMap) {
    if (state.debug) log.trace "${device.displayName}: rgbToRGBW(): Translating colorMap: ${colorMap}"

    if (colorMap.containsKey("red") & colorMap.containsKey("green") & colorMap.containsKey("blue")) {
        def w = [colorMap.red, colorMap.green, colorMap.blue].min()
        return colorMap << [ white: w ]
    }
    else {
        log.error "${device.displayName}: rgbToRGBW(): Cannot obtain color information from colorMap: ${colorMap}"
    }
}

/**
 *  hexToRGBW(colorMap)
 *
 *  Adds red, green, blue, and white keys to a colorMap containing a hex key.
 **/
private hexToRGBW(Map colorMap) {
    if (state.debug) log.trace "${device.displayName}: hexToRGBW(): Translating colorMap: ${colorMap}"

    if (colorMap.containsKey("hex")) {
        def r = Integer.parseInt(colorMap.hex.substring(1,3),16)
        def g = Integer.parseInt(colorMap.hex.substring(3,5),16)
        def b = Integer.parseInt(colorMap.hex.substring(5,7),16)
        def w = [r, g, b].min()
        return colorMap << [ red: r, green: g, blue: b, white: w]
    }
    else {
        log.error "${device.displayName}: hexToRGBW(): Cannot obtain color information from colorMap: ${colorMap}"
    }
}

/**
 *  rgbwToHex(colorMap)
 *
 *  Adds hex key to a colorMap containing red, green, and blue keys.
 *  The white value is just ignored.
 **/
private rgbwToHex(Map colorMap) {
    if (state.debug) log.trace "${device.displayName}: rgbwToHex(): Translating colorMap: ${colorMap}"

    if (colorMap.containsKey("red") & colorMap.containsKey("green") & colorMap.containsKey("blue")) {
        def r = hex(colorMap.red,2)
        def g = hex(colorMap.green,2)
        def b = hex(colorMap.blue,2)
        return colorMap << [ hex: "#${r}${g}${b}" ]
    }
    else {
        log.error "${device.displayName}: rgbwToHex(): Cannot obtain color information from colorMap: ${colorMap}"
    }
}

/**
 *  hex(value, width=2)
 *
 *  Formats an int as a hex string.
 **/
private hex(value, width=2) {
    def s = new BigInteger(Math.round(value).toString()).toString(16)
    while (s.size() < width) { s = "0" + s }
    return s
}

/**
 *  hsvToRGBW(colorMap)
 *
 *  Adds red, green, blue, and white keys to a colorMap containing hue, saturation, level (value) keys.
 **/
private hsvToRGBW(Map colorMap) {
    if (state.debug) log.trace "${device.displayName}: hsvToRGBW(): Translating colorMap: ${colorMap}"

    if (colorMap.containsKey("hue") & colorMap.containsKey("saturation") & colorMap.containsKey("level")) {
        float h = colorMap.hue / 100
        while (h >= 1) h -= 1
        float s = colorMap.saturation / 100
        float v = colorMap.level * 255 / 100

        int d = (int) h * 6
        float f = (h * 6) - d
        int n = Math.round(v)
        int p = Math.round(v * (1 - s))
        int q = Math.round(v * (1 - f * s))
        int t = Math.round(v * (1 - (1 - f) * s))

        switch (d) {
          case 0: return colorMap << [ red: n, green: t, blue: p, white: [n,t,p].min() ]
          case 1: return colorMap << [ red: q, green: n, blue: p, white: [q,n,p].min() ]
          case 2: return colorMap << [ red: p, green: n, blue: t, white: [p,n,t].min() ]
          case 3: return colorMap << [ red: p, green: q, blue: n, white: [p,q,n].min() ]
          case 4: return colorMap << [ red: t, green: p, blue: n, white: [t,p,n].min() ]
          case 5: return colorMap << [ red: n, green: p, blue: q, white: [n,p,q].min() ]
        }
    }
    else {
        log.error "${device.displayName}: hsvToRGBW(): Cannot obtain color information from colorMap: ${colorMap}"
    }
}

/**
 *  rgbwToHSV(colorMap)
 *
 *  Adds hue, saturation, level (value/brightness) keys to a colorMap containing red, green, and blue keys.
 **/
private rgbwToHSV(Map colorMap) {
    if (state.debug) log.trace "${device.displayName}: rgbwToHSV(): Translating colorMap: ${colorMap}"

    if (colorMap.containsKey("red") & colorMap.containsKey("green") & colorMap.containsKey("blue")) { // Don't test for white key.

        float r = colorMap.red / 255f
        float g = colorMap.green / 255f
        float b = colorMap.blue / 255f
        float w = (colorMap.white) ? colorMap.white / 255f : 0.0
        float max = [r, g, b].max()
        float min = [r, g, b].min()
        float delta = max - min

        float h,s,v = 0

        if (delta) {
            s = delta / max
            if (r == max) {
                h = ((g - b) / delta) / 6
            } else if (g == max) {
                h = (2 + (b - r) / delta) / 6
            } else {
                h = (4 + (r - g) / delta) / 6
            }
            while (h < 0) h += 1
            while (h >= 1) h -= 1
        }

        v = [max,w].max() // The white value contributes to brightness only.

        return colorMap << [ hue: h * 100, saturation: s * 100, level: Math.round(v * 100) ] // hue and sat are not rounded.
    }
    else {
        log.error "${device.displayName}: rgbwToHSV(): Cannot obtain color information from colorMap: ${colorMap}"
    }
}

/**
 *  getPresetColors()
 *
 *  Returns a map of preset colors. Used by nameToRGBW() and rgbwToName().
 **/
private getPresetColors() {
    return [
        [name: "black",     red:   0, green:   0, blue:   0, white:   0 ],
        [name: "white",     red: 255, green: 255, blue: 255, white: 255 ],
        [name: "red",       red: 255, green:   0, blue:   0, white:   0 ],
        [name: "green",     red:   0, green: 255, blue:   0, white:   0 ],
        [name: "blue",      red:   0, green:   0, blue: 255, white:   0 ],
        [name: "cyan",      red:   0, green: 255, blue: 255, white:   0 ],
        [name: "magenta",   red: 255, green:   0, blue:  64, white:   0 ],
        [name: "orange",    red: 255, green: 102, blue:   0, white:   0 ],
        [name: "purple",    red: 170, green:   0, blue: 255, white:   0 ],
        [name: "yellow",    red: 255, green: 160, blue:   0, white:   0 ],
        [name: "pink",      red: 255, green:  50, blue: 204, white:   0 ],
        [name: "coldWhite", red: 255, green: 255, blue: 255, white:   0 ],
        [name: "warmWhite", red:   0, green:   0, blue:   0, white: 255 ]
    ]
}

/**
 *  nameToRGBW(colorMap)
 *
 *  Adds red, green, blue, and white keys to a colorMap containing a name key.
 **/
private nameToRGBW(Map colorMap) {
    if (state.debug) log.trace "${device.displayName}: nameToRGBW(): Translating colorMap: ${colorMap}"

    if (colorMap.containsKey("name")) {
        def rgbwMap = getPresetColors().find { it.name == colorMap.name }
        if (rgbwMap) {
            return colorMap << rgbwMap
        }
        else {
            log.error "${device.displayName}: nameToRGBW(): Cannot translate color name: ${colorMap.name}"
        }
    }
    else {
        log.error "${device.displayName}: nameToRGBW(): Cannot obtain color information from colorMap: ${colorMap}"
    }
}

/**
 *  rgbwToName(colorMap)
 *
 *  Adds a name key to a colorMap containing red, green, blue, white keys.
 *  Allows a tolerance of 10 on each r/g/b channel, and 50 on white channel.
 *  If color cannot be matched to a named preset color, name: "custom" is returned.
 **/
private rgbwToName(Map colorMap) {
    if (state.debug) log.trace "${device.displayName}: rgbwToName(): Translating colorMap: ${colorMap}"

    if (colorMap.containsKey("red") & colorMap.containsKey("green") & colorMap.containsKey("blue")) {

        def t = 10
        def r = colorMap.red
        def g = colorMap.green
        def b = colorMap.blue
        def w = (colorMap.white) ?: 0

        def match = getPresetColors().find { (it.red >= r-t) & (it.red <= r+t) &
                                             (it.green >= g-t) & (it.green <= g+t) &
                                             (it.blue >= b-t) & (it.blue <= b+t) &
                                             (it.white >= w- (5*t)) & (it.white <= w+(5*t))
                                           }

        if (match) {
            if (state.debug) log.trace "${device.displayName}: rgbwToName(): Found match: ${match.name}"
            return colorMap << [name: match.name]
        }
        else {
            return colorMap << [name: "custom"]
        }
    }
    else {
        log.error "${device.displayName}: rgbwToName(): Cannot obtain color information from colorMap: ${colorMap}"
    }
}


/**********************************************************************
 *  Testing Commands:
 **********************************************************************/

/**
 * getConfigReport() - Get current device parameters and output to debug log.
 *
 *  The device settings in the UI cannot be updated due to platform restrictions.
 */
def getConfigReport() {
    if (state.debug) log.trace "${device.displayName}: getConfigReport()"
    def cmds = []

    cmds << zwave.configurationV2.configurationGet(parameterNumber: 1).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 6).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 8).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 9).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 10).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 11).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 12).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 13).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 14).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 16).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 30).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 38).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 39).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 42).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 43).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 44).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 45).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 71).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 72).format()
    cmds << zwave.configurationV2.configurationGet(parameterNumber: 73).format()

    // Request Association Reports:
    cmds << zwave.associationV2.associationGet(groupingIdentifier:1).format()
    cmds << zwave.associationV2.associationGet(groupingIdentifier:2).format()
    cmds << zwave.associationV2.associationGet(groupingIdentifier:3).format()
    cmds << zwave.associationV2.associationGet(groupingIdentifier:4).format()
    cmds << zwave.associationV2.associationGet(groupingIdentifier:5).format()

    // Request Manufacturer, Version, Firmware Reports:
    cmds << zwave.manufacturerSpecificV2.manufacturerSpecificGet().format()
    cmds << zwave.versionV1.versionGet().format()
    cmds << zwave.firmwareUpdateMdV2.firmwareMdGet().format()

    return delayBetween(cmds,800) // Need log delay here, otherwise the IDE Live Logging can't keep up.
}

/**
 *  test()
 **/
def test() {
    log.trace "$device.displayName: test()"

    def cmds = []

    // EXAMPLE COMMANDS:

    // Verify device configuration:
    //cmds << getConfigReport()

    // Setting Color:
    //cmds << setColor(red: 255, green: 128, blue: 66)
    //cmds << setColor(hex: "#7FFFD4")
    //cmds << setColor(name: "pink")

    // Programs:
    //cmds << startProgram(7)

    // Set device paramters:
    //cmds << response(zwave.configurationV1.configurationSet(configurationValue: [17,17], parameterNumber: 14, size: 2)) // 4xRGB
    //cmds << response(zwave.configurationV1.configurationSet(configurationValue: [17,24], parameterNumber: 14, size: 2)) // 3xRGB, I4=0-10V.
    //cmds << response(zwave.configurationV1.configurationSet(configurationValue: [136,136], parameterNumber: 14, size: 2)) // All 0-10v inputs
    //cmds << response(zwave.configurationV1.configurationSet(configurationValue: [153,152], parameterNumber: 14, size: 2)) // 3x(OUT momentary/Normal), I4=INPUT
    //cmds << response(zwave.configurationV1.configurationSet(configurationValue: [51,51], parameterNumber: 14, size: 2)) // 4x RGBW (RAINBOW)
    //cmds << response(zwave.configurationV1.configurationGet(parameterNumber: 14))

    // Get Basic:
    //cmds << response(zwave.basicV1.basicGet().format())

    // Get level (aggregate - channel 0):
    //cmds << response(zwave.switchMultilevelV2.switchMultilevelGet()).format()) // Returns a SwitchMultilevelReport.
    // OR
    //cmds << response(zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint:0).encapsulate(zwave.switchMultilevelV2.switchMultilevelGet()).format())

    // Get level (individual channels):
    //cmds << response(zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint:2).encapsulate(zwave.switchMultilevelV2.switchMultilevelGet()).format())
    //cmds << response(zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint:3).encapsulate(zwave.switchMultilevelV2.switchMultilevelGet()).format())
    //cmds << response(zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint:4).encapsulate(zwave.switchMultilevelV2.switchMultilevelGet()).format())
    //cmds << response(zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint:5).encapsulate(zwave.switchMultilevelV2.switchMultilevelGet()).format())

    // Set level (individual channels):
    //cmds << response(zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint:2).encapsulate(zwave.switchMultilevelV2.switchMultilevelSet(value: 0x00)).format())
    //cmds << response(zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint:3).encapsulate(zwave.switchMultilevelV2.switchMultilevelSet(value: 0x00)).format())
    //cmds << response(zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint:4).encapsulate(zwave.switchMultilevelV2.switchMultilevelSet(value: 0x00)).format())
    //cmds << response(zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint:5).encapsulate(zwave.switchMultilevelV2.switchMultilevelSet(value: 0x00)).format())

    // Using the Switch Color Command Class:
    // See: https://community.smartthings.com/t/color-switch-z-wave-command-class/19300
    // switchColorSet allows you to send level for each colour channel in one command. It doesn't affect the channels not specified.
    // The Fibaro RGBW returns SwitchMultilevelReport for each channel affected, so unfortunately, you don't get a single report back.

    //cmds << response(zwave.switchColorV3.switchColorSet(red: 0xFF, green: 0xFF, blue: 0xFF, warmWhite: 0, coldWhite: 0).format()) // Set all colours.
    //cmds << response(zwave.switchColorV3.switchColorSet(red: 128).format()) // Sets just the red channel.

    // SwitchColour reports can only be requested for one colour at a time though:
    //cmds << response(zwave.switchColorV3.switchColorGet().format()) // Returns report for warmWhite by default: SwitchColorReport(colorComponent: warmWhite, colorComponentId: 0, value: 161)
    //cmds << response(zwave.switchColorV3.switchColorGet(colorComponent: "red").format()) // This should return a SwitchColorReport, however there appears to be a bug in the command class which causes an error.
    // To get round the bug, we can make the request using the colorComponentId instead:
    //cmds << response(zwave.switchColorV3.switchColorGet(colorComponentId: 2).format()) // Returns SwitchColorReport(colorComponent: red, colorComponentId: 2, value: 95)
    //cmds << response(zwave.switchColorV3.switchColorGet(colorComponentId: 3).format()) // Returns SwitchColorReport(colorComponent: green, colorComponentId: 3, value: 0)
    //cmds << response(zwave.switchColorV3.switchColorGet(colorComponentId: 4).format()) // Returns SwitchColorReport(colorComponent: blue, colorComponentId: 4, value: 0)
    //cmds << response(zwave.switchColorV3.switchColorGet(colorComponentId: 0).format()) // Returns SwitchColorReport(colorComponent: warmWhite, colorComponentId: 0, value: 161)

    // Get Meter Reports (aggregate values):
    //cmds << response(zwave.meterV3.meterGet(scale: 0).format()) // Get energy meter report.
    //cmds << response(zwave.meterV3.meterGet(scale: 2).format()) // Get power meter report.
    //cmds << response(zwave.meterV3.meterReset().format()) // Reset accumulated energy.

    // Get Meter Reports (individual channels): [DOES NOT APPEAR TO BE SUPPORTED BY THE FIBARO RGBW CONTROLLER]
    //cmds << response(zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint:3).encapsulate(zwave.meterV3.meterGet(scale: 0)).format()) // Get energy meter report for channel #3 - NO RESPONSE
    //cmds << response(zwave.multiChannelV3.multiChannelCmdEncap(destinationEndPoint:5).encapsulate(zwave.meterV3.meterGet(scale: 2)).format()) // Get power meter report for channel #5 - NO RESPONSE

    // Get a MultiChannelEndPointReport:
    //cmds << response(zwave.multiChannelV3.multiChannelEndPointGet())
    //  This returns: MultiChannelEndPointReport(dynamic: false, endPoints: 5, identical: true, res00: 0, res11: false) - which basically just tells us there's 5 static endPoints.

    // Get SensorMultilevelReport:
    //cmds << response(zwave.sensorMultilevelV3.sensorMultilevelGet().format()) // Returns one report for sensorType == 4 (Instantaneous Power).

    // Get CONFIGURATION reports (must specify a parameterNumber):
    //cmds << response(zwave.configurationV1.configurationGet(parameterNumber: 10))
    //cmds << response(zwave.configurationV1.configurationGet(parameterNumber: 12))
    // There doesn't seem to be a way to request all Parameters in one go.

    // Association Group Set/Get:
    //cmds << response(zwave.associationV2.associationSet(groupingIdentifier:4, nodeId:[zwaveHubNodeId]).format()) // This adds the controller to Assoc. Group 4.
    //cmds << response(zwave.associationV2.associationGet(groupingIdentifier:4).format())

    // Get Manufaturer, Version, and Firmware reports.
    //cmds << response(zwave.manufacturerSpecificV2.manufacturerSpecificGet().format())
    //cmds << response(zwave.versionV1.versionGet().format())
    //cmds << response(zwave.firmwareUpdateMdV2.firmwareMdGet().format())

    return delayBetween(cmds,200)
}
