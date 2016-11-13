# SmartThings
https://github.com/codersaur/SmartThings

Copyright (c) 2016 [David Lomas](https://github.com/codersaur)

## Overview

This repository contains device handlers and SmartApps for use with Samsung's [SmartThings](http://www.smartthings.com) home automation platform.

## SmartApps

* [Evohome (Connect) - BETA](https://github.com/codersaur/SmartThings/tree/master/smartapps/Evohome):
 - This SmartApp connects your Honeywell Evohome System to SmartThings.
 - Note, the Evohome Heating Zone device handler (below) must also be installed.

* [InfluxDB Logger](https://github.com/codersaur/SmartThings/tree/master/smartapps/InfluxDB%20Logger):
 - This SmartApp logs device states to an [InfluxDB](https://influxdata.com/) database.

### SmartApp Installation Procedure

1. Within the SmartThings IDE, click '*My SmartApps*', then '*+ New SmartApp*'. 
2. Select the '*From Code*' tab and paste in the contents of the relevant groovy file.
3. Click '*Create*', and then '*Publish*' *(For Me)*.
4. Now using the SmartThings smartphone app, navigate to the '*Marketplace*', select '*SmartApps*', then browse to '*My Apps*' at the bottom of the list. You should see the new SmartApp available for installation.
5. Select the new SmartApp, complete the configuraiton options and press '*Done*'.

  
## Device Handlers

* [Aeon Home Energy Meter (GEN2 - UK - 1 Clamp)](https://github.com/codersaur/SmartThings/tree/master/devices/Aeon%20Home%20Energy%20Meter%20(GEN2%20-%20UK%20-%201%20Clamp)):
 - This device handler is written specifically for the Aeon Home Energy Meter Gen2 UK version, with a single clamp.
 - It supports live reporting of energy, power, current, and voltage, as well as energy and cost statistics over multiple pre-defined periods.

* [Evohome Heating Zone - BETA](https://github.com/codersaur/SmartThings/tree/master/devices/Evohome):
 - This device handler is required for the Evohome (Connect) SmartApp.

* [Fibaro Dimmer 2 (FGD-212)](https://github.com/codersaur/SmartThings/tree/master/devices/Fibaro%20Dimmer%202%20(FGD-212)):
 - This device handler is written specifically for the Fibaro Dimmer 2 (FGD-212).
 - It extends hajar97's original device handler to suport Nightmode and fixes a number of issues.
 - Nightmode function: Nightmode forces the dimmer to switch on at a specific level (e.g. low-level during the night). It can be enabled/disabled manually using the new Nightmode tile, and/or scheduled from the device settings.
 
* [Fibaro RGBW Controller (FGRGBWM-441)](https://github.com/codersaur/SmartThings/tree/master/devices/Fibaro%20RGBW%20Controller%20(FGRGBWM-441)):
 - This device handler is written specifically for the Fibaro RGBW Controller (FGRGBWM-441).
 - It extends the native SmartThings device handler to support editing the device's parameters from the SmartThings GUI, and to support the use of one or more of the controller's channels in IN/OUT mode (i.e. analog sensor inputs).
 
* [Philio Dual Relay (PAN04)](https://github.com/codersaur/SmartThings/tree/master/devices/Philio%20Dual%20Relay%20(PAN04)):
 - This device handler is written specifically for the Philio Dual Relay (PAN04), when used as a single switch/relay only.
 - It supports live reporting of energy, power, current, voltage, and power factor,  as well as energy and cost statistics over multiple pre-defined periods.
 
* [TKB Metering Switch (TZ88E-GEN5)](https://github.com/codersaur/SmartThings/tree/master/devices/TKB%20Metering%20Switch%20(TZ88E-GEN5)):
 - This device handler is written specifically for the TKB Metering Switch (TZ88E-GEN5).
 - It supports live reporting of energy, power, current, voltage, and power factor,  as well as energy and cost statistics over multiple pre-defined periods.
 
### Device Handler Installation Procedure

1. Within the SmartThings IDE, click on '*My Device Handlers*' at the top, then the '*+ Create New Device Handler*' button. 
2. Select the '*From Code*' tab and paste in the contents of the relevant groovy file.
3. Click '*Create*', then '*Publish*' *(For Me)*.

When you add new devices, SmartThings will automatically select the device handler with the closest-matching *fingerprint*. However, this process is not perfect and it often fails to select the desired device handler. You may also have pre-existing devices that you want to have use the new device handler. In these cases, you need to change the device type of each device instance from the IDE:

4. Within the SmartThings IDE, click on '*My Devices*' at the top to list all your devices.
5. Click on the appropriate device to bring up its properties, then click the '*Edit*' button at the bottom.
6. Change the '*Type*' using the drop-down box (custom devices will be near the bottom of the list).
7. Hit the '*Update*' button at the bottom.
8. IMPORTANT: In the SmartThings app on your phone, navigate to the device (you should the GUI has updated to reflect the new tiles configuraiton). Press the gear icon to edit the device's settings and review each setting to ensure it has a suitable value, then press '*Done*'. (This will trigger the update() command and ensure the device instance is fully configured for use with the new device handler).

## License


Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
in compliance with the License. You may obtain a copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
for the specific language governing permissions and limitations under the License.
