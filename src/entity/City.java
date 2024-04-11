package entity;

public class City {
	private String name;
	private String cityCountry;
	private String cityCode;
	
	public City(String cityCode, String name, String cityCountry) {
		super();
		this.name = name;
		this.cityCountry = cityCountry;
		this.cityCode = cityCode;
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCityCountry() {
		
		
		return cityCountry;
		
	}

	public void setCityCountry(String cityCountry) {
		this.cityCountry = cityCountry;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}


	@Override
	public String toString() {
		return name;
	}
	
	
	
	

}
