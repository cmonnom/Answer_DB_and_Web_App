//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.02.28 at 02:28:17 PM CST 
//


package utsw.bicf.answer.model.ehr;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NucleicAcidTypeEnum.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="NucleicAcidTypeEnum">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="DNA"/>
 *     &lt;enumeration value="RNA"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "NucleicAcidTypeEnum")
@XmlEnum
public enum NucleicAcidTypeEnum {

    DNA,
    RNA;

    public String value() {
        return name();
    }

    public static NucleicAcidTypeEnum fromValue(String v) {
        return valueOf(v);
    }

}
