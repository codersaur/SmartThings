# Fibaro Flood Sensor (FGFS-101) (EU)
https://github.com/codersaur/SmartThings/tree/master/devices/fibaro-flood-sensor

Copyright (c) [David Lomas](https://github.com/codersaur)

## Overview
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-flood-sensor/screenshots/ffs-ss-tiles-main.png" width="200" align="right">
An advanced SmartThings device handler for the Fibaro Flood Sensor (FGFS-101) (EU).

**The newer ZW5 (Z-Wave Plus) version is NOT supported.**

### Key features:
* Reports water, temperature, tamper, and battery attributes.
* All Z-Wave parameters can be configured using the SmartThings GUI.
* Multi-channel device associations can be configured using the SmartThings GUI.
* Supports battery and hard-wired power modes.
* _Sync_ tile indicates when all configuration options are successfully synchronised with the physical device.
* Logger functionality enables critical errors and warnings to be saved to the _logMessage_ attribute.
* Extensive inline code comments to support community development.

## Installation

1. Follow [these instructions](https://github.com/codersaur/SmartThings#device-handler-installation-procedure) to install the device handler in the SmartThings IDE.

2. **Note for iPhone users**: The _defaultValue_ of inputs (preferences) are commented out to cater for Android users. iPhone users can uncomment these lines if they wish (search for "iPhone" in the code).

3. From the SmartThings app on your phone, edit the device settings to suit your installation and hit _Done_. Note, if the device is in battery-powered mode, new settings will only by synchronised when the device wakes up, however you should be able to give the device a shake to force it to wake up.

## Settings

#### General Settings:
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-flood-sensor/screenshots/ffs-ss-settings-general.png" width="200" align="right">

* **IDE Live Logging Level**: Set the level of log messages shown in the SmartThings IDE _Live Logging_ tab. For normal operation _Info_ or _Warning_ is recommended, if troubleshooting use _Debug_ or _Trace_.

* **Device Logging Level**: Set the level of log messages that will be recorded in the device's _logMessage_ attribute. This offers a way to review historical messages without having to keep the IDE _Live Logging_ screen open. To prevent excessive events, the maximum level supported is _Warning_.

* **Force Full Sync**: By default, only settings that have been modified will be synchronised with the device. Enable this setting to force all device parameters and association groups to be re-sent to the device.

* **Auto-reset Tamper Alarm**: Automatically reset tamper alarms after a time delay.

#### Wake Up Interval:

* **Wake Up Interval**: The device will wake up periodically to sync configuration. A longer interval will save battery power. Only applicable when in battery-power mode.

#### Device Parameters:

The settings in this section can be used to specify the value of all writable device parameters. It is recommended to consult the [manufacturer's manual](http://manuals.fibaro.com/flood-sensor/) for a full description of each parameter.

If no value is specified for a parameter, then it will not be synched with the device and the existing value in the device will be preserved.

#### Multi-channel Device Associations:
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-flood-sensor/screenshots/ffs-ss-settings-assoc.png" width="200" align="right">

The Fibaro Floor Sensor supports _Multi-channel_ Device Associations. This allows the device to send water and tamper alarm commands directly to Z-Wave other devices (e.g. sirens), without the commands being processed by the SmartThings hub. This results in faster response times compared to using a SmartApp.

The Fibaro Flood Sensor supports three association groups:

- **Association Group #1**: Sends BASIC_SET or ALARM commands when the sensor detects water.
- **Association Group #2**: Sends ALARM commands when the device detects movement or tampering.
- **Association Group #3**: Device status (contains the main controller only).

The members of each _Association Group_ must be defined as a comma-delimited list of target nodes. Each target device can be specified in one of two ways:
- _Node_: A single hexadecimal number (e.g. "0C") representing the target _Device Network ID_.
- _Endpoint_: A pair of hexadecimal numbers separated by a colon (e.g. "10:1") that represent the target _Device Network ID_ and _Endpoint ID_ respectively. For devices that support multiple endpoints, this allows a specific endpoint to be targeted by the association group.

You can find the _Device Network ID_ for all Z-Wave devices in your SmartThings network from the _My Devices_ tab in the SmartThings IDE. Consult the relevant manufacturer's manual for information about the endpoints supported by a particular target device.

## GUI

#### Main Tile:
The main tile indicates water detection and temperature.

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-flood-sensor/screenshots/ffs-ss-tiles-wet.png" width="200">

#### Power Status Tile:
This tile indicates the battery level, or that the device is hard-wired to DC power.

#### Tamper Tile:
This tile indicates if the device has detected movement or tampering. Pressing the tile will clear the tamper status. Tamper alerts can also be cleared automatically (see settings).

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-flood-sensor/screenshots/ffs-ss-tiles-tamper.png" width="200">

#### Sync Tile:
This tile indicates when all configuration settings have been successfully synchronised with the physical device. Note, if the device is in battery-powered mode, new settings will only by synchronised when the device wakes up.

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-flood-sensor/screenshots/ffs-ss-tiles-sync.png" width="200">

## SmartApp Integration

#### Attributes:

The device handler publishes the following attributes:

* **battery [NUMBER]**: Current battery level (%).
* **logMessage [STRING]**: Important log messages.
* **powerSource [ENUM]**: Indicates if the device is battery-, dc-, or mains-powered.
* **syncPending [NUMBER]**: The number of configuration items that need to be synced with the physical device. _0_ if the device is fully synchronised.
* **tamper [ENUM]**: Indicates if the device has been tampered with.
* **temperature [NUMBER]**: Current temperature (C).
* **water [ENUM]**: Indicates if the sensor is 'dry' or 'wet'.

#### Commands:

The device exposes the following custom commands which can be called from a SmartApp:

* **resetTamper()**: Clear any tamper alerts.

## Version History

#### 2017-03-02: v1.00
  *  Initial version.

## Physical Device Notes

General notes concerning the Fibaro Flood Sensor:

* **Remember to calibrate temperature measurements using parameter #73.** The Fibaro Flood Sensor typically reports temperatures that are ~5°C above the air temperature outside the casing.
* If the device does not send temperature reports with the expected frequency, it is recommended to perform a full reset of the device.
* In hard-wired power mode, the device is active and listening. It will not issue Wake Up notifications or battery reports.
* In battery-powered mode, the device is _sleepy_ and can only be configured after it has woken up.

## References
 Some useful links relevant to the development of this device handler:
* [Fibaro Flood Sensor - Z-Wave certification information](http://products.z-wavealliance.org/products/1036)
* [Fibaro Flood Sensor  - Manual](http://manuals.fibaro.com/flood-sensor/)

## License

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
