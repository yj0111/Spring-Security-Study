package com.study.springSecurity.global.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseDateEntity {

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDate createdDate;

	@LastModifiedDate
	@Column(nullable = false)
	private LocalDate modifiedDate;
}
