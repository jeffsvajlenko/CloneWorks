package cwformat.objects;

import java.nio.file.Path;

public class Clone {

	private long    id1;
	private Path   file1;
	private int    start1;
	private int    end1;
	
	private long    id2;
	private Path   file2;
	private int    start2;
	private int    end2;
	
	public Clone(long id1, Path file1, int start1, int end1, long id2, Path file2, int start2, int end2) {
		super();
		this.id1 = id1;
		this.file1 = file1;
		this.start1 = start1;
		this.end1 = end1;
		this.id2 = id2;
		this.file2 = file2;
		this.start2 = start2;
		this.end2 = end2;
	}

	public long getId1() {
		return id1;
	}

	public Path getFile1() {
		return file1;
	}

	public int getStart1() {
		return start1;
	}

	public int getEnd1() {
		return end1;
	}

	public long getId2() {
		return id2;
	}

	public Path getFile2() {
		return file2;
	}

	public int getStart2() {
		return start2;
	}

	public int getEnd2() {
		return end2;
	}

	@Override
	public String toString() {
		return "Clone [id1=" + id1 + ", file1=" + file1 + ", start1=" + start1 + ", end1=" + end1 + ", id2=" + id2
				+ ", file2=" + file2 + ", start2=" + start2 + ", end2=" + end2 + "]";
	}
	
}
