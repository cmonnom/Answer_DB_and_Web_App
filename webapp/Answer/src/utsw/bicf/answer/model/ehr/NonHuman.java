//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.02.28 at 02:28:17 PM CST 
//


package utsw.bicf.answer.model.ehr;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for NonHuman complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="NonHuman">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="dna-evidence" type="{http://foundationmedicine.com/compbio/variant-report-external}NonHumanDnaEvidence"/>
 *         &lt;element name="rna-evidence" type="{http://foundationmedicine.com/compbio/variant-report-external}NonHumanRnaEvidence"/>
 *       &lt;/choice>
 *       &lt;attribute name="organism" use="required" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" />
 *       &lt;attribute name="reads-per-million" use="required" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="status" use="required" type="{http://foundationmedicine.com/compbio/variant-report-external}NonHumanStatusEnum" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "NonHuman", propOrder = {
    "dnaEvidenceOrRnaEvidence"
})
public class NonHuman {

    @XmlElements({
        @XmlElement(name = "dna-evidence", type = NonHumanDnaEvidence.class),
        @XmlElement(name = "rna-evidence", type = NonHumanRnaEvidence.class)
    })
    protected List<VariantEvidence> dnaEvidenceOrRnaEvidence;
    @XmlAttribute(name = "organism", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String organism;
    @XmlAttribute(name = "reads-per-million", required = true)
    protected BigInteger readsPerMillion;
    @XmlAttribute(name = "status", required = true)
    protected NonHumanStatusEnum status;

    /**
     * Gets the value of the dnaEvidenceOrRnaEvidence property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dnaEvidenceOrRnaEvidence property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDnaEvidenceOrRnaEvidence().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NonHumanDnaEvidence }
     * {@link NonHumanRnaEvidence }
     * 
     * 
     */
    public List<VariantEvidence> getDnaEvidenceOrRnaEvidence() {
        if (dnaEvidenceOrRnaEvidence == null) {
            dnaEvidenceOrRnaEvidence = new ArrayList<VariantEvidence>();
        }
        return this.dnaEvidenceOrRnaEvidence;
    }

    /**
     * Gets the value of the organism property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrganism() {
        return organism;
    }

    /**
     * Sets the value of the organism property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrganism(String value) {
        this.organism = value;
    }

    /**
     * Gets the value of the readsPerMillion property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getReadsPerMillion() {
        return readsPerMillion;
    }

    /**
     * Sets the value of the readsPerMillion property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setReadsPerMillion(BigInteger value) {
        this.readsPerMillion = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link NonHumanStatusEnum }
     *     
     */
    public NonHumanStatusEnum getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link NonHumanStatusEnum }
     *     
     */
    public void setStatus(NonHumanStatusEnum value) {
        this.status = value;
    }

}
