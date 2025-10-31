# map_to_kml

Java application for the visualisation of dashcam recording files with extensions ".map" or ".nmea" as KML file.

Sample command line:

```
java -cp $CLASS_PATH \
  map_to_kml.MainApp \
  -x ConvertMapToKml.xml \
  -Dlog4j.configurationFile=log4j2.xml -ea -DDIR=${folder_prompt}
```

Notes:
1. Application constructed with Spring Framework XML.
2. At initialisation, the class `map_to_kml.util.TimeZoneLookup` will load time zone polygons from a GeoJson Zip file, available from https://github.com/evansiroky/timezone-boundary-builder. If the Zip file is not present, the default TimeZone of the Java virtual machine will be used.

