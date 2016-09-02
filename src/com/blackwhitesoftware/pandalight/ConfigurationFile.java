package com.blackwhitesoftware.pandalight;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Properties;

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
                Class<?> fieldType = field.getType();

                if (fieldType == boolean.class) {
                    mProps.setProperty(key, Boolean.toString((boolean) value));
                } else if (fieldType == int.class) {
                    mProps.setProperty(key, Integer.toString((int) value));
                } else if (fieldType == double.class) {
                    mProps.setProperty(key, Double.toString((double) value));
                } else if (fieldType == String.class) {
                    mProps.setProperty(key, (String) value);
                } else if (fieldType == Color.class) {
                    Color color = (Color) value;
                    mProps.setProperty(key, String.format("[%d, %d, %d]",
                            color.getRed(),
                            color.getGreen(),
                            color.getBlue()));
                } else if (fieldType.isEnum()) {
                    mProps.setProperty(key, ((Enum<?>) value).name());
                } else if (fieldType == byte[].class) {
                    mProps.setProperty(key, Arrays.toString((byte[]) value));
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

    private boolean anySubproperties(Properties props, String preamble) {
        for (Object key : props.keySet()) {
            if (((String) key).startsWith(preamble))
                return true;
        }
        return false;
    }

    private void restoreField(Field field, Object pObj, Properties pProps, String pPreamble) throws IllegalAccessException {
        String key = pPreamble + field.getName();
        String value = pProps.getProperty(key);
        if (value == null) {
            if (!anySubproperties(pProps, pPreamble)) {
                System.out.println("Persistent settings does not contain value for " + key);
                return;
            }

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

        Class<?> fieldType = field.getType();

        try {
            if (fieldType == boolean.class) {
                field.set(pObj, Boolean.parseBoolean(value));
            } else if (fieldType == int.class) {
                field.set(pObj, Integer.parseInt(value));
            } else if (fieldType == double.class) {
                field.set(pObj, Double.parseDouble(value));
            } else if (fieldType == Color.class) {
                String[] channelValues = value.substring(1, value.length() - 1).split(", ");
                field.set(pObj, new Color(
                        Integer.parseInt(channelValues[0]),
                        Integer.parseInt(channelValues[1]),
                        Integer.parseInt(channelValues[2])));
            } else if (fieldType == String.class) {
                field.set(pObj, value);
            } else if (fieldType.isEnum()) {
                Method valMet = fieldType.getMethod("valueOf", String.class);
                Object enumVal = valMet.invoke(null, value);
                field.set(pObj, enumVal);
            } else if (fieldType == byte[].class) {
                String[] byteValues = value.substring(1, value.length() - 1).split(", ");
                byte[] bytes = new byte[byteValues.length];
                for (int i=0, len=bytes.length; i<len; i++) {
                    bytes[i] = Byte.parseByte(byteValues[i].trim());
                }
                field.set(pObj, bytes);
            } else {
                System.out.println("Failed to parse value(" + value + ") for " + key);
            }
        } catch (Throwable t) {
            System.out.println("Failed to parse value(" + value + ") for " + key);
            t.printStackTrace();
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
