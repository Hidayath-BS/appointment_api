package org.zerhusen.payload;

public class AppointmentMail {
private String subject;
private String text;
public String getSubject() {
	return subject;
}
public void setSubject(String subject) {
	this.subject = subject;
}
public String getText() {
	return text;
}
public void setText(String text) {
	this.text = text;
}
@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((subject == null) ? 0 : subject.hashCode());
	result = prime * result + ((text == null) ? 0 : text.hashCode());
	return result;
}
@Override
public boolean equals(Object obj) {
	if (this == obj)
		return true;
	if (obj == null)
		return false;
	if (getClass() != obj.getClass())
		return false;
	AppointmentMail other = (AppointmentMail) obj;
	if (subject == null) {
		if (other.subject != null)
			return false;
	} else if (!subject.equals(other.subject))
		return false;
	if (text == null) {
		if (other.text != null)
			return false;
	} else if (!text.equals(other.text))
		return false;
	return true;
}
@Override
public String toString() {
	return "AppointmentMail [subject=" + subject + ", text=" + text + "]";
}
public AppointmentMail(String subject, String text) {
	super();
	this.subject = subject;
	this.text = text;
}
public AppointmentMail() {}
}
