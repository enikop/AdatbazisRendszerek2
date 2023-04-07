package schemacheck;

import java.util.Objects;

public class FieldSchema {
	private String name;
	private String type;
	
	@Override
	public int hashCode() {
		return Objects.hash(name, type);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FieldSchema other = (FieldSchema) obj;
		return Objects.equals(name, other.name) && Objects.equals(type, other.type);
	}
	public FieldSchema(String name, String type) {
		super();
		this.name = name;
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
