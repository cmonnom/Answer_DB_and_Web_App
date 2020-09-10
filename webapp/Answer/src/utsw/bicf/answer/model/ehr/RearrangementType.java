//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.02.28 at 02:28:17 PM CST 
//


package utsw.bicf.answer.model.ehr;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RearrangementType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="RearrangementType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="fusion"/>
 *     &lt;enumeration value="rearrangement"/>
 *     &lt;enumeration value="truncation"/>
 *     &lt;enumeration value="deletion"/>
 *     &lt;enumeration value="duplication"/>
 *     &lt;enumeration value="unknown"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "RearrangementType")
@XmlEnum
public enum RearrangementType {

    @XmlEnumValue("fusion")
    FUSION("fusion"),
    @XmlEnumValue("rearrangement")
    REARRANGEMENT("rearrangement"),
    @XmlEnumValue("truncation")
    TRUNCATION("truncation"),
    @XmlEnumValue("deletion")
    DELETION("deletion"),
    @XmlEnumValue("duplication")
    DUPLICATION("duplication"),
    @XmlEnumValue("unknown")
    UNKNOWN("unknown");
    private final String value;

    RearrangementType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static RearrangementType fromValue(String v) {
        for (RearrangementType c: RearrangementType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}