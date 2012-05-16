package models;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
@DiscriminatorValue("PENDING") 
public class PendingUser extends User {}
