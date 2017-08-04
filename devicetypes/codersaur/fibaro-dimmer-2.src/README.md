# Fibaro Dimmer 2 (FGD-212)
https://github.com/codersaur/SmartThings/tree/master/devices/fibaro-dimmer-2

Copyright (c) [David Lomas](https://github.com/codersaur)

## Overview
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-dimmer-2/screenshots/fd2-ss-tiles-on.png" width="200" align="right">
An advanced SmartThings device handler for the Fibaro Dimmer 2 (FGD-212) Z-Wave Dimmer.

### Key features:
* Z-Wave parameters can be configured using the SmartThings GUI.
* Multi-channel device associations can be configured using the SmartThings GUI.
* Child protection modes can be configured using the SmartThings GUI.
* _Fault_ tile indicates burnt-out bulb / overload / hardware errors.
* _Scene_ tile indicates last activated scene.
* _Sync_ tile indicates when all configuration options are successfully synchronised with the physical device.
* Dimmer _level_ range is now 0-100% (instead of 0-99%).
* _Nightmode_ feature allows switch-on brightness level to be controlled on a schedule.
* Logger functionality enables critical errors and warnings to be saved to the _logMessage_ attribute.
* Extensive inline code comments to support community development.

## Installation

1. Follow [these instructions](https://github.com/codersaur/SmartThings#device-handler-installation-procedure) to install the device handler in the SmartThings IDE.

2. **Note for iPhone users**: The _defaultValue_ of inputs (preferences) are commented out to cater for Android users. iPhone users can uncomment these lines if they wish (search for "iPhone" in the code).

3. From the SmartThings app on your phone, edit the device settings to suit your installation and hit _Done_. The first configuration sync may take some time. If the device has not synced after 2 minutes, tap the _sync_ tile to force any remaining configuration items to be synchronised.
**Note, if you are upgrading from an earlier version of this device handler it is still important to review all the settings, as many will not carry over from earlier versions!**

## Settings

#### General Settings:
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-dimmer-2/screenshots/fd2-ss-settings-general.png" width="200" align="right">

* **IDE Live Logging Level**: Set the level of log messages shown in the SmartThings IDE _Live Logging_ tab. For normal operation _Info_ or _Warning_ is recommended, if troubleshooting use _Debug_ or _Trace_.

* **Device Logging Level**: Set the level of log messages that will be recorded in the device's _logMessage_ attribute. This offers a way to review historical messages without having to keep the IDE _Live Logging_ screen open. To prevent excessive events, the maximum level supported is _Warning_.

* **Force Full Sync**: By default, only settings that have been modified will be synchronised with the device. Enable this setting to force all device parameters, association groups, and protection settings to re-sent to the device. This will take several minutes and you may need to press the _sync_ tile a few times before the device is fully synced.

* **Proactively Request Reports**: If you find that device status is slow to update, enabling this setting will cause additional reports to be requested.


#### Child Protection Mode:
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-dimmer-2/screenshots/fd2-ss-settings-protection.png" width="200" align="right">

The Fibaro Dimmer 2 supports the Z-wave Protection Command Class. This allows the device to be protected from unintentional control (e.g. by a child) by disabling the physical switches and/or RF control.

* **Local Protection**: Setting this option to _No operation possible_ will disable both physical switches (S1/S2).

* **RF Protection**: Setting this option to _No RF control_ will prevent z-wave commands from altering the device state. This includes commands received from the hub as well as other associated devices.


#### Nightmode:
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-dimmer-2/screenshots/fd2-ss-settings-nightmode.png" width="200" align="right">

The _Nightmode_ feature forces the dimmer to switch on at a specified level. (Behind the scenes, this feature is updating Paramter #19). _Nightmode_ can be manually enabled/disabled using the _Nightmode_ tile and can also be scheduled using the settings here.

* **Nightmode Level**: The dimmer will always switch on at this level when _Nightmode_ is enabled.

* **Force Nightmode**: If the dimmer is on when _Nightmode_ is enabled, the _Nightmode Level_ is applied immediately (otherwise it's only applied next time the dimmer is switched on). Similarly, if the dimmer is on when _Nightmode_ is disabled, the brightness level will immediately be returned to the state prior to _Nightmode_ being enabled.

* **Nightmode Start Time**: _Nightmode_ will be enabled every day at this time.

* **Nightmode Stop Time**: _Nightmode_ will be disabled every day at this time.

If _Nightmode_ Start and Stop times are set here, they will only apply to the corresponding instance of the device. If you want to implement a _Nightmode_ schedule for multiple devices it is possible to write a simple SmartApp (or use CoRE) to  call the _enableNightmode()_ and _disableNightmode()_ commands on each device.

#### Device Parameters:
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-dimmer-2/screenshots/fd2-ss-settings-params.png" width="200" align="right">

The settings in this section can be used to specify the value of all writable device parameters. It is recommended to consult the [manufacturer's manual](http://manuals.fibaro.com/dimmer-2/) for a full description of each parameter.

If no value is specified for a parameter, then it will not be synched with the device and the existing value in the device will be preserved.

##### Auto-calibration:
If parameter #13 is used to force auto-calibration of the device, any values that are specified for parameters #1, #2, and #30 will be ignored. After hitting _Done_, monitor the _Live Logging_ tab in the IDE to discover what the new auto-calibrated values are (the auto-calibrated values will not be updated in the device's settings screen due to limitations of the SmartThings platform).

Next time device settings are updated, remember to set parameter #13 back to _0: Readout_ if you do not want auto-calibration to be forced again. Additionally, review parameters #1, #2, and #30, as any values specified will over-write the auto-calibrated values.

##### Read-only Parameters:
The Fibaro Dimmer 2 has a few read-only parameters that are not shown in this section. The dimmer will periodically report the values of these read-only parameters to the hub, and their values can be seen in the _Live Logging_ tab in the IDE.

#### Multi-channel Device Associations:
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-dimmer-2/screenshots/fd2-ss-settings-assocgroups.png" width="200" align="right">

The Fibaro Dimmer 2 supports _Multi-channel_ Device Associations. This allows the physical switches connected to a Fibaro Dimmer 2 to send z-wave commands directly to groups of other devices (e.g. other dimmers or relays), without the commands being processed by the SmartThings hub. This results in faster response times compared to using a SmartApp for example.

The Fibaro Dimmer 2 supports four association groups:

- **Association Group #2**: Sends on/off commands (BASIC_SET) when Switch #1 (S1) is used.
- **Association Group #3**: Sends dim/brighten commands (SWITCH_MUTLILEVEL_SET) when Switch #1 (S1) is used.
- **Association Group #4**: Sends on/off commands (BASIC_SET) when Switch #2 (S2) is used.
- **Association Group #5**: Sends dim/brighten commands (SWITCH_MUTLILEVEL_SET) when Switch #2 (S2) is used.

The members of each _Association Group_ must be defined as a comma-delimited list of target nodes. Each target device can be specified in one of two ways:
- _Node_: A single hexadecimal number (e.g. "0C") representing the target _Device Network ID_.
- _Endpoint_: A pair of hexadecimal numbers separated by a colon (e.g. "10:1") that represent the target _Device Network ID_ and _Endpoint ID_ respectively. For devices that support multiple endpoints, this allows a specific endpoint to be targeted by the association group.

You can find the _Device Network ID_ for all Z-Wave devices in your SmartThings network from the _My Devices_ tab in the SmartThings IDE. Consult the relevant manufacturer's manual for information about the endpoints supported by a particular target device.

## GUI

#### Power and Energy Tiles:
<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-dimmer-2/screenshots/fd2-ss-tiles-power-energy.png" width="200" align="right">

These tiles display the instantaneous power consumption of the device (Watts) and the accumulated energy consumption (KWh). The _Now:_ tile can be tapped to force the device state to be refreshed. The _Since: ..._ tile can be tapped to reset the _Accumulated Energy_ figure.


#### Nightmode Tile:
This tile can be used to toggle (enable/disable) _Nightmode_.

#### Scene Tile:
If parameter #28 (Scene Activation) has been enabled, then the Fibaro Dimmer 2 will send SCENE_ACTIVATION_SET commands to the SmartThings hub. This tile will indicate the ID of the last-activated scene.

#### Sync Tile:
This tile indicates when all configuation settings have been successfully synchronised with the physical device. If the tile remains in the orange SYNC PENDING state, tap it to force any remaining unsynced items to be sent to the device.

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-dimmer-2/screenshots/fd2-ss-tiles-sync.png" width="200">

#### Fault Tile:
This tile indicates if the device has reported any faults. These may include burnt-out-bulbs (load error), overload, low voltage, surge, temperature warnings, firmware, or hardware issues. Once any faults have been investigated and remediated, the tile can be tapped to clear the fault status.

<img src="https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/fibaro-dimmer-2/screenshots/fd2-ss-tiles-fault.png" width="200">

## SmartApp Integration

#### Attributes:

The device handler has the following attributes:

* **switch [ENUM]**: The switch State, 'On' or 'Off'.
* **level [NUMBER]**: The current light level (0-100%).
* **power [NUMBER]**: The current instantaneous power usage (Watts).
* **energy [NUMBER]**: The Accumulated energy consumption (KWh).
* **energyLastReset**: Last time that the _Accumulated Energy_ figure was reset.
* **scene [NUMBER]**: ID of last-activated scene.
* **nightmode [ENUM]**: Indicates if _Nightmode_ is 'Enabled' or 'Disabled'.
* **syncPending [NUMBER]**: The number of configuration items that need to be synced with the physical device. _0_ if the device is fully synchronised.
* **fault [ENUM]**: Indicates if the device has any faults. '_clear_' if there are no active faults.
* **logMessage [STRING]**: Important log messages.

#### Commands:

The device exposes the following custom commands which can be called from a SmartApp:

* **enableNightmode(level)**: Enable _Nightmode_. The optional level parameter will override the _Nightmode Level_.
* **disableNightmode()**: Disable _Nightmode_.
* **toggleNightmode()**: Toggle _Nightmode_.
* **clearFault()**: Clear any active faults.
* **reset()**: Alias for _resetEnergy()_.
* **resetEnergy()**: Reset the _Accumulated Energy_ figure back to _0_.
* **sync()**: Trigger device synchronisation.

## Version History

#### 2017-02-27: v2.02:
 * Fixed backgroundColor for fault tile.

#### 2017-02-25: v2.01:
  * Preferences: defaultValues are commented out by default to cater for Android users. iPhone users can uncomment these lines if they wish (search for "iPhone").
  * updated(): Fix to allow device to sync after a forced auto-calibration.
  * updateSyncPending(): If a target value is null, then it does not need syncing.

#### 2017-02-24: v2.00
 * Complete re-write in-line with new coding standards.
 * General Behaviour Changes:
  *     Dimmer level now reverts to zero when switched off.
  *     Dimmer level range is now 0-100%.
  *     Fewer report requests are made, as the Fibaro Dimmer 2 is good at sending back reports anyway.
  *     Nightmode scheduling fixed after change in the behaviour of _schedule()_.
 *   Capabilities:
  *     Added "Light" capability.
  *     Added unofficial "Fault" capability. [attributes: 'fault', commands: clearFault()]
  *     Added unofficial "Logging" capability. [attributes: 'logMessage']
  *     Added unofficial "Scene Controller" capability. [attributes: 'scene']
  *     Removed "Configuration" capability and configure() command, as not used.
 *   Attributes:
  *     energyLastReset: renamed from lastReset.
  *     logMessage: Critical error and warning log messages.
  *     syncPending: Number of items that need to be synced with the physical device.
  *     fault: Indicates if the device has any faults (load, surge, overload, overCurrent, voltage, temperature, hardware, firmware). 'clear' if no active faults.
 *   Commands:
  *     resetEnergy(): Resets accumulated energy figure.
  *     clearFault(): Clears any active faults.
 *   Fingerprints: Updated to use new Z-Wave fingerprint format.
 *   Tiles:
  *     level: range is now 0-100%
  *     scene: Indicates last activated scene.
  *     syncPending: Shows when device configuration is synced.
  *     fault: Indicates device faults.
 *   Settings/Preferences:
  *     Proactive Requests.
  *     IDE Live Logging Level
  *     Device Logging Level
  *     Association Group members can be configured from the Settings GUI, including multi-channel endpoint destinations.
  *     Protection Options can be set for local (physical switches) and RF Control, to prevent unintentional changes.
 *   zwaveEvent():
  *     zwaveEvent(CONFIGURATION_REPORT): Uses new scaledConfigurationValue attribute.
  *     zwaveEvent(POWERLEVEL_REPORT): New handler for powerlevel reports.
  *     zwaveEvent(COMMAND_CLASS_SWITCH_BINARY): Removed as it doesn't appear to be supported by the device.
  *     zwaveEvent(ASSOCIATION_REPORT): New handlers for both normal and multi-channel association reports.
  *   dimmerEvent(): Various optimisations and fixes.
 *   update():
  *     Added a check to prevent double execution.
  *     Requests Firmware Metadata, Manufacturer-specific, and Version reports.
 *   New custom commands:
  *     clearFault(): Clears any active fault.
  *     resetEnergy(): Reset the Accumulated Energy figure in the device.
 *   New private helper functions:
  *     logger(): Wrapper function for all logging: Logs events to IDE Live Logging, and also by raising logMessage events. Configured using configLoggingLevelIDE and configLoggingLevelDevice preferences.
  *     sync(): Manages synchronisation of all parameters and association groups with the physical device. The syncPending attribute advertises remaining number of sync operations.
  *     refreshConfig(): Requests all configuration, association group reports.
  *     sendSecureSequence(): Secure an array of commands and send them using sendHubCommand.
  *     Additional functions to dynamically build the parameters and association groups preferences.
 *   New Metadata Funtions:
  *     getCommandClassVersions(): Returns supported command class versions.
  *     getParamsMd(): Returns device parameters metadata (including read-only parameters).
  *     getAssocGroupsMd(): Returns association groups metadata.

#### 2016-10-31: v1.03
  *  Added event handlers for Crc16Encap, SensorMultilevelReport, ManufacturerSpecificReport, VersionReport, and FirmwareMdReport.

#### 2016-10-24: v1.02
  *  Increased delay between ConfigurationSet commands to 500ms to improve reliability of sending parameters.

#### 2016-10-11: v1.01
  *   Added Nightmode functionality.
  *   dimmerEvents(): Fixed MeterGet requests after a switch or level state change.
  *   on(), off(), setLevel(): Delayed switchMultilevelGet() requests by 8s as early requests generate erroneous data. The dimmer will send a correct report once it has completed the request anyway (which is usually sooner than 8s).
  *   zwaveEvent(_MeterReport_): Removed rounding of power values. Also, Energy and power values are stored as dispEnergy and dispPower to work-around UI formatting issue.
  *   Settings/Preferences: Fixed param24/25/27 to allow combination of options.
  *   Simplified fingerprint.

#### 2016-10-05: v1.00
  *  Initial version based on device handler by hajar97.
  *  Tiles: Added GetConfig button to retrieve the current device settings (which are displayed in the debug log).

## To Do
 *   Optimise zwaveEvent(CRC_16_ENCAP) by using _ecapsulatedCommand()_, once implemenation has been fixed by SmartThings.
 *   Allow protection state to be controlled via commands (maybe just the local). This would allow a smartApp to disable all physical light switches, perhaps on a schedule, for example. E.g. stop children turning on the lights after 10PM. Similar to Nightmode.
 *   Add _Button_ capability and raise _button_ events.

## Physical Device Notes

General notes relating to the Fibaro Dimmer 2:

* The device has three read-only parameters. These are not shown in the settings GUI, but their values will be reported in the Live Logging tab of the IDE when Configuration Reports are received.

## References
 Some useful links relevant to the development of this device handler:
* [Fibaro Dimmer 2 - Z-Wave certification information](http://products.z-wavealliance.org/products/1729)
* [Fibaro Dimmer 2 - Manual](http://manuals.fibaro.com/dimmer-2/)

## License

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
