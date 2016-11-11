# Fibaro RGBW Controller (FGRGBWM-441)
https://github.com/codersaur/SmartThings/tree/master/devices/Fibaro%20RGBW%20Controller%20(FGRGBWM-441)

Copyright (c) 2016 [David Lomas](https://github.com/codersaur)

## Overview

This SmartThings device handler is written for the Fibaro RGBW Controller (FGRGBWM-441). It extends the native SmartThings device handler to support editing the device's parameters from the SmartThings GUI, and to support the use of one or more of the controller's channels in IN/OUT mode.

### Key features:
* Physical device parameters can be edited from the Smartthings GUI, and verified in the IDE Log.
* Channels can be mapped to different colours without needing to physically rewire the device.
* `setColor()` supports a wide range of colorMap key combinations:
 * `red:`, `green:`, `blue:`, `white:`
 * `red:`, `green:`, `blue:`
 * `hue:`, `saturation:`, `level:`
 * `hex:`
 * `name:`
* Multiple options for the calculation of aggregate `switch` and `level` attributes (useful when using INPUTS).
* Implements "Energy Meter", "Power Meter", and "Polling" capabilities.

### Screenshots:

![Color Shortcuts](https://raw.githubusercontent.com/codersaur/SmartThings/master/devices/Fibaro%20RGBW%20Controller%20(FGRGBWM-441)/screenshots/screenshot_color_shortcuts.png)

## Installation
To install this device handler:

1. Follow [these instructions](https://github.com/codersaur/SmartThings#device-handler-installation-procedure) to install the device handler in the SmartThings IDE.

2. Edit the device handler code to suit your needs. Specifically, the tiles section will need to be customised to suit the channel configuration (see the use cases below).

3. Configure your device instance to use the device handler, then edit the device settings in the SmartThings GUI.

#### Example Use Cases
###### A four-channel RGBW LED strip:
...

###### A three-channel RGB LED strip, plus a 0-10V analog sensor input:
For this use case, it is recommended to use Channel #1 as Red, Channel #2 as Green, Channel #3 as Blue, and Channel #4 as the analog input.

In the device handler code, you will want to comment out the tiles for ...

In the device settings you will want to configure as follows:

###### Two single-channel output loads, and two 0-10V analog sensor inputs:
...

###### Four 0-10V analog sensor inputs:
...

## Physical Device Notes:
Some general notes relating to the Fibaro RGBW Controller:

* Parameter #14 is used to control the mode of each channel. When editing keep in mind:
 * If using RGBW modes, all channels must have exactly the same mode. Mixing RGBW channels with IN/OUT channels at the same time will cause IN channels to report incorrect levels (the INPUT is treated as a switch input for the RGBW mode).
  * If you want to use one or more channels as analog inputs, then the remaining channels must be set to OUT mode.
  * If using IN/OUT channel modes, the OUT channels can still be mapped to colours, but the built-in "RGBW programs" will have no effect.
  * switchColorSet commands do not affect INPUT channels, but you can't use switchColorGet to get the level of an INPUT channel either.
  * Energy and power reports for individual channels are not available, only the aggregate device as a whole.

I have discovered two potential bugs in firmware 25.25:

* BUG: If the device's parameters are changed, the device may stop responding to many Z-Wave _Get_ commands. **It is therefore recommended to power-cycle the device after changing parameters.**
* BUG: If a basicSet or switchMultilevelSet command is issued to channel 0 or to an INPUT channel, then the levels of all INPUT channels may be incorrectly reported as zero. Incorrect reports will persist until there is a change to the input voltages that is greater than the 'input change threshold' defined by Paramter #43. To avoid this issue, this device handler does not send basicSet or switchMultilevelSet commands to channels in INPUT mode.

## Version History:

#### 2016-11-08: v0.01
 * Added support for channels in IN/OUT modes.
 * Physical device parameters can be changed from the Smartthings GUI, and verified in the IDE Log.
 * Added event handlers for: MeterReport, SwitchColorReport, AssociationReport.
 * `reset()` resets the accumulated energy usage, not the brightness.
 * Added three options for the calculation of aggregate `switch` and `level` attributes (IN Only / OUT Only / ALL).
 * Added support for channel mappings and thresholds.
 * `on()/off()`: Only sends commands to OUT channels, to avoid changing levels of INPUTS.
 * `setLevel()`: Added two modes for setting levels (SIMPLE / SCALE).
 * `setColor()`: Supports colorMaps with red/green/blue/white, hue/saturation/level, hex, or name keys.
 * `updated()`: Validates settings for parameter #14 and generates a warning if there's a mixture of RGBW and IN/OUT.
 * `getSupportedCommands()`: New method to encapsulate a map of the command class versions supported by the device.
 * `color` attribute is now a map [hue: x, saturation: y, ...], as per SmartThings Capabilities Reference.
 * `level` attributes and sliders now have a have range of 0-100 percent, instead of 0-99.
 * Added `activeProgram` attribute to support Program tiles.
 * Added `colorName` attribute to support Color Shortcut tiles.
 * Added Test tile and ability to interrogate current device parameters.
 * Added Polling capability, which polls all channels, plus energy and power.
 * `configure()`: Added workaround for bug in configurationV1.configurationSet().

 ## To Do:
 * When sending commands, consider Parameter #42 (reporting). If the device is configured not to send reports, or to only send reports generated by inputs (and not the controller), then a request for the appropriate report must be issued (or call refresh()) a couple of seconds after the command has been issued.
 * Add new device preference "Update ST UI before sending commands": This will issue fake events to make the GUI more responsive when using long dimming durations.
 * Allow Association Group Members to be edited from the SmartThings GUI, via device preferences.
 * Consider if `setLevel()` in SCALE mode should really be moved to new `setBrightness()` command. Slider on the multiTile can then be linked to either setLevel or setBrightness via settings.

## References
 Some useful links relevant to the development of this device handler:
* Fibaro RGBW Controller Z-Wave certification information: (http://products.z-wavealliance.org/products/1054)
* Color Control Capability: https://community.smartthings.com/t/capability-color-control-color-attribute-command-ambiguous-incorrectly-documented-and-or-improperly-used-in-dths/58018/23
* Using the Switch Color Command Class: https://community.smartthings.com/t/color-switch-z-wave-command-class/19300
* RGB-RGBW colorMap conversion: http://stackoverflow.com/questions/21117842/converting-an-rgbw-color-to-a-standard-rgb-hsb-rappresentation

## License

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
