
public class XMLCodeFragment implements Comparable<XMLCodeFragment>  {

	//<source file="example/JHotDraw54b1/src/CH/ifa/draw/standard/DeleteCommand.java" startline="63" endline="72">
	
	String file;
	int startline;
	int endline;
	
	public XMLCodeFragment(String file, int startline, int endline) {
		super();
		this.file = file;
		this.startline = startline;
		this.endline = endline;
	}

	public String getFile() {
		return file;
	}

	public int getStartline() {
		return startline;
	}

	public int getEndline() {
		return endline;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + endline;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		result = prime * result + startline;
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
		XMLCodeFragment other = (XMLCodeFragment) obj;
		if (endline != other.endline)
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		if (startline != other.startline)
			return false;
		return true;
	}

	@Override
	public int compareTo(XMLCodeFragment o) {
		if(this.file.compareTo((o.file)) != 0) {
			return this.file.compareTo((o.file));
		} else {
			if(this.startline < o.startline) {
				return -1;
			} else if (this.startline > o.startline) {
				return 1;
			} else {
				if(this.endline < o.endline) {
					return -1;
				} else if (this.endline > o.endline) {
					return 1;
				} else {
					return 0;
				}
			}
		}
	}


	
}
