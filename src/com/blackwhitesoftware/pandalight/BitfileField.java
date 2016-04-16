package com.blackwhitesoftware.pandalight;

/**
 * Created by hudini on 15.04.2016.
 */
public enum BitfileField {
    DESIGN_NAME('a'),
    PART_NAME('b'),
    CREATION_DATE('c'),
    CREATION_TIME('d'),
    DATA('e');

    private char identifier;

    BitfileField(char identifier) {
        this.identifier = identifier;
    }

    public char getIdentifier() {
        return identifier;
    }

    public static BitfileField fromIdentifier(char identifier) {
        switch (identifier) {
            case 'a': return BitfileField.DESIGN_NAME;
            case 'b': return BitfileField.PART_NAME;
            case 'c': return BitfileField.CREATION_DATE;
            case 'd': return BitfileField.CREATION_TIME;
            case 'e': return BitfileField.DATA;
            default: throw new IllegalArgumentException("identifier");
        }
    }
}
