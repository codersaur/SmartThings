# Z-Wave Tweaker
https://github.com/codersaur/SmartThings/tree/master/devices/zwave-tweaker

Copyright (c) [David Lomas](https://github.com/codersaur)

## Overview
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-tiles-main.png" width="200" align="right">

A SmartThings device handler to assist with interrogating and tweaking Z-Wave devices.

### Key features:
* Discover association groups, multi-channel endpoints, and configuration parameters.
* Configure association group members from the SmartThings GUI.
* Configure parameter values from the SmartThings GUI.
* Configure Protection and Switch_All modes from the SmartThings GUI.
* Discover supported meter/alarm/notification/sensor report types.
* Automatically build a complete list of the Z-Wave commands sent by a device.
* Support for Z-Wave and Z-Wave Plus devices.
* Extensive inline code comments to support community development.

## Installation
The Z-Wave Tweaker is designed to temporarily replace the normal device handler for a device. Follow [these instructions](https://github.com/codersaur/SmartThings#device-handler-installation-procedure) to install the device handler using the SmartThings IDE.

## GUI
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-tiles-main.png" width="200" align="right">

The Z-Wave Tweaker has two main types of tile: _Scan_ tiles and _Print_ tiles.

#### Scan Tiles:
Each _Scan_ tile triggers interrogation of a certain aspect of the device:

* **Scan General**: Obtains basic properties common to most devices, such as product ID, firmware version, and supported commands.
* **Scan Association Groups**: Collects information about association groups and their members.
* **Scan Endpoints**: Scans _endpoints_ advertised by _multi-channel_ devices and discovers their capabilities.
* **Scan Parameters**: Discovers available configuration parameters, which can be used to customise the device.
* **Scan Actuator**: Discovers common actuator attributes, such as _basic_, _switch_, and _switchMultiLevel_.
* **Scan Sensor**: Discovers common sensor capabilities, such as _sensorBinary_, _sensorMultilevel_, _meter_, and _notification_.

#### Print Tiles:
Each _Print_ tile can be used to output the information collected by the corresponding _Scan_ tile. The output can be viewed using the _Live Logging_ tab within the SmartThings IDE.

#### Sync Tile:
This tile indicates when all configuration changes have been successfully synchronised with the physical device.

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-tiles-synced.png" width="100"> <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-tiles-sync-pending.png" width="100">

#### Cleanup Tile:
Tap this tile when you have finished using the Z-Wave Tweaker. It will remove all collected data in preparation for reinstating the original device handler.

## Usage
The Z-Wave Tweaker is designed to be operated from the SmartThings smartphone app, however all of the information collected will be output to the _Live Logging_ tab in the SmartThings IDE.

* Begin by opening the _Live Logging_ tab in the SmartThings IDE. It is recommended to filter the IDE Log so that it shows only the events from the specific device in use. 
* Next, navigate to the device in the SmartThings app on your smartphone.

#### Discovery of Device Properties:
The Z-Wave Tweaker can scan a device to discover basic properties, including any supported association groups, multi-channel endpoints, and configuration parameters.

* Tap the _Scan General_ tile to begin collecting basic information. After a few seconds, you should see some responses from the device in the IDE, such as _Version_ and _Protection_ reports.
* After the responses stop, tap on of the other _Scan_ tiles to begin collecting more-specific information.  
   Be sure to set appropriate [scan ranges](https://github.com/codersaur/SmartThings/blob/master/devices/zwave-tweaker/README.md#scan-ranges) in the Tweaker's settings, and allow time for each scan to complete.  
   **Do not run multiple scans at the same time as this will cause network congestion and some responses from the device may be lost**.
* To view the data that has been collected, tap on the corresponding _Print_ tile. This should output information to the _Live Logging_ tab in the IDE.

   _Print General:_
   <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-log-print-general.png">

   <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-log-print-general-stats.png">

   _Print Association Groups:_
   <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-log-print-assocgroups.png">

   _Print Endpoints:_
   <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-log-print-endpoints.png">

   _Print Parameters:_
   <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-log-print-params.png">

   _Print Commands:_
   <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-log-print-commands.png">

* If the information appears incomplete, try tapping the relevant _Print_ tile again as the IDE sometimes fails to show all messages.
* If some expected association groups, endpoints, or parameters are not shown, try re-scanning (it's unlikely that all information will be collected successfully on a first scan). Also, check the _settings_ to confirm that appropriate scan ranges are configured.

#### Creating Device Associations:
_Device Associations_ enable a Z-Wave device to send commands directly to other devices without the commands having to be relayed by the SmartThings hub. For example, a Z-Wave motion sensor may be configured to send a _Basic (ON)_ command to a nearby dimmer device when motion is detected. This direct communication typically gives faster response times compared to triggering rules in a SmartApp.

For a device to be able to send commands it must support either the ASSOCIATION or MULTI-CHANNEL_ASSOCIATION command classes.  If so, it will have one or more _Association Groups_ that will send certain types of commands on certain conditions. **The operation of these groups will be specific to the device and should be documented in the manufacturer's product manual.**

Using the Z-Wave Tweaker's settings it is possible to configure one association group at a time:

1. From the SmartThings smartphone app, click on the gear icon to open the device settings.
2. In the _CONFIGURE ASSOCIATION GROUP_ section, input the ID of the target group, and [fill in the members](https://github.com/codersaur/SmartThings/tree/master/devices/zwave-tweaker#configure-association-group).   
   If you want to remove all members from the association group, leave the members blank.  
   Note, the _Device Network IDs_ for all Z-Wave devices in your SmartThings network are displayed on the _My Devices_ tab in the SmartThings IDE. Consult the relevant manufacturer's manual for information about the endpoints supported by a particular target device.

   <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-settings-assocgroup.png" width="200">
3. If needed, specify the command class to be used (this is not normally required as the Z-Wave Tweaker will automatically select the appropriate command class).
4. Tap _Done_. The change will now be synced with the device, when complete, the _Sync_ tile should turn green.  
   In the IDE, you should see the old members and the new members displayed:

   <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-log-assocgroupsync.png">
5. If a change to an association group will not sync, check the following:
   * The device supports ASSOCIATION, and if you are setting endpoint destinations MULTI-CHANNEL ASSOCIATION command classes.
   * The association group ID exists.
   * The association groups supports the required number of members (it is common for an association groups to support a maximum of 5-8 destinations).
6. Repeat steps 1-5 for each association group that you wish to change.
7. Finally, tap the _Print Association Groups_ tile to verify the configuration of all groups.

#### Changing a Device Parameter:
Z-Wave device parameters can be used to alter the behaviour of a device, for example, a reporting interval, or a sensor threshold. **All parameters are device-specific so it is essential to consult the manufacturer's product manual for a full description of each parameter.**

Using the Z-Wave Tweaker's settings it is possible to configure one parameter at a time:

1. From the SmartThings smartphone app, click on the gear icon to open the device settings.
2. In the _CONFIGURE A PARAMETER_ section, input the parameter ID and desired parameter value.

   <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-settings-param.png" width="200">
3. Tap _Done_. The change will now be synced with the device, when complete, the _Sync_ tile should turn green.  
   In the IDE, you should see the old value and the new value displayed:

   <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-log-paramsync.png">
4. If a change to a parameter value fails to sync, check the following:
   * The device supports the CONFIGURATION command class.
   * The parameter ID is correct.
   * The parameter is not a read-only parameter.
   * The value is in the allowed range.
5. Repeat steps 1-4 for each parameter value that you wish to change.
6. Finally, tap the _Print Parameters_ tile to verify the configuration of all parameters.

#### Configuring _Protection_ Mode:
Devices that support the Z-Wave PROTECTION Command Class can be configured to prevent unintentional control (e.g. by a child) by disabling the physical switches and/or RF control.

Using the Z-Wave Tweaker's settings it is possible to configure both the _Local_ and _RF_ protection mode:

1. From the SmartThings smartphone app, click on the gear icon to open the device settings.
2. In the _CONFIGURE OTHER SETTINGS_ section, select the desired mode for _Local_ and _RF_ protection.

   <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-settings-protection.png" width="200">
3. Tap _Done_. The change will now be synced with the device, when complete, the _Sync_ tile should turn green.

   <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-log-protection-sync.png">
4. If a change to the protection mode fails to sync, check the following:
   * The device supports the PROTECTION command class.
   * The device supports the specific mode selected (e.g. _Sequence_ is likely to be supported by keypads, but not by simple toggle switches).

#### Configuring Switch-All Mode:
Devices that support the Z-Wave SWITCH_ALL Command Class can be configured to respond or ignore certain SWITCH_ALL_SET broadcast commands.

Using the Z-Wave Tweaker's settings it is possible to configure a device's response to SWITCH_ALL commands:

1. From the SmartThings smartphone app, click on the gear icon to open the device settings.
2. In the _CONFIGURE OTHER SETTINGS_ section, select the desired mode for _ALL ON / ALL OFF_ function.

   <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-settings-switch-all.png" width="200">
3. Tap _Done_. The change will now be synced with the device, when complete, the _Sync_ tile should turn green.

   <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-log-switch-all-sync.png">
4. If a change to the SWITCH_ALL mode fails to sync, check the following:
   * The device supports the SWITCH_ALL command class.
   * The device supports the specific mode selected.


## Settings

#### General Settings:

* **IDE Live Logging Level**: Set the level of log messages shown in the SmartThings IDE _Live Logging_ tab. For normal operation _Info_ is recommended, if troubleshooting use _Debug_ or _Trace_.

#### Scan Ranges:
Configure the scan range for association groups, endpoints, and configuration parameters. If not configured, the default scan ranges are:
* Association Groups: 0 to 10.
* Endpoints: 0 to 10.
* Parameters: 0 to 20.

#### Configure Association Group:
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-settings-assocgroup.png" width="200" align="right">

Use the settings in this section to configure an association group.

* **Association Group ID**: The ID of the group that will be configured. If this input is left blank, no association groups will by modified.

* **Association Group Members**: Members must be defined as a comma-delimited list of targets. Each target device can be specified in one of two ways:
   * _Node_: A single hexadecimal number (e.g. "0C") representing the target _Device Network ID_.
   * _Endpoint_: A pair of hexadecimal numbers separated by a colon (e.g. "10:1") that represent the target _Device Network ID_ and _Endpoint ID_ respectively. For devices that support multiple endpoints (e.g. a dual relay), this allows a specific endpoint to be targeted by the association group.  
   
   Note, the Device Network IDs for all Z-Wave devices in your SmartThings network are displayed on the My Devices tab in the SmartThings IDE. Consult the relevant manufacturer's manual for information about the endpoints supported by a particular target device.

* **Command Class**: The Z-Wave Tweaker will automatically detect whether to use _Association_ or _Multi-channel Association_ commands, however you can force it to use a specific command class using this setting.

#### Configure A Parameter:

Use the settings in this section to configure a configuration parameter. Parameters are device-specific so it is recommended to consult the manufacturer's product manual for a full description of each parameter.

* **Parameter ID**: The ID of the parameter that will be configured. If this input is left blank, no parameter values will by modified.

* **Parameter Value**: Enter the desired value for the parameter.

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-settings-param.png" width="200">

#### Configure Other Settings:
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-settings-protection.png" width="200" align="right">

* **Local Protection**: Prevent unintentional control (e.g. by a child), by disabling any physical switches on the device. The device must support the PROTECTION command class.

* **RF Protection**: Enabling _RF Protection_ means the device will not respond to wireless commands from other Z-Wave devices, including on/off commands issued via the SmartThings app.The device must support the PROTECTION command class.

* **ALL ON/ALL OFF Function**: Control the device's response to SWITCH_ALL_SET commands.

#### Original Settings:

Do not delete any setting values below this line! They belong to the original device handler and will be reinstated when the original device handler is restored.

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/zwave-tweaker/screenshots/zwt-ss-settings-original.png" width="200">

## Current Limitations
* The Z-Wave Tweaker will not work with sleepy (e.g. battery-powered) devices.
* It is not possible to collect parameter meta-data such as names, descriptions, and value ranges, as SmartThings does not yet support the CONFIGURATION V3 command class.

## Version History

#### 2017-03-16: v0.08
* initialise(): Removes any null ccIds parsed from the rawDescription.

#### 2017-03-15: v0.07
* cleanUp(): Uses state.remove() and device.updateSetting()

#### 2017-03-15: v0.06
* Beta release.

## References
 Some useful links relevant to the development of this device handler:
* [SmartThings Device Type Developers Guide]( http://docs.smartthings.com/en/latest/device-type-developers-guide/index.html)
* [Z-Wave Public Specification Files](http://z-wave.sigmadesigns.com/design-z-wave/z-wave-public-specification/)

## License

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
