# GreenWave PowerNode (Single) (NS210-G-EN)
https://github.com/codersaur/SmartThings/tree/master/devices/greenwave-powernode-single

Copyright (c) [David Lomas](https://github.com/codersaur)

## Overview
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/greenwave-powernode-single/screenshots/gwpn-ss-tiles-main.png" width="200" align="right">
An advanced SmartThings device handler for the GreenWave PowerNode (Single socket) Z-Wave power outlet. Firmware versions 4.23 / 4.28.

### Key features:
* Instantaneous _Power_ and Accumulated _Energy_ reporting.
* _Room Colour_ indicator tile.
* _Blink_ function for easy identification of the physical power outlet.
* Physical and RF protection modes can be configured using the SmartThings GUI.
* _Sync_ tile indicates when all configuration options are successfully synchronised with the physical device.
* _Fault_ tile indicates overload / hardware errors.
* All Z-Wave parameters can be configured using the SmartThings GUI.
* Auto-off timer function.
* Logger functionality enables critical errors and warnings to be saved to the _logMessage_ attribute.
* Extensive inline code comments to support community development.

## Installation

1. Follow [these instructions](https://github.com/codersaur/SmartThings#device-handler-installation-procedure) to install the device handler in the SmartThings IDE.

2. From the SmartThings app on your phone, edit the device settings to suit your installation and hit _Done_.

## Settings

#### General Settings:
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/greenwave-powernode-single/screenshots/gwpn-ss-settings-general.png" width="200" align="right">

* **IDE Live Logging Level**: Set the level of log messages shown in the SmartThings IDE _Live Logging_ tab. For normal operation _Info_ or _Warning_ is recommended, if troubleshooting use _Debug_ or _Trace_.

* **Device Logging Level**: Set the level of log messages that will be recorded in the device's _logMessage_ attribute. This offers a way to review historical messages without having to keep the IDE _Live Logging_ screen open. To prevent excessive events, the maximum level supported is _Warning_.

* **Force Full Sync**: By default, only settings that have been modified will be synchronised with the device. Enable this setting to force all device parameters and association groups to be re-sent to the device.

* **Timer Function (Auto-off)**: Automatically switch off the device after a specified time. Note, this is scheduled in SmartThings and is not a native function of the physical device.

* **Ignore Current Leakage Alarms**: The PowerNode is eager to raise current leakage alarms. Enable this setting to ignore them.

* **ALL ON/ALL OFF Function**: Control the device's response to SWITCH_ALL_SET commands.

#### Device Parameters:
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/greenwave-powernode-single/screenshots/gwpn-ss-settings-params.png" width="200" align="right">

The settings in this section can be used to specify the value of all writable device parameters. It is recommended to consult the manufacturer's manual for a full description of each parameter.

If no value is specified for a parameter, then it will not be synched with the device and the existing value in the device will be preserved.

#### Power Report Threshold:
Determines the percentage change in power consumption that will trigger a report to be sent by the device. **IMPORTANT: Be careful not to set this value too low, as the device will send reports every second causing network congestion!** It is recommended to use a value between 30% and 50%.

#### Keep-Alive Time:
It is recommended to set this setting to 255 minutes to prevent the _Circle LED_ from flashing.

#### State After Power Failure:
Determine the power state to be restored after a power failure. **Only supported with firmware v4.28+**

#### LED for Network Error:
Determine if the LED will indicate network errors. **Only supported with firmware v4.28+**

## GUI

#### Main Tile:
The main tile indicates the switch state. Tap it to toggle the switch on and off.

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/greenwave-powernode-single/screenshots/gwpn-ss-tiles-main.png" width="200">

#### Power and Energy Tiles:
These tiles display the instantaneous power consumption of the device (Watts) and the accumulated energy consumption (KWh). The _Now:_ tile can be tapped to force the device state to be refreshed. The _Since: ..._ tile can be tapped to reset the _Accumulated Energy_ figure.

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/greenwave-powernode-single/screenshots/gwpn-ss-tiles-power-energy.png" width="200">

#### Room Colour Wheel Tile:
This tile mirrors the _Room Colour Wheel_ on the bottom right of the physical power outlet.

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/greenwave-powernode-single/screenshots/gwpn-ss-tiles-colour-wheel-aqua.png" width="100"> <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/greenwave-powernode-single/screenshots/gwpn-ss-tiles-colour-wheel-orange.png" width="100">

#### Blink Tile:
The _Blink_ tile will cause the _Circle LED_ on the outlet to blink for ~20 seconds. This is useful to identify the physical device.

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/greenwave-powernode-single/screenshots/gwpn-ss-tiles-blink.png" width="100">

#### Local Protection Tile:
This tile toggles the _local protection_ state. This can be used to prevent unintentional control (e.g. by a child), by disabling the physical power switch on the device.

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/greenwave-powernode-single/screenshots/gwpn-ss-tiles-lp-unprotected.png" width="100"> <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/greenwave-powernode-single/screenshots/gwpn-ss-tiles-lp-protected.png" width="100">

#### RF Protection Tile:
This tile toggles the _RF protection_ state. Enabling _RF Protection_ means the device will not respond to wireless commands from other Z-Wave devices, including on/off commands issued via the SmartThings app.

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/greenwave-powernode-single/screenshots/gwpn-ss-tiles-rfp-unprotected.png" width="100"> <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/greenwave-powernode-single/screenshots/gwpn-ss-tiles-rfp-protected.png" width="100">

#### Sync Tile:
This tile indicates when all configuration settings have been successfully synchronised with the physical device.

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/greenwave-powernode-single/screenshots/gwpn-ss-tiles-synced.png" width="100"> <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/greenwave-powernode-single/screenshots/gwpn-ss-tiles-sync-pending.png" width="100">

#### Fault Tile:
The _Fault_ tile indicates if the device has reported any faults. These may include load faults, firmware, or hardware issues. Once any faults have been investigated and remediated, the tile can be tapped to clear the fault status.

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/greenwave-powernode-single/screenshots/gwpn-ss-tiles-clear.png" width="100"> <img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/greenwave-powernode-single/screenshots/gwpn-ss-tiles-fault-active.png" width="100">

## SmartApp Integration

#### Attributes:

The device handler publishes the following attributes:

* **switch [ENUM]**: Switch status [_on_, _off_].
* **power [NUMBER]**: Instantaneous power consumption (Watts).
* **energy [NUMBER]**: Accumulated energy consumption (kWh).
* **energyLastReset [STRING]**: Last time _Accumulated Energy_ was reset.
* **fault [STRING]**: Indicates if the device has any faults. '_clear_' if no active faults.
* **localProtectionMode [ENUM]**: Physical protection mode [_unprotected_, _sequence_, _noControl_].
* **rfProtectionMode [ENUM]**: Wireless protection mode [_unprotected_, _noControl_, _noResponse_].
* **logMessage [STRING]**: Important log messages.
* **syncPending [NUMBER]**: The number of configuration items that need to be synced with the physical device. _0_ if the device is fully synchronised.
* **wheelStatus [ENUM]**: Status of the _Room Colour Wheel_ [_black_, _white_, _green_, ...]

#### Commands:

The device exposes the following commands which can be called from a SmartApp:

* **on()**: Turn the switch on.
* **off()**: Turn the switch off.
* **refresh()**: Refresh device state.
* **resetTamper()**: Clear any tamper alerts.
* **blink()**: Causes the Circle LED to blink for ~20 seconds.
* **reset()**: Alias for _resetEnergy()_.
* **resetEnergy()**:  Reset accumulated energy figure to 0.
* **resetFault()**: Reset fault alarm to 'clear'.
* **setLocalProtectionMode()**: Set physical protection mode.
* **toggleLocalProtectionMode()**: Toggle physical protection mode.
* **setRfProtectionMode()**: Set wireless protection mode.
* **toggleRfProtectionMode()**: Toggle wireless protection mode.

## Version History

#### 2017-03-08: v1.01
  *  getParamsMd(): set fwVersion to 4.22, for parameters #0,#1, and #2.

#### 2017-03-05: v1.00
  *  Initial version.

## Physical Device Notes

General notes concerning the GreenWave PowerNode:

* The device is generally poor at reporting physical switch events (reports are typically delayed by 10-20s). To work round the issue, this device handler will request BinarySwitchReports if a meter report indicates that there has been a change in state.
* The device seems to send a lot of Meter Reports. It is important to be cautious setting parameter #0, to avoid spamming the Z-Wave network. Ideally, there should be a parameter to control the _power reporting interval_.
* The device reports _Current Leakage_ alarms frequently, hence this device handler has an option to ignore them.
* There does not appear to be any way (in software) to turn off the white power button LED, so this device isn't great for use in bedrooms as it lights up dark rooms. If anyone has a solution, please let me know!

## References
 Some useful links relevant to the development of this device handler:
* [GreenWave PowerNode - Z-Wave certification information](http://products.z-wavealliance.org/products/629)

## License

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
