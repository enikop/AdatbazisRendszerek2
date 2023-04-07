package jdbc;

public interface Verifiable {
	public void checkValidity(String tableField, String data) throws Exception;
}
