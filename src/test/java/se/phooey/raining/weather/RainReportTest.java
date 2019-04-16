package se.phooey.raining.weather;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Since the RainReport class overrides the equals, hashCode and toString
 * methods, it contains some simple logic that we test with some simple unit
 * tests here.
 *
 */
public class RainReportTest {

	@Test
	public void equalsReturnTrueForComparingARainReportWithItself() {
		RainReport r = new RainReport();
		assertThat(r.equals(r)).isTrue();
	}

	@Test
	public void equalsReturnsTrueForTwoIdenticalRainReports() {
		RainReport a = new RainReport();
		RainReport b = new RainReport();
		assertThat(a.equals(b)).isTrue();
	}

	@Test
	public void equalsReturnsFalseForTwoDifferentRainReports() {
		RainReport a = new RainReport();
		RainReport b = new RainReport();
		b.setLatitude(13.37);
		assertThat(a.equals(b)).isFalse();
	}

	@Test
	public void equalsReturnsFalseWhenComparingARainReportToNull() {
		RainReport r = new RainReport();
		assertThat(r.equals(null)).isFalse();
	}

	@SuppressWarnings("unlikely-arg-type")
	@Test
	public void equalsReturnsFalseWhenComparingToAnObjectOfADifferentClass() {
		RainReport r = new RainReport();
		assertThat(r.equals(new String("test"))).isFalse();
	}

	@Test
	public void hashCodeIsTheSameForTwoIdenticalRainReports() {
		RainReport a = new RainReport();
		RainReport b = new RainReport();
		assertThat(a.hashCode()).isEqualTo(b.hashCode());
	}

	@Test
	public void hashCodeIsDifferentForTwoNonIdenticalRainReports() {
		RainReport a = new RainReport();
		a.setLatitude(13.37);
		RainReport b = new RainReport();
		assertThat(a.hashCode()).isNotEqualTo(b.hashCode());
	}
	
	@Test
	public void toStringShouldReturnAStringRepresentationOfTheRainReport() {
		RainReport r = new RainReport();
		r.setLatitude(13.37);
		assertThat(r.toString()).contains("13.37");
	}
}
