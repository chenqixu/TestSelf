package com.cqx.common.utils.net.asnone;

/**
 * UniversalTagNumber
 *
 * @author chenqixu
 */
public enum  EnumUniversalTagNumber {
    Reserved_For_BER(0),
    BOOLEAN(1),
    INTEGER(2),
    BITSTRING(3),
    OCTET_STRING(4),
    NULL(5),
    OBJECT_IDENTIFIER(6),
    ObjectDescriptor(7),
    INSTANCE_OF_EXTERNAL(8),
    REAL(9),
    ENUMERATED(10),
    EMBEDDED_PDV(11),
    UTF8String(12),
    RELATIVE_OID(13),
    SEQUENCE_SEQUENCE_OF(16),
    SET_SET_OF(17),
    NumericString(18),
    PrintableString(19),
    TeletexString_T61String(20),
    VideotexString(21),
    IA5String(22),
    UTCTime(23),
    GeneralizedTime(24),
    GraphicString(25),
    VisibleString_ISO646String(26),
    GeneralString(27),
    UniversalString(28),
    CHARACTER_STRING(29),
    BMPString(30),
    ;

    EnumUniversalTagNumber(int tagNumber) {
        this.tagNumber = tagNumber;
    }

    private int tagNumber;

    public int getTagNumber() {
        return tagNumber;
    }

    public static EnumUniversalTagNumber valueOfByValue(int tagNumber) {
        switch (tagNumber) {
            case 0:
                return Reserved_For_BER;
            case 1:
                return BOOLEAN;
            case 2:
                return INTEGER;
            case 3:
                return BITSTRING;
            case 4:
                return OCTET_STRING;
            case 5:
                return NULL;
            case 6:
                return OBJECT_IDENTIFIER;
            case 7:
                return ObjectDescriptor;
            case 8:
                return INSTANCE_OF_EXTERNAL;
            case 9:
                return REAL;
            case 10:
                return ENUMERATED;
            case 11:
                return EMBEDDED_PDV;
            case 12:
                return UTF8String;
            case 13:
                return RELATIVE_OID;
            case 16:
                return SEQUENCE_SEQUENCE_OF;
            case 17:
                return SET_SET_OF;
            case 18:
                return NumericString;
            case 19:
                return PrintableString;
            case 20:
                return TeletexString_T61String;
            case 21:
                return VideotexString;
            case 22:
                return IA5String;
            case 23:
                return UTCTime;
            case 24:
                return GeneralizedTime;
            case 25:
                return GraphicString;
            case 26:
                return VisibleString_ISO646String;
            case 27:
                return GeneralString;
            case 28:
                return UniversalString;
            case 29:
                return CHARACTER_STRING;
            case 30:
                return BMPString;
            default:
                return null;
        }
    }
}
