package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Profile extends Model{
	
	public enum LEVEL {BASIC, ADMIN};

	public LEVEL name;
	public String description;
}
