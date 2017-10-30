# Preface #

This document describes the functionality provided by the xlr-remote-file-plugin.

See the **[XL Release Reference Manual](https://docs.xebialabs.com/xl-release/)** for background information on XL Release and release concepts.

# Overview #

The xlr-remote-file-plugin is a XL Release plugin provides a task which read file on remote system. This plugin provide facility to read remote file contents or configuration/properties stored in `xml`, `json`, `ini`, `yaml` or `properties` files.
For `XML` file type the path expression to select the value is `XPATH`, for `Properties` it is the `property name`. For all other file type it is `JSONPATH`.
