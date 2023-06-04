package org.ksl.supplychain.geography.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Geographical coordinate of an object
 * @author Miller
 *
 */
@Embeddable
@Setter @NoArgsConstructor @AllArgsConstructor
public class Coordinate {
	private double x;
	
	private double y;
	
	@Column(name = "X")	
	public double getX() {
		return x;
	}

	@Column(name = "Y")
	public double getY() {
		return y;
	}
}
