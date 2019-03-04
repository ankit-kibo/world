package com.kibo.pegateway.dto.base;

import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * Implement this interface if you want
 * your overrided class to post-process
 * maps.
 */
public interface IHasMap {
    /**
     * Helper method to get a value from a map.
     * @param map The map from which to get the value.
     * @param name The name to use to get the value.
     * @param required If true, throws an exception if the value is null or non-String.
     * @param mapName The map name to use in the exception text.
     * @return A String containing the value found.
     * @throws Exception Thrown if required is true and the value is non-String or empty.
     */
    public static String getStringFromMap(Map<String, Object> map, String name, boolean required, String mapName) throws Exception {
        Object o = map.get(name);
        if (o == null || !(o instanceof String) || StringUtils.isEmpty((String) o)) {
            if (required) {
                throw new Exception("Invalid '" + name + "' object in '" + mapName + "' in adapter context: '" + o + "'.");
            }
        }
        return (String) o;
    }

    /**
     * Helper method to add Strings to a map.
     * @param name The name to use to add the string.
     * @param value The value to add.
     * @param map The map to which to add the value.
     * @param mapName
     */
    public static void addStringToMap(String name, String value, Map<String, Object> map) {
        map.put(name, value);
    }

    /**
     * Helper method to get a Long value from the map.
     * @param map The map to use.
     * @param name The name to get from the map.
     * @param required Throw an exception if the value is missing or not a Long.
     * @param mapName The name to use in the exception text if thrown.
     * @return Returns a Long value if found.
     * @throws Exception Thrown if required is true and the value is missing or not a Long.
     */
    public static Long getLongFromMap(Map<String, Object> map, String name, boolean required, String mapName) throws Exception {
        Object o = map.get(name);
        if (o == null) {
            if (required) {
                throw new Exception("Missing '" + name + "' from '" + mapName + "'.");
            }
            else {
                return null;
            }
        }
        if(o instanceof Long)
            return (Long)o;
        try {
            return Long.valueOf(o.toString());
        } catch (Exception ex) {
            throw new Exception("Invalid non-numeric '"+name+"' from '"+mapName+"'.");
        }
    }

    /**
     * Implement this method in your class to set your properties.
     *
     * Make sure your map is not null first.
     * @throws Exception Throw an exception if required elements are missing.
     */
    public void setPropertiesFromMap() throws Exception;

    /**
     * Implement this method to add the properties to the map.
     */
    public void addPropertiesToMap();
}
