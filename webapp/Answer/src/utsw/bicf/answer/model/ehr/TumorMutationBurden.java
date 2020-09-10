//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.02.28 at 02:28:17 PM CST 
//


package utsw.bicf.answer.model.ehr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for TumorMutationBurden complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TumorMutationBurden">
 *   &lt;complexContent>
 *     &lt;extension base="{http://foundationmedicine.com/compbio/variant-report-external}Biomarker">
 *       &lt;attribute name="status" use="required" type="{http://foundationmedicine.com/compbio/variant-report-external}TumorMutationBurdenStatus" />
 *       &lt;attribute name="score" use="required" type="{http://www.w3.org/2001/XMLSchema}float" />
 *       &lt;attribute name="unit" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TumorMutationBurden")
public class TumorMutationBurden
    extends Biomarker
{

    @XmlAttribute(name = "status", required = true)
    protected TumorMutationBurdenStatus status;
    @XmlAttribute(name = "score", required = true)
    protected float score;
    @XmlAttribute(name = "unit", required = true)
    protected String unit;

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link TumorMutationBurdenStatus }
     *     
     */
    public TumorMutationBurdenStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link TumorMutationBurdenStatus }
     *     
     */
    public void setStatus(TumorMutationBurdenStatus value) {
        this.status = value;
    }

    /**
     * Gets the value of the score property.
     * 
     */
    public float getScore() {
        return score;
    }

    /**
     * Sets the value of the score property.
     * 
     */
    public void setScore(float value) {
        this.score = value;
    }

    /**
     * Gets the value of the unit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Sets the value of the unit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUnit(String value) {
        this.unit = value;
    }

}