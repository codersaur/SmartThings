# InfluxDB Logger

Copyright (c) [David Lomas](https://github.com/codersaur)

## Overview

This SmartApp logs SmartThings device attributes to an [InfluxDB](https://influxdata.com/) database.

### Key features:
* Changes to device attributes are immediately logged to InfluxDB.
* The _Soft-Polling_ feature forces attribute values to be written to the database periodically, even if values haven't changed.
* Logs Location _Mode_ events.
* Supports an InfluxDB instance on the local LAN, without needing to route traffic via the cloud.
* Supports Basic Authentication to InfluxDB database.

## Installation
Follow [these instructions](https://github.com/codersaur/SmartThings#smartapp-installation-procedure) to install the SmartApp in the SmartThings IDE. However, before publishing the code in the IDE, edit the _getGroupName()_ command (at the bottom of the code) to add the Group IDs for your SmartThings instance. These can be found from the _'My Locations'_ tab in the SmartThings IDE.

For more information about installing InfluxDB, Grafana, and this SmartApp, [see this guide](http://codersaur.com/2016/04/smartthings-data-visualisation-using-influxdb-and-grafana/).

## Usage
SmartApp settings:

* **InfluxDB Database**: Specify your InfluxDB instance details in this section.
* **Polling**: Configure the _Soft-Polling_ interval. All device attribute values will be written to the database at least once per interval. This is useful to ensure attribute values are written to the database, even when they have not changed. Set to zero to disable.
* **System Monitoring**: Configure which location and hub attributes are logged.
* **Devices to Monitor**: Specify which device attributes to monitor.

## Version History

#### 2017-04-03: v1.11
 * Supports Basic HTTP Authentication.
 * logger(): Wrapper for all logging.
 * softPoll(): checks that attribute values are != null.
 * postToInfluxDB(): Added callback option to the HubAction object.
 * handleInfluxResponse(): New callback function. Handles response from posts made in postToInfluxDB() and logs errors.
 * updated(): Removed custom attributes for EnergyMeters.
 
#### 2017-01-30: v1.10
 * Fixed typo in postToInfluxDB().

#### 2016-11-27: v1.09
 * Added support for more capabilities:
  * Shock Sensors (capability.shockSensor)
  * Signal Strength Meters (capability.signalStrength)
  * Sound Sensors (capability.soundSensor)
  * Tamper Alerts (capability.tamperAlert)
  * Window Shades (capability.windowShade)

#### 2016-11-27: v1.08
 * Added support for Sound Pressure Level Sensors (capability.soundPressureLevel).

#### 2016-10-30: v1.07
 * Added support for:
  * Buttons (capability.button)
  * Carbon Dioxide Detectors (capability.carbonDioxideMeasurement)
  * Consumables (capability.consumable)
  * pH Meters (capability.pHMeasurement)
  * Pressure Sensors (non-standard capability: capability.sensor)
  * Touch Sensors (capability.touch)
  * UV Meters (capability.ultravioletIndex)
  * Voltage Meters (capability.voltageMeasurement)
 * Added support for logging SmartThings Mode changes. [Measurement name: _stMode]
 * Added support for logging SmartThings Location properties (e.g. mode and timeZone) [Measurement name: _stLocation]
 * Added support for logging SmartThings Hub properties (e.g. uptime and firmware version). [Measurement name: _stHub]
 * _handleEvent()_: All device measurements now include groupId, groupName, hubId, hubName, locationId, and locationName as tags.
 * _handleEvent()_: ThreeAxis measurements are split into valueX, valueY, valueZ fields.

#### 2016-09-06: v1.06
 * _escapeStringForInfluxDB()_: Added substitution of apostrophes (uncomment to use).

#### 2016-04-04: v1.05
 * Added subscription to _'scheduledSetpoint'_, _'optimisation'_, and _'windowFunction'_ custom attributes for Evohome thermostats.
 * Added handling of many new string value events.
 * Added a catch-all for any events with string values.

#### 2016-03-22: v1.04
 * Added subscription to _'thermostatSetpointMode'_ custom attribute for Evohome thermostats.

#### 2016-03-10: v1.03
 * Device subscriptions now auto-generated from _state.deviceAttributes_.
 * Soft-polling auto-generated from _state.deviceAttributes_.
 * Better escaping of characters.

#### 2016-03-02: v1.02
 * _softpoll_ automatically sends values to InfluxDB, to give enough points for Grafana to display.
 * switch events now have _value_ and _valueBinary_ fields.

#### 2016-02-29: v1.01
 * Expanded range of device types supported.
 * Uses a generic event handler for all subscriptions.
 * Sends the following tags: device, group, unit.
 * Event.name now maps to the 'measurement' name.
 * Headers and path are stored as state (to avoid recalculating on every event).

#### 2016-02-28: v1.00
 * Initial Version.

## References
 Some useful links relevant to the development of this SmartApp:
* [SmartThings Capabilities Reference](http://docs.smartthings.com/en/latest/capabilities-reference.html)
* [InfluxDB Documentation](https://docs.influxdata.com/influxdb/)
* [Codersaur.com - SmartThings Data Visualisation using InfluxDB and Grafana](http://codersaur.com/2016/04/smartthings-data-visualisation-using-influxdb-and-grafana/)

## License

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
