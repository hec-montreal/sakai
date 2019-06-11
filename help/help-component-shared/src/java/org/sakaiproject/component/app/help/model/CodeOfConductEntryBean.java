package org.sakaiproject.component.app.help.model;

import java.util.Date;

public class CodeOfConductEntryBean implements Comparable<CodeOfConductEntryBean> {

	private String matricule;
	private Date date;
	
	@Override
	public int hashCode() {
		return matricule.hashCode();
	}
	
	@Override
	public boolean equals(Object o)	{
		if (this == o) {
			return true;
		}
		
		if (!(o instanceof CodeOfConductEntryBean)) {
			return false;
		}
		
		return this.matricule.equals(((CodeOfConductEntryBean) o).matricule);
	}
	
	@Override
	public int compareTo(CodeOfConductEntryBean o) {
		return matricule.compareTo(o.matricule);
	}

	public String getMatricule() {
		return matricule;
	}

	public void setMatricule(String matricule) {
		this.matricule = matricule;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
