package com.jjinterna.pbxevents.asterisk.queue;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;

public class QueueLog implements Serializable {

    protected int timeId;
    protected String callId;
    protected String queue;
    protected String agent;
    protected QueueLogType verb;
    protected String data1;
    protected String data2;
    protected String data3;
    protected String data4;
    protected String data5;

    /**
     * Gets the value of the timeId property.
     * 
     */
    public int getTimeId() {
        return timeId;
    }

    /**
     * Sets the value of the timeId property.
     * 
     */
    public void setTimeId(int value) {
        this.timeId = value;
    }

    /**
     * Gets the value of the callId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCallId() {
        return callId;
    }

    /**
     * Sets the value of the callId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCallId(String value) {
        this.callId = value;
    }

    /**
     * Gets the value of the queue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getQueue() {
        return queue;
    }

    /**
     * Sets the value of the queue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setQueue(String value) {
        this.queue = value;
    }

    /**
     * Gets the value of the agent property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgent() {
        return agent;
    }

    /**
     * Sets the value of the agent property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgent(String value) {
        this.agent = value;
    }

    /**
     * Gets the value of the verb property.
     * 
     * @return
     *     possible object is
     *     {@link QueueLogType }
     *     
     */
    public QueueLogType getVerb() {
        return verb;
    }

    /**
     * Sets the value of the verb property.
     * 
     * @param value
     *     allowed object is
     *     {@link QueueLogType }
     *     
     */
    public void setVerb(QueueLogType value) {
        this.verb = value;
    }

    /**
     * Gets the value of the data1 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getData1() {
        return data1;
    }

    /**
     * Sets the value of the data1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setData1(String value) {
        this.data1 = value;
    }

    /**
     * Gets the value of the data2 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getData2() {
        return data2;
    }

    /**
     * Sets the value of the data2 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setData2(String value) {
        this.data2 = value;
    }

    /**
     * Gets the value of the data3 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getData3() {
        return data3;
    }

    /**
     * Sets the value of the data3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setData3(String value) {
        this.data3 = value;
    }

    /**
     * Gets the value of the data4 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getData4() {
        return data4;
    }

    /**
     * Sets the value of the data4 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setData4(String value) {
        this.data4 = value;
    }

    /**
     * Gets the value of the data5 property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getData5() {
        return data5;
    }

    /**
     * Sets the value of the data5 property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setData5(String value) {
        this.data5 = value;
    }

}
