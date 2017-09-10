
public class XMLClone {

	XMLCodeFragment f1;
	XMLCodeFragment f2;
	String text;
	
	public XMLClone(XMLCodeFragment f1, XMLCodeFragment f2, String text) {
		if(f1.compareTo(f1) < 0) {
			this.f1 = f1;
			this.f2 = f2;
		} else {
			this.f2 = f1;
			this.f1 = f2;
		}
		this.text = text;
	}
	
	public XMLCodeFragment getF1() {
		return f1;
	}
	
	public XMLCodeFragment getF2() {
		return f2;
	}
	
	public String getText() {
		return this.text;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((f1 == null) ? 0 : f1.hashCode());
		result = prime * result + ((f2 == null) ? 0 : f2.hashCode());
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
		XMLClone other = (XMLClone) obj;
		if (f1 == null) {
			if (other.f1 != null)
				return false;
		} else if (!f1.equals(other.f1))
			return false;
		if (f2 == null) {
			if (other.f2 != null)
				return false;
		} else if (!f2.equals(other.f2))
			return false;
		return true;
	}	
	
}
