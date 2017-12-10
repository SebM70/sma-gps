package sma;

import org.junit.Test;

public class ConvertTtqvToPolishRun {

	/**
	 * java sma.ConvertTtqvToPolish "TTQV_tracks_FranceSW.xml"
	 * "C:\\SMa\\QU4_SMa;C:\\SMa\\QU4_Afrique;C:\\SMa\\QU4_Afrique_Sud;C:\\SMa\\QU4_EST;C:\\SMa\\QU4_SMa\\other" "ttqv.mp"
	 * -levels0 "-mapFranceSortiesR"
	 * 
	 * @throws Throwable
	 */
	@Test
	public void runMain() throws Throwable {

		String[] args = { "TTQV_tracks_FranceSW.xml",
				"C:\\SMa\\QU4_SMa;C:\\SMa\\QU4_Afrique;C:\\SMa\\QU4_Afrique_Sud;C:\\SMa\\QU4_EST;C:\\SMa\\QU4_SMa\\other",
				"ttqv.mp", "-levels0", "-mapFranceSortiesR" };

		ConvertTtqvToPolish.main(args);

	}

}
