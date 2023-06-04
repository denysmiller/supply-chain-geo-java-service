package org.ksl.supplychain.geography.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * Base class for all business entities
 * 
 * @author Miller
 *
 */
@MappedSuperclass
@Setter @EqualsAndHashCode(of="id")
public abstract class AbstractEntity {
	public static final String FIELD_CREATED_AT = "createdAt";
	
	public static final String FIELD_ID = "id";
	/**
	 * Unique entity identifier
	 */
	private int id;

	/**
	 * Timestamp of entity creation
	 */
	private LocalDateTime createdAt;

	/**
	 * Timestamp of entity last modification
	 */
	private LocalDateTime modifiedAt;

	/**
	 * Person who created specific entity
	 */
	private String createdBy;

	/**
	 * Last person who modified entity
	 */
	private String modifiedBy;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	public int getId() {
		return id;
	}

	@Column(name = "CREATED_AT", nullable = false, updatable = false)
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	@Column(name = "MODIFIED_AT", insertable = false)
	public LocalDateTime getModifiedAt() {
		return modifiedAt;
	}

	@Column(name = "CREATED_BY", updatable = false)
	public String getCreatedBy() {
		return createdBy;
	}

	@Column(name = "MODIFIED_BY", updatable = false)
	public String getModifiedBy() {
		return modifiedBy;
	}

	@PrePersist
	public void prePersist() {
		if (getId() == 0) {
			setCreatedAt(LocalDateTime.now());
		}		
	}
}
