//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.02.28 at 02:28:17 PM CST 
//


package utsw.bicf.answer.model.ehr;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Rearrangement complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Rearrangement">
 *   &lt;complexContent>
 *     &lt;extension base="{http://foundationmedicine.com/compbio/variant-report-external}Variant">
 *       &lt;choice maxOccurs="unbounded" minOccurs="0">
 *         &lt;element name="dna-evidence" type="{http://foundationmedicine.com/compbio/variant-report-external}RearrangementDnaEvidence"/>
 *         &lt;element name="rna-evidence" type="{http://foundationmedicine.com/compbio/variant-report-external}RearrangementRnaEvidence"/>
 *       &lt;/choice>
 *       &lt;attribute name="type" use="required" type="{http://foundationmedicine.com/compbio/variant-report-external}RearrangementType" />
 *       &lt;attribute name="in-frame" type="{http://foundationmedicine.com/compbio/variant-report-external}InFrameType" />
 *       &lt;attribute name="targeted-gene" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="other-gene" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="pos1" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="pos2" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="supporting-read-pairs" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="description" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="percent-reads" type="{http://foundationmedicine.com/compbio/variant-report-external}Percent" />
 *       &lt;attribute name="allele-fraction" type="{http://foundationmedicine.com/compbio/variant-report-external}Fraction" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Rearrangement", propOrder = {
    "dnaEvidenceOrRnaEvidence"
})
public class Rearrangement
    extends Variant
{

    @XmlElements({
        @XmlElement(name = "dna-evidence", type = RearrangementDnaEvidence.class),
        @XmlElement(name = "rna-evidence", type = RearrangementRnaEvidence.class)
    })
    protected List<VariantEvidence> dnaEvidenceOrRnaEvidence;
    @XmlAttribute(name = "type", required = true)
    protected RearrangementType type;
    @XmlAttribute(name = "in-frame")
    protected InFrameType inFrame;
    @XmlAttribute(name = "targeted-gene", required = true)
    protected String targetedGene;
    @XmlAttribute(name = "other-gene")
    protected String otherGene;
    @XmlAttribute(name = "pos1", required = true)
    protected String pos1;
    @XmlAttribute(name = "pos2")
    protected String pos2;
    @XmlAttribute(name = "supporting-read-pairs", required = true)
    protected int supportingReadPairs;
    @XmlAttribute(name = "description", required = true)
    protected String description;
    @XmlAttribute(name = "percent-reads")
    protected Float percentReads;
    @XmlAttribute(name = "allele-fraction")
    protected Float alleleFraction;

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
     * {@link RearrangementDnaEvidence }
     * {@link RearrangementRnaEvidence }
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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link RearrangementType }
     *     
     */
    public RearrangementType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link RearrangementType }
     *     
     */
    public void setType(RearrangementType value) {
        this.type = value;
    }

    /**
     * Gets the value of the inFrame property.
     * 
     * @return
     *     possible object is
     *     {@link InFrameType }
     *     
     */
    public InFrameType getInFrame() {
        return inFrame;
    }

    /**
     * Sets the value of the inFrame property.
     * 
     * @param value
     *     allowed object is
     *     {@link InFrameType }
     *     
     */
    public void setInFrame(InFrameType value) {
        this.inFrame = value;
    }

    /**
     * Gets the value of the targetedGene property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTargetedGene() {
        return targetedGene;
    }

    /**
     * Sets the value of the targetedGene property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTargetedGene(String value) {
        this.targetedGene = value;
    }

    /**
     * Gets the value of the otherGene property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOtherGene() {
        return otherGene;
    }

    /**
     * Sets the value of the otherGene property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOtherGene(String value) {
        this.otherGene = value;
    }

    /**
     * Gets the value of the pos1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPos1() {
        return pos1;
    }

    /**
     * Sets the value of the pos1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPos1(String value) {
        this.pos1 = value;
    }

    /**
     * Gets the value of the pos2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPos2() {
        return pos2;
    }

    /**
     * Sets the value of the pos2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPos2(String value) {
        this.pos2 = value;
    }

    /**
     * Gets the value of the supportingReadPairs property.
     * 
     */
    public int getSupportingReadPairs() {
        return supportingReadPairs;
    }

    /**
     * Sets the value of the supportingReadPairs property.
     * 
     */
    public void setSupportingReadPairs(int value) {
        this.supportingReadPairs = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the percentReads property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getPercentReads() {
        return percentReads;
    }

    /**
     * Sets the value of the percentReads property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setPercentReads(Float value) {
        this.percentReads = value;
    }

    /**
     * Gets the value of the alleleFraction property.
     * 
     * @return
     *     possible object is
     *     {@link Float }
     *     
     */
    public Float getAlleleFraction() {
        return alleleFraction;
    }

    /**
     * Sets the value of the alleleFraction property.
     * 
     * @param value
     *     allowed object is
     *     {@link Float }
     *     
     */
    public void setAlleleFraction(Float value) {
        this.alleleFraction = value;
    }

}
