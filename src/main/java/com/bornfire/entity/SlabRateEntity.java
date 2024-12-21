package com.bornfire.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SLABRATES")
public class SlabRateEntity {
    @Id
    @Column(name = "STARTRATE")
    private String startRate;

    @Column(name = "ENDRATE")
    private String endRate;

    @Column(name = "CHARGES")
    private String charges;

	public String getStartRate() {
		return startRate;
	}

	public void setStartRate(String startRate) {
		this.startRate = startRate;
	}

	public String getEndRate() {
		return endRate;
	}

	public void setEndRate(String endRate) {
		this.endRate = endRate;
	}

	public String getCharges() {
		return charges;
	}

	public void setCharges(String charges) {
		this.charges = charges;
	}

	@Override
	public String toString() {
		return "SlabRateEntity [startRate=" + startRate + ", endRate=" + endRate + ", charges=" + charges + "]";
	}

	public SlabRateEntity(String startRate, String endRate, String charges) {
		super();
		this.startRate = startRate;
		this.endRate = endRate;
		this.charges = charges;
	}

	public SlabRateEntity() {
		super();
		// TODO Auto-generated constructor stub
	}


}
