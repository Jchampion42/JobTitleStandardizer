package jobtitles;

import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Normalizes an input job title to one of the job titles in {@link JobTitle}
 */
public class Normalizer {

    /**
     * Attempts to match a title based on the words it contains
     * by searching for exact matches
     * @param jobToCheck The input string to check, already lowercase
     * @return null, if no matches are found, or a string containing the human-readable name of a job
     */
    private String matchByWord(String jobToCheck) {
        Set<String> checkWords = Arrays.stream(jobToCheck.toLowerCase()
                        .split("\\s+"))
                .collect(Collectors.toSet());

        Map<JobTitle, Integer> wordMatches = new HashMap<>();

        for (JobTitle jobTitle : JobTitle.values()) {
            Set<String> jobWords = Arrays.stream(jobTitle.getSimpleName().toLowerCase().split("\\s+"))
                    .collect(Collectors.toSet());

            // Count how many words overlap
            long matches = checkWords.stream().filter(jobWords::contains).count();
            if (matches > 0) {
                wordMatches.put(jobTitle, (int) matches);
            }
        }
        if (wordMatches.isEmpty()){
            return null;
        }
        if (wordMatches.size() == 1) {
           JobTitle matchedTitle = (JobTitle) wordMatches.keySet().toArray()[0];
           return matchedTitle.getDisplayName();
        }
        int highestMatches = wordMatches.values().stream().max(Integer::compare).orElse(0);
        List<JobTitle> closestMatches = wordMatches.keySet().stream().filter(key -> wordMatches.get(key) == highestMatches).toList();

        // Fallback: pick the shortest title
        JobTitle best = closestMatches.stream()
                .min(Comparator.comparingInt(jt -> jt.getSimpleName().length()))
                .orElse(null);

        return best != null ? best.getDisplayName() : null;
    }

    /**
     * Checks the Levenshtein distance of an input word to the job titles
     * This is done by checking breaking the input and the job into words
     * then calculating the number of letters that need to be changed to match a title
     * if any word has a strong match in a job title, it returns that title
     * @param inputTitle An input title to find a job for
     * @return The job which is closest to the string parsed
     */
    private String closeWordMatch(String inputTitle) {
        LevenshteinDistance distance = new LevenshteinDistance();
        String[] inputWords = inputTitle.toLowerCase().split("\\s+");

        for (JobTitle jobTitle : JobTitle.values()) {
            String[] jobWords = jobTitle.getSimpleName().toLowerCase().split("\\s+");

            // Check if any single word is very close
            for (String inputWord : inputWords) {
                for (String jobWord : jobWords) {
                    int dist = distance.apply(inputWord, jobWord);
                    int maxLen = Math.max(inputWord.length(), jobWord.length());
                    double similarity = 1.0 - ((double) dist / maxLen);
                    if (similarity >= 0.85) {  // very close match threshold
                        return jobTitle.getDisplayName(); // immediate accept
                    }
                }
            }
        }
        // no title was found
        return null;
    }

    /**
     * Checks the Levenshtein distance of a word to one of the job titles
     * This is done by checking breaking the input and the job into words
     * then calculating the number of letters that need to be changed to match a title
     * This is performed across the whole input and accepts a more loose answer
     * @param inputTitle An input title to find a job for
     * @return The job which is closest to the string parsed
     */
    private String fallbackWithLevenshtein(String inputTitle) {
        LevenshteinDistance distance = new LevenshteinDistance();
        String[] inputWords = inputTitle.toLowerCase().split("\\s+");

        JobTitle bestMatch = null;
        double bestScore = 0.0;

        for (JobTitle jobTitle : JobTitle.values()) {
            String[] jobWords = jobTitle.getSimpleName().toLowerCase().split("\\s+");
            double totalSimilarity = 0.0;

            for (String inputWord : inputWords) {
                double maxWordSimilarity = 0.0;
                for (String jobWord : jobWords) {
                    int dist = distance.apply(inputWord, jobWord);
                    int maxLen = Math.max(inputWord.length(), jobWord.length());
                    double similarity = 1.0 - ((double) dist / maxLen);
                    if (similarity > maxWordSimilarity) {
                        maxWordSimilarity = similarity;
                    }
                }
                totalSimilarity += maxWordSimilarity;
            }

            double avgSimilarity = totalSimilarity / inputWords.length;

            if (avgSimilarity > bestScore) {
                bestScore = avgSimilarity;
                bestMatch = jobTitle;
            }
        }

        return bestScore >= 0.6 ? bestMatch.getDisplayName() : null;
    }

    /**
     * Takes an input job title, and maps it to a {@link JobTitle}
     * @param inputString The string to interpret
     * @return A job title string in a human-readable format
     */
    public String normalize (String inputString) {
        inputString = inputString.toLowerCase(Locale.ROOT);
        String title = matchByWord(inputString);
        if (title == null) {
            title = closeWordMatch(inputString);
        }
        if (title == null) {
            title = fallbackWithLevenshtein(inputString);
        }
        if (title == null) {
            title = "Unknown";
        }
        return title;
    }
}
