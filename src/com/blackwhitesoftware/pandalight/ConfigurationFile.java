package com.blackwhitesoftware.pandalight;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Properties;
import java.util.Vector;

/**
 * Class for supporting the serialisation and deserialisation of settings.
 */
public class ConfigurationFile {

    /**
     * Temporary storage of the configuration
     */
    private final Properties mProps = new Properties();

    /**
     * Loads the configuration from the given filename into this {@link ConfigurationFile}
     *
     * @param pFilename The absolute filename containing the configuration
     */
    public void load(String pFilename) {
        mProps.clear();
//		try (InputStream in = new InflaterInputStream(new FileInputStream(pFilename))){
//		try (InputStream in = new GZIPInputStream(new FileInputStream(pFilename))){
        try (InputStream in = new FileInputStream(pFilename)) {
            mProps.load(in);
        } catch (Throwable t) {
            // TODO Auto-generated catch block
            t.printStackTrace();
        }
    }

    /**
     * Saves the configuration of this {@link ConfigurationFile} to the given filename
     *
     * @param pFilename The absolute filename to which to save the configuration
     */
    public void save(String pFilename) {
//		try (OutputStream out = new DeflaterOutputStream(new FileOutputStream(pFilename))) {
//		try (OutputStream out = new GZIPOutputStream(new FileOutputStream(pFilename))) {
        try (OutputStream out = (new FileOutputStream(pFilename))) {
            mProps.store(out, "Pesistent settings file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores the given object to the local properties object
     *
     * @param pObj The object to store
     */
    public void store(Object pObj) {
        store(pObj, pObj.getClass().getSimpleName());
    }

    /**
     * Stores the given object to the local properties object (with given preamble and postamble)
     *
     * @param pObj      The object to store
     * @param preamble  The preamble prepended to the key of the object members
     */
    public void store(Object pObj, String preamble) {
        String className = pObj.getClass().getSimpleName();
        // Retrieve the member variables
        Field[] fields = pObj.getClass().getDeclaredFields();
        // Iterate each variable
        for (Field field : fields) {
            if (!Modifier.isPublic(field.getModifiers())) {
                System.out.println("Unable to synchronise non-public field(" + field.getName() + ") in configuration structure(" + className + ")");
                continue;
            }

            String key = preamble + "." + field.getName();
            try {
                Object value = field.get(pObj);

                if (field.getType() == boolean.class) {
                    mProps.setProperty(key, Boolean.toString((boolean) value));
                } else if (field.getType() == int.class) {
                    mProps.setProperty(key, Integer.toString((int) value));
                } else if (field.getType() == double.class) {
                    mProps.setProperty(key, Double.toString((double) value));
                } else if (field.getType() == String.class) {
                    mProps.setProperty(key, (String) value);
                } else if (field.getType() == Color.class) {
                    Color color = (Color) value;
                    mProps.setProperty(key, String.format("[%d; %d; %d]", color.getRed(), color.getGreen(), color.getBlue()));
                } else if (value.getClass().isEnum()) {
                    mProps.setProperty(key, ((Enum<?>) value).name());
                } else if (value instanceof Vector) {
                    @SuppressWarnings("unchecked")
                    Vector<Object> v = (Vector<Object>) value;
                    for (int i = 0; i < v.size(); ++i) {
                        store(v.get(i), key + "[" + i + "]");
                    }
                } else if (field.getType() == Object.class) {
                    if (value instanceof Boolean) {
                        mProps.setProperty(key, Boolean.toString((boolean) value));
                    }
                    if (value instanceof Integer) {
                        mProps.setProperty(key, Integer.toString((int) value));
                    } else if (value instanceof Double) {
                        mProps.setProperty(key, Double.toString((double) value));
                    } else if (value instanceof Color) {
                        Color color = (Color) value;
                        mProps.setProperty(key, String.format("[%d; %d; %d]", color.getRed(), color.getGreen(), color.getBlue()));
                    } else if (value instanceof String) {
                        mProps.setProperty(key, '"' + (String) value + '"');
                    }
                } else {
                    store(value, preamble + "." + field.getName());
                }
            } catch (Throwable t) {
            }
        }
    }

    /**
     * Restores the object from the local properties object
     *
     * @param pObj The object to restore
     */
    public void restore(Object pObj) {
        restore(pObj, mProps);
    }

    /**
     * Restores the object from the given object object
     *
     * @param pObj   The object to restore
     * @param pProps The properties containing values for the members of obj
     */
    public void restore(Object pObj, Properties pProps) {
        String className = pObj.getClass().getSimpleName();
        restore(pObj, pProps, className + ".");
    }

    /**
     * Restores the object from the given settings object, using the given preamble
     *
     * @param pObj      The object to restore
     * @param pProps    The properties containing values for the members of obj
     * @param pPreamble The preamble to use
     */
    @SuppressWarnings("unchecked")
    public void restore(Object pObj, Properties pProps, String pPreamble) {
        // Retrieve the member variables
        Field[] fields = pObj.getClass().getDeclaredFields();
        // Iterate each variable
        for (Field field : fields) {
            try {
                restoreField(field, pObj, pProps, pPreamble);
            } catch (Throwable t) {
                t.printStackTrace();
                break;
            }
        }
    }

    private void restoreField(Field field, Object pObj, Properties pProps, String pPreamble) throws IllegalAccessException {
        if (field.getType().equals(Vector.class)) {
            restoreVectorField(field, pObj, pProps, pPreamble);
            return;
        }

        String key = pPreamble + field.getName();
        String value = pProps.getProperty(key);
        if (value == null) {
            Field[] subFields = field.getType().getDeclaredFields();
            if (subFields.length == 0) {
                System.out.println("Persistent settings does not contain value for " + key);
                return;
            }

            Object instance = field.get(pObj);

            for (Field subField : subFields) {
                restoreField(subField, instance, pProps, pPreamble + field.getName() + ".");
            }

            return;
        }

        try {
            if (field.getType() == boolean.class) {
                field.set(pObj, Boolean.parseBoolean(value));
            } else if (field.getType() == int.class) {
                field.set(pObj, Integer.parseInt(value));
            } else if (field.getType() == double.class) {
                field.set(pObj, Double.parseDouble(value));
            } else if (field.getType() == Color.class) {
                String[] channelValues = value.substring(1, value.length() - 1).split(";");
                field.set(pObj, new Color(Integer.parseInt(channelValues[0].trim()), Integer.parseInt(channelValues[1].trim()), Integer.parseInt(channelValues[2].trim())));
            } else if (field.getType() == String.class) {
                field.set(pObj, value);
            } else if (field.getType().isEnum()) {
                Method valMet = field.getType().getMethod("valueOf", String.class);
                Object enumVal = valMet.invoke(null, value);
                field.set(pObj, enumVal);
            } else if (field.getType() == Object.class) {
                // We can not infer from the type of the field, let's try the actual stored value
                if (value.isEmpty()) {
                    // We will never known ...
                } else if (value.startsWith("[") && value.endsWith("]")) {
                    String[] channelValues = value.substring(1, value.length() - 1).split(";");
                    field.set(pObj, new Color(Integer.parseInt(channelValues[0].trim()), Integer.parseInt(channelValues[1].trim()), Integer.parseInt(channelValues[2].trim())));
                } else if (value.startsWith("\"") && value.endsWith("\"")) {
                    field.set(pObj, value.substring(1, value.length() - 1));
                } else {
                    try {
                        int i = Integer.parseInt(value);
                        field.set(pObj, i);
                    } catch (Throwable t1) {
                        try {
                            double d = Double.parseDouble(value);
                            field.set(pObj, d);
                        } catch (Throwable t2) {
                            try {
                                boolean bool = Boolean.parseBoolean(value);
                                field.set(pObj, bool);
                            } catch (Throwable t3) {

                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            System.out.println("Failed to parse value(" + value + ") for " + key);
            t.printStackTrace();
        }
    }

    private void restoreVectorField(Field field, Object pObj, Properties pProps, String pPreamble) throws IllegalAccessException {
        // Obtain the Vector
        Vector<Object> vector = (Vector<Object>) field.get(pObj);

        // Clear existing elements from the vector
        vector.clear();

        // Iterate through the properties to find the indices of the vector
        int i = 0;
        while (true) {
            String curIndexKey = pPreamble + field.getName() + "[" + i + "]";
            Properties elemProps = new Properties();
            // Find all the elements for the current vector index
            for (Object keyObj : pProps.keySet()) {
                String keyStr = (String) keyObj;
                if (keyStr.startsWith(curIndexKey)) {
                    // Remove the name and dot
                    elemProps.put(keyStr.substring(curIndexKey.length() + 1), pProps.get(keyStr));
                }
            }

            if (elemProps.isEmpty()) {
                // Found no more elements for the vector
                return;
            }

            Object newElement = instanciateField(field);
            if (newElement == null) {
                return;
            }

            // Restore the instance members from the collected properties
            restore(newElement, elemProps, "");

            // Add the instance to the vector
            vector.addElement(newElement);

            ++i;
        }
    }

    private Object instanciateField(Field field) {
        // Construct new instance of vectors generic type
        Class<?> fieldType = field.getType();

        // Find the constructor with no arguments and create a new instance
        try {
            Object newElement = fieldType.getConstructor().newInstance();

            if (newElement == null)
                System.err.println("Failed to construct instance for " + fieldType.getName());

            return newElement;
        } catch (Throwable t) {
            System.err.println("Failed to find empty default constructor for " + fieldType.getName());
            return null;
        }
    }

    /**
     * Returns a String representation of this ConfigurationFile, which is the {@link #toString()}
     * of the underlying {@link Properties}
     *
     * @return The String representation of this ConfigurationFile
     */
    @Override
    public String toString() {
        return mProps.toString();
    }
}
