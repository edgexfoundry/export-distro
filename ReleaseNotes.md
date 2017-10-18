# v0.2 (10/20/2017)
# Release Notes

## Notable Changes
The Barcelona Release (v 0.2) of the Export Distribution micro service includes the following:
* POM changes for appropriate repository information for distribution/repos management, checkstyle plugins, etc.
* Added Dockerfile for creation of micro service targeted for ARM64 
* Added ability to distribute EdgeX data to Google IoT Core 
* Added ability to distribute EdgeX data to MQTT topic by device id
* Added ability to distribute EdgeX data to MQTTS endpoint

## Bug Fixes
* Removed OS specific file path for logging file 
* Fixed ZMQ socket multi-threading issue
* Fix of SerializedFormatTransformerTest

## Pull Request/Commit Details
 - [#19](https://github.com/edgexfoundry/export-distro/pull/19) - Remove staging plugin contributed by Jeremy Phelps ([JPWKU](https://github.com/JPWKU))
 - [#18](https://github.com/edgexfoundry/export-distro/pull/18) - Initial implementation contributed by ([Dunstable](https://github.com/Dunstable))
 - [#17](https://github.com/edgexfoundry/export-distro/pull/17) - Add path or device id to MQTT topic contributed by Darko Draskovic ([darkodraskovic](https://github.com/darkodraskovic))
 - [#16](https://github.com/edgexfoundry/export-distro/pull/16) - Add path or device id to MQTT topic contributed by Darko Draskovic ([darkodraskovic](https://github.com/darkodraskovic))
 - [#15](https://github.com/edgexfoundry/export-distro/pull/15) - Fixes Maven artifact dependency path contributed by Tyler Cox ([trcox](https://github.com/trcox))
 - [#14](https://github.com/edgexfoundry/export-distro/pull/14) - added staging and snapshots repos to pom along with nexus staging mav… contributed by Jim White ([jpwhitemn](https://github.com/jpwhitemn))
 - [#13](https://github.com/edgexfoundry/export-distro/pull/13) - fixed unit tests contributed by Bernard Van Haecke ([bhaecke](https://github.com/bhaecke))
 - [#12](https://github.com/edgexfoundry/export-distro/pull/12) - removal of breaking unit test - contacting developer to fix contributed by Jim White ([jpwhitemn](https://github.com/jpwhitemn))
 - [#11](https://github.com/edgexfoundry/export-distro/pull/11) - Add aarch64 docker file contributed by ([feclare](https://github.com/feclare))
 - [#10](https://github.com/edgexfoundry/export-distro/pull/10) - Fixes #9. Fix test that broke with change of command serializer change contributed by Jim White ([jpwhitemn](https://github.com/jpwhitemn))
 - [#9](https://github.com/edgexfoundry/export-distro/issues/9) - org.edgexfoundry.transformer.SerializedFormatTransformerTest testTransform test is failing. +fix
 - [#8](https://github.com/edgexfoundry/export-distro/pull/8) - updated pom for nexus repos and google checkstyles contributed by Jim White ([jpwhitemn](https://github.com/jpwhitemn))
 - [#7](https://github.com/edgexfoundry/export-distro/pull/7) - Adds Docker build capability contributed by Tyler Cox ([trcox](https://github.com/trcox))
 - [#6](https://github.com/edgexfoundry/export-distro/pull/6) - added support for Google IoT Core telemetry contributed by Bernard Van Haecke ([bhaecke](https://github.com/bhaecke))
 - [#5](https://github.com/edgexfoundry/export-distro/pull/5) - Fixes Log File Path contributed by Tyler Cox ([trcox](https://github.com/trcox))
 - [#4](https://github.com/edgexfoundry/export-distro/issues/4) - Log File Path not Platform agnostic
 - [#3](https://github.com/edgexfoundry/export-distro/pull/3) - Add distributionManagement for artifact storage contributed by Andrew Grimberg ([tykeal](https://github.com/tykeal))
 - [#2](https://github.com/edgexfoundry/export-distro/pull/2) - Fixes ZeroMQ Socket Multi-Threading contributed by Tyler Cox ([trcox](https://github.com/trcox))
 - [#1](https://github.com/edgexfoundry/export-distro/pull/1) - Contributed Project Fuse source code contributed by Tyler Cox ([trcox](https://github.com/trcox))

