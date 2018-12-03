package implementations;

// reference https://www.baeldung.com/java-levenshtein-distance
public class EditDistanceAlgo {
	
	public static int findEditDistance(String String1, String String2) {
		Integer str1Length = String1.length();
		Integer str2Length = String2.length();
		Integer editDistanceMatrix[][] = new Integer[str1Length + 1][str2Length + 1];
		
		for (int i = 0; i <= Math.max(str1Length, str2Length); i++) {
			if(!(i > str1Length))
				editDistanceMatrix[i][0] = i;
			if(!(i > str2Length))
				editDistanceMatrix[0][i] = i;
		}
		
		for (int i = 1; i < str1Length; i++) {
			for (int j = 1; j < str2Length; j++) {
				if (String1.charAt(i) == String2.charAt(j)) {
					editDistanceMatrix[i][j] = editDistanceMatrix[i - 1][j - 1];
				} else {
					editDistanceMatrix[i][j] = Math.min(Math.min( (editDistanceMatrix[i][j - 1]) + 1, (editDistanceMatrix[i - 1][j]) + 1),(editDistanceMatrix[i-1][j-1]) + 1);
				}
			}
		}
		int editDistance = editDistanceMatrix[str1Length - 1][str2Length - 1];
		return editDistance;
	}
}
