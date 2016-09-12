package cwbuild.file;

import java.nio.file.Path;

public class InputFile {
	
	private Long id;
	
	private Path path;
	
	private int language;
	
	private byte[] content;
		
	public InputFile(Long id, Path path, int language, byte[] content) {
		super();
		this.id = id;
		this.path = path;
		this.language = language;
		this.content = content;
	}
	
	public byte[] getContent() {
		return this.content;
	}

	public int getLanguage() {
		return language;
	}
	
	public Long getId() {
		return id;
	}

	public Path getPath() {
		return path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		InputFile other = (InputFile) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "InputFile [id=" + id + ", path=" + path + "]";
	}
	
}
